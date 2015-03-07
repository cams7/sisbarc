/**
 * 
 */
package br.com.cams7.sisbarc.arduino.vo;

/**
 * @author cams7
 *
 */
public abstract class ArduinoEEPROM extends Arduino implements EEPROMData {

	private static final long serialVersionUID = 1L;

	private byte threadInterval;
	private byte actionEvent;

	/**
	 * 
	 */
	public ArduinoEEPROM() {
		super();

		setThreadInterval((byte) 0x00);
		setActionEvent((byte) 0x00);
	}

	/**
	 * @param status
	 * @param event
	 * @param pinType
	 * @param pin
	 */
	public ArduinoEEPROM(ArduinoStatus status, ArduinoEvent event,
			ArduinoPinType pinType, byte pin, byte threadTime, byte actionEvent) {
		super(status, event, pinType, pin);

		setThreadInterval(threadTime);
		setActionEvent(actionEvent);
	}

	public void changeCurrentValues(ArduinoEEPROM arduino) {
		super.changeCurrentValues(arduino);

		setThreadInterval(arduino.getThreadInterval());
		setActionEvent(arduino.getActionEvent());
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[transmitter = "
				+ getTransmitter() + ", status = " + getStatus() + ", event = "
				+ getEvent() + ", pinType = " + getPinType() + ", pin = "
				+ getPin() + ", threadInterval = " + getThreadInterval()
				+ ", actionEvent = " + getActionEvent() + "]";
	}

	public byte getThreadInterval() {
		return threadInterval;
	}

	public void setThreadInterval(byte threadInterval) {
		this.threadInterval = threadInterval;
	}

	public byte getActionEvent() {
		return actionEvent;
	}

	public void setActionEvent(byte actionEvent) {
		this.actionEvent = actionEvent;
	}

}
