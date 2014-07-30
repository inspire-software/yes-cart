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

package org.yes.cart.payment.service.impl;


import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;
import org.yes.cart.payment.service.CustomerOrderPaymentService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
     * Get order amount.
     *
     * @param orderNumber given order number.
     * @return order amount
     */
    public BigDecimal getOrderAmount(final String orderNumber) {
        BigDecimal rez = BigDecimal.ZERO;
        final List<CustomerOrderPayment> payments = findBy(orderNumber, null, null, null);
        for (CustomerOrderPayment payment : payments) {
            if (
                    payment.getTransactionOperation().equals(PaymentGateway.AUTH)
                    || payment.getTransactionOperation().equals(PaymentGateway.AUTH_CAPTURE)   //external processing of payment form
                    ) {
                // We have two payment records AUTH and CAPTURE for the same amount.
                // Therefore we only need to sum up AUTH to get total payment value of the order.
                rez = rez.add(payment.getPaymentAmount());
            }
        }
        return rez.setScale(DEFAULT_SCALE);
    }


    /**
     * {@inheritDoc}
     */
    public List<?> getData(final String dataIdentifier, final Object... params) {
        final ArrayList<Criterion> creterias = new ArrayList<Criterion>(6);
        if ("paymentReport".equals(dataIdentifier)) {
            int idx = 0;
            for (Object param : params) {
                if (param != null) {
                    if (idx == 0) {
                        creterias.add(Restrictions.ge("createdTimestamp", param));
                    } else if (idx == 1) {
                        creterias.add(Restrictions.le("createdTimestamp", param));
                    }
                }
                idx ++;
            }
            return getGenericDao().findByCriteria(
                    creterias.toArray(new Criterion[creterias.size()])
            );
        }
        return null;

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
    public List<CustomerOrderPayment> findBy(
            final String orderNumber,
            final String shipmentNumber,
            final String paymentProcessorResult,
            final String transactionOperation) {

        final ArrayList<Criterion> creterias = new ArrayList<Criterion>(4);

        if (orderNumber != null) {
            creterias.add(Restrictions.eq("orderNumber", orderNumber));
        }

        if (shipmentNumber != null) {
            creterias.add(Restrictions.eq("orderShipment", shipmentNumber));
        }

        if (paymentProcessorResult != null) {
            creterias.add(Restrictions.eq("paymentProcessorResult", paymentProcessorResult));
        }

        if (transactionOperation != null) {
            creterias.add(Restrictions.eq("transactionOperation", transactionOperation));
        }

        return getGenericDao().findByCriteria(
                creterias.toArray(new Criterion[creterias.size()])

        );
    }


}
