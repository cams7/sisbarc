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
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.CorLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EstadoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EventoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.IntervaloLED;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.arduino.ArduinoException;
import br.com.cams7.sisbarc.arduino.ArduinoServiceImpl;
import br.com.cams7.sisbarc.arduino.vo.Arduino;
import br.com.cams7.sisbarc.arduino.vo.Arduino.ArduinoEvent;
import br.com.cams7.sisbarc.arduino.vo.Arduino.ArduinoStatus;
import br.com.cams7.sisbarc.arduino.vo.ArduinoEEPROM;
import br.com.cams7.sisbarc.arduino.vo.ArduinoPin.ArduinoPinType;
import br.com.cams7.sisbarc.arduino.vo.ArduinoUSART;

/**
 * @author cesar
 *
 */
public class AppArduinoService extends ArduinoServiceImpl implements
		AppArduinoServiceMBean {

	private static final Logger LOG = Logger.getLogger(AppArduinoService.class
			.getName());

	private final byte PIN_LED_PISCA = 13; // Pino 13 Digital

	private final byte PIN_LED_AMARELO = 11; // Pino 11 PWM
	private final byte PIN_LED_VERDE = 10; // Pino 10 PWM
	private final byte PIN_LED_VERMELHO = 9; // Pino 09 PWM

	// private final byte PIN_BOTAO_LED_AMARELO = 12; // Pino 12 Digital
	// private final byte PIN_BOTAO_LED_VERDE = 8; // Pino 8 Digital
	// private final byte PIN_BOTAO_LED_VERMELHO = 7; // Pino 7 Digital

	private final byte PIN_POTENCIOMETRO = 0; // Pino 0 Analogico

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
			case PIN_LED_AMARELO:
			case PIN_LED_VERDE:
			case PIN_LED_VERMELHO: {
				LOG.info("EXECUTE -> LED ("
						+ pin
						+ "): "
						+ (pinValue == 0x0001 ? EstadoLED.ACESO
								: EstadoLED.APAGADO));
				break;
			}
			case PIN_LED_PISCA: {
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
			case PIN_POTENCIOMETRO: {
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
			case PIN_LED_AMARELO:
			case PIN_LED_VERDE:
			case PIN_LED_VERMELHO:
			case PIN_LED_PISCA: {
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
			case PIN_POTENCIOMETRO: {
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
			byte threadInterval, byte actionEvent) {
		switch (pinType) {
		case DIGITAL: {
			switch (pin) {
			case PIN_LED_AMARELO:
			case PIN_LED_VERDE:
			case PIN_LED_VERMELHO:
			case PIN_LED_PISCA: {
				LOG.info("WRITE -> LED (" + pin + "): threadInterval = "
						+ threadInterval + ", actionEvent = " + actionEvent);
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
				LOG.info("WRITE -> Potenciometro (" + pin
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
			case PIN_LED_AMARELO:
			case PIN_LED_VERDE:
			case PIN_LED_VERMELHO:
			case PIN_LED_PISCA: {
				LOG.info("READ -> LED (" + pin + "): threadInterval = "
						+ threadInterval + ", actionEvent = " + actionEvent);
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
				LOG.info("READ -> Potenciometro (" + pin
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
			case PIN_LED_AMARELO:
			case PIN_LED_VERDE:
			case PIN_LED_VERMELHO: {
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
			LOG.log(Level.SEVERE, e.getMessage());
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

		// LOG.info("getSituacaoLED ('" + pinPK.getPinType() + "',"
		// + pinPK.getPin() + ") = [transmitter = "
		// + arduino.getTransmitter() + ", status = "
		// + arduino.getStatus() + ", event = " + arduino.getEvent()
		// + ", pinType = " + arduino.getPinType() + ", pin = "
		// + arduino.getPin() + ", pinValue = "
		// + ((ArduinoUSART) arduino).getPinValue() + "]");

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
	public void alteraEventoLED(PinPK pino, EventoLED evento,
			IntervaloLED intervalo) {
		ArduinoPinType tipoPino = pino.getPinType();
		byte pinoLED = pino.getPin().byteValue();

		byte indiceEvento = (byte) evento.ordinal();
		byte indiceIntervalo = (byte) intervalo.ordinal();

		try {
			if (tipoPino == ArduinoPinType.DIGITAL)
				sendDigitalEEPROMWrite(ArduinoStatus.SEND_RESPONSE, pinoLED,
						indiceIntervalo, indiceEvento);
		} catch (ArduinoException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

	}

	@Override
	public EventoLED getEventoLED(PinPK pino) {
		Arduino arduino = getArduinoResponse(Arduino.ArduinoEvent.WRITE, pino);
		if (arduino == null)
			return null;

		// LOG.info("getEventoLED ('" + pinPK.getPinType() + "'," +
		// pinPK.getPin()
		// + ") = [transmitter = " + arduino.getTransmitter()
		// + ", status = " + arduino.getStatus() + ", event = "
		// + arduino.getEvent() + ", pinType = " + arduino.getPinType()
		// + ", pin = " + arduino.getPin() + ", threadInterval = "
		// + ((ArduinoEEPROM) arduino).getThreadInterval()
		// + ", actionEvent = "
		// + ((ArduinoEEPROM) arduino).getActionEvent() + "]");

		byte eventoLED = ((ArduinoEEPROM) arduino).getActionEvent();
		EventoLED evento = null;

		switch (eventoLED) {
		case 0x00:
			evento = EventoLED.ACENDE_APAGA;
			break;
		case 0x01:
			evento = EventoLED.PISCA_PISCA;
			break;
		case 0x02:
			evento = EventoLED.FADE;
			break;
		default:
			break;
		}

		return evento;
	}

	private short acendeOuApagaLEDPorBotao(byte pinoLED, short estadoPino) {

		if (estadoPino == 0x0000)
			return estadoPino;

		estadoPino = 0x0000;

		try {
			AppWildflyService service = lookupAppWildflyService();

			for (CorLED cor : CorLED.values()) {
				if (cor.getPin() == pinoLED) {
					// Verifica permissão para ACENDE o LED
					EstadoLED estado = service.getEstadoLEDAtivadoPorBotao(cor);
					LOG.info("Status: " + estado);

					if (estado != null && estado == EstadoLED.ACESO)
						estadoPino = (short) 0x0001;
					break;
				}
			}

		} catch (NamingException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}

		return estadoPino;
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
