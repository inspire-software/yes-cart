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

package org.yes.cart.shoppingcart;

import java.io.Serializable;
import java.util.List;

/**
 * Responsible to hold shopping context data like viewed products and categories, security context, geo data and
 * other stuff
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 12-May-2011
 * Time: 10:31:36
 */
public interface ShoppingContext extends Serializable {

    /**
     * Get customer email.
     * @return customer email.
     */
    String getCustomerEmail();

    /**
     * Set customer email.
     * @param customerEmail customer email.
     */
    void setCustomerEmail(String customerEmail);

    /**
     * Get customer name.
     *
     * @return customer name or null if customer is anonymous
     */
    String getCustomerName();

    /**
     * Set customer name.
     *
     * @param customerName customer name.
     */
    void setCustomerName(String customerName);

    /**
     * Get customer active shops.
     *
     * @return customer active shops
     */
    List<String> getCustomerShops();

    /**
     * Set customer active shops.
     *
     * @param shops customer active shops
     */
    void setCustomerShops(List<String> shops);

    /**
     * Get current shop id
     *
     * @return current shop id.
     */
    long getShopId();

    /**
     * Set current shop id.
     *
     * @param shopId current shop id.
     */
    void setShopId(long shopId);

    /**
     * Get current shop code
     *
     * @return current shop id.
     */
    String getShopCode();

    /**
     * Set current shop code.
     *
     * @param shopCode current shop code.
     */
    void setShopCode(String shopCode);


    /**
     * Get shopper ip address
     *
     * TODO: YC-361
     *
     * @return customer's IP
     */
    String getResolvedIp();

    /**
     * Set shopper ip address.
     *
     * TODO: YC-361
     *
     * @param resolvedIp resolved ip address.
     */
    void setResolvedIp(String resolvedIp);


    /**
     * Get last viewed sku codes.
     *
     * @return comma separated string of viewed skus.
     */
    List<String> getLatestViewedSkus();

    /**
     * Set latest viewed sku codes.
     *
     * @param latestViewedSkus latest viewed skus.
     */
    void setLatestViewedSkus(List<String> latestViewedSkus);


    /**
     * Get last viewed categories.
     *
     * TODO: YC-360
     *
     * @return comma separated string of category ids.
     */
    List<String> getLatestViewedCategories();

    /**
     * Set last viewed categories.
     *
     * TODO: YC-360
     *
     * @param latestViewedCategories comma separated list of category ids.
     */
    void setLatestViewedCategories(List<String> latestViewedCategories);


    /**
     * Clear context.
     */
    void clearContext();

}
