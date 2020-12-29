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

package org.yes.cart.domain.dto;

/**
 * User: inspiresoftware
 * Date: 07/10/2020
 * Time: 17:04
 */
public interface CustomerShopDTO extends AuditInfoDTO {

    /**
     * Get pk;
     *
     * @return pk value
     */
    long getCustomerShopId();

    /**
     * Set pk value.
     *
     * @param customerShopId pk value.
     */
    void setCustomerShopId(long customerShopId);


    /**
     * Get customer id .
     *
     * @return customer id
     */
    long getCustomerId();


    /**
     * Set customer id.
     *
     * @param customerId customer id
     */
    void setCustomerId(long customerId);

    /**
     * @return shop id
     */
    long getShopId();

    /**
     * @param shopId shop id
     */
    void setShopId(long shopId);

    /**
     * Disable this customer in shop.
     *
     * @return true if this is disabled
     */
    boolean isDisabled();

    /**
     * Disable this customer in shop.
     *
     * @param disabled true if this is disabled
     */
    void setDisabled(boolean disabled);

}


