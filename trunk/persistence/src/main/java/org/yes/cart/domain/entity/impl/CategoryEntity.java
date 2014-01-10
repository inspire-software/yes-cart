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


import org.apache.commons.lang.StringUtils;
import org.hibernate.search.annotations.*;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.xml.CategoryPriceNavigationXStreamProvider;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.stream.xml.XStreamProvider;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Indexed(index = "luceneindex/category")
public class CategoryEntity implements org.yes.cart.domain.entity.Category, java.io.Serializable {

    private long categoryId;
    private long version;

    private long parentId;
    private int rank;
    private ProductType productType;
    private String name;
    private String displayName;
    private String description;
    private String uitemplate;
    private Date availablefrom;
    private Date availableto;
    private Collection<AttrValueCategory> attributes = new ArrayList<AttrValueCategory>(0);
    private SeoEntity seoInternal;
    private Set<ProductCategory> productCategory = new HashSet<ProductCategory>(0);
    private Boolean navigationByAttributes;
    private Boolean navigationByBrand;
    private Boolean navigationByPrice;
    private String navigationByPriceTiers;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CategoryEntity() {
    }



    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public long getParentId() {
        return this.parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public ProductType getProductType() {
        return this.productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUitemplate() {
        return this.uitemplate;
    }

    public void setUitemplate(String uitemplate) {
        this.uitemplate = uitemplate;
    }

    public Date getAvailablefrom() {
        return this.availablefrom;
    }

    public void setAvailablefrom(Date availablefrom) {
        this.availablefrom = availablefrom;
    }

    public Date getAvailableto() {
        return this.availableto;
    }

    public void setAvailableto(Date availableto) {
        this.availableto = availableto;
    }

    public Collection<AttrValueCategory> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Collection<AttrValueCategory> attributes) {
        this.attributes = attributes;
    }

    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.SeoBridge.class)
    public SeoEntity getSeoInternal() {
        return this.seoInternal;
    }

    public void setSeoInternal(SeoEntity seo) {
        this.seoInternal = seo;
    }

    public Set<ProductCategory> getProductCategory() {
        return this.productCategory;
    }

    public void setProductCategory(Set<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    public Boolean getNavigationByAttributes() {
        return this.navigationByAttributes;
    }

    public void setNavigationByAttributes(Boolean navigationByAttributes) {
        this.navigationByAttributes = navigationByAttributes;
    }

    public Boolean getNavigationByBrand() {
        return this.navigationByBrand;
    }

    public void setNavigationByBrand(Boolean navigationByBrand) {
        this.navigationByBrand = navigationByBrand;
    }

    public Boolean getNavigationByPrice() {
        return this.navigationByPrice;
    }

    public void setNavigationByPrice(Boolean navigationByPrice) {
        this.navigationByPrice = navigationByPrice;
    }

    public String getNavigationByPriceTiers() {
        return this.navigationByPriceTiers;
    }

    public void setNavigationByPriceTiers(String navigationByPriceTiers) {
        this.navigationByPriceTiers = navigationByPriceTiers;
    }

    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @DocumentId
    public long getCategoryId() {
        return this.categoryId;
    }

    public long getId() {
        return this.categoryId;
    }

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

    public PriceTierTree getNavigationByPriceTree() {
        if (priceTierTreeCache == null && StringUtils.isNotBlank(getNavigationByPriceTiers())) {
            priceTierTreeCache = xStreamProvider.fromXML(getNavigationByPriceTiers()); //This method like to eat CPU
        }
        return priceTierTreeCache;
    }


    public void setNavigationByPriceTree(PriceTierTree tree) {
        setNavigationByPriceTiers(xStreamProvider.toXML(tree));
        this.priceTierTreeCache = tree;
    }

    @Override
    public boolean isRoot() {
        return (getParentId() == 0l || getParentId() == getCategoryId());
    }

    public String toString() {
        return this.getClass().getName() + this.getCategoryId();
    }

    public Set<AttrValueCategory> getAttributesByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        final Set<AttrValueCategory> result = new HashSet<AttrValueCategory>();
        if (this.attributes != null) {
            for (AttrValueCategory attrValue : this.attributes) {
                if (attrValue.getAttribute().getCode().equals(attributeCode)) {
                    result.add(attrValue);
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    public AttrValueCategory getAttributeByCode(String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        if (this.attributes != null) {
            for (AttrValueCategory attrValue : this.attributes) {
                if (attrValue.getAttribute() != null && attributeCode.equals(attrValue.getAttribute().getCode())) {
                    return attrValue;
                }
            }
        }
        return null;
    }

    public Map<String, AttrValue> getAllAttibutesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<String, AttrValue>();
        for (AttrValue attrValue : getAllAttibutes()) {
            if (attrValue != null && attrValue.getAttribute() != null) {
                rez.put(attrValue.getAttribute().getCode(), attrValue);
            }
        }
        return rez;
    }

    public Collection<AttrValue> getAllAttibutes() {
        return new ArrayList<AttrValue>(attributes);
    }


    /** {@inheritDoc} */
    public Seo getSeo() {
        SeoEntity seo = getSeoInternal();
        if (seo == null) {
            seo = new SeoEntity();
            this.setSeoInternal(seo);
        }
        return seo;
    }

    /** {@inheritDoc} */
    public void setSeo(final Seo seo) {
        this.setSeoInternal((SeoEntity) seo);
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


