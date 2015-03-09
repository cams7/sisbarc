/**
 * 
 */
package br.com.cams7.sisbarc.aal.view;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

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
