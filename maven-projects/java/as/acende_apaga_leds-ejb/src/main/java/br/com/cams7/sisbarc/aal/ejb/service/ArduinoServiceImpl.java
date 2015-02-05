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

import br.com.cams7.sisbarc.aal.jmx.service.AppArduinoServiceMBean;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity_;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.as.service.BaseServiceImpl;
import br.com.cams7.sisbarc.util.AppException;
import br.com.cams7.sisbarc.util.AppUtil;

@Stateless
@Local(ArduinoService.class)
@Remote(AppWildflyService.class)
public class ArduinoServiceImpl extends BaseServiceImpl<LedEntity, PinPK>
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
	public Future<LedEntity> changeStatusLED(LedEntity led) {

		if (mbeanProxy != null) {
			mbeanProxy.changeStatusLED(led.getId(), led.getStatus());

			log.info("mudaStatusLED('" + led.getColor() + "','"
					+ led.getStatus() + "') - Before sleep: "
					+ DF.format(new Date()));

			serialThreadTime();

			LedEntity.Status status = mbeanProxy.getStatusLED(led.getId());
			led.setStatus(status);

			log.info("mudaStatusLED('" + led.getColor() + "','"
					+ led.getStatus() + "') - After sleep: "
					+ DF.format(new Date()));

			if (status != null) {
				log.info("LED '" + led.getColor() + "' esta '"
						+ led.getStatus() + "'");
			} else
				log.log(Level.WARNING,
						"Ocorreu um erro ao tenta buscar o status do LED '"
								+ led.getColor() + "'");

			return new AsyncResult<LedEntity>(led);

		}
		return null;
	}

	private void serialThreadTime() {
		try {
			Thread.sleep(mbeanProxy.getSerialThreadTime());
		} catch (InterruptedException e) {
			log.log(Level.WARNING, e.getMessage());
		}
	}

	@Override
	public LedEntity.Status getStatusActiveLED(LedEntity.Color ledCor) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Boolean> criteria = builder.createQuery(Boolean.class);

		Root<LedEntity> root = criteria.from(getEntityType());

		criteria.select(root.get(LedEntity_.active));

		Predicate isActiveButton = builder.isTrue(root
				.get(LedEntity_.activeByButton));
		Predicate equalColor = builder
				.equal(root.get(LedEntity_.color), ledCor);

		Predicate and = builder.and(isActiveButton, equalColor);

		criteria.where(and);

		TypedQuery<Boolean> query = getEntityManager().createQuery(criteria);
		Boolean active = query.getSingleResult();
		if (active == Boolean.TRUE)
			return LedEntity.Status.ON;

		return LedEntity.Status.OFF;
	}

}
