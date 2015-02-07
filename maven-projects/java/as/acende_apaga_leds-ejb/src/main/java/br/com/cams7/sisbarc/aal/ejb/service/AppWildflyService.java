package br.com.cams7.sisbarc.aal.ejb.service;

import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity;

public interface AppWildflyService {

	public LedEntity.LedStatus getStatusActiveLED(LedEntity.LedColor ledCor);

}
