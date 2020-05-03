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
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductOption;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.QuantityModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Default implementation of the add to cart visitor.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:17:10 PM
 */
public class AddSkuToCartEventCommandImpl extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20100122L;

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
    public AddSkuToCartEventCommandImpl(final ShoppingCartCommandRegistry registry,
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
        return CMD_ADDTOCART;
    }


    private BigDecimal getQuantityValue(final long shopId,
                                        final String supplier,
                                        final String productSku,
                                        final BigDecimal qty,
                                        final BigDecimal quantityInCart) {

        final QuantityModel pqm = productQuantityStrategy.getQuantityModel(shopId, quantityInCart, productSku, supplier);
        return pqm.getValidAddQty(qty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute(final MutableShoppingCart shoppingCart,
                           final ProductSku productSku,
                           final String skuCode,
                           final String supplier,
                           final String itemGroup,
                           final BigDecimal qty,
                           final Map<String, Object> parameters) {

        if (determineSkuPrice(shoppingCart, supplier, skuCode, BigDecimal.ONE) == null) {
            LOG.debug("[{}] Unable to add item {} for supplier {} because could not resolve price",
                    shoppingCart.getGuid(), skuCode, supplier);
            return;
        }

        final long shopId = shoppingCart.getShoppingContext().getCustomerShopId();
        final BigDecimal cartQty = shoppingCart.getProductSkuQuantity(supplier, skuCode);
        final BigDecimal toAdd = getQuantityValue(shopId, supplier, skuCode, qty, cartQty);

        String skuName = skuCode;
        boolean configurable = false;
        Map<String, Pair<String, BigDecimal>> selectedOptions = Collections.emptyMap();

        if (productSku != null) {

            skuName = new FailoverStringI18NModel(
                    productSku.getDisplayName(),
                    productSku.getName()
            ).getValue(shoppingCart.getCurrentLocale());

            final Product product = getProductService().getProductById(productSku.getProduct().getProductId());

            if (product.getOptions().isConfigurable()) {

                configurable = true;
                selectedOptions = new LinkedHashMap<>();

                final List<ProductOption> options = product.getOptions().getConfigurationOptionForSKU(productSku);

                for (final ProductOption option : options) {

                    final String skuOption = (String) parameters.get(option.getAttributeCode());
                    if ((option.isMandatory() && StringUtils.isBlank(skuOption)) ||
                            (StringUtils.isNotBlank(skuOption) && !option.getOptionSkuCodes().contains(skuOption))) {
                        LOG.debug("[{}] Unable to add item {} for supplier {} because option {} has invalid value {}",
                                shoppingCart.getGuid(), skuCode, supplier, option.getAttributeCode(), skuOption);
                        return;
                    }

                    if (StringUtils.isBlank(skuOption)) {
                        continue; // skip valid missed selections
                    }

                    final ProductSku productSkuOption = getProductService().getProductSkuByCode(skuOption);

                    if (determineSkuPrice(shoppingCart, supplier, skuOption, BigDecimal.ONE) == null) {
                        LOG.debug("[{}] Unable to add item {} for supplier {} because could not resolve price for option {} value {}",
                                shoppingCart.getGuid(), skuCode, supplier, option.getAttributeCode(), skuOption);
                        return;
                    }

                    final BigDecimal toAddOption = toAdd.multiply(option.getQuantity());

                    final String skuNameOption = new FailoverStringI18NModel(
                            productSkuOption.getDisplayName(),
                            productSkuOption.getName()
                    ).getValue(shoppingCart.getCurrentLocale());

                    selectedOptions.put(skuOption, new Pair<>(skuNameOption, toAddOption));

                }

            }

        }

        final String enforceGroup = configurable && StringUtils.isBlank(itemGroup) ? UUID.randomUUID().toString() : itemGroup;

        final boolean optionalGroupExists = configurable && shoppingCart.getCartItemList().stream().anyMatch(cartItem -> ShoppingCartUtils.isCartItem(cartItem, supplier, skuCode, enforceGroup));

        if (optionalGroupExists) {
            LOG.debug("[{}] Unable to add item {} for supplier {} because item with this group already exists {}",
                    shoppingCart.getGuid(), skuCode, supplier, enforceGroup);
            return;
        }

        shoppingCart.addProductSkuToCart(supplier, skuCode, skuName, toAdd, enforceGroup, configurable, false);
        if (configurable) {
            for (final Map.Entry<String, Pair<String, BigDecimal>> optItem : selectedOptions.entrySet()) {
                shoppingCart.addProductSkuToCart(supplier, optItem.getKey(), optItem.getValue().getFirst(), optItem.getValue().getSecond(), enforceGroup, false, true);
            }
        }
        recalculatePricesInCart(shoppingCart);
        markDirty(shoppingCart);
        LOG.debug("[{}] Added {} item(s) of sku code {}", shoppingCart.getGuid(), toAdd, skuCode);

    }
}
