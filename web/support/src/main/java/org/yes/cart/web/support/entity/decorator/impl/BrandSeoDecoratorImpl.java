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

package org.yes.cart.web.support.entity.decorator.impl;

import org.yes.cart.domain.entity.Brand;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

/**
 * User: denispavlov
 * Date: 30/01/2016
 * Time: 22:59
 */
public class BrandSeoDecoratorImpl implements Seo {

    private final Brand brand;

    public BrandSeoDecoratorImpl(final Brand brand) {
        this.brand = brand;
    }

    public String getUri() {
        return ProductSearchQueryBuilder.BRAND_FIELD + "/" + this.brand.getName().toLowerCase();
    }

    public void setUri(final String uri) {
        throw new UnsupportedOperationException("Read only");
    }

    public String getTitle() {
        return this.brand.getName();
    }

    public void setTitle(final String title) {
        throw new UnsupportedOperationException("Read only");
    }

    public String getDisplayTitle() {
        return null;
    }

    public void setDisplayTitle(final String title) {
        throw new UnsupportedOperationException("Read only");
    }

    public String getMetakeywords() {
        return this.brand.getName();
    }

    public void setMetakeywords(final String metakeywords) {
        throw new UnsupportedOperationException("Read only");
    }

    public String getDisplayMetakeywords() {
        return null;
    }

    public void setDisplayMetakeywords(final String metakeywords) {
        throw new UnsupportedOperationException("Read only");
    }

    public String getMetadescription() {
        return null;
    }

    public void setMetadescription(final String metadescription) {
        throw new UnsupportedOperationException("Read only");
    }

    public String getDisplayMetadescription() {
        return null;
    }

    public void setDisplayMetadescription(final String metadescription) {
        throw new UnsupportedOperationException("Read only");
    }
}
