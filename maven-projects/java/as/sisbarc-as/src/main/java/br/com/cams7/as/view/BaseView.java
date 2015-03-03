/**
 * 
 */
package br.com.cams7.as.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.metamodel.SingularAttribute;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.cams7.as.AbstractBase;
import br.com.cams7.as.service.BaseService;
import br.com.cams7.jpa.domain.BaseEntity;
import br.com.cams7.jpa.domain.SortOrderField;
import br.com.cams7.util.AppUtil;

/**
 * @author cesar
 *
 */
public abstract class BaseView<S extends BaseService<E, ?>, E extends BaseEntity<?>>
		extends AbstractBase<E> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final byte ENTITY_ARGUMENT_NUMBER = 1;

	private LazyDataModel<E> lazyModel;

	private E selectedEntity;

	private final short PAGE_FIRST = 0;
	private final byte PAGE_SIZE = 10;

	private short lastPageFirst;
	private byte lastPageSize;

	private String lastSortField;
	private SortOrder lastSortOrder;

	private Map<String, Object> lastFilters;

	private SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>[] joins;

	@SuppressWarnings("unchecked")
	public BaseView(
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>... joins) {
		super();
		this.joins = joins;
	}

	@PostConstruct
	public void initView() {
		lastPageFirst = PAGE_FIRST;
		lastPageSize = PAGE_SIZE;

		lastSortOrder = SortOrder.UNSORTED;

		lazyModel = new LazyDataModel<E>() {

			private static final long serialVersionUID = 1L;

			private List<E> entities;

			@Override
			public E getRowData(String rowKey) {
				for (E entity : entities) {
					if (String.valueOf(entity.getId()).equals(rowKey))
						return entity;
				}

				return null;
			}

			@Override
			public Object getRowKey(E entity) {
				return String.valueOf(entity.getId());
			}

			@Override
			public List<E> load(int first, int pageSize, String sortField,
					SortOrder sortOrder, Map<String, Object> filters) {

				boolean rowCountChanged = false;

				if (pageSize != lastPageSize) {
					lastPageSize = (byte) pageSize;
				} else if ((first == 0 || first == lastPageFirst)) {
					if (sortField != null
							&& (!sortField.equals(lastSortField) || !sortOrder
									.equals(lastSortOrder))) {
						lastSortField = sortField;
						lastSortOrder = sortOrder;
					} else if (((!filters.isEmpty() || lastFilters != null) && !AppUtil
							.equalMaps(filters, lastFilters))) {

						if (!filters.isEmpty())
							lastFilters = filters;
						else
							lastFilters = null;

						rowCountChanged = true;
					}
				}

				lastPageFirst = (short) first;

				entities = getService().search(lastPageFirst, lastPageSize,
						lastSortField,
						SortOrderField.valueOf(lastSortOrder.name()),
						lastFilters, joins);

				// rowCount
				if (rowCountChanged)
					setRowCount((int) getService().count(lastFilters, joins));

				return entities;
			}
		};

		lazyModel.setRowCount((int) getService().count(joins));

		init();
	}

	protected abstract void init();

	public void onRowSelect(SelectEvent event) {
		@SuppressWarnings("unchecked")
		E selectedEntity = (E) event.getObject();
		setSelectedEntity(selectedEntity);

		getLog().info("selectedEntity: " + selectedEntity);
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Entity Selected", String.valueOf(selectedEntity.getId()));
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cams7.apps.jee.AbstractBase#getEntityArgumentNumber()
	 */
	@Override
	protected byte getEntityArgumentNumber() {
		return ENTITY_ARGUMENT_NUMBER;
	}

	protected abstract S getService();

	public LazyDataModel<E> getLazyModel() {
		return lazyModel;
	}

	public E getSelectedEntity() {
		return selectedEntity;
	}

	public void setSelectedEntity(E selectedEntity) {
		this.selectedEntity = selectedEntity;
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
