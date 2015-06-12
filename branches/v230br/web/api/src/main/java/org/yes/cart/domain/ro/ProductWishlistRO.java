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

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 31/03/2015
 * Time: 14:11
 */
@Dto
@XmlRootElement(name = "wishlist-result")
public class ProductWishlistRO extends ProductSearchResultRO {

    private SkuPriceRO priceWhenAdded;
    private BigDecimal quantity;

    public SkuPriceRO getPriceWhenAdded() {
        return priceWhenAdded;
    }

    public void setPriceWhenAdded(final SkuPriceRO priceWhenAdded) {
        this.priceWhenAdded = priceWhenAdded;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }
}
