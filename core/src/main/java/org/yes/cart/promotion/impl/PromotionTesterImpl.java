/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.promotion.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.promotion.PromotionContextFactory;
import org.yes.cart.promotion.PromotionTester;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.utils.TimeContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 10/04/2018
 * Time: 18:55
 */
public class PromotionTesterImpl implements PromotionTester {

    private final ShopService shopService;
    private final PriceService priceService;
    private final PromotionContextFactory factory;
    private final CarrierSlaService carrierSlaService;

    private final AmountCalculationStrategy calculationStrategy;
    private final ShoppingCartCommandFactory cartCommandFactory;

    public PromotionTesterImpl(final ShopService shopService,
                               final PriceService priceService,
                               final PromotionContextFactory factory,
                               final CarrierSlaService carrierSlaService,
                               final AmountCalculationStrategy calculationStrategy,
                               final ShoppingCartCommandFactory cartCommandFactory) {
        this.shopService = shopService;
        this.priceService = priceService;
        this.factory = factory;
        this.carrierSlaService = carrierSlaService;
        this.calculationStrategy = calculationStrategy;
        this.cartCommandFactory = cartCommandFactory;
    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart testPromotions(final String shopCode,
                                       final String currency,
                                       final String language,
                                       final String customer,
                                       final String supplier,
                                       final Map<String, BigDecimal> products,
                                       final String shipping,
                                       final List<String> coupons,
                                       final Instant time) {

        boolean setTime = time != null;
        try {
            if (setTime) {
                TimeContext.setTime(time);
            }

            final Shop shop = this.shopService.getShopByCode(shopCode);

            ensureNoCache(shop, currency);

            final ShoppingCartImpl cart = new ShoppingCartImpl();
            cart.initialise(calculationStrategy);

            prepareDefaults(cart, shop.getMaster() != null ? shop.getMaster() : shop, currency, language);
            loginCustomer(cart, customer);
            addProductsToCart(cart, supplier, products);
            addCouponsToCart(cart, coupons);
            prepareShipping(cart);
            setShippingMethod(cart, shipping);

            return cart;

        } finally {
            if (setTime) {
                TimeContext.clear();
            }
        }
    }

    private void ensureNoCache(final Shop shop, final String currency) {
        // Ensure no cache
        factory.refresh(shop.getCode(), currency);
        if (shop.getMaster() != null) {
            factory.refresh(shop.getMaster().getCode(), currency);
        }
    }

    private void prepareDefaults(final ShoppingCart cart, final Shop shop, final String currency, final String language) {

        final Map<String, Object> params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_SETSHOP, shop.getShopId());
        params.put(ShoppingCartCommand.CMD_CHANGECURRENCY, currency);
        params.put(ShoppingCartCommand.CMD_CHANGELOCALE, language);
        params.put(ShoppingCartCommand.CMD_INTERNAL_SETIP, "127.0.0.1");
        cartCommandFactory.execute(cart, params);

    }

    private void loginCustomer(final ShoppingCart cart, final String customer) {

        if (StringUtils.isNotBlank(customer)) {
            final Map<String, Object> params = new HashMap<>();
            params.put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, customer);
            params.put(ShoppingCartCommand.CMD_LOGIN_P_PASS, "promotest");
            params.put("promoTestLoginCmd", "1");
            cartCommandFactory.execute("promoTestLoginCmd", cart, params);
        }

    }

    private void addProductsToCart(final ShoppingCart cart, final String supplier, final Map<String, BigDecimal> products) {

        if (products != null) {
            for (final Map.Entry<String, BigDecimal> product : products.entrySet()) {
                final Map<String, Object> params = new HashMap<>();
                params.put(ShoppingCartCommand.CMD_ADDTOCART, product.getKey());
                if (product.getValue() != null) {
                    if (StringUtils.isNotBlank(supplier)) {
                        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, supplier);
                    }
                    params.put(ShoppingCartCommand.CMD_P_QTY, product.getValue().toPlainString());
                }
                cartCommandFactory.execute(ShoppingCartCommand.CMD_ADDTOCART, cart, params);
            }
        }
    }

    private void addCouponsToCart(final ShoppingCart cart, final List<String> coupons) {

        if (coupons != null) {
            for (final String coupon : coupons) {
                final Map<String, Object> params = new HashMap<>();
                params.put(ShoppingCartCommand.CMD_ADDCOUPON, coupon);
                cartCommandFactory.execute(ShoppingCartCommand.CMD_ADDCOUPON, cart, params);
            }
        }
    }

    private void prepareShipping(final ShoppingCart cart) {

        final Map<String, Object> params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_SPLITCARTITEMS, ShoppingCartCommand.CMD_SPLITCARTITEMS);
        cartCommandFactory.execute(ShoppingCartCommand.CMD_SPLITCARTITEMS, cart, params);

    }

    private void setShippingMethod(final ShoppingCart cart, final String shipping) {

        if (StringUtils.isNotBlank(shipping)) {

            final CarrierSla carrierSla = carrierSlaService.findSingleByCriteria(" where e.guid = ?1", shipping);
            if (carrierSla != null) {
                final Map<String, Object> params = new HashMap<>();
                final StringBuilder sla = new StringBuilder();
                for (final String supplier : cart.getCartItemsSuppliers()) {
                    if (sla.length() > 0) {
                        sla.append('|');
                    }
                    sla.append(String.valueOf(carrierSla.getCarrierslaId())).append('-').append(supplier);
                }
                params.put(ShoppingCartCommand.CMD_SETCARRIERSLA, sla.toString());
                cartCommandFactory.execute(ShoppingCartCommand.CMD_SETCARRIERSLA, cart, params);
            }
        }
    }



}
