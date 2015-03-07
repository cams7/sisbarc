/**
 * 
 */
package br.com.cams7.sisbarc.arduino.vo;

/**
 * @author cams7
 *
 */
public class ArduinoEEPROMRead extends ArduinoEEPROM {

	private static final long serialVersionUID = 1L;

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
	private ArduinoEEPROMRead(ArduinoStatus status, ArduinoPinType pinType,
			byte pin, byte threadTime, byte actionEvent) {
		super(status, ArduinoEvent.READ, pinType, pin, threadTime, actionEvent);
	}

	public ArduinoEEPROMRead(ArduinoStatus status, ArduinoPinType pinType,
			byte pin) {
		this(status, pinType, pin, (byte) -1, (byte) -1);
	}

}
