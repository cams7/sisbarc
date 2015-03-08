package br.com.cams7.sisbarc.aal.view;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;

import br.com.cams7.sisbarc.aal.ejb.service.LEDService;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Evento;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.CorLED;

/**
 * Componente responsável por integrar o front-end (páginas JSF) c/ camada de
 * serviço (EJB), para resolver o cadastro de <code>LED</code>.
 * 
 * <p>
 * Trata-se de um <code>Managed Bean</code>, ou seja, as instâncias dessa classe
 * são controladas pelo <code>JSF</code>. Um objeto é criado ao carregar alguma
 * página do cadastro (Lista / Novo / Editar). Enquanto a página permanecer
 * aberta no browser, o objeto <code>LEDBean</code> permanece no servidor.
 * </p>
 * 
 * <p>
 * Esse componente atua com um papel parecido com o <code>Controller</code> de
 * outros frameworks <code>MVC</code>, ele resolve o fluxo de navegação e liga
 * os componentes visuais com os dados.
 * </p>
 * 
 * @author cams7
 *
 */
@ManagedBean(name = "ledView")
@ViewScoped
public class LEDView extends AALView<LEDService, LEDEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Container injeta a referencia p/ o ejb MercadoriaService
	 */
	@EJB
	private LEDService service;

	@SuppressWarnings("unchecked")
	public LEDView() {
		super();
	}

	@Override
	protected void init() {
		getLog().info("Init View");
	}

	public void atualizaLED(ActionEvent event) {
		LEDEntity led = getSelectedEntity();

		final String MSG_ERROR_UPDATE = getMessageFromI18N(
				"error.msg.led.update", led.getCor().name(), led.getId()
						.getPin());

		RequestContext context = RequestContext.getCurrentInstance();
		final String CALLBACK_PARAM = "arduinoAtualizado";

		try {
			Future<Boolean> call = getService().atualizaLED(led);

			boolean arduinoRun = call.get();

			if (arduinoRun) {
				String summary = getMessageFromI18N("info.msg.led.update.ok");// Resumo
				String detail = getMessageFromI18N("info.msg.led.update", led
						.getCor().name(), led.getId().getPin());// Detalhes

				addINFOMessage(summary, detail);
				context.addCallbackParam(CALLBACK_PARAM, true);
			} else {
				addMessageArduinoNotRun(MSG_ERROR_UPDATE);
				context.addCallbackParam(CALLBACK_PARAM, false);
			}
		} catch (InterruptedException | ExecutionException e) {
			addERRORMessage(MSG_ERROR_UPDATE, e.getMessage());
			context.addCallbackParam(CALLBACK_PARAM, false);
		} catch (NullPointerException e) {
			addMessageMonitorNotRun(MSG_ERROR_UPDATE);
			context.addCallbackParam(CALLBACK_PARAM, false);
		}

	}

	public void atualizaLEDs(ActionEvent event) {
		final String MSG_ERROR_UPDATE = getMessageFromI18N("error.msg.leds.update");

		List<LEDEntity> leds = getService().findAll();
		try {
			Future<Boolean> call = getService().alteraLEDEventos(leds);

			boolean arduinoRun = call.get();

			if (arduinoRun) {
				String summary = getMessageFromI18N("info.msg.leds.update.ok");// Resumo
				String detail = getMessageFromI18N("info.msg.leds.update");// Detalhes

				addINFOMessage(summary, detail);
			} else
				addMessageArduinoNotRun(MSG_ERROR_UPDATE);

		} catch (InterruptedException | ExecutionException e) {
			addERRORMessage(MSG_ERROR_UPDATE, e.getMessage());
		} catch (NullPointerException e) {
			addMessageMonitorNotRun(MSG_ERROR_UPDATE);
		}

	}

	public void sincronizaLEDs(ActionEvent event) {
		final String MSG_ERROR_SYNCHRONIZE = getMessageFromI18N("error.msg.leds.synchronize");

		List<LEDEntity> leds = getService().findAll();
		try {
			Future<Boolean> call = getService().sincronizaLEDEventos(leds);

			boolean arduinoRun = call.get();

			if (arduinoRun) {
				String summary = getMessageFromI18N("info.msg.leds.synchronize.ok");// Resumo
				String detail = getMessageFromI18N("info.msg.leds.synchronize");// Detalhes

				addINFOMessage(summary, detail);
			} else
				addMessageArduinoNotRun(MSG_ERROR_SYNCHRONIZE);

		} catch (InterruptedException | ExecutionException e) {
			addERRORMessage(MSG_ERROR_SYNCHRONIZE, e.getMessage());
		} catch (NullPointerException e) {
			addMessageMonitorNotRun(MSG_ERROR_SYNCHRONIZE);
		}

	}

	@Override
	protected LEDService getService() {
		return service;
	}

	public CorLED[] getCores() {
		return CorLED.values();
	}

	@Override
	public Evento[] getEventos() {
		Evento[] eventos = new Evento[3];
		eventos[0] = Evento.ACENDE_APAGA;
		eventos[1] = Evento.PISCA_PISCA;
		eventos[2] = Evento.FADE;

		return eventos;
	}

}
