package org.yes.cart.web.service.ws.client;

import org.yes.cart.service.async.model.AsyncContext;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 12/08/2016
 * Time: 21:52
 */
public interface AsyncContextFactory {

    /**
     * Create async context for current runtime.
     *
     * @return async context
     */
    AsyncContext getInstance();

    /**
     * Create async context for current runtime.
     *
     * @param attributes additional context attributes
     *
     * @return async context
     */
    AsyncContext getInstance(Map<String, Object> attributes);

}
