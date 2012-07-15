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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.DtoToEntityMatcher;
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
