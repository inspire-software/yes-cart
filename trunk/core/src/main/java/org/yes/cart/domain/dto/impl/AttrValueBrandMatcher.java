package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.DtoToEntityMatcher;
import org.yes.cart.domain.dto.AttrValueBrandDTO;
import org.yes.cart.domain.entity.AttrValueBrand;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AttrValueBrandMatcher implements DtoToEntityMatcher<AttrValueBrandDTO, AttrValueBrand> {

    /** {@inheritDoc}*/
    public boolean match(final AttrValueBrandDTO attrValueBrandDTO, final AttrValueBrand attrValueBrand) {
        return attrValueBrandDTO != null
                &&
               attrValueBrand != null
                &&
               attrValueBrandDTO.getAttrvalueId() == attrValueBrand.getAttrvalueId();
    }
}
