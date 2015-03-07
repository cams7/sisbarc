/**
 * 
 */
package br.com.cams7.as.view;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.persistence.metamodel.SingularAttribute;

import org.primefaces.event.CloseEvent;
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
	private final String RESOURCE_BUNDLE = "messages";

	private final byte ENTITY_ARGUMENT_NUMBER = 1;

	private LazyDataModel<E> lazyModel;

	private E selectedEntity;

	private final short PAGE_FIRST = 0;
	private final byte PAGE_SIZE = 10;

	private int totalRows;

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

				filters = AppUtil.removeEmptyArray(filters);
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

		totalRows = (int) getService().count(joins);
		lazyModel.setRowCount(totalRows);

		init();
	}

	protected abstract void init();

	public void onRowSelect(SelectEvent event) {
		@SuppressWarnings("unchecked")
		E selectedEntity = (E) event.getObject();
		setSelectedEntity(selectedEntity);

		getLog().info("entity selected: " + getSelectedEntity());
	}

	public void handleClose(CloseEvent event) {
		setSelectedEntity(null);
	}

	private void addMessage(Severity severity, String summary, String detail) {
		FacesMessage message = new FacesMessage(severity, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	protected void addINFOMessage(String summary, String detail) {
		addMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		getLog().info(detail);
	}

	protected void addWARNMessage(String summary, String detail) {
		addMessage(FacesMessage.SEVERITY_WARN, summary, detail);
		getLog().log(Level.WARNING, detail);
	}

	protected void addERRORMessage(String summary, String detail) {
		addMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
		getLog().log(Level.SEVERE, detail);
	}

	protected void addMessageMonitorNotRun(String detail) {
		String summary = getMessageFromI18N("error.msg.monitor.not.run");// Resumo
		addWARNMessage(summary, detail);
	}

	protected void addMessageArduinoNotRun(String detail) {
		String summary = getMessageFromI18N("error.msg.arduino.not.run");// Resumo
		addWARNMessage(summary, detail);
	}

	/**
	 * @param key
	 * @return Recupera a mensagem do arquivo properties
	 *         <code>ResourceBundle</code>.
	 */
	protected String getMessageFromI18N(String key, Object... params) {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale();

		ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE,
				locale, this.getClass().getClassLoader());

		String message;
		if (params.length > 0)
			message = MessageFormat.format(bundle.getString(key), params);
		else
			message = bundle.getString(key);

		return message;
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

	/**
	 * @return the rowCount
	 */
	public int getTotalRows() {
		return totalRows;
	}

}
