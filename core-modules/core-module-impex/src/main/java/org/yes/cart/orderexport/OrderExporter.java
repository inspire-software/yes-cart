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
     */
    ExportResult export(CustomerOrder customerOrder, Collection<CustomerOrderDelivery> customerOrderDeliveries);

    /**
     * Get Id of this exporter.
     *
     * @return exporter
     */
    String getExporterId();

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

    }

}
