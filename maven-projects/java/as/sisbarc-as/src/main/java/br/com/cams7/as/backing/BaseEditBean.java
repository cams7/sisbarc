/**
 * 
 */
package br.com.cams7.as.backing;

import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import br.com.cams7.as.service.BaseService;
import br.com.cams7.jpa.domain.BaseEntity;
import br.com.cams7.util.AppException;
import br.com.cams7.util.AppUtil;

/**
 * @author cesar
 *
 */
public abstract class BaseEditBean<S extends BaseService<E, ?>, E extends BaseEntity<?>>
		extends AbstractBean<E> {

	// @Inject
	@EJB
	private S service;

	@Inject
	private Event<E> entityEventSrc;

	// @Inject
	// @NewEntity
	private E entity;

	// @Inject
	// @InjectFacesContext
	// private FacesContext facesContext;

	public BaseEditBean() {
		super();
	}

	public E getEntity() {
		return entity;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void initNewEntity() {
		Class<E> entityType = getService().getEntityType();

		try {
			entity = (E) AppUtil
					.getNewEntity((Class<BaseEntity<?>>) entityType);
		} catch (AppException e) {
			getLog().log(Level.SEVERE, e.getMessage());
		}
	}

	public String salva() {
		try {
			entity = getService().save(entity);

			getLog().info("New entity: id=" + entity.getId());

			getEntityEventSrc().fire(entity);

			initNewEntity();
		} catch (Exception ex) {
			// addErrorMessage("msg.erro.salvar.mercadoria", ex.getMessage());
			getLog().log(Level.SEVERE, "Erro ao salvar mercadoria.", ex);
			return "error";
		}
		return "ok";
	}

	protected S getService() {
		return service;
	}

	protected Event<E> getEntityEventSrc() {
		return entityEventSrc;
	}

}
