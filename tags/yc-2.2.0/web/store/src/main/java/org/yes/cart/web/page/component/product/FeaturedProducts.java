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

import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * Display featured product in particular category or entire shop.
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 18-Sep-2011
 * Time: 11:03:57
 */
public class FeaturedProducts extends AbstractProductSearchResultList {

    private List<ProductSearchResultDTO> products = null;

    /**
     * Construct product list to show.
     *
     * @param id component id.
     */
    public FeaturedProducts(final String id) {
        super(id, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductSearchResultDTO> getProductListToShow() {
        if (products == null) {

            final long shopId = ShopCodeContext.getShopId();
            final long categoryId = WicketUtil.getCategoryId(getPage().getPageParameters());

            products = productServiceFacade.getFeaturedProducts(categoryId, shopId);

        }
        return products;
    }

}
