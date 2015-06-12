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


import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
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
    public List<CustomerOrderPayment> findBy(
            final String orderNumber,
            final Date fromDate,
            final Date tillDate,
            final String lastCardDigits,
            final String cardHolderName,
            final String paymentGateway
    ) {

        final ArrayList<Criterion> creterias = new ArrayList<Criterion>(6);

        if (orderNumber != null) {
            creterias.add(Restrictions.like("orderNumber", orderNumber, MatchMode.ANYWHERE));
        }

        if (fromDate != null) {
            creterias.add(Restrictions.ge("orderDate", fromDate));
        }

        if (tillDate != null) {
            creterias.add(Restrictions.le("orderDate", tillDate));
        }

        if (lastCardDigits != null) {
            creterias.add(Restrictions.eq("cardNumber", lastCardDigits));
        }

        if (cardHolderName != null) {
            creterias.add(Restrictions.like("cardHolderName", cardHolderName, MatchMode.ANYWHERE));
        }

        if (paymentGateway != null) {
            creterias.add(Restrictions.eq("transactionGatewayLabel", paymentGateway));
        }

        return getGenericDao().findByCriteria(
                creterias.toArray(new Criterion[creterias.size()])
        );

    }


    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderPayment> findBy(final String orderNumber,
                                             final String shipmentNumber,
                                             final String paymentProcessorResult,
                                             final String transactionOperation) {

        final ArrayList<Criterion> creteria = new ArrayList<Criterion>(4);

        if (orderNumber != null) {
            creteria.add(Restrictions.eq("orderNumber", orderNumber));
        }

        if (shipmentNumber != null) {
            creteria.add(Restrictions.eq("orderShipment", shipmentNumber));
        }

        if (paymentProcessorResult != null) {
            creteria.add(Restrictions.eq("paymentProcessorResult", paymentProcessorResult));
        }

        if (transactionOperation != null) {
            creteria.add(Restrictions.eq("transactionOperation", transactionOperation));
        }

        return getGenericDao().findByCriteria(creteria.toArray(new Criterion[creteria.size()]));

    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderPayment> findBy(final String orderNumber,
                                             final String shipmentNumber,
                                             final String[] paymentProcessorResult,
                                             final String[] transactionOperation) {

        final ArrayList<Criterion> creteria = new ArrayList<Criterion>(4);

        if (orderNumber != null) {
            creteria.add(Restrictions.eq("orderNumber", orderNumber));
        }

        if (shipmentNumber != null) {
            creteria.add(Restrictions.eq("orderShipment", shipmentNumber));
        }

        if (paymentProcessorResult != null) {
            creteria.add(Restrictions.in("paymentProcessorResult", paymentProcessorResult));
        }

        if (transactionOperation != null) {
            creteria.add(Restrictions.in("transactionOperation", transactionOperation));
        }

        return getGenericDao().findByCriteria(creteria.toArray(new Criterion[creteria.size()]));

    }
}
