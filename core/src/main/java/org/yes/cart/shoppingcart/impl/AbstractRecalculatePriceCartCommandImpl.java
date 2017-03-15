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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;

/**
 * Abstract cart prices recalculation command.
 * <p/>
 */
public abstract class AbstractRecalculatePriceCartCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20100313L;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRecalculatePriceCartCommandImpl.class);

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

            LOG.error("Can not recalculate price because the shop id is 0");

        } else {

            final long customerShopId = shoppingCart.getShoppingContext().getCustomerShopId();
            final long masterShopId = shoppingCart.getShoppingContext().getShopId();
            // Fallback only if we have a B2B non-strict mode
            final Long fallbackShopId = masterShopId == customerShopId || shopService.getById(customerShopId).isB2BStrictPriceActive() ? null : masterShopId;

            final PricingPolicyProvider.PricingPolicy policy = determinePricingPolicy(shoppingCart);

            for (final CartItem cartItem : shoppingCart.getCartItemList()) {

                setProductSkuPrice(shoppingCart, customerShopId, fallbackShopId, cartItem.getProductSkuCode(), cartItem.getQty(), policy);

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

            LOG.error("Can not recalculate price because the shop id is 0");

        } else {

            final long customerShopId = shoppingCart.getShoppingContext().getCustomerShopId();
            final long masterShopId = shoppingCart.getShoppingContext().getShopId();
            // Fallback only if we have a B2B non-strict mode
            final Long fallbackShopId = masterShopId == customerShopId || shopService.getById(customerShopId).isB2BStrictPriceActive() ? null : masterShopId;

            final PricingPolicyProvider.PricingPolicy policy = determinePricingPolicy(shoppingCart);

            final SkuPrice skuPrice = getPriceService().getMinimalPrice(
                    null,
                    skuCode,
                    customerShopId,
                    fallbackShopId,
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

    private PricingPolicyProvider.PricingPolicy determinePricingPolicy(final MutableShoppingCart shoppingCart) {
        return getPricingPolicyProvider().determinePricingPolicy(
                        shoppingCart.getShoppingContext().getShopCode(), shoppingCart.getCurrencyCode(), shoppingCart.getCustomerEmail(),
                        shoppingCart.getShoppingContext().getCountryCode(),
                        shoppingCart.getShoppingContext().getStateCode()
                );
    }

    private void setProductSkuPrice(final MutableShoppingCart shoppingCart,
                                    final long customerShopId,
                                    final Long masterShopId,
                                    final String skuCode,
                                    final BigDecimal qty,
                                    final PricingPolicyProvider.PricingPolicy policy) {

        final SkuPrice skuPrice = getPriceService().getMinimalPrice(
                null,
                skuCode,
                customerShopId,
                masterShopId,
                shoppingCart.getCurrencyCode(),
                qty,
                false,
                policy.getID());

        if (!shoppingCart.setProductSkuPrice(
                skuCode,
                MoneyUtils.minPositive(skuPrice.getSalePriceForCalculation(), skuPrice.getRegularPrice()),
                skuPrice.getRegularPrice()
        )) {
            LOG.warn("Can not set price to sku with code {}", skuCode);

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
