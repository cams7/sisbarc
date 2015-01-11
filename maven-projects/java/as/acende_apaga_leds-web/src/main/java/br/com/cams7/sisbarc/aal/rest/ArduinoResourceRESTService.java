/**
 * 
 */
package br.com.cams7.sisbarc.aal.rest;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.cams7.sisbarc.aal.ejb.service.ArduinoService;
import br.com.cams7.sisbarc.aal.vo.Led;

/**
 * @author cams7
 *
 */
@Path("/arduino")
@RequestScoped
public class ArduinoResourceRESTService {

	@Inject
	private Logger log;

	@EJB
	private ArduinoService service;

	// arduino/led/AMARELA
	// arduino/led/VERDE
	// arduino/led/VERMELHA
	@GET
	@Path("/led/{led}")
	@Produces(MediaType.APPLICATION_JSON)
	public Led mudaStatusLED(@PathParam("led") String stringLed) {

		Led.Cor cor = Led.Cor.valueOf(stringLed);

		Led led = new Led();
		led.setCor(cor);

		Future<Boolean> call = null;

		log.info("mudaStatusLED('" + led.getCor() + "') - Before sleep: "
				+ ArduinoService.DF.format(new Date()));

		switch (led.getCor()) {
		case AMARELA:
			call = service.mudaStatusLEDAmarela();
			break;
		case VERDE:
			call = service.mudaStatusLEDVerde();
			break;
		case VERMELHA:
			call = service.mudaStatusLEDVermelha();
			break;
		default:
			break;
		}

		if (call != null)
			try {
				Boolean status = call.get();
				led.setAcesa(status);
				log.info("LED '" + led.getCor() + "' esta "
						+ (led.getAcesa() ? "'Acesa'" : "'Apagada'"));
			} catch (InterruptedException | ExecutionException e) {
				log.log(Level.WARNING, e.getMessage());
			}

		log.info("mudaStatusLED('" + led.getCor() + "') - After sleep: "
				+ ArduinoService.DF.format(new Date()));

		return led;
	}

}
