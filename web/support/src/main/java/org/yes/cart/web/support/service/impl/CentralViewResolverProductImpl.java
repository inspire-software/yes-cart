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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.SearchFeatures;
import org.yes.cart.search.SearchQueryFactory;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.utils.HttpUtil;

import java.util.Collections;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 10/04/2015
 * Time: 08:28
 */
public class CentralViewResolverProductImpl implements CentralViewResolver {

    private static final Pair<String, String> DEFAULT_S = new Pair<>(CentralViewLabel.SKU, CentralViewLabel.SKU);
    private static final Pair<String, String> DEFAULT_P = new Pair<>(CentralViewLabel.PRODUCT, CentralViewLabel.PRODUCT);

    private final ProductService productService;
    private final SearchQueryFactory searchQueryFactory;
    private final SearchFeatures searchFeatures;

    public CentralViewResolverProductImpl(final ProductService productService,
                                          final SearchQueryFactory searchQueryFactory,
                                          final SearchFeatures searchFeatures) {
        this.productService = productService;
        this.searchQueryFactory = searchQueryFactory;
        this.searchFeatures = searchFeatures;
    }

    /**
     * Resolve product view if applicable.
     * <p>
     * Rules:<p>
     * 1. If there is no {@link WebParametersKeys#SKU_ID} or {@link WebParametersKeys#PRODUCT_ID} then this resolver
     *    is not applicable, return null<p>
     * 2. If sku/product type has template then use template ({@link org.yes.cart.domain.entity.ProductType#getUitemplate()}).<p>
     * 3. If sku product type has template is blank then use template ({@link CentralViewLabel#SKU}) for SKU or
     *    ({@link CentralViewLabel#PRODUCT}) for product.<p>
     *
     * @param parameters            request parameters map
     *
     * @return product view label or null (if not applicable)
     */
    @Override
    public Pair<String, String> resolveMainPanelRendererLabel(final Map parameters) {
        if (parameters.containsKey(WebParametersKeys.SKU_ID)) {
            final long skuId = NumberUtils.toLong(HttpUtil.getSingleValue(parameters.get(WebParametersKeys.SKU_ID)));

            if (skuId > 0L) {

                if (!isPresentInSearchIndex(skuId, ProductSearchQueryBuilder.SKU_ID_FIELD)) {
                    return null; // Unable to find in FT
                }

                final ProductSku sku = productService.getSkuById(skuId, true); // Need to load it the same way as central view does
                if (sku != null) {
                    final String template = sku.getProduct().getProducttype().getUitemplate();
                    if (StringUtils.isNotBlank(template)) {
                        return new Pair<>(template, CentralViewLabel.SKU);
                    }
                    return DEFAULT_S;
                }
            }
        } else if (parameters.containsKey(WebParametersKeys.PRODUCT_ID)) {
            final long prodId = NumberUtils.toLong(HttpUtil.getSingleValue(parameters.get(WebParametersKeys.PRODUCT_ID)));

            if (prodId > 0L) {

                if (!isPresentInSearchIndex(prodId, ProductSearchQueryBuilder.PRODUCT_ID_FIELD)) {
                    return null; // Unable to find in FT
                }
                
                final Product product = productService.getProductById(prodId, true);  // Need to load it the same way as central view does
                if (product != null) {
                    final String template = product.getProducttype().getUitemplate();
                    if (StringUtils.isNotBlank(template)) {
                        return new Pair<>(template, CentralViewLabel.PRODUCT);
                    }
                    return DEFAULT_P;
                }
            }
        }
        return null;
    }

    private boolean isPresentInSearchIndex(final long objectId, final String idField) {

        if (searchFeatures.isFullTextEnabled()) {

            final ShoppingCart cart = ApplicationDirector.getShoppingCart();
            final long shopId = cart.getShoppingContext().getShopId();
            final long browsingShopId = cart.getShoppingContext().getCustomerShopId();

            final NavigationContext skuContext = searchQueryFactory.getFilteredNavigationQueryChain(shopId, browsingShopId, null, null,
                    false, Collections.singletonMap(idField,
                            Collections.singletonList(String.valueOf(objectId))));

            final int match = productService.getProductQty(skuContext);
            if (match == 0) {
                return false;
            }

        }
        return true;
    }
}
