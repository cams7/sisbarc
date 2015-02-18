/**
 * 
 */
package br.com.cams7.sisbarc.arduino.vo;

/**
 * @author cams7
 *
 */
public abstract class Arduino extends ArduinoPin {

	private ArduinoTransmitter transmitter;
	private ArduinoStatus status;
	private ArduinoEvent event;

	public Arduino() {
		super();

		setTransmitter(ArduinoTransmitter.OTHER_DEVICE);
	}

	public Arduino(ArduinoStatus status, ArduinoEvent event,
			ArduinoPinType pinType, byte pin) {
		super(pinType, pin);

		setTransmitter(ArduinoTransmitter.OTHER_DEVICE);
		setStatus(status);
		setEvent(event);

	}

	public void changeCurrentValues(Arduino arduino) {

		super.changeCurrentValues(arduino);

		setTransmitter(arduino.getTransmitter());
		setStatus(arduino.getStatus());
		setEvent(arduino.getEvent());
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

	public enum ArduinoTransmitter {
		ARDUINO, // Mensagem enviada do Arduino
		OTHER_DEVICE; // Mensagem enviada do PC
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

		private char abbreviation;

		private ArduinoEvent(char abbreviation) {
			this.abbreviation = abbreviation;
		}

		public char getAbbreviation() {
			return abbreviation;
		}
	}

}
