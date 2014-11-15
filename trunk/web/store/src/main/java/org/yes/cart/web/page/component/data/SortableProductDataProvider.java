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

package org.yes.cart.web.page.component.data;

import org.apache.lucene.search.Query;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.support.entity.decorator.DecoratorFacade;
import org.yes.cart.web.support.i18n.I18NWebSupport;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:38 PM
 */


public class SortableProductDataProvider extends SortableDataProvider<ProductSearchResultDTO> {

    private final ProductService productService;
    private final Query query;
    private String sortFieldName = null;
    private boolean reverse = false;
    private List<ProductSearchResultDTO> products;
    private final I18NWebSupport i18NWebSupport;
    private final DecoratorFacade decoratorFacade;


    /**
     * Construct product data provider.
     *
     * @param productService  product service to get the products.
     * @param query           lucene query.
     * @param i18NWebSupport  i18n
     * @param decoratorFacade decorator facade
     */
    public SortableProductDataProvider(final ProductService productService,
                                       final Query query,
                                       final I18NWebSupport i18NWebSupport,
                                       final DecoratorFacade decoratorFacade) {
        this.productService = productService;
        this.query = query;
        this.i18NWebSupport = i18NWebSupport;
        this.decoratorFacade = decoratorFacade;
    }

    public Iterator<? extends ProductSearchResultDTO> iterator(int first, int count) {
        if (query == null || size() == 0) {
            products = Collections.EMPTY_LIST;
        } else {
            products = productService.getProductSearchResultDTOByQuery(
                    query,
                    first,
                    count,
                    sortFieldName,
                    reverse);
        }

        return products.iterator();
    }


   /*private List<ProductDecorator> decorate(final List<ProductSearchResultDTO> productsToDecorate) {
        final List<ProductDecorator> rez = new ArrayList<ProductDecorator>(productsToDecorate.size());
        for (ProductSearchResultDTO product : productsToDecorate) {
            rez.add(
                    decoratorFacade.decorate(
                            product,
                            WicketUtil.getHttpServletRequest().getContextPath(),
                            i18NWebSupport, false)
            );
        }
        return rez;
    } */


    public int size() {
        if (query != null) {
            return productService.getProductQty(query);
        }
        return 0;
    }

    public String getSortFieldName() {
        return sortFieldName;
    }

    public void setSortFieldName(final String sortFieldName) {
        this.sortFieldName = sortFieldName;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(final boolean reverse) {
        this.reverse = reverse;
    }

    public IModel<ProductSearchResultDTO> model(ProductSearchResultDTO productDecorator) {

        IModel<ProductSearchResultDTO> model = new IModel<ProductSearchResultDTO>() {

            private ProductSearchResultDTO product;

            public ProductSearchResultDTO getObject() {
                return product;
            }

            public void setObject(final ProductSearchResultDTO product) {
                this.product = product;
            }

            public void detach() {
                //Nothing to do
            }
        };

        model.setObject(productDecorator);

        return model;


    }
}
