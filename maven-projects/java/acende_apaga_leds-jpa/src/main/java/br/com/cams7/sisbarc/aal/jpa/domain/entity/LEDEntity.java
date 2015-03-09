/**
 * 
 */
package br.com.cams7.sisbarc.aal.jpa.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
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
@NamedQueries({
		@NamedQuery(name = "Led.findAll", query = "SELECT led FROM LEDEntity led"),
		@NamedQuery(name = "Led.buscaLEDsAtivadoPorBotao", query = "SELECT led FROM LEDEntity led WHERE led.ativadoPorBotao=true") })
public class LEDEntity extends Pin {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "cor_led")
	private CorLED cor;

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
		AMARELO, // LED Amarela
		VERDE, // LED Verde
		VERMELHO; // LED Vermelha
	}

	public enum EstadoLED {
		ACESO, // Acende
		APAGADO;// Apaga
	}

}
