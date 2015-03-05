package br.com.cams7.sisbarc.aal.view;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;

import br.com.cams7.as.view.BaseView;
import br.com.cams7.sisbarc.aal.ejb.service.ArduinoService;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.CorLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EventoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.IntervaloLED;

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
public class LEDView extends BaseView<ArduinoService, LEDEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Container injeta a referencia p/ o ejb MercadoriaService
	 */
	@EJB
	private ArduinoService service;

	@SuppressWarnings("unchecked")
	public LEDView() {
		super();
	}

	@Override
	protected ArduinoService getService() {
		return service;
	}

	@Override
	protected void init() {
		getLog().info("Init View");
	}

	public void updateArduino(ActionEvent event) {
		LEDEntity led = getSelectedEntity();

		Future<LEDEntity> call = service.alteraEventoLED(led);

		boolean updated = false;

		if (call != null) {
			try {
				led = call.get();

				if (led.getEvento() != null) {
					try {
						service.save(led);

						updated = true;

						String summary = getMessageFromI18N("info.msg.led.update.ok");
						String detail = getMessageFromI18N(
								"info.msg.led.update", led.getCor().name(), led
										.getId().getPin());// Detalhes

						addMessage(FacesMessage.SEVERITY_INFO, summary, detail);
						getLog().info(detail);
					} catch (Exception e) {
						String summary = getMessageFromI18N(
								"error.msg.led.update", led.getCor(), led
										.getId().getPin());// Resumo
						String detail = e.getMessage();

						addMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
						getLog().log(Level.WARNING, detail);
					}

				} else {
					String summary = getMessageFromI18N("error.msg.arduino.not.run");// Resumo
					String detail = getMessageFromI18N("error.msg.led.update",
							led.getCor().name(), led.getId().getPin());// Detalhes

					addMessage(FacesMessage.SEVERITY_WARN, summary, detail);
					getLog().log(Level.WARNING, detail);
				}

			} catch (InterruptedException | ExecutionException e) {
				String summary = getMessageFromI18N("error.msg.led.update",
						led.getCor(), led.getId().getPin());// Resumo
				String detail = e.getMessage();

				addMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
				getLog().log(Level.WARNING, detail);
			}
		} else {
			String summary = getMessageFromI18N("error.msg.monitor.not.run");// Resumo
			String detail = getMessageFromI18N("error.msg.led.update", led
					.getCor().name(), led.getId().getPin());// Detalhes

			addMessage(FacesMessage.SEVERITY_WARN, summary, detail);
			getLog().log(Level.WARNING, detail);
		}

		RequestContext context = RequestContext.getCurrentInstance();
		context.addCallbackParam("arduino_updated", updated);
	}

	public CorLED[] getCores() {
		return CorLED.values();
	}

	public EventoLED[] getEventos() {
		return EventoLED.values();
	}

	public IntervaloLED[] getIntervalos() {
		return IntervaloLED.values();
	}

}
