package br.com.cams7.sisbarc.arduino;

import br.com.cams7.sisbarc.arduino.status.Arduino;
import br.com.cams7.sisbarc.arduino.status.Arduino.ArduinoEvent;
import br.com.cams7.sisbarc.arduino.status.Arduino.ArduinoPinType;
import br.com.cams7.sisbarc.arduino.status.Arduino.ArduinoStatus;
import br.com.cams7.sisbarc.arduino.status.Arduino.ArduinoTransmitter;
import br.com.cams7.sisbarc.arduino.status.ArduinoEEPROM;
import br.com.cams7.sisbarc.arduino.status.ArduinoEEPROMRead;
import br.com.cams7.sisbarc.arduino.status.ArduinoEEPROMWrite;
import br.com.cams7.sisbarc.arduino.status.ArduinoUSART;
import br.com.cams7.sisbarc.arduino.status.ArduinoUSARTMessage;
import br.com.cams7.sisbarc.arduino.util.Binary;
import br.com.cams7.sisbarc.arduino.util.Checksum;

public final class SisbarcProtocol {

	private static final int EMPTY_BITS = 0x00000000;

	// Total de BITs do protocolo - 32 bits
	private static final byte TOTAL_BITS_PROTOCOL = 0x20;
	// Total de BITs reservado para o INDICE - 4 bits
	private static final byte TOTAL_BITS_INDEX = 0x04;
	// Total de BITs reservado para o CRC - 8 bits
	private static final byte TOTAL_BITS_CHECKSUM = 0x08;

	// Total de BITs reservado para os DADOs - 20 bits
	private static final byte TOTAL_BITS_DATA = TOTAL_BITS_PROTOCOL
			- TOTAL_BITS_INDEX - TOTAL_BITS_CHECKSUM;

	// Total de BITs reservado para o PINO DIGITAL - 6 bits
	private static final byte TOTAL_BITS_DIGITAL_PIN = 0x06;
	// Total de BITs reservado para o PINO ANALOGICO - 4 bits
	private static final byte TOTAL_BITS_ANALOG_PIN = 0x04;

	// Total de BITs reservado para o VALOR do PINO DIGITAL - 8 bits
	private static final byte TOTAL_BITS_DIGITAL_PIN_VALUE = 0x08;
	// Total de BITs reservado para o VALOR do PINO ANALOGICO - 10 bits
	private static final byte TOTAL_BITS_ANALOG_PIN_VALUE = 0x0A;

	// Total de BITs reservado para o 'THREAD TIME' - 3 bits
	private static final byte TOTAL_BITS_THREAD_TIME = 0x03;

	// Total de BITs reservado para o 'ACTION EVENT' do PINO DIGITAL - 5 bits
	private static final byte TOTAL_BITS_DIGITAL_ACTION_EVENT = 0x05;
	// Total de BITs reservado para o 'ACTION EVENT' do PINO ANALOGICO - 7 bits
	private static final byte TOTAL_BITS_ANALOG_ACTION_EVENT = 0x07;

	// Total de BYTEs do protocolo - 4 bytes
	public static final byte TOTAL_BYTES_PROTOCOL = 0x04;

	// cria o protocolo
	private static int encode(Arduino arduino) {

		// EXECUTE/MESSAGE - DIGITAL
		// 0000 0 00 00 0 000000 00000000 00000000
		// 0000 _ Ordem bytes 4bits
		// 0/1 _ ARDUINO/PC 1bit
		// 0-3 _ SEND/SEND_RESPONSE/RESPONSE/RESPONSE_RESPONSE 2bit
		// 0-3 _ EXECUTE/WRITE/READ/MESSAGE 2bit
		// 0/1 _ DIGITAL/ANALOG 1bit
		// 0-63 _ PIN 6bits
		// 0-255 _ VALOR PIN | CODE MESSAGE 8bits
		// 0-255 _ CRC 8bits

		// EXECUTE/MESSAGE - ANALOG
		// 0000 0 00 11 1 0000 0000000000 00000000
		// 0000 _ Ordem bytes 4bits
		// 0/1 _ ARDUINO/PC 1bit
		// 0-3 _ SEND/SEND_RESPONSE/RESPONSE/RESPONSE_RESPONSE 2bit
		// 0-3 _ EXECUTE/WRITE/READ/MESSAGE 2bit
		// 0/1 _ DIGITAL/ANALOG 1bit
		// 0-15 _ PIN 4bits
		// 0-1023 _ VALOR PIN | CODE MESSAGE 10bits
		// 0-255 _ CRC 8bits

		// WRITE/READ - DIGITAL
		// 0000 0 00 01 0 000000 000 00000 00000000
		// 0000 _ Ordem bytes 4bits
		// 0/1 _ ARDUINO/PC 1bit
		// 0-3 _ SEND/SEND_RESPONSE/RESPONSE/RESPONSE_RESPONSE 2bit
		// 0-3 _ EXECUTE/WRITE/READ/MESSAGE 2bit
		// 0/1 _ DIGITAL/ANALOG 1bit
		// 0-63 _ PIN 6bits
		// 0-7 _ THREAD TIME 3bits
		// 0-31 _ ACTION EVENT 5bits
		// 0-255 _ CRC 8bits

		// WRITE/READ - ANALOG
		// 0000 0 00 10 1 0000 000 0000000 00000000
		// 0000 _ Ordem bytes 4bits
		// 0/1 _ ARDUINO/PC 1bit
		// 0-3 _ SEND/SEND_RESPONSE/RESPONSE/RESPONSE_RESPONSE 2bit
		// 0-3 _ EXECUTE/WRITE/READ/MESSAGE 2bit
		// 0/1 _ DIGITAL/ANALOG 1bit
		// 0-15 _ PIN 4bits
		// 0-7 _ THREAD TIME 3bits
		// 0-127 _ ACTION EVENT 7bits
		// 0-255 _ CRC 8bits

		if (arduino == null)
			return EMPTY_BITS;

		// Os status sao 0/1/2/3 = SEND/ SEND_RESPONSE/ RESPONSE/
		// RESPONSE_RESPONSE
		if (arduino.getStatus() == null)
			return EMPTY_BITS;

		// Os eventos sao 0/1/2/3 = EXECUTE/WRITE/READ/MESSAGE
		if (arduino.getEvent() == null)
			return EMPTY_BITS;

		// O tipos de pino sao 0/1 = DIGITAL/ANALOG
		if (arduino.getPinType() == null)
			return EMPTY_BITS;

		// Os pinos estao entre 0-63
		if (arduino.getPinType() == ArduinoPinType.DIGITAL
				&& arduino.getPin() > Arduino.DIGITAL_PIN_MAX)
			return EMPTY_BITS;
		// Os pinos estao entre 0-15
		else if (arduino.getPinType() == ArduinoPinType.ANALOG
				&& arduino.getPin() > Arduino.ANALOG_PIN_MAX)
			return EMPTY_BITS;

		if (arduino.getEvent() == ArduinoEvent.EXECUTE
				|| arduino.getEvent() == ArduinoEvent.MESSAGE) {

			// Os valores do pino estao entre 0-255
			if (((ArduinoUSART) arduino).getPinType() == ArduinoPinType.DIGITAL
					&& ((ArduinoUSART) arduino).getPinValue() > ArduinoUSART.DIGITAL_PIN_VALUE_MAX)
				return EMPTY_BITS;
			// Os valores do pino estao entre 0-1023
			else if (((ArduinoUSART) arduino).getPinType() == ArduinoPinType.ANALOG
					&& ((ArduinoUSART) arduino).getPinValue() > ArduinoUSART.ANALOG_PIN_VALUE_MAX)
				return EMPTY_BITS;
		} else if (arduino.getEvent() == ArduinoEvent.WRITE
				|| arduino.getEvent() == ArduinoEvent.READ) {

			// Os valores da 'thread time' estao entre 0-7
			if (((ArduinoEEPROM) arduino).getThreadTime() > ArduinoEEPROM.THREAD_TIME_MAX)
				return EMPTY_BITS;

			// Os valores do 'action event' estao entre 0-31
			if (((ArduinoEEPROM) arduino).getPinType() == ArduinoPinType.DIGITAL
					&& ((ArduinoEEPROM) arduino).getActionEvent() > ArduinoEEPROM.DIGITAL_ACTION_EVENT_MAX)
				return EMPTY_BITS;
			// Os valores do 'action event' estao entre 0-127
			else if (((ArduinoEEPROM) arduino).getPinType() == ArduinoPinType.ANALOG
					&& ((ArduinoEEPROM) arduino).getActionEvent() > ArduinoEEPROM.ANALOG_ACTION_EVENT_MAX)
				return EMPTY_BITS;
		}

		int protocol = EMPTY_BITS;

		// 00000000 00001000 00000000 00000000
		int mask = 0x00080000;
		int transmitterValue = ArduinoTransmitter.OTHER_DEVICE.ordinal();
		transmitterValue <<= (TOTAL_BITS_DATA - 1);
		protocol |= (transmitterValue & mask);

		// 00000000 00000110 00000000 00000000
		mask = 0x00060000;
		int statusValue = arduino.getStatus().ordinal();
		statusValue <<= (TOTAL_BITS_DATA - 3);
		protocol |= (statusValue & mask);

		// 00000000 00000001 10000000 00000000
		mask = 0x00018000;
		int eventValue = arduino.getEvent().ordinal();
		eventValue <<= (TOTAL_BITS_DATA - 5);
		protocol |= (eventValue & mask);

		// 00000000 00000000 01000000 00000000
		mask = 0x00004000;
		int pinType = arduino.getPinType().ordinal();
		pinType <<= (TOTAL_BITS_DATA - 6);
		protocol |= (pinType & mask);

		if (arduino.getPinType() == ArduinoPinType.DIGITAL) {
			// 00000000 00000000 00111111 00000000
			mask = 0x00003F00;
			int pin = arduino.getPin();
			pin <<= TOTAL_BITS_DIGITAL_PIN_VALUE;
			protocol |= (pin & mask);

			if (arduino.getEvent() == ArduinoEvent.EXECUTE
					|| arduino.getEvent() == ArduinoEvent.MESSAGE) {
				// 00000000 00000000 00000000 11111111
				mask = 0x000000FF;
				int pinValue = ((ArduinoUSART) arduino).getPinValue();
				protocol |= (pinValue & mask);
			} else if (arduino.getEvent() == ArduinoEvent.WRITE
					|| arduino.getEvent() == ArduinoEvent.READ) {
				// 00000000 00000000 00000000 11100000
				mask = 0x000000E0;
				int threadTime = ((ArduinoEEPROM) arduino).getThreadTime();
				threadTime <<= TOTAL_BITS_DIGITAL_ACTION_EVENT;
				protocol |= (threadTime & mask);

				// 00000000 00000000 00000000 00011111
				mask = 0x0000001F;
				int actionEvent = ((ArduinoEEPROM) arduino).getActionEvent();
				protocol |= (actionEvent & mask);
			}
		} else if (arduino.getPinType() == ArduinoPinType.ANALOG) {
			// 00000000 00000000 00111100 00000000
			mask = 0x00003C00;
			int pin = arduino.getPin();
			pin <<= TOTAL_BITS_ANALOG_PIN_VALUE;
			protocol |= (pin & mask);

			if (arduino.getEvent() == ArduinoEvent.EXECUTE
					|| arduino.getEvent() == ArduinoEvent.MESSAGE) {
				// 00000000 00000000 00000011 11111111
				mask = 0x000003FF;
				int pinValue = ((ArduinoUSART) arduino).getPinValue();
				protocol |= (pinValue & mask);
			} else if (arduino.getEvent() == ArduinoEvent.WRITE
					|| arduino.getEvent() == ArduinoEvent.READ) {
				// 00000000 00000000 00000011 10000000
				mask = 0x00000380;
				int threadTime = ((ArduinoEEPROM) arduino).getThreadTime();
				threadTime <<= TOTAL_BITS_ANALOG_ACTION_EVENT;
				protocol |= (threadTime & mask);

				// 00000000 00000000 00000000 01111111
				mask = 0x0000007F;
				int actionEvent = ((ArduinoEEPROM) arduino).getActionEvent();
				protocol |= (actionEvent & mask);
			}
		}

		byte checksum = Checksum.getCrc3Bytes(protocol);

		protocol <<= TOTAL_BITS_CHECKSUM;

		protocol |= (checksum & 0x000000FF); // 0000_00000000000000000000_11111111

		byte[] bytes = new byte[TOTAL_BYTES_PROTOCOL];

		for (byte i = 0x00; i < TOTAL_BYTES_PROTOCOL; i++) { // 0000_1111_111111_1111111111_11111111->11111111_01111111_01111111_01111111
			int aux = protocol;
			aux <<= (TOTAL_BITS_INDEX + (i * 7));
			aux >>= (TOTAL_BITS_PROTOCOL - 7);
			if (i == 0x00) {
				aux |= 0x00000080;
				aux = aux & 0x000000FF; // 00000000 00000000 00000000 11111111
			} else
				aux = aux & 0x0000007F; // 00000000 00000000 00000000 01111111

			bytes[i] = (byte) aux;
		}

		protocol = Binary.bytesToInt32(bytes);

		return protocol;
	}

	private static byte[] getProtocol(Arduino arduino) {
		int protocol = encode(arduino);

		if (protocol == EMPTY_BITS)
			return null;

		return Binary.intTo4Bytes(protocol);
	}

	public static byte[] getProtocolUSART(Arduino arduino) {
		if (!(arduino.getEvent() == ArduinoEvent.EXECUTE || arduino.getEvent() == ArduinoEvent.MESSAGE))
			return null;

		return getProtocol(arduino);
	}

	public static byte[] getProtocolEEPROM(Arduino arduino) {
		if (!(arduino.getEvent() == ArduinoEvent.WRITE || arduino.getEvent() == ArduinoEvent.READ))
			return null;

		return getProtocol(arduino);
	}

	// Decodifica o protocolo
	public static Arduino decode(byte[] values) throws ArduinoException {
		int protocol = 0x00000000;
		final int TOTAL_BYTES = values.length;
		int mask;

		for (byte i = 0; i < TOTAL_BYTES; i++) {
			int byteValue = values[i];
			byteValue <<= ((TOTAL_BYTES - i - 1) * 7);

			mask = 0x0000007F;
			mask <<= ((TOTAL_BYTES - i - 1) * 7);
			protocol |= (byteValue & mask);
		}

		// 00000000 00000000 00000000 11111111
		mask = 0x000000FF;
		byte checksumProtocol = (byte) (protocol & mask);

		// 00001111 11111111 11111111 00000000
		mask = 0x0FFFFF00;
		int message = (protocol & mask) >> TOTAL_BITS_CHECKSUM;

		byte checksum = Checksum.getCrc3Bytes(message);

		if (checksumProtocol != checksum)
			throw new ArduinoException("CRC invalido");

		// 0/1 _ ARDUINO/PC 1bit
		// 00001000 00000000 00000000 00000000
		mask = 0x08000000;
		int transmitterValue = protocol & mask;
		transmitterValue >>= (TOTAL_BITS_PROTOCOL - TOTAL_BITS_INDEX - 1);

		// 0-3 _ SEND/SEND_RESPONSE/RESPONSE/RESPONSE_RESPONSE 2bit
		// 00000110 00000000 00000000 00000000
		mask = 0x06000000;
		int statusValue = protocol & mask;
		statusValue >>= (TOTAL_BITS_PROTOCOL - TOTAL_BITS_INDEX - 3);

		// 0-3 _ EXECUTE/WRITE/READ/MESSAGE 2bit
		// 00000001 10000000 00000000 00000000
		mask = 0x01800000;
		int eventValue = protocol & mask;
		eventValue >>= (TOTAL_BITS_PROTOCOL - TOTAL_BITS_INDEX - 5);

		// 0/1 _ DIGITAL/ANALOG 1bit
		// 00000000 01000000 00000000 00000000
		mask = 0x00400000;
		int pinTypeValue = protocol & mask;
		pinTypeValue >>= (TOTAL_BITS_PROTOCOL - TOTAL_BITS_INDEX - 6);

		ArduinoTransmitter transmitter = getTransmitter((byte) transmitterValue);
		ArduinoStatus status = getStatus((byte) statusValue);
		ArduinoEvent event = getEvent((byte) eventValue);
		ArduinoPinType pinType = getPinType((byte) pinTypeValue);

		Arduino arduino = null;

		if (event == ArduinoEvent.EXECUTE)
			arduino = new ArduinoUSART();
		else if (event == ArduinoEvent.WRITE)
			arduino = new ArduinoEEPROMWrite();
		else if (event == ArduinoEvent.READ)
			arduino = new ArduinoEEPROMRead();
		else if (event == ArduinoEvent.MESSAGE)
			arduino = new ArduinoUSARTMessage();

		if (arduino == null)
			throw new ArduinoException("Evento invalido");

		arduino.setTransmitter(transmitter);
		arduino.setStatus(status);
		arduino.setPinType(pinType);

		if (arduino.getPinType() == ArduinoPinType.DIGITAL) {
			// 0-63 _ PIN 6bits
			// 00000000 00111111 00000000 00000000
			mask = 0x003F0000;
			int pin = protocol & mask;
			pin >>= (TOTAL_BITS_PROTOCOL - TOTAL_BITS_INDEX - 6 - TOTAL_BITS_DIGITAL_PIN);
			arduino.setPin((byte) pin);

			if (arduino.getEvent() == ArduinoEvent.EXECUTE
					|| arduino.getEvent() == ArduinoEvent.MESSAGE) {
				// 0-255 _ VALOR PIN 8bits
				// 00000000 00000000 11111111 00000000
				mask = 0x0000FF00;
				int pinValue = protocol & mask;
				pinValue >>= TOTAL_BITS_CHECKSUM;
				((ArduinoUSART) arduino).setPinValue((short) pinValue);
			} else if (arduino.getEvent() == ArduinoEvent.WRITE
					|| arduino.getEvent() == ArduinoEvent.READ) {
				// 0-7 _ VALOR PIN 3bits
				// 00000000 00000000 11100000 00000000
				mask = 0x0000E000;
				int threadTime = protocol & mask;
				threadTime >>= (TOTAL_BITS_CHECKSUM + TOTAL_BITS_DIGITAL_ACTION_EVENT);
				((ArduinoEEPROM) arduino).setThreadTime((byte) threadTime);

				// 0-31 _ VALOR PIN 5bits
				// 00000000 00000000 00011111 00000000
				mask = 0x00001F00;
				int actionEvent = protocol & mask;
				actionEvent >>= TOTAL_BITS_CHECKSUM;
				((ArduinoEEPROM) arduino).setActionEvent((byte) actionEvent);
			}
		} else if (arduino.getPinType() == ArduinoPinType.ANALOG) {
			// 0-15 _ PIN 6bits
			// 00000000 00111100 00000000 00000000
			mask = 0x003C0000;
			int pin = protocol & mask;
			pin >>= (TOTAL_BITS_PROTOCOL - TOTAL_BITS_INDEX - 4 - TOTAL_BITS_DIGITAL_PIN);
			arduino.setPin((byte) pin);

			if (arduino.getEvent() == ArduinoEvent.EXECUTE
					|| arduino.getEvent() == ArduinoEvent.MESSAGE) {
				// 0-1023 _ VALOR PIN 10bits
				// 00000000 00000011 11111111 00000000
				mask = 0x0003FF00;
				int pinValue = protocol & mask;
				pinValue >>= TOTAL_BITS_CHECKSUM;
				((ArduinoUSART) arduino).setPinValue((short) pinValue);
			} else if (arduino.getEvent() == ArduinoEvent.WRITE
					|| arduino.getEvent() == ArduinoEvent.READ) {
				// 0-7 _ VALOR PIN 3bits
				// 00000000 00000011 10000000 00000000
				mask = 0x00038000;
				int threadTime = protocol & mask;
				threadTime >>= (TOTAL_BITS_CHECKSUM + TOTAL_BITS_ANALOG_ACTION_EVENT);
				((ArduinoEEPROM) arduino).setThreadTime((byte) threadTime);

				// 0-31 _ VALOR PIN 5bits
				// 00000000 00000000 01111111 00000000
				mask = 0x00007F00;
				int actionEvent = protocol & mask;
				actionEvent >>= TOTAL_BITS_CHECKSUM;
				((ArduinoEEPROM) arduino).setActionEvent((byte) actionEvent);
			}
		}

		return arduino;
	}

	private static ArduinoTransmitter getTransmitter(byte transmitterValue) {
		ArduinoTransmitter transmitter = null;
		switch (transmitterValue) {
		case 0:
			transmitter = ArduinoTransmitter.ARDUINO;
			break;
		case 1:
			transmitter = ArduinoTransmitter.OTHER_DEVICE;
			break;
		default:
			break;
		}
		return transmitter;
	}

	private static ArduinoStatus getStatus(byte statusValue) {
		ArduinoStatus status = null;
		switch (statusValue) {
		case 0:
			status = ArduinoStatus.SEND;
			break;
		case 1:
			status = ArduinoStatus.SEND_RESPONSE;
			break;
		case 2:
			status = ArduinoStatus.RESPONSE;
			break;
		case 3:
			status = ArduinoStatus.RESPONSE_RESPONSE;
			break;
		default:
			break;
		}
		return status;
	}

	private static ArduinoEvent getEvent(byte eventValue) {
		ArduinoEvent event = null;
		switch (eventValue) {
		case 0:
			event = ArduinoEvent.EXECUTE;
			break;
		case 1:
			event = ArduinoEvent.WRITE;
			break;
		case 2:
			event = ArduinoEvent.READ;
			break;
		case 3:
			event = ArduinoEvent.MESSAGE;
			break;
		default:
			break;
		}
		return event;
	}

	private static ArduinoPinType getPinType(byte pinTypeValue) {
		ArduinoPinType pinType = null;
		switch (pinTypeValue) {
		case 0:
			pinType = ArduinoPinType.DIGITAL;
			break;
		case 1:
			pinType = ArduinoPinType.ANALOG;
			break;
		default:
			break;
		}
		return pinType;
	}

}
