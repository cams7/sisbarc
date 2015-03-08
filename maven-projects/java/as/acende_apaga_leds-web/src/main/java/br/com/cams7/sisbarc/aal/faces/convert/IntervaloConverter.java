/**
 * 
 */
package br.com.cams7.sisbarc.aal.faces.convert;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import br.com.cams7.sisbarc.aal.jpa.domain.Pin;

/**
 * @author cams7
 *
 */
@FacesConverter(value = "intervaloConverter")
public class IntervaloConverter extends EnumConverter {

	public IntervaloConverter() {
		super(Pin.Intervalo.class);
	}

}
