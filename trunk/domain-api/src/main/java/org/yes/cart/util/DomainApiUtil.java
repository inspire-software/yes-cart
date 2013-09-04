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

package org.yes.cart.util;

import org.yes.cart.domain.entity.AttrValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 * <p/>
 * Util class for domain objects.
 */
public class DomainApiUtil {

    /**
     * Get the value of attribute from attribute value map.
     *
     * @param attrName attribute name
     * @param values   map of attribute name and {@link AttrValue}
     * @return null if attribute not present in map, otherwise value of attribute
     */
    public static String getAttirbuteValue(final String attrName, final Map<String, AttrValue> values) {
        AttrValue attrValue = values.get(attrName);
        if (attrValue != null) {
            return attrValue.getVal();
        }
        return null;
    }

    /**
     * Get attribute value
     *
     * @param attrName   attribute name
     * @param attributes collection of attribute
     * @return value if fount otherwise null
     */
    public static String getAttirbuteValue(final String attrName, final Collection<? extends AttrValue> attributes) {
        for (AttrValue attrValue : attributes) {
            if (attrName.equals(attrValue.getAttribute().getName())) {
                return attrValue.getVal();
            }
        }
        return null;
    }

    private DomainApiUtil() {
        
    }
}
