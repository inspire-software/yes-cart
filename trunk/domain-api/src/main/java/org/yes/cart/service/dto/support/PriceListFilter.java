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

package org.yes.cart.service.dto.support;

import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.ProductDTO;
import org.yes.cart.domain.dto.ShopDTO;

import java.util.Date;

/**
 * Price list filter DTO.
 *
 * User: denispavlov
 * Date: 12-11-29
 * Time: 6:53 PM
 */
public interface PriceListFilter {

    /**
     * @return shop filter.
     */
    ShopDTO getShop();

    /**
     * @return currency filter.
     */
    String getCurrencyCode();

    /**
     * @return category filter.
     */
    CategoryDTO getCategory();

    /**
     * @return product filter.
     */
    ProductDTO getProduct();

    /**
     * @return from filter.
     */
    Date getFrom();

    /**
     * @return to filter.
     */
    Date getTo();

}
