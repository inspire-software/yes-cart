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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 23/03/2015
 * Time: 12:36
 */
@Dto
@XmlRootElement(name = "product-quantity")
public class ProductQuantityModelRO implements Serializable {

    private static final long serialVersionUID = 20190914L;

    @DtoField(readOnly = true)
    private String supplier;
    @DtoField(readOnly = true)
    private BigDecimal min;
    @DtoField(readOnly = true)
    private BigDecimal minOrder;
    @DtoField(readOnly = true)
    private BigDecimal max;
    @DtoField(readOnly = true)
    private BigDecimal maxOrder;
    @DtoField(readOnly = true)
    private BigDecimal step;
    @DtoField(readOnly = true)
    private BigDecimal cartQty;
    @DtoField(readOnly = true)
    private String defaultSkuCode;

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(final String supplier) {
        this.supplier = supplier;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(final BigDecimal min) {
        this.min = min;
    }

    public BigDecimal getMinOrder() {
        return minOrder;
    }

    @XmlElement(name = "min-order")
    public void setMinOrder(final BigDecimal minOrder) {
        this.minOrder = minOrder;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(final BigDecimal max) {
        this.max = max;
    }

    @XmlElement(name = "max-order")
    public BigDecimal getMaxOrder() {
        return maxOrder;
    }

    public void setMaxOrder(final BigDecimal maxOrder) {
        this.maxOrder = maxOrder;
    }

    public BigDecimal getStep() {
        return step;
    }

    public void setStep(final BigDecimal step) {
        this.step = step;
    }

    @XmlElement(name = "cart-quantity")
    public BigDecimal getCartQty() {
        return cartQty;
    }

    public void setCartQty(final BigDecimal cartQty) {
        this.cartQty = cartQty;
    }

    @XmlElement(name = "default-sku-code")
    public String getDefaultSkuCode() {
        return defaultSkuCode;
    }

    public void setDefaultSkuCode(final String defaultSkuCode) {
        this.defaultSkuCode = defaultSkuCode;
    }
    
}
