/**
 * 
 */
package org.primefaces.showcase.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

import br.com.cams7.jpa.domain.BaseEntity;

/**
 * @author cams7
 *
 */
@XmlRootElement
@Entity
@Table(name = "uf")
@NamedQuery(name = "State.findAll", query = "SELECT state FROM  StateEntity state")
public class StateEntity extends BaseEntity<Short> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "uf_seq", sequenceName = "uf_seq", initialValue = INITIAL_VALUE, allocationSize = ALLOCATION_SIZE)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "uf_seq")
	@Column(name = "id_uf")
	private Short id;

	@NotEmpty
	@Size(min = 3, max = 30)
	@Column(name = "nome_uf")
	private String name;// UF

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_pais", referencedColumnName = "id_pais")
	private CountryEntity country;// Pais

	@OneToMany(mappedBy = "state")
	private List<CityEntity> cities;

	/*
	 * 
	 */
	public StateEntity() {
		super();
	}

	public StateEntity(Short id) {
		this();

		this.id = id;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[id = " + getId()
				+ ", name = " + getName() + ", country = " + getCountry() + "]";
	}

	/**
	 * @return the id
	 */
	public Short getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Short id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the country
	 */
	public CountryEntity getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(CountryEntity country) {
		this.country = country;
	}

	/**
	 * @return the cities
	 */
	public List<CityEntity> getCities() {
		return cities;
	}

	/**
	 * @param cities
	 *            the cities to set
	 */
	public void setCities(List<CityEntity> cities) {
		this.cities = cities;
	}

}
