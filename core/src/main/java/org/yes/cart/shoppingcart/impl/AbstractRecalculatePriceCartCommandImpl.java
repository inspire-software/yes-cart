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
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * Abstract cart prices recalculation command.
 * <p/>
 */
public abstract class AbstractRecalculatePriceCartCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20100313L;

    private final PriceService priceService;

    private final PricingPolicyProvider pricingPolicyProvider;

    private final ProductService productService;

    private final ShopService shopService;


    /**
     * Construct abstract sku command.
     *
     * @param registry shopping cart command registry
     * @param priceService price service
     * @param pricingPolicyProvider pricing policy provider
     * @param productService product service
     * @param shopService shop service
     */
    public AbstractRecalculatePriceCartCommandImpl(final ShoppingCartCommandRegistry registry,
                                                   final PriceService priceService,
                                                   final PricingPolicyProvider pricingPolicyProvider,
                                                   final ProductService productService,
                                                   final ShopService shopService) {
        super(registry);
        this.priceService = priceService;
        this.pricingPolicyProvider = pricingPolicyProvider;
        this.productService = productService;
        this.shopService = shopService;
    }


    /**
     * Recalculate price in shopping cart. At this moment price depends from shop, current and quantity.
     *
     * @param shoppingCart shopping cart
     */
    protected void recalculatePricesInCart(final MutableShoppingCart shoppingCart) {

        if (shoppingCart.getShoppingContext().getShopId() == 0L) {

            ShopCodeContext.getLog(this).error("Can not recalculate price because the shop id is 0");

        } else {

            final Shop shop = shopService.getById(shoppingCart.getShoppingContext().getShopId());

            final PricingPolicyProvider.PricingPolicy policy = getPricingPolicyProvider().determinePricingPolicy(
                    shop.getCode(), shoppingCart.getCurrencyCode(), shoppingCart.getCustomerEmail(),
                    shoppingCart.getShoppingContext().getCountryCode(),
                    shoppingCart.getShoppingContext().getStateCode()
            );


            for (final CartItem cartItem : shoppingCart.getCartItemList()) {

                setProductSkuPrice(shoppingCart, shop, cartItem.getProductSkuCode(), cartItem.getQty(), policy);

            }

            recalculate(shoppingCart);

        }

    }

    /**
     * Determine price for SKU.
     *
     * @param shoppingCart shopping cart
     * @param skuCode      SKU code
     * @param qty          quantity tier
     *
     * @return sku price or null
     */
    protected SkuPrice determineSkuPrice(final MutableShoppingCart shoppingCart,
                                         final String skuCode,
                                         final BigDecimal qty) {

        if (shoppingCart.getShoppingContext().getShopId() == 0L) {

            ShopCodeContext.getLog(this).error("Can not recalculate price because the shop id is 0");

        } else {

            final Shop shop = shopService.getById(shoppingCart.getShoppingContext().getShopId());

            final PricingPolicyProvider.PricingPolicy policy = getPricingPolicyProvider().determinePricingPolicy(
                    shop.getCode(), shoppingCart.getCurrencyCode(), shoppingCart.getCustomerEmail(),
                    shoppingCart.getShoppingContext().getCountryCode(),
                    shoppingCart.getShoppingContext().getStateCode()
            );

            final SkuPrice skuPrice = getPriceService().getMinimalPrice(
                    null,
                    skuCode,
                    shop.getShopId(),
                    shoppingCart.getCurrencyCode(),
                    qty,
                    false,
                    policy.getID());

            if (skuPrice.getRegularPrice() != null) {

                return skuPrice;

            }

        }

        return null;
    }

    private void setProductSkuPrice(final MutableShoppingCart shoppingCart,
                                    final Shop shop,
                                    final String skuCode,
                                    final BigDecimal qty,
                                    final PricingPolicyProvider.PricingPolicy policy) {

        final SkuPrice skuPrice = getPriceService().getMinimalPrice(
                null,
                skuCode,
                shop.getShopId(),
                shoppingCart.getCurrencyCode(),
                qty,
                false,
                policy.getID());

        if (!shoppingCart.setProductSkuPrice(
                skuCode,
                MoneyUtils.minPositive(skuPrice.getSalePriceForCalculation(), skuPrice.getRegularPrice()),
                skuPrice.getRegularPrice()
        )) {
            ShopCodeContext.getLog(this).warn(MessageFormat.format("Can not set price to sku with code {0} ",
                    skuCode));

        }
    }

    /**
     * Get pricing policy service
     *
     * @return {@link PricingPolicyProvider}
     */
    public PricingPolicyProvider getPricingPolicyProvider() {
        return pricingPolicyProvider;
    }

    /**
     * Get price service.
     *
     * @return {@link PriceService}
     */
    public PriceService getPriceService() {
        return priceService;
    }

    /**
     * Get product Service.
     *
     * @return product service.
     */
    public ProductService getProductService() {
        return productService;
    }

    /**
     * Get shop service.
     *
     * @return shop service
     */
    public ShopService getShopService() {
        return shopService;
    }

}
