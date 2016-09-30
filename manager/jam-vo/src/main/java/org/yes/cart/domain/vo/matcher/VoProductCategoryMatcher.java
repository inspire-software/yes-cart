package org.yes.cart.domain.vo.matcher;

import com.inspiresoftware.lib.dto.geda.adapter.DtoToEntityMatcher;
import org.yes.cart.domain.dto.ProductCategoryDTO;
import org.yes.cart.domain.vo.VoProductCategory;

/**
 * User: denispavlov
 * Date: 27/09/2016
 * Time: 16:06
 */
public class VoProductCategoryMatcher implements DtoToEntityMatcher<VoProductCategory, ProductCategoryDTO> {

    @Override
    public boolean match(final VoProductCategory voProductCategory, final ProductCategoryDTO productCategoryDTO) {
        return voProductCategory != null && productCategoryDTO != null && voProductCategory.getProductCategoryId() == productCategoryDTO.getProductCategoryId();
    }
}
