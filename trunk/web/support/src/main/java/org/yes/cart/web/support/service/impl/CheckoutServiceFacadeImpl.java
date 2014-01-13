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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.web.support.service.CheckoutServiceFacade;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 13/01/2014
 * Time: 08:36
 */
public class CheckoutServiceFacadeImpl implements CheckoutServiceFacade {

    private final CustomerOrderService customerOrderService;
    private final AmountCalculationStrategy amountCalculationStrategy;
    private final CustomerOrderPaymentService customerOrderPaymentService;

    public CheckoutServiceFacadeImpl(final CustomerOrderService customerOrderService,
                                     final AmountCalculationStrategy amountCalculationStrategy,
                                     final CustomerOrderPaymentService customerOrderPaymentService) {
        this.customerOrderService = customerOrderService;
        this.amountCalculationStrategy = amountCalculationStrategy;
        this.customerOrderPaymentService = customerOrderPaymentService;
    }

    /** {@inheritDoc} */
    @Override
    public CustomerOrder findByGuid(final String shoppingCartGuid) {
        return customerOrderService.findByGuid(shoppingCartGuid);
    }

    /** {@inheritDoc} */
    @Override
    public Total getOrderTotal(final CustomerOrder customerOrder) {
        return amountCalculationStrategy.calculate(customerOrder);
    }

    /** {@inheritDoc} */
    @Override
    public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery delivery) {
        return amountCalculationStrategy.calculate(customerOrder, delivery);
    }

    /** {@inheritDoc} */
    @Override
    public List<CustomerOrderPayment> findPaymentRecordsByOrderNumber(final String orderNumber) {
        return customerOrderPaymentService.findBy(orderNumber, null, null, null);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOrderPaymentSuccessful(final CustomerOrder customerOrder) {

        if (customerOrder == null) {
            return false;
        }

        final List<CustomerOrderPayment> payments =
                customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);
        return (payments != null && !payments.isEmpty()
                && Payment.PAYMENT_STATUS_OK.equals(payments.get(0).getPaymentProcessorResult()));

    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getOrderPromoCodes(final CustomerOrder customerOrder) {

        final Set<String> allPromos = new HashSet<String>();

        if (StringUtils.isNotBlank(customerOrder.getAppliedPromo())) {
            allPromos.addAll(Arrays.asList(StringUtils.split(customerOrder.getAppliedPromo(), ',')));
        }
        return allPromos;

    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getOrderShippingPromoCodes(final CustomerOrderDelivery orderDelivery) {

        final Set<String> allPromos = new HashSet<String>();

        if (StringUtils.isNotBlank(orderDelivery.getAppliedPromo())) {
            allPromos.addAll(Arrays.asList(StringUtils.split(orderDelivery.getAppliedPromo(), ',')));
        }

        return allPromos;
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getOrderItemPromoCodes(final CustomerOrderDeliveryDet orderDeliveryDet) {

        final Set<String> allPromos = new HashSet<String>();

        if (StringUtils.isNotBlank(orderDeliveryDet.getAppliedPromo())) {
            allPromos.addAll(Arrays.asList(StringUtils.split(orderDeliveryDet.getAppliedPromo(), ',')));
        }

        return allPromos;

    }

    /** {@inheritDoc} */
    @Override
    public CustomerOrder createFromCart(final ShoppingCart shoppingCart) {
        return customerOrderService.createFromCart(shoppingCart, !shoppingCart.getOrderInfo().isMultipleDelivery());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMultipleDeliveryAllowedForCart(final ShoppingCart shoppingCart) {
        return customerOrderService.isOrderCanHasMultipleDeliveries(shoppingCart);
    }

    /** {@inheritDoc} */
    @Override
    public CustomerOrder update(final CustomerOrder customerOrder) {
        return customerOrderService.update(customerOrder);
    }
}
