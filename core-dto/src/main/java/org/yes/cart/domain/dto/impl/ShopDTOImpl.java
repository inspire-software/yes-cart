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

    @DtoField(value = "disabled", readOnly = true)
    private boolean disabled;

    @DtoField(value = "shopId", readOnly = true)
    private long shopId;

    @DtoField(value = "master.shopId", readOnly = true)
    private Long masterId;

    @DtoField(value = "master.code", readOnly = true)
    private String masterCode;

    @DtoField(value = "seo.uri", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String uri;

    @DtoField(value = "seo.title", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String title;

    @DtoField(value = "seo.metakeywords", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String metakeywords;

    @DtoField(value = "seo.metadescription", entityBeanKeys = "org.yes.cart.domain.entity.Seo")
    private String metadescription;

    @DtoField(value = "seo.displayTitle", converter = "i18nModelConverter")
    private Map<String, String> displayTitles;

    @DtoField(value = "seo.displayMetakeywords", converter = "i18nModelConverter")
    private Map<String, String> displayMetakeywords;

    @DtoField(value = "seo.displayMetadescription", converter = "i18nModelConverter")
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
    @Override
    public long getId() {
        return shopId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String getFspointer() {
        return fspointer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFspointer(final String fspointer) {
        this.fspointer = fspointer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public long getShopId() {
        return shopId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShopId(final long shopId) {
        this.shopId = shopId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getMasterId() {
        return masterId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMasterId(final Long masterId) {
        this.masterId = masterId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMasterCode() {
        return masterCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMasterCode(final String masterCode) {
        this.masterCode = masterCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDisplayTitles() {
        return displayTitles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayTitles(final Map<String, String> displayTitles) {
        this.displayTitles = displayTitles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMetakeywords() {
        return metakeywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayMetakeywords(final Map<String, String> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMetadescription() {
        return metadescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayMetadescriptions(final Map<String, String> displayMetadescriptions) {
        this.displayMetadescriptions = displayMetadescriptions;
    }

    /** {@inheritDoc}*/
    @Override
    public Collection<AttrValueShopDTO> getAttributes() {
        return attributes;
    }

    /** {@inheritDoc}*/
    @Override
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
