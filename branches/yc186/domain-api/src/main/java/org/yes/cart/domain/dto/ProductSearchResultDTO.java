/*
 * Copyright 2013 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.domain.dto;

import java.math.BigDecimal;

/**
 * Represent product from search result. The whole entity usage id overhead.
 */
public interface ProductSearchResultDTO {


    long getId();

    void setId(long id);

    String getCode();

    void setCode(String code);

    String getName(final String locale);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    int getAvailability();

    void setAvailability(int availability);

    BigDecimal getQtyOnWarehouse();

    void setQtyOnWarehouse(BigDecimal qty);

    String getFirstAvailableSkuCode();

    void setFirstAvailableSkuCode(String firstAvailableSkuCode);

    BigDecimal getFirstAvailableSkuQuantity();

    void setFirstAvailableSkuQuantity(BigDecimal firstAvailableSkuQuantity);

    String getDefaultImage();

    void setDefaultImage(String defaultImage);


}
