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

package org.yes.cart.web.page.component.product;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.StringResourceModel;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.query.impl.ProductQueryBuilderImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.util.WicketUtil;

import java.util.Collections;
import java.util.List;

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

        add(new Label("productsTitle", new StringResourceModel("recentlyViewed", this, null)).setVisible(!getProductListToShow().isEmpty()));
        super.onBeforeRender();
    }

    /** {@inheritDoc} */
    @Override
    public List<ProductSearchResultDTO> getProductListToShow() {
        if (products == null) {

            List<String> productIds = ApplicationDirector.getShoppingCart().getShoppingContext().getLatestViewedSkus();
            if (CollectionUtils.isNotEmpty(productIds)) {
                final long categoryId = WicketUtil.getCategoryId(getPage().getPageParameters());

                int limit = getProductsLimit(categoryId);
                if (limit > productIds.size()) {
                    limit = productIds.size();
                } else {
                    productIds = productIds.subList(productIds.size() - limit, productIds.size());
                }

                final ProductQueryBuilderImpl ftq = new ProductQueryBuilderImpl();
                products = productService.getProductSearchResultDTOByQuery(
                        ftq.createQuery(productIds), 0, limit, "code", false);

            } else {

                products = Collections.emptyList();
            }

        }
        return products;
    }

    /**
     * Get quantity limit.
     *
     * @return quantity limit
     */
    public int getProductsLimit(final long categoryId) {
        final Category category = categoryService.getById(categoryId);
        if (category != null) {
            AttrValue av = category.getAttributeByCode(AttributeNamesKeys.Category.CATEGORY_ITEMS_NEW_ARRIVAL);
            if (av != null) {
                try {
                    return Integer.valueOf(av.getVal());
                } catch (Exception ex) {
                    return 5;
                }
            }
        }
        return 5;

    }

}
