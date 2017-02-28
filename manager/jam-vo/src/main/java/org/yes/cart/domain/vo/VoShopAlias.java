/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 28/02/2017
 * Time: 15:01
 */
@Dto
public class VoShopAlias {

    @DtoField(value = "shop.shopId", readOnly = true)
    private long shopId;

    private List<VoShopAliasDetail> aliases = new ArrayList<>();

    public long getShopId() {
        return shopId;
    }

    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    public List<VoShopAliasDetail> getAliases() {
        return aliases;
    }

    public void setAliases(final List<VoShopAliasDetail> aliases) {
        this.aliases = aliases;
    }
}
