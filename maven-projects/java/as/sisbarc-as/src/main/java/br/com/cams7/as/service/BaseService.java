/**
 * 
 */
package br.com.cams7.as.service;

import java.io.Serializable;

import br.com.cams7.as.jpa.repository.BaseRepository;
import br.com.cams7.jpa.domain.BaseEntity;

/**
 * @author cesar
 *
 */
public interface BaseService<E extends BaseEntity<ID>, ID extends Serializable>
		extends BaseRepository<E, ID> {

}
