/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.com.cams7.arduino.ArduinoException;
import br.com.cams7.arduino.ArduinoServiceImpl;
import br.com.cams7.arduino.util.ArduinoStatus;
import br.com.cams7.arduino.util.ArduinoStatus.PinType;
import br.com.cams7.sisbarc.aal.ejb.service.AppWildflyService;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity;

/**
 * @author cesar
 *
 */
public class AppArduinoService extends ArduinoServiceImpl implements
		AppArduinoServiceMBean {

	private static final Logger LOG = Logger.getLogger(AppArduinoService.class
			.getName());

	private final byte PIN_LED_BLINK = 13; // Pino 13 Digital

	private final byte PIN_LED_YELLOW = 11; // Pino 11 PWM
	private final byte PIN_LED_GREEN = 10; // Pino 10 PWM
	private final byte PIN_LED_RED = 9; // Pino 09 PWM

	// private final byte PIN_BOTAO_LED_AMARELA = 12; // Pino 12 Digital
	// private final byte PIN_BOTAO_LED_VERDE = 8; // Pino 8 Digital
	// private final byte PIN_BOTAO_LED_VERMELHA = 7; // Pino 7 Digital

	private final byte PIN_POTENTIOMETER = 0; // Pino 0 Analogico

	public AppArduinoService(String serialPort, int baudRate, long threadTime)
			throws ArduinoException {
		super(serialPort, baudRate, threadTime);
		LOG.info("Novo Servico");
	}

	@Override
	protected void receive(PinType pinType, byte pin, short pinValue) {
		switch (pinType) {
		case DIGITAL: {
			switch (pin) {
			case PIN_LED_YELLOW:
			case PIN_LED_GREEN:
			case PIN_LED_RED: {
				LOG.info("LED ("
						+ pin
						+ "): "
						+ (pinValue == 0x0001 ? LedEntity.Status.ON
								: LedEntity.Status.OFF));
				break;
			}
			case PIN_LED_BLINK: {
				// LOG.info("LED Pisca (" + pin + "): " + (pinValue == 0x0001 ?
				// LedEntity.Status.ON : LedEntity.Status.OFF));
				break;
			}
			default:
				break;
			}
			break;
		}
		case ANALOG: {
			switch (pin) {
			case PIN_POTENTIOMETER: {
				LOG.info("Potenciometro (" + pin + "): " + pinValue);
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
			case PIN_LED_YELLOW:
			case PIN_LED_GREEN:
			case PIN_LED_RED: {
				pinValue = turnsOnOffLEDByButton(pin, pinValue);
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
	public void changeStatusLED(LedEntity.Color color, LedEntity.Status status) {

		byte pin = color.getPin();

		boolean pinValue = (status == LedEntity.Status.ON);

		try {
			sendPinDigital(pin, pinValue, ArduinoStatus.Status.SEND_RESPONSE);
		} catch (ArduinoException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

	}

	@Override
	public LedEntity.Status getStatusLED(LedEntity.Color color) {
		String key = getKeyCurrentStatus(ArduinoStatus.PinType.DIGITAL,
				color.getPin());

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
			ledStatus = LedEntity.Status.OFF;
			break;
		case 0x0001:
			ledStatus = LedEntity.Status.ON;
			break;
		default:
			break;
		}

		return ledStatus;
	}

	private short turnsOnOffLEDByButton(byte pin, short pinValue) {

		if (pinValue == 0x0000)
			return pinValue;

		pinValue = 0x0000;

		try {
			AppWildflyService service = lookupAppWildflyService();

			for (LedEntity.Color color : LedEntity.Color.values()) {
				if (color.getPin() == pin) {
					// Verifica permissão para ACENDE o LED
					LedEntity.Status status = service.getStatusActiveLED(color);
					LOG.info("Status: " + status);

					if (status != null && status == LedEntity.Status.ON)
						pinValue = (short) 0x0001;
					break;
				}
			}

		} catch (NamingException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

		return pinValue;
	}

	private AppWildflyService lookupAppWildflyService() throws NamingException {
		final Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
		jndiProperties.put(Context.URL_PKG_PREFIXES,
				"org.jboss.ejb.client.naming");
		final Context context = new InitialContext(jndiProperties);

		return (AppWildflyService) context
				.lookup("ejb:sisbarc/acende_apaga_leds-ejb//ArduinoServiceImpl!"
						+ AppWildflyService.class.getName());

	}
}
