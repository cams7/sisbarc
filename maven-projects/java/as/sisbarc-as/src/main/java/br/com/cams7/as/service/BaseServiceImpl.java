/**
 * 
 */
package br.com.cams7.as.service;

import java.io.Serializable;

import br.com.cams7.as.jpa.repository.BaseRepositoryImpl;
import br.com.cams7.jpa.domain.BaseEntity;

/**
 * @author cesar
 *
 */
public abstract class BaseServiceImpl<E extends BaseEntity<ID>, ID extends Serializable>
		extends BaseRepositoryImpl<E, ID> implements BaseService<E, ID> {

	public BaseServiceImpl() {
		super();
	}

}
