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

import java.math.BigDecimal;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 * <p/>
 * Represent carrier SLA, that can be quite complex. At this moment
 * several strategies(types) are supported:
 * <p/>
 * 1. E - External
 * 2. R - Free
 * <p/>
 * 3. F - Fixed
 * 4. O - Per Order
 * 5. P - Per package
 * <p/>
 * Each FOP stategy has destination flag. It mean that price and percent will be selected as
 * single values in case of F - fixed strategy
 * or
 * null delivery cost in case of free shipping
 * or
 * maximum from destination-weight / destination-volume matrix for Per Order or Per Package.
 * <p/>
 * <p/>
 * This this not represent the actual shipping rates for delivery but price for customer.
 * <p/>
 * Total devilery cost cab be limited as well as price and percent
 * <p/>
 * When price ant percent are obtained cost calculation
 * will be following:
 * <p/>
 * (F)ixed
 * delivery cost = 1 * price + max(order_sub_total * percent, percent_not_less)
 * (O) Per Order
 * delivery cost = max( 1 * price                                 + max(order_sub_total * percent, percent_not_less), cost_not_less)
 * (P) Per package
 * delivery cost = max( max(packages qty * price, price_not_less) + max(     item_price * percent, percent_not_less), cost_not_less)
 */
public interface CarrierSla extends Auditable {

    /**
     * External procedure delivery cost calculation
     */
    String EXTERNAL = "E";

    /**
     * Free delivery cost calculation
     */
    String FREE = "R";

    /**
     * Fixed price, percent
     */
    String FIXED = "F";

    /**
     * Per order price, percent
     */
    String PER_ORDER = "O";

    /**
     * Per package price, percent
     */
    String PER_PACKAGE = "P";

    /**
     * Get pk value.
     *
     * @return pk value
     */
    long getCarrierslaId();

    /**
     * Set pk value.
     *
     * @param carrierslaId pk value
     */
    void setCarrierslaId(long carrierslaId);


    /**
     * Get SLA type.
     *
     * @return SLA type.
     */
    String getSlaType();

    /**
     * Set SLA type.
     *
     * @param slaType SLA type.
     */
    void setSlaType(String slaType);


    /**
     * Get SLA name.
     *
     * @return SLA name
     */
    String getName();

    /**
     * Set SLA name.
     *
     * @param name SLA name
     */
    void setName(String name);

    /**
     * Get name.
     *
     * @return localisable name of carrier SLA.
     */
    String getDisplayName();

    /**
     * Set name of carrier SLA.
     *
     * @param name localisable name.
     */
    void setDisplayName(String name);

    /**
     * Get SLA description.
     *
     * @return SLA description.
     */
    String getDescription();

    /**
     * Set description.
     *
     * @param description description
     */
    void setDescription(String description);

    /**
     * Get name.
     *
     * @return localisable description of carrier SLA.
     */
    String getDisplayDescription();

    /**
     * Set description of carrier SLA.
     *
     * @param description localisable description.
     */
    void setDisplayDescription(String description);


    /**
     * Get price. This is for internal usage only. Not a part of api.
     * Price of delivery must be obtained from service.
     *
     * @return price.
     */
    BigDecimal getPrice();

    /**
     * Set price. This is for internal usage only.
     *
     * @param price price.
     */
    void setPrice(BigDecimal price);

    /**
     * Get percent.
     *
     * @return percent.
     */
    BigDecimal getPercent();

    /**
     * Set percent.
     *
     * @param percent percent.
     */
    void setPercent(BigDecimal percent);


    /**
     * Get minimal cost for delivery.
     *
     * @return minimal cost for delivery.
     */
    BigDecimal getPriceNotLess();

    /**
     * Set minimal cost for delivery.
     *
     * @param priceNotLess minimal cost for delivery.
     */
    void setPriceNotLess(BigDecimal priceNotLess);

    /**
     * Get minimal amount  for prrcent.
     *
     * @return minimal amount  for percent.
     */
    BigDecimal getPercentNotLess();

    /**
     * Set minimal amount  for percent.
     *
     * @param percentNotLess minimal amount  for percent.
     */
    void setPercentNotLess(BigDecimal percentNotLess);

    /**
     * Get minimal delivery cost.
     *
     * @return minimal delivery cost.
     */
    BigDecimal getCostNotLess();

    /**
     * Set minimal delivery cost.
     *
     * @param costNotLess minimal delivery cost.
     */
    void setCostNotLess(BigDecimal costNotLess);

    /**
     * Get external script to calculate delivery cost.
     *
     * @return external script.
     */
    String getScript();

    /**
     * Set external script to calculate delivery cost.
     *
     * @param script script.
     */

    void setScript(String script);


    /**
     * Get currency.
     *
     * @return currency.
     */
    String getCurrency();

    /**
     * Set currency.
     *
     * @param currency currency.
     */
    void setCurrency(String currency);


    /**
     * Get max days delivery.
     *
     * @return max days delivery.
     */
    Integer getMaxDays();

    /**
     * Set max days delivery.
     *
     * @param maxDays max days delivery.
     */
    void setMaxDays(Integer maxDays);


    /**
     * Get carrier.
     *
     * @return {@link Carrier}
     */
    Carrier getCarrier();

    /**
     * Set carrier.
     *
     * @param carrier carrier.
     */
    void setCarrier(Carrier carrier);

}


