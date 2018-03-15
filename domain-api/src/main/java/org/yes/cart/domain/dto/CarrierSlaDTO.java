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

import org.yes.cart.domain.entity.Codable;
import org.yes.cart.domain.entity.Identifiable;

import java.util.Map;

/**
 * Carrier's SLA DTO interface.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CarrierSlaDTO extends Identifiable, Codable {


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
     * Get min days delivery.
     *
     * @return min days delivery.
     */
    Integer getMinDays();

    /**
     * Set min days delivery.
     *
     * @param minDays min days delivery.
     */
    void setMinDays(Integer minDays);

    /**
     * Exclude weekdays. CSV of {@link java.util.Calendar#DAY_OF_WEEK}
     *
     * @return csv of days of week when delivery is NOT available
     */
    String getExcludeWeekDays();

    /**
     * Exclude weekdays. CSV of {@link java.util.Calendar#DAY_OF_WEEK}
     *
     * @param excludeWeekDays csv of days of week when delivery is NOT available
     */
    void setExcludeWeekDays(String excludeWeekDays);

    /**
     * CSV od Dates and Date Ranges when delivery is not available.
     *
     * Format of date: yyyy-MM-dd
     * Format of range: yyyy-MM-dd:yyyy-MM-dd
     *
     * @return csv of dates and ranges
     */
    String getExcludeDates();

    /**
     * CSV od Dates and Date Ranges when delivery is not available.
     *
     * Format of date: yyyy-MM-dd
     * Format of range: yyyy-MM-dd:yyyy-MM-dd
     *
     * @param excludeDates  csv of dates and ranges
     */
    void setExcludeDates(String excludeDates);

    /**
     * Flag to denote that this is a guaranteed delivery for {@link #getMinDays()}. (e.g. next day delivery)
     *
     * @return guaranteed delivery flag
     */
    boolean isGuaranteed();

    /**
     * Flag to denote that this is a guaranteed delivery for {@link #getMinDays()}. (e.g. next day delivery)
     *
     * @param guaranteed guaranteed delivery flag
     */
    void setGuaranteed(boolean guaranteed);

    /**
     * Flag to set named date delivery (customer can pick dates).
     *
     * @return true if customer can choose date
     */
    boolean isNamedDay();

    /**
     * Flag to set named date delivery (customer can pick dates).
     *
     * @param namedDay named day
     */
    void setNamedDay(boolean namedDay);

    /**
     * Customer types exclusions (CSV).
     *
     * @return exclude customer types
     */
    String getExcludeCustomerTypes();

    /**
     * Customer types exclusions.
     *
     * @param excludeCustomerTypes exclude customer types CSV
     */
    void setExcludeCustomerTypes(String excludeCustomerTypes);

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
     * Get supported warehouse codes CSV
     *
     * @return CSV warehouse codes
     */
    String getSupportedFulfilmentCentres();

    /**
     * Set supported warehouse codes CSV
     *
     * @param supportedFulfilmentCentres CSV warehouse codes
     */
    void setSupportedFulfilmentCentres(String supportedFulfilmentCentres);


    /**
     * External reference (e.g. for custom export to 3rd party systems)
     *
     * @return external reference
     */
    String getExternalRef();

    /**
     * External reference (e.g. for custom export to 3rd party systems)
     *
     * @param externalRef external reference
     */
    void setExternalRef(String externalRef);


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
