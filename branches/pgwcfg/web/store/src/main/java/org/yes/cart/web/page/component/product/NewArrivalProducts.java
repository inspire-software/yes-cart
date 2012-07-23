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

import org.yes.cart.domain.entity.Product;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * Show new arrival. Current category will be selected to pick up
 * new arrivals.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 9/18/11
 * Time: 11:45 AM
 */
public class NewArrivalProducts extends AbstractProductList {


    private List<Product> products = null;

    /**
     * Construct product list to show.
     *
     * @param id component id.
     */
    public NewArrivalProducts(final String id) {
        super(id, true);
    }

    /** {@inheritDoc} */
    @Override
    public List<Product> getProductListToShow() {
        if (products == null) {
            products = productService.getNewArrivalsProductInCategory(
                    WicketUtil.getCategoryId(getPage().getPageParameters()),
                    getProductsLimit());
        }
        return products;
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
