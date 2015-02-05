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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.cams7.sisbarc.jpa.domain.BaseEntity;

/**
 * @author cams7
 *
 */
@XmlRootElement
@Entity
@Table(name = "led")
@NamedQuery(name = "Led.findAll", query = "SELECT led FROM LedEntity led")
public class LedEntity extends BaseEntity<Short> {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "led_seq", sequenceName = "led_seq", initialValue = INITIAL_VALUE, allocationSize = ALLOCATION_SIZE)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "led_seq")
	@Column(name = "id_led")
	private Short id;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "cor_led")
	private Color color;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "evento_led")
	private Event event;

	@Column(name = "led_ativa", nullable = false)
	private boolean active;

	@Column(name = "ativada_por_botao", nullable = false)
	private boolean activeByButton;

	@Transient
	private Status status;

	public LedEntity() {
		super();
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActiveByButton() {
		return activeByButton;
	}

	public void setActiveByButton(boolean activeByButton) {
		this.activeByButton = activeByButton;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public enum Color {
		YELLOW((byte) 0x0B), // Pin 11
		GREEN((byte) 0x0A), // Pin 10
		RED((byte) 0x09);// Pin 09

		private byte pin;

		private Color(byte pin) {
			this.pin = pin;
		}

		public byte getPin() {
			return pin;
		}
	}

	public enum Event {
		ON_OFF, BLINK, FADE
	}

	public enum Status {
		ON, OFF
	}

}
