/**
 * 
 */
package br.com.cams7.sisbarc.aal.jpa.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.cams7.sisbarc.aal.jpa.domain.Pin;

/**
 * @author cams7
 *
 */
@XmlRootElement
@Entity
@Table(name = "led")
@NamedQuery(name = "Led.findAll", query = "SELECT led FROM LedEntity led")
public class LedEntity extends Pin {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "cor_led")
	private Color color;

	// @NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "evento_led")
	private Event event;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "duracao_evento")
	private EventTime eventTime;

	@Column(name = "led_ativa", nullable = false)
	private boolean active;

	@Column(name = "ativada_por_botao", nullable = false)
	private boolean activeByButton;

	@Transient
	private Status status;

	public LedEntity() {
		super();
	}

	public LedEntity(PinType pinType, Short pin) {
		super(pinType, pin);
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

	public EventTime getEventTime() {
		return eventTime;
	}

	public void setEventTime(EventTime eventTime) {
		this.eventTime = eventTime;
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
		YELLOW((byte) 0x0B), // LED Amarela - Pin 11
		GREEN((byte) 0x0A), // LED Verde - Pin 10
		RED((byte) 0x09);// LED Vermelha - Pin 09

		private byte pin;

		private Color(byte pin) {
			this.pin = pin;
		}

		public byte getPin() {
			return pin;
		}
	}

	public enum Status {
		ON, // Acende
		OFF;// Apaga
	}

	public enum Event {
		ON_OFF, // Acende ou apaga
		BLINK, // Pisca-pisca
		FADE;// Acende ao poucos
	}

	public enum EventTime {
		TIME_100MILLIS, // 1/10 de segundo
		TIME_250MILLIS, // 1/4 de segundo
		TIME_500MILLIS, // 1/2 de segundo
		TIME_1SECOUND, // 1 segundo
		TIME_2SECOUNDS, // 2 segundos
		TIME_3SECOUNDS, // 3 segundos
		TIME_5SECOUNDS, // 5 segundos
		TIME_10SECOUNDS;// 10 segundos
	}
}
