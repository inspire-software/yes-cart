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


import org.apache.commons.lang.StringUtils;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;
import org.yes.cart.payment.service.CustomerOrderPaymentService;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public class CustomerOrderPaymentServiceImpl
        extends PaymentModuleGenericServiceImpl<CustomerOrderPayment>
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
    public BigDecimal getOrderAmount(final String orderNumber) {

        final List<CustomerOrderPayment> payments = findBy(orderNumber, null,
                new String[] { Payment.PAYMENT_STATUS_OK },
                new String[] { PaymentGateway.AUTH_CAPTURE, PaymentGateway.CAPTURE, PaymentGateway.VOID_CAPTURE, PaymentGateway.REFUND });

        BigDecimal credit = BigDecimal.ZERO;
        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal voided = BigDecimal.ZERO;

        List<BigDecimal> credited = new ArrayList<BigDecimal>();

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

        return credit.subtract(debit).setScale(DEFAULT_SCALE);
    }


    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderPayment> findBy(final String orderNumber,
                                             final Date fromDate,
                                             final Date tillDate,
                                             final String lastCardDigits,
                                             final String cardHolderName,
                                             final String paymentGateway) {

        return getGenericDao().findByCriteria(
                " where (?1 is null or e.orderNumber like ?1) and (?2 is null or e.orderDate >= ?2) and (?3 is null or e.orderDate <= ?3) and (?4 is null or e.cardNumber like ?4) and (?5 is null or lower(e.cardHolderName) like ?5) and (?6 is null or e.transactionGatewayLabel like ?6)",
                StringUtils.isNotBlank(orderNumber) ? "%" + orderNumber + "%" : null,
                fromDate,
                tillDate,
                StringUtils.isNotBlank(lastCardDigits) ? "%" + lastCardDigits + "%" : null,
                StringUtils.isNotBlank(cardHolderName) ? "%" + cardHolderName.toLowerCase() + "%" : null,
                StringUtils.isNotBlank(paymentGateway) ? paymentGateway : null
        );

    }


    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderPayment> findBy(final String orderNumber,
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
    public List<CustomerOrderPayment> findBy(final String orderNumber,
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
