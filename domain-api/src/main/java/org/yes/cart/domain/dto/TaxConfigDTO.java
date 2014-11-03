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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Auditable;
import org.yes.cart.domain.entity.Identifiable;

/**
 * User: denispavlov
 * Date: 27/10/2014
 * Time: 12:36
 */
public interface TaxConfigDTO extends Identifiable {

    /**
     * Get tax for this config.
     *
     * @return tax
     */
    long getTaxId();

    /**
     * Set tax for this config
     *
     * @param taxId tax
     */
    void setTaxId(long taxId);

    /**
     * Get specific product code (or null if non product specific).
     *
     * @return product code or null
     */
    String getProductCode();

    /**
     * Set specific product code (or null if non product specific).
     *
     * @param productCode product code or null
     */
    void setProductCode(String productCode);

    /**
     * Get specific state code (or null if non state specific).
     *
     * @return state code or null
     */
    String getStateCode();

    /**
     * Set specific state code (or null if non state specific).
     *
     * @param stateCode state code or null
     */
    void setStateCode(String stateCode);

    /**
     * Get specific country code (or null if non country specific).
     *
     * @return country code or null
     */
    String getCountryCode();

    /**
     * Set specific country code (or null if non state specific).
     *
     * @param countryCode state code or null
     */
    void setCountryCode(String countryCode);

    /**
     * Get tax config PK.
     *
     * @return tax config PK
     */
    long getTaxConfigId();

    /**
     * Set tax config PK.
     *
     * @param taxConfigId tax config PK
     */
    void setTaxConfigId(long taxConfigId);
}
