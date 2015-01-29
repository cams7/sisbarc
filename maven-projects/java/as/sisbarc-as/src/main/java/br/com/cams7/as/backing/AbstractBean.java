/**
 * 
 */
package br.com.cams7.as.backing;

import br.com.cams7.as.AbstractBase;
import br.com.cams7.jpa.domain.BaseEntity;

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
