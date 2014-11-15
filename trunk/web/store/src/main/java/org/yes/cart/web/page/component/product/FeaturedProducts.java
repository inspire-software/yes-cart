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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.domain.query.impl.FeaturedProductsInCategoryQueryBuilderImpl;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.util.WicketUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Display featured product in particular category or entire shop.
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 18-Sep-2011
 * Time: 11:03:57
 */
public class FeaturedProducts extends AbstractProductSearchResultList {

    @SpringBean(name = ServiceSpringKeys.SHOP_SERVICE)
    private ShopService shopService;


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
            final long categoryId = WicketUtil.getCategoryId(getPage().getPageParameters());
            final List<Long> categories;
            if (categoryId == 0) {
                categories = Collections.EMPTY_LIST;
            } else {

                if (shopService.getShopCategoriesIds(ShopCodeContext.getShopId()).contains(categoryId)) {
                    categories = Collections.singletonList(categoryId);
                } else {
                    categories = Collections.EMPTY_LIST;
                }
            }

            final FeaturedProductsInCategoryQueryBuilderImpl queryBuilder = new FeaturedProductsInCategoryQueryBuilderImpl();

            final BooleanQuery featured = queryBuilder.createQuery(categories, ShopCodeContext.getShopId());

            products = productService.getProductSearchResultDTOByQuery(featured, 0, getProductsLimit(categoryId), null, false);

        }
        return products;
    }

    /**
     * Transform list of categories to list of category ids
     *
     * @param categories given category list
     * @return category IDs.
     */
    private List<Long> adapt(final Collection<ShopCategory> categories) {
        if (categories == null) {
            return Collections.EMPTY_LIST;
        } else {
            final List<Long> rez = new ArrayList<Long>(categories.size());
            for (ShopCategory cat : categories) {
                rez.add(cat.getCategory().getCategoryId());
            }
            return rez;
        }
    }

    /**
     * Get quantity limit.
     *
     * @return quantity limit
     */
    public int getProductsLimit(final long categoryId) {
        final Category category = categoryService.getById(categoryId);
        if (category != null) {
            AttrValue av = category.getAttributeByCode(AttributeNamesKeys.Category.CATEGORY_ITEMS_FEATURED);
            if (av != null) {
                try {
                    return Integer.valueOf(av.getVal());
                } catch (Exception ex) {
                    return 7;
                }
            }
        }
        return 15;
    }


}
