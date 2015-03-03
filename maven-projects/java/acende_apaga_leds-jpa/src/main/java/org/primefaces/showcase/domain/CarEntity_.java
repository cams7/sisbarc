/**
 * 
 */
package org.primefaces.showcase.domain;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @author cams7
 *
 */
@StaticMetamodel(CarEntity.class)
public class CarEntity_ {
	public static volatile SingularAttribute<CarEntity, Integer> id;
	public static volatile SingularAttribute<CarEntity, Short> year;
	public static volatile SingularAttribute<CarEntity, CarEntity.Color> color;
	public static volatile SingularAttribute<CarEntity, CarBrandEntity> brand;
	public static volatile SingularAttribute<CarEntity, Float> price;
	public static volatile SingularAttribute<CarEntity, Boolean> sold;
	public static volatile SingularAttribute<CarEntity, Date> saleDate;
}
