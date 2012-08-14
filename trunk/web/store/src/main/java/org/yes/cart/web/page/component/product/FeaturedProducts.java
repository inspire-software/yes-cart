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

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.service.domain.ShopCategoryService;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.util.WicketUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * Display featured product in particular category or entire shop.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 18-Sep-2011
 * Time: 11:03:57
 */
public class FeaturedProducts  extends AbstractProductList {


    @SpringBean(name = ServiceSpringKeys.SHOP_CATEGORY_SERVICE)
    private ShopCategoryService shopCategoryService;



    private List<Product> products = null;

    /**
     * Construct product list to show.
     *
     * @param id component id.
     */
    public FeaturedProducts(final String id) {
        super(id, false);
    }

    /** {@inheritDoc} */
    @Override
    public List<Product> getProductListToShow() {
        if (products == null) {
            final long categoryId = WicketUtil.getCategoryId(getPage().getPageParameters());
            final Collection<Long> categories;
            if (categoryId == 0) {
                categories = adapt(ApplicationDirector.getCurrentShop().getShopCategory());
            } else {
                categories = adapt(Collections.singletonList(
                        shopCategoryService.findByShopCategory(
                                ApplicationDirector.getCurrentShop(),
                                categoryService.getById(categoryId))
                ));
            }
            products = productService.getFeaturedProducts(
                    categories,
                    getProductsLimit());

        }
        return products;
    }

    /**
     * Transform list of categories to list of category ids
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
    public int getProductsLimit() {
        return 5;   //TODO from configuration
    }


}
