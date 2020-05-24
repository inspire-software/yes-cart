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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Abstract sku cart command.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractSkuCartCommandImpl extends AbstractRecalculatePriceCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20100313L;

    /**
     * Construct abstract sku command.
     *
     * @param registry              shopping cart command registry
     * @param priceResolver         price service
     * @param pricingPolicyProvider pricing policy provider
     * @param productService        product service
     * @param shopService           shop service
     */
    public AbstractSkuCartCommandImpl(final ShoppingCartCommandRegistry registry,
                                      final PriceResolver priceResolver,
                                      final PricingPolicyProvider pricingPolicyProvider,
                                      final ProductService productService,
                                      final ShopService shopService) {
        super(registry, priceResolver, pricingPolicyProvider, productService, shopService);
    }

    /** {@inheritDoc} */
    @Override
    public final void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {

        if (parameters.containsKey(getCmdKey())) {
            final String skuCode = (String) parameters.get(getCmdKey());
            final String supplier = (String) parameters.get(CMD_P_SUPPLIER);
            final String itemGroup = (String) parameters.get(CMD_P_ITEM_GROUP);
            final Object strQty = parameters.get(CMD_P_QTY);
            BigDecimal qty = null;
            if (strQty instanceof String) {
                try {
                    qty = new BigDecimal((String) strQty);
                } catch (NumberFormatException nfe) {
                    LOG.error("[{}] Invalid quantity {} in add to cart command", shoppingCart.getGuid(), strQty);
                } catch (Exception exp) {
                    LOG.error("[" + shoppingCart.getGuid() + "] Invalid quantity in add to cart command", exp);
                }
            } else if (strQty instanceof BigDecimal) {
                qty = (BigDecimal) strQty;
            }


            try {
                final ProductSku productSku = getProductService().getProductSkuByCode(skuCode);
                execute(shoppingCart, productSku, skuCode, supplier, itemGroup, qty, parameters);
            } catch (Exception e) {
                LOG.error("[" + shoppingCart.getGuid() + "] Error processing command for " + skuCode + ", caused: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Abstract execute method.
     *
     * @param shoppingCart  shopping cart
     * @param productSku    current sku object (if exists)
     * @param skuCode       actual SKU code sent
     * @param supplier      SKU supplier
     * @param itemGroup     marker to group items together in cart
     * @param qty           SKU quantity parameter if provided
     * @param parameters    all parameters
     */
    protected abstract void execute(final MutableShoppingCart shoppingCart,
                                    final ProductSku productSku,
                                    final String skuCode,
                                    final String supplier,
                                    final String itemGroup,
                                    final BigDecimal qty,
                                    final Map<String, Object> parameters);


}
