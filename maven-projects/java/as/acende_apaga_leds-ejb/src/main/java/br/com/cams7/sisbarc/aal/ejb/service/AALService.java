/**
 * 
 */
package br.com.cams7.sisbarc.aal.ejb.service;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

import br.com.cams7.as.service.BaseService;
import br.com.cams7.jpa.domain.BaseEntity;

/**
 * @author cams7
 *
 */
public interface AALService<E extends BaseEntity<ID>, ID extends Serializable>
		extends BaseService<E, ID> {

	public Future<Boolean> atualizaPino(E entidade);

	public Future<Boolean> sincronizaEventos(List<E> entidades);

	public Future<Boolean> alteraEventos(List<E> entidades);

}
