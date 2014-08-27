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
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.CustomerWishListService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;
import org.yes.cart.util.ShopCodeContext;

import java.util.List;
import java.util.Map;

/**
 * Default implementation of the add to cart visitor.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:17:10 PM
 */
public class RemoveSkuFromWishListEventCommandImpl extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20100122L;

    private final CustomerService customerService;
    private final CustomerWishListService customerWishListService;

    /**
     * Construct sku command.
     *
     * @param registry shopping cart command registry
     * @param priceService price service
     * @param productService product service
     * @param shopService shop service
     * @param customerService customer service
     * @param customerWishListService customer wish list service
     */
    public RemoveSkuFromWishListEventCommandImpl(final ShoppingCartCommandRegistry registry,
                                                 final PriceService priceService,
                                                 final ProductService productService,
                                                 final ShopService shopService,
                                                 final CustomerService customerService,
                                                 final CustomerWishListService customerWishListService) {
        super(registry, priceService, productService, shopService);
        this.customerService = customerService;
        this.customerWishListService = customerWishListService;
    }

    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return CMD_REMOVEFROMWISHLIST;
    }


    private Long getItemIdValue(final Map parameters) {
        final Object strId = parameters.get(CMD_REMOVEFROMWISHLIST_P_ID);

        if (strId instanceof String) {
            try {
                return Long.valueOf((String) strId);
            } catch (Exception exp) {
                ShopCodeContext.getLog(this).error("Invalid item id in remove from wish list command", exp);
            }
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

            final Long pk = getItemIdValue(parameters);

            if (pk != null) {

                removeWishListItem(shoppingCart, productSku, pk);

                /*
                    We do not need it for demo but if we have dependency of promotions on wish list items
                    then this is how:
                 */
                // recalculatePrice(shoppingCart, null);
                // markDirty(shoppingCart);

                final Logger log = ShopCodeContext.getLog(this);
                if (log.isDebugEnabled()) {
                    log.debug("Removed one item of sku code {} from wishlist",
                            productSku.getCode());
                }
            }
        }
    }

    private void removeWishListItem(final ShoppingCart shoppingCart, final ProductSku productSku, final long pk) {

        final List<CustomerWishList> wishList = customerWishListService.getWishListByCustomerEmail(shoppingCart.getCustomerEmail());

        for (final CustomerWishList item : wishList) {

            if (item.getCustomerwishlistId() == pk) {

                customerWishListService.delete(item);
                return;

            }

        }

    }
}
