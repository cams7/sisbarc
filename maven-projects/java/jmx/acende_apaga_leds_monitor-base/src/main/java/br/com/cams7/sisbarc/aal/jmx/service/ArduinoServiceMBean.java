/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

/**
 * @author cesar
 *
 */
public interface ArduinoServiceMBean {

	public void mudaStatusLEDAmarela();

	public boolean isLedAmarelaLigada();

	public void mudaStatusLEDVerde();

	public boolean isLedVerdeLigada();

	public void mudaStatusLEDVermelha();

	public boolean isLedVermelhaLigada();

}
