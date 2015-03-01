package org.primefaces.showcase.service;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.primefaces.showcase.domain.CarEntity;

import br.com.cams7.sisbarc.as.service.BaseServiceImpl;

@Stateless
@Local(CarService.class)
public class CarServiceImpl extends BaseServiceImpl<CarEntity, Integer>
		implements CarService {

	public CarServiceImpl() {
		super();
	}

}