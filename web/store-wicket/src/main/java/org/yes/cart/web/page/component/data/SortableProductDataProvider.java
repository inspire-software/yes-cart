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

package org.yes.cart.web.page.component.data;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;

import java.util.Iterator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:38 PM
 */


public class SortableProductDataProvider extends SortableDataProvider<ProductSearchResultDTO, String> {

    private ProductSearchResultPageDTO products;


    /**
     * Construct product data provider.
     *
     * @param products products page.
     */
    public SortableProductDataProvider(final ProductSearchResultPageDTO products) {
        this.products = products;
    }

    @Override
    public Iterator<? extends ProductSearchResultDTO> iterator(final long first, final long count) {
        return products.getResults().iterator();
    }


    @Override
    public long size() {
        return products.getTotalHits();
    }

    @Override
    public IModel<ProductSearchResultDTO> model(ProductSearchResultDTO productDecorator) {

        IModel<ProductSearchResultDTO> model = new IModel<ProductSearchResultDTO>() {

            private ProductSearchResultDTO product;

            @Override
            public ProductSearchResultDTO getObject() {
                return product;
            }

            @Override
            public void setObject(final ProductSearchResultDTO product) {
                this.product = product;
            }

            @Override
            public void detach() {
                //Nothing to do
            }
        };

        model.setObject(productDecorator);

        return model;


    }
}
