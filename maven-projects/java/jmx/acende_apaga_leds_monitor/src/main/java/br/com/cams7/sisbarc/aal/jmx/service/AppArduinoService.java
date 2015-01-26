/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.arduino.ArduinoException;
import br.com.cams7.arduino.ArduinoServiceImpl;
import br.com.cams7.arduino.util.ArduinoStatus;
import br.com.cams7.arduino.util.ArduinoStatus.PinType;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity;

/**
 * @author cesar
 *
 */
public class AppArduinoService extends ArduinoServiceImpl implements
		AppArduinoServiceMBean {

	private final byte PIN_LED_PISCA = 13; // Pino 13 Digital

	private final byte PIN_LED_AMARELA = 11; // Pino 11 PWM
	private final byte PIN_LED_VERDE = 10; // Pino 10 PWM
	private final byte PIN_LED_VERMELHA = 9; // Pino 09 PWM

	// private final byte PIN_BOTAO_LED_AMARELA = 12; // Pino 12 Digital
	// private final byte PIN_BOTAO_LED_VERDE = 8; // Pino 8 Digital
	// private final byte PIN_BOTAO_LED_VERMELHA = 7; // Pino 7 Digital

	private final byte PIN_POTENCIOMETRO = 0; // Pino 0 Analogico

	public AppArduinoService(String serialPort, int baudRate, long threadTime)
			throws ArduinoException {
		super(serialPort, baudRate, threadTime);
		System.out.println("Novo Servico");
	}

	@Override
	protected void receive(PinType pinType, byte pin, short pinValue) {
		switch (pinType) {
		case DIGITAL: {
			switch (pin) {
			case PIN_LED_AMARELA:
			case PIN_LED_VERDE:
			case PIN_LED_VERMELHA: {
				System.out.println("LED ("
						+ pin
						+ "): "
						+ (pinValue == 0x0001 ? LedEntity.Status.ACESA
								: LedEntity.Status.APAGADA));
				break;
			}
			case PIN_LED_PISCA: {
				System.out.println("LED Pisca ("
						+ pin
						+ "): "
						+ (pinValue == 0x0001 ? LedEntity.Status.ACESA
								: LedEntity.Status.APAGADA));
				break;
			}
			default:
				break;
			}
			break;
		}
		case ANALOG: {
			switch (pin) {
			case PIN_POTENCIOMETRO: {
				System.out.println("Potenciometro (" + pin + "): " + pinValue);
				break;
			}
			default:
				break;
			}
			break;
		}
		default:
			break;
		}

	}

	@Override
	protected short sendResponse(PinType pinType, byte pin, short pinValue) {
		switch (pinType) {
		case DIGITAL: {
			switch (pin) {
			case PIN_LED_AMARELA:
			case PIN_LED_VERDE:
			case PIN_LED_VERMELHA: {
				pinValue = acendeApagaLEDPorBotao(pin, pinValue);
				break;
			}
			default:
				break;
			}
			break;
		}
		case ANALOG: {
			break;
		}
		default:
			break;
		}
		return pinValue;
	}

	@Override
	public void mudaStatusLED(LedEntity.Cor cor, LedEntity.Status status) {

		byte pin = cor.getPin();

		boolean pinValue = (status == LedEntity.Status.ACESA);

		try {
			sendPinDigital(pin, pinValue, ArduinoStatus.Status.SEND_RESPONSE);
			// sendPinPWM(pin, (short) (pinValue ? 255 :
			// 0),Status.RESPONSE_RESPONSE);
			// sendPinAnalog(pin, (short) (pinValue ? 1023 : 0),
			// Status.RESPONSE);

		} catch (ArduinoException e) {
			e.printStackTrace();
		}

	}

	@Override
	public LedEntity.Status getStatusLED(LedEntity.Cor cor) {
		String key = getKeyCurrentStatus(ArduinoStatus.PinType.DIGITAL,
				cor.getPin());

		if (getCurrentStatus().isEmpty()
				|| !getCurrentStatus().containsKey(key))
			return null;

		ArduinoStatus arduino = getCurrentStatus().get(key);

		if (arduino.getTransmitter() != ArduinoStatus.Transmitter.ARDUINO)
			return null;

		if (arduino.getStatus() != ArduinoStatus.Status.RESPONSE)
			return null;

		short pinValue = arduino.getPinValue();
		LedEntity.Status ledStatus = null;

		switch (pinValue) {
		case 0x0000:
			ledStatus = LedEntity.Status.APAGADA;
			break;
		case 0x0001:
			ledStatus = LedEntity.Status.ACESA;
			break;
		default:
			break;
		}

		return ledStatus;
	}

	private short acendeApagaLEDPorBotao(byte pin, short pinValue) {
		if (pinValue == 0x0001) {
			// Verifica permissão para ACENDE ou APAGA o LED
			return pinValue;
		}
		return pinValue;
	}
}
