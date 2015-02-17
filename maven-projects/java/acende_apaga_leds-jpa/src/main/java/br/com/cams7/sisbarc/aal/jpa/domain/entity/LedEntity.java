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
import br.com.cams7.sisbarc.arduino.status.Arduino.ArduinoPinType;

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
	private LedColor color;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "evento_led")
	private LedEvent event;

	@Column(name = "altera_evento", nullable = false)
	private boolean changeEvent;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "evento_intervalo")
	private LedEventTime eventInterval;

	@Column(name = "altera_intervalo", nullable = false)
	private boolean changeEventInterval;

	@Column(name = "led_ativa", nullable = false)
	private boolean active;

	@Column(name = "ativada_por_botao", nullable = false)
	private boolean activeByButton;

	@Transient
	private LedStatus status;

	public LedEntity() {
		super();
	}

	public LedEntity(ArduinoPinType pinType, Short pin) {
		super(pinType, pin);
	}

	public LedColor getColor() {
		return color;
	}

	public void setColor(LedColor color) {
		this.color = color;
	}

	public LedEvent getEvent() {
		return event;
	}

	public void setEvent(LedEvent event) {
		this.event = event;
	}

	/**
	 * @return the changeEvent
	 */
	public boolean isChangeEvent() {
		return changeEvent;
	}

	/**
	 * @param changeEvent
	 *            the changeEvent to set
	 */
	public void setChangeEvent(boolean changeEvent) {
		this.changeEvent = changeEvent;
	}

	public LedEventTime getEventInterval() {
		return eventInterval;
	}

	public void setEventInterval(LedEventTime eventInterval) {
		this.eventInterval = eventInterval;
	}

	/**
	 * @return the changeEventInterval
	 */
	public boolean isChangeEventInterval() {
		return changeEventInterval;
	}

	/**
	 * @param changeEventInterval
	 *            the changeEventInterval to set
	 */
	public void setChangeEventInterval(boolean changeEventInterval) {
		this.changeEventInterval = changeEventInterval;
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

	public LedStatus getStatus() {
		return status;
	}

	public void setStatus(LedStatus status) {
		this.status = status;
	}

	public enum LedColor {
		YELLOW((byte) 0x0B), // LED Amarela - Pin 11
		GREEN((byte) 0x0A), // LED Verde - Pin 10
		RED((byte) 0x09);// LED Vermelha - Pin 09

		private byte pin;

		private LedColor(byte pin) {
			this.pin = pin;
		}

		public byte getPin() {
			return pin;
		}
	}

	public enum LedStatus {
		ON, // Acende
		OFF;// Apaga
	}

	public enum LedEvent {
		ON_OFF, // Acende ou apaga
		BLINK, // Pisca-pisca
		FADE;// Acende ao poucos
	}

	public enum LedEventTime {
		THREAD_INTERVAL_100MILLIS, // 1/10 de segundo
		THREAD_INTERVAL_250MILLIS, // 1/4 de segundo
		THREAD_INTERVAL_500MILLIS, // 1/2 de segundo
		THREAD_INTERVAL_1SECOUND, // 1 segundo
		THREAD_INTERVAL_2SECOUNDS, // 2 segundos
		THREAD_INTERVAL_3SECOUNDS, // 3 segundos
		THREAD_INTERVAL_5SECOUNDS, // 5 segundos
		NO_THREAD_INTERVAL; // Evento não definido
	}
}
