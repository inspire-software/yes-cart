package org.yes.cart.web.phazelistener;

import org.springframework.web.jsf.DelegatingPhaseListenerMulticaster;

import javax.faces.event.PhaseId;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/14/11
 * Time: 9:13 PM
 */
public class DelegatingShoppingCartPersister extends DelegatingPhaseListenerMulticaster {

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

}
