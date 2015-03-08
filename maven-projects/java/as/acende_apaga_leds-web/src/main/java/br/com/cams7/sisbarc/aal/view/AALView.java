/**
 * 
 */
package br.com.cams7.sisbarc.aal.view;

import javax.persistence.metamodel.SingularAttribute;

import br.com.cams7.as.service.BaseService;
import br.com.cams7.as.view.BaseView;
import br.com.cams7.jpa.domain.BaseEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Evento;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Intervalo;

/**
 * @author cams7
 *
 */
public abstract class AALView<S extends BaseService<E, ?>, E extends BaseEntity<?>>
		extends BaseView<S, E> {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public AALView(
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>... joins) {
		super(joins);
	}

	public Evento[] getEventos() {
		return Evento.values();
	}

	public Intervalo[] getIntervalos() {
		return Intervalo.values();
	}

}
