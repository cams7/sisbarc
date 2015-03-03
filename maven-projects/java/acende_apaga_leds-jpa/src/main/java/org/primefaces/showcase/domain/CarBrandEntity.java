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
@Table(name = "carro_marca")
@NamedQuery(name = "CarBrand.findAll", query = "SELECT brand FROM CarBrandEntity brand")
public class CarBrandEntity extends BaseEntity<Short> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "marca_seq", sequenceName = "marca_seq", initialValue = INITIAL_VALUE, allocationSize = ALLOCATION_SIZE)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "marca_seq")
	@Column(name = "id_marca")
	private Short id;

	@NotEmpty
	@Size(min = 3, max = 30)
	@Column(name = "nome_marca")
	private String name;// Marca

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cidade_origem", referencedColumnName = "id_cidade")
	private CityEntity city;// Cidade

	@OneToMany(mappedBy = "brand")
	private List<CarEntity> cars;

	/**
	 * 
	 */
	public CarBrandEntity() {
		super();
	}

	public CarBrandEntity(Short id) {
		this();

		this.id = id;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[id = " + getId()
				+ ", name = " + getName() + ", city = " + getCity() + "]";
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
	 * @return the city
	 */
	public CityEntity getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(CityEntity city) {
		this.city = city;
	}

	/**
	 * @return the cars
	 */
	public List<CarEntity> getCars() {
		return cars;
	}

	/**
	 * @param cars
	 *            the cars to set
	 */
	public void setCars(List<CarEntity> cars) {
		this.cars = cars;
	}

}
