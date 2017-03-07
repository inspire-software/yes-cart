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

package org.yes.cart.report.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.report.ReportPair;
import org.yes.cart.report.ReportWorker;
import org.yes.cart.service.vo.VoPaymentService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12/11/2014
 * Time: 10:29
 */
public class PaymentReportWorker implements ReportWorker {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentReportWorker.class);

    private final VoPaymentService paymentService;

    public PaymentReportWorker(final VoPaymentService paymentService) {
        this.paymentService = paymentService;
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
        final String fromDate = (String) currentSelection.get("fromDate");
        final String tillDate = (String) currentSelection.get("tillDate");

        final StringBuilder filter = new StringBuilder();

        if (StringUtils.isNotBlank(orderNumber)) {
            filter.append("#").append(orderNumber);
        } else if (fromDate != null || tillDate != null) {
            if (StringUtils.isNotBlank(fromDate)) {
                filter.append(fromDate);
            }
            filter.append("<");
            if (StringUtils.isNotBlank(tillDate)) {
                filter.append(tillDate);
            }
        }

        try {
            return (List) paymentService.getFiltered(filter.length() > 0 ? filter.toString() : null, Integer.MAX_VALUE);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getEnhancedParameterValues(final List<Object> result, final Map<String, Object> currentSelection) {
        return new HashMap<String, Object>(currentSelection);
    }
}
