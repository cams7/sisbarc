package br.com.cams7.sisbarc.aal.vo;

public class Led {
	private Color color;
	private Status status;

	public Led() {
		super();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public enum Color {
		YELLOW, GREEN, RED;
	}

	public enum Status {
		ON, OFF
	}

}