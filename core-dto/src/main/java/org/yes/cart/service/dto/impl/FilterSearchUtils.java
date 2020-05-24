/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.dto.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.List;

/**
 * User: denispavlov
 * Date: 06/01/2020
 * Time: 08:27
 */
public class FilterSearchUtils {

    private FilterSearchUtils() {
        // no instance
    }

    /**
     * Get single String value filter
     *
     * @param filterParam    reduced filter parameter from search context
     *
     * @return trimmed string value or null (black also returns null)
     */
    public static String getStringFilter(List filterParam) {
        if (CollectionUtils.isNotEmpty(filterParam) && filterParam.get(0) instanceof String && StringUtils.isNotBlank((String) filterParam.get(0))) {
            return ((String) filterParam.get(0)).trim();
        }
        return null;
    }

    /**
     * Get single long value filter
     *
     * @param filterParam    reduced filter parameter from search context
     *
     * @return trimmed string value or null (black also returns null)
     */
    public static long getIdFilter(List filterParam) {
        if (CollectionUtils.isNotEmpty(filterParam) && filterParam.get(0) instanceof Number) {
            return ((Number) filterParam.get(0)).longValue();
        }
        return NumberUtils.toLong(getStringFilter(filterParam));
    }

}
