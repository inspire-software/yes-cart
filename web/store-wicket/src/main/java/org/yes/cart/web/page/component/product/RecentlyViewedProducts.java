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

package org.yes.cart.web.page.component.product;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.groovy.util.ListHashMap;
import org.yes.cart.domain.dto.ProductSearchResultDTO;

import java.util.ArrayList;
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
public class RecentlyViewedProducts extends AbstractProductSearchResultList {

    private List<ProductSearchResultDTO> products = null;

    /**
     * Construct product list to show.
     *
     * @param id component id.
     */
    public RecentlyViewedProducts(final String id) {
        super(id, true);
    }

    @Override
    protected void onBeforeRender() {

        super.onBeforeRender();
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductSearchResultDTO> getProductListToShow() {
        if (products == null) {

            final long categoryId = getWicketUtil().getCategoryId(getPage().getPageParameters());
            final List<String> viewedSkus = getCurrentCart().getShoppingContext().getLatestViewedSkus();
            if (CollectionUtils.isNotEmpty(viewedSkus)) {
                final Map<String, List<String>> skuAndSupplier = new ListHashMap<>();
                for (final String viewedSku : viewedSkus) {
                    final int pos = viewedSku.indexOf("|");
                    if (pos != -1) {
                        final String skuCode = viewedSku.substring(0, pos);
                        final String skuSupplier = viewedSku.substring(pos + 1);
                        final List<String> skuSuppliers = skuAndSupplier.computeIfAbsent(skuCode, k -> new ArrayList<>());
                        skuSuppliers.add(skuSupplier);
                    }
                }

                final long shopId = getCurrentShopId();
                final long browsingShopId = getCurrentCustomerShopId();
                final List<ProductSearchResultDTO> viewedProducts =
                        new ArrayList<>(productServiceFacade.getListProductSKUs(
                                new ArrayList<>(skuAndSupplier.keySet()), categoryId, shopId, browsingShopId));
                viewedProducts.removeIf(viewed -> {
                    final List<String> skuSuppliers = skuAndSupplier.get(viewed.getDefaultSkuCode());
                    return skuSuppliers == null || !skuSuppliers.contains(viewed.getFulfilmentCentreCode());
                });
                products = viewedProducts;
            } else {
                products = new ArrayList<>();
            }

        }
        return products;
    }

}
