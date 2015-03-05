package org.primefaces.showcase.service;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.showcase.domain.CarEntity;

import br.com.cams7.as.service.BaseServiceImpl;

@Stateless
@Local(CarService.class)
public class CarServiceImpl extends BaseServiceImpl<CarEntity, Integer>
		implements CarService {

	@PersistenceContext(unitName = "acendeApagaLEDsUnit")
	private EntityManager entityManager;

	public CarServiceImpl() {
		super();
	}

	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}

}