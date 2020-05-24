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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.CustomerWishListService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.utils.MoneyUtils;

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

    private static final Logger LOG = LoggerFactory.getLogger(AddSkuToWishListEventCommandImpl.class);

    private final CustomerService customerService;
    private final CustomerWishListService customerWishListService;
    private final ProductQuantityStrategy productQuantityStrategy;

    private boolean forceRecalculateCart = false;

    /**
     * Construct sku command.
     *
     * @param registry shopping cart command registry
     * @param priceResolver price service
     * @param pricingPolicyProvider pricing policy provider
     * @param productService product service
     * @param shopService shop service
     * @param customerService customer service
     * @param customerWishListService customer wish list service
     * @param productQuantityStrategy product quantity strategy
     */
    public AddSkuToWishListEventCommandImpl(final ShoppingCartCommandRegistry registry,
                                            final PriceResolver priceResolver,
                                            final PricingPolicyProvider pricingPolicyProvider,
                                            final ProductService productService,
                                            final ShopService shopService,
                                            final CustomerService customerService,
                                            final CustomerWishListService customerWishListService,
                                            final ProductQuantityStrategy productQuantityStrategy) {
        super(registry, priceResolver, pricingPolicyProvider, productService, shopService);
        this.customerService = customerService;
        this.customerWishListService = customerWishListService;
        this.productQuantityStrategy = productQuantityStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCmdKey() {
        return CMD_ADDTOWISHLIST;
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

    private boolean isTagsValueReplace(final Map parameters) {
        final Object strType = parameters.get(CMD_ADDTOWISHLIST_P_TAGS_REPLACE);

        return strType instanceof String && CMD_ADDTOWISHLIST_P_TAGS_REPLACE.equals(strType);
    }


    private String getVisibilityValue(final Map parameters) {
        final Object strVisibility = parameters.get(CMD_ADDTOWISHLIST_P_VISIBILITY);

        if (CustomerWishList.SHARED.equals(strVisibility)) {
            return CustomerWishList.SHARED;
        }
        return CustomerWishList.PRIVATE;
    }

    private String getNotificationValue(final Map parameters) {
        final Object strNotification = parameters.get(CMD_ADDTOWISHLIST_P_NOTIFICATION);

        if (strNotification instanceof String) {
            return (String) strNotification;
        }
        return null;
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

        if (ShoppingCart.LOGGED_IN == shoppingCart.getLogonState()) {

            final String type = getTypeValue(parameters);
            final String tags = getTagsValue(parameters);
            final boolean tagsr = isTagsValueReplace(parameters);
            final String visibility = getVisibilityValue(parameters);
            final String notification = getNotificationValue(parameters);

            createWishListItem(shoppingCart, skuCode, supplier, qty, type, tags, tagsr, visibility, notification, parameters);

            if (CustomerWishList.CART_SAVE_FOR_LATER.equals(type)) {

                LOG.debug("[{}] Added item of sku code {} to save for later, removing sku from cart", shoppingCart.getGuid(), skuCode);

                final Map removeParams = new HashMap();
                removeParams.put(ShoppingCartCommand.CMD_REMOVEALLSKU, skuCode);
                removeParams.put(ShoppingCartCommand.CMD_P_SUPPLIER, supplier);
                getRemoveAllSku().execute(shoppingCart, removeParams);

            } else if (forceRecalculateCart) {
                recalculatePricesInCart(shoppingCart);
                markDirty(shoppingCart);
            }

            LOG.debug("[{}] Added item of sku code {} to wishlist", shoppingCart.getGuid(), skuCode);
        } else {
            LOG.debug("[{}] Unable to added item of sku code {} to wishlist, user is not logged in", shoppingCart.getGuid(), skuCode);
        }
    }

    private void createWishListItem(final ShoppingCart shoppingCart,
                                    final String skuCode,
                                    final String supplier,
                                    final BigDecimal qty,
                                    final String type,
                                    final String tags,
                                    final boolean tagsr,
                                    final String visibility,
                                    final String notification,
                                    final Map<String, Object> parameters) {

        final Shop shop = getShopService().getById(shoppingCart.getShoppingContext().getShopId());
        if (shop == null) {
            LOG.debug("[{}] Unable to add item of sku code {} to wishlist, shop undefined", shoppingCart.getGuid(), skuCode);
            return;
        }
        final Customer customer = customerService.getCustomerByEmail(shoppingCart.getCustomerEmail(), shop);
        if (customer == null) {
            LOG.debug("[{}] Unable to add item of sku code {} to wishlist, customer undefined", shoppingCart.getGuid(), skuCode);
            return;
        }
        if (supplier == null) {
            LOG.debug("[{}] Unable to add item of sku code {} to wishlist, supplier undefined", shoppingCart.getGuid(), skuCode);
            return;
        }

        final long customerShopId = shoppingCart.getShoppingContext().getCustomerShopId();
        final long masterShopId = shoppingCart.getShoppingContext().getShopId();
        final boolean isNamedList = isNamedListType(type);

        final List<CustomerWishList> wishList = customerWishListService.findWishListByCustomerId(customer.getCustomerId());

        for (final CustomerWishList item : wishList) {

            if (item.getSkuCode().equals(skuCode) && item.getSupplierCode().equals(supplier)
                    && item.getWlType().equals(type)) {

                if (isNamedList && !tags.equals(item.getTag())) {
                    continue; // This is not the same named list
                }

                // duplicate item, so just update quantity
                final QuantityModel pqm = productQuantityStrategy.getQuantityModel(customerShopId, item.getQuantity(), skuCode, supplier);
                if (!pqm.isCanOrderMore()) {
                    LOG.debug("[{}] Unable to add item of sku code {} to wishlist, cannot add more", shoppingCart.getGuid(), skuCode);
                    return; // cannot add more
                }

                final BigDecimal qtyAdd = pqm.getValidAddQty(qty);
                final BigDecimal quantity = pqm.getValidSetQty(item.getQuantity().add(qtyAdd));
                item.setQuantity(quantity);

                if (!isNamedList) {
                    final Set<String> tag = new TreeSet<>();
                    if (!tagsr && StringUtils.isNotBlank(item.getTag())) {
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

                    item.setVisibility(visibility);
                }

                customerWishListService.update(item);
                return;

            }

        }

        // not found so need to create one

        final QuantityModel pqm = productQuantityStrategy.getQuantityModel(customerShopId, BigDecimal.ZERO, skuCode, supplier);
        if (!pqm.isCanOrderMore()) {
            return; // cannot add more
        }

        final BigDecimal quantity = pqm.getValidAddQty(qty);

        // Fallback only if we have a B2B non-strict mode
        final Long fallbackShopId = masterShopId == customerShopId || getShopService().getById(customerShopId).isB2BStrictPriceActive() ? null : masterShopId;
        final String shopCode = shoppingCart.getShoppingContext().getShopCode();
        final String currency = shoppingCart.getCurrencyCode();

        // Policy is setup on master
        final PricingPolicyProvider.PricingPolicy policy = getPricingPolicyProvider().determinePricingPolicy(
                shopCode, currency, shoppingCart.getCustomerEmail(),
                shoppingCart.getShoppingContext().getCountryCode(),
                shoppingCart.getShoppingContext().getStateCode()
        );


        final SkuPrice skuPrice = getPriceResolver().getMinimalPrice(
                null,
                skuCode,
                customerShopId,
                fallbackShopId,
                currency,
                quantity,
                false,
                policy.getID(),
                supplier
        );

        final BigDecimal price = skuPrice.isPriceUponRequest() ? MoneyUtils.ZERO : MoneyUtils.secondOrFirst(skuPrice.getSalePriceForCalculation());

        final CustomerWishList customerWishList = customerWishListService.getGenericDao().getEntityFactory().getByIface(CustomerWishList.class);
        customerWishList.setCustomer(customer);
        customerWishList.setSkuCode(skuCode);
        customerWishList.setSupplierCode(supplier);
        customerWishList.setWlType(type);
        customerWishList.setTag(tags);
        customerWishList.setVisibility(visibility);
        customerWishList.setNotificationEmail(notification);
        customerWishList.setQuantity(quantity);
        customerWishList.setRegularPriceWhenAdded(price);
        customerWishList.setRegularPriceCurrencyWhenAdded(shoppingCart.getCurrencyCode());

        customerWishListService.create(customerWishList);

    }

    protected boolean isNamedListType(final String type) {
        return CustomerWishList.SHOPPING_LIST_ITEM.equals(type) || CustomerWishList.MANAGED_LIST_ITEM.equals(type);
    }

    /**
     * Spring IoC.
     *
     * @return remove sku lone from command
     */
    public ShoppingCartCommand getRemoveAllSku() {
        return null;
    }


    /**
     * Configuration point.
     *
     * @param forceRecalculateCart set to true to force cart recalculation after wish list items are added,
     *                             default is false - not to recalculate since wish list has no influence on cart
     *                             in core code.
     */
    public void setForceRecalculateCart(final boolean forceRecalculateCart) {
        this.forceRecalculateCart = forceRecalculateCart;
    }
}
