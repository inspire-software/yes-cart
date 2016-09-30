/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/09/2016
 * Time: 09:08
 */
@Dto
public class VoProductWithLinks extends VoProduct {

    private List<VoProductAssociation> associations;

    public List<VoProductAssociation> getAssociations() {
        return associations;
    }

    public void setAssociations(final List<VoProductAssociation> associations) {
        this.associations = associations;
    }

}
