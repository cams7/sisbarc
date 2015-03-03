package br.com.cams7.sisbarc.aal.ejb.service;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
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
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity_;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.util.AppException;
import br.com.cams7.util.AppUtil;

@Stateless
@Local(ArduinoService.class)
@Remote(AppWildflyService.class)
public class ArduinoServiceImpl extends BaseServiceImpl<LEDEntity, PinPK>
		implements ArduinoService, AppWildflyService {

	@Inject
	private Logger log;

	private JMXConnector jmxConnector;
	private AppArduinoServiceMBean mbeanProxy;

	public ArduinoServiceImpl() {
		super();
	}

	@PostConstruct
	private void jmxConnectorInit() {
		try {
			Properties config = AppUtil.getPropertiesFile(
					ArduinoServiceImpl.class, "config.properties");

			String jmxURL = "service:jmx:rmi:///jndi/rmi://"
					+ config.getProperty("JMX_HOST").trim() + ":"
					+ config.getProperty("JMX_PORT").trim() + "/jmxrmi";

			log.info("JMX URL: " + jmxURL);

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
			log.log(Level.SEVERE, e.getMessage());
		}

		log.info("JMX Connector Open");
	}

	@PreDestroy
	private void jmxConnectorClose() {
		if (jmxConnector != null)
			try {
				jmxConnector.close();
			} catch (IOException e) {
				log.log(Level.WARNING, e.getMessage());
			}

		log.info("JMX Connector Close");
	}

	@Asynchronous
	public Future<LEDEntity> alteraEstadoLED(LEDEntity led) {

		if (mbeanProxy == null)
			return null;

		mbeanProxy.alteraEstadoLED(led.getId(), led.getEstado());

		log.info("LED " + led.getCor() + " -> changeStatusLED(pin = '"
				+ led.getId().getPinType() + " " + led.getId().getPin()
				+ "', status = '" + led.getEstado() + "') - Before sleep: "
				+ DF.format(new Date()));

		serialThreadTime();

		EstadoLED estado = mbeanProxy.getEstadoLED(led.getId());
		led.setEstado(estado);

		log.info("LED " + led.getCor() + " -> changeStatusLED(pin = '"
				+ led.getId().getPinType() + " " + led.getId().getPin()
				+ "', status = '" + led.getEstado() + "') - After sleep: "
				+ DF.format(new Date()));

		if (estado != null) {
			log.info("LED '" + led.getCor() + "' esta '" + led.getEstado()
					+ "'");
		} else
			log.log(Level.WARNING,
					"Ocorreu um erro ao tenta buscar o status do LED '"
							+ led.getCor() + "'");

		return new AsyncResult<LEDEntity>(led);

	}

	@Override
	public Future<LEDEntity> alteraEventoLED(LEDEntity led) {
		if (mbeanProxy == null)
			return null;

		mbeanProxy.alteraEventoLED(led.getId(), led.getEvento(),
				led.getIntervalo());

		log.info("LED " + led.getCor() + " -> changeEventLED(pin = '"
				+ led.getId().getPinType() + " " + led.getId().getPin()
				+ "', event = '" + led.getEvento() + "', time = '"
				+ led.getIntervalo() + "') - Before sleep: "
				+ DF.format(new Date()));

		serialThreadTime();

		EventoLED evento = mbeanProxy.getEventoLED(led.getId());
		// TODO: Quando implementa o getEventLED,descomenta linha abaixo
		led.setEvento(evento);

		log.info("LED " + led.getCor() + " -> changeEventLED(pin = '"
				+ led.getId().getPinType() + " " + led.getId().getPin()
				+ "', event = '" + led.getEvento() + "', time = '"
				+ led.getIntervalo() + "') - After sleep: "
				+ DF.format(new Date()));

		if (evento != null) {
			log.info("O evento do LED '" + led.getCor() + "' foi alterado '"
					+ led.getEvento() + "' e o time e '" + led.getIntervalo()
					+ "'");
		} else
			log.log(Level.WARNING,
					"Ocorreu um erro ao tenta buscar o evento do LED '"
							+ led.getCor() + "'");

		return new AsyncResult<LEDEntity>(led);
	}

	private void serialThreadTime() {
		try {
			Thread.sleep(mbeanProxy.getSerialThreadTime());
		} catch (InterruptedException e) {
			log.log(Level.WARNING, e.getMessage());
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

}
