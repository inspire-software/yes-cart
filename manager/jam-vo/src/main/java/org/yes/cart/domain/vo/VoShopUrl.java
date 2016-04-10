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
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor_Azarny on 4/4/2016.
 */
@Dto
public class VoShopUrl {


    @DtoField(value = "shop.shopId", readOnly = true)
    private long shopId;

    private List<VoShopUrlDetail> urls = new ArrayList<>();

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public List<VoShopUrlDetail> getUrls() {
        return urls;
    }

    public void setUrls(List<VoShopUrlDetail> urls) {
        this.urls = urls;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("shopId", shopId)
                .append("urls", urls)
                .build();
    }
}
