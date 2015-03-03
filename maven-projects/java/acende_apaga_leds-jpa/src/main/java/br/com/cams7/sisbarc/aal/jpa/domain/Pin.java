package br.com.cams7.sisbarc.aal.jpa.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;

import br.com.cams7.jpa.domain.BaseEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.arduino.vo.ArduinoPin.ArduinoPinType;

@MappedSuperclass
public abstract class Pin extends BaseEntity<PinPK> {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PinPK id;

	public Pin() {
		super();
	}

	public Pin(ArduinoPinType pinType, Short pin) {
		super(new PinPK(pinType, pin));
	}

	/**
	 * @return the id
	 */
	public PinPK getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(PinPK id) {
		this.id = id;
	}

}
