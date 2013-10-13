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
 * CustomerOrderDeliveryDet represent an sku quantity and price  in particular
 * shipment. Currency can be obtained from order.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerOrderDeliveryDet extends Auditable, CartItem {

    /**
     * Get pk value.
     *
     * @return pk value.
     */
    long getCustomerOrderDeliveryDetId();

    /**
     * Set pk value.
     *
     * @param customerorderdeliveryId pk value
     */
    void setCustomerOrderDeliveryDetId(long customerorderdeliveryId);


    /**
     * Get quantity of sku.
     *
     * @return quantity of sku.
     */
    BigDecimal getQty();

    /**
     * Set  quantity of sku.
     *
     * @param qty quantity of sku.
     */
    void setQty(BigDecimal qty);

    /**
     * Get SKU code for item purchased.
     *
     * @return SKU code of purchased item
     */
    String getProductSkuCode();

    /**
     * Set SKU code for item purchased.
     *
     * @param skuCode SKU code of purchased item
     */
    void setProductSkuCode(String skuCode);

    /**
     * Get product name in CustomerOrder.locale.
     *
     * @return copy of product name
     */
    String getProductName();

    /**
     * Set product name in CustomerOrder.locale.
     *
     * @param productName copy of product name
     */
    void setProductName(String productName);

    /**
     * Get order delivery.
     *
     * @return delivery
     */
    CustomerOrderDelivery getDelivery();

    /**
     * Set delivery.
     *
     * @param delivery delivery.
     */
    void setDelivery(CustomerOrderDelivery delivery);

    /**
     * Get sku price for this particular order.
     *
     * @return price.
     */
    BigDecimal getPrice();

    /**
     * Set Price.
     *
     * @param price price to set.
     */
    void setPrice(BigDecimal price);

    /**
     * Get list / catalog price.
     * @return price
     */
    BigDecimal getListPrice();

    /**
     * Set list (regular/catalog) price.
     * @param listPrice to set.
     */
    void setListPrice( BigDecimal listPrice);

}