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

import org.yes.cart.domain.entity.Identifiable;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Carrier's SLA DTO interface.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CarrierSlaDTO extends Identifiable {


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
     * Carrier SLA name.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayNames();

    /**
     * Get carrier SLA name
     *
     * @param names localised locale => name pairs
     */
    void setDisplayNames(Map<String, String> names);


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
     * Carrier SLA description.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayDescriptions();

    /**
     * Get carrier SLA description
     *
     * @param names localised locale => name pairs
     */
    void setDisplayDescriptions(Map<String, String> names);


    /**
     * Get price.
     *
     * @return price.
     */
    BigDecimal getPrice();

    /**
     * Set price.
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
     * Get minimal amount  for precent.
     *
     * @return minimal amount  for precent.
     */
    BigDecimal getPercentNotLess();

    /**
     * Set minimal amount  for precent.
     *
     * @param percentNotLess minimal amount  for precent.
     */
    void setPercentNotLess(BigDecimal percentNotLess);

    /**
     * Get minimal delivery cost.
     *
     * @return minimal delivery cost.     *
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
     * Get max deys delivery.
     *
     * @return max deys delivery.
     */
    Integer getMaxDays();

    /**
     * Set max deys delivery.
     *
     * @param maxDays max deys delivery.
     */
    void setMaxDays(Integer maxDays);



    /**
     * Is billing address not required for this order.
     *
     * @return true is  address not required for this order.
     */
    boolean isBillingAddressNotRequired();

    /**
     * Set billing address not required for this order flag.
     *
     * @param billingAddressNotRequired flag.
     */
    void setBillingAddressNotRequired(boolean billingAddressNotRequired);

    /**
     * Is delivery address not required for this order.
     *
     * @return true is  address not required for this order.
     */
    boolean isDeliveryAddressNotRequired();

    /**
     * Set delivery address not required for this order flag.
     *
     * @param deliveryAddressNotRequired flag.
     */
    void setDeliveryAddressNotRequired(boolean deliveryAddressNotRequired);


    /**
     * Get supported payment gateways CSV
     *
     * @return CSV PG labels
     */
    String getSupportedPaymentGateways();

    /**
     * Set supported payment gateways CSV
     *
     * @param supportedPaymentGateways CSV PG labels
     */
    void setSupportedPaymentGateways(String supportedPaymentGateways);

    /**
     * Get carrier.
     *
     * @return pk value of carrier.
     */
    long getCarrierId();

    /**
     * Set carrier.
     *
     * @param carrierId carrier
     */
    void setCarrierId(long carrierId);


}
