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
import org.yes.cart.domain.misc.MutablePair;

import java.util.List;

/**
 * Created by Igor_Azarny on 3/28/2016.
 */
@Dto
public class VoShopSeo {

    @DtoField(value = "shopId", readOnly = true)
    private long shopId;

    @DtoField(value = "uri")
    private String uri;

    @DtoField(value = "title")
    private String title;

    @DtoField(value = "metakeywords")
    private String metakeywords;

    @DtoField(value = "metadescription")
    private String metadescription;

    @DtoField(value = "displayTitles", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayTitles;

    @DtoField(value = "displayMetakeywords", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayMetakeywords;

    @DtoField(value = "displayMetadescriptions", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayMetadescriptions;


    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetakeywords() {
        return metakeywords;
    }

    public void setMetakeywords(String metakeywords) {
        this.metakeywords = metakeywords;
    }

    public String getMetadescription() {
        return metadescription;
    }

    public void setMetadescription(String metadescription) {
        this.metadescription = metadescription;
    }

    public List<MutablePair<String, String>> getDisplayTitles() {
        return displayTitles;
    }

    public void setDisplayTitles(List<MutablePair<String, String>> displayTitles) {
        this.displayTitles = displayTitles;
    }

    public List<MutablePair<String, String>> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    public void setDisplayMetakeywords(List<MutablePair<String, String>> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    public List<MutablePair<String, String>> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    public void setDisplayMetadescriptions(List<MutablePair<String, String>> displayMetadescriptions) {
        this.displayMetadescriptions = displayMetadescriptions;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("shopId", shopId)
                .append("uri", uri)
                .append("title", title)
                .append("metakeywords", metakeywords)
                .append("metadescription", metadescription)
                .build();
    }
}
