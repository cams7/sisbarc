/**
 * 
 */
package br.com.cams7.sisbarc.arduino.vo;

/**
 * @author cams7
 *
 */
public class ArduinoUSART extends Arduino {

	// Valor maximo da PORTA DIGITAL e 255
	public static final short DIGITAL_PIN_VALUE_MAX = 0xFF;
	// Valor maximo da PORTA ANALOGICA e 1023
	public static final short ANALOG_PIN_VALUE_MAX = 0x3FF;

	private short pinValue;

	public ArduinoUSART() {
		super();

		setEvent(ArduinoEvent.EXECUTE);
		setPinValue((short) 0x0000);
	}

	protected ArduinoUSART(ArduinoStatus status, ArduinoEvent event,
			ArduinoPinType pinType, byte pin, short pinValue) {
		super(status, event, pinType, pin);

		setPinValue(pinValue);
	}

	public ArduinoUSART(ArduinoStatus status, ArduinoPinType pinType, byte pin,
			short pinValue) {
		this(status, ArduinoEvent.EXECUTE, pinType, pin, pinValue);
	}

	public void changeCurrentValues(ArduinoUSART arduino) {
		super.changeCurrentValues(arduino);

		setPinValue(arduino.getPinValue());
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[transmitter = "
				+ getTransmitter() + ", status = " + getStatus() + ", event = "
				+ getEvent() + ", pinType = " + getPinType() + ", pin = "
				+ getPin() + ", pinValue = " + getPinValue() + "]";
	}

	public short getPinValue() {
		return pinValue;
	}

	public void setPinValue(short pinValue) {
		this.pinValue = pinValue;
	}

}
