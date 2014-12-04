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

package org.yes.cart.domain.dto.impl;

import org.apache.commons.lang.ObjectUtils;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

/**
 * User: denispavlov
 * Date: 04/12/2014
 * Time: 00:03
 */
public class ProductSkuSearchResultDTOImpl implements ProductSkuSearchResultDTO {

    private long id;
    private long productId;
    private String code;
    private String name;
    private String displayName;
    private String defaultImage;

    private I18NModel i18NModelName;


    /** {@inheritDoc} */
    public String getDefaultImage() {
        return defaultImage;
    }

    /** {@inheritDoc} */
    public void setDefaultImage(final String defaultImage) {
        this.defaultImage = defaultImage;
    }


    /** {@inheritDoc} */
    public long getId() {
        return id;
    }

    /** {@inheritDoc} */
    public void setId(final long id) {
        this.id = id;
    }

    /** {@inheritDoc} */
    public long getProductId() {
        return productId;
    }

    /** {@inheritDoc} */
    public void setProductId(final long productId) {
        this.productId = productId;
    }

    /** {@inheritDoc} */
    public String getCode() {
        return code;
    }

    /** {@inheritDoc} */
    public void setCode(final String code) {
        this.code = code;
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return displayName;
    }

    /** {@inheritDoc} */
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
        this.i18NModelName = new StringI18NModel(this.displayName);
    }


    /** {@inheritDoc} */
    public String getName(final String locale) {
        return (String) ObjectUtils.defaultIfNull(
                this.i18NModelName.getValue(locale),
                name
        );
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void setName(final String name) {
        this.name = name;
    }


    /** {@inheritDoc} */
    public ProductSkuSearchResultDTO copy() {
        final ProductSkuSearchResultDTOImpl copy = new ProductSkuSearchResultDTOImpl();
        copy.setId(this.id);
        copy.setProductId(this.productId);
        copy.setCode(this.code);
        copy.setName(this.name);
        copy.setDisplayName(this.displayName);
        copy.setDefaultImage(this.defaultImage);
        return copy;
    }

}
