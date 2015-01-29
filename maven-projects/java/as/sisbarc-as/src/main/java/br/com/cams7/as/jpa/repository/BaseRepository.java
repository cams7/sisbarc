/**
 * 
 */
package br.com.cams7.as.jpa.repository;

import java.io.Serializable;

import br.com.cams7.jpa.domain.BaseEntity;

/**
 * @author cesar
 *
 */
public interface BaseRepository<E extends BaseEntity<ID>, ID extends Serializable> {

	public E save(E entity);

	public E findOne(ID id);

	public Iterable<E> findAll();

	public long count();

	public void delete(E entity);

	public Class<E> getEntityType();
}
