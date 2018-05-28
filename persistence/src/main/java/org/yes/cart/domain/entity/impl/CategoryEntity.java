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


import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.xml.CategoryPriceNavigationXStreamProvider;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.stream.xml.XStreamProvider;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CategoryEntity implements org.yes.cart.domain.entity.Category, java.io.Serializable {

    private long categoryId;
    private long version;

    private long parentId;
    private Long linkToId;
    private int rank;
    private ProductType productType;
    private String name;
    private String displayName;
    private String description;
    private String uitemplate;
    private boolean disabled;
    private LocalDateTime availablefrom;
    private LocalDateTime availableto;
    private Collection<AttrValueCategory> attributes = new ArrayList<>(0);
    private SeoEntity seoInternal;
    private Set<ProductCategory> productCategory = new HashSet<>(0);
    private Boolean navigationByAttributes;
    private Boolean navigationByPrice;
    private String navigationByPriceTiers;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CategoryEntity() {
    }



    @Override
    public long getParentId() {
        return this.parentId;
    }

    @Override
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override
    public Long getLinkToId() {
        return linkToId;
    }

    @Override
    public void setLinkToId(final Long linkToId) {
        this.linkToId = linkToId;
    }

    @Override
    public int getRank() {
        return this.rank;
    }

    @Override
    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public ProductType getProductType() {
        return this.productType;
    }

    @Override
    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getUitemplate() {
        return this.uitemplate;
    }

    @Override
    public void setUitemplate(String uitemplate) {
        this.uitemplate = uitemplate;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public LocalDateTime getAvailablefrom() {
        return this.availablefrom;
    }

    @Override
    public void setAvailablefrom(LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    @Override
    public LocalDateTime getAvailableto() {
        return this.availableto;
    }

    @Override
    public void setAvailableto(LocalDateTime availableto) {
        this.availableto = availableto;
    }

    @Override
    public Collection<AttrValueCategory> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(Collection<AttrValueCategory> attributes) {
        this.attributes = attributes;
    }

    public SeoEntity getSeoInternal() {
        return this.seoInternal;
    }

    public void setSeoInternal(SeoEntity seo) {
        this.seoInternal = seo;
    }

    @Override
    public Set<ProductCategory> getProductCategory() {
        return this.productCategory;
    }

    @Override
    public void setProductCategory(Set<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    @Override
    public Boolean getNavigationByAttributes() {
        return this.navigationByAttributes;
    }

    @Override
    public void setNavigationByAttributes(Boolean navigationByAttributes) {
        this.navigationByAttributes = navigationByAttributes;
    }

    @Override
    public Boolean getNavigationByPrice() {
        return this.navigationByPrice;
    }

    @Override
    public void setNavigationByPrice(Boolean navigationByPrice) {
        this.navigationByPrice = navigationByPrice;
    }

    @Override
    public String getNavigationByPriceTiers() {
        return this.navigationByPriceTiers;
    }

    @Override
    public void setNavigationByPriceTiers(String navigationByPriceTiers) {
        this.navigationByPriceTiers = navigationByPriceTiers;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public long getCategoryId() {
        return this.categoryId;
    }

    @Override
    public long getId() {
        return this.categoryId;
    }

    @Override
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryEntity that = (CategoryEntity) o;
        if (categoryId != that.categoryId) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return (int) (categoryId ^ (categoryId >>> 32));
    }

    private static final XStreamProvider<PriceTierTree> xStreamProvider = new CategoryPriceNavigationXStreamProvider();

    private PriceTierTree priceTierTreeCache = null;

    @Override
    public PriceTierTree getNavigationByPriceTree() {
        if (priceTierTreeCache == null && StringUtils.isNotBlank(getNavigationByPriceTiers())) {
            priceTierTreeCache = xStreamProvider.fromXML(getNavigationByPriceTiers()); //This method like to eat CPU
        }
        return priceTierTreeCache;
    }


    @Override
    public void setNavigationByPriceTree(PriceTierTree tree) {
        setNavigationByPriceTiers(xStreamProvider.toXML(tree));
        this.priceTierTreeCache = tree;
    }

    @Override
    public boolean isRoot() {
        return (getParentId() == 0L || getParentId() == getCategoryId());
    }

    public String toString() {
        return this.getClass().getName() + this.getCategoryId();
    }

    @Override
    public Set<AttrValueCategory> getAttributesByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        final Set<AttrValueCategory> result = new HashSet<>();
        if (this.attributes != null) {
            for (AttrValueCategory attrValue : this.attributes) {
                if (attributeCode.equals(attrValue.getAttributeCode())) {
                    result.add(attrValue);
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    @Override
    public AttrValueCategory getAttributeByCode(String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        if (this.attributes != null) {
            for (AttrValueCategory attrValue : this.attributes) {
                if (attributeCode.equals(attrValue.getAttributeCode())) {
                    return attrValue;
                }
            }
        }
        return null;
    }



    @Override
    public String getAttributeValueByCode(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null ? val.getVal() : null;
    }


    @Override
    public boolean isAttributeValueByCodeTrue(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null && Boolean.valueOf(val.getVal());
    }



    @Override
    public Map<String, AttrValue> getAllAttributesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<>();
        for (AttrValue attrValue : getAllAttributes()) {
            if (attrValue != null && attrValue.getAttributeCode() != null) {
                rez.put(attrValue.getAttributeCode(), attrValue);
            }
        }
        return rez;
    }

    @Override
    public Collection<AttrValue> getAllAttributes() {
        return new ArrayList<>(attributes);
    }


    /** {@inheritDoc} */
    @Override
    public Seo getSeo() {
        SeoEntity seo = getSeoInternal();
        if (seo == null) {
            seo = new SeoEntity();
            this.setSeoInternal(seo);
        }
        return seo;
    }

    /** {@inheritDoc} */
    @Override
    public void setSeo(final Seo seo) {
        this.setSeoInternal((SeoEntity) seo);
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


