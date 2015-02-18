package br.com.cams7.sisbarc.aal.vo;

public class LED {
	private Byte pino;
	private EstadoLED estado;

	public LED() {
		super();
	}

	/**
	 * /**
	 * 
	 * @return the pin
	 */
	public Byte getPino() {
		return pino;
	}

	/**
	 * @param pino
	 *            the pin to set
	 */
	public void setPino(Byte pino) {
		this.pino = pino;
	}

	public EstadoLED getEstado() {
		return estado;
	}

	public void setEstado(EstadoLED estado) {
		this.estado = estado;
	}

	public enum EstadoLED {
		ACESO, APAGADO
	}

}