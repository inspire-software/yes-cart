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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Hold current shop code context.
 *
 */
public class ShopCodeContext {

    private static ThreadLocal<String> shopCode = new ThreadLocal<String>();
    private static ThreadLocal<Long> shopId = new ThreadLocal<Long>();

    private static final Map<String, Logger> LOGS = new ConcurrentHashMap<String, Logger>();

    /**
     * Get current shop code.
     *
     * @return current shop code.
     */
    public static String getShopCode() {
        if (shopCode.get() == null) {
            shopCode.set("DEFAULT");
        }
        return shopCode.get();
    }

    /**
     * Set shop code.
     *
     * @param currentShopCode shop code to set.
     */
    public static void setShopCode(final String currentShopCode) {
        shopCode.set(currentShopCode);
    }

    /**
     * Get current shop Id
     *
     * @return current shop id
     */
    public static Long getShopId() {
        return shopId.get();
    }

    /**
     * Set shop Id.
     *
     * @param currentShopId shop Id to set.
     */
    public static void setShopId(final long currentShopId) {
        shopId.set(currentShopId);
    }


    /**
     * This is a faster way to get loggers since we keep references in a concurrent
     * hash map as opposed to synchronised look ups offered by slf4j API.
     *
     * @return get code specific logger to separate shop streams in
     *         multistore environment
     * @param context calling object
     */
    public static Logger getLog(final Object context) {
        final String code = getLogKey(context);
        if (!LOGS.containsKey(code)) {
            final Logger log = LoggerFactory.getLogger(code);
            LOGS.put(code, log);
            return log;
        }
        return LOGS.get(code);
    }

    static String getLogKey(final Object context) {
        // 1m calls == 0.547s Intel 64 mac Hot Spot 1_6_22
        // return new StringBuilder(getShopCode()).append('.').append(context.getClass().getCanonicalName()).toString();

        // 1m calls == 0.48s Intel 64 mac Hot Spot 1_6_22
        return getShopCode().concat(".").concat(context.getClass().getName());
    }

}
