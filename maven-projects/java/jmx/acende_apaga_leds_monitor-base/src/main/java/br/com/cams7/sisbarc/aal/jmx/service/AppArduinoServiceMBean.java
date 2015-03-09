/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Evento;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Intervalo;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EstadoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.arduino.ArduinoService;
import br.com.cams7.sisbarc.arduino.vo.Arduino.ArduinoEvent;
import br.com.cams7.sisbarc.arduino.vo.EEPROMData;

/**
 * @author cesar
 *
 */
public interface AppArduinoServiceMBean extends ArduinoService {

	/**
	 * Altera o ESTADO do LED para ACESO ou APAGADO
	 * 
	 * @param PINO
	 *            do LED - Numero do PINO DIGITAL
	 * @param ESTADO
	 *            do LED - ACESO/APAGADO
	 * 
	 * 
	 */
	public void alteraEstadoLED(PinPK pino, EstadoLED estado);

	/**
	 * Busca o ESTADO do LED, que pode ser ACESO ou APAGADO
	 * 
	 * @param PINO
	 *            do LED - Numero do PINO DIGITAL
	 */
	public void buscaEstadoLED(PinPK pino);

	/**
	 * Altera o EVENTO e o INTERVALO
	 * 
	 * @param PINO
	 *            - Numero do PINO DIGITAL/ANALOGICO
	 * @param EVENTO
	 * 
	 * @param INTERVALO
	 */
	public void alteraEvento(PinPK pino, Evento evento, Intervalo intervalo);

	/**
	 * Obtem os Dados na EEPROM do ARDUINO
	 * 
	 * @param PINO
	 *            - Numero do PINO DIGITAL/ANALOGICO
	 */
	public void buscaDados(PinPK pino);

	/**
	 * Obtem o ESTADO atual do LED informado,
	 * 
	 * Obs.: Os dados sao recebidos pela SERIAL do ARDUINO
	 * 
	 * @param PINO
	 *            do LED - Numero do PINO DIGITAL
	 * @return ESTADO do LED
	 */
	public EstadoLED getEstadoLED(PinPK pino, ArduinoEvent arduinoEvent);

	/**
	 * Obtem o EVENTO, os dados sao recebidos pela SERIAL do ARDUINO
	 * 
	 * @param PINO
	 *            - Numero do PINO DIGITAL/ANALOG
	 * @return EVENTO do LED
	 */
	public Evento getEvento(PinPK pino);

	/**
	 * Obtem os DADOS da EEPROM no ARDUINO
	 * 
	 * Obs.: Os dados sao recebidos pela SERIAL do ARDUINO
	 * 
	 * @param PINO
	 *            - Numero do PINO DIGITAL/ANALOGICO
	 * @return DADOS da EEPROM no ARDUINO
	 */
	public EEPROMData getDados(PinPK pino);

}
