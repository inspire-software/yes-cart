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

import org.slf4j.Logger;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Abstract sku cart command.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractSkuCartCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20100313L;

    private final PriceService priceService;

    private final ProductService productService;

    private final ShopService shopService;


    /**
     * Construct abstract sku command.
     *
     * @param priceService price service
     * @param productService product service
     * @param shopService shop service
     */
    public AbstractSkuCartCommandImpl(final PriceService priceService,
                                      final ProductService productService,
                                      final ShopService shopService) {
        super();
        this.priceService = priceService;
        this.productService = productService;
        this.shopService = shopService;
    }

    /** {@inheritDoc} */
    @Override
    public final void execute(final ShoppingCart shoppingCart, final Map<String, Object> parameters) {

        if (parameters.containsKey(getCmdKey())) {
            final String skuCode = (String) parameters.get(getCmdKey());
            try {
                final ProductSku productSku = productService.getProductSkuByCode(skuCode);
                execute(shoppingCart, productSku, parameters);
            } catch (Exception e) {
                final Logger log = ShopCodeContext.getLog(this);
                log.error("Can not retreive product sku dto with code {}", skuCode, e);
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Abstract execute method.
     *
     * @param shoppingCart shopping cart
     * @param productSku current sku
     * @param parameters all parameters
     */
    protected abstract void execute(final ShoppingCart shoppingCart,
                                    final ProductSku productSku,
                                    final Map<String, Object> parameters);

    /**
     * Recalculate price in shopping cart. At this moment price depends from shop, current and quantity.
     * Promotions also can impact the price. It will be implemented letter
     *
     * @param shoppingCart shopping cart
     * @param productSku current sku (or null)
     */
    protected void recalculatePrice(final ShoppingCart shoppingCart, final ProductSku productSku) {

        if (shoppingCart.getShoppingContext().getShopId() == 0L) {

            ShopCodeContext.getLog(this).error("Can not recalculate price because the shop id is 0");

        } else {

            final Shop shop = shopService.getById(shoppingCart.getShoppingContext().getShopId());

            if (productSku == null) {

                for (int i = 0; i < shoppingCart.getCartItemList().size(); i++) {

                    final CartItem cartItem = shoppingCart.getCartItemList().get(i);

                    setProductSkuPrice(shoppingCart, shop, cartItem.getProductSkuCode(), cartItem.getQty());

                }

            } else {
                // particular sku command
                final String skuCode = productSku.getCode();

                int skuIdx = shoppingCart.indexOfProductSku(skuCode);

                if (skuIdx != -1) {

                    setProductSkuPrice(shoppingCart, shop, skuCode, shoppingCart.getCartItemList().get(skuIdx).getQty());

                }
            }

            recalculate(shoppingCart);

        }

    }

    private void setProductSkuPrice(final ShoppingCart shoppingCart,
                                    final Shop shop,
                                    final String skuCode,
                                    final BigDecimal qty) {

        final SkuPrice skuPrice = getPriceService().getMinimalRegularPrice(
                null,
                skuCode,
                shop,
                shoppingCart.getCurrencyCode(),
                qty
        );

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
