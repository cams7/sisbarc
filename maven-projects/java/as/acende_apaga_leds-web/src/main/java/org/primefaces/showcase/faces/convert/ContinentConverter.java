/**
 * 
 */
package org.primefaces.showcase.faces.convert;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import org.primefaces.showcase.domain.CountryEntity;

/**
 * @author cams7
 *
 */
@FacesConverter(value = "continentConverter")
public class ContinentConverter extends EnumConverter {

	public ContinentConverter() {
		super(CountryEntity.Continent.class);
	}

}
