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
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import com.inspiresoftware.lib.dto.geda.annotations.DtoVirtualField;
import org.yes.cart.domain.ro.xml.impl.QuantityMapAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedSet;

/**
 * User: denispavlov
 * Date: 23/03/2015
 * Time: 12:36
 */
@Dto
@XmlRootElement(name = "product-availability")
public class ProductAvailabilityModelRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(readOnly = true)
    private boolean available;
    @DtoField(readOnly = true)
    private boolean inStock;
    @DtoField(readOnly = true)
    private boolean perpetual;
    @DtoField(readOnly = true)
    private int availability;
    @DtoField(readOnly = true)
    private String defaultSkuCode;
    @DtoField(readOnly = true)
    private String firstAvailableSkuCode;
    @DtoField(readOnly = true)
    private SortedSet<String> skuCodes;
    @DtoVirtualField(readOnly = true, converter = "productAvailabilityModelATSConverter")
    private Map<String, BigDecimal> availableToSellQuantity;

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(final boolean available) {
        this.available = available;
    }

    @XmlElement(name = "in-stock")
    public boolean getInStock() {
        return inStock;
    }

    public void setInStock(final boolean inStock) {
        this.inStock = inStock;
    }

    public boolean getPerpetual() {
        return perpetual;
    }

    public void setPerpetual(final boolean perpetual) {
        this.perpetual = perpetual;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(final int availability) {
        this.availability = availability;
    }

    @XmlElement(name = "default-sku")
    public String getDefaultSkuCode() {
        return defaultSkuCode;
    }

    public void setDefaultSkuCode(final String defaultSkuCode) {
        this.defaultSkuCode = defaultSkuCode;
    }

    @XmlElement(name = "first-available-sku")
    public String getFirstAvailableSkuCode() {
        return firstAvailableSkuCode;
    }

    public void setFirstAvailableSkuCode(final String firstAvailableSkuCode) {
        this.firstAvailableSkuCode = firstAvailableSkuCode;
    }

    @XmlElement(name = "sku-codes")
    public SortedSet<String> getSkuCodes() {
        return skuCodes;
    }

    public void setSkuCodes(final SortedSet<String> skuCodes) {
        this.skuCodes = skuCodes;
    }

    @XmlJavaTypeAdapter(QuantityMapAdapter.class)
    @XmlElement(name = "ats-quantity")
    public Map<String, BigDecimal> getAvailableToSellQuantity() {
        return availableToSellQuantity;
    }

    public void setAvailableToSellQuantity(final Map<String, BigDecimal> availableToSellQuantity) {
        this.availableToSellQuantity = availableToSellQuantity;
    }
}
