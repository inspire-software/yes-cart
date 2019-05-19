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

package org.yes.cart.bulkimport.xml.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.ShippingMethodExclusionsDatesDateType;
import org.yes.cart.bulkimport.xml.internal.ShippingMethodType;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.CarrierService;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.utils.DateUtils;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ShippingMethodXmlEntityHandler extends AbstractXmlEntityHandler<ShippingMethodType, CarrierSla> implements XmlEntityImportHandler<ShippingMethodType, CarrierSla> {

    private CarrierSlaService carrierSlaService;
    private CarrierService carrierService;

    public ShippingMethodXmlEntityHandler() {
        super("shipping-method");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final CarrierSla method) {
        this.carrierSlaService.delete(method);
        this.carrierSlaService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final CarrierSla domain, final ShippingMethodType xmlType, final EntityImportModeType mode) {

        if (xmlType.getConfiguration() != null) {
            domain.setGuaranteed(xmlType.getConfiguration().isGuaranteedDelivery());
            domain.setNamedDay(xmlType.getConfiguration().isNamedDayDelivery());
            domain.setMinDays(xmlType.getConfiguration().getMinDays());
            domain.setMaxDays(xmlType.getConfiguration().getMaxDays());
            domain.setSlaType(xmlType.getConfiguration().getType());
            domain.setScript(xmlType.getConfiguration().getScript());
            domain.setBillingAddressNotRequired(xmlType.getConfiguration().isBillingAddressNotRequired());
            domain.setDeliveryAddressNotRequired(xmlType.getConfiguration().isDeliveryAddressNotRequired());
        }

        if (xmlType.getExclusions() != null) {

            if (xmlType.getExclusions().getCustomerTypes() != null) {

                final List<String> customerTypes = xmlType.getExclusions().getCustomerTypes().getType();
                if (CollectionUtils.isNotEmpty(customerTypes)) {
                    domain.setExcludeCustomerTypes(StringUtils.join(customerTypes, ','));
                } else {
                    domain.setExcludeCustomerTypes(null);
                }

            }

            if (xmlType.getExclusions().getWeekdays() != null) {

                final List<String> weekdays = xmlType.getExclusions().getWeekdays().getWeekday();
                final List<String> indexes = new ArrayList<>();
                for (final String weekday : weekdays) {
                    final DayOfWeek dow = DayOfWeek.valueOf(weekday);
                    indexes.add(String.valueOf(DateUtils.fromISOtoCalendarDayOfWeek(dow)));
                }
                if (CollectionUtils.isNotEmpty(indexes)) {
                    domain.setExcludeWeekDays(StringUtils.join(indexes, ','));
                } else {
                    domain.setExcludeWeekDays(null);
                }

            }

            if (xmlType.getExclusions().getDates() != null) {

                final List<String> dates = new ArrayList<>();
                for (final ShippingMethodExclusionsDatesDateType date : xmlType.getExclusions().getDates().getDate()) {

                    if (StringUtils.isNotBlank(date.getFrom())) {
                        if (StringUtils.isNotBlank(date.getTo())) {
                            dates.add(date.getFrom() + ":" + date.getTo());
                        } else {
                            dates.add(date.getFrom());
                        }
                    } else if (StringUtils.isNotBlank(date.getTo())) {
                        dates.add(date.getTo());
                    }

                }

                if (CollectionUtils.isNotEmpty(dates)) {
                    domain.setExcludeDates(StringUtils.join(dates, ','));
                } else {
                    domain.setExcludeDates(null);
                }

            }

        }

        if (xmlType.getSupported() != null) {

            if (xmlType.getSupported().getFulfilmentCentres() != null) {

                final List<String> centres = xmlType.getSupported().getFulfilmentCentres().getFulfilmentCentre();
                if (CollectionUtils.isNotEmpty(centres)) {
                    domain.setSupportedFulfilmentCentres(StringUtils.join(centres, ','));
                } else {
                    domain.setSupportedFulfilmentCentres(null);
                }

            }

            if (xmlType.getSupported().getPaymentGateways() != null) {

                final List<String> pgs = xmlType.getSupported().getPaymentGateways().getPaymentGateway();
                if (CollectionUtils.isNotEmpty(pgs)) {
                    domain.setSupportedPaymentGateways(StringUtils.join(pgs, ','));
                } else {
                    domain.setSupportedPaymentGateways(null);
                }

            }

        }

        domain.setExternalRef(xmlType.getExternalRef());
        domain.setName(xmlType.getName());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        domain.setDescription(xmlType.getDescription());
        domain.setDisplayDescription(processI18n(xmlType.getDisplayDescription(), domain.getDisplayDescription()));
        if (domain.getCarrierslaId() == 0L) {
            this.carrierSlaService.create(domain);
        } else {
            this.carrierSlaService.update(domain);
        }
        this.carrierSlaService.getGenericDao().flush();
        this.carrierSlaService.getGenericDao().evict(domain);
    }

    @Override
    protected CarrierSla getOrCreate(final JobStatusListener statusListener, final ShippingMethodType xmlType) {
        CarrierSla sla = this.carrierSlaService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (sla != null) {
            return sla;
        }
        sla = this.carrierSlaService.getGenericDao().getEntityFactory().getByIface(CarrierSla.class);
        sla.setGuid(xmlType.getGuid());
        sla.setCarrier(this.carrierService.findSingleByCriteria(" where e.guid = ?1", xmlType.getProvider()));
        return sla;
    }

    @Override
    protected EntityImportModeType determineImportMode(final ShippingMethodType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final CarrierSla domain) {
        return domain.getCarrierslaId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param carrierSlaService SLA service
     */
    public void setCarrierSlaService(final CarrierSlaService carrierSlaService) {
        this.carrierSlaService = carrierSlaService;
    }

    /**
     * Spring IoC.
     *
     * @param carrierService carrier service
     */
    public void setCarrierService(final CarrierService carrierService) {
        this.carrierService = carrierService;
    }
}
