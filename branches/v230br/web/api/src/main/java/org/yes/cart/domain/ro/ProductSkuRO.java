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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.domain.entity.AttrValueProductSku;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/03/2015
 * Time: 10:44
 */
@Dto
@XmlRootElement(name = "sku")
public class ProductSkuRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(value = "skuId", readOnly = true)
    private long skuId;

    @DtoField(value = "code", readOnly = true)
    private String code;

    @DtoField(value = "manufacturerCode", readOnly = true)
    private String manufacturerCode;

    @DtoField(value = "name", readOnly = true)
    private String name;

    @DtoField(value = "displayName", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> displayNames;

    @DtoField(value = "description", readOnly = true)
    private String description;

    @DtoField(value = "product.productId", readOnly = true)
    private long productId;

    @DtoField(value = "rank", readOnly = true)
    private int rank;

    @DtoField(value = "barCode", readOnly = true)
    private String barCode;

    @DtoField(value = "seo.uri", entityBeanKeys = "org.yes.cart.domain.entity.Seo", readOnly = true)
    private String uri;

    @DtoField(value = "seo.title", entityBeanKeys = "org.yes.cart.domain.entity.Seo", readOnly = true)
    private String title;

    @DtoField(value = "seo.metakeywords", entityBeanKeys = "org.yes.cart.domain.entity.Seo", readOnly = true)
    private String metakeywords;

    @DtoField(value = "seo.metadescription", entityBeanKeys = "org.yes.cart.domain.entity.Seo", readOnly = true)
    private String metadescription;

    @DtoField(value = "seo.displayTitle", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> displayTitles;

    @DtoField(value = "seo.displayMetakeywords", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> displayMetakeywords;

    @DtoField(value = "seo.displayMetadescription", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> displayMetadescriptions;

    private String uitemplate;
    private String uitemplateFallback;

    private ProductAvailabilityModelRO productAvailabilityModel;

    private SkuPriceRO price;


    @DtoCollection(
            value = "attributes",
            dtoBeanKey = "org.yes.cart.domain.ro.AttrValueProductSkuRO",
            entityGenericType = AttrValueProductSku.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private Collection<AttrValueProductSkuRO> attributes;



    @XmlElement(name = "sku-id")
    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(final long skuId) {
        this.skuId = skuId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    @XmlElement(name = "manufacturer-code")
    public String getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-names")
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @XmlElement(name = "product-id")
    public long getProductId() {
        return productId;
    }

    public void setProductId(final long productId) {
        this.productId = productId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    @XmlElement(name = "barcode")
    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(final String barCode) {
        this.barCode = barCode;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getMetakeywords() {
        return metakeywords;
    }

    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    public String getMetadescription() {
        return metadescription;
    }

    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-titles")
    public Map<String, String> getDisplayTitles() {
        return displayTitles;
    }

    public void setDisplayTitles(final Map<String, String> displayTitles) {
        this.displayTitles = displayTitles;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-metakeywords")
    public Map<String, String> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    public void setDisplayMetakeywords(final Map<String, String> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-metadescription")
    public Map<String, String> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    public void setDisplayMetadescriptions(final Map<String, String> displayMetadescriptions) {
        this.displayMetadescriptions = displayMetadescriptions;
    }


    public String getUitemplate() {
        return uitemplate;
    }

    public void setUitemplate(final String uitemplate) {
        this.uitemplate = uitemplate;
    }

    @XmlElement(name = "uitemplate-fallback")
    public String getUitemplateFallback() {
        return uitemplateFallback;
    }

    public void setUitemplateFallback(final String uitemplateFallback) {
        this.uitemplateFallback = uitemplateFallback;
    }

    @XmlElement(name = "product-availability")
    public ProductAvailabilityModelRO getProductAvailabilityModel() {
        return productAvailabilityModel;
    }

    public void setProductAvailabilityModel(final ProductAvailabilityModelRO productAvailabilityModel) {
        this.productAvailabilityModel = productAvailabilityModel;
    }

    public SkuPriceRO getPrice() {
        return price;
    }

    public void setPrice(final SkuPriceRO price) {
        this.price = price;
    }

    @XmlElementWrapper(name = "attribute-values")
    @XmlElement(name = "attribute-value")
    public Collection<AttrValueProductSkuRO> getAttributes() {
        return attributes;
    }

    public void setAttributes(final Collection<AttrValueProductSkuRO> attributes) {
        this.attributes = attributes;
    }
}
