/**
 * 
 */
package br.com.cams7.sisbarc.aal.faces.convert;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import br.com.cams7.sisbarc.arduino.vo.ArduinoPin;

/**
 * @author cams7
 *
 */
@FacesConverter(value = "arduinoPinTypeConverter")
public class ArduinoPinTypeConverter extends EnumConverter {

	public ArduinoPinTypeConverter() {
		super(ArduinoPin.ArduinoPinType.class);
	}

}
