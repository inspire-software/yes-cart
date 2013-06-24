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

package org.yes.cart.domain.entity;


/**
 * Associations between products. It can be:
 * Cross sell - other related products from other categories.
 * up sell - the simular products with higher prices.
 * accesories - accessories to product, like charger for mobile phone.
 * who buy also buy - dynamic association, base on different customers buy history.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Association extends Auditable, Codable {


    String CROSS_SELL = "cross";
    String ACCESSORIES = "accessories";
    String UP_SELL = "up";
    String BUY_WITH_THIS = "buywiththis";
    String EXPENDABLE = "expendable";

    /**
     * @return primary key.
     */
    long getAssociationId();

    /**
     * @param associationId primary key to set
     */
    void setAssociationId(long associationId);

    /**
     * Identifiable human readable association code.
     *
     * @return unique human readable association code
     */
    String getCode();

    /**
     * @param code unique human readable association code to use.
     */
    void setCode(String code);

    /**
     * @return Association name.
     */
    String getName();

    /**
     * @param name association name.
     */
    void setName(String name);

    /**
     * @return description
     */
    String getDescription();

    /**
     * @param description description to use.
     */
    void setDescription(String description);


}


