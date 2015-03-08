/**
 * 
 */
package br.com.cams7.sisbarc.aal.view;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;

import br.com.cams7.sisbarc.aal.ejb.service.PotenciometroService;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Evento;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Intervalo;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.PotenciometroEntity;

/**
 * @author cams7
 *
 */
@ManagedBean(name = "potenciometroView")
@ViewScoped
public class PotenciometroView extends
		AALView<PotenciometroService, PotenciometroEntity> {

	private static final long serialVersionUID = 1L;

	@EJB
	private PotenciometroService service;

	@SuppressWarnings("unchecked")
	public PotenciometroView() {
		super();
	}

	@Override
	protected void init() {
		getLog().info("Init View");
	}

	public void atualizaPotenciometro(ActionEvent event) {
		PotenciometroEntity potenciometro = getSelectedEntity();

		final String MSG_ERROR_UPDATE = getMessageFromI18N(
				"error.msg.potentiometer.update", potenciometro.getId()
						.getPin());

		RequestContext context = RequestContext.getCurrentInstance();
		final String CALLBACK_PARAM = "arduinoAtualizado";

		try {
			Future<Boolean> call = getService().atualizaPotenciometro(
					potenciometro);

			boolean arduinoRun = call.get();

			if (arduinoRun) {
				String summary = getMessageFromI18N("info.msg.potentiometer.update.ok");// Resumo
				String detail = getMessageFromI18N(
						"info.msg.potentiometer.update", potenciometro.getId()
								.getPin());// Detalhes

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

	public void atualizaPotenciometros(ActionEvent event) {
		final String MSG_ERROR_UPDATE = getMessageFromI18N("error.msg.potentiometers.update");

		List<PotenciometroEntity> potenciometros = getService().findAll();
		try {
			Future<Boolean> call = getService().alteraPotenciometroEventos(
					potenciometros);

			boolean arduinoRun = call.get();

			if (arduinoRun) {
				String summary = getMessageFromI18N("info.msg.potentiometers.update.ok");// Resumo
				String detail = getMessageFromI18N("info.msg.potentiometers.update");// Detalhes

				addINFOMessage(summary, detail);
			} else
				addMessageArduinoNotRun(MSG_ERROR_UPDATE);

		} catch (InterruptedException | ExecutionException e) {
			addERRORMessage(MSG_ERROR_UPDATE, e.getMessage());
		} catch (NullPointerException e) {
			addMessageMonitorNotRun(MSG_ERROR_UPDATE);
		}

	}

	public void sincronizaPotenciometros(ActionEvent event) {
		final String MSG_ERROR_SYNCHRONIZE = getMessageFromI18N("error.msg.potentiometers.synchronize");

		List<PotenciometroEntity> potenciometros = getService().findAll();
		try {
			Future<Boolean> call = getService().sincronizaPotenciometroEventos(
					potenciometros);

			boolean arduinoRun = call.get();

			if (arduinoRun) {
				String summary = getMessageFromI18N("info.msg.potentiometers.synchronize.ok");// Resumo
				String detail = getMessageFromI18N("info.msg.potentiometers.synchronize");// Detalhes

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
	public Evento[] getEventos() {
		Evento[] eventos = new Evento[2];
		eventos[0] = Evento.ACENDE_APAGA;
		eventos[1] = Evento.NENHUM;

		return eventos;
	}

	@Override
	public Intervalo[] getIntervalos() {
		Intervalo[] intervalos = new Intervalo[4];
		intervalos[0] = Intervalo.INTERVALO_100MILISEGUNDOS;
		intervalos[1] = Intervalo.INTERVALO_1SEGUNDO;
		intervalos[2] = Intervalo.INTERVALO_3SEGUNDOS;
		intervalos[3] = Intervalo.SEM_INTERVALO;
		return intervalos;
	}

	@Override
	protected PotenciometroService getService() {
		return service;
	}

}
