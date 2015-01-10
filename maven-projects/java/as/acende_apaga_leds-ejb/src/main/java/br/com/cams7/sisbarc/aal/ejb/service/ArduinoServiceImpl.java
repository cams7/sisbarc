package br.com.cams7.sisbarc.aal.ejb.service;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import br.com.cams7.sisbarc.aal.jmx.service.ArduinoServiceMBean;

@Stateless
public class ArduinoServiceImpl implements ArduinoService {

	private static final String HOST = "192.168.0.150";
	private static final String PORT = "1234";

	@Inject
	private Logger log;

	private JMXConnector jmxConnector = null;
	private ArduinoServiceMBean mbeanProxy;

	public ArduinoServiceImpl() {
		super();
	}

	@PostConstruct
	private void jmxConnectorInit() {
		try {
			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://" + HOST + ":" + PORT
							+ "/jmxrmi");

			jmxConnector = JMXConnectorFactory.connect(url);
			MBeanServerConnection mbeanServerConnection = jmxConnector
					.getMBeanServerConnection();
			// ObjectName should be same as your MBean name
			ObjectName mbeanName = new ObjectName(
					"br.com.cams7.sisbarc.aal.jmx.service:type=ArduinoService");

			// Get MBean proxy instance that will be used to make calls to
			// registered MBean
			mbeanProxy = (ArduinoServiceMBean) MBeanServerInvocationHandler
					.newProxyInstance(mbeanServerConnection, mbeanName,
							ArduinoServiceMBean.class, true);
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

	public void mudaStatusLEDAmarela() {
		if (mbeanProxy != null) {
			mbeanProxy.mudaStatusLEDAmarela();
			log.info("Acende LED Amarela");
		}
	}

	public void mudaStatusLEDVerde() {
		if (mbeanProxy != null) {
			mbeanProxy.mudaStatusLEDVerde();
			log.info("Acende LED Verde");
		}
	}

	public void mudaStatusLEDVermelha() {
		if (mbeanProxy != null) {
			mbeanProxy.mudaStatusLEDVermelha();
			log.info("Acende LED Vermelha");
		}
	}

}
