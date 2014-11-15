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

import org.apache.lucene.search.BooleanQuery;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.query.impl.TagSearchQueryBuilder;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.util.WicketUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Show new arrival. Current category will be selected to pick up
 * new arrivals.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 9/18/11
 * Time: 11:45 AM
 */
public class NewArrivalProducts extends AbstractProductSearchResultList {

    private final TagSearchQueryBuilder tagSearch = new TagSearchQueryBuilder();

    private List<ProductSearchResultDTO> products = null;

    /**
     * Construct product list to show.
     *
     * @param id component id.
     */
    public NewArrivalProducts(final String id) {
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
            final long categoryId = WicketUtil.getCategoryId(getPage().getPageParameters());

            final long shopId = ShopCodeContext.getShopId();
            final Date beforeDays = categoryService.getCategoryNewArrivalDate(categoryId, shopId);
            final int limit = categoryService.getCategoryNewArrivalLimit(categoryId, shopId);

            final BooleanQuery query;
            if (categoryId > 0L) {
                query = tagSearch.createQuery(Arrays.asList(categoryId), shopId, beforeDays);
            } else {
                query = tagSearch.createQuery(null, shopId, beforeDays);
            }

            products = productService.getProductSearchResultDTOByQuery(query, 0, limit, null, true);
        }
        return products;
    }

}
