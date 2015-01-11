package br.com.cams7.sisbarc.aal.vo;

public class Led {
	private Cor cor;
	private Status status;

	public Led() {
		super();
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