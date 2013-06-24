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


import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.search.annotations.*;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.xml.CategoryPriceNavigationXStreamProvider;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.stream.xml.XStreamProvider;

import javax.persistence.*;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Indexed(index = "luceneindex/category")
@Entity
@Table(name = "TCATEGORY")
public class CategoryEntity implements org.yes.cart.domain.entity.Category, java.io.Serializable {


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
    private SeoEntity seo;
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



    @Column(name = "PARENT_ID")
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public long getParentId() {
        return this.parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Column(name = "RANK")
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade({CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "PRODUCTTYPE_ID")
    public ProductType getProductType() {
        return this.productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    @Column(name = "NAME", nullable = false)
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DISPLAYNAME", length = 4000)
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Column(name = "DESCRIPTION", length = 4000)
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "UITEMPLATE")
    public String getUitemplate() {
        return this.uitemplate;
    }

    public void setUitemplate(String uitemplate) {
        this.uitemplate = uitemplate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AVAILABLEFROM")
    public Date getAvailablefrom() {
        return this.availablefrom;
    }

    public void setAvailablefrom(Date availablefrom) {
        this.availablefrom = availablefrom;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AVAILABLETO")
    public Date getAvailableto() {
        return this.availableto;
    }

    public void setAvailableto(Date availableto) {
        this.availableto = availableto;
    }

    @OneToMany(cascade = javax.persistence.CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "category")
    public Collection<AttrValueCategory> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Collection<AttrValueCategory> attributes) {
        this.attributes = attributes;
    }

    @AttributeOverrides({
            @AttributeOverride(name = "uri", column = @Column(name = "URI")),
            @AttributeOverride(name = "title", column = @Column(name = "TITLE")),
            @AttributeOverride(name = "metakeywords", column = @Column(name = "METAKEYWORDS")),
            @AttributeOverride(name = "metadescription", column = @Column(name = "METADESCRIPTION"))})
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.SeoBridge.class)
    public SeoEntity getSeo() {
        return this.seo;
    }

    public void setSeo(SeoEntity seo) {
        this.seo = seo;
    }

    @OneToMany(cascade = javax.persistence.CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", updatable = false)
    public Set<ProductCategory> getProductCategory() {
        return this.productCategory;
    }

    public void setProductCategory(Set<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    @Column(name = "NAV_BY_ATTR", length = 1)
    public Boolean getNavigationByAttributes() {
        return this.navigationByAttributes;
    }

    public void setNavigationByAttributes(Boolean navigationByAttributes) {
        this.navigationByAttributes = navigationByAttributes;
    }

    @Column(name = "NAV_BY_BRAND", length = 1)
    public Boolean getNavigationByBrand() {
        return this.navigationByBrand;
    }

    public void setNavigationByBrand(Boolean navigationByBrand) {
        this.navigationByBrand = navigationByBrand;
    }

    @Column(name = "NAV_BY_PRICE", length = 1)
    public Boolean getNavigationByPrice() {
        return this.navigationByPrice;
    }

    public void setNavigationByPrice(Boolean navigationByPrice) {
        this.navigationByPrice = navigationByPrice;
    }

    @Column(name = "NAV_BY_PRICE_TIERS", length = 4000)
    public String getNavigationByPriceTiers() {
        return this.navigationByPriceTiers;
    }

    public void setNavigationByPriceTiers(String navigationByPriceTiers) {
        this.navigationByPriceTiers = navigationByPriceTiers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIMESTAMP")
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_TIMESTAMP")
    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Column(name = "CREATED_BY", length = 64)
    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED_BY", length = 64)
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "GUID", unique = true, nullable = false, length = 36)
    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    // The following is extra code specified in the hbm.xml files


    private long categoryId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})
    @Id
    @GeneratedValue
    @Column(name = "CATEGORY_ID", nullable = false)
    @DocumentId
    public long getCategoryId() {
        return this.categoryId;
    }

    @Transient
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

    @Transient
    public PriceTierTree getNavigationByPriceTree() {
        if (priceTierTreeCache == null && getNavigationByPriceTiers() != null) {
            priceTierTreeCache = xStreamProvider.fromXML(getNavigationByPriceTiers()); //This method like to eat CPU
        }
        return priceTierTreeCache;
    }


    public void setNavigationByPriceTree(PriceTierTree tree) {
        setNavigationByPriceTiers(xStreamProvider.toXML(tree));
        this.priceTierTreeCache = tree;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + this.getCategoryId();
    }

    @Transient
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

    @Transient
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

    @Transient
    public Map<String, AttrValue> getAllAttibutesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<String, AttrValue>();
        for (AttrValue attrValue : getAllAttibutes()) {
            if (attrValue != null && attrValue.getAttribute() != null) {
                rez.put(attrValue.getAttribute().getCode(), attrValue);
            }
        }
        return rez;
    }

    @Transient
    public Collection<AttrValue> getAllAttibutes() {
        return new ArrayList<AttrValue>(attributes);
    }

    /**
     * {@inheritDoc}
     */
    public void setSeo(final Seo seo) {
        this.seo = (SeoEntity) seo;
    }


    // end of extra code specified in the hbm.xml files

}


