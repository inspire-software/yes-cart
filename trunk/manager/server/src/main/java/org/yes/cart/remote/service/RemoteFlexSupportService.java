package org.yes.cart.remote.service;

/**
 *
 * Utility to support flex session.
 * User: Igor Azarny
 * Date: 4/28/12
 * Time: 10:53 AM
 */
public interface RemoteFlexSupportService {

    /**
     * Add information info flex session.
     * @param key key
     * @param value value
     */
    void setSessionInfo(String key, String value);
    
}
