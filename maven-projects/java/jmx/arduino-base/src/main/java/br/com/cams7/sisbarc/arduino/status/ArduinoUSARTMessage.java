/**
 * 
 */
package br.com.cams7.sisbarc.arduino.status;

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
		setEvent(Event.MESSAGE);
	}

	/**
	 * @param status
	 * @param pinType
	 * @param pin
	 * @param pinValue
	 */
	public ArduinoUSARTMessage(Status status, PinType pinType, byte pin,
			short pinValue) {
		super(status, Event.MESSAGE, pinType, pin, pinValue);
	}

}
