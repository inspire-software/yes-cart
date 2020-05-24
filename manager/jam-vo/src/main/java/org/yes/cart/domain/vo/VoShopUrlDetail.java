/*
 * Copyright 2009 Inspire-Software.com
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

/**
 * Created by Igor_Azarny on 4/5/2016.
 */
@Dto
public class VoShopUrlDetail {

    @DtoField(value = "storeUrlId")
    private long urlId;

    @DtoField(value = "url")
    private String url;

    @DtoField(value = "themeChain")
    private String theme;

    @DtoField(value = "primary")
    private boolean primary;


    public VoShopUrlDetail(final long urlId, final String url, final String theme, final boolean primary) {
        this.urlId = urlId;
        this.url = url;
        this.theme = theme;
        this.primary = primary;
    }

    public VoShopUrlDetail() {
    }

    public long getUrlId() {
        return urlId;
    }

    public void setUrlId(long urlId) {
        this.urlId = urlId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(final boolean primary) {
        this.primary = primary;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("urlId", urlId)
                .append("url", url)
                .append("theme", theme)
                .build();
    }
}
