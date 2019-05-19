/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.utils;

import org.yes.cart.domain.entity.AttrValue;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 * <p/>
 * Util class for domain objects.
 */
public final class DomainApiUtils {

    private DomainApiUtils() {
        // no instance
    }

    /**
     * Get the value of attribute from attribute value map.
     *
     * @param attrCode attribute name
     * @param values   map of attribute name and {@link AttrValue}
     * @return null if attribute not present in map, otherwise value of attribute
     */
    public static String getAttributeValue(final String attrCode, final Map<String, AttrValue> values) {
        AttrValue attrValue = values.get(attrCode);
        if (attrValue != null) {
            return attrValue.getVal();
        }
        return null;
    }

    /**
     * Get attribute value
     *
     * @param attrCode   attribute name
     * @param attributes collection of attribute
     * @return value if found otherwise null
     */
    public static String getAttributeValue(final String attrCode, final Collection<? extends AttrValue> attributes) {
        for (AttrValue attrValue : attributes) {
            if (attrCode.equals(attrValue.getAttributeCode())) {
                return attrValue.getVal();
            }
        }
        return null;
    }

    /**
     * Check availability of an object.
     *
     * @param enabled flag for on/off switch
     * @param availableFrom availability start (or null)
     * @param availableTo availability finish (or null)
     * @param now time now
     *
     * @return true if object is available given provided settings
     */
    public static boolean isObjectAvailableNow(final boolean enabled,
                                               final LocalDateTime availableFrom,
                                               final LocalDateTime availableTo,
                                               final LocalDateTime now) {

        if (!enabled) {
            return false;
        }

        if (availableFrom != null && now.isBefore(availableFrom)) {
            return false;
        }

        if (availableTo != null && now.isAfter(availableTo)) {
            return false;
        }

        return true;

    }

}
