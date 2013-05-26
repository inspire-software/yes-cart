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

import org.yes.cart.shoppingcart.CartItem;

import java.math.BigDecimal;


/**
 * Represent sku in order with quantity and price in order currency.
 * At this moment prices in shopiing cart items list and here without
 * catalog promotions. TODOV2 add catalog promotion sku price modificators
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerOrderDet extends Auditable, CartItem {

    /**
     * Get order detail pk value.
     *
     * @return detail pk value
     */
    long getCustomerOrderDetId();

    /**
     * Set detail pk value
     *
     * @param customerOrderDetId set pk value.
     */
    void setCustomerOrderDetId(long customerOrderDetId);

    /**
     * Get quantity of skus.
     *
     * @return set quantity of skus.
     */
    BigDecimal getQty();

    /**
     * Set quantity of skus.
     *
     * @param qty quantity of skus.
     */
    void setQty(BigDecimal qty);

    /**
     * Get the price.
     *
     * @return price per single sku.
     */
    BigDecimal getPrice();

    /**
     * Set single sku price.
     *
     * @param price single sku price.
     */
    void setPrice(BigDecimal price);

    /**
     * Get list / catalog price.
     * @return list price
     */
    BigDecimal getListPrice();


    /**
     * Set list price.
     * @param listPrice   list price
     */
    void setListPrice(BigDecimal listPrice);


    /**
     * Concrete product sku in cart.
     *
     * @return {@link ProductSku} product sku.
     */
    ProductSku getSku();

    /**
     * Set {@link ProductSku}.
     *
     * @param sku product sku
     */
    void setSku(ProductSku sku);

    /**
     * @return
     */
    CustomerOrder getCustomerOrder();

    /**
     * @param customerOrder
     */
    void setCustomerOrder(CustomerOrder customerOrder);

}


