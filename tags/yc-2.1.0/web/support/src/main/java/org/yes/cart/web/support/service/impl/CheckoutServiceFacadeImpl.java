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
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderAssemblyException;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.web.support.service.CheckoutServiceFacade;

import java.util.*;

/**
 * User: denispavlov
 * Date: 13/01/2014
 * Time: 08:36
 */
public class CheckoutServiceFacadeImpl implements CheckoutServiceFacade {

    private final CustomerOrderService customerOrderService;
    private final AmountCalculationStrategy amountCalculationStrategy;
    private final CustomerOrderPaymentService customerOrderPaymentService;
    private final PaymentProcessorFactory paymentProcessorFactory;
    private final PaymentModulesManager paymentModulesManager;

    public CheckoutServiceFacadeImpl(final CustomerOrderService customerOrderService,
                                     final AmountCalculationStrategy amountCalculationStrategy,
                                     final CustomerOrderPaymentService customerOrderPaymentService,
                                     final PaymentProcessorFactory paymentProcessorFactory,
                                     final PaymentModulesManager paymentModulesManager) {
        this.customerOrderService = customerOrderService;
        this.amountCalculationStrategy = amountCalculationStrategy;
        this.customerOrderPaymentService = customerOrderPaymentService;
        this.paymentProcessorFactory = paymentProcessorFactory;
        this.paymentModulesManager = paymentModulesManager;
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
    public Payment createPaymentToAuthorize(final CustomerOrder order) {

        final String pgLabel = order.getPgLabel();
        final PaymentProcessor processor = paymentProcessorFactory.create(pgLabel);
        final PaymentGateway gateway = processor.getPaymentGateway();

        if (gateway.getPaymentGatewayFeatures().isRequireDetails()) {
            return processor.createPaymentsToAuthorize(
                    order,
                    true,
                    Collections.EMPTY_MAP,
                    PaymentGateway.AUTH
            ).get(0);
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public PaymentGateway getOrderPaymentGateway(final CustomerOrder order) {

        final String pgLabel = order.getPgLabel();
        return paymentModulesManager.getPaymentGateway(pgLabel);

    }

    /** {@inheritDoc} */
    @Override
    public List<Pair<PaymentGatewayDescriptor, String>> getPaymentGatewaysDescriptors(final Shop shop, final ShoppingCart cart) {

        final String lang = cart.getCurrentLocale();
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false);
        final List<Pair<PaymentGatewayDescriptor, String>> available = new ArrayList<Pair<PaymentGatewayDescriptor, String>>(descriptors.size());
        for (final PaymentGatewayDescriptor descriptor : descriptors) {
            final PaymentGateway gateway = paymentModulesManager.getPaymentGateway(descriptor.getLabel());
            available.add(new Pair<PaymentGatewayDescriptor, String>(descriptor, gateway.getName(lang)));
        }
        return available;

    }

    /** {@inheritDoc} */
    @Override
    public boolean isOrderPaymentSuccessful(final CustomerOrder customerOrder) {

        if (customerOrder == null) {
            return false;
        }

        final List<CustomerOrderPayment> payments =
                customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);

        if (payments == null || payments.isEmpty()) {
            return false;
        }

        for (final CustomerOrderPayment payment : payments) {
            // AUTH or AUTH_CAPTURE for full order amount with successful result
            if (
                    (payment.getTransactionOperation().equals(PaymentGateway.AUTH)
                            || payment.getTransactionOperation().equals(PaymentGateway.AUTH_CAPTURE))
                    && (customerOrder.getPrice().compareTo(payment.getPaymentAmount()) == 0)
                    && Payment.PAYMENT_STATUS_OK.equals(payment.getPaymentProcessorResult())
                    ) {
                return true;
            }
        }

        return false;

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
    public CustomerOrder createFromCart(final ShoppingCart shoppingCart) throws OrderAssemblyException {
        return customerOrderService.createFromCart(shoppingCart, !shoppingCart.getOrderInfo().isMultipleDelivery());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isMultipleDeliveryAllowedForCart(final ShoppingCart shoppingCart) {
        return customerOrderService.isOrderMultipleDeliveriesAllowed(shoppingCart);
    }

    /** {@inheritDoc} */
    @Override
    public CustomerOrder update(final CustomerOrder customerOrder) {
        return customerOrderService.update(customerOrder);
    }
}
