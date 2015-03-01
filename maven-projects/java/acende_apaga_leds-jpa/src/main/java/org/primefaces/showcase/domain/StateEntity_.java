/**
 * 
 */
package org.primefaces.showcase.domain;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @author cams7
 *
 */
@StaticMetamodel(StateEntity.class)
public class StateEntity_ {

	public static volatile SingularAttribute<StateEntity, Short> id;
	public static volatile SingularAttribute<StateEntity, String> name;
	public static volatile SingularAttribute<StateEntity, CountryEntity> country;
	public static volatile ListAttribute<StateEntity, CityEntity> cities;

}
