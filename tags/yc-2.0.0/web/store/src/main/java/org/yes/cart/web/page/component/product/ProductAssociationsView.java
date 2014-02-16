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
import org.apache.wicket.RestartResponseException;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.query.impl.ProductQueryBuilderImpl;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    private List<ProductSearchResultDTO> associatedProductList = null;
    private final String associationType;

    /**
     * Construct product association view.
     *
     * @param id              component id
     * @param associationType type of association. See
     */
    public ProductAssociationsView(final String id, final String associationType) {
        super(id, true);
        this.associationType = associationType;
    }

    /**
     * Get product id from page parameters or from sku.
     *
     * @return product id
     */
    protected long getProductId() {
        final long productId;
        String productIdStr = getPage().getPageParameters().get(WebParametersKeys.PRODUCT_ID).toString();
        try {
            if (productIdStr == null) {
                long skuId = getPage().getPageParameters().get(WebParametersKeys.SKU_ID).toLong();
                productId = productService.getSkuById(skuId).getProduct().getProductId();
            } else {
                productId = Long.valueOf(productIdStr);
            }
            return productId;
        } catch (Exception exp) {
            throw new RestartResponseException(HomePage.class);
        }
    }

    /**
     * {@inheritDoc
     */
    public List<ProductSearchResultDTO> getProductListToShow() {
        if (associatedProductList == null) {
            final List<Long> productIds = productAssociationService.getProductAssociationsIds(
                    getProductId(),
                    associationType
            );

            if (productIds != null && !productIds.isEmpty()) {

                final Collection<String> ids = new ArrayList<String>();
                for (final Long productId : productIds) {
                    ids.add(String.valueOf(productId));
                }

                final ProductQueryBuilderImpl builder = new ProductQueryBuilderImpl();
                final BooleanQuery assoc = builder.createQuery(ids);

                associatedProductList = productService.getProductSearchResultDTOByQuery(
                        assoc, 0, productIds.size(), ProductSearchQueryBuilder.SKU_PRODUCT_CODE_FIELD, false);
            } else {

                associatedProductList = Collections.emptyList();
            }

        }
        return associatedProductList;
    }

}
