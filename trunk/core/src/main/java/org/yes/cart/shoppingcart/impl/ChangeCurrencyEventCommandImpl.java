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

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ChangeCurrencyEventCommandImpl extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20101702L;

    public static final String CMD_KEY = "changeCurrencyCmd";

    private final String currencyCode;

    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return CMD_KEY;
    }


    public ChangeCurrencyEventCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super(applicationContext, parameters);
        currencyCode = (String) parameters.get(CMD_KEY);
    }


    /**
     * {@inheritDoc}
     */
    public void execute(final ShoppingCart shoppingCart) {
        if (currencyCode != null) {
            ((ShoppingCartImpl) shoppingCart).setCurrencyCode(currencyCode);
            recalculatePrice(shoppingCart);
            setModifiedDate(shoppingCart);
        }
    }
}
