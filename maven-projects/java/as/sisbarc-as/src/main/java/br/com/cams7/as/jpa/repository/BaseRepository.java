/**
 * 
 */
package br.com.cams7.as.jpa.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.SingularAttribute;

import br.com.cams7.jpa.domain.BaseEntity;
import br.com.cams7.jpa.domain.SortOrderField;

/**
 * @author cesar
 *
 */
public interface BaseRepository<E extends BaseEntity<ID>, ID extends Serializable> {

	public E save(E entity);

	public void update(List<E> entities);

	public E findOne(ID id);

	public List<E> findAll();

	@SuppressWarnings("unchecked")
	public long count(
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>... joins);

	@SuppressWarnings("unchecked")
	public long count(
			Map<String, Object> filters,
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>... joins);

	public void remove(E entity);

	public void remove(ID id);

	@SuppressWarnings("unchecked")
	public List<E> search(
			short first,
			byte pageSize,
			String sortField,
			SortOrderField sortOrder,
			Map<String, Object> filters,
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>... joins);

}
