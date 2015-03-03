package org.primefaces.showcase.view.data.datatable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.showcase.domain.CarBrandEntity_;
import org.primefaces.showcase.domain.CarEntity;
import org.primefaces.showcase.domain.CarEntity_;
import org.primefaces.showcase.domain.CityEntity_;
import org.primefaces.showcase.domain.CountryEntity;
import org.primefaces.showcase.domain.StateEntity_;
import org.primefaces.showcase.service.CarService;

import br.com.cams7.as.view.BaseView;

@ManagedBean(name = "dtLazyView")
@ViewScoped
public class LazyView extends BaseView<CarService, CarEntity> {

	private static final long serialVersionUID = 1L;

	@EJB
	private CarService service;

	@SuppressWarnings("unchecked")
	public LazyView() {
		super(CarEntity_.brand, CarBrandEntity_.city, CityEntity_.state,
				StateEntity_.country);
	}

	@Override
	protected CarService getService() {
		return service;
	}

	@Override
	protected void init() {
		getLog().info("Init View");
	}

	public CarEntity.Color[] getColors() {
		return CarEntity.Color.values();
	}

	public CountryEntity.Continent[] getContinents() {
		return CountryEntity.Continent.values();
	}

}