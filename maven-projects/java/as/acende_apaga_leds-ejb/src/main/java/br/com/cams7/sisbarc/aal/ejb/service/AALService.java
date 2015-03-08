/**
 * 
 */
package br.com.cams7.sisbarc.aal.ejb.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import br.com.cams7.util.AppException;
import br.com.cams7.util.AppUtil;

/**
 * @author cams7
 *
 */
public abstract class AALService<E extends BaseEntity<ID>, ID extends Serializable>
		extends BaseServiceImpl<E, ID> {

	@PersistenceContext(unitName = "acendeApagaLEDsUnit")
	private EntityManager entityManager;

	private JMXConnector jmxConnector;
	private AppArduinoServiceMBean mbeanProxy;

	public AALService() {
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
