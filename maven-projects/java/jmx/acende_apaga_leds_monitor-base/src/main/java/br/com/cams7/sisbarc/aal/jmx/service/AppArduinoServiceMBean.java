/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EstadoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EventoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.IntervaloLED;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.arduino.ArduinoService;

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
	 * @param color
	 * @return
	 * 
	 *         Obtem o estado atual do LED informado
	 */
	public EstadoLED getEstadoLED(PinPK pino);

	public void alteraEventoLED(PinPK pino, EventoLED evento,
			IntervaloLED intervalo);

	public EventoLED getEventoLED(PinPK pino);

}
