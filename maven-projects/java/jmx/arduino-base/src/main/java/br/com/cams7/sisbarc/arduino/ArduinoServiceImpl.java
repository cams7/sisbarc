package br.com.cams7.sisbarc.arduino;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.cams7.sisbarc.arduino.util.Binary;
import br.com.cams7.sisbarc.arduino.util.Bytes;
import br.com.cams7.sisbarc.arduino.vo.Arduino;
import br.com.cams7.sisbarc.arduino.vo.Arduino.ArduinoEvent;
import br.com.cams7.sisbarc.arduino.vo.Arduino.ArduinoStatus;
import br.com.cams7.sisbarc.arduino.vo.Arduino.ArduinoTransmitter;
import br.com.cams7.sisbarc.arduino.vo.ArduinoEEPROM;
import br.com.cams7.sisbarc.arduino.vo.ArduinoEEPROMRead;
import br.com.cams7.sisbarc.arduino.vo.ArduinoEEPROMWrite;
import br.com.cams7.sisbarc.arduino.vo.ArduinoPin.ArduinoPinType;
import br.com.cams7.sisbarc.arduino.vo.ArduinoUSART;

public abstract class ArduinoServiceImpl implements ArduinoService, Runnable,
		SerialPortEventListener {

	private Logger log;

	private OutputStream output;
	private InputStream input;

	private String serialPort;
	private int serialBaudRate;
	private long serialThreadTime;

	private Byte[] serialData;
	private byte serialDataIndex;

	private Map<String, Arduino> currentStatus;

	/**
	 * Construtor da classe Arduino
	 * 
	 * @param serialPort
	 *            - Porta COM que sera utilizada para enviar os dados para o
	 *            Arduino
	 * @param bauldRate
	 *            - Taxa de transferencia da porta serial geralmente e 9600
	 */
	protected ArduinoServiceImpl(String serialPort, int serialBaudRate,
			long serialThreadTime) throws ArduinoException {
		super();

		this.serialPort = serialPort;
		this.serialBaudRate = serialBaudRate;
		this.serialThreadTime = serialThreadTime;

		serialData = new Byte[SisbarcProtocol.TOTAL_BYTES_PROTOCOL];
		serialDataIndex = 0x00;

		currentStatus = new HashMap<String, Arduino>();

		log = Logger.getLogger(this.getClass().getName());

		init();
	}

	/**
	 * Metodo que verifica se a comunicacao com a porta serial esta OK
	 */
	private void init() throws ArduinoException {
		// close();

		// Define uma variavel portId do tipo CommPortIdentifier para
		// realizar a comunicacao serial
		CommPortIdentifier portId = null;
		try {
			// Tenta verificar se a porta COM informada existe
			portId = CommPortIdentifier.getPortIdentifier(serialPort);
		} catch (NoSuchPortException e) {
			// Caso a porta COM nao exista sera exibido um erro
			throw new ArduinoException("Porta '" + serialPort
					+ "' nao encontrada", e.getCause());
		}

		try {
			// Abre a porta COM
			SerialPort port = (SerialPort) portId.open("Comunicacao serial",
					serialBaudRate);

			output = port.getOutputStream();
			input = port.getInputStream();

			port.addEventListener(this);

			port.notifyOnDataAvailable(true);

			port.setSerialPortParams(serialBaudRate, // taxa de transferencia da
					// porta serial
					SerialPort.DATABITS_8, // taxa de 10 bits 8 (envio)
					SerialPort.STOPBITS_1, // taxa de 10 bits 1 (recebimento)
					SerialPort.PARITY_NONE); // receber e enviar dados

			Thread readThread = new Thread(this);
			readThread.start();
		} catch (PortInUseException | IOException | TooManyListenersException
				| UnsupportedCommOperationException e) {
			throw new ArduinoException("Erro na comunicacao serial",
					e.getCause());
		} finally {
			close();
		}

	}

	/**
	 * M�todo que fecha a comunica��o com a porta serial
	 */
	private void close() throws ArduinoException {
		ArduinoException exception = new ArduinoException(
				"Nao foi possivel fechar a porta '" + serialPort + "'");
		try {
			if (input != null)
				input.close();
		} catch (IOException e) {
			exception.addSuppressed(e);
		}

		try {
			if (output != null)
				output.close();
		} catch (IOException e) {
			exception.addSuppressed(e);
		}

		if (exception.getSuppressed().length > 0)
			throw exception;
	}

	/**
	 * @param opcao
	 *            - Valor a ser enviado pela porta serial
	 */
	private void serialWrite(byte[] data) throws ArduinoException {
		if (output == null)
			throw new ArduinoException("O 'OutputStream' nao foi inicializado");

		try {
			// escreve o valor na porta serial para ser enviado
			output.write(data);
		} catch (IOException e) {
			throw new ArduinoException("Nao foi possivel enviar o dado",
					e.getCause());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gnu.io.SerialPortEventListener#serialEvent(gnu.io.SerialPortEvent)
	 */
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			try {
				while (input.available() > 0)
					receiveDataBySerial((byte) input.read());
			} catch (IOException e) {
				getLog().log(Level.SEVERE, e.getMessage());
			}
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Thread.sleep(serialThreadTime);
		} catch (InterruptedException e) {
			getLog().log(Level.SEVERE, e.getMessage());
		}
	}

	private void receiveDataBySerial(byte data) {

		byte lastBit = Binary.getLastBitByte(data);

		if (0x01 == lastBit) {
			serialData[0] = data;
			serialDataIndex = 0x01;
		} else if (serialDataIndex > 0x00) {
			serialData[serialDataIndex] = data;
			serialDataIndex++;

			if (serialDataIndex == SisbarcProtocol.TOTAL_BYTES_PROTOCOL) {
				byte[] datas = Bytes.toPrimitiveArray(serialData);
				try {
					Arduino arduino = receive(datas);
					addCurrentStatus(arduino);
					receiveDataBySerial(arduino);
				} catch (ArduinoException e) {
					getLog().log(Level.SEVERE, e.getMessage());
				}
				serialDataIndex = 0x00;
			}

		} else {
			getLog().log(
					Level.WARNING,
					"O dado '" + Integer.toBinaryString(data)
							+ "' foi corrompido");
		}

	}

	protected void addCurrentStatus(Arduino arduino) {
		String key = getKeyCurrentStatus(arduino.getEvent(),
				arduino.getPinType(), arduino.getPin());

		if (currentStatus.isEmpty() || !currentStatus.containsKey(key))
			currentStatus.put(key, arduino);
		else {
			if (arduino instanceof ArduinoUSART)
				((ArduinoUSART) currentStatus.get(key))
						.changeCurrentValues((ArduinoUSART) arduino);
			else if (arduino instanceof ArduinoEEPROM)
				((ArduinoEEPROM) currentStatus.get(key))
						.changeCurrentValues((ArduinoEEPROM) arduino);
		}
	}

	protected String getKeyCurrentStatus(ArduinoEvent event,
			ArduinoPinType pinType, byte pin) {
		String key = event.getAbbreviation() + "_" + pinType.getAbbreviation()
				+ pin;

		return key;
	}

	private void receiveDataBySerial(Arduino arduino) {
		if (ArduinoTransmitter.ARDUINO != arduino.getTransmitter()) {
			getLog().log(Level.WARNING, "O dado não vem do Arduino");
			return;
		}

		switch (arduino.getStatus()) {
		case SEND:
		case RESPONSE:
			switch (arduino.getEvent()) {
			case EXECUTE:
				receiveExecute(arduino.getPinType(), arduino.getPin(),
						((ArduinoUSART) arduino).getPinValue());
				break;
			case MESSAGE:
				receiveMessage(arduino.getPinType(), arduino.getPin(),
						((ArduinoUSART) arduino).getPinValue());
				break;
			case WRITE:
				receiveWrite(arduino.getPinType(), arduino.getPin(),
						((ArduinoEEPROM) arduino).getThreadInterval(),
						((ArduinoEEPROM) arduino).getActionEvent());
				break;
			case READ:
				receiveRead(arduino.getPinType(), arduino.getPin(),
						((ArduinoEEPROM) arduino).getThreadInterval(),
						((ArduinoEEPROM) arduino).getActionEvent());
				break;
			default:
				break;
			}

			break;

		case SEND_RESPONSE:
		case RESPONSE_RESPONSE:
			switch (arduino.getEvent()) {
			case EXECUTE:
			case MESSAGE:
				short pinValue = sendResponse(arduino.getPinType(),
						arduino.getPin(),
						((ArduinoUSART) arduino).getPinValue());

				try {
					switch (arduino.getPinType()) {
					case DIGITAL: {
						boolean isPinPWD = false;
						for (byte pinPWD : Arduino.getPinsDigitalPWM())
							if (arduino.getPin() == pinPWD) {
								isPinPWD = true;
								break;
							}

						if (isPinPWD)
							sendPinPWM(ArduinoStatus.RESPONSE_RESPONSE,
									arduino.getPin(), pinValue);
						else
							sendPinDigital(ArduinoStatus.RESPONSE_RESPONSE,
									arduino.getPin(), pinValue == 0x0001);
						break;
					}
					case ANALOG: {
						sendPinAnalog(ArduinoStatus.RESPONSE_RESPONSE,
								arduino.getPin(), pinValue);
						break;
					}
					default:
						break;
					}
				} catch (ArduinoException e) {
					getLog().log(Level.WARNING, e.getMessage());
				}

				break;
			case WRITE:
			case READ:
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}

	}

	protected abstract void receiveExecute(ArduinoPinType pinType, byte pin,
			short pinValue);

	protected abstract void receiveWrite(ArduinoPinType pinType, byte pin,
			byte threadTime, byte actionEvent);

	protected abstract void receiveRead(ArduinoPinType pinType, byte pin,
			byte threadTime, byte actionEvent);

	protected abstract void receiveMessage(ArduinoPinType pinType, byte pin,
			short codMessage);

	protected abstract short sendResponse(ArduinoPinType pinType, byte pin,
			short pinValue);

	protected void sendPinDigital(ArduinoStatus status, byte digitalPin,
			boolean pinValue) throws ArduinoException {
		ArduinoUSART arduino = new ArduinoUSART(status, ArduinoPinType.DIGITAL,
				digitalPin, (short) (pinValue ? 0x0001 : 0x0000));

		boolean pinOk = false;
		for (byte pin : Arduino.getPinsDigital())
			if (arduino.getPin() == pin) {
				pinOk = true;
				break;
			}

		if (!pinOk)
			for (byte pin : Arduino.getPinsDigitalPWM())
				if (arduino.getPin() == pin) {
					pinOk = true;
					break;
				}

		if (!pinOk)
			throw new ArduinoException("O PINO Digital nao e valido");

		arduino.setPinType(ArduinoPinType.DIGITAL);

		serialWrite(SisbarcProtocol.getProtocolUSART(arduino));

		addCurrentStatus(arduino);
	}

	protected void sendPinPWM(ArduinoStatus status, byte digitalPin,
			short pinValue) throws ArduinoException {
		ArduinoUSART arduino = new ArduinoUSART(status, ArduinoPinType.DIGITAL,
				digitalPin, pinValue);

		boolean pinOk = false;
		for (byte pin : Arduino.getPinsDigitalPWM())
			if (arduino.getPin() == pin) {
				pinOk = true;
				break;
			}

		if (!pinOk)
			throw new ArduinoException("O PINO PWM nao e valido");

		if (((ArduinoUSART) arduino).getPinValue() < 0x0000)
			throw new ArduinoException(
					"O valor do PINO PWM e maior ou igual a '0'");

		if (((ArduinoUSART) arduino).getPinValue() > ArduinoUSART.DIGITAL_PIN_VALUE_MAX)
			throw new ArduinoException(
					"O valor do PINO PWM e menor ou igual a '255'");

		arduino.setPinType(ArduinoPinType.DIGITAL);

		serialWrite(SisbarcProtocol.getProtocolUSART(arduino));

		addCurrentStatus(arduino);
	}

	protected void sendPinAnalog(ArduinoStatus status, byte analogPin,
			short pinValue) throws ArduinoException {
		ArduinoUSART arduino = new ArduinoUSART(status, ArduinoPinType.ANALOG,
				analogPin, pinValue);

		boolean pinOk = false;
		for (byte pin : Arduino.getPinsAnalog())
			if (arduino.getPin() == pin) {
				pinOk = true;
				break;
			}

		if (!pinOk)
			throw new ArduinoException("O PINO Analogico nao e valido");

		if (((ArduinoUSART) arduino).getPinValue() < 0x0000)
			throw new ArduinoException(
					"O valor do PINO Analogico e maior ou igual a '0'");

		if (((ArduinoUSART) arduino).getPinValue() > ArduinoUSART.ANALOG_PIN_VALUE_MAX)
			throw new ArduinoException(
					"O valor do PINO Analogico e menor ou igual a '1023'");

		arduino.setPinType(ArduinoPinType.ANALOG);

		serialWrite(SisbarcProtocol.getProtocolUSART(arduino));

		addCurrentStatus(arduino);
	}

	private static Arduino receive(byte[] values) throws ArduinoException {
		return SisbarcProtocol.decode(values);
	}

	protected void sendDigitalEEPROMWrite(ArduinoStatus status, byte pin,
			byte threadTime, byte actionEvent) throws ArduinoException {

		ArduinoEEPROMWrite arduino = new ArduinoEEPROMWrite(status,
				ArduinoPinType.DIGITAL, pin, threadTime, actionEvent);
		serialWrite(SisbarcProtocol.getProtocolEEPROM(arduino));

		addCurrentStatus(arduino);
	}

	protected void sendDigitalEEPROMRead(ArduinoStatus status, byte pin)
			throws ArduinoException {

		ArduinoEEPROMRead arduino = new ArduinoEEPROMRead(status,
				ArduinoPinType.DIGITAL, pin);
		serialWrite(SisbarcProtocol.getProtocolEEPROM(arduino));

		addCurrentStatus(arduino);
	}

	protected void sendAnalogEEPROMWrite(ArduinoStatus status, byte pin,
			byte threadTime, byte actionEvent) throws ArduinoException {

		ArduinoEEPROMWrite arduino = new ArduinoEEPROMWrite(status,
				ArduinoPinType.ANALOG, pin, threadTime, actionEvent);
		serialWrite(SisbarcProtocol.getProtocolEEPROM(arduino));

		addCurrentStatus(arduino);
	}

	protected void sendAnalogEEPROMRead(ArduinoStatus status, byte pin)
			throws ArduinoException {

		ArduinoEEPROMRead arduino = new ArduinoEEPROMRead(status,
				ArduinoPinType.ANALOG, pin);
		serialWrite(SisbarcProtocol.getProtocolEEPROM(arduino));

		addCurrentStatus(arduino);
	}

	protected Logger getLog() {
		return log;
	}

	public String getSerialPort() {
		return serialPort;
	}

	public int getSerialBaudRate() {
		return serialBaudRate;
	}

	public long getSerialThreadTime() {
		return serialThreadTime;
	}

	protected Map<String, Arduino> getCurrentStatus() {
		return currentStatus;
	}

}
