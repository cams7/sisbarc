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
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import br.com.cams7.sisbarc.aal.jmx.service.AppArduinoServiceMBean;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity;
import br.com.cams7.util.AppException;
import br.com.cams7.util.AppUtil;

@Stateless
public class ArduinoServiceImpl implements ArduinoService {

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
	public Future<LedEntity> mudaStatusLED(LedEntity led) {

		if (mbeanProxy != null) {
			mbeanProxy.mudaStatusLED(led.getCor(), led.getStatus());

			log.info("mudaStatusLED('" + led.getCor() + "','" + led.getStatus()
					+ "') - Before sleep: " + DF.format(new Date()));
			serialThreadTime();
			log.info("mudaStatusLED('" + led.getCor() + "','" + led.getStatus()
					+ "') - After sleep: " + DF.format(new Date()));

			LedEntity.Status ledLigada = mbeanProxy.getStatusLED(led.getCor());

			if (ledLigada != null) {
				led.setStatus(ledLigada);

				log.info("LED '" + led.getCor() + "' esta '" + led.getStatus()
						+ "'");
			} else
				log.log(Level.WARNING,
						"Ocorreu um erro ao tenta buscar o status do LED '"
								+ led.getCor() + "'");

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

}
