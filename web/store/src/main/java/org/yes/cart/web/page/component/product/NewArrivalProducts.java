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

import org.apache.lucene.search.Query;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.util.WicketUtil;

import java.util.Arrays;
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
public class NewArrivalProducts extends AbstractProductSearchResultList {

    @SpringBean(name = ServiceSpringKeys.LUCENE_QUERY_FACTORY)
    private LuceneQueryFactory luceneQueryFactory;

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
            final int limit = categoryService.getCategoryNewArrivalLimit(categoryId, shopId);

            final List<Long> newArrivalCats;
            if (categoryId > 0L) {
                newArrivalCats = Arrays.asList(categoryId);
            } else {
                newArrivalCats = null;
            }
            final Query newarrival = luceneQueryFactory.getFilteredNavigationQueryChain(shopId, newArrivalCats,
                    Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD,
                            (List) Arrays.asList(ProductSearchQueryBuilder.TAG_NEWARRIVAL)));

            products = productService.getProductSearchResultDTOByQuery(newarrival, 0, limit, null, true);
        }
        return products;
    }

}
