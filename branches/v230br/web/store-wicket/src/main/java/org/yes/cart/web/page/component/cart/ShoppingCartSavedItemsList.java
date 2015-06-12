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

package org.yes.cart.web.page.component.cart;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.customer.wishlist.WishListView;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.support.constants.WebParametersKeys;

/**
 * Show new arrival. Current category will be selected to pick up
 * new arrivals.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 9/18/11
 * Time: 11:45 AM
 */
public class ShoppingCartSavedItemsList extends WishListView {


    /**
     * Construct wish list to show for given user.
     *
     * @param id component id.
     *
     * @param customerEmail customer email
     */
    public ShoppingCartSavedItemsList(final String id,
                                      final Model<String> customerEmail) {
        super(id, customerEmail, new Model<String>(CustomerWishList.CART_SAVE_FOR_LATER), new Model<String>(null));
    }


    @Override
    protected void onBeforeRenderSetVisibility() {
        // do not show anything if we do not have items
        setVisible(ApplicationDirector.getShoppingCart().getLogonState() == ShoppingCart.LOGGED_IN
                && !getProductListToShow().isEmpty());
    }

    /**
     * Extension hook for sub classes.
     *
     * @param links links support
     * @param linkId link id
     * @param product product
     * @param itemData wish list item
     * @param qty quantity as string
     *
     * @return link
     */
    protected Link determineAtbLink(final LinksSupport links,
                                    final String linkId,
                                    final ProductSearchResultDTO product,
                                    final CustomerWishList itemData,
                                    final String qty) {
        final PageParameters params = new PageParameters();
        params.add(WebParametersKeys.SKU_ID, itemData.getSkus().getSkuId());
        return links.newAddToCartLink(linkId, product.getDefaultSkuCode(), qty, String.valueOf(itemData.getCustomerwishlistId()), (Class) getPage().getClass(), params);
    }

}
