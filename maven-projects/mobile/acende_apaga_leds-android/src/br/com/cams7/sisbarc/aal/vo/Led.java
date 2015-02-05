package br.com.cams7.sisbarc.aal.vo;

public class Led {
	private PinType pinType;
	private Short pin;

	private Status status;

	public Led() {
		super();
	}

	/**
	 * @return the pinType
	 */
	public PinType getPinType() {
		return pinType;
	}

	/**
	 * @param pinType
	 *            the pinType to set
	 */
	public void setPinType(PinType pinType) {
		this.pinType = pinType;
	}

	/**
	 * @return the pin
	 */
	public Short getPin() {
		return pin;
	}

	/**
	 * @param pin
	 *            the pin to set
	 */
	public void setPin(Short pin) {
		this.pin = pin;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public enum Status {
		ON, OFF
	}

	public enum PinType {
		DIGITAL, // Pino DIGITAL
		ANALOG;// Pino ANALOGICO
	}

}