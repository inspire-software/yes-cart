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

package org.yes.cart.domain.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 * <p/>
 * Represent carrier SLA, that can be quite complex. At this moment
 * several strategies(types) are supported:
 * <p/>
 * 1. E - External (uses script property which should specify Spring bean that implements DeliveryCostStrategy)
 * 2. R - Free (simply puts 0 delivery cost)
 * <p/>
 * 3. F - Fixed (uses price lists where SKU is the CarrierSla.GUID)
 * <p/>
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
     * Weight and/or volume based
     */
    String WEIGHT_VOLUME = "W";

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
     * Get supported payment gateways list.
     *
     * @return supported payment gateways list.
     */
    List<String> getSupportedPaymentGatewaysAsList();


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
     * Get supported fulfilment centres list.
     *
     * @return supported fulfilment centres list.
     */
    List<String> getSupportedFulfilmentCentresAsList();


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


