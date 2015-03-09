/**
 * 
 */
package br.com.cams7.sisbarc.aal.ejb.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.cams7.as.service.BaseServiceImpl;
import br.com.cams7.jpa.domain.BaseEntity;
import br.com.cams7.sisbarc.aal.jmx.service.AppArduinoServiceMBean;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Evento;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Intervalo;
import br.com.cams7.sisbarc.arduino.vo.EEPROMData;
import br.com.cams7.util.AppException;
import br.com.cams7.util.AppUtil;

/**
 * @author cams7
 *
 */
public abstract class AALServiceImpl<E extends BaseEntity<ID>, ID extends Serializable>
		extends BaseServiceImpl<E, ID> implements AALService<E, ID> {

	@PersistenceContext(unitName = "acendeApagaLEDsUnit")
	private EntityManager entityManager;

	private JMXConnector jmxConnector;
	private AppArduinoServiceMBean mbeanProxy;

	public AALServiceImpl() {
		super();
	}

	@PostConstruct
	private void jmxConnectorInit() {
		try {
			Properties config = AppUtil.getPropertiesFile(LEDServiceImpl.class,
					"config.properties");

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

	protected void serialThreadTime() {
		try {
			Thread.sleep(getMbeanProxy().getSerialThreadTime());
		} catch (InterruptedException e) {
			getLog().log(Level.WARNING, e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.sisbarc.aal.ejb.service.AALService#atualizaPino(br.com.cams7
	 * .sisbarc.aal.jpa.domain.Pin)
	 */
	@Asynchronous
	public Future<Boolean> atualizaPino(E entidade) {
		if (getMbeanProxy() == null)
			return null;

		Pin pino = (Pin) entidade;

		getMbeanProxy().alteraEvento(pino.getId(), pino.getEvento(),
				pino.getIntervalo());

		serialThreadTime();

		Evento evento = getMbeanProxy().getEvento(pino.getId());

		Boolean arduinoRun = Boolean.FALSE;

		if (evento != null) {
			pino.setEvento(evento);
			save(entidade);
			arduinoRun = Boolean.TRUE;

			getLog().info(
					"O evento do PINO '" + pino.getId() + "' foi alterado '"
							+ pino.getEvento() + "'");
		} else
			getLog().log(
					Level.WARNING,
					"Ocorreu um erro ao tenta buscar o EVENTO do PINO '"
							+ pino.getId() + "'");

		return new AsyncResult<Boolean>(arduinoRun);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.sisbarc.aal.ejb.service.AALService#sincronizaEventos(java
	 * .util.List)
	 */
	@Asynchronous
	public Future<Boolean> sincronizaEventos(List<E> entidades) {
		if (getMbeanProxy() == null)
			return null;

		for (E entidade : entidades)
			getMbeanProxy().buscaDados(((Pin) entidade).getId());

		serialThreadTime();

		Boolean arduinoRun = Boolean.TRUE;

		for (E entidade : entidades) {
			Pin pino = (Pin) entidade;

			EEPROMData data = getMbeanProxy().getDados(pino.getId());
			if (data == null) {
				arduinoRun = Boolean.FALSE;
				break;
			}

			Evento evento = Evento.values()[data.getActionEvent()];
			Intervalo intervalo = Intervalo.values()[data.getThreadInterval()];

			pino.setEvento(evento);
			pino.setIntervalo(intervalo);
		}

		if (arduinoRun) {
			update(entidades);
			getLog().info("Os EVENTOs dos PINOs foram sincronizados");
		} else
			getLog().log(Level.WARNING,
					"Ocorreu um erro ao tenta buscar os DADOs dos PINOs");

		return new AsyncResult<Boolean>(arduinoRun);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.sisbarc.aal.ejb.service.AALService#alteraEventos(java.util
	 * .List)
	 */
	@Asynchronous
	public Future<Boolean> alteraEventos(List<E> entidades) {
		if (getMbeanProxy() == null)
			return null;

		for (E entidade : entidades) {
			Pin pino = (Pin) entidade;
			getMbeanProxy().alteraEvento(pino.getId(), pino.getEvento(),
					pino.getIntervalo());
		}

		serialThreadTime();

		Boolean arduinoRun = Boolean.TRUE;

		for (E entidade : entidades) {
			Evento evento = getMbeanProxy().getEvento(((Pin) entidade).getId());

			if (evento == null) {
				arduinoRun = Boolean.FALSE;
				break;
			}
		}

		if (arduinoRun)
			getLog().info("Os EVENTOs dos PINOs foram alterados");
		else
			getLog().log(Level.WARNING,
					"Ocorreu um erro ao tenta buscar os EVENTOs dos PINOs");

		return new AsyncResult<Boolean>(arduinoRun);
	}

	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * @return the mbeanProxy
	 */
	protected AppArduinoServiceMBean getMbeanProxy() {
		return mbeanProxy;
	}

}
