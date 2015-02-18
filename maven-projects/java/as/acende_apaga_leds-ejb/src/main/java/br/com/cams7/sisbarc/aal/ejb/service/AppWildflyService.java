package br.com.cams7.sisbarc.aal.ejb.service;

import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.CorLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EstadoLED;

public interface AppWildflyService {

	public EstadoLED getEstadoLEDAtivadoPorBotao(CorLED cor);

}
