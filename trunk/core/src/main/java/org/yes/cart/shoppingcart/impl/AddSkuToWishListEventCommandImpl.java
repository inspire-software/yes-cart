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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.*;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.*;

/**
 * Default implementation of the add to cart visitor.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:17:10 PM
 */
public class AddSkuToWishListEventCommandImpl extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20100122L;

    private final CustomerService customerService;
    private final CustomerWishListService customerWishListService;
    private final ProductQuantityStrategy productQuantityStrategy;

    /**
     * Construct sku command.
     *
     * @param registry shopping cart command registry
     * @param priceService price service
     * @param productService product service
     * @param shopService shop service
     * @param customerService customer service
     * @param customerWishListService customer wish list service
     * @param productQuantityStrategy product quantity strategy
     */
    public AddSkuToWishListEventCommandImpl(final ShoppingCartCommandRegistry registry,
                                            final PriceService priceService,
                                            final ProductService productService,
                                            final ShopService shopService,
                                            final CustomerService customerService,
                                            final CustomerWishListService customerWishListService,
                                            final ProductQuantityStrategy productQuantityStrategy) {
        super(registry, priceService, productService, shopService);
        this.customerService = customerService;
        this.customerWishListService = customerWishListService;
        this.productQuantityStrategy = productQuantityStrategy;
    }

    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return CMD_ADDTOWISHLIST;
    }


    private BigDecimal getQuantityValue(final Map parameters) {
        final Object strQty = parameters.get(CMD_ADDTOWISHLIST_P_QTY);

        if (strQty instanceof String) {
            try {
                return new BigDecimal((String) strQty);
            } catch (Exception exp) {
                ShopCodeContext.getLog(this).error("Invalid quantity in add to cart command", exp);
            }
        }
        return null;
    }


    private String getTypeValue(final Map parameters) {
        final Object strType = parameters.get(CMD_ADDTOWISHLIST_P_TYPE);

        if (strType instanceof String) {
            return (String) strType;
        }
        return CustomerWishList.SIMPLE_WISH_ITEM;
    }


    private String getTagsValue(final Map parameters) {
        final Object strType = parameters.get(CMD_ADDTOWISHLIST_P_TAGS);

        if (strType instanceof String) {
            return (String) strType;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute(final ShoppingCart shoppingCart,
                           final ProductSku productSku,
                           final Map<String, Object> parameters) {

        if (productSku != null && ShoppingCart.LOGGED_IN == shoppingCart.getLogonState()) {

            final String type = getTypeValue(parameters);
            final String tags = getTagsValue(parameters);

            createWishListItem(shoppingCart, productSku, type, parameters, tags);

            /*
                We do not need it for demo but if we have dependency of promotions on wish list items
                then this is how:
             */
            // recalculatePrice(shoppingCart, null);
            // markDirty(shoppingCart);

            final Logger log = ShopCodeContext.getLog(this);
            if (log.isDebugEnabled()) {
                log.debug("Added one item of sku code {} to wishlist",
                        productSku.getCode());
            }
        }
    }

    private void createWishListItem(final ShoppingCart shoppingCart,
                                    final ProductSku productSku,
                                    final String type,
                                    final Map<String, Object> parameters,
                                    final String tags) {

        final List<CustomerWishList> wishList = customerWishListService.getWishListByCustomerEmail(shoppingCart.getCustomerEmail());

        for (final CustomerWishList item : wishList) {

            if (item.getSkus().getSkuId() == productSku.getSkuId()
                    && item.getWlType().equals(type)) {

                // duplicate item, so just update quantity
                final ProductQuantityModel pqm = productQuantityStrategy.getQuantityModel(item.getQuantity(), productSku);
                if (!pqm.canOrderMore()) {
                    return; // cannot add more
                }

                final BigDecimal quantity = pqm.getValidAddQty(getQuantityValue(parameters));
                item.setQuantity(item.getQuantity().add(quantity));

                final Set<String> tag = new TreeSet<String>();
                if (StringUtils.isNotBlank(item.getTag())) {
                    tag.addAll(Arrays.asList(StringUtils.split(item.getTag(), ' ')));
                }
                if (StringUtils.isNotBlank(tags)) {
                    tag.addAll(Arrays.asList(StringUtils.split(tags, ' ')));
                }
                if (tag.isEmpty()) {
                    item.setTag(null);
                } else {
                    item.setTag(StringUtils.join(tag, ' '));
                }

                customerWishListService.update(item);
                return;

            }

        }

        // not found so need to create one
        final Shop shop = getShopService().getById(shoppingCart.getShoppingContext().getShopId());
        final String skuCode = productSku.getCode();

        final ProductQuantityModel pqm = productQuantityStrategy.getQuantityModel(BigDecimal.ZERO, productSku);
        if (!pqm.canOrderMore()) {
            return; // cannot add more
        }

        final BigDecimal quantity = pqm.getValidAddQty(getQuantityValue(parameters));

        final SkuPrice skuPrice = getPriceService().getMinimalRegularPrice(
                null,
                skuCode,
                shop,
                shoppingCart.getCurrencyCode(),
                quantity
        );

        final BigDecimal price = MoneyUtils.minPositive(skuPrice.getSalePriceForCalculation(), skuPrice.getRegularPrice());

        final CustomerWishList customerWishList = customerWishListService.getGenericDao().getEntityFactory().getByIface(CustomerWishList.class);
        customerWishList.setCustomer(customerService.getCustomerByEmail(shoppingCart.getCustomerEmail()));
        customerWishList.setSkus(productSku);
        customerWishList.setWlType(type);
        customerWishList.setTag(tags);
        customerWishList.setVisibility(CustomerWishList.PRIVATE);
        customerWishList.setQuantity(quantity);
        customerWishList.setRegularPriceWhenAdded(price);

        customerWishListService.create(customerWishList);

    }
}
