/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.arduino.ArduinoServiceImpl;
import br.com.cams7.arduino.ArduinoException;
import br.com.cams7.sisbarc.aal.jmx.service.AppArduinoServiceMBean;

/**
 * @author cesar
 *
 */
public class AppArduinoService extends ArduinoServiceImpl implements
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

	private boolean _ledAmarelaAcesa = false;
	private boolean _ledVerdeAcesa = false;
	private boolean _ledVermelhaAcesa = false;

	private boolean ledAmarelaAcesa = false;
	private boolean ledVerdeAcesa = false;
	private boolean ledVermelhaAcesa = false;

	public AppArduinoService(String serialPort, int baudRate, long threadTime)
			throws ArduinoException {
		super(serialPort, baudRate, threadTime);
		System.out.println("Novo Servico");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.sisbarc.aal.jmx.service.ArduinoServiceMBean#mudaStatusLEDAmarela
	 * ()
	 */
	public void mudaStatusLEDAmarela(boolean ledAcesa) {
		_ledAmarelaAcesa = ledAcesa;
		try {
			if (ledAcesa)
				serialWrite(LED_AMARELA_ACESA);
			else
				serialWrite(LED_AMARELA_APAGADA);

		} catch (ArduinoException e) {
			e.printStackTrace();
		}

		setEventoAtual(EVENTO_MUDA_STATUS_LED_AMARELA);
	}

	private void printStatusLEDArmarela(boolean ledAcesa) {
		System.out.println(ledAcesa ? "LED Amarela acesa"
				: "LED Amarela apagada");
	}

	private void mudaStatusLEDArmarelaOK() {
		ledAmarelaAcesa = _ledAmarelaAcesa;
		printStatusLEDArmarela(ledAmarelaAcesa);
	}

	private void printStatusErrorLEDArmarela() {
		System.err
				.println("Ocorreu um erro ao tentar acender ou apagar o LED Amarelo");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.sisbarc.aal.jmx.service.ArduinoServiceMBean#mudaStatusLEDVerde
	 * ()
	 */
	public void mudaStatusLEDVerde(boolean ledAcesa) {
		_ledVerdeAcesa = ledAcesa;
		try {
			if (ledAcesa)
				serialWrite(LED_VERDE_ACESA);
			else
				serialWrite(LED_VERDE_APAGADA);

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
		ledVerdeAcesa = _ledVerdeAcesa;
		printStatusLEDVerde(ledVerdeAcesa);
	}

	private void printStatusErrorLEDVerde() {
		System.err
				.println("Ocorreu um erro ao tentar acender ou apagar o LED Verde");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.sisbarc.aal.jmx.service.ArduinoServiceMBean#
	 * mudaStatusLEDVermelha()
	 */
	public void mudaStatusLEDVermelha(boolean ledAcesa) {
		_ledVermelhaAcesa = ledAcesa;
		try {
			if (ledAcesa)
				serialWrite(LED_VERMELHA_ACESA);
			else
				serialWrite(LED_VERMELHA_APAGADA);

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
		ledVermelhaAcesa = _ledVermelhaAcesa;
		printStatusLEDVermelha(ledVermelhaAcesa);
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
				ledAmarelaAcesa = false;
				printStatusLEDArmarela(ledAmarelaAcesa);
				break;
			case BTN_LED_AMARELA_PRESSIONADO:
				ledAmarelaAcesa = true;
				printStatusLEDArmarela(ledAmarelaAcesa);
				break;
			case BTN_LED_VERDE_SOLTO:
				ledVerdeAcesa = false;
				printStatusLEDVerde(ledVerdeAcesa);
				break;
			case BTN_LED_VERDE_PRESSIONADO:
				ledVerdeAcesa = true;
				printStatusLEDVerde(ledVerdeAcesa);
				break;
			case BTN_LED_VERMELHA_SOLTO:
				ledVermelhaAcesa = false;
				printStatusLEDVermelha(ledVermelhaAcesa);
				break;
			case BTN_LED_VERMELHA_PRESSIONADO:
				ledVermelhaAcesa = true;
				printStatusLEDVermelha(ledVermelhaAcesa);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.sisbarc.aal.jmx.service.ArduinoServiceMBean#isLedAmarelaLigada
	 * ()
	 */
	public boolean isLEDAmarelaAcesa() {
		return ledAmarelaAcesa;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.sisbarc.aal.jmx.service.ArduinoServiceMBean#isLedVerdeLigada
	 * ()
	 */
	public boolean isLEDVerdeAcesa() {
		return ledVerdeAcesa;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.sisbarc.aal.jmx.service.ArduinoServiceMBean#isLedVermelhaLigada
	 * ()
	 */
	public boolean isLEDVermelhaAcesa() {
		return ledVermelhaAcesa;
	}

}
