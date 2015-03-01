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
@StaticMetamodel(CityEntity.class)
public class CityEntity_ {

	public static volatile SingularAttribute<CityEntity, Short> id;
	public static volatile SingularAttribute<CityEntity, String> name;
	public static volatile SingularAttribute<CityEntity, StateEntity> state;
	public static volatile ListAttribute<CityEntity, CarBrandEntity> brands;

}
