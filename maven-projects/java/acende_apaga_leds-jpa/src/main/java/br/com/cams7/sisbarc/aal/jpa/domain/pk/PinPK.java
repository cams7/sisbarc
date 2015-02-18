/**
 * 
 */
package br.com.cams7.sisbarc.aal.jpa.domain.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import br.com.cams7.sisbarc.arduino.vo.ArduinoPin.ArduinoPinType;

/**
 * @author cams7
 *
 */
@Embeddable
public class PinPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "tipo_pino", insertable = false, updatable = false)
	private ArduinoPinType pinType;

	@NotNull
	@Column(name = "pino_arduino", insertable = false, updatable = false)
	private Short pin;

	/**
	 * 
	 */
	public PinPK() {
		super();
	}

	public PinPK(ArduinoPinType pinType, Short pin) {
		this();

		this.pinType = pinType;
		this.pin = pin;
	}

	@Override
	public String toString() {
		return "PK[pinType = " + getPinType() + ", pin = " + getPin() + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object entityPK) {
		if (this == entityPK) {
			return true;
		}
		if (!(entityPK instanceof PinPK)) {
			return false;
		}
		PinPK pk = (PinPK) entityPK;
		return this.pinType.equals(pk.pinType) && this.pin.equals(pk.pin);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.pinType.hashCode();
		hash = hash * prime + this.pin.hashCode();

		return hash;
	}

	/**
	 * @return the pinType
	 */
	public ArduinoPinType getPinType() {
		return pinType;
	}

	/**
	 * @param pinType
	 *            the pinType to set
	 */
	public void setPinType(ArduinoPinType pinType) {
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

}
