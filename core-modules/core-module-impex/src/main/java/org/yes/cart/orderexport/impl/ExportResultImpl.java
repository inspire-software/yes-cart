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

package org.yes.cart.orderexport.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.orderexport.OrderExporter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 20/04/2018
 * Time: 19:09
 */
public class ExportResultImpl implements OrderExporter.ExportResult {

    private final Set<Long> exportedDeliveries;
    private final Map<String, String> orderAuditParams;

    private final String nextOrderState;
    private final Map<Long, String> nextDeliveryState;

    /**
     * Basic export result denotes export which is last in the export chain (no next eligibility)
     *
     * @param exportedDeliveries exported deliveries
     * @param orderAuditParams   audit information
     */
    public ExportResultImpl(final Set<Long> exportedDeliveries,
                            final Map<String, String> orderAuditParams) {
        this.exportedDeliveries = Collections.unmodifiableSet(exportedDeliveries);
        this.orderAuditParams = Collections.unmodifiableMap(orderAuditParams);
        this.nextOrderState = null;
        this.nextDeliveryState = Collections.emptyMap();
    }

    /**
     * Follow up export result with eligibility values for next exporter in chain
     *
     * @param exportedDeliveries exported deliveries
     * @param orderAuditParams   audit information
     * @param nextOrderState     next order state
     * @param nextDeliveryState  next state for exported deliveries
     */
    public ExportResultImpl(final Set<Long> exportedDeliveries,
                            final Map<String, String> orderAuditParams,
                            final String nextOrderState,
                            final String nextDeliveryState) {
        this.exportedDeliveries = Collections.unmodifiableSet(exportedDeliveries);
        this.orderAuditParams = Collections.unmodifiableMap(orderAuditParams);
        this.nextOrderState = nextOrderState;
        this.nextDeliveryState = mapStateForAllExportedDeliveries(exportedDeliveries, nextDeliveryState);
    }

    public Map<Long, String> mapStateForAllExportedDeliveries(final Set<Long> exportedDeliveries,
                                                              final String nextDeliveryState) {
        if (StringUtils.isNotBlank(nextDeliveryState)) {
            final Map<Long, String> deliveryEligibility = new HashMap<>();
            for (final Long exportedId : exportedDeliveries) {
                deliveryEligibility.put(exportedId, nextDeliveryState);
            }
            return deliveryEligibility;
        }
        return Collections.emptyMap();
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> getExportedDeliveryIds() {
        return exportedDeliveries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getOrderAuditParams() {
        return orderAuditParams;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNextExportEligibilityForOrder() {
        return nextOrderState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, String> getNextExportEligibilityForDelivery() {
        return nextDeliveryState;
    }

}
