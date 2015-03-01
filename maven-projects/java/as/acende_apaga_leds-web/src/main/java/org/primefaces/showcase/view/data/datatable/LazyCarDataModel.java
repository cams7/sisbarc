package org.primefaces.showcase.view.data.datatable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.showcase.domain.CarEntity;

/**
 * Dummy implementation of LazyDataModel that uses a list to mimic a real
 * datasource like a database.
 */
public class LazyCarDataModel extends LazyDataModel<CarEntity> {

	private static final long serialVersionUID = 1L;

	private Iterable<CarEntity> datasource;

	public LazyCarDataModel(Iterable<CarEntity> datasource) {
		this.datasource = datasource;
	}

	@Override
	public CarEntity getRowData(String rowKey) {
		for (CarEntity car : datasource) {
			if (car.getId().equals(rowKey))
				return car;
		}

		return null;
	}

	@Override
	public Object getRowKey(CarEntity car) {
		return car.getId();
	}

	@Override
	public List<CarEntity> load(int first, int pageSize,
			final String sortField, final SortOrder sortOrder,
			Map<String, String> filters) {
		List<CarEntity> data = new ArrayList<CarEntity>();

		// filter
		for (CarEntity car : datasource) {
			boolean match = true;

			if (filters != null) {
				for (Iterator<String> it = filters.keySet().iterator(); it
						.hasNext();) {
					try {
						String filterProperty = it.next();
						Object filterValue = filters.get(filterProperty);

						Field field = car.getClass().getDeclaredField(
								filterProperty);
						field.setAccessible(true);

						String fieldValue = String.valueOf(field.get(car));

						if (filterValue == null
								|| fieldValue.toLowerCase().startsWith(
										String.valueOf(filterValue)
												.toLowerCase())) {
							match = true;
						} else {
							match = false;
							break;
						}
					} catch (Exception e) {
						match = false;
					}
				}
			}

			if (match) {
				data.add(car);
			}
		}

		// sort
		if (sortField != null) {
			Collections.sort(data, new Comparator<CarEntity>() {

				public int compare(CarEntity car1, CarEntity car2) {
					try {
						Field field = CarEntity.class
								.getDeclaredField(sortField);
						field.setAccessible(true);

						Object value1 = field.get(car1);
						Object value2 = field.get(car2);

						int value = ((Comparable<Object>) value1)
								.compareTo(value2);

						return SortOrder.ASCENDING.equals(sortOrder) ? value
								: -1 * value;
					} catch (Exception e) {
						throw new RuntimeException();
					}
				}

			});

		}

		// rowCount
		int dataSize = data.size();
		this.setRowCount(dataSize);

		// paginate
		if (dataSize > pageSize) {
			try {
				return data.subList(first, first + pageSize);
			} catch (IndexOutOfBoundsException e) {
				return data.subList(first, first + (dataSize % pageSize));
			}
		} else {
			return data;
		}
	}

}