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

import org.springframework.security.core.context.SecurityContext;

import java.io.Serializable;

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



    /** Get shopper ip address */
    String getResolvedIp();

    /**
     * Set shopper ip address  .
     * @param resolvedIp resolved ip address.
     */
    void setResolvedIp(String resolvedIp);

    /**
     * Get lastest viewed sku codes.
     *
     * @return comma separated string of viewed skus.
     */
    String getLatestViewedSkus();

    /**
     * Set latest viewed sku codes.
     *
     * @param latestViewedSkus latest viewed skus.
     */
    void setLatestViewedSkus(String latestViewedSkus);


    /**
     * Get lastest viewed categories.
     *
     * @return comma separated string of category ids.
     */
    String getLatestViewedCategories();

    /**
     * Get lastest viewed categories.
     *
     * @param latestViewedCategories comma separated list of category ids.
     */
    void setLatestViewedCategories(String latestViewedCategories);

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
     * Clear context.
     */
    void clearContext();

    /* private long currentCategoryId;

    private BooleanQuery apppliedQuery;

    private Date currentDate;*/


}
