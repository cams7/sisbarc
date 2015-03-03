/**
 * 
 */
package org.primefaces.showcase.faces.convert;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import org.primefaces.showcase.domain.CarEntity;

/**
 * @author cams7
 *
 */

@FacesConverter(value = "carColorConverter")
public class CarColorConverter extends EnumConverter {

	public CarColorConverter() {
		super(CarEntity.Color.class);
	}

}
