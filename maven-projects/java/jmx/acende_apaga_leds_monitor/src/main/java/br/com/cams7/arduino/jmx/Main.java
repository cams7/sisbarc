/**
 * 
 */
package br.com.cams7.arduino.jmx;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import br.com.cams7.arduino.ArduinoException;

/**
 * @author cesar
 *
 */
public class Main {
	public static void main(String[] args) {
		try {
			AppArduinoService service = new AppArduinoService();

			ObjectName name = new ObjectName(
					"br.com.cams7.arduino.jmx:type=AppArduinoService");

			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

			mbs.registerMBean(service, name);

			System.out.println("Waiting forever...");

			Thread.sleep(Long.MAX_VALUE);

		} catch (MalformedObjectNameException | InstanceAlreadyExistsException
				| MBeanRegistrationException | NotCompliantMBeanException
				| InterruptedException | ArduinoException e) {
			e.printStackTrace();
		}
	}
}
