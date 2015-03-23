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

package org.yes.cart.report.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.remote.service.RemoteCustomerOrderService;
import org.yes.cart.report.ReportWorker;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12/11/2014
 * Time: 13:52
 */
public class DeliveryReportWorker implements ReportWorker {

    private final RemoteCustomerOrderService remoteCustomerOrderService;

    public DeliveryReportWorker(final RemoteCustomerOrderService remoteCustomerOrderService) {
        this.remoteCustomerOrderService = remoteCustomerOrderService;
    }

    /**
     * {@inheritDoc}
     */
    public List<ReportPair> getParameterValues(final String lang, final String param, final Map<String, Object> currentSelection) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<Object> getResult(final String lang, final Map<String, Object> currentSelection) {

        final String orderNumber = (String) currentSelection.get("orderNumber");

        if (StringUtils.isBlank(orderNumber)) {
            return Collections.emptyList();
        }

        try {
            final List<CustomerOrderDTO> orders = remoteCustomerOrderService.findCustomerOrdersByCriteria(0, null, null, null, null, null, null, orderNumber);
            final List rez =  remoteCustomerOrderService.findDeliveryByOrderNumber(orderNumber, null); // All deliveries
            return (List) Collections.singletonList(new Pair(orders.get(0), rez));
        } catch (Exception e) {
            return Collections.emptyList();
        }

    }
}
