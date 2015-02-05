/**
 * 
 */
package br.com.cams7.sisbarc.as.service;

import java.io.Serializable;

import br.com.cams7.sisbarc.as.jpa.repository.BaseRepository;
import br.com.cams7.sisbarc.jpa.domain.BaseEntity;

/**
 * @author cesar
 *
 */
public interface BaseService<E extends BaseEntity<ID>, ID extends Serializable>
		extends BaseRepository<E, ID> {

}
