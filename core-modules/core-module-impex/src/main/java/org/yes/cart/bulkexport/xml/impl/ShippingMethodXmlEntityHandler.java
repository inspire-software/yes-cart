/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.bulkexport.xml.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.utils.DateUtils;

import java.io.OutputStreamWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class ShippingMethodXmlEntityHandler extends AbstractXmlEntityHandler<CarrierSla> {

    public ShippingMethodXmlEntityHandler() {
        super("shipping-methods");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, CarrierSla> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer) throws Exception {

        handleInternal(tagShippingMethod(null, tuple.getData()), writer, statusListener);

    }

    Tag tagShippingMethod(final Tag parent, final CarrierSla shippingMethod) {

        final Tag tag = tag(parent, "shipping-method")
                .attr("id", shippingMethod.getCarrierslaId())
                .attr("guid", shippingMethod.getGuid())
                .attr("provider", shippingMethod.getCarrier().getGuid())
                .attr("external-ref", shippingMethod.getExternalRef())
                    .tagCdata("name", shippingMethod.getName())
                    .tagI18n("display-name", shippingMethod.getDisplayName())
                    .tagCdata("description", shippingMethod.getDescription())
                    .tagI18n("display-description", shippingMethod.getDisplayDescription())
                    .tag("configuration")
                        .attr("type", shippingMethod.getSlaType())
                        .attr("min-days", shippingMethod.getMinDays())
                        .attr("max-days", shippingMethod.getMaxDays())
                        .attr("guaranteed-delivery", shippingMethod.isGuaranteed())
                        .attr("named-day-delivery", shippingMethod.isNamedDay())
                        .attr("billing-address-not-required", shippingMethod.isBillingAddressNotRequired())
                        .attr("delivery-address-not-required", shippingMethod.isDeliveryAddressNotRequired())
                            .tagCdata("script", shippingMethod.getScript())
                    .end();

        final Tag exclusions = tag.tag("exclusions");

        if (CollectionUtils.isNotEmpty(shippingMethod.getExcludeCustomerTypesAsList())) {

                    final Tag customerTypes = exclusions.tag("customer-types");
                    for (final String type : shippingMethod.getExcludeCustomerTypesAsList()) {
                        customerTypes.tagChars("type", type);
                    }
                    customerTypes.end();

        }

        final List<Integer> skipDays = shippingMethod.getExcludeWeekDaysAsList();
        if (CollectionUtils.isNotEmpty(skipDays)) {

                    final Tag weekDays = exclusions.tag("weekdays");
                    for (final DayOfWeek dayOfWeek : new ArrayList<>(DateUtils.fromCalendarDaysOfWeekToISO(skipDays))) {
                        weekDays.tagChars("weekday", dayOfWeek.name());
                    }
                    weekDays.end();

        }

        final Map<LocalDate, LocalDate> skipDates = shippingMethod.getExcludeDatesAsMap();
        if (MapUtils.isNotEmpty(skipDates)) {

                    final Tag dates = exclusions.tag("dates");
                    for (final Map.Entry<LocalDate, LocalDate> date : skipDates.entrySet()) {
                        dates.tag("date")
                                .tagTime("from", date.getKey())
                                .tagTime("to", date.getValue())
                            .end();
                    }
                    dates.end();

        }

                exclusions.end();

        final Tag supported = tag.tag("supported");

        if (CollectionUtils.isNotEmpty(shippingMethod.getSupportedFulfilmentCentresAsList())) {

                    final Tag fcs = supported.tag("fulfilment-centres");
                    for (final String fc : shippingMethod.getSupportedFulfilmentCentresAsList()) {
                        fcs.tagChars("fulfilment-centre", fc);
                    }
                    fcs.end();

        }

        if (CollectionUtils.isNotEmpty(shippingMethod.getSupportedPaymentGatewaysAsList())) {

                    final Tag pgs = supported.tag("payment-gateways");
                    for (final String pg : shippingMethod.getSupportedPaymentGatewaysAsList()) {
                        pgs.tagChars("payment-gateway", pg);
                    }
                    pgs.end();

        }

                supported.end();

        return tag
                    .tagTime(shippingMethod)
                .end();

    }
}
