/**
 * 
 */
package br.com.cams7.sisbarc.aal.jpa.domain.entity;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.cams7.sisbarc.aal.jpa.domain.Pin;
import br.com.cams7.sisbarc.arduino.vo.ArduinoPin.ArduinoPinType;

/**
 * @author cams7
 *
 */
@XmlRootElement
@Entity
@Table(name = "potenciometro")
@NamedQuery(name = "Potenciometro.findAll", query = "SELECT potenciometro FROM PotenciometroEntity potenciometro")
public class PotenciometroEntity extends Pin {

	private static final long serialVersionUID = 1L;

	public PotenciometroEntity() {
		super();
	}

	/**
	 * @param pinType
	 * @param pin
	 */
	public PotenciometroEntity(ArduinoPinType pinType, Short pin) {
		super(pinType, pin);
	}

}
