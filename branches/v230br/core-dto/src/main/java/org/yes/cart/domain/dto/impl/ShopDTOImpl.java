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

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttrValueShopDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.entity.AttrValueShop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class ShopDTOImpl implements ShopDTO {

    private static final long serialVersionUID = 20100528L;

    @DtoField(value = "code")
    private String code;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "fspointer")
    private String fspointer;

    @DtoField(value = "shopId", readOnly = true)
    private long shopId;

    @DtoField(value = "seo.uri", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String uri;

    @DtoField(value = "seo.title", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String title;

    @DtoField(value = "seo.metakeywords", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String metakeywords;

    @DtoField(value = "seo.metadescription", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String metadescription;

    @DtoField(value = "seo.displayTitle", converter = "i18nStringConverter")
    private Map<String, String> displayTitles;

    @DtoField(value = "seo.displayMetakeywords", converter = "i18nStringConverter")
    private Map<String, String> displayMetakeywords;

    @DtoField(value = "seo.displayMetadescription", converter = "i18nStringConverter")
    private Map<String, String> displayMetadescriptions;


    @DtoCollection(
            value="attributes",
            dtoBeanKey="org.yes.cart.domain.dto.AttrValueShopDTO",
            entityGenericType = AttrValueShop.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = IdentifiableMatcher.class,
            readOnly = true
    )
    private Collection<AttrValueShopDTO> attributes;



    /**
     * {@inheritDoc}
     */
    public long getId() {
        return shopId;
    }


    /**
     * {@inheritDoc}
     */
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    public String getFspointer() {
        return fspointer;
    }

    /**
     * {@inheritDoc}
     */
    public void setFspointer(final String fspointer) {
        this.fspointer = fspointer;
    }

    public long getShopId() {
        return shopId;
    }

    /**
     * {@inheritDoc}
     */
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }


    /**
     * {@inheritDoc}
     */
    public String getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getDisplayTitles() {
        return displayTitles;
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplayTitles(final Map<String, String> displayTitles) {
        this.displayTitles = displayTitles;
    }

    /**
     * {@inheritDoc}
     */
    public String getMetakeywords() {
        return metakeywords;
    }

    /**
     * {@inheritDoc}
     */
    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplayMetakeywords(final Map<String, String> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    /**
     * {@inheritDoc}
     */
    public String getMetadescription() {
        return metadescription;
    }

    /**
     * {@inheritDoc}
     */
    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplayMetadescriptions(final Map<String, String> displayMetadescriptions) {
        this.displayMetadescriptions = displayMetadescriptions;
    }

    /** {@inheritDoc}*/
    public Collection<AttrValueShopDTO> getAttributes() {
        return attributes;
    }

    /** {@inheritDoc}*/
    public void setAttributes(final Collection<AttrValueShopDTO> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "ShopDTOImpl{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", fspointer='" + fspointer + '\'' +
                ", shopId=" + shopId +
                '}';
    }
}
