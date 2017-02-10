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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.ProductPriceModelImpl;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.report.ReportDescriptor;
import org.yes.cart.report.ReportGenerator;
import org.yes.cart.report.ReportParameter;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderAssemblyException;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.web.support.service.CheckoutServiceFacade;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 * Date: 13/01/2014
 * Time: 08:36
 */
public class CheckoutServiceFacadeImpl implements CheckoutServiceFacade {

    private final CustomerOrderService customerOrderService;
    private final AmountCalculationStrategy amountCalculationStrategy;
    private final DeliveryTimeEstimationVisitor deliveryTimeEstimationVisitor;
    private final CustomerOrderPaymentService customerOrderPaymentService;
    private final CarrierSlaService carrierSlaService;
    private final PaymentProcessorFactory paymentProcessorFactory;
    private final PaymentModulesManager paymentModulesManager;
    private final ReportGenerator reportGenerator;

    public CheckoutServiceFacadeImpl(final CustomerOrderService customerOrderService,
                                     final AmountCalculationStrategy amountCalculationStrategy,
                                     final DeliveryTimeEstimationVisitor deliveryTimeEstimationVisitor,
                                     final CustomerOrderPaymentService customerOrderPaymentService,
                                     final CarrierSlaService carrierSlaService,
                                     final PaymentProcessorFactory paymentProcessorFactory,
                                     final PaymentModulesManager paymentModulesManager,
                                     final ReportGenerator reportGenerator) {
        this.customerOrderService = customerOrderService;
        this.amountCalculationStrategy = amountCalculationStrategy;
        this.deliveryTimeEstimationVisitor = deliveryTimeEstimationVisitor;
        this.customerOrderPaymentService = customerOrderPaymentService;
        this.carrierSlaService = carrierSlaService;
        this.paymentProcessorFactory = paymentProcessorFactory;
        this.paymentModulesManager = paymentModulesManager;
        this.reportGenerator = reportGenerator;
    }

    /** {@inheritDoc} */
    @Override
    public CustomerOrder findByReference(final String reference) {
        return customerOrderService.findByReference(reference);
    }

    /** {@inheritDoc} */
    @Override
    public CustomerOrder findByGuid(final String shoppingCartGuid) {
        return customerOrderService.findByReference(shoppingCartGuid);
    }

    /** {@inheritDoc} */
    @Override
    public Total getOrderTotal(final CustomerOrder customerOrder) {
        return amountCalculationStrategy.calculate(customerOrder);
    }

    static final String ORDER_TOTAL_REF = "yc-order-total";

    /** {@inheritDoc} */
    @Override
    public ProductPriceModel getOrderTotalAmount(final CustomerOrder customerOrder, final ShoppingCart cart) {

        final Total grandTotal = getOrderTotal(customerOrder);

        // TODO: fix this later (YC-760)
        String firstTaxCode = "";
        for (final CartItem item : customerOrder.getOrderDetail()) {
            if (StringUtils.isNotBlank(item.getTaxCode())) {
                firstTaxCode = item.getTaxCode();
                break;
            }
        }

        BigDecimal taxRate = BigDecimal.ZERO;
        if (MoneyUtils.isFirstBiggerThanSecond(grandTotal.getTotalTax(), BigDecimal.ZERO)) {

            final BigDecimal netAmount = grandTotal.getTotalAmount().subtract(grandTotal.getTotalTax());
            final BigDecimal taxPercent = grandTotal.getTotalTax().divide(netAmount, Constants.DEFAULT_SCALE, BigDecimal.ROUND_FLOOR);
            taxRate = taxPercent.movePointRight(2);

        }

        return new ProductPriceModelImpl(ORDER_TOTAL_REF, customerOrder.getCurrency(), BigDecimal.ONE,
                grandTotal.getListTotalAmount(),
                grandTotal.getTotalAmount(),
                false, false, false,
                firstTaxCode,
                taxRate,
                false, // TotalAmount includes taxes
                grandTotal.getTotalTax()
        );

    }

    /** {@inheritDoc} */
    @Override
    public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery delivery) {
        return amountCalculationStrategy.calculate(customerOrder, delivery);
    }

    /** {@inheritDoc} */
    @Override
    public List<CustomerOrderPayment> findPaymentRecordsByOrderNumber(final String orderNumber) {
        return customerOrderPaymentService.findBy(orderNumber, null, (String) null, (String) null);
    }

    /** {@inheritDoc} */
    @Override
    public Payment createPaymentToAuthorize(final CustomerOrder order) {

        final String pgLabel = order.getPgLabel();

        final Shop pgShop = order.getShop().getMaster() != null ? order.getShop().getMaster() : order.getShop();
        final PaymentProcessor processor = paymentProcessorFactory.create(pgLabel, pgShop.getCode());

        if (processor.isPaymentGatewayEnabled() && processor.getPaymentGateway().getPaymentGatewayFeatures().isRequireDetails()) {
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
        final Shop pgShop = order.getShop().getMaster() != null ? order.getShop().getMaster() : order.getShop();
        return paymentModulesManager.getPaymentGateway(pgLabel, pgShop.getCode());

    }

    /** {@inheritDoc} */
    @Override
    public List<Pair<PaymentGatewayDescriptor, String>> getPaymentGatewaysDescriptors(final Shop shop, final ShoppingCart cart) {

        if (cart.getOrderInfo().isDetailByKeyTrue(AttributeNamesKeys.Cart.ORDER_INFO_BLOCK_CHECKOUT)) {
            return Collections.emptyList();
        }

        final String lang = cart.getCurrentLocale();
        if (!cart.isAllCarrierSlaSelected() || cart.getShippingList().isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> carrierSlaPGs = null;
        for (final Long carrierSlaId : cart.getCarrierSlaId().values()) {
            final CarrierSla carrierSla = carrierSlaService.getById(carrierSlaId);
            if (carrierSla == null) {
                return Collections.emptyList();
            }
            if (carrierSlaPGs == null) {
                // initialise first
                carrierSlaPGs = new HashSet<String>(carrierSla.getSupportedPaymentGatewaysAsList());
            } else {
                // every subsequent SLA will limit supported PGs via intersection
                carrierSlaPGs.retainAll(carrierSla.getSupportedPaymentGatewaysAsList());
            }
        }

        if (CollectionUtils.isEmpty(carrierSlaPGs)) {
            return Collections.emptyList();
        }

        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false, cart.getShoppingContext().getShopCode());
        final List<Pair<PaymentGatewayDescriptor, String>> available = new ArrayList<Pair<PaymentGatewayDescriptor, String>>(descriptors.size());
        final Map<String, Integer> sorting = new HashMap<String, Integer>();
        final boolean approve = cart.getOrderInfo().isDetailByKeyTrue(AttributeNamesKeys.Cart.ORDER_INFO_APPROVE_ORDER);
        for (final PaymentGatewayDescriptor descriptor : descriptors) {
            if (carrierSlaPGs.contains(descriptor.getLabel())) {
                final PaymentGateway gateway = paymentModulesManager.getPaymentGateway(descriptor.getLabel(), cart.getShoppingContext().getShopCode());
                if (approve && gateway.getPaymentGatewayFeatures().isOnlineGateway()) {
                    continue; // TODO: online PG's should not be allowed through approve flow at least for now
                }
                available.add(new Pair<PaymentGatewayDescriptor, String>(descriptor, gateway.getName(lang)));
                final String priority = gateway.getParameterValue("priority");
                if (priority == null) {
                    sorting.put(descriptor.getLabel(), 0);
                } else {
                    sorting.put(descriptor.getLabel(), NumberUtils.toInt(priority, 0));
                }
            }
        }

        Collections.sort(
                available,
                new Comparator<Pair<PaymentGatewayDescriptor, String>>() {
                    public int compare(final Pair<PaymentGatewayDescriptor, String> pgd1, final Pair<PaymentGatewayDescriptor, String> pgd2) {
                        final int priority1 = sorting.get(pgd1.getFirst().getLabel());
                        final int priority2 = sorting.get(pgd2.getFirst().getLabel());
                        if (priority1 == priority2) {
                            return pgd1.getSecond().compareTo(pgd2.getSecond()); // if no priority sort naturally by name
                        }
                        return priority1 - priority2; // if prioritised then sort by priority
                    }
                }
        );

        return available;

    }

    /** {@inheritDoc} */
    @Override
    public boolean isOrderPaymentSuccessful(final CustomerOrder customerOrder) {

        if (customerOrder == null) {
            return false;
        }

        // AUTH or AUTH_CAPTURE for full order amount with successful result
        final List<CustomerOrderPayment> payments =
                customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null,
                        new String[]{Payment.PAYMENT_STATUS_OK},
                        new String[]{PaymentGateway.AUTH, PaymentGateway.AUTH_CAPTURE});

        if (payments == null || payments.isEmpty()) {
            return false;
        }

        BigDecimal amount = BigDecimal.ZERO;
        for (final CustomerOrderPayment payment : payments) {
            amount = amount.add(payment.getPaymentAmount());
        }

        return amount.compareTo(customerOrder.getOrderTotal()) == 0;

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
    public Set<String> getOrderItemPromoCodes(final CartItem orderDeliveryDet) {

        final Set<String> allPromos = new HashSet<String>();

        if (StringUtils.isNotBlank(orderDeliveryDet.getAppliedPromo())) {
            allPromos.addAll(Arrays.asList(StringUtils.split(orderDeliveryDet.getAppliedPromo(), ',')));
        }

        return allPromos;

    }

    /** {@inheritDoc} */
    @Override
    public CustomerOrder createFromCart(final ShoppingCart shoppingCart) throws OrderAssemblyException {
        return customerOrderService.createFromCart(shoppingCart);
    }

    /** {@inheritDoc} */
    @Override
    public void estimateDeliveryTimeForOnlinePaymentOrder(final CustomerOrder customerOrder) {
        final PaymentGateway pg = getOrderPaymentGateway(customerOrder);
        if (pg != null && pg.getPaymentGatewayFeatures().isOnlineGateway()) {
            deliveryTimeEstimationVisitor.visit(customerOrder);
        } else {
            for (final CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
                delivery.setDeliveryGuaranteed(null);
                delivery.setDeliveryEstimatedMin(null);
                delivery.setDeliveryEstimatedMax(null);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Boolean> isMultipleDeliveryAllowedForCart(final ShoppingCart shoppingCart) {
        return customerOrderService.isOrderMultipleDeliveriesAllowed(shoppingCart);
    }

    /** {@inheritDoc} */
    @Override
    public CustomerOrder update(final CustomerOrder customerOrder) {
        return customerOrderService.update(customerOrder);
    }

    private ReportDescriptor createReceiptDescriptor() {
        final ReportDescriptor receipt = new ReportDescriptor();
        receipt.setReportId("reportDelivery");
        receipt.setXslfoBase("client/order/delivery");
        final ReportParameter param1 = new ReportParameter();
        param1.setParameterId("orderNumber");
        param1.setBusinesstype("String");
        param1.setMandatory(true);
        receipt.setParameters(Collections.singletonList(param1));
        return receipt;
    }

    /** {@inheritDoc} */
    @Override
    public void printOrderByReference(final String reference, final OutputStream outputStream) {

        final CustomerOrder order = customerOrderService.findByReference(reference);
        if (order != null) {
            final Pair data = new Pair(order, order.getDelivery());

            final Map<String, Object> values = new HashMap<String, Object>();
            values.put("orderNumber", order.getOrdernum());
            values.put("shop", order.getShop());

            reportGenerator.generateReport(
                    createReceiptDescriptor(),
                    values,
                    data,
                    order.getLocale(),
                    outputStream
            );

        }
    }
}
