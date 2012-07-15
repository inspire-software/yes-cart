/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
