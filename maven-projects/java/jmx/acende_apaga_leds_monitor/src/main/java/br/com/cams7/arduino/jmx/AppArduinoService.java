/**
 * 
 */
package br.com.cams7.arduino.jmx;

import br.com.cams7.arduino.Arduino;
import br.com.cams7.arduino.ArduinoException;

/**
 * @author cesar
 *
 */
public class AppArduinoService extends Arduino implements
		AppArduinoServiceMBean {

	private final int LED_VERDE_APAGADA = 10;
	private final int LED_VERDE_ACESA = 11;

	private final int LED_AMARELA_APAGADA = 12;
	private final int LED_AMARELA_ACESA = 13;

	private final int LED_VERMELHA_APAGADA = 14;
	private final int LED_VERMELHA_ACESA = 15;

	private final int BTN_LED_VERDE_SOLTO = 20;
	private final int BTN_LED_VERDE_PRESSIONADO = 21;

	private final int BTN_LED_AMARELA_SOLTO = 22;
	private final int BTN_LED_AMARELA_PRESSIONADO = 23;

	private final int BTN_LED_VERMELHA_SOLTO = 24;
	private final int BTN_LED_VERMELHA_PRESSIONADO = 25;

	private final int MIN_POTENCIOMETRO = 100;
	private final int MAX_POTENCIOMETRO = 200;

	private final byte EVENTO_MUDA_STATUS_LED_AMARELA = EVENTO_NAO_INFORMADO + 1;
	private final byte EVENTO_MUDA_STATUS_LED_VERDE = EVENTO_MUDA_STATUS_LED_AMARELA + 1;
	private final byte EVENTO_MUDA_STATUS_LED_VERMELHA = EVENTO_MUDA_STATUS_LED_VERDE + 1;

	private boolean ledAmarelaLigada = false;
	private boolean ledVerdeLigada = false;
	private boolean ledVermelhaLigada = false;

	public AppArduinoService() throws ArduinoException {
		super();
		System.out.println("Novo Servico");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.embarcado.arduino.jmx.ArduinoServiceMBean#mudaStatusLED()
	 */
	public void mudaStatusLEDAmarela() {
		try {
			if (ledAmarelaLigada)
				serialWrite(LED_AMARELA_APAGADA);
			else
				serialWrite(LED_AMARELA_ACESA);
		} catch (ArduinoException e) {
			e.printStackTrace();
		}

		setEventoAtual(EVENTO_MUDA_STATUS_LED_AMARELA);
	}

	private void printStatusLEDArmarela(boolean ledAmarelaLigada) {
		System.out.println(ledAmarelaLigada ? "LED Amarela acesa"
				: "LED Amarela apagada");
	}

	private void mudaStatusLEDArmarelaOK() {
		ledAmarelaLigada = !ledAmarelaLigada;

		printStatusLEDArmarela(ledAmarelaLigada);
	}

	private void printStatusErrorLEDArmarela() {
		System.err
				.println("Ocorreu um erro ao tentar acender ou apagar o LED Amarelo");
	}

	public void mudaStatusLEDVerde() {
		try {
			if (ledVerdeLigada)
				serialWrite(LED_VERDE_APAGADA);
			else
				serialWrite(LED_VERDE_ACESA);
		} catch (ArduinoException e) {
			e.printStackTrace();
		}

		setEventoAtual(EVENTO_MUDA_STATUS_LED_VERDE);
	}

	private void printStatusLEDVerde(boolean ledVerdeLigada) {
		System.out.println(ledVerdeLigada ? "LED Verde acesa"
				: "LED Verde apagada");
	}

	private void mudaStatusLEDVerdeOK() {
		ledVerdeLigada = !ledVerdeLigada;
		printStatusLEDVerde(ledVerdeLigada);
	}

	private void printStatusErrorLEDVerde() {
		System.err
				.println("Ocorreu um erro ao tentar acender ou apagar o LED Verde");
	}

	public void mudaStatusLEDVermelha() {
		try {
			if (ledVermelhaLigada)
				serialWrite(LED_VERMELHA_APAGADA);
			else
				serialWrite(LED_VERMELHA_ACESA);
		} catch (ArduinoException e) {
			e.printStackTrace();
		}

		setEventoAtual(EVENTO_MUDA_STATUS_LED_VERMELHA);
	}

	private void printStatusLEDVermelha(boolean ledVermelha) {
		System.out.println(ledVermelha ? "LED Vermelha acesa"
				: "LED Vermelha apagada");
	}

	private void mudaStatusLEDVermelhaOK() {
		ledVermelhaLigada = !ledVermelhaLigada;
		printStatusLEDVermelha(ledVermelhaLigada);
	}

	private void printStatusErrorLEDVermelha() {
		System.err
				.println("Ocorreu um erro ao tentar acender ou apagar o LED Vermelha");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.arduino.Arduino#envioOK()
	 */
	protected void envioOK() {
		switch (getEventoAtual()) {
		case EVENTO_NAO_INFORMADO:
			break;
		case EVENTO_MUDA_STATUS_LED_AMARELA:
			mudaStatusLEDArmarelaOK();
			break;
		case EVENTO_MUDA_STATUS_LED_VERDE:
			mudaStatusLEDVerdeOK();
			break;
		case EVENTO_MUDA_STATUS_LED_VERMELHA:
			mudaStatusLEDVermelhaOK();
			break;

		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.arduino.Arduino#envioError()
	 */
	protected void envioError() {
		switch (getEventoAtual()) {
		case EVENTO_NAO_INFORMADO:
			break;
		case EVENTO_MUDA_STATUS_LED_AMARELA:
			printStatusErrorLEDArmarela();
			break;
		case EVENTO_MUDA_STATUS_LED_VERDE:
			printStatusErrorLEDVerde();
			break;
		case EVENTO_MUDA_STATUS_LED_VERMELHA:
			printStatusErrorLEDVermelha();
			break;
		default:
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.arduino.Arduino#setDadoRecebido(int)
	 */
	protected void setDadoRecebido(int dadoRecebido) {
		if (dadoRecebido >= MIN_POTENCIOMETRO
				&& dadoRecebido <= MAX_POTENCIOMETRO)
			System.out.println("Valor atual do potenciometro '"
					+ (dadoRecebido - 100) + "%'");
		else
			switch (dadoRecebido) {
			case BTN_LED_AMARELA_SOLTO:
				ledAmarelaLigada = false;
				printStatusLEDArmarela(ledAmarelaLigada);
				break;
			case BTN_LED_AMARELA_PRESSIONADO:
				ledAmarelaLigada = true;
				printStatusLEDArmarela(ledAmarelaLigada);
				break;
			case BTN_LED_VERDE_SOLTO:
				ledVerdeLigada = false;
				printStatusLEDVerde(ledVerdeLigada);
				break;
			case BTN_LED_VERDE_PRESSIONADO:
				ledVerdeLigada = true;
				printStatusLEDVerde(ledVerdeLigada);
				break;
			case BTN_LED_VERMELHA_SOLTO:
				ledVermelhaLigada = false;
				printStatusLEDVermelha(ledVermelhaLigada);
				break;
			case BTN_LED_VERMELHA_PRESSIONADO:
				ledVermelhaLigada = true;
				printStatusLEDVermelha(ledVermelhaLigada);
				break;
			default:
				System.err
						.println("O dado '" + dadoRecebido + "' nao e valido");
				break;
			}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.arduino.Arduino#recebimentoError()
	 */
	protected void recebimentoError() {
		System.out
				.println("O prototipo no Proteus nao esta rodando ou a porta '"
						+ getSerialPort() + "' esta fechada");

	}

	public boolean isLedAmarelaLigada() {
		return ledAmarelaLigada;
	}

	public boolean isLedVerdeLigada() {
		return ledVerdeLigada;
	}

	protected boolean isLedVermelhaLigada() {
		return ledVermelhaLigada;
	}

}
