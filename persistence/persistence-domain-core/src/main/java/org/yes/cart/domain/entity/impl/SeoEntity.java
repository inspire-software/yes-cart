/*
 * Copyright 2009 Inspire-Software.com
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


import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class SeoEntity implements org.yes.cart.domain.entity.Seo, java.io.Serializable {


    private String uri;
    private String title;
    private String displayTitleInternal;
    private I18NModel displayTitle;
    private String metakeywords;
    private String displayMetakeywordsInternal;
    private I18NModel displayMetakeywords;
    private String metadescription;
    private String displayMetadescriptionInternal;
    private I18NModel displayMetadescription;

    public SeoEntity() {
    }


    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public void setUri(final String uri) {
        this.uri = uri;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDisplayTitleInternal() {
        return displayTitleInternal;
    }

    public void setDisplayTitleInternal(final String displayTitleInternal) {
        this.displayTitleInternal = displayTitleInternal;
        this.displayTitle = new StringI18NModel(displayTitleInternal);
    }

    @Override
    public I18NModel getDisplayTitle() {
        return displayTitle;
    }

    @Override
    public void setDisplayTitle(final I18NModel displayTitle) {
        this.displayTitle = displayTitle;
        this.displayTitleInternal = displayTitle != null ? displayTitle.toString() : null;
    }

    @Override
    public String getMetakeywords() {
        return this.metakeywords;
    }

    @Override
    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    public String getDisplayMetakeywordsInternal() {
        return displayMetakeywordsInternal;
    }

    public void setDisplayMetakeywordsInternal(final String displayMetakeywordsInternal) {
        this.displayMetakeywordsInternal = displayMetakeywordsInternal;
        this.displayMetakeywords = new StringI18NModel(displayMetakeywordsInternal);
    }

    @Override
    public I18NModel getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    @Override
    public void setDisplayMetakeywords(final I18NModel displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
        this.displayMetakeywordsInternal = displayMetakeywords != null ? displayMetakeywords.toString() : null;
    }

    @Override
    public String getMetadescription() {
        return this.metadescription;
    }

    @Override
    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
    }

    public String getDisplayMetadescriptionInternal() {
        return displayMetadescriptionInternal;
    }

    public void setDisplayMetadescriptionInternal(final String displayMetadescriptionInternal) {
        this.displayMetadescriptionInternal = displayMetadescriptionInternal;
        this.displayMetadescription = new StringI18NModel(displayMetadescriptionInternal);
    }

    @Override
    public I18NModel getDisplayMetadescription() {
        return displayMetadescription;
    }

    @Override
    public void setDisplayMetadescription(final I18NModel displayMetadescription) {
        this.displayMetadescription = displayMetadescription;
        this.displayMetadescriptionInternal = displayMetadescription != null ? displayMetadescription.toString() : null;
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


