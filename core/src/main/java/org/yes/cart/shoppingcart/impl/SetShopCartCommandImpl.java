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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.MutableShoppingContext;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Arrays;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 22-May-2011
 * Time: 14:12:54
 */
public class SetShopCartCommandImpl  extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 2010522L;

    private final ShopService shopService;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     * @param shopService shop service
     */
    public SetShopCartCommandImpl(final ShoppingCartCommandRegistry registry,
                                  final ShopService shopService) {
        super(registry);
        this.shopService = shopService;
    }

    /**
     * @return command key
     */
    public String getCmdKey() {
        return CMD_SETSHOP;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {
            final Long value = NumberUtils.createLong(String.valueOf(parameters.get(getCmdKey())));
            if (value != null && !value.equals(shoppingCart.getShoppingContext().getShopId())) {

                final Shop shop = shopService.getById(value);

                final MutableShoppingContext ctx = shoppingCart.getShoppingContext();
                ctx.setShopId(shop.getShopId());
                ctx.setShopCode(shop.getCode());

                setDefaultTaxOptions(shop, ctx);

                markDirty(shoppingCart);
            }
        }
    }

    protected void setDefaultTaxOptions(final Shop shop, final MutableShoppingContext ctx) {

        boolean showTax = Boolean.valueOf(shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO));
        if (showTax) {
            // If types limit is set then only enable showTax option for given types. Anonymous type is B2G
            final String types = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CUSTOMER_TYPES);
            showTax = StringUtils.isBlank(types) || Arrays.asList(StringUtils.split(types, ',')).contains("B2G");
        }
        final boolean showTaxNet = showTax && Boolean.valueOf(shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET));
        final boolean showTaxAmount = showTax && Boolean.valueOf(shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT));

        ctx.setTaxInfoChangeViewEnabled(false);
        final String typesThatCanChangeView = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE_TYPES);
        // Ensure change view is allowed for anonymous
        if (StringUtils.isNotBlank(typesThatCanChangeView)) {
            final String[] customerTypesThatCanChangeView = StringUtils.split(typesThatCanChangeView, ',');
            if (Arrays.asList(customerTypesThatCanChangeView).contains("B2G")) {
                ctx.setTaxInfoChangeViewEnabled(true);
            }
        }

        ctx.setTaxInfoEnabled(showTax);
        ctx.setTaxInfoUseNet(showTaxNet);
        ctx.setTaxInfoShowAmount(showTaxAmount);

    }

}
