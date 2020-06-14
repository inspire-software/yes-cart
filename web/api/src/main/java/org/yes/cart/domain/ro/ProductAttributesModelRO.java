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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.domain.entity.ProductAttributesModelGroup;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 24/05/2020
 * Time: 08:51
 */
@Dto
@XmlRootElement(name = "product-attributes")
public class ProductAttributesModelRO implements Serializable {

    private static final long serialVersionUID = 20200524L;

    @DtoField(readOnly = true)
    private long productId;

    @DtoField(readOnly = true)
    private String productCode;

    @DtoField(readOnly = true)
    private long skuId;

    @DtoField(readOnly = true)
    private String skuCode;

    @DtoField(readOnly = true)
    private long productType;

    @DtoCollection(
            value = "groups",
            dtoBeanKey = "org.yes.cart.domain.ro.ProductAttributesModelGroupRO",
            entityGenericType = ProductAttributesModelGroup.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<ProductAttributesModelGroupRO> groups;

    @XmlAttribute(name = "product-id")
    public long getProductId() {
        return productId;
    }

    public void setProductId(final long productId) {
        this.productId = productId;
    }

    @XmlAttribute(name = "product-code")
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }

    @XmlAttribute(name = "sku-id")
    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(final long skuId) {
        this.skuId = skuId;
    }

    @XmlAttribute(name = "sku-code")
    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    @XmlAttribute(name = "product-type")
    public long getProductType() {
        return productType;
    }

    public void setProductType(final long productType) {
        this.productType = productType;
    }

    @XmlElementWrapper(name = "groups")
    @XmlElement(name = "group")
    public List<ProductAttributesModelGroupRO> getGroups() {
        return groups;
    }

    public void setGroups(final List<ProductAttributesModelGroupRO> groups) {
        this.groups = groups;
    }
}
