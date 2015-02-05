/**
 * 
 */
package br.com.cams7.sisbarc.arduino.status;

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
		setEvent(Event.READ);
	}

	/**
	 * @param status
	 * @param event
	 * @param pinType
	 * @param pin
	 * @param threadTime
	 * @param actionEvent
	 */
	public ArduinoEEPROMRead(Status status, PinType pinType, byte pin,
			byte threadTime, byte actionEvent) {
		super(status, Event.READ, pinType, pin, threadTime, actionEvent);
	}

}
