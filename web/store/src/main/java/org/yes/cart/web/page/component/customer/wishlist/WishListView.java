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

package org.yes.cart.web.page.component.customer.wishlist;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.domain.entity.ProductAvailabilityModel;
import org.yes.cart.domain.query.impl.ProductQueryBuilderImpl;
import org.yes.cart.service.domain.ProductAvailabilityStrategy;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.product.AbstractProductSearchResultList;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Show new arrival. Current category will be selected to pick up
 * new arrivals.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 9/18/11
 * Time: 11:45 AM
 */
public class WishListView extends AbstractProductSearchResultList {


    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_AVAILABILITY_STRATEGY)
    private ProductAvailabilityStrategy productAvailabilityStrategy;


    private List<ProductSearchResultDTO> products = null;

    private Map<ProductSearchResultDTO, CustomerWishList> wishListDataByProduct = new HashMap<ProductSearchResultDTO, CustomerWishList>();

    private final Model<String> customerEmail;
    private final Model<String> wishListType;
    private final Model<String> wishListTag;

    private boolean showRemoveLink = false;


    /**
     * Construct wish list to show for current user.
     *
     * @param id component id.
     */
    public WishListView(final String id) {
        this(
                id,
                new Model<String>(ApplicationDirector.getShoppingCart().getCustomerEmail()),
                new Model<String>(null),
                new Model<String>(null)
        );
    }


    /**
     * Construct wish list to show for given user.
     *
     * @param id component id.
     *
     * @param customerEmail customer email
     * @param wishListType wishlist type
     * @param wishListTag tags
     */
    public WishListView(final String id,
                        final Model<String> customerEmail,
                        final Model<String> wishListType,
                        final Model<String> wishListTag) {
        super(id, true);
        this.customerEmail = customerEmail;
        this.wishListType = wishListType;
        this.wishListTag = wishListTag;
    }

    @Override
    protected void onBeforeRender() {

        showRemoveLink = ApplicationDirector.getShoppingCart().getCustomerEmail().equals(customerEmail.getObject());

        addOrReplace(new Label("noProducts", new StringResourceModel("wishlist.no.items", this, null)).setVisible(getProductListToShow().isEmpty()));
        super.onBeforeRender();
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductSearchResultDTO> getProductListToShow() {
        if (products == null) {

            final List<CustomerWishList> wishList = customerServiceFacade.getCustomerWishListByEmail(
                    this.customerEmail.getObject(),
                    this.wishListType.getObject(),
                    this.wishListTag.getObject() != null ? new String[] { this.wishListTag.getObject() } : null);

            if (CollectionUtils.isNotEmpty(wishList)) {

                final List<String> productIds = new ArrayList<String>();

                for (final CustomerWishList item : wishList) {

                    productIds.add(String.valueOf(item.getSkus().getProduct().getProductId()));

                }

                int limit = productIds.size();

                final ProductQueryBuilderImpl ftq = new ProductQueryBuilderImpl();
                final List<ProductSearchResultDTO> uniqueProducts = productService.getProductSearchResultDTOByQuery(
                        ftq.createQuery(productIds), 0, limit, "code", false);

                final List<ProductSearchResultDTO> wishListProducts = new ArrayList<ProductSearchResultDTO>();

                for (final CustomerWishList item : wishList) {

                    for (final ProductSearchResultDTO uniqueProduct : uniqueProducts) {

                        if (uniqueProduct.getId() == item.getSkus().getProduct().getProductId()) {
                            final ProductSearchResultDTO copy = uniqueProduct.copy();
                            copy.setDefaultSkuCode(item.getSkus().getCode());
                            wishListProducts.add(copy);
                            wishListDataByProduct.put(copy, item);

                        }

                    }

                    productIds.add(String.valueOf(item.getSkus().getProduct().getProductId()));

                }

                products = wishListProducts;

            } else {

                products = Collections.emptyList();
            }

        }
        return products;
    }

    @Override
    protected void onBeforeRenderSetVisibility() {
        // do nothing, should be visible always
    }

    @Override
    protected void onBeforeRenderPopulateListItem(final ListItem<ProductSearchResultDTO> listItem,
                                                  final String selectedLocale,
                                                  final Category category) {
        super.onBeforeRenderPopulateListItem(listItem, selectedLocale, category);

        final LinksSupport links = getWicketSupportFacade().links();

        final ProductSearchResultDTO product = listItem.getModel().getObject();
        final CustomerWishList itemData = wishListDataByProduct.get(product);

        final ProductAvailabilityModel pam = productAvailabilityStrategy.getAvailabilityModel(ShopCodeContext.getShopId(), product);

        final PageParameters params = new PageParameters();
        params.add(WebParametersKeys.SKU_ID, itemData.getSkus().getSkuId());

        final String qty = itemData.getQuantity().setScale(0, RoundingMode.CEILING).toString();

        listItem.add(
                links.newAddToCartLink("addToCardFromWishListLink", product.getDefaultSkuCode(), qty, params)
                        .add(new Label("addToCardFromWishListLinkLabel", pam.isInStock() || pam.isPerpetual() ?
                                getLocalizer().getString("add.to.cart", this, new Model<Serializable>(new String[] { qty })) :
                                getLocalizer().getString("preorder.cart", this, new Model<Serializable>(new String[] { qty }))))
                        .setVisible(pam.isAvailable())
        );

        listItem.add(
                links.newRemoveFromWishListLink("removeFromWishListLink", product.getDefaultSkuCode(), itemData.getCustomerwishlistId(), (Class) getPage().getClass(), null)
                        .add(new Label("removeFromWishListLinkLabel", getLocalizer().getString("remove.from.wishlist", this))
                                .setVisible(showRemoveLink))
        );

    }
}
