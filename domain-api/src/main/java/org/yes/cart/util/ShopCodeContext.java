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

package org.yes.cart.util;


import org.slf4j.MDC;

/**
 *
 * Hold current shop code context.
 *
 */
public final class ShopCodeContext {

    private static final ThreadLocal<String> SHOP_CODE = new ThreadLocal<>();
    private static final ThreadLocal<Long> SHOP_ID = new ThreadLocal<>();

    private ShopCodeContext() {
        // no instance
    }

    /**
     * Get current shop code.
     *
     * @return current shop code.
     */
    public static String getShopCode() {
        final String code = SHOP_CODE.get();
        if (code == null) {
            return "DEFAULT";
        }
        return code;
    }

    /**
     * Set shop code.
     *
     * @param currentShopCode shop code to set.
     */
    public static void setShopCode(final String currentShopCode) {
        SHOP_CODE.set(currentShopCode);
        MDC.put("shopCode", getShopCode());
    }

    /**
     * Get current shop Id
     *
     * @return current shop id
     */
    public static Long getShopId() {
        final Long code = SHOP_ID.get();
        if (code == null) {
            return 0L;
        }
        return code;
    }

    /**
     * Set shop Id.
     *
     * @param currentShopId shop Id to set.
     */
    public static void setShopId(final long currentShopId) {
        SHOP_ID.set(currentShopId);
    }


    /**
     * Clear thread locals at the end of the request
     */
    public static void clear() {
        SHOP_ID.set(0L);
        SHOP_CODE.set("DEFAULT");
        MDC.put("shopCode", getShopCode());
    }

    /**
     * Explicitly remove thread locals to prevent memory leaks.
     */
    public static  void destroy() {
        SHOP_ID.remove();
        SHOP_CODE.remove();
    }


}
