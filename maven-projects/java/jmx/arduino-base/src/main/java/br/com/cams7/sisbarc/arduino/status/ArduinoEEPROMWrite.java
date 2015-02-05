/**
 * 
 */
package br.com.cams7.sisbarc.arduino.status;

/**
 * @author cams7
 *
 */
public class ArduinoEEPROMWrite extends ArduinoEEPROM {

	/**
	 * 
	 */
	public ArduinoEEPROMWrite() {
		super();
		setEvent(Event.WRITE);
	}

	/**
	 * @param status
	 * @param event
	 * @param pinType
	 * @param pin
	 * @param threadTime
	 * @param actionEvent
	 */
	public ArduinoEEPROMWrite(Status status, PinType pinType, byte pin,
			byte threadTime, byte actionEvent) {
		super(status, Event.WRITE, pinType, pin, threadTime, actionEvent);
	}

}
