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
import javax.ws.rs.core.Response;

import br.com.cams7.sisbarc.aal.LED;
import br.com.cams7.sisbarc.aal.ejb.service.ArduinoService;

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
	public Response mudaStatusLED(@PathParam("led") String stringLed) {

		LED led = LED.valueOf(stringLed);

		switch (led) {
		case AMARELA:
			service.mudaStatusLEDAmarela();
			break;
		case VERDE:
			service.mudaStatusLEDVerde();
			break;
		case VERMELHA:
			service.mudaStatusLEDVermelha();
			break;
		default:
			break;
		}

		Response.ResponseBuilder builder = Response.ok();
		return builder.build();
	}

}
