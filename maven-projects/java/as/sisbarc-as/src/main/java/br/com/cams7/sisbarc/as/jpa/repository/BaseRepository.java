/**
 * 
 */
package br.com.cams7.sisbarc.as.jpa.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.SingularAttribute;

import br.com.cams7.sisbarc.jpa.domain.BaseEntity;
import br.com.cams7.sisbarc.jpa.domain.SortOrderField;

/**
 * @author cesar
 *
 */
public interface BaseRepository<E extends BaseEntity<ID>, ID extends Serializable> {

	public E save(E entity);

	public E findOne(ID id);

	public List<E> findAll();

	public long count(
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>... joins);

	public long count(
			Map<String, String> filters,
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>... joins);

	public void remove(E entity);

	public void remove(ID id);

	public List<E> search(
			short first,
			byte pageSize,
			String sortField,
			SortOrderField sortOrder,
			Map<String, String> filters,
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>... joins);

	public Class<E> getEntityType();

}
