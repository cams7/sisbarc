package br.com.cams7.sisbarc.aal.vo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Led {
	private Cor cor;
	private Boolean acesa;

	public Led() {
		super();
	}

	public Cor getCor() {
		return cor;
	}

	public void setCor(Cor cor) {
		this.cor = cor;
	}

	public Boolean getAcesa() {
		return acesa;
	}

	public void setAcesa(Boolean acesa) {
		this.acesa = acesa;
	}

	public enum Cor {
		AMARELA, VERDE, VERMELHA;
	}

}
