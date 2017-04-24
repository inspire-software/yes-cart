/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.MutableShoppingContext;

/**
 * User: denispavlov
 * Date: 24/01/2017
 * Time: 07:49
 */
public class ShoppingCartCommandConfigurationTaxOptionsVisitorImpl extends ShoppingCartCommandConfigurationCustomerTypeVisitorImpl {

    public ShoppingCartCommandConfigurationTaxOptionsVisitorImpl(final CustomerService customerService,
                                                                 final ShopService shopService) {
        super(customerService, shopService);
    }


    @Override
    public void visit(final MutableShoppingCart cart, final Object... args) {

        if (args == null || args.length != 3) {
            throw new IllegalArgumentException(
                    "This visitor requires 3 parameters (Boolean showTaxOption, Boolean showNetOption, Boolean showAmountOption), to use default provide 'null'");
        }

        final Boolean showTaxOption = (Boolean) args[0];
        final Boolean showNetOption = (Boolean) args[1];
        final Boolean showAmountOption = (Boolean) args[2];

        final Shop shop = determineShop(cart);

        // Resolve type. Anonymous type is B2G, blank is B2C
        final String customerType = ensureCustomerType(cart);
        boolean showTax = shop.isSfShowTaxInfo(customerType);
        boolean showTaxNet = showTax && shop.isSfShowTaxNet(customerType);
        boolean showTaxAmount = showTax && shop.isSfShowTaxAmount(customerType);

        final MutableShoppingContext ctx = cart.getShoppingContext();

        ctx.setTaxInfoChangeViewEnabled(shop.isSfShowTaxOptions(customerType));

        if (ctx.isTaxInfoChangeViewEnabled()) {
            showTax = showTaxOption != null ? showTaxOption : showTax;
            showTaxNet = showNetOption == null ? showTax && showTaxNet : showTax && showNetOption;
            showTaxAmount = showAmountOption == null ? showTax && showTaxAmount : showTax && showAmountOption;
        }

        ctx.setTaxInfoEnabled(showTax);
        ctx.setTaxInfoUseNet(showTaxNet);
        ctx.setTaxInfoShowAmount(showTaxAmount);


    }
}
