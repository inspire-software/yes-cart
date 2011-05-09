package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.adapter.DtoToEntityMatcher;
import org.yes.cart.domain.dto.AttrValueCategoryDTO;
import org.yes.cart.domain.entity.AttrValueCategory;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AttrValueCategoryMatcher  implements DtoToEntityMatcher<AttrValueCategoryDTO, AttrValueCategory> {


    /**
     * @param attrValueCategoryDTO    DTO to match
     * @param attrValueCategory Entity to match
     * @return if this DTO matches to Entity
     */
    public boolean match(
            final AttrValueCategoryDTO attrValueCategoryDTO,
            final AttrValueCategory attrValueCategory) {
        return attrValueCategoryDTO != null
                &&
               attrValueCategory != null
                &&
               attrValueCategoryDTO.getAttrvalueId() == attrValueCategory.getAttrvalueId();
    }
}
