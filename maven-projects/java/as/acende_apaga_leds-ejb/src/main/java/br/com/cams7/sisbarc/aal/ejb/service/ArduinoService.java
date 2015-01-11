/**
 * 
 */
package br.com.cams7.sisbarc.aal.ejb.service;

import java.text.SimpleDateFormat;
import java.util.concurrent.Future;

import javax.ejb.Local;

/**
 * @author cams7
 *
 */
@Local
public interface ArduinoService {
	public static final String YEARS_TO_MILLIS = "dd/MM/yyyy HH:mm:ss.SSS";
	public static final SimpleDateFormat DF = new SimpleDateFormat(
			YEARS_TO_MILLIS);

	public Future<Boolean> mudaStatusLEDAmarela();

	public Future<Boolean> mudaStatusLEDVerde();

	public Future<Boolean> mudaStatusLEDVermelha();

}
