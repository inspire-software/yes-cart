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

package org.yes.cart.shoppingcart;


import java.io.Serializable;
import java.util.Map;

/**
 * .
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:07:03 PM
 */
public interface ShoppingCartCommand extends Serializable {

    String CMD_ADDTOCART = "addToCartCmd";
    String CMD_ADDTOCART_P_QTY = "qty";
    String CMD_REMOVEALLSKU = "removeAllSkuCmd";
    String CMD_REMOVEONESKU = "removeOneSkuCmd";
    String CMD_SETQTYSKU = "setQuantityToCartCmd";
    String CMD_SETQTYSKU_P_QTY = "qty";

    String CMD_SEPARATEBILLING = "setBillingAddressSeparateCmd";
    String CMD_SETCARRIERSLA = "setCarrierSlaCmd";
    String CMD_MULTIPLEDELIVERY = "setMultipleDeliveryCmd";
    String CMD_SETPGLABEL = "setPgLabelCmd";
    String CMD_SETSHOP = "setShopIdCmd";

    String CMD_CHANGECURRENCY = "changeCurrencyCmd";
    String CMD_CHANGELOCALE = "changeLocaleCmd";

    String CMD_CLEAN = "cleanCartCmd";
    String CMD_EXPIRE = "expireCartCmd";
    String CMD_LOGIN = "loginCmd";
    String CMD_LOGIN_P_EMAIL = "email";
    String CMD_LOGIN_P_NAME = "name";
    String CMD_LOGOUT = "logoutCmd";


    /**
     * Execute command on shopping cart to perform changes.
     *
     * @param shoppingCart the shopping cart
     * @param parameters parameters
     */
    void execute(ShoppingCart shoppingCart, Map<String, Object> parameters);

    /**
     * @return command key
     */
    String getCmdKey();

}
