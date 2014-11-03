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

import org.yes.cart.domain.entity.Codable;
import org.yes.cart.domain.entity.Identifiable;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 27/10/2014
 * Time: 12:23
 */
public interface TaxDTO extends Codable, Identifiable {

    /**
     * Tax rate as percentage in range 0-100. E.g. taxRate = 6.00, means
     * 6% tax rate.
     *
     * @return tax rate
     */
    BigDecimal getTaxRate();

    /**
     * Set tax rate.
     *
     * @param taxRate tax rate
     */
    void setTaxRate(BigDecimal taxRate);

    /**
     * Exclusive taxes are those that are added onto base price to
     * amount to the actual price customer has to pay. Inclusive are those
     * included in price.
     *
     * E.g.
     * base price $10.00 with exclusive tax 20% gives net price $10.00, gross
     * price $12.00 and tax $2.00
     *
     * price £10.00 with inclusive tax 20% gives newt price £8.33, gross
     * price £10.00 and tax £1.66.
     *
     * @return true means tax is exclusive, false means tax is inclusive
     */
    boolean getExclusiveOfPrice();

    /**
     * Set tax exlusive flag.
     *
     * @param exclusiveOfPrice exclusive flag
     */
    void setExclusiveOfPrice(boolean exclusiveOfPrice);

    /**
     * Get shop for this tax.
     *
     * @return shop for tax
     */
    String getShopCode();

    /**
     * Set shop for this tax.
     *
     * @param shopCode shop for tax
     */
    void setShopCode(String shopCode);

    /**
     * @return currency
     */
    String getCurrency();

    /**
     * @param currency currency
     */
    void setCurrency(String currency);

    /**
     * @return default description for this promo
     */
    String getDescription();

    /**
     * @param description default description for this promo
     */
    void setDescription(String description);


    /**
     * Get tax PK.
     *
     * @return tax PK
     */
    long getTaxId();

    /**
     * Set tax PK.
     *
     * @param taxId tax PK
     */
    void setTaxId(long taxId);

}
