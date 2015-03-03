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
@Table(name = "cidade")
@NamedQuery(name = "City.findAll", query = "SELECT city FROM  CityEntity city")
public class CityEntity extends BaseEntity<Short> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "cidade_seq", sequenceName = "cidade_seq", initialValue = INITIAL_VALUE, allocationSize = ALLOCATION_SIZE)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cidade_seq")
	@Column(name = "id_cidade")
	private Short id;

	@NotEmpty
	@Size(min = 3, max = 30)
	@Column(name = "nome_cidade")
	private String name;// Cidade

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_uf", referencedColumnName = "id_uf")
	private StateEntity state;// UF

	@OneToMany(mappedBy = "city")
	private List<CarBrandEntity> brands;

	/**
	 * 
	 */
	public CityEntity() {
		super();
	}

	public CityEntity(Short id) {
		this();

		this.id = id;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[id = " + getId()
				+ ", name = " + getName() + ", state = " + getState() + "]";
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
	 * @return the state
	 */
	public StateEntity getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(StateEntity state) {
		this.state = state;
	}

	/**
	 * @return the brands
	 */
	public List<CarBrandEntity> getBrands() {
		return brands;
	}

	/**
	 * @param brands
	 *            the brands to set
	 */
	public void setBrands(List<CarBrandEntity> brands) {
		this.brands = brands;
	}

}
