/**
 * 
 */
package br.com.cams7.sisbarc.aal.ejb.service;

import java.util.List;
import java.util.concurrent.Future;

import br.com.cams7.as.service.BaseService;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.PotenciometroEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;

/**
 * @author cams7
 *
 */
public interface PotenciometroService extends
		BaseService<PotenciometroEntity, PinPK> {

	public Future<Boolean> atualizaPotenciometro(
			PotenciometroEntity potenciometro);

	public Future<Boolean> sincronizaPotenciometroEventos(
			List<PotenciometroEntity> potenciometros);

	public Future<Boolean> alteraPotenciometroEventos(
			List<PotenciometroEntity> potenciometros);

}
