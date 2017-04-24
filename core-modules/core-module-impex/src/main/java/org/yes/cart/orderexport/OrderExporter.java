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

package org.yes.cart.orderexport;

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 20/02/2017
 * Time: 11:35
 */
public interface OrderExporter {

    /**
     * Check if this exporter supports given supplier code.
     *
     * @param customerOrder customer order
     * @param customerOrderDeliveries specific customer order delivery if this is delivery update
     *
     * @return true if supplier code is supported
     */
    boolean supports(CustomerOrder customerOrder, Collection<CustomerOrderDelivery> customerOrderDeliveries);

    /**
     * Export single customer order.
     *
     * @param customerOrder customer order
     * @param customerOrderDeliveries specific customer order delivery if this is delivery update
     *
     * @return set of delivery ids that were exported by this exporter
     *
     * @throws Exception must throw exception to denote that export process failed. Exception message will be used
     *         to mark failed order
     */
    ExportResult export(CustomerOrder customerOrder, Collection<CustomerOrderDelivery> customerOrderDeliveries) throws Exception;

    /**
     * Get Id of this exporter.
     *
     * @return exporter
     */
    String getExporterId();

    /**
     * Exporter priority.
     *
     * @return priority
     */
    int getPriority();

    /**
     * Result object
     */
    interface ExportResult {

        /**
         * Set of updated deliveries ID. Empty set denotes that no deliveries where updated.
         *
         * @return set of PKs
         */
        Set<Long> getExportedDeliveryIds();

        /**
         * Additional data to be save with successfully exported order. Could be audit information
         * of message exchange details.
         *
         * @return set of parameters
         */
        Map<String, String> getOrderAuditParams();

        /**
         * Optional export eligibility to trigger after this exporter.
         *
         * Note that if multiple exporters are available for given order then ONLY one of them
         * can set the next eligibility.
         *
         * In current implementation it will be the first exporter that runs, for others we raise
         * alert
         *
         * @return next eligibility
         */
        String getNextExportEligibilityForOrder();

        /**
         * Optional export eligibility to trigger after this exporter.
         * If this map is provided then {@link #getNextExportEligibilityForOrder()} must be provided.
         *
         * @return next eligibility
         */
        Map<Long, String> getNextExportEligibilityForDelivery();

    }

}
