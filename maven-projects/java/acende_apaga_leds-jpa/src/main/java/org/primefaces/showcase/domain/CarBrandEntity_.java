package org.primefaces.showcase.domain;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CarBrandEntity.class)
public class CarBrandEntity_ {
	public static volatile SingularAttribute<CarBrandEntity, Short> id;
	public static volatile SingularAttribute<CarBrandEntity, String> name;
	public static volatile SingularAttribute<CarBrandEntity, CityEntity> city;
	public static volatile ListAttribute<CarBrandEntity, CarEntity> cars;
}
