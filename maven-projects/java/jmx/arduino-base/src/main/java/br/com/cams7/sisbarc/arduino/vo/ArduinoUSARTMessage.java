/**
 * 
 */
package br.com.cams7.sisbarc.arduino.vo;

/**
 * @author cams7
 *
 */
public class ArduinoUSARTMessage extends ArduinoUSART {

	/**
	 * 
	 */
	public ArduinoUSARTMessage() {
		super();
		setEvent(ArduinoEvent.MESSAGE);
	}

	/**
	 * @param status
	 * @param pinType
	 * @param pin
	 * @param pinValue
	 */
	private ArduinoUSARTMessage(ArduinoStatus status, ArduinoPinType pinType,
			byte pin, short pinValue) {
		super(status, ArduinoEvent.MESSAGE, pinType, pin, pinValue);
	}

	public ArduinoUSARTMessage(ArduinoStatus status, ArduinoPinType pinType,
			byte pin) {
		this(status, pinType, pin, (byte) 0);
	}

}
