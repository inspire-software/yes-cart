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

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Default implementation of the add to cart visitor.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:17:10 PM
 */
public class AddSkuToCartEventCommandImpl extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20100122L;

    public static final String CMD_KEY = "addToCartCmd";
    public static final String CMD_QTY_KEY = "addToCartCmdQty";

    private final BigDecimal quantity;

    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return CMD_KEY;
    }

    /**
     * Construct command.
     * @param applicationContext    application context.
     * @param parameters            command parameters.
     */
    public AddSkuToCartEventCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super(applicationContext, parameters);
        final Object strQty = parameters.get(CMD_QTY_KEY);

        BigDecimal qty = BigDecimal.ONE;
        if (strQty instanceof String) {
            try {
                qty = new BigDecimal((String) strQty);
            } catch (Exception exp) {
                ShopCodeContext.getLog(this).error("Invalid quantity in add to cart command", exp);
            }
        }
        quantity = MoneyUtils.isFirstBiggerThanSecond(qty, BigDecimal.ZERO) ? qty : BigDecimal.ONE;
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final ShoppingCart shoppingCart) {
        if (getProductSkuDTO() != null) {
            shoppingCart.addProductSkuToCart(getProductSkuDTO(), quantity);
            recalculatePrice(shoppingCart);
            setModifiedDate(shoppingCart);
            final Logger log = ShopCodeContext.getLog(this);
            if (log.isDebugEnabled()) {
                log.debug("Added one item of sku code {} to cart",
                        getProductSkuDTO().getCode());
            }
        }
    }
}
