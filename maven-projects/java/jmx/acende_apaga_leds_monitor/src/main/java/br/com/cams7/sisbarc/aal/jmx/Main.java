/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx;

import java.lang.management.ManagementFactory;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import br.com.cams7.arduino.ArduinoException;
import br.com.cams7.sisbarc.aal.jmx.service.ArduinoService;
import br.com.cams7.util.AppException;
import br.com.cams7.util.AppUtil;

/**
 * @author cesar
 *
 */
public class Main {
	public static void main(String[] args) {
		try {
			Properties config = AppUtil.getPropertiesFile(Main.class,
					"config.properties");

			String serialPort = config.getProperty("SERIAL_PORT").trim();
			Integer baudRate = Integer.valueOf(config.getProperty("BAUD_RATE")
					.trim());
			Long serialThreadTime = Long.valueOf(config.getProperty(
					"SERIAL_THREAD_TIME").trim());

			ArduinoService service = new ArduinoService(serialPort, baudRate,
					serialThreadTime);

			ObjectName name = new ObjectName(
					"br.com.cams7.sisbarc.aal.jmx.service:type=ArduinoService");

			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

			mbs.registerMBean(service, name);

			System.out.println("Waiting forever...");

			Thread.sleep(Long.MAX_VALUE);

		} catch (MalformedObjectNameException | InstanceAlreadyExistsException
				| MBeanRegistrationException | NotCompliantMBeanException
				| InterruptedException | ArduinoException | AppException e) {
			e.printStackTrace();
		}
	}
}
