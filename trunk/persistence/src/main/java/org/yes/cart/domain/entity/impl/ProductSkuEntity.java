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
import org.hibernate.search.annotations.Indexed;
import org.yes.cart.domain.entity.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */

/**
 */
@Indexed(index = "luceneindex/productsku")
/*
*/
@Entity
@Table(name = "TSKU"
)
public class ProductSkuEntity implements org.yes.cart.domain.entity.ProductSku, java.io.Serializable {


    private String code;
    private String name;
    private String displayName;
    private String description;
    private Product product;
    private int rank;
    private String barCode;
    private Collection<SkuPrice> skuPrice = new ArrayList<SkuPrice>(0);
    private Collection<AttrValueProductSku> attributes = new ArrayList<AttrValueProductSku>(0);
    private Collection<SkuWarehouse> quantityOnWarehouse = new ArrayList<SkuWarehouse>(0);
    private SeoEntity seo;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductSkuEntity() {
    }




    @Column(name = "CODE", nullable = false)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "NAME", nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DISPLAYNAME", length = 4000)
    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Column(name = "DESCRIPTION", length = 4000)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUCT_ID")
    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Column(name = "RANK")
    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Column(name = "BARCODE", length = 128)
    public String getBarCode() {
        return this.barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "SKU_ID", nullable = false, updatable = false)
    @Cascade({CascadeType.ALL})
    public Collection<SkuPrice> getSkuPrice() {
        return this.skuPrice;
    }

    public void setSkuPrice(Collection<SkuPrice> skuPrice) {
        this.skuPrice = skuPrice;
    }

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "SKU_ID", updatable = false)
    @Cascade({CascadeType.ALL})
    public Collection<AttrValueProductSku> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Collection<AttrValueProductSku> attributes) {
        this.attributes = attributes;
    }

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "SKU_ID", updatable = false)
    @Cascade({CascadeType.ALL})
    public Collection<SkuWarehouse> getQuantityOnWarehouse() {
        return this.quantityOnWarehouse;
    }

    public void setQuantityOnWarehouse(Collection<SkuWarehouse> quantityOnWarehouse) {
        this.quantityOnWarehouse = quantityOnWarehouse;
    }

    @AttributeOverrides({
            @AttributeOverride(name = "uri", column = @Column(name = "URI")),
            @AttributeOverride(name = "title", column = @Column(name = "TITLE")),
            @AttributeOverride(name = "metakeywords", column = @Column(name = "METAKEYWORDS")),
            @AttributeOverride(name = "metadescription", column = @Column(name = "METADESCRIPTION"))})
    public SeoEntity getSeo() {
        return this.seo;
    }

    public void setSeo(SeoEntity seo) {
        this.seo = seo;
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


    private long skuId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})
    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "SKU_ID", nullable = false)
    public long getSkuId() {
        return this.skuId;
    }

    @Transient
    public long getId() {
        return this.skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    @Transient
    public Collection<AttrValueProductSku> getAttributesByCode(final String attributeCode) {
        final Collection<AttrValueProductSku> result = new ArrayList<AttrValueProductSku>();
        if (this.attributes != null) {
            for (AttrValueProductSku attrValue : this.attributes) {
                if (attrValue.getAttribute().getCode().equals(attributeCode)) {
                    result.add(attrValue);
                }
            }
        }
        return result;
    }

    @Transient
    public AttrValueProductSku getAttributeByCode(final String attributeCode) {
        if (this.attributes != null) {
            for (AttrValueProductSku attrValue : this.attributes) {
                if (attrValue.getAttribute().getCode().equals(attributeCode)) {
                    return attrValue;
                }
            }
        }
        return null;
    }


    @Transient
    public Map<String, AttrValue> getAllAttibutesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<String, AttrValue>();
        if (this.attributes != null) {
            for (AttrValue attrValue : this.attributes) {
                if (attrValue != null && attrValue.getAttribute() != null) {
                    rez.put(attrValue.getAttribute().getCode(), attrValue);
                }
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

    @Transient
    public BigDecimal getQty() {
        BigDecimal rez = BigDecimal.ZERO.setScale(2);
        for (SkuWarehouse swe : getQuantityOnWarehouse()) {
            rez = rez.add(swe.getQuantity());
        }
        return rez;
    }


    // end of extra code specified in the hbm.xml files

}


