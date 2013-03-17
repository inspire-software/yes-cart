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


import org.springframework.util.Assert;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;

import javax.persistence.*;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
@Entity
@Table(name = "TSHOP"
)
public class ShopEntity implements org.yes.cart.domain.entity.Shop, java.io.Serializable {


    private String code;
    private String name;
    private String description;
    private String fspointer;
    private String imageVaultFolder;
    private Set<ShopUrl> shopUrl = new HashSet<ShopUrl>(0);
    private Collection<ShopExchangeRate> exchangerates = new ArrayList<ShopExchangeRate>(0);
    private Collection<ShopAdvPlace> advertisingPlaces = new ArrayList<ShopAdvPlace>(0);
    private Collection<ShopDiscount> shopDiscountRules = new ArrayList<ShopDiscount>(0);
    private Collection<AttrValueShop> attributes = new ArrayList<AttrValueShop>(0);
    private SeoEntity seo;
    private Collection<ShopCategory> shopCategory = new ArrayList<ShopCategory>(0);
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ShopEntity() {
    }



    @Column(name = "CODE", nullable = false)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "NAME", nullable = false, length = 64)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION", length = 4000)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "FSPOINTER", nullable = false, length = 4000)
    public String getFspointer() {
        return this.fspointer;
    }

    public void setFspointer(String fspointer) {
        this.fspointer = fspointer;
    }

    @Column(name = "IMGVAULT", nullable = false, length = 4000)
    public String getImageVaultFolder() {
        return this.imageVaultFolder;
    }

    public void setImageVaultFolder(String imageVaultFolder) {
        this.imageVaultFolder = imageVaultFolder;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "shop")
    public Set<ShopUrl> getShopUrl() {
        return this.shopUrl;
    }

    public void setShopUrl(Set<ShopUrl> shopUrl) {
        this.shopUrl = shopUrl;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "SHOP_ID", nullable = false, updatable = false)
    public Collection<ShopExchangeRate> getExchangerates() {
        return this.exchangerates;
    }

    public void setExchangerates(Collection<ShopExchangeRate> exchangerates) {
        this.exchangerates = exchangerates;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "SHOP_ID", nullable = false, updatable = false)
    public Collection<ShopAdvPlace> getAdvertisingPlaces() {
        return this.advertisingPlaces;
    }

    public void setAdvertisingPlaces(Collection<ShopAdvPlace> advertisingPlaces) {
        this.advertisingPlaces = advertisingPlaces;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "shop")
    public Collection<ShopDiscount> getShopDiscountRules() {
        return this.shopDiscountRules;
    }

    public void setShopDiscountRules(Collection<ShopDiscount> shopDiscountRules) {
        this.shopDiscountRules = shopDiscountRules;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "shop")
    public Collection<AttrValueShop> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Collection<AttrValueShop> attributes) {
        this.attributes = attributes;
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "SHOP_ID", updatable = false)
    public Collection<ShopCategory> getShopCategory() {
        return this.shopCategory;
    }

    public void setShopCategory(Collection<ShopCategory> shopCategory) {
        this.shopCategory = shopCategory;
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


    private long shopId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})
    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "SHOP_ID", nullable = false)
    public long getShopId() {
        return this.shopId;
    }

    @Transient
    public long getId() {
        return this.shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    @Transient
    public String getSupportedCurrencies() {
        final AttrValueShop attrValueShop = getAttributeByCode(AttributeNamesKeys.SUPPORTED_CURRENCIES);
        if (attrValueShop != null) {
            return attrValueShop.getVal();
        }
        return null;
    }

    @Transient
    public String getDefaultCurrency() {
        String currencies = getSupportedCurrencies();
        if (currencies != null) {
            return currencies.split(",")[0];
        }
        return null;
    }

    @Transient
    public List<String> getSupportedCurrenciesAsList() {
        final String currencies = getSupportedCurrencies();
        if (currencies != null) {
            return Arrays.asList(currencies.split(","));
        }
        return null;
    }


    @Transient
    public Collection<AttrValueShop> getAttributesByCode(final String attributeCode) {
        final Collection<AttrValueShop> result = new ArrayList<AttrValueShop>();
        if (attributeCode != null && this.attributes != null) {
            for (AttrValueShop attrValue : this.attributes) {
                if (attrValue.getAttribute().getCode().equals(attributeCode)) {
                    result.add(attrValue);
                }
            }
        }
        return result;
    }

    @Transient
    public AttrValueShop getAttributeByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        if (this.attributes != null) {
            for (AttrValueShop attrValue : this.attributes) {
                if (attrValue.getAttribute() != null && attributeCode.equals(attrValue.getAttribute().getCode())) {
                    return attrValue;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + this.getShopId();
    }

    @Transient
    public boolean isB2ProfileActive() {
        final AttrValueShop avs = getAttributeByCode(
                AttributeNamesKeys.SHOP_B2B);
        if (avs != null) {
            return Boolean.valueOf(avs.getVal());
        }
        return false;
    }

    private String markupFolder = null;

    @Transient
    public String getMarkupFolder() {
        if (markupFolder == null) {
            synchronized (this) {
                markupFolder = "/" + fspointer + "/markup";
            }
        }
        return markupFolder;
    }

    private String mailFolder = null;

    @Transient
    public String getMailFolder() {
        if (mailFolder == null) {
            synchronized (this) {
                mailFolder = "/" + fspointer + "/mail";
            }
        }
        return mailFolder;
    }

    @Transient
    public String getDefaultShopUrl() {
        for (ShopUrl shopUrl : getShopUrl()) {
            if (shopUrl.getUrl().contains("localhost") || shopUrl.getUrl().contains("127.0.0.1")) {
                continue;
            }
            return "http://" + shopUrl.getUrl();
        }
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public void setSeo(final Seo seo) {
        this.seo = (SeoEntity) seo;
    }


    // end of extra code specified in the hbm.xml files

}


