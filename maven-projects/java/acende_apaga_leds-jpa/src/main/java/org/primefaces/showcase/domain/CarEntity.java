/**
 * 
 */
package org.primefaces.showcase.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.cams7.jpa.domain.BaseEntity;

/**
 * @author cams7
 *
 */
@XmlRootElement
@Entity
@Table(name = "carro")
@NamedQuery(name = "Car.findAll", query = "SELECT car FROM CarEntity car")
public class CarEntity extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "carro_seq", sequenceName = "carro_seq", initialValue = INITIAL_VALUE, allocationSize = ALLOCATION_SIZE)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carro_seq")
	@Column(name = "id_carro")
	private Integer id;

	@NotNull
	@Column(name = "ano")
	private Short year;// Ano

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "cor")
	private Color color;// Cor

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_marca", referencedColumnName = "id_marca")
	private CarBrandEntity brand;// Marca

	@Column(name = "preco")
	private Float price;// Preço

	@Column(name = "vendido", nullable = false)
	private boolean sold;// Vendido

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_venda", nullable = true)
	private Date saleDate;

	public CarEntity() {
		super();
	}

	public CarEntity(Integer id) {
		this();

		this.id = id;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[id = " + getId()
				+ ", year = " + getYear() + ", color = " + getColor()
				+ ", brand = " + getBrand() + ", price = " + getPrice()
				+ ", sold = " + isSold() + ", saleDate = " + getSaleDate()
				+ "]";
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the year
	 */
	public Short getYear() {
		return year;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(Short year) {
		this.year = year;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the brand
	 */
	public CarBrandEntity getBrand() {
		return brand;
	}

	/**
	 * @param brand
	 *            the brand to set
	 */
	public void setBrand(CarBrandEntity brand) {
		this.brand = brand;
	}

	/**
	 * @return the price
	 */
	public Float getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(Float price) {
		this.price = price;
	}

	/**
	 * @return the sold
	 */
	public boolean isSold() {
		return sold;
	}

	/**
	 * @param sold
	 *            the sold to set
	 */
	public void setSold(boolean sold) {
		this.sold = sold;
	}

	/**
	 * @return the saleDate
	 */
	public Date getSaleDate() {
		return saleDate;
	}

	/**
	 * @param saleDate
	 *            the saleDate to set
	 */
	public void setSaleDate(Date saleDate) {
		this.saleDate = saleDate;
	}

	public enum Color {
		BLACK, // Preto
		WHITE, // Branco
		GREEN, // Verde
		RED, // Vermelho
		BLUE, // Azul
		ORANGE, // Laranja
		SILVER, // Prata
		YELLOW, // Amarelo
		BROWN, // Marron
		MAROON;// Castanho avermelhado
	}

}
