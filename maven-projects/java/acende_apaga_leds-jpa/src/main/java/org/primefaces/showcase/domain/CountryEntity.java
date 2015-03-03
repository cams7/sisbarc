/**
 * 
 */
package org.primefaces.showcase.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
@Table(name = "pais")
@NamedQuery(name = "Country.findAll", query = "SELECT country FROM CountryEntity country")
public class CountryEntity extends BaseEntity<Short> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "pais_seq", sequenceName = "pais_seq", initialValue = INITIAL_VALUE, allocationSize = ALLOCATION_SIZE)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pais_seq")
	@Column(name = "id_pais")
	private Short id;

	@NotEmpty
	@Size(min = 3, max = 30)
	@Column(name = "nome_pais")
	private String name;// Nome

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "continente")
	private Continent continent;

	@OneToMany(mappedBy = "country")
	private List<StateEntity> states;

	/**
	 * 
	 */
	public CountryEntity() {
		super();
	}

	public CountryEntity(Short id) {
		this();

		this.id = id;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[id = " + getId()
				+ ", name = " + getName() + ", continent = " + getContinent()
				+ "]";
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
	 * @return the continent
	 */
	public Continent getContinent() {
		return continent;
	}

	/**
	 * @param continent
	 *            the continent to set
	 */
	public void setContinent(Continent continent) {
		this.continent = continent;
	}

	/**
	 * @return the states
	 */
	public List<StateEntity> getStates() {
		return states;
	}

	/**
	 * @param states
	 *            the states to set
	 */
	public void setStates(List<StateEntity> states) {
		this.states = states;
	}

	public enum Continent {
		AFRICA, // África
		AMERICA, // América
		ASIA, // Ásia
		OCEANIA, // Oceania
		EUROPE, // Europa
		ANTARCTICA// Antártida
	}

}
