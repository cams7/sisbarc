package br.com.cams7.sisbarc.aal.ejb.service;

import java.io.IOException;
import java.util.Date;
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

@Stateless
public class ArduinoServiceImpl implements ArduinoService {

	private static final String JMX_HOST = "192.168.0.150";
	private static final String JMX_PORT = "1234";

	@Inject
	private Logger log;

	private JMXConnector jmxConnector = null;
	private AppArduinoServiceMBean mbeanProxy;

	public ArduinoServiceImpl() {
		super();
	}

	@PostConstruct
	private void jmxConnectorInit() {
		try {
			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://" + JMX_HOST + ":"
							+ JMX_PORT + "/jmxrmi");

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
		} catch (IOException | MalformedObjectNameException e) {
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
	public Future<Boolean> mudaStatusLEDAmarela() {
		if (mbeanProxy != null) {
			mbeanProxy.mudaStatusLEDAmarela();

			log.info("mudaStatusLEDAmarela() - Before sleep: "
					+ DF.format(new Date()));
			serialThreadTime();
			log.info("mudaStatusLEDAmarela() - After sleep: "
					+ DF.format(new Date()));

			Boolean ledLigada = mbeanProxy.isLedAmarelaLigada();

			log.info("LED 'Amarela' esta "
					+ (ledLigada ? "'Acesa'" : "'Apagada'"));
			return new AsyncResult<Boolean>(ledLigada);
		}
		return null;
	}

	@Asynchronous
	public Future<Boolean> mudaStatusLEDVerde() {
		if (mbeanProxy != null) {
			mbeanProxy.mudaStatusLEDVerde();

			log.info("mudaStatusLEDVerde() - Before sleep: "
					+ DF.format(new Date()));
			serialThreadTime();
			log.info("mudaStatusLEDVerde() - After sleep: "
					+ DF.format(new Date()));

			Boolean ledLigada = mbeanProxy.isLedVerdeLigada();

			log.info("LED 'Verde' esta "
					+ (ledLigada ? "'Acesa'" : "'Apagada'"));
			return new AsyncResult<Boolean>(ledLigada);
		}
		return null;
	}

	@Asynchronous
	public Future<Boolean> mudaStatusLEDVermelha() {
		if (mbeanProxy != null) {
			mbeanProxy.mudaStatusLEDVermelha();

			log.info("mudaStatusLEDVermelha() - Before sleep: "
					+ DF.format(new Date()));
			serialThreadTime();
			log.info("mudaStatusLEDVermelha() - After sleep: "
					+ DF.format(new Date()));

			Boolean ledLigada = mbeanProxy.isLedVermelhaLigada();

			log.info("LED 'Vermelha' esta "
					+ (ledLigada ? "'Acesa'" : "'Apagada'"));
			return new AsyncResult<Boolean>(ledLigada);
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
