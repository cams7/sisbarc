/**
 * 
 */
package br.com.cams7.sisbarc.aal.rest;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
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

	@EJB
	private ArduinoService service;

	// arduino/led/AMARELA
	// arduino/led/VERDE
	// arduino/led/VERMELHA
	@GET
	@Path("/led/{led}")
	@Produces(MediaType.APPLICATION_JSON)
	public Led mudaStatusLED(@PathParam("led") String stringLed) {

		Led led = new Led();
		led.setCor(stringLed);

		Boolean status = null;
		if ("AMARELA".equals(led.getCor()))
			status = service.mudaStatusLEDAmarela();
		else if ("VERDE".equals(led.getCor()))
			status = service.mudaStatusLEDVerde();
		else if ("VERMELHA".equals(led.getCor()))
			status = service.mudaStatusLEDVermelha();

		led.setAcesa(status);

		return led;
	}

}
