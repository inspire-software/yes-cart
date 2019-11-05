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

package org.yes.cart.payment.service.impl;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.utils.HQLUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public class CustomerOrderPaymentServiceImpl extends PaymentModuleGenericServiceImpl<CustomerOrderPayment>
        implements CustomerOrderPaymentService {

    private static final int DEFAULT_SCALE = 2;

    /**
     * Construct service to work with payments
     *
     * @param genericDao dao to use.
     */
    public CustomerOrderPaymentServiceImpl(final PaymentModuleGenericDAO<CustomerOrderPayment, Long> genericDao) {
        super(genericDao);
    }

    /**
     * Get order payment amount.
     * <p>
     * If no payments were made then the amount is equal to sum of all AUTH less REVERSE_AUTH.
     * <p>
     * Otherwise amount is sum of  CAPTURE or  AUTH_CAPTURE less VOID_CAPTURE and REFUND.
     *
     * @param orderNumber given order number.
     *
     * @return order amount
     */
    @Override
    public BigDecimal getOrderAmount(final String orderNumber) {

        final List<CustomerOrderPayment> payments = findCustomerOrderPayment(orderNumber, null,
                new String[] { Payment.PAYMENT_STATUS_OK },
                new String[] { PaymentGateway.AUTH_CAPTURE, PaymentGateway.CAPTURE, PaymentGateway.VOID_CAPTURE, PaymentGateway.REFUND });

        BigDecimal credit = BigDecimal.ZERO;
        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal voided = BigDecimal.ZERO;

        List<BigDecimal> credited = new ArrayList<>();

        for (CustomerOrderPayment payment : payments) {
            // only tracking real payments (including unsettled)
            if (PaymentGateway.AUTH_CAPTURE.equals(payment.getTransactionOperation()) || PaymentGateway.CAPTURE.equals(payment.getTransactionOperation())) {
                credit = credit.add(payment.getPaymentAmount());
                credited.add(payment.getPaymentAmount());
            }

        }

        for (CustomerOrderPayment payment : payments) {
            if (PaymentGateway.REFUND.equals(payment.getTransactionOperation())) {
                // refund is always tracked as it could be manual
                debit = debit.add(payment.getPaymentAmount());
            } else if (PaymentGateway.VOID_CAPTURE.equals(payment.getTransactionOperation())) {
                // There could be VOID_CAPTURE Ok for CAPTURE Processing, so we need to skip them, so we do not get negative
                final Iterator<BigDecimal> creditedIt = credited.iterator();
                while (creditedIt.hasNext()) {
                    // if this is VOID_CAPTURE for known CAPTURE
                    if (creditedIt.next().compareTo(payment.getPaymentAmount()) == 0) {
                        debit = debit.add(payment.getPaymentAmount());
                        creditedIt.remove();
                    }

                }
            }

        }

        return credit.subtract(debit).setScale(DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    }


    private Pair<String, Object[]> findCustomerOrderPaymentQuery(final boolean count,
                                                                 final String sort,
                                                                 final boolean sortDescending,
                                                                 final Set<String> shops,
                                                                 final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(p) from CustomerOrderPaymentEntity p ");
        } else {
            hqlCriteria.append("select p from CustomerOrderPaymentEntity p ");
        }

        final List paymentProcessorResult  = currentFilter != null ? currentFilter.remove("paymentProcessorResult") : null;
        final List transactionOperation  = currentFilter != null ? currentFilter.remove("transactionOperation") : null;

        if (CollectionUtils.isNotEmpty(shops)) {
            hqlCriteria.append(" where p.shopCode in (?1)  ");
            params.add(shops);
        }

        if (CollectionUtils.isNotEmpty(paymentProcessorResult)) {
            if (params.isEmpty()) {
                hqlCriteria.append(" where p.paymentProcessorResult in (?1)  ");
            } else {
                hqlCriteria.append(" and p.paymentProcessorResult in (?" + (params.size() + 1) + ")  ");
            }
            params.add(paymentProcessorResult);
        }

        if (CollectionUtils.isNotEmpty(transactionOperation)) {
            if (params.isEmpty()) {
                hqlCriteria.append(" where p.transactionOperation in (?1)  ");
            } else {
                hqlCriteria.append(" and p.transactionOperation in (?" + (params.size() + 1) + ")  ");
            }
            params.add(transactionOperation);
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "p", currentFilter);


        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by p." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomerOrderPayment> findCustomerOrderPayment(final int start, final int offset, final String sort, final boolean sortDescending, final Set<String> shops, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findCustomerOrderPaymentQuery(false, sort, sortDescending, shops, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCustomerOrderPaymentCount(final Set<String> shops, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findCustomerOrderPaymentQuery(true, null, false, shops, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomerOrderPayment> findCustomerOrderPayment(final String orderNumber,
                                                               final String shipmentNumber,
                                                               final String paymentProcessorResult,
                                                               final String transactionOperation) {

        return getGenericDao().findByCriteria(
                " where (?1 is null or e.orderNumber = ?1) and (?2 is null or e.orderShipment = ?2) and (?3 is null or e.paymentProcessorResult = (?3)) and (?4 is null or e.transactionOperation = (?4))",
                orderNumber,
                shipmentNumber,
                paymentProcessorResult,
                transactionOperation
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomerOrderPayment> findCustomerOrderPayment(final String orderNumber,
                                                               final String shipmentNumber,
                                                               final String[] paymentProcessorResult,
                                                               final String[] transactionOperation) {

        return getGenericDao().findByCriteria(
                " where (?1 is null or e.orderNumber = ?1) and (?2 is null or e.orderShipment = ?2) and (?3 is null or e.paymentProcessorResult in (?4)) and (?5 is null or e.transactionOperation in (?6))",
                orderNumber,
                shipmentNumber,
                paymentProcessorResult != null ? "1" : null,
                paymentProcessorResult != null ? Arrays.asList(paymentProcessorResult) : Collections.singletonList("x"),
                transactionOperation != null ? "1" : null,
                transactionOperation != null ? Arrays.asList(transactionOperation) : Collections.singletonList("x")
        );

    }
}
