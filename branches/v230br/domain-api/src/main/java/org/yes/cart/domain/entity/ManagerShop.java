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
 * Represent relation between manager and shop to support federated environment.
 */
public interface ManagerShop extends Guidable {

    /**
     * Get pk;
     *
     * @return pk value
     */
    long getManagerShopId();

    /**
     * Set pk value.
     *
     * @param managerShopId pk value.
     */
    void setManagerShopId(long managerShopId);

    /**
     * Get Manager.
     *
     * @return manager.
     */
    Manager getManager();

    /**
     * Set Manager
     *
     * @param manager manager.
     */
    void setManager(Manager manager);

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


