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

package org.yes.cart.domain.entity.impl;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.util.DateUtils;
import org.yes.cart.util.log.Markers;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class CarrierSlaEntity implements org.yes.cart.domain.entity.CarrierSla, java.io.Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(CarrierSlaEntity.class);

    private long carrierslaId;
    private long version;

    private String name;
    private String displayName;
    private String description;
    private String displayDescription;
    private Integer maxDays;
    private Integer minDays;
    private String excludeWeekDays;
    private List<Integer> excludeWeekDaysList;
    private String excludeDates;
    private boolean guaranteed;
    private boolean namedDay;
    private String slaType;
    private String script;
    private String excludeCustomerTypes;
    private List<String> excludeCustomerTypesList;
    private String supportedPaymentGateways;
    private List<String> supportedPaymentGatewaysAsList;
    private String supportedFulfilmentCentres;
    private List<String> supportedFulfilmentCentresAsList;
    private boolean billingAddressNotRequired;
    private boolean deliveryAddressNotRequired;
    private String externalRef;
    private Carrier carrier;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public CarrierSlaEntity() {
    }




    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDisplayDescription() {
        return displayDescription;
    }

    @Override
    public void setDisplayDescription(final String displayDescription) {
        this.displayDescription = displayDescription;
    }

    @Override
    public Integer getMaxDays() {
        return this.maxDays;
    }

    @Override
    public void setMaxDays(Integer maxDays) {
        this.maxDays = maxDays;
    }

    @Override
    public Integer getMinDays() {
        return minDays;
    }

    @Override
    public void setMinDays(final Integer minDays) {
        this.minDays = minDays;
    }

    @Override
    public String getExcludeWeekDays() {
        return excludeWeekDays;
    }

    @Override
    public void setExcludeWeekDays(final String excludeWeekDays) {
        this.excludeWeekDays = excludeWeekDays;
        this.excludeWeekDaysList = null;
    }

    @Override
    public List<Integer> getExcludeWeekDaysAsList() {
        if (excludeWeekDaysList == null) {
            if (excludeWeekDays != null) {
                final List<Integer> days = new ArrayList<>(7);
                for (final String day : Arrays.asList(StringUtils.split(excludeWeekDays, ','))) {
                    days.add(NumberUtils.toInt(day));
                }
                excludeWeekDaysList = Collections.unmodifiableList(days);
            } else {
                excludeWeekDaysList = Collections.emptyList();
            }

        }
        return excludeWeekDaysList;

    }

    @Override
    public String getExcludeDates() {
        return excludeDates;
    }

    @Override
    public void setExcludeDates(final String excludeDates) {
        this.excludeDates = excludeDates;
    }

    @Override
    public Map<LocalDate, LocalDate> getExcludeDatesAsMap() {

        final Map<LocalDate, LocalDate> dates = new HashMap<>();
        if (StringUtils.isNotBlank(getExcludeDates())) {

            final String[] all = StringUtils.split(getExcludeDates(), ',');
            for (final String range : all) {
                final int rangePos = range.indexOf(':');
                if (rangePos == -1) {
                    final LocalDate date = DateUtils.ldParseSDT(range);
                    if (date != null) {
                        dates.put(date, date);
                    } else {
                        LOG.error(Markers.alert(),
                                "Error reading excluded date during delivery time estimation: {}, sla: {}/{}",
                                range, getGuid(), getName());
                    }
                } else {
                    final LocalDate from = DateUtils.ldParseSDT(range.substring(0, rangePos));
                    final LocalDate to = DateUtils.ldParseSDT(range.substring(rangePos + 1));
                    if (from != null && to != null) {
                        dates.put(from, to);
                    } else {
                        LOG.error(Markers.alert(),
                                "Error reading excluded date range during delivery time estimation: {}, sla: {}/{}",
                                range, getGuid(), getName());
                    }
                }
            }

        }
        return dates;

    }

    @Override
    public boolean isGuaranteed() {
        return guaranteed;
    }

    @Override
    public void setGuaranteed(final boolean guaranteed) {
        this.guaranteed = guaranteed;
    }

    @Override
    public boolean isNamedDay() {
        return namedDay;
    }

    @Override
    public void setNamedDay(final boolean namedDay) {
        this.namedDay = namedDay;
    }

    @Override
    public String getSlaType() {
        return this.slaType;
    }

    @Override
    public void setSlaType(String slaType) {
        this.slaType = slaType;
    }

    @Override
    public String getScript() {
        return this.script;
    }

    @Override
    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public String getExcludeCustomerTypes() {
        return excludeCustomerTypes;
    }

    @Override
    public void setExcludeCustomerTypes(final String excludeCustomerTypes) {
        this.excludeCustomerTypes = excludeCustomerTypes;
        this.excludeCustomerTypesList = null;
    }

    @Override
    public List<String> getExcludeCustomerTypesAsList() {
        if (excludeCustomerTypesList == null) {
            if (excludeCustomerTypes != null) {
                excludeCustomerTypesList = Arrays.asList(StringUtils.split(excludeCustomerTypes, ','));
            } else {
                excludeCustomerTypesList = Collections.emptyList();
            }

        }
        return excludeCustomerTypesList;
    }

    @Override
    public String getSupportedPaymentGateways() {
        return supportedPaymentGateways;
    }

    @Override
    public void setSupportedPaymentGateways(final String supportedPaymentGateways) {
        this.supportedPaymentGateways = supportedPaymentGateways;
        this.supportedPaymentGatewaysAsList = null;
    }

    @Override
    public List<String> getSupportedPaymentGatewaysAsList() {
        if (supportedPaymentGatewaysAsList == null) {
            if (supportedPaymentGateways != null) {
                supportedPaymentGatewaysAsList = Arrays.asList(StringUtils.split(supportedPaymentGateways, ','));
            } else {
                supportedPaymentGatewaysAsList = Collections.emptyList();
            }

        }
        return supportedPaymentGatewaysAsList;
    }


    public void setSupportedPaymentGatewaysAsList(final List<String> supportedPaymentGatewaysAsList) {
        this.supportedPaymentGatewaysAsList = supportedPaymentGatewaysAsList;
    }


    @Override
    public String getSupportedFulfilmentCentres() {
        return supportedFulfilmentCentres;
    }

    @Override
    public void setSupportedFulfilmentCentres(final String supportedFulfilmentCentres) {
        this.supportedFulfilmentCentres = supportedFulfilmentCentres;
        this.supportedFulfilmentCentresAsList = null;
    }

    @Override
    public List<String> getSupportedFulfilmentCentresAsList() {
        if (supportedFulfilmentCentresAsList == null) {
            if (supportedFulfilmentCentres != null) {
                supportedFulfilmentCentresAsList = Arrays.asList(StringUtils.split(supportedFulfilmentCentres, ','));
            } else {
                supportedFulfilmentCentresAsList = Collections.emptyList();
            }

        }
        return supportedFulfilmentCentresAsList;
    }

    public void setSupportedFulfilmentCentresAsList(final List<String> supportedFulfilmentCentresAsList) {
        this.supportedFulfilmentCentresAsList = supportedFulfilmentCentresAsList;
    }


    @Override
    public boolean isBillingAddressNotRequired() {
        return billingAddressNotRequired;
    }

    @Override
    public void setBillingAddressNotRequired(final boolean billingAddressNotRequired) {
        this.billingAddressNotRequired = billingAddressNotRequired;
    }

    @Override
    public boolean isDeliveryAddressNotRequired() {
        return deliveryAddressNotRequired;
    }

    @Override
    public void setDeliveryAddressNotRequired(final boolean deliveryAddressNotRequired) {
        this.deliveryAddressNotRequired = deliveryAddressNotRequired;
    }

    @Override
    public String getExternalRef() {
        return externalRef;
    }

    @Override
    public void setExternalRef(final String externalRef) {
        this.externalRef = externalRef;
    }

    @Override
    public Carrier getCarrier() {
        return this.carrier;
    }

    @Override
    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public long getCarrierslaId() {
        return this.carrierslaId;
    }


    @Override
    public long getId() {
        return this.carrierslaId;
    }

    @Override
    public void setCarrierslaId(long carrierslaId) {
        this.carrierslaId = carrierslaId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


