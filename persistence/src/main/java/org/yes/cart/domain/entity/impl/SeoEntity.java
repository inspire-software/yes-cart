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

package org.yes.cart.domain.entity.impl;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class SeoEntity implements org.yes.cart.domain.entity.Seo, java.io.Serializable {


    private String uri;
    private String title;
    private String displayTitle;
    private String metakeywords;
    private String displayMetakeywords;
    private String metadescription;
    private String displayMetadescription;

    public SeoEntity() {
    }


    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDisplayTitle() {
        return displayTitle;
    }

    @Override
    public void setDisplayTitle(final String displayTitle) {
        this.displayTitle = displayTitle;
    }

    @Override
    public String getMetakeywords() {
        return this.metakeywords;
    }

    @Override
    public void setMetakeywords(String metakeywords) {
        this.metakeywords = metakeywords;
    }

    @Override
    public String getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    @Override
    public void setDisplayMetakeywords(final String displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    @Override
    public String getMetadescription() {
        return this.metadescription;
    }

    @Override
    public void setMetadescription(String metadescription) {
        this.metadescription = metadescription;
    }

    @Override
    public String getDisplayMetadescription() {
        return displayMetadescription;
    }

    @Override
    public void setDisplayMetadescription(final String displayMetadescription) {
        this.displayMetadescription = displayMetadescription;
    }

    @Override
    public String toString() {
        return "SeoEntity{" +
                "uri:'" + uri + '\'' +
                ",title:'" + title + '\'' +
                ",displayTitle:'" + displayTitle + '\'' +
                ",metakeywords:'" + metakeywords + '\'' +
                ",displayMetakeywords:'" + displayMetakeywords + '\'' +
                ",metadescription:'" + metadescription + '\'' +
                ",displayMetadescription:'" + displayMetadescription + '\'' +
                '}';
    }
}


