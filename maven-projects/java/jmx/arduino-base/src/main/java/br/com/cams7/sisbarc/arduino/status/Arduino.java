/**
 * 
 */
package br.com.cams7.sisbarc.arduino.status;

import java.util.Properties;

import br.com.cams7.sisbarc.util.AppException;
import br.com.cams7.sisbarc.util.AppUtil;

/**
 * @author cams7
 *
 */
public abstract class Arduino {

	// Numero maximo da PORTA DIGITAL e 63
	public static final byte DIGITAL_PIN_MAX = 0x3F;
	// Numero maximo da PORTA ANALOGICA e 15
	public static final byte ANALOG_PIN_MAX = 0x0F;

	private static byte[] pinsDigital;
	private static byte[] pinsDigitalPWM;
	private static byte[] pinsAnalog;

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

	private ArduinoTransmitter transmitter;
	private ArduinoStatus status;
	private ArduinoEvent event;

	private ArduinoPinType pinType;

	private byte pin;

	public Arduino() {
		super();

		setTransmitter(ArduinoTransmitter.PC);
		setPin((byte) 0x00);
	}

	public Arduino(ArduinoStatus status, ArduinoEvent event,
			ArduinoPinType pinType, byte pin) {
		this();

		setStatus(status);
		setEvent(event);
		setPinType(pinType);
		setPin(pin);
	}

	public void changeCurrentValues(Arduino arduino) {
		setTransmitter(arduino.getTransmitter());
		setStatus(arduino.getStatus());
		setEvent(arduino.getEvent());

		setPinType(arduino.getPinType());
		setPin(arduino.getPin());
	}

	private static byte[] getPins(final String[] values) {
		byte[] pins = new byte[values.length];
		for (byte i = 0; i < values.length; i++)
			pins[i] = Byte.parseByte(values[i].trim());
		return pins;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[transmitter = "
				+ getTransmitter() + ", status = " + getStatus() + ", event = "
				+ getEvent() + ", pinType = " + getPinType() + ", pin = "
				+ getPin() + "]";
	}

	public ArduinoTransmitter getTransmitter() {
		return transmitter;
	}

	public void setTransmitter(ArduinoTransmitter transmitter) {
		this.transmitter = transmitter;
	}

	public ArduinoStatus getStatus() {
		return status;
	}

	public void setStatus(ArduinoStatus status) {
		this.status = status;
	}

	public ArduinoEvent getEvent() {
		return event;
	}

	public void setEvent(ArduinoEvent event) {
		this.event = event;
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

	public enum ArduinoTransmitter {
		ARDUINO, // Mensagem enviada do Arduino
		PC; // Mensagem enviada do PC
	}

	public enum ArduinoStatus {
		SEND, // Mensagem de envio que nao exige uma resposta
		SEND_RESPONSE, // Mensagem de envio que exige uma resposta
		RESPONSE, // Mensagem de resposta
		RESPONSE_RESPONSE;// Mensagem de resposta que exige outra resposta
	}

	public enum ArduinoEvent {
		EXECUTE('x'), // Executa uma ação, ex: ACENDE ou APAGA um LED
		WRITE('w'), // Escreve o TIME e EVENTO para um determinado PINO no
					// Arduino
		READ('r'), // Lê o TIME e EVENTO para um determinado PINO no Arduino
		MESSAGE('m');// Caso aconteca algo fora do previsto o Arduino manda uma
		// mensagem

		private char type;

		private ArduinoEvent(char type) {
			this.type = type;
		}

		public char getType() {
			return type;
		}
	}

	public enum ArduinoPinType {
		DIGITAL('d'), // Porta Digital
		ANALOG('a'); // Porta Analogica

		private char type;

		private ArduinoPinType(char type) {
			this.type = type;
		}

		public char getType() {
			return type;
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
