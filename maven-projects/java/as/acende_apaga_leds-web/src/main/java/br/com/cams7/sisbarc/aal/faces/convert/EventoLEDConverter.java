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
@FacesConverter(value = "eventoLEDConverter")
public class EventoLEDConverter extends EnumConverter {

	public EventoLEDConverter() {
		super(LEDEntity.EventoLED.class);
	}

}
