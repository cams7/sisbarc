/**
 * 
 */
package br.com.cams7.sisbarc.arduino.vo;

import java.util.Properties;

import br.com.cams7.util.AppException;
import br.com.cams7.util.AppUtil;

/**
 * @author cams7
 *
 */
public abstract class ArduinoPin {

	// Numero maximo da PORTA DIGITAL e 63
	public static final byte DIGITAL_PIN_MAX = 0x3F;
	// Numero maximo da PORTA ANALOGICA e 15
	public static final byte ANALOG_PIN_MAX = 0x0F;

	private static byte[] pinsDigital;
	private static byte[] pinsDigitalPWM;
	private static byte[] pinsAnalog;

	private ArduinoPinType pinType;

	private byte pin;

	static {
		try {
			Properties pins = AppUtil.getPropertiesFile(Arduino.class,
					"pins.properties");
			final String SEPARATE = ",";

			pinsDigital = getPins(pins.getProperty("PIN_DIGITAL").trim()
					.split(SEPARATE));
			pinsDigitalPWM = getPins(pins.getProperty("PIN_DIGITAL_PWM").trim()
					.split(SEPARATE));
			pinsAnalog = getPins(pins.getProperty("PIN_ANALOG").trim()
					.split(SEPARATE));
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public ArduinoPin() {
		setPin((byte) 0x00);
	}

	public ArduinoPin(ArduinoPinType pinType, byte pin) {
		this();

		setPinType(pinType);
		setPin(pin);
	}

	public void changeCurrentValues(ArduinoPin arduino) {
		setPinType(arduino.getPinType());
		setPin(arduino.getPin());
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[pinType = " + getPinType()
				+ ", pin = " + getPin() + "]";
	}

	private static byte[] getPins(final String[] values) {
		byte[] pins = new byte[values.length];
		for (byte i = 0; i < values.length; i++)
			pins[i] = Byte.parseByte(values[i].trim());
		return pins;
	}

	public ArduinoPinType getPinType() {
		return pinType;
	}

	public void setPinType(ArduinoPinType pinType) {
		this.pinType = pinType;
	}

	public byte getPin() {
		return pin;
	}

	public void setPin(byte pin) {
		this.pin = pin;
	}

	public enum ArduinoPinType {
		DIGITAL('d'), // Porta Digital
		ANALOG('a'); // Porta Analogica

		private char abbreviation;

		private ArduinoPinType(char abbreviation) {
			this.abbreviation = abbreviation;
		}

		public char getAbbreviation() {
			return abbreviation;
		}

	}

	public static byte[] getPinsDigital() {
		return pinsDigital;
	}

	public static byte[] getPinsDigitalPWM() {
		return pinsDigitalPWM;
	}

	public static byte[] getPinsAnalog() {
		return pinsAnalog;
	}

}
