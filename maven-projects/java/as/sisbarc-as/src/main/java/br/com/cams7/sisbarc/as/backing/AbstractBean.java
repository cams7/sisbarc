/**
 * 
 */
package br.com.cams7.sisbarc.as.backing;

import br.com.cams7.sisbarc.as.AbstractBase;
import br.com.cams7.sisbarc.jpa.domain.BaseEntity;

/**
 * @author cesar
 *
 */
public abstract class AbstractBean<E extends BaseEntity<?>> extends
		AbstractBase<E> {

	public AbstractBean() {
		super();
	}

}
