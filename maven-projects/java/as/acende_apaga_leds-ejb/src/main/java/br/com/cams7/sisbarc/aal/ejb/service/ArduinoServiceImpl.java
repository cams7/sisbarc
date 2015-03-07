package br.com.cams7.sisbarc.aal.ejb.service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.cams7.as.service.BaseServiceImpl;
import br.com.cams7.sisbarc.aal.jmx.service.AppArduinoServiceMBean;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EstadoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EventoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.IntervaloLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity_;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.arduino.vo.EEPROMData;
import br.com.cams7.util.AppException;
import br.com.cams7.util.AppUtil;

@Stateless
@Local(ArduinoService.class)
@Remote(AppWildflyService.class)
public class ArduinoServiceImpl extends BaseServiceImpl<LEDEntity, PinPK>
		implements ArduinoService, AppWildflyService {

	@PersistenceContext(unitName = "acendeApagaLEDsUnit")
	private EntityManager entityManager;

	private JMXConnector jmxConnector;
	private AppArduinoServiceMBean mbeanProxy;

	public ArduinoServiceImpl() {
		super();
	}

	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}

	@PostConstruct
	private void jmxConnectorInit() {
		try {
			Properties config = AppUtil.getPropertiesFile(
					ArduinoServiceImpl.class, "config.properties");

			String jmxURL = "service:jmx:rmi:///jndi/rmi://"
					+ config.getProperty("JMX_HOST").trim() + ":"
					+ config.getProperty("JMX_PORT").trim() + "/jmxrmi";

			getLog().info("JMX URL: " + jmxURL);

			JMXServiceURL url = new JMXServiceURL(jmxURL);

			jmxConnector = JMXConnectorFactory.connect(url);
			MBeanServerConnection mbeanServerConnection = jmxConnector
					.getMBeanServerConnection();
			// ObjectName should be same as your MBean name
			ObjectName mbeanName = new ObjectName(
					"br.com.cams7.sisbarc.aal.jmx.service:type=AppArduinoService");

			// Get MBean proxy instance that will be used to make calls to
			// registered MBean
			mbeanProxy = (AppArduinoServiceMBean) MBeanServerInvocationHandler
					.newProxyInstance(mbeanServerConnection, mbeanName,
							AppArduinoServiceMBean.class, true);
		} catch (IOException | MalformedObjectNameException | AppException e) {
			getLog().log(Level.SEVERE, e.getMessage());
		}

		getLog().info("JMX Connector Open");
	}

	@PreDestroy
	private void jmxConnectorClose() {
		if (jmxConnector != null)
			try {
				jmxConnector.close();
			} catch (IOException e) {
				getLog().log(Level.WARNING, e.getMessage());
			}

		getLog().info("JMX Connector Close");
	}

	@Asynchronous
	public Future<LEDEntity> alteraLEDEstado(LEDEntity led) {
		if (mbeanProxy == null)
			return null;

		if (led.getEstado() == EstadoLED.ACESO && !led.isAtivo()) {
			led.setEstado(EstadoLED.APAGADO);
		} else {
			mbeanProxy.alteraEstadoLED(led.getId(), led.getEstado());

			serialThreadTime();

			EstadoLED estado = mbeanProxy.getEstadoLED(led.getId());
			led.setEstado(estado);

			if (estado != null) {
				getLog().info(
						"LED '" + led.getId() + "' esta '" + led.getEstado()
								+ "'");
			} else
				getLog().log(
						Level.WARNING,
						"Ocorreu um erro ao tenta buscar o ESTADO do LED '"
								+ led.getId() + "'");
		}

		return new AsyncResult<LEDEntity>(led);
	}

	@Asynchronous
	public Future<Boolean> atualizaLED(LEDEntity led) {
		if (mbeanProxy == null)
			return null;

		mbeanProxy.alteraEventoLED(led.getId(), led.getEvento(),
				led.getIntervalo());

		serialThreadTime();

		EventoLED evento = mbeanProxy.getEventoLED(led.getId());

		Boolean arduinoRun = Boolean.FALSE;

		if (evento != null) {
			led.setEvento(evento);
			save(led);
			arduinoRun = Boolean.TRUE;

			getLog().info(
					"O evento do LED '" + led.getId() + "' foi alterado '"
							+ led.getEvento() + "'");
		} else
			getLog().log(
					Level.WARNING,
					"Ocorreu um erro ao tenta buscar o EVENTO do LED '"
							+ led.getId() + "'");

		return new AsyncResult<Boolean>(arduinoRun);
	}

	private void serialThreadTime() {
		try {
			Thread.sleep(mbeanProxy.getSerialThreadTime());
		} catch (InterruptedException e) {
			getLog().log(Level.WARNING, e.getMessage());
		}
	}

	@Override
	public EstadoLED getEstadoLEDAtivadoPorBotao(LEDEntity.CorLED ledCor) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Boolean> criteria = builder.createQuery(Boolean.class);

		Root<LEDEntity> root = criteria.from(getEntityType());

		criteria.select(root.get(LEDEntity_.ativo));

		Predicate isActiveButton = builder.isTrue(root
				.get(LEDEntity_.ativadoPorBotao));
		Predicate equalColor = builder.equal(root.get(LEDEntity_.cor), ledCor);

		Predicate and = builder.and(isActiveButton, equalColor);

		criteria.where(and);

		TypedQuery<Boolean> query = getEntityManager().createQuery(criteria);
		Boolean active = query.getSingleResult();
		if (active == Boolean.TRUE)
			return EstadoLED.ACESO;

		return EstadoLED.APAGADO;
	}

	@Asynchronous
	public Future<Boolean> alteraLEDEventos(List<LEDEntity> leds) {
		if (mbeanProxy == null)
			return null;

		for (LEDEntity led : leds)
			mbeanProxy.alteraEventoLED(led.getId(), led.getEvento(),
					led.getIntervalo());

		serialThreadTime();

		Boolean arduinoRun = Boolean.TRUE;

		for (LEDEntity led : leds) {
			EventoLED evento = mbeanProxy.getEventoLED(led.getId());

			if (evento == null) {
				arduinoRun = Boolean.FALSE;
				break;
			}
		}

		if (arduinoRun)
			getLog().info("Os EVENTOs dos LEDs foram alterados");
		else
			getLog().log(Level.WARNING,
					"Ocorreu um erro ao tenta buscar os EVENTOs dos LEDs");

		return new AsyncResult<Boolean>(arduinoRun);
	}

	@Asynchronous
	public Future<Boolean> sincronizaLEDEventos(List<LEDEntity> leds) {
		if (mbeanProxy == null)
			return null;

		for (LEDEntity led : leds)
			mbeanProxy.buscaDadosLED(led.getId());

		serialThreadTime();

		Boolean arduinoRun = Boolean.TRUE;

		for (LEDEntity led : leds) {
			EEPROMData data = mbeanProxy.getDadosLED(led.getId());
			if (data == null) {
				arduinoRun = Boolean.FALSE;
				break;
			}

			EventoLED evento = EventoLED.values()[data.getActionEvent()];
			IntervaloLED intervalo = IntervaloLED.values()[data
					.getThreadInterval()];

			led.setEvento(evento);
			led.setIntervalo(intervalo);
		}

		if (arduinoRun) {
			update(leds);
			getLog().info("Os EVENTOs dos LEDs foram sincronizados");
		} else
			getLog().log(Level.WARNING,
					"Ocorreu um erro ao tenta buscar os DADOs dos LEDs");

		return new AsyncResult<Boolean>(arduinoRun);
	}

}
