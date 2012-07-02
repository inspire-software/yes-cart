package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.DtoToEntityMatcher;
import org.yes.cart.domain.dto.AttrValueProductSkuDTO;
import org.yes.cart.domain.entity.AttrValueProductSku;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AttrValueProductSkuMatcher  implements DtoToEntityMatcher<AttrValueProductSkuDTO, AttrValueProductSku> {

    /** {@inheritDoc} */
    public boolean match(
            final AttrValueProductSkuDTO attrValueProductSkuDTO,
            final AttrValueProductSku attrValueProductSku) {
        return attrValueProductSkuDTO != null &&
                attrValueProductSku != null &&
                attrValueProductSku.getAttrvalueId() ==  attrValueProductSkuDTO.getAttrvalueId();


    }
}
