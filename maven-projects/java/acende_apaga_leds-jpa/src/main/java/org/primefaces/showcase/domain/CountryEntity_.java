/**
 * 
 */
package org.primefaces.showcase.domain;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import org.primefaces.showcase.domain.CountryEntity.Continent;

/**
 * @author cams7
 *
 */
@StaticMetamodel(CountryEntity.class)
public class CountryEntity_ {
	public static volatile SingularAttribute<CountryEntity, Short> id;
	public static volatile SingularAttribute<CountryEntity, String> name;
	public static volatile SingularAttribute<CountryEntity, Continent> continent;
	public static volatile ListAttribute<CountryEntity, StateEntity> states;
}
