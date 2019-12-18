/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.dto.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.payment.service.DtoCustomerOrderPaymentService;
import org.yes.cart.utils.DateUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 05/09/2016
 * Time: 18:33
 */
public class DtoCustomerOrderPaymentServiceImpl implements DtoCustomerOrderPaymentService {

    private final CustomerOrderPaymentService customerOrderPaymentService;

    public DtoCustomerOrderPaymentServiceImpl(final CustomerOrderPaymentService customerOrderPaymentService) {
        this.customerOrderPaymentService = customerOrderPaymentService;
    }


    private final static char[] ORDER_OR_CUSTOMER_OR_DETAILS = new char[] { '#', '?', '@', '^' };
    private final static char[] PAYMENT_STATUS = new char[] { '~', '-', '+', '*' };
    static {
        Arrays.sort(ORDER_OR_CUSTOMER_OR_DETAILS);
        Arrays.sort(PAYMENT_STATUS);
    }


    @Override
    public SearchResult<CustomerOrderPayment> findPayments(final Set<String> shopCodes, final SearchContext filter) {

        final Map<String, List> params = filter.reduceParameters("filter", "statuses", "operations");
        final List filterParam = params.get("filter");
        final List statusesParam = params.get("statuses");
        final List opsParam = params.get("operations");

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final Map<String, List> currentFilter = new HashMap<>();


        if (CollectionUtils.isNotEmpty(filterParam) && filterParam.get(0) instanceof String && StringUtils.isNotBlank((String) filterParam.get(0))) {

            final String textFilter = ((String) filterParam.get(0)).trim();
            final Pair<String, String> orderNumberOrCustomerOrDetails =
                    ComplexSearchUtils.checkSpecialSearch(textFilter, ORDER_OR_CUSTOMER_OR_DETAILS);
            final Pair<LocalDateTime, LocalDateTime> dateSearch = orderNumberOrCustomerOrDetails == null ?
                    ComplexSearchUtils.checkDateRangeSearch(textFilter) : null;

            if (orderNumberOrCustomerOrDetails != null) {

                if ("#".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // order/transaction number search
                    final String orderNumber = orderNumberOrCustomerOrDetails.getSecond();

                    SearchContext.JoinMode.OR.setMode(currentFilter);
                    currentFilter.put("orderNumber", Collections.singletonList(orderNumber));
                    currentFilter.put("orderShipment", Collections.singletonList(orderNumber));
                    currentFilter.put("transactionReferenceId", Collections.singletonList(orderNumber));
                    currentFilter.put("transactionRequestToken", Collections.singletonList(orderNumber));
                    currentFilter.put("transactionAuthorizationCode", Collections.singletonList(orderNumber));

                } else if ("?".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // customer search
                    final String customer = orderNumberOrCustomerOrDetails.getSecond();

                    SearchContext.JoinMode.OR.setMode(currentFilter);
                    currentFilter.put("cardNumber", Collections.singletonList(customer));
                    currentFilter.put("cardHolderName", Collections.singletonList(customer));
                    currentFilter.put("shopperIpAddress", Collections.singletonList(customer));

                } else if ("@".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // details search
                    final String details = orderNumberOrCustomerOrDetails.getSecond();

                    SearchContext.JoinMode.OR.setMode(currentFilter);
                    currentFilter.put("transactionGatewayLabel", Collections.singletonList(details));
                    currentFilter.put("transactionOperationResultCode", Collections.singletonList(details));

                } else if ("^".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // shop code search
                    final String shopCode = orderNumberOrCustomerOrDetails.getSecond();

                    currentFilter.put("shopCode", Collections.singletonList(shopCode));

                }

            } else if (dateSearch != null) {

                final Instant from = DateUtils.iFrom(dateSearch.getFirst());
                final Instant to = DateUtils.iFrom(dateSearch.getSecond());

                final List range = new ArrayList(2);
                if (from != null) {
                    range.add(SearchContext.MatchMode.GT.toParam(from));
                }
                if (to != null) {
                    range.add(SearchContext.MatchMode.LE.toParam(to));
                }

                currentFilter.put("createdTimestamp", range);

            } else {

                final String basic = textFilter;

                SearchContext.JoinMode.OR.setMode(currentFilter);
                currentFilter.put("orderNumber", Collections.singletonList(basic));
                currentFilter.put("orderShipment", Collections.singletonList(basic));
                currentFilter.put("cardHolderName", Collections.singletonList(basic));
                currentFilter.put("transactionGatewayLabel", Collections.singletonList(basic));

            }

        }

        // Filter by payment status
        if (CollectionUtils.isNotEmpty(statusesParam)) {
            currentFilter.put("paymentProcessorResult", statusesParam);
        }

        // Filter by payment status
        if (CollectionUtils.isNotEmpty(opsParam)) {
            currentFilter.put("transactionOperation", opsParam);
        }

        final int count = customerOrderPaymentService.findCustomerOrderPaymentCount(shopCodes, currentFilter);
        if (count > startIndex) {

            final List<CustomerOrderPayment> orders = customerOrderPaymentService.findPayments(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), shopCodes, currentFilter);

            return new SearchResult<>(filter, orders, count);

        }
        return new SearchResult<>(filter, Collections.emptyList(), count);

    }



}
