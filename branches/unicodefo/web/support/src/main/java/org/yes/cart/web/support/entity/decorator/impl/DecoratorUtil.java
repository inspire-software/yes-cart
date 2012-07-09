package org.yes.cart.web.support.entity.decorator.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.Seo;

/**
 * User: igora Igor Azarny
 * Date: 6/19/12
 * Time: 4:38 PM
 */
public class DecoratorUtil {

    /**
     * Seo endcode id helper function, which retur seo uri if possible instead of id.
     * @param idValueToEncode gicen id
     * @param seo given seo
     * @return seo uri
     */
    public static String encodeId(final String idValueToEncode, final Seo seo) {
        if (seo != null && StringUtils.isNotBlank(seo.getUri())) {
            return seo.getUri();
        } else {
            return idValueToEncode;
        }
    }

}
