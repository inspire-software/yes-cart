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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: denispavlov
 * Date: 23/02/2020
 * Time: 12:30
 */
public interface ProductOptionDTO extends Identifiable {


    /**
     * PK
     */
    long getProductoptionId();

    void setProductoptionId(long productoptionId);

    /**
     * Mandatory flag.
     *
     * @return true if value must be set for this attribute.
     */
    boolean isMandatory();

    /**
     * Set mandatory flag.
     *
     * @param mandatory flag value
     */
    void setMandatory(boolean mandatory);

    /**
     * Proportional quantity of this option (default 1:1)
     *
     * @return quantity for 1 items of product
     */
    BigDecimal getQuantity();

    /**
     * Proportional quantity.
     *
     * @param quantity ratio
     */
    void setQuantity(BigDecimal quantity);

    /**
     * SKU of the product to which this option is limited to (if null then applies to all SKU)
     *
     * @return SKU
     */
    String getSkuCode();

    /**
     * SKU to which this option is restricted.
     *
     * @param skuCode SKU
     */
    void setSkuCode(String skuCode);

    /**
     * Option rank
     */
    int getRank();

    /**
     * Option rank.
     *
     * @param rank sorting rank
     */
    void setRank(int rank);


    /**
     * Get the main(source) product id.
     *
     * @return main product id.
     */
    long getProductId();

    /**
     * Set main product id.
     *
     * @param productId main product id.
     */
    void setProductId(long productId);

    /**
     * Get the attribute.
     *
     * @return attribute code
     */
    String getAttributeCode();

    /**
     * Set attribute.
     *
     * @param attributeCode attribute.
     */
    void setAttributeCode(String attributeCode);

    /**
     * SKUs that represent options
     *
     * @return list of SKU
     */
    List<Pair<String, String>> getOptionSkuCodes();

    /**
     * SKU that represents options.
     *
     * @param skus list of SKU
     */
    void setOptionSkuCodes(List<Pair<String, String>> skus);



}
