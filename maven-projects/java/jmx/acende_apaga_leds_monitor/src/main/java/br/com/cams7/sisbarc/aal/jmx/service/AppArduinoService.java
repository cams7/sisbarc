/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import java.util.Hashtable;
import java.util.logging.Level;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.com.cams7.sisbarc.aal.ejb.service.AppWildflyService;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Evento;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Intervalo;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EstadoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.arduino.ArduinoException;
import br.com.cams7.sisbarc.arduino.ArduinoServiceImpl;
import br.com.cams7.sisbarc.arduino.vo.Arduino;
import br.com.cams7.sisbarc.arduino.vo.Arduino.ArduinoEvent;
import br.com.cams7.sisbarc.arduino.vo.Arduino.ArduinoStatus;
import br.com.cams7.sisbarc.arduino.vo.ArduinoEEPROM;
import br.com.cams7.sisbarc.arduino.vo.ArduinoPin.ArduinoPinType;
import br.com.cams7.sisbarc.arduino.vo.ArduinoUSART;
import br.com.cams7.sisbarc.arduino.vo.EEPROMData;

/**
 * @author cesar
 *
 */
public class AppArduinoService extends ArduinoServiceImpl implements
		AppArduinoServiceMBean {

	private final byte D13_LED_PISCA = 13; // Pino 13 Digital

	private final byte D11_LED_AMARELO = 11; // Pino 11 PWM
	private final byte D10_LED_VERDE = 10; // Pino 10 PWM
	private final byte D09_LED_VERMELHO = 9; // Pino 09 PWM
	private final byte D06_LED_AMARELO = 6; // Pino 06 PWM
	private final byte D05_LED_VERDE = 5; // Pino 05 PWM
	private final byte D04_LED_VERMELHO = 4; // Pino 03 PWM

	private final byte A0_POTENCIOMETRO = 0; // Pino 0 Analogico

	public AppArduinoService(String serialPort, int baudRate, long threadTime)
			throws ArduinoException {
		super(serialPort, baudRate, threadTime);
		getLog().info("Novo Servico");
	}

	@Override
	protected void receiveExecute(ArduinoPinType pinType, byte pin,
			short pinValue) {
		switch (pinType) {
		case DIGITAL: {
			switch (pin) {
			case D11_LED_AMARELO:
			case D10_LED_VERDE:
			case D09_LED_VERMELHO:
			case D06_LED_AMARELO:
			case D05_LED_VERDE:
			case D04_LED_VERMELHO: {
				getLog().info(
						"EXECUTE -> LED ("
								+ pin
								+ "): "
								+ (pinValue == 0x0001 ? EstadoLED.ACESO
										: EstadoLED.APAGADO));
				break;
			}
			case D13_LED_PISCA: {
				// getLog().info("USART -> LED Pisca (" + pin + "): " +
				// (pinValue ==
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
			case A0_POTENCIOMETRO: {
				getLog().info(
						"EXECUTE -> Potenciometro (" + pin + "): " + pinValue);
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
			case D13_LED_PISCA:
			case D11_LED_AMARELO:
			case D10_LED_VERDE:
			case D09_LED_VERMELHO:
			case D06_LED_AMARELO:
			case D05_LED_VERDE:
			case D04_LED_VERMELHO: {
				getLog().info("MESSAGE -> LED (" + pin + "): " + pinValue);
				break;
			}
			default:
				break;
			}
			break;
		}
		case ANALOG: {
			switch (pin) {
			case A0_POTENCIOMETRO: {
				getLog().info(
						"MESSAGE -> Potenciometro (" + pin + "): " + pinValue);
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
			byte threadInterval, byte actionEvent) {
		switch (pinType) {
		case DIGITAL: {
			switch (pin) {
			case D13_LED_PISCA:
			case D11_LED_AMARELO:
			case D10_LED_VERDE:
			case D09_LED_VERMELHO:
			case D06_LED_AMARELO:
			case D05_LED_VERDE:
			case D04_LED_VERMELHO: {
				getLog().info(
						"WRITE -> LED (" + pin + "): threadInterval = "
								+ threadInterval + ", actionEvent = "
								+ actionEvent);
				break;
			}
			default:
				break;
			}
			break;
		}
		case ANALOG: {
			switch (pin) {
			case A0_POTENCIOMETRO: {
				getLog().info(
						"WRITE -> Potenciometro (" + pin
								+ "): threadInterval = " + threadInterval
								+ ", actionEvent = " + actionEvent);
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
			byte threadInterval, byte actionEvent) {
		switch (pinType) {
		case DIGITAL: {
			switch (pin) {
			case D13_LED_PISCA:
			case D11_LED_AMARELO:
			case D10_LED_VERDE:
			case D09_LED_VERMELHO:
			case D06_LED_AMARELO:
			case D05_LED_VERDE:
			case D04_LED_VERMELHO: {
				getLog().info(
						"READ -> LED (" + pin + "): threadInterval = "
								+ threadInterval + ", actionEvent = "
								+ actionEvent);
				break;
			}
			default:
				break;
			}
			break;
		}
		case ANALOG: {
			switch (pin) {
			case A0_POTENCIOMETRO: {
				getLog().info(
						"READ -> Potenciometro (" + pin
								+ "): threadInterval = " + threadInterval
								+ ", actionEvent = " + actionEvent);
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
			case D11_LED_AMARELO:
			case D10_LED_VERDE:
			case D09_LED_VERMELHO:
			case D06_LED_AMARELO:
			case D05_LED_VERDE:
			case D04_LED_VERMELHO: {
				pinValue = acendeOuApagaLEDPorBotao(pin, pinValue);
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
	public void alteraEstadoLED(PinPK pino, EstadoLED estado) {
		ArduinoPinType tipoPino = pino.getPinType();
		byte pinoLED = pino.getPin().byteValue();

		boolean estadoLED = (estado == EstadoLED.ACESO);

		try {
			if (tipoPino == ArduinoPinType.DIGITAL)
				sendPinDigital(ArduinoStatus.SEND_RESPONSE, pinoLED, estadoLED);
		} catch (ArduinoException e) {
			getLog().log(Level.SEVERE, e.getMessage());
		}

	}

	private Arduino getArduinoResponse(ArduinoEvent event, PinPK pinPK) {
		ArduinoPinType pinType = pinPK.getPinType();
		byte pin = pinPK.getPin().byteValue();

		String key = getKeyCurrentStatus(event, pinType, pin);

		if (getCurrentStatus().isEmpty()
				|| !getCurrentStatus().containsKey(key))
			return null;

		Arduino arduino = getCurrentStatus().get(key);

		if (arduino.getTransmitter() != Arduino.ArduinoTransmitter.ARDUINO)
			return null;

		if (arduino.getStatus() != ArduinoStatus.RESPONSE)
			return null;

		return arduino;
	}

	@Override
	public EstadoLED getEstadoLED(PinPK pino) {
		Arduino arduino = getArduinoResponse(Arduino.ArduinoEvent.EXECUTE, pino);
		if (arduino == null)
			return null;

		short estadoLED = ((ArduinoUSART) arduino).getPinValue();
		EstadoLED estado = null;
		switch (estadoLED) {
		case 0x0000:
			estado = EstadoLED.APAGADO;
			break;
		case 0x0001:
			estado = EstadoLED.ACESO;
			break;
		default:
			break;
		}
		return estado;
	}

	@Override
	public void alteraEventoLED(PinPK pino, Evento evento, Intervalo intervalo) {
		try {
			if (pino.getPinType() == ArduinoPinType.DIGITAL)
				sendDigitalEEPROMWrite(ArduinoStatus.SEND_RESPONSE, pino
						.getPin().byteValue(), (byte) intervalo.ordinal(),
						(byte) evento.ordinal());
		} catch (ArduinoException e) {
			getLog().log(Level.SEVERE, e.getMessage());
		}

	}

	@Override
	public void alteraEventoPotenciometro(PinPK pino, Evento evento,
			Intervalo intervalo) {
		try {
			if (pino.getPinType() == ArduinoPinType.ANALOG)
				sendAnalogEEPROMWrite(ArduinoStatus.SEND_RESPONSE, pino
						.getPin().byteValue(), (byte) intervalo.ordinal(),
						(byte) evento.ordinal());
		} catch (ArduinoException e) {
			getLog().log(Level.SEVERE, e.getMessage());
		}

	}

	@Override
	public Evento getEventoLED(PinPK pino) {
		Arduino arduino = getArduinoResponse(Arduino.ArduinoEvent.WRITE, pino);
		if (arduino == null)
			return null;

		return Evento.values()[((ArduinoEEPROM) arduino).getActionEvent()];
	}

	@Override
	public Evento getEventoPotenciometro(PinPK pino) {
		Arduino arduino = getArduinoResponse(Arduino.ArduinoEvent.WRITE, pino);
		if (arduino == null)
			return null;

		return Evento.values()[((ArduinoEEPROM) arduino).getActionEvent()];
	}

	@Override
	public void buscaDadosLED(PinPK pino) {
		try {
			if (pino.getPinType() == ArduinoPinType.DIGITAL)
				sendDigitalEEPROMRead(ArduinoStatus.SEND_RESPONSE, pino
						.getPin().byteValue());
		} catch (ArduinoException e) {
			getLog().log(Level.SEVERE, e.getMessage());
		}

	}

	@Override
	public void buscaDadosPotenciometro(PinPK pino) {
		try {
			if (pino.getPinType() == ArduinoPinType.ANALOG)
				sendAnalogEEPROMRead(ArduinoStatus.SEND_RESPONSE, pino.getPin()
						.byteValue());
		} catch (ArduinoException e) {
			getLog().log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	public EEPROMData getDados(PinPK pino) {
		Arduino arduino = getArduinoResponse(Arduino.ArduinoEvent.READ, pino);
		if (arduino == null)
			return null;

		return (EEPROMData) arduino;
	}

	private short acendeOuApagaLEDPorBotao(byte pinoLED, short estadoPino) {
		if (estadoPino == 0x0000)
			return estadoPino;

		estadoPino = 0x0000;

		try {
			AppWildflyService service = lookupAppWildflyService();

			EstadoLED estado = service.getEstadoLEDAtivadoPorBotao(pinoLED);

			if (estado != null && estado == EstadoLED.ACESO)
				estadoPino = (short) 0x0001;

		} catch (NamingException e) {
			getLog().log(Level.SEVERE, e.getMessage());
		}

		return estadoPino;
	}

	private AppWildflyService lookupAppWildflyService() throws NamingException {
		final Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
		jndiProperties.put(Context.URL_PKG_PREFIXES,
				"org.jboss.ejb.client.naming");
		final Context context = new InitialContext(jndiProperties);

		return (AppWildflyService) context
				.lookup("ejb:sisbarc/acende_apaga_leds-ejb//LEDServiceImpl!"
						+ AppWildflyService.class.getName());

	}

}
