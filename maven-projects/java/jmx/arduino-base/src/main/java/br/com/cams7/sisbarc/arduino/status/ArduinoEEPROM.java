/**
 * 
 */
package br.com.cams7.sisbarc.arduino.status;

/**
 * @author cams7
 *
 */
public abstract class ArduinoEEPROM extends ArduinoStatus {

	public static final byte THREAD_TIME_MAX = 0x07; // 7

	public static final byte DIGITAL_ACTION_EVENT_MAX = 0x1F; // 31
	public static final byte ANALOG_ACTION_EVENT_MAX = 0x7F; // 127

	private byte threadTime;
	private byte actionEvent;

	/**
	 * 
	 */
	public ArduinoEEPROM() {
		super();

		setThreadTime((byte) 0x00);
		setActionEvent((byte) 0x00);
	}

	/**
	 * @param status
	 * @param event
	 * @param pinType
	 * @param pin
	 */
	public ArduinoEEPROM(Status status, Event event, PinType pinType, byte pin,
			byte threadTime, byte actionEvent) {
		super(status, event, pinType, pin);

		setThreadTime(threadTime);
		setActionEvent(actionEvent);
	}

	public void changeCurrentValues(ArduinoEEPROM arduino) {
		super.changeCurrentValues(arduino);

		setThreadTime(arduino.getThreadTime());
		setActionEvent(arduino.getActionEvent());
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[transmitter = "
				+ getTransmitter() + ", status = " + getStatus() + ", event = "
				+ getEvent() + ", pinType = " + getPinType() + ", pin = "
				+ getPin() + ", threadTime = " + getThreadTime()
				+ ", actionEvent = " + getActionEvent() + "]";
	}

	public byte getThreadTime() {
		return threadTime;
	}

	public void setThreadTime(byte threadTime) {
		this.threadTime = threadTime;
	}

	public byte getActionEvent() {
		return actionEvent;
	}

	public void setActionEvent(byte actionEvent) {
		this.actionEvent = actionEvent;
	}

}
