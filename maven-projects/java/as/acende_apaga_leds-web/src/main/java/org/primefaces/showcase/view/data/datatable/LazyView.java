package org.primefaces.showcase.view.data.datatable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.showcase.domain.CarBrandEntity_;
import org.primefaces.showcase.domain.CarEntity;
import org.primefaces.showcase.domain.CarEntity_;
import org.primefaces.showcase.domain.CityEntity_;
import org.primefaces.showcase.domain.StateEntity_;
import org.primefaces.showcase.service.CarService;

import br.com.cams7.sisbarc.jpa.domain.SortOrderField;
import br.com.cams7.sisbarc.util.AppUtil;

@ManagedBean(name = "dtLazyView")
@ViewScoped
public class LazyView implements Serializable {

	private static final long serialVersionUID = 1L;

	private LazyDataModel<CarEntity> lazyModel;

	private CarEntity selectedCar;

	@EJB
	private CarService service;

	private final short PAGE_FIRST = 0;
	private final byte PAGE_SIZE = 10;

	private short lastPageFirst;
	private byte lastPageSize;

	private String lastSortField;
	private SortOrder lastSortOrder;

	private Map<String, String> lastFilters;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		lastPageFirst = PAGE_FIRST;
		lastPageSize = PAGE_SIZE;

		lastSortOrder = SortOrder.UNSORTED;

		lazyModel = new LazyDataModel<CarEntity>() {

			private static final long serialVersionUID = 1L;

			private List<CarEntity> entities;

			@Override
			public CarEntity getRowData(String rowKey) {
				for (CarEntity car : entities) {
					if (String.valueOf(car.getId()).equals(rowKey))
						return car;
				}

				return null;
			}

			@Override
			public Object getRowKey(CarEntity car) {
				return String.valueOf(car.getId());
			}

			@Override
			public List<CarEntity> load(int first, int pageSize,
					final String sortField, final SortOrder sortOrder,
					Map<String, String> filters) {

				boolean rowCountChanged = false;

				if (pageSize != lastPageSize) {
					lastPageSize = (byte) pageSize;
				} else if ((first == 0 || first == lastPageFirst)) {
					if (sortField != null
							&& (sortField != lastSortField || sortOrder != lastSortOrder)) {
						lastSortField = sortField;
						lastSortOrder = sortOrder;
					} else if (((!filters.isEmpty() || lastFilters != null) && !AppUtil
							.equalMaps(filters, lastFilters))) {

						boolean changeFilter = true;

						if (lastPageFirst - pageSize == 0
								&& lastFilters != null) {
							if (lastFilters.size() == 1) {
								String key = lastFilters.keySet().toArray(
										new String[lastFilters.size()])[0];
								String value = lastFilters.get(key);

								if (value.length() > 1)
									changeFilter = false;
							} else
								changeFilter = false;

						}

						if (changeFilter) {
							if (!filters.isEmpty())
								lastFilters = filters;
							else
								lastFilters = null;

							rowCountChanged = true;
						}

					}
				}

				lastPageFirst = (short) first;

				entities = service.search(lastPageFirst, lastPageSize,
						lastSortField,
						SortOrderField.valueOf(lastSortOrder.name()),
						lastFilters, CarEntity_.brand, CarBrandEntity_.city,
						CityEntity_.state, StateEntity_.country);

				// rowCount
				if (rowCountChanged)
					setRowCount((int) service.count(lastFilters,
							CarEntity_.brand, CarBrandEntity_.city,
							CityEntity_.state, StateEntity_.country));

				return entities;
			}
		};

		lazyModel.setRowCount((int) service.count(CarEntity_.brand,
				CarBrandEntity_.city, CityEntity_.state, StateEntity_.country));
	}

	public void onRowSelect(SelectEvent event) {
		CarEntity selectedCar = (CarEntity) event.getObject();
		setSelectedCar(selectedCar);

		System.out.println("selectedCar: " + selectedCar);
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Car Selected", String.valueOf(selectedCar.getId()));
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public LazyDataModel<CarEntity> getLazyModel() {
		return lazyModel;
	}

	public CarEntity getSelectedCar() {
		return selectedCar;
	}

	public void setSelectedCar(CarEntity selectedCar) {
		this.selectedCar = selectedCar;
	}

	public CarEntity.Color[] getColors() {
		return CarEntity.Color.values();
	}

	/**
	 * @return the first
	 */
	public short getFirst() {
		return lastPageFirst;
	}

	/**
	 * @return
	 */
	public byte getRows() {
		return lastPageSize;
	}

}