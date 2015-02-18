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
import br.com.cams7.sisbarc.arduino.vo.ArduinoPin.ArduinoPinType;

/**
 * @author cams7
 *
 */
@XmlRootElement
@Entity
@Table(name = "led")
@NamedQuery(name = "Led.findAll", query = "SELECT led FROM LEDEntity led")
public class LEDEntity extends Pin {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "cor_led")
	private CorLED cor;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "evento_led")
	private EventoLED evento;

	@Column(name = "altera_evento", nullable = false)
	private boolean alteraEvento;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "evento_intervalo")
	private IntervaloLED intervalo;

	@Column(name = "altera_intervalo", nullable = false)
	private boolean alteraIntervalo;

	@Column(name = "led_ativo", nullable = false)
	private boolean ativo;

	@Column(name = "ativado_por_botao", nullable = false)
	private boolean ativadoPorBotao;

	@Transient
	private EstadoLED estado;

	public LEDEntity() {
		super();
	}

	public LEDEntity(ArduinoPinType pinType, Short pin) {
		super(pinType, pin);
	}

	public CorLED getCor() {
		return cor;
	}

	public void setCor(CorLED cor) {
		this.cor = cor;
	}

	public EventoLED getEvento() {
		return evento;
	}

	public void setEvento(EventoLED evento) {
		this.evento = evento;
	}

	/**
	 * @return the changeEvent
	 */
	public boolean isAlteraEvento() {
		return alteraEvento;
	}

	/**
	 * @param alteraEvento
	 *            the changeEvent to set
	 */
	public void setAlteraEvento(boolean alteraEvento) {
		this.alteraEvento = alteraEvento;
	}

	public IntervaloLED getIntervalo() {
		return intervalo;
	}

	public void setIntervalo(IntervaloLED intervalo) {
		this.intervalo = intervalo;
	}

	/**
	 * @return the changeEventInterval
	 */
	public boolean isAlteraIntervalo() {
		return alteraIntervalo;
	}

	/**
	 * @param alteraIntervalo
	 *            the changeEventInterval to set
	 */
	public void setAlteraIntervalo(boolean alteraIntervalo) {
		this.alteraIntervalo = alteraIntervalo;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isAtivadoPorBotao() {
		return ativadoPorBotao;
	}

	public void setAtivadoPorBotao(boolean ativadoPorBotao) {
		this.ativadoPorBotao = ativadoPorBotao;
	}

	public EstadoLED getEstado() {
		return estado;
	}

	public void setEstado(EstadoLED estado) {
		this.estado = estado;
	}

	public enum CorLED {
		AMARELO((byte) 0x0B), // LED Amarela - Pin 11
		VERDE((byte) 0x0A), // LED Verde - Pin 10
		VERMELHO((byte) 0x09);// LED Vermelha - Pin 09

		private byte pin;

		private CorLED(byte pin) {
			this.pin = pin;
		}

		public byte getPin() {
			return pin;
		}
	}

	public enum EstadoLED {
		ACESO, // Acende
		APAGADO;// Apaga
	}

	public enum EventoLED {
		ACENDE_APAGA, // Acende ou apaga
		PISCA_PISCA, // Pisca-pisca
		FADE;// Acende ao poucos
	}

	public enum IntervaloLED {
		INTERVALO_100MILISEGUNDOS, // 1/10 de segundo
		INTERVALO_250MILISEGUNDOS, // 1/4 de segundo
		INTERVALO_500MILISEGUNDOS, // 1/2 de segundo
		INTERVALO_1SEGUNDO, // 1 segundo
		INTERVALO_2SEGUNDOS, // 2 segundos
		INTERVALO_3SEGUNDOS, // 3 segundos
		INTERVALO_5SEGUNDOS, // 5 segundos
		SEM_INTERVALO; // O evento sera apenas executado quando for chamado
	}

}
