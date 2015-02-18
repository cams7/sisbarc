/**
 * 
 */
package br.com.cams7.sisbarc.arduino.vo;

/**
 * @author cams7
 *
 */
public class ArduinoEEPROMRead extends ArduinoEEPROM {

	/**
	 * 
	 */
	public ArduinoEEPROMRead() {
		super();
		setEvent(ArduinoEvent.READ);
	}

	/**
	 * @param status
	 * @param event
	 * @param pinType
	 * @param pin
	 * @param threadTime
	 * @param actionEvent
	 */
	public ArduinoEEPROMRead(ArduinoStatus status, ArduinoPinType pinType, byte pin,
			byte threadTime, byte actionEvent) {
		super(status, ArduinoEvent.READ, pinType, pin, threadTime, actionEvent);
	}

}
