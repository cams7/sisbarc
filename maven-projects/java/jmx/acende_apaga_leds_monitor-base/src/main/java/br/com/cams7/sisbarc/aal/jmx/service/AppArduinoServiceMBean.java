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

	public void mudaStatusLEDAmarela();

	public boolean isLedAmarelaLigada();

	public void mudaStatusLEDVerde();

	public boolean isLedVerdeLigada();

	public void mudaStatusLEDVermelha();

	public boolean isLedVermelhaLigada();

}
