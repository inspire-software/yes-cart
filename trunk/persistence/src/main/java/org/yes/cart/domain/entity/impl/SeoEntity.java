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

package org.yes.cart.domain.entity.impl;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class SeoEntity implements org.yes.cart.domain.entity.Seo, java.io.Serializable {


    private String uri;
    private String title;
    private String metakeywords;
    private String metadescription;

    public SeoEntity() {
    }


    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetakeywords() {
        return this.metakeywords;
    }

    public void setMetakeywords(String metakeywords) {
        this.metakeywords = metakeywords;
    }

    public String getMetadescription() {
        return this.metadescription;
    }

    public void setMetadescription(String metadescription) {
        this.metadescription = metadescription;
    }

}


