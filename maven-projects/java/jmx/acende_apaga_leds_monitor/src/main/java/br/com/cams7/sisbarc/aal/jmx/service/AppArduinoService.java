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

import br.com.cams7.sisbarc.aal.ejb.service.AppWildflyService;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.LedEvent;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.LedEventTime;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.LedStatus;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.arduino.ArduinoException;
import br.com.cams7.sisbarc.arduino.ArduinoServiceImpl;
import br.com.cams7.sisbarc.arduino.status.Arduino;
import br.com.cams7.sisbarc.arduino.status.Arduino.ArduinoPinType;
import br.com.cams7.sisbarc.arduino.status.Arduino.ArduinoStatus;
import br.com.cams7.sisbarc.arduino.status.ArduinoUSART;

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
	protected void receiveExecute(ArduinoPinType pinType, byte pin,
			short pinValue) {
		switch (pinType) {
		case DIGITAL: {
			switch (pin) {
			case PIN_LED_YELLOW:
			case PIN_LED_GREEN:
			case PIN_LED_RED: {
				LOG.info("EXECUTE -> LED (" + pin + "): "
						+ (pinValue == 0x0001 ? LedStatus.ON : LedStatus.OFF));
				break;
			}
			case PIN_LED_BLINK: {
				// LOG.info("USART -> LED Pisca (" + pin + "): " + (pinValue ==
				// 0x0001 ? LedStatus.ON : LedStatus.OFF));
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
				LOG.info("EXECUTE -> Potenciometro (" + pin + "): " + pinValue);
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
	protected void receiveMessage(ArduinoPinType pinType, byte pin,
			short pinValue) {
		switch (pinType) {
		case DIGITAL: {
			switch (pin) {
			case PIN_LED_YELLOW:
			case PIN_LED_GREEN:
			case PIN_LED_RED:
			case PIN_LED_BLINK: {
				LOG.info("MESSAGE -> LED (" + pin + "): " + pinValue);
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
				LOG.info("MESSAGE -> Potenciometro (" + pin + "): " + pinValue);
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
	protected void receiveWrite(ArduinoPinType pinType, byte pin,
			byte threadTime, byte actionEvent) {
		switch (pinType) {
		case DIGITAL: {
			switch (pin) {
			case PIN_LED_YELLOW:
			case PIN_LED_GREEN:
			case PIN_LED_RED:
			case PIN_LED_BLINK: {
				LOG.info("WRITE -> LED (" + pin + "): threadTime = "
						+ threadTime + ", actionEvent = " + actionEvent);
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
				LOG.info("WRITE -> Potenciometro (" + pin + "): threadTime = "
						+ threadTime + ", actionEvent = " + actionEvent);
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
	protected void receiveRead(ArduinoPinType pinType, byte pin,
			byte threadTime, byte actionEvent) {
		switch (pinType) {
		case DIGITAL: {
			switch (pin) {
			case PIN_LED_YELLOW:
			case PIN_LED_GREEN:
			case PIN_LED_RED:
			case PIN_LED_BLINK: {
				LOG.info("READ -> LED (" + pin + "): threadTime = "
						+ threadTime + ", actionEvent = " + actionEvent);
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
				LOG.info("READ -> Potenciometro (" + pin + "): threadTime = "
						+ threadTime + ", actionEvent = " + actionEvent);
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
	protected short sendResponse(ArduinoPinType pinType, byte pin,
			short pinValue) {
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
	public void changeStatusLED(PinPK pin, LedStatus status) {

		ArduinoPinType ledPinType = pin.getPinType();
		byte ledPin = pin.getPin().byteValue();

		boolean ledPinValue = (status == LedStatus.ON);

		try {
			if (ledPinType == ArduinoPinType.DIGITAL)
				sendPinDigital(ArduinoStatus.SEND_RESPONSE, ledPin, ledPinValue);
		} catch (ArduinoException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

	}

	@Override
	public LedStatus getStatusLED(PinPK pin) {
		ArduinoPinType ledPinType = pin.getPinType();
		byte ledPin = pin.getPin().byteValue();

		String key = getKeyCurrentStatus(Arduino.ArduinoEvent.EXECUTE,
				ledPinType, ledPin);

		if (getCurrentStatus().isEmpty()
				|| !getCurrentStatus().containsKey(key))
			return null;

		Arduino arduino = getCurrentStatus().get(key);

		if (arduino.getTransmitter() != Arduino.ArduinoTransmitter.ARDUINO)
			return null;

		if (arduino.getStatus() != ArduinoStatus.RESPONSE)
			return null;

		short pinValue = ((ArduinoUSART) arduino).getPinValue();
		LedStatus ledStatus = null;

		switch (pinValue) {
		case 0x0000:
			ledStatus = LedStatus.OFF;
			break;
		case 0x0001:
			ledStatus = LedStatus.ON;
			break;
		default:
			break;
		}

		return ledStatus;
	}

	@Override
	public void changeEventLED(PinPK pin, LedEvent event, LedEventTime eventTime) {
		ArduinoPinType ledPinType = pin.getPinType();
		byte ledPin = pin.getPin().byteValue();

		byte actionEvent = (byte) event.ordinal();
		byte threadTime = (byte) eventTime.ordinal();

		try {
			if (ledPinType == ArduinoPinType.DIGITAL)
				sendDigitalEEPROMWrite(ArduinoStatus.SEND_RESPONSE, ledPin,
						threadTime, actionEvent);
		} catch (ArduinoException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

	}

	@Override
	public LedEvent getEventLED(PinPK pin) {
		// TODO Auto-generated method stub
		return null;
	}

	private short turnsOnOffLEDByButton(byte pin, short pinValue) {

		if (pinValue == 0x0000)
			return pinValue;

		pinValue = 0x0000;

		try {
			AppWildflyService service = lookupAppWildflyService();

			for (LedEntity.LedColor color : LedEntity.LedColor.values()) {
				if (color.getPin() == pin) {
					// Verifica permissão para ACENDE o LED
					LedStatus status = service.getStatusActiveLED(color);
					LOG.info("Status: " + status);

					if (status != null && status == LedStatus.ON)
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
