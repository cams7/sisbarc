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
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.Status;
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
		boolean status = false;

		switch (led.getStatus()) {
		case ACESA:
			status = true;
			break;
		case APAGADA:
			status = false;
			break;
		default:
			break;
		}

		if (mbeanProxy != null) {
			switch (led.getCor()) {
			case AMARELA:
				mbeanProxy.mudaStatusLEDAmarela(status);
				break;
			case VERDE:
				mbeanProxy.mudaStatusLEDVerde(status);
				break;
			case VERMELHA:
				mbeanProxy.mudaStatusLEDVermelha(status);
				break;
			default:
				break;
			}

			log.info("mudaStatusLED('" + led.getCor() + "','" + led.getStatus()
					+ "') - Before sleep: " + DF.format(new Date()));
			serialThreadTime();
			log.info("mudaStatusLED('" + led.getCor() + "','" + led.getStatus()
					+ "') - After sleep: " + DF.format(new Date()));

			Boolean ledLigada = null;

			switch (led.getCor()) {
			case AMARELA:
				ledLigada = mbeanProxy.isLEDAmarelaAcesa();
				break;
			case VERDE:
				ledLigada = mbeanProxy.isLEDVerdeAcesa();
				break;
			case VERMELHA:
				ledLigada = mbeanProxy.isLEDVermelhaAcesa();
				break;
			default:
				break;
			}
			if (ledLigada != null) {
				led.setStatus(ledLigada ? Status.ACESA : Status.APAGADA);

				log.info("LED '" + led.getCor() + "' esta '" + led.getStatus()
						+ "'");
			}

			return new AsyncResult<LedEntity>(led);

		}
		return null;
	}

	private void serialThreadTime() {
		try {
			Thread.sleep(mbeanProxy.getSerialThreadTime() + 250);
		} catch (InterruptedException e) {
			log.log(Level.WARNING, e.getMessage());
		}
	}

}
