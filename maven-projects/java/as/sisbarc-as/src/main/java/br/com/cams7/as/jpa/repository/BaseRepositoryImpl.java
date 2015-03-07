package br.com.cams7.as.jpa.repository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import br.com.cams7.as.AbstractBase;
import br.com.cams7.jpa.domain.BaseEntity;
import br.com.cams7.jpa.domain.SortOrderField;
import br.com.cams7.util.AppUtil;

/**
 * Classe resolve os métodos básicos de cadastro (CRUD) com API da
 * <code>JPA</code>.
 * 
 * @author YaW Tecnologia
 */
public abstract class BaseRepositoryImpl<E extends BaseEntity<ID>, ID extends Serializable>
		extends AbstractBase<E> implements BaseRepository<E, ID> {

	/**
	 * Classe da entidade, necessário para o método
	 * <code>EntityManager.find</code>.
	 */
	private Class<E> entityType;

	@SuppressWarnings("unchecked")
	public BaseRepositoryImpl() {
		super();
		entityType = (Class<E>) AppUtil
				.getType(this, getEntityArgumentNumber());
	}

	public E save(E entity) {
		if (entity.getId() != null)
			return getEntityManager().merge(entity);

		getEntityManager().persist(entity);
		return entity;
	}

	public void update(List<E> entities) {
		for (E entity : entities)
			getEntityManager().merge(entity);
	}

	public void remove(E entity) {
		getEntityManager().remove(getEntityManager().merge(entity));
	}

	public void remove(ID id) {
		getEntityManager().remove(findOne(id));
	}

	public E findOne(ID id) {
		E entity = getEntityManager().find(getEntityType(), id);
		return entity;
	}

	public List<E> findAll() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<E> cq = cb.createQuery(getEntityType());

		Root<E> from = cq.from(getEntityType());
		cq.select(from);

		TypedQuery<E> query = getEntityManager().createQuery(cq);

		List<E> entities = query.getResultList();
		return entities;
	}

	public List<E> findRange(int[] range) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<E> cq = cb.createQuery(getEntityType());

		Root<E> from = cq.from(getEntityType());
		cq.select(from);

		TypedQuery<E> query = getEntityManager().createQuery(cq);
		query.setMaxResults(range[1] - range[0]);
		query.setFirstResult(range[0]);

		List<E> entities = query.getResultList();
		return entities;
	}

	private Path<?> getPath(Root<E> from, String field,
			Map<String, From<?, ?>> joins) {
		String[] fields = field.split("\\.");

		Path<?> path;
		if (fields.length > 1) {
			// [brand, city, state, country, name]
			// *.*.*.*.country

			if (joins.isEmpty())
				return null;

			String key = "*";
			for (byte i = 0; i < fields.length - 2; i++)
				key += ".*";
			key += "." + fields[fields.length - 2];

			From<?, ?> join = joins.get(key);

			field = fields[fields.length - 1];
			path = join.get(field);
		} else
			path = from.get(fields[0]);
		return path;
	}

	private void addOrderBy(CriteriaBuilder cb, CriteriaQuery<?> cq,
			Root<E> from, String sortField, SortOrderField sortOrder,
			Map<String, From<?, ?>> joins) {
		// Sorting
		if (sortField != null && !sortField.isEmpty()) {
			Path<?> path = getPath(from, sortField, joins);

			if (path != null)
				switch (sortOrder) {
				case ASCENDING:
					cq.orderBy(cb.asc(path));
					break;
				case DESCENDING:
					cq.orderBy(cb.desc(path));
					break;
				default:
					break;
				}
		}
	}

	private Predicate getEqual(CriteriaBuilder cb, Expression<Date> expression,
			String functionName, Short value) {
		if (value == null)
			return null;

		Expression<Short> function = cb.function(functionName, Short.class,
				expression);
		return cb.equal(function, value);
	}

	@SuppressWarnings("unchecked")
	private void addWhere(CriteriaBuilder cb, CriteriaQuery<?> cq,
			Root<E> from, Map<String, Object> filters,
			Map<String, From<?, ?>> joins) {
		// Filtering
		if (filters == null || filters.isEmpty())
			return;

		List<Predicate> and = new ArrayList<Predicate>();
		for (Map.Entry<String, Object> filter : filters.entrySet()) {
			Object value = filter.getValue();

			if (value == null)
				continue;

			Object[] values = null;

			if (value instanceof Object[]) {
				if (((Object[]) value).length > 0)
					values = (Object[]) value;
			} else if (!(value instanceof String)
					|| !((String) value).isEmpty()) {
				values = new Object[1];
				values[0] = value;
			}

			if (values != null) {
				Path<?> path = getPath(from, filter.getKey(), joins);

				if (path != null) {
					Class<?> type = path.getJavaType();

					final String WILD_CARD = "%";

					Predicate predicate = null;

					if (AppUtil.isBoolean(type)) {
						List<Predicate> or = new ArrayList<Predicate>();
						for (Object objectValue : values) {
							Boolean booleanValue = (Boolean) objectValue;

							Expression<Boolean> expression = (Expression<Boolean>) path;
							or.add((booleanValue == Boolean.TRUE) ? cb
									.isTrue(expression) : cb
									.isFalse(expression));

						}
						if (!or.isEmpty())
							predicate = cb.or(or.toArray(new Predicate[0]));
						else
							predicate = cb.isNull(path);
					} else if (AppUtil.isEnum(type)) {
						List<Predicate> or = new ArrayList<Predicate>();
						for (Object objectValue : values)
							or.add(cb.equal(path, objectValue));

						if (!or.isEmpty())
							predicate = cb.or(or.toArray(new Predicate[0]));
						else
							predicate = cb.isNull(path);
					} else if (AppUtil.isDate(type)) {
						List<Predicate> or = new ArrayList<Predicate>();
						for (Object objectValue : values) {
							String stringValue = (String) objectValue;
							Map<Integer, Short>[] between = AppUtil
									.getDate(stringValue);
							if (between != null) {
								Expression<Date> expression = (Expression<Date>) path;

								if (between.length == 2) {
									Calendar[] dates = AppUtil.getDate(between);
									Date date1 = dates[0].getTime();
									Date date2 = dates[1].getTime();

									or.add(cb.between(expression, date1, date2));

									final DateFormat DF = new SimpleDateFormat(
											"dd/MM/yyyy HH:mm:ss");
									getLog().info(
											"from: " + DF.format(date1)
													+ ", to: "
													+ DF.format(date2));
								} else {
									List<Predicate> andDateValue = new ArrayList<Predicate>();

									Predicate equal = getEqual(cb, expression,
											"year",
											between[0].get(Calendar.YEAR));

									if (equal != null)
										andDateValue.add(equal);

									equal = getEqual(cb, expression, "month",
											between[0].get(Calendar.MONTH));

									if (equal != null)
										andDateValue.add(equal);

									equal = getEqual(cb, expression, "day",
											between[0]
													.get(Calendar.DAY_OF_MONTH));

									if (equal != null)
										andDateValue.add(equal);

									equal = getEqual(cb, expression, "hour",
											between[0]
													.get(Calendar.HOUR_OF_DAY));

									if (equal != null)
										andDateValue.add(equal);

									equal = getEqual(cb, expression, "minute",
											between[0].get(Calendar.MINUTE));

									if (equal != null)
										andDateValue.add(equal);

									equal = getEqual(cb, expression, "second",
											between[0].get(Calendar.SECOND));

									if (equal != null)
										andDateValue.add(equal);

									or.add(cb.and(andDateValue
											.toArray(new Predicate[0])));
								}
							}
						}
						if (!or.isEmpty())
							predicate = cb.or(or.toArray(new Predicate[0]));
						else
							predicate = cb.isNull(path);
					} else {
						List<Predicate> or = new ArrayList<Predicate>();
						for (Object objectValue : values) {

							Expression<?> expression = (Expression<?>) path;

							Expression<String> stringExpression;

							String stringValue;
							if (type.equals(String.class)) {
								stringValue = ((String) objectValue)
										.toLowerCase() + WILD_CARD;

								stringExpression = (Expression<String>) expression;

								or.add(cb.like(cb.lower(stringExpression),
										stringValue));
							} else if (AppUtil.isNumber(type)) {
								stringValue = ((Number) objectValue)
										+ WILD_CARD;

								stringExpression = (Expression<String>) expression
										.as(String.class);

								or.add(cb.like(stringExpression, stringValue));
							}

						}
						if (!or.isEmpty())
							predicate = cb.or(or.toArray(new Predicate[0]));
						else
							predicate = cb.isNull(path);

					}

					if (predicate != null)
						and.add(predicate);

				}

			}
		}

		cq.where(and.toArray(new Predicate[0]));
	}

	private Map<String, From<?, ?>> addJoins(
			boolean isFetchJoin,
			Map<String, From<?, ?>> froms,
			List<SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>> joins,
			String prefix) {

		if (joins == null || joins.isEmpty())
			return null;

		Map<String, From<?, ?>> newFroms = new HashMap<String, From<?, ?>>();

		for (String key : froms.keySet()) {
			From<?, ?> from = froms.get(key);

			@SuppressWarnings("unchecked")
			Class<BaseEntity<?>> fromType = (Class<BaseEntity<?>>) from
					.getJavaType();

			Iterator<SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>> i = joins
					.iterator();

			while (i.hasNext()) {
				SingularAttribute<?, ?> join = i.next();

				String joinName = join.getName();
				@SuppressWarnings("unchecked")
				Class<BaseEntity<?>> joinType = (Class<BaseEntity<?>>) join
						.getJavaType();

				for (Field field : fromType.getDeclaredFields()) {
					String fieldName = field.getName();
					@SuppressWarnings("unchecked")
					Class<BaseEntity<?>> fieldType = (Class<BaseEntity<?>>) field
							.getType();

					if (fieldName.equals(joinName)
							&& fieldType.equals(joinType)) {
						from = (From<?, ?>) (isFetchJoin ? from.fetch(
								fieldName, JoinType.INNER) : from.join(
								fieldName, JoinType.INNER));

						newFroms.put(prefix + "." + fieldName, from);

						i.remove();
						break;
					}
				}
			}
		}

		froms = addJoins(isFetchJoin, newFroms, joins, prefix + ".*");

		if (froms != null)
			newFroms.putAll(froms);

		return newFroms;
	}

	private Map<String, From<?, ?>> addFetchJoins(
			Root<E> from,
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>[] joins) {

		Map<String, From<?, ?>> froms = new HashMap<String, From<?, ?>>();
		froms.put("*", from);

		List<SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>> list = null;

		if (joins.length > 0) {
			list = new ArrayList<SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>>();
			for (SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>> join : joins)
				list.add(join);
		}

		return addJoins(true, froms, list, "*");
	}

	private Map<String, From<?, ?>> addJoins(
			Root<E> from,
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>[] joins) {

		Map<String, From<?, ?>> froms = new HashMap<String, From<?, ?>>();
		froms.put("*", from);

		List<SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>> list = null;

		if (joins.length > 0) {
			list = new ArrayList<SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>>();
			for (SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>> join : joins)
				list.add(join);
		}

		return addJoins(false, froms, list, "*");
	}

	@SuppressWarnings("unchecked")
	public List<E> search(
			short first,
			byte pageSize,
			String sortField,
			SortOrderField sortOrder,
			Map<String, Object> filters,
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>... joins) {

		getLog().info(
				"search(" + first + ", " + pageSize + ", " + sortField + ", "
						+ sortOrder + ", " + filters + ")\n");

		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<E> cq = cb.createQuery(getEntityType());

		Root<E> from = cq.from(getEntityType());
		CriteriaQuery<E> select = cq.select(from);

		// Fetch join
		Map<String, From<?, ?>> froms = addFetchJoins(from, joins);

		// Sorting
		addOrderBy(cb, cq, from, sortField, sortOrder, froms);

		// Filtering
		addWhere(cb, cq, from, filters, froms);

		TypedQuery<E> query = getEntityManager().createQuery(select);

		query.setFirstResult(first);
		query.setMaxResults(pageSize);

		List<E> entities = query.getResultList();
		return entities;
	}

	@SuppressWarnings("unchecked")
	public long count(
			Map<String, Object> filters,
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>... joins) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);

		Root<E> from = cq.from(getEntityType());
		cq.select(cb.count(from));

		// Join
		Map<String, From<?, ?>> froms = addJoins(from, joins);

		// Filtering
		addWhere(cb, cq, from, filters, froms);

		TypedQuery<Long> query = getEntityManager().createQuery(cq);

		long count = query.getSingleResult();
		return count;
	}

	@SuppressWarnings("unchecked")
	public long count(
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>... joins) {
		long count = count(null, joins);
		return count;
	}

	/**
	 * Exige a definição do <code>EntityManager</code> responsável pelas
	 * operações de persistência.
	 */
	protected abstract EntityManager getEntityManager();

	protected Class<E> getEntityType() {
		return entityType;
	}

}
