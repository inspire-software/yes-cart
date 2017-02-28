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

/**
 * User: denispavlov
 * Date: 28/02/2017
 * Time: 15:02
 */
@Dto
public class VoShopAliasDetail {

    @DtoField(value = "storeAliasId")
    private long aliasId;

    @DtoField(value = "alias")
    private String alias;

    public VoShopAliasDetail(final long aliasId, final String alias) {
        this.aliasId = aliasId;
        this.alias = alias;
    }

    public VoShopAliasDetail() {
    }

    public long getAliasId() {
        return aliasId;
    }

    public void setAliasId(final long aliasId) {
        this.aliasId = aliasId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }
}
