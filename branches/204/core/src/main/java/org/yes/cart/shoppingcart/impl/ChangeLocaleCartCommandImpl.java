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
 * Date: 6/25/11
 * Time: 11:37 AM
 */
public class ChangeLocaleCartCommandImpl  extends AbstractCartCommandImpl {

    private static final long serialVersionUID = 20110625L;

    public final static String CMD_KEY = "changeLocaleCmd";

    private final String locale;

    /** {@inheritDoc} */
    public String getCmdKey() {
        return CMD_KEY;
    }

    /**
     * Construct command to change shopping cart locale.
     * @param applicationContext  application context.
     * @param parameters  command parameters.
     */
    public ChangeLocaleCartCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        //super(applicationContext, parameters);
        locale = (String) parameters.get(getCmdKey());
    }


    /** {@inheritDoc} */
    public void execute(final ShoppingCart shoppingCart) {
        if (locale != null) {
            ((ShoppingCartImpl)shoppingCart).setCurrentLocale(locale);
            setModifiedDate(shoppingCart);
        }
    }
}