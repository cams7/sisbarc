/**
 * 
 */
package br.com.cams7.sisbarc.aal.faces.convert;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity;

/**
 * @author cams7
 *
 */
@FacesConverter(value = "estadoLEDConverter")
public class EstadoLEDConverter extends EnumConverter {

	public EstadoLEDConverter() {
		super(LEDEntity.EstadoLED.class);
	}

}
