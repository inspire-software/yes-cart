package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.adapter.DtoToEntityMatcher;
import org.yes.cart.domain.dto.ProductCategoryDTO;
import org.yes.cart.domain.entity.ProductCategory;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductCategoryMatcher  implements DtoToEntityMatcher<ProductCategoryDTO, ProductCategory> {


    /** {@inheritDoc} */
    public boolean match(final ProductCategoryDTO productCategoryDTO, final ProductCategory productCategory) {
        return (productCategoryDTO != null)
                && (productCategory != null)
                && (productCategory.getProductCategoryId() == productCategoryDTO.getProductCategoryId());
    }
    
}
