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
 * User: denispavlov
 * Date: 21/08/2014
 * Time: 20:50
 */
public interface ShoppingCartState extends Auditable {

    /**
     * Serialized shopping cart state.
     *
     * @return cart
     */
    byte[] getState();

    /**
     * Serialized shopping cart state.
     *
     * @param state cart state
     */
    void setState(byte[] state);

    /**
     * Customer email (or null for anonymous).
     *
     * @return email
     */
    String getCustomerEmail();

    /**
     * Customer email (or null for anonymous).
     *
     * @param customerEmail email
     */
    void setCustomerEmail(String customerEmail);

    /**
     * Get pk value.
     *
     * @return pk value.
     */
    long getShoppingCartStateId();

    /**
     * Set pk value.
     *
     * @param shopId shop pk value.
     */
    void setShoppingCartStateId(long shopId);


}
