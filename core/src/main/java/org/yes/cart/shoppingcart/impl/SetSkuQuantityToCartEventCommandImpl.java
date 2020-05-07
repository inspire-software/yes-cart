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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.QuantityModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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



    private BigDecimal getQuantityValue(final Map parameters,
                                        final long shopId,
                                        final String productSku,
                                        final String supplier,
                                        final BigDecimal quantityInCart) {

        final Object strQty = parameters.get(CMD_P_QTY);

        if (strQty instanceof String) {
            try {
                final BigDecimal qty = new BigDecimal((String) strQty);
                final QuantityModel pqm = productQuantityStrategy.getQuantityModel(shopId, quantityInCart, productSku, supplier);
                return pqm.getValidSetQty(qty);
            } catch (NumberFormatException nfe) {
                LOG.error("Invalid quantity {} in set qty to cart command", strQty);
            } catch (Exception exp) {
                LOG.error("Invalid quantity in set qty to cart command", exp);
            }
        }
        final QuantityModel pqm = productQuantityStrategy.getQuantityModel(shopId, quantityInCart, productSku, supplier);
        return pqm.getValidSetQty(null);
    }


    /** {@inheritDoc} */
    @Override
    protected void execute(final MutableShoppingCart shoppingCart,
                           final ProductSku productSku,
                           final String skuCode,
                           final String supplier,
                           final String itemGroup,
                           final BigDecimal qty,
                           final Map<String, Object> parameters) {

        if (determineSkuPrice(shoppingCart, supplier, skuCode, BigDecimal.ONE) == null) {
            LOG.debug("[{}] Unable to set item quantity {} for supplier {} because could not resolve price",
                    shoppingCart.getGuid(), skuCode, supplier);
            return;
        }

        final long shopId = shoppingCart.getShoppingContext().getCustomerShopId();
        final BigDecimal cartQty = shoppingCart.getProductSkuQuantity(supplier, skuCode);
        final BigDecimal validQuantity = getQuantityValue(parameters, shopId, skuCode, supplier, cartQty);

        String skuName = skuCode;

        if (productSku != null) {

            skuName = new FailoverStringI18NModel(
                    productSku.getDisplayName(),
                    productSku.getName()
            ).getValue(shoppingCart.getCurrentLocale());

        }

        if (StringUtils.isNotBlank(itemGroup)) {

            final Optional<CartItem> main = shoppingCart.getCartItemList().stream()
                    .filter(item -> ShoppingCartUtils.isCartItem(item, supplier, skuCode, itemGroup)).findFirst();

            if (!main.isPresent() || main.get().isNotSoldSeparately()) {
                LOG.warn("Cannot set sku quantity with code {}:{} from cart, it is not main in group", skuCode, itemGroup);
                return;
            }

            final CartItem mainItem = main.get();

            final List<CartItem> allInGroup = shoppingCart.getCartItemList().stream()
                    .filter(item -> itemGroup.equals(item.getItemGroup()) && !mainItem.getProductSkuCode().equals(item.getProductSkuCode()))
                    .collect(Collectors.toList());

            final BigDecimal toSet = validQuantity;

            for (final CartItem component : allInGroup) {

                final BigDecimal ratio = component.getQty().divide(mainItem.getQty(), 10, BigDecimal.ROUND_HALF_UP);
                final BigDecimal toSetComponent = toSet.multiply(ratio).setScale(Constants.INVENTORY_SCALE, BigDecimal.ROUND_HALF_EVEN).stripTrailingZeros();

                if(!shoppingCart.setProductSkuToCart(supplier, component.getProductSkuCode(), component.getProductName(), toSetComponent, itemGroup)) {
                    LOG.warn("Cannot set sku quantity with code {}:{} from cart", component.getProductSkuCode(), itemGroup);
                    return;
                }
            }

            if (shoppingCart.setProductSkuToCart(supplier, skuCode, skuName, validQuantity, itemGroup)) {

                recalculatePricesInCart(shoppingCart);
                LOG.debug("Set sku quantity with code {}:{} to qty {}", skuCode, itemGroup, validQuantity);
                markDirty(shoppingCart);

            } else {

                LOG.warn("Cannot set sku quantity with code {}:{} from cart", skuCode, itemGroup);

            }

        } else if (shoppingCart.setProductSkuToCart(supplier, skuCode, skuName, validQuantity, itemGroup)) {

            recalculatePricesInCart(shoppingCart);
            LOG.debug("Set sku quantity with code {} to qty {}", skuCode, validQuantity);
            markDirty(shoppingCart);

        } else {

            LOG.warn("Cannot set sku quantity with code {} from cart", skuCode);

        }

    }

}
