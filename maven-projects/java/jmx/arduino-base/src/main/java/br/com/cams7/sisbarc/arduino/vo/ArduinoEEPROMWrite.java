/**
 * 
 */
package br.com.cams7.sisbarc.arduino.vo;

/**
 * @author cams7
 *
 */
public class ArduinoEEPROMWrite extends ArduinoEEPROM {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ArduinoEEPROMWrite() {
		super();
		setEvent(ArduinoEvent.WRITE);
	}

	/**
	 * @param status
	 * @param event
	 * @param pinType
	 * @param pin
	 * @param threadTime
	 * @param actionEvent
	 */
	public ArduinoEEPROMWrite(ArduinoStatus status, ArduinoPinType pinType,
			byte pin, byte threadTime, byte actionEvent) {
		super(status, ArduinoEvent.WRITE, pinType, pin, threadTime, actionEvent);
	}

}
