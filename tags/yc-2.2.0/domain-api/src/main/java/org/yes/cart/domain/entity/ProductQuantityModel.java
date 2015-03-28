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

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 25/10/2014
 * Time: 11:42
 */
public interface ProductQuantityModel {

    /**
     * Returns true if we can order less one step of this item
     * (i.e. we will not yet reach min by going one step down)
     *
     * @return can order less flag
     */
    boolean canOrderLess();

    /**
     * Returns true if we can order more one step of this item
     * (i.e. we will not yet reach max by going one step up)
     *
     * @return can order less flag
     */
    boolean canOrderMore();

    /**
     * Is min setting defined for this product.
     *
     * @return true if product min order quantity is not null or 0
     */
    boolean hasMin();

    /**
     * Is max setting defined for this product.
     *
     * @return true if product max order quantity is not null or Integer.MAX
     */
    boolean hasMax();

    /**
     * Is step setting defined for this product.
     *
     * @return true if product step order quantity is not null or 0
     */
    boolean hasStep();

    /**
     * Does this product have any order quantity settings?
     *
     * @return true if any of the settings is provided
     */
    boolean hasMinMaxStep();

    /**
     * Valid min quantity, if none specified use 1.
     *
     * @return valid min quantity
     */
    BigDecimal getMin();

    /**
     * Valid min quantity excluding items in cart.
     *
     * @return valid min order quantity
     */
    BigDecimal getMinOrder();

    /**
     * Valid max quantity, if none specified use Integer.MAX.
     *
     * @return valid max quantity
     */
    BigDecimal getMax();

    /**
     * Valid max quantity excluding items in cart.
     *
     * @return valid max order quantity
     */
    BigDecimal getMaxOrder();

    /**
     * Valid step quantity, if none specified use 1.
     *
     * @return valid step quantity
     */
    BigDecimal getStep();

    /**
     * Current number of items in cart (excluding gifts).
     *
     * @return cart quantity for this SKU.
     */
    BigDecimal getCartQty();

    /**
     * Determine valid add quantity with respect to step and max.
     *
     * @param addQty desired quantity to add
     *
     * @return valid quantity to add
     */
    BigDecimal getValidAddQty(BigDecimal addQty);

    /**
     * Determine valid add quantity with respect to step and min.
     *
     * @param remQty desired quantity to remove
     *
     * @return valid quantity to remove
     */
    BigDecimal getValidRemoveQty(BigDecimal remQty);

    /**
     * Determine valid add quantity with respect to step, min and max.
     *
     * @param qty new quantity to set in cart
     *
     * @return valid quantity to set
     */
    BigDecimal getValidSetQty(BigDecimal qty);

}
