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
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.util.ShopCodeContext;

import java.util.List;

/**
 * Product association panel, that can show configured from markup
 * associations like cross/up/accessories/sell/etc
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 16-Sep-2011
 * Time: 15:36:10
 */
public class ProductAssociationsView extends AbstractProductSearchResultList {

    @SpringBean(name = ServiceSpringKeys.LUCENE_QUERY_FACTORY)
    private LuceneQueryFactory luceneQueryFactory;

    private List<ProductSearchResultDTO> associatedProductList = null;
    private final long productId;
    private final String associationType;

    /**
     * Construct product association view.
     *
     * @param id              component id
     * @param associationType type of association. See
     */
    public ProductAssociationsView(final String id, final long productId, final String associationType) {
        super(id, true);
        this.productId = productId;
        this.associationType = associationType;
    }

    /**
     * Get product id from page parameters or from sku.
     *
     * @return product id
     */
    protected long getProductId() {
        return productId;
    }

    /**
     * {@inheritDoc
     */
    public List<ProductSearchResultDTO> getProductListToShow() {
        if (associatedProductList == null) {

            associatedProductList = productServiceFacade.getProductAssociations(
                    getProductId(), ShopCodeContext.getShopId(), associationType);

        }
        return associatedProductList;
    }

}
