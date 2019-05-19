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
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.QuantityModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * Set sku quantity in cart command class.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SetSkuQuantityToCartEventCommandImpl  extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20110312L;

    private static final Logger LOG = LoggerFactory.getLogger(SetSkuQuantityToCartEventCommandImpl.class);

    private final ProductQuantityStrategy productQuantityStrategy;

    /**
     * Construct sku command.
     *
     * @param registry shopping cart command registry
     * @param priceResolver price service
     * @param pricingPolicyProvider pricing policy provider
     * @param productService product service
     * @param shopService shop service
     * @param productQuantityStrategy product quantity strategy
     */
    public SetSkuQuantityToCartEventCommandImpl(final ShoppingCartCommandRegistry registry,
                                                final PriceResolver priceResolver,
                                                final PricingPolicyProvider pricingPolicyProvider,
                                                final ProductService productService,
                                                final ShopService shopService,
                                                final ProductQuantityStrategy productQuantityStrategy) {
        super(registry, priceResolver, pricingPolicyProvider, productService, shopService);
        this.productQuantityStrategy = productQuantityStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCmdKey() {
        return CMD_SETQTYSKU;
    }



    private BigDecimal getQuantityValue(final Map parameters, final ProductSku productSku, final BigDecimal quantityInCart) {
        final Object strQty = parameters.get(CMD_SETQTYSKU_P_QTY);

        if (strQty instanceof String) {
            try {
                final BigDecimal qty = new BigDecimal((String) strQty);
                if (productSku != null) {
                    final QuantityModel pqm = productQuantityStrategy.getQuantityModel(quantityInCart, productSku);
                    return pqm.getValidSetQty(qty);
                }
                if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(qty, BigDecimal.ZERO)) {
                    return qty.setScale(0, BigDecimal.ROUND_CEILING);
                }
            } catch (NumberFormatException nfe) {
                LOG.error("Invalid quantity {} in set qty to cart command", strQty);
            } catch (Exception exp) {
                LOG.error("Invalid quantity in set qty to cart command", exp);
            }
        }
        if (productSku != null) {
            final QuantityModel pqm = productQuantityStrategy.getQuantityModel(quantityInCart, productSku);
            return pqm.getValidSetQty(null);
        }
        return BigDecimal.ONE;
    }


    /** {@inheritDoc} */
    @Override
    protected void execute(final MutableShoppingCart shoppingCart,
                           final ProductSku productSku,
                           final String skuCode,
                           final Map<String, Object> parameters) {

        if (productSku != null) {

            final BigDecimal validQuantity = getQuantityValue(parameters, productSku, shoppingCart.getProductSkuQuantity(productSku.getCode()));
            final String skuName = new FailoverStringI18NModel(
                    productSku.getDisplayName(),
                    productSku.getName()
            ).getValue(shoppingCart.getCurrentLocale());
            shoppingCart.setProductSkuToCart(productSku.getCode(), skuName, validQuantity);
            recalculatePricesInCart(shoppingCart);
            LOG.debug("Set product sku with code {} to qty {}", productSku.getCode(), validQuantity);
            markDirty(shoppingCart);
        } else if (determineSkuPrice(shoppingCart, skuCode, BigDecimal.ONE) != null) {
            // if we have no product for SKU, make sure we have price for this SKU
            final BigDecimal validQuantity = getQuantityValue(parameters, null, shoppingCart.getProductSkuQuantity(skuCode));
            shoppingCart.setProductSkuToCart(skuCode, skuCode, validQuantity);
            recalculatePricesInCart(shoppingCart);
            LOG.debug("Set product sku with code {} to qty {}", skuCode, validQuantity);
            markDirty(shoppingCart);
        }
    }

}
