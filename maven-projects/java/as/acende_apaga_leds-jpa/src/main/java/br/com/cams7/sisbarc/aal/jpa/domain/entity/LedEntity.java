/**
 * 
 */
package br.com.cams7.sisbarc.aal.jpa.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.cams7.apps.jpa.domain.BaseEntity;

/**
 * @author cams7
 *
 */
@XmlRootElement
@Entity
@Table(name = "led")
@NamedQuery(name = "Led.findAll", query = "SELECT led FROM LedEntity led")
public class LedEntity extends BaseEntity<Byte> {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "led_seq", sequenceName = "led_seq", initialValue = INITIAL_VALUE, allocationSize = ALLOCATION_SIZE)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "led_seq")
	@Column(name = "id_led")
	private Byte id;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "cor_led")
	private Cor cor;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "status_led")
	private Status status;

	public LedEntity() {
		super();
	}

	public Byte getId() {
		return id;
	}

	public void setId(Byte id) {
		this.id = id;
	}

	public Cor getCor() {
		return cor;
	}

	public void setCor(Cor cor) {
		this.cor = cor;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public enum Cor {
		AMARELA, VERDE, VERMELHA;
	}

	public enum Status {
		ACESA, APAGADA
	}

}
