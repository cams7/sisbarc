/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EstadoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EventoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.IntervaloLED;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.arduino.ArduinoService;
import br.com.cams7.sisbarc.arduino.vo.EEPROMData;

/**
 * @author cesar
 *
 */
public interface AppArduinoServiceMBean extends ArduinoService {

	/**
	 * @param pino
	 * @param estado
	 * 
	 *            Altera o estado do LED ligado/desligado
	 */
	public void alteraEstadoLED(PinPK pino, EstadoLED estado);

	/**
	 * @param pino
	 * @return
	 * 
	 *         Obtem o estado atual do LED informado
	 */
	public EstadoLED getEstadoLED(PinPK pino);

	/**
	 * @param pino
	 * @param evento
	 * @param intervalo
	 */
	public void alteraEventoLED(PinPK pino, EventoLED evento,
			IntervaloLED intervalo);

	/**
	 * @param pino
	 * @return
	 */
	public EventoLED getEventoLED(PinPK pino);

	/**
	 * @param pino
	 */
	public void buscaDadosLED(PinPK pino);

	/**
	 * @param pino
	 * @return
	 */
	public EEPROMData getDadosLED(PinPK pino);

}
