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

package org.yes.cart.shoppingcart.impl;

import org.springframework.context.ApplicationContext;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * Remove one sku from cart.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class RemoveSkuFromCartCommandImpl extends AbstractSkuCartCommandImpl{

    private static final long serialVersionUID = 20100313L;

    public static final String CMD_KEY = "removeOneSkuCmd";


    /** {@inheritDoc} */
    public String getCmdKey() {
        return CMD_KEY;
    }

   /**
     *
     * @param applicationContext application context
     * @param parameters page parameters
     */
    public RemoveSkuFromCartCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super(applicationContext, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final ShoppingCart shoppingCart) {
        if (getProductSkuDTO() != null) {
            final String skuCode = getProductSkuDTO().getCode();
            if(!shoppingCart.removeCartItemQuantity(getProductSkuDTO(), BigDecimal.ONE)) {
                ShopCodeContext.getLog(this).warn("Can not remove one sku with code {} from cart",
                        skuCode);
            }

            recalculatePrice(shoppingCart);
            setModifiedDate(shoppingCart);
        }
    }



}
