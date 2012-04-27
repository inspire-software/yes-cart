package org.yes.cart.remote.service.impl;

import flex.messaging.FlexContext;
import org.yes.cart.remote.service.RemoteFlexSupportService;

/**
 *
 * Utility to support flex session.
 *
 * User:  Igor Azarny
 * Date: 4/28/12
 * Time: 10:54 AM
 */
public class RemoteFlexSupportServiceImpl implements RemoteFlexSupportService {

    /** {@inheritDoc} */
    public void setSessionInfo(final String key, final String value) {
        FlexContext.getFlexSession().setAttribute(key, value);
    }
}
