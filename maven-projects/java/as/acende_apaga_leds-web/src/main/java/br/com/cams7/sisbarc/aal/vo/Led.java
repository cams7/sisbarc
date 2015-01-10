package br.com.cams7.sisbarc.aal.vo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Led {
	private String cor;
	private boolean acesa;

	public Led() {
		super();
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public boolean isAcesa() {
		return acesa;
	}

	public void setAcesa(boolean acesa) {
		this.acesa = acesa;
	}

}
