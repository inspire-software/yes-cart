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

package org.yes.cart.domain.entity;

/**
 * Represent relation between carrier and shop to support federated environment.
 */
public interface CarrierShop extends Guidable {

    /**
     * Get pk;
     *
     * @return pk value
     */
    long getCarrierShopId();

    /**
     * Set pk value.
     *
     * @param carrierShopId pk value.
     */
    void setCarrierShopId(long carrierShopId);

    /**
     * Get Carrier.
     *
     * @return manager.
     */
    Carrier getCarrier();

    /**
     * Set Carrier
     *
     * @param manager manager.
     */
    void setCarrier(Carrier manager);

    /**
     * Get shop.
     *
     * @return shop.
     */
    Shop getShop();

    /**
     * Set the shop.
     *
     * @param shop shop
     */
    void setShop(Shop shop);


}


