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
import org.yes.cart.domain.entity.ProductQuantityModel;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductQuantityStrategy;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.PricingPolicyProvider;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * Remove one sku from cart.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class RemoveSkuFromCartCommandImpl extends AbstractSkuCartCommandImpl{

    private static final long serialVersionUID = 20100313L;

    private static final Logger LOG = LoggerFactory.getLogger(RemoveSkuFromCartCommandImpl.class);

    private final ProductQuantityStrategy productQuantityStrategy;

    /**
     * Construct sku command.
     *
     * @param registry shopping cart command registry
     * @param priceService price service
     * @param pricingPolicyProvider pricing policy provider
     * @param productService product service
     * @param shopService shop service
     * @param productQuantityStrategy product quantity strategy
     */
    public RemoveSkuFromCartCommandImpl(final ShoppingCartCommandRegistry registry,
                                        final PriceService priceService,
                                        final PricingPolicyProvider pricingPolicyProvider,
                                        final ProductService productService,
                                        final ShopService shopService,
                                        final ProductQuantityStrategy productQuantityStrategy) {
        super(registry, priceService, pricingPolicyProvider, productService, shopService);
        this.productQuantityStrategy = productQuantityStrategy;
    }

    /** {@inheritDoc} */
    public String getCmdKey() {
        return CMD_REMOVEONESKU;
    }



    private BigDecimal getQuantityValue(final ProductSku productSku, final BigDecimal quantityInCart) {

        if (productSku != null) {
            final ProductQuantityModel pqm = productQuantityStrategy.getQuantityModel(quantityInCart, productSku);
            return pqm.getValidRemoveQty(null);
        }

        return BigDecimal.ONE;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute(final MutableShoppingCart shoppingCart,
                           final ProductSku productSku,
                           final String skuCode,
                           final Map<String, Object> parameters) {
        if (productSku != null) {
            if(!shoppingCart.removeCartItemQuantity(productSku.getCode(),
                    getQuantityValue(productSku, shoppingCart.getProductSkuQuantity(productSku.getCode())))) {
                LOG.warn("Can not remove one sku with code {} from cart", productSku.getCode());
            }

            recalculatePricesInCart(shoppingCart);
            markDirty(shoppingCart);
        } else {
            if(!shoppingCart.removeCartItemQuantity(skuCode,
                    getQuantityValue(null, shoppingCart.getProductSkuQuantity(skuCode)))) {
                LOG.warn("Can not remove one sku with code {} from cart", skuCode);
            }

            recalculatePricesInCart(shoppingCart);
            markDirty(shoppingCart);
        }
    }

}
