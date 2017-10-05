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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.IntegerType;

import java.util.Collection;
import java.util.Collections;

/**
 * User: denispavlov
 * Date: 03/10/2017
 * Time: 15:01
 */
public final class HQLUtils {

    private HQLUtils() {
        // no instance
    }

    /**
     * Convert value to correct format for ilike match anywhere mode.
     *
     * @param value raw value
     *
     * @return null or match anywhere
     */
    public static String criteriaIlikeAnywhere(final String value) {
        if (StringUtils.isBlank(value)) {
            return null; // no value
        }
        return "%" + value.toLowerCase() + "%";
    }


    /**
     * Convert value to correct format for like match anywhere mode.
     *
     * @param value raw value
     *
     * @return null or match anywhere
     */
    public static String criteriaLikeAnywhere(final String value) {
        if (StringUtils.isBlank(value)) {
            return null; // no value
        }
        return "%" + value + "%";
    }


    /**
     * Convert value to correct format for case insensitive eq.
     *
     * @param value raw value
     *
     * @return null or match anywhere
     */
    public static String criteriaIeq(final String value) {
        if (StringUtils.isBlank(value)) {
            return null; // no value
        }
        return value.toLowerCase();
    }

    /**
     * Convert value to correct format for case insensitive eq.
     *
     * @param value raw value
     *
     * @return null or match anywhere
     */
    public static String criteriaEq(final String value) {
        if (StringUtils.isBlank(value)) {
            return null; // no value
        }
        return value;
    }

    /**
     * Create a test value to check if collection is empty or not.
     *
     * First part of check in form of: ?1 = 0 or e.x in (?2)
     * Sets ?1 to null or "1"
     *
     * @param value raw value
     *
     * @return null or match anywhere
     */
    public static Object criteriaInTest(final Collection value) {
        if (CollectionUtils.isEmpty(value)) {
            return new TypedParameterValue(IntegerType.INSTANCE, 0); // no value
        }
        return new TypedParameterValue(IntegerType.INSTANCE, 1);
    }

    /**
     * Create a test value to check if collection is empty or not.
     *
     * Second part of check in form of: ?1 = 0 or e.x in (?2)
     * Sets ?2 to ["x"] or collection
     *
     * @param value raw value
     *
     * @return null or match anywhere
     */
    public static Collection criteriaIn(final Collection value) {
        if (CollectionUtils.isEmpty(value)) {
            return Collections.singletonList("x"); // no value
        }
        return value;
    }
}
