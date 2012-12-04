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


import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TSKUPRICE"
)
public class SkuPriceEntity implements org.yes.cart.domain.entity.SkuPrice, java.io.Serializable {


    private ProductSku sku;
    private Shop shop;
    private String currency;
    private BigDecimal quantity;
    private BigDecimal regularPrice;
    private BigDecimal salePrice;
    private BigDecimal minimalPrice;
    private Date salefrom;
    private Date saleto;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;
    private String tag;

    public SkuPriceEntity() {
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SKU_ID", nullable = false)
    public ProductSku getSku() {
        return this.sku;
    }

    public void setSku(ProductSku sku) {
        this.sku = sku;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOP_ID", nullable = false)
    public Shop getShop() {
        return this.shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Column(name = "CURRENCY", nullable = false, length = 3)
    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name = "QTY", nullable = false)
    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Column(name = "REGULAR_PRICE", nullable = false)
    public BigDecimal getRegularPrice() {
        return this.regularPrice;
    }

    public void setRegularPrice(BigDecimal regularPrice) {
        this.regularPrice = regularPrice;
    }

    @Column(name = "SALE_PRICE")
    public BigDecimal getSalePrice() {
        return this.salePrice;
    }

    /** {@inheritDoc} */
    @Transient
    public BigDecimal getSalePriceForCalculation() {  //TODOV2 time machine
        if (salefrom == null) {
            if (saleto == null) {
                return this.salePrice;
            } else {
                if (System.currentTimeMillis() < saleto.getTime()) {
                    return this.salePrice;  //sale not yet end
                } else {
                    return null; //the sale is end;
                }
            }
        } else {
            if (saleto == null) {
                if (System.currentTimeMillis() > salefrom.getTime()) {
                    return this.salePrice; //endless sale
                } else {
                    return null; // sale not yet started
                }
            } else {
                if (System.currentTimeMillis() > salefrom.getTime() && System.currentTimeMillis() < saleto.getTime()) {
                    return this.salePrice; //sale in time range
                } else {
                    return null;
                }
            }
        }
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    @Column(name = "MINIMAL_PRICE")
    public BigDecimal getMinimalPrice() {
        return this.minimalPrice;
    }

    public void setMinimalPrice(BigDecimal minimalPrice) {
        this.minimalPrice = minimalPrice;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SALE_FROM")
    public Date getSalefrom() {
        return this.salefrom;
    }

    public void setSalefrom(Date salefrom) {
        this.salefrom = salefrom;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SALE_TO")
    public Date getSaleto() {
        return this.saleto;
    }

    public void setSaleto(Date saleto) {
        this.saleto = saleto;
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

    @Column(name = "GUID", nullable = false, length = 36)
    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Column(name = "TAG", length = 45)
    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    // The following is extra code specified in the hbm.xml files


    private long skuPriceId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})
    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "SKUPRICE_ID", nullable = false)
    public long getSkuPriceId() {
        return this.skuPriceId;
    }

    @Transient
    public long getId() {
        return this.skuPriceId;
    }

    public void setSkuPriceId(long skuPriceId) {
        this.skuPriceId = skuPriceId;
    }


    // end of extra code specified in the hbm.xml files

}


