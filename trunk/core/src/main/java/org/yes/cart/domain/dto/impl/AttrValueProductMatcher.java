package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.DtoToEntityMatcher;
import org.yes.cart.domain.dto.AttrValueProductDTO;
import org.yes.cart.domain.entity.AttrValueProduct;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AttrValueProductMatcher implements DtoToEntityMatcher<AttrValueProductDTO, AttrValueProduct> {

    /** {@inheritDoc} */
    public boolean match(final AttrValueProductDTO attrValueProductDTO, final AttrValueProduct attrValueProduct) {
        return attrValueProductDTO != null
                &&
                attrValueProduct != null
                && attrValueProduct.getAttrvalueId() == attrValueProductDTO.getAttrvalueId();
    }
}
