/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.arduino.ArduinoService;

/**
 * @author cesar
 *
 */
public interface AppArduinoServiceMBean extends ArduinoService {

	public void mudaStatusLEDAmarela(boolean ledAcesa);

	public boolean isLEDAmarelaAcesa();

	public void mudaStatusLEDVerde(boolean ledAcesa);

	public boolean isLEDVerdeAcesa();

	public void mudaStatusLEDVermelha(boolean ledAcesa);

	public boolean isLEDVermelhaAcesa();

}
