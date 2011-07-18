package org.yes.cart.web.util;

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/15/11
 * Time: 8:40 AM
 */
public class SmartPageParameters  extends PageParameters {

    /** {@inheritDoc} */
    public SmartPageParameters(final PageParameters copy) {
        super(copy);
    }

    /** {@inheritDoc} */
    public SmartPageParameters(final String keyValuePairs) {
        super(keyValuePairs);
    }

    /** {@inheritDoc} */
    public SmartPageParameters(final String keyValuePairs, final String delimiter) {
        super(keyValuePairs, delimiter);
    }


    /**
     *
     * Put parameter pair into page parameters.  Behavior the same as map#put , but with additional check - value will bre replaced in vase if
     * key present in parameters. Value replacement instead remove / add pattern need to not brake parameters sequence.
     * @param name key
     * @param value value
     * @return self
     */
    public SmartPageParameters put(final String name, final Object value) {
        if (getNamedKeys().contains(name)) {
            //TODO wait for WICKET-3906 resolution
            set(name, value);
        } else {
            put(name, value);
        }
        return this; //super.add(name, value);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
