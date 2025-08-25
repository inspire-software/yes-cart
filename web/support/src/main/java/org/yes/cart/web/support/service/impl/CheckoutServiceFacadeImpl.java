/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.domain.entity.impl.PriceModelImpl;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.report.ReportDescriptor;
import org.yes.cart.report.ReportGenerator;
import org.yes.cart.report.impl.ReportDescriptors;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderAssemblyException;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.utils.MoneyUtils;
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

    String determineTaxCode(final CustomerOrder customerOrder) {

        // TODO: fix this later (YC-760)
        String firstTaxCode = "";
        for (final CartItem item : customerOrder.getOrderDetail()) {
            if (StringUtils.isNotBlank(item.getTaxCode())) {
                firstTaxCode = item.getTaxCode();
                break;
            }
        }
        return firstTaxCode;

    }

    static final String ORDER_SUB_TOTAL_REF = "order-sub-total";

    /** {@inheritDoc} */
    @Override
    public PriceModel getOrderTotalSub(final CustomerOrder customerOrder, final ShoppingCart cart) {

        final Total grandTotal = getOrderTotal(customerOrder);

        final boolean hasTax = MoneyUtils.isPositive(grandTotal.getTotalTax());

        final boolean showTax = hasTax && cart.getShoppingContext().isTaxInfoEnabled();
        final boolean showTaxNet = showTax && cart.getShoppingContext().isTaxInfoUseNet();
        final boolean showTaxAmount = showTax && cart.getShoppingContext().isTaxInfoShowAmount();

        if (showTax) {

            final String firstTaxCode = determineTaxCode(customerOrder);

            final BigDecimal netAmount = grandTotal.getTotalAmount().subtract(grandTotal.getTotalTax());
            final BigDecimal taxPercent = grandTotal.getTotalTax().divide(netAmount, Constants.TAX_SCALE, BigDecimal.ROUND_FLOOR);
            final BigDecimal taxRate = taxPercent.multiply(MoneyUtils.HUNDRED).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP);

            final boolean priceTaxExclusive = MoneyUtils.isFirstBiggerThanSecond(grandTotal.getTotalAmount(), grandTotal.getTotal());

            // no discounts on sub total
            return new PriceModelImpl(
                    ORDER_SUB_TOTAL_REF,
                    customerOrder.getCurrency(),
                    BigDecimal.ONE,
                    false,
                    false,
                    showTaxNet ? grandTotal.getTotalAmount().subtract(grandTotal.getTotalTax()) : grandTotal.getTotalAmount(),
                    null,
                    showTax, showTaxNet, showTaxAmount,
                    firstTaxCode,
                    taxRate,
                    priceTaxExclusive,
                    grandTotal.getTotalTax(),
                    null,
                    null
            );

        }

        // no discounts on sub total
        return new PriceModelImpl(
                ORDER_SUB_TOTAL_REF,
                customerOrder.getCurrency(),
                BigDecimal.ONE,
                false,
                false,
                grandTotal.getTotal(),
                null,
                null,
                null
        );

    }

    static final String ORDER_TOTAL_REF = "order-total";

    /** {@inheritDoc} */
    @Override
    public PriceModel getOrderTotalAmount(final CustomerOrder customerOrder, final ShoppingCart cart) {

        final Total grandTotal = getOrderTotal(customerOrder);

        final boolean hasTax = MoneyUtils.isPositive(grandTotal.getTotalTax());
        final boolean showTax = hasTax; // Total amount always must show tax if it has it
        final boolean showTaxAmount = showTax && cart.getShoppingContext().isTaxInfoShowAmount();

        final boolean hasDiscounts = MoneyUtils.isFirstBiggerThanSecond(grandTotal.getListTotalAmount(), grandTotal.getTotalAmount());

        if (showTax) {

            final String firstTaxCode = determineTaxCode(customerOrder);

            final BigDecimal netAmount = grandTotal.getTotalAmount().subtract(grandTotal.getTotalTax());
            final BigDecimal taxPercent = grandTotal.getTotalTax().divide(netAmount, Constants.TAX_SCALE, BigDecimal.ROUND_FLOOR);
            final BigDecimal taxRate = taxPercent.multiply(MoneyUtils.HUNDRED).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP);

            return new PriceModelImpl(
                    ORDER_TOTAL_REF,
                    customerOrder.getCurrency(),
                    BigDecimal.ONE,
                    false,
                    false,
                    hasDiscounts ? grandTotal.getListTotalAmount() : grandTotal.getTotalAmount(),
                    hasDiscounts ? grandTotal.getTotalAmount() : null,
                    showTax, false, showTaxAmount,
                    firstTaxCode,
                    taxRate,
                    false, // TotalAmount always includes taxes
                    grandTotal.getTotalTax(),
                    null,
                    null
            );


        }

        if (hasDiscounts) {

            return new PriceModelImpl(
                    ORDER_TOTAL_REF,
                    customerOrder.getCurrency(),
                    BigDecimal.ONE,
                    false,
                    false,
                    grandTotal.getListTotalAmount(),
                    grandTotal.getTotalAmount(),
                    null,
                    null
            );

        }

        return new PriceModelImpl(
                ORDER_TOTAL_REF,
                customerOrder.getCurrency(),
                BigDecimal.ONE,
                false,
                false,
                grandTotal.getTotalAmount(),
                null,
                null,
                null
        );

    }

    /** {@inheritDoc} */
    @Override
    public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery delivery) {
        return amountCalculationStrategy.calculate(customerOrder, delivery);
    }

    static final String DELIVERY_TOTAL_REF = "delivery-sub-total";

    @Override
    public PriceModel getOrderDeliveryTotalSub(final CustomerOrder customerOrder, final CustomerOrderDelivery delivery, final ShoppingCart cart) {

        final Total grandTotal = getOrderDeliveryTotal(customerOrder, delivery);

        final boolean hasTax = MoneyUtils.isPositive(grandTotal.getSubTotalTax());

        final boolean showTax = hasTax && cart.getShoppingContext().isTaxInfoEnabled();
        final boolean showTaxNet = showTax && cart.getShoppingContext().isTaxInfoUseNet();
        final boolean showTaxAmount = showTax && cart.getShoppingContext().isTaxInfoShowAmount();

        if (showTax) {

            final String firstTaxCode = delivery.getTaxCode();

            final BigDecimal netAmount = grandTotal.getSubTotalAmount().subtract(grandTotal.getSubTotalTax());
            final BigDecimal taxPercent = grandTotal.getSubTotalTax().divide(netAmount, Constants.TAX_SCALE, BigDecimal.ROUND_FLOOR);
            final BigDecimal taxRate = taxPercent.multiply(MoneyUtils.HUNDRED).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP);

            final boolean priceTaxExclusive = MoneyUtils.isFirstBiggerThanSecond(grandTotal.getSubTotalAmount(), grandTotal.getSubTotal());

            // no discounts on sub total
            return new PriceModelImpl(
                    DELIVERY_TOTAL_REF,
                    customerOrder.getCurrency(),
                    BigDecimal.ONE,
                    false,
                    false,
                    showTaxNet ? grandTotal.getSubTotalAmount().subtract(grandTotal.getSubTotalTax()) : grandTotal.getSubTotalAmount(),
                    null,
                    showTax, showTaxNet, showTaxAmount,
                    firstTaxCode,
                    taxRate,
                    priceTaxExclusive,
                    grandTotal.getSubTotalTax(),
                    null,
                    null
            );

        }

        // no discounts on sub total
        return new PriceModelImpl(
                DELIVERY_TOTAL_REF,
                customerOrder.getCurrency(),
                BigDecimal.ONE,
                false,
                false,
                grandTotal.getSubTotal(),
                null,
                null,
                null
        );

    }

    static final String DELIVERY_SHIPPING_REF = "delivery-shipping";

    @Override
    public PriceModel getOrderDeliveryTotalShipping(final CustomerOrder customerOrder, final CustomerOrderDelivery delivery, final ShoppingCart cart) {

        final Total grandTotal = getOrderDeliveryTotal(customerOrder, delivery);

        boolean hasTax = MoneyUtils.isPositive(grandTotal.getDeliveryTax());
        final boolean showTax = hasTax && cart.getShoppingContext().isTaxInfoEnabled();
        final boolean showTaxNet = showTax && cart.getShoppingContext().isTaxInfoUseNet();
        final boolean showTaxAmount = showTax && cart.getShoppingContext().isTaxInfoShowAmount();

        final boolean hasDiscount = MoneyUtils.isFirstBiggerThanSecond(grandTotal.getDeliveryListCost(), grandTotal.getDeliveryCost());

        if (showTax) {

            final String firstTaxCode = delivery.getTaxCode();

            final BigDecimal netAmount = grandTotal.getDeliveryCostAmount().subtract(grandTotal.getDeliveryTax());
            final BigDecimal taxPercent = grandTotal.getDeliveryTax().divide(netAmount, Constants.TAX_SCALE, BigDecimal.ROUND_FLOOR);
            final BigDecimal taxRate = taxPercent.multiply(MoneyUtils.HUNDRED).setScale(Constants.MONEY_SCALE, BigDecimal.ROUND_HALF_UP);

            final boolean priceTaxExclusive = MoneyUtils.isFirstBiggerThanSecond(grandTotal.getDeliveryCostAmount(), grandTotal.getDeliveryCost());

            if (hasDiscount) {

                return new PriceModelImpl(DELIVERY_SHIPPING_REF, customerOrder.getCurrency(), BigDecimal.ONE,
                        false,
                        false,
                        showTaxNet ?
                                MoneyUtils.getNetAmount(grandTotal.getDeliveryListCost(), taxRate, !priceTaxExclusive) :
                                MoneyUtils.getGrossAmount(grandTotal.getDeliveryListCost(), taxRate, !priceTaxExclusive),
                        showTaxNet ?
                                grandTotal.getDeliveryCostAmount().subtract(grandTotal.getDeliveryTax()) :
                                grandTotal.getDeliveryCostAmount(),
                        showTax, showTaxNet, showTaxAmount,
                        firstTaxCode,
                        taxRate,
                        priceTaxExclusive,
                        grandTotal.getDeliveryTax(),
                        null,
                        null
                );

            }


            return new PriceModelImpl(DELIVERY_SHIPPING_REF, customerOrder.getCurrency(), BigDecimal.ONE,
                    false,
                    false,
                    showTaxNet ?
                            grandTotal.getDeliveryCostAmount().subtract(grandTotal.getDeliveryTax()) :
                            grandTotal.getDeliveryCostAmount(),
                    null,
                    showTax, showTaxNet, showTaxAmount,
                    firstTaxCode,
                    taxRate,
                    priceTaxExclusive,
                    grandTotal.getDeliveryTax(),
                    null,
                    null
            );

        }

        if (hasDiscount) {

            return new PriceModelImpl(
                    DELIVERY_SHIPPING_REF,
                    customerOrder.getCurrency(),
                    BigDecimal.ONE,
                    false,
                    false,
                    grandTotal.getDeliveryListCost(),
                    grandTotal.getDeliveryCost(),
                    null,
                    null
            );

        }

        return new PriceModelImpl(
                DELIVERY_SHIPPING_REF,
                customerOrder.getCurrency(),
                BigDecimal.ONE,
                false,
                false,
                grandTotal.getDeliveryCost(),
                null,
                null,
                null
        );

    }

    /** {@inheritDoc} */
    @Override
    public List<CustomerOrderPayment> findPaymentRecordsByOrderNumber(final String orderNumber) {
        return customerOrderPaymentService.findPayments(orderNumber, null, (String) null, (String) null);
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
                    false,
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
                carrierSlaPGs = new HashSet<>(carrierSla.getSupportedPaymentGatewaysAsList());
            } else {
                // every subsequent SLA will limit supported PGs via intersection
                carrierSlaPGs.retainAll(carrierSla.getSupportedPaymentGatewaysAsList());
            }
        }

        if (CollectionUtils.isEmpty(carrierSlaPGs)) {
            return Collections.emptyList();
        }

        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false, cart.getShoppingContext().getShopCode());
        final List<Pair<PaymentGatewayDescriptor, String>> available = new ArrayList<>(descriptors.size());
        final Map<String, Integer> sorting = new HashMap<>();
        final boolean approve = cart.getOrderInfo().isDetailByKeyTrue(AttributeNamesKeys.Cart.ORDER_INFO_APPROVE_ORDER);
        final String customerTags = cart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_CUSTOMER_TAGS);
        for (final PaymentGatewayDescriptor descriptor : descriptors) {
            if (carrierSlaPGs.contains(descriptor.getLabel())) {
                final PaymentGateway gateway = paymentModulesManager.getPaymentGateway(descriptor.getLabel(), cart.getShoppingContext().getShopCode());
                if (approve && gateway.getPaymentGatewayFeatures().isOnlineGateway()) {
                    continue; // TODO: online PG's should not be allowed through approve flow at least for now
                }
                final String restrictToCustomerTags = gateway.getParameterValue("restrictToCustomerTags");
                if (restrictAccessByTag(customerTags, restrictToCustomerTags)) {
                    continue; // customer has tags but they do not match restrictions
                }
                available.add(new Pair<>(descriptor, gateway.getName(lang)));
                final String priority = gateway.getParameterValue("priority");
                if (priority == null) {
                    sorting.put(descriptor.getLabel(), 0);
                } else {
                    sorting.put(descriptor.getLabel(), NumberUtils.toInt(priority, 0));
                }
            }
        }

        available.sort((pgd1, pgd2) -> {
            final int priority1 = sorting.get(pgd1.getFirst().getLabel());
            final int priority2 = sorting.get(pgd2.getFirst().getLabel());
            if (priority1 == priority2) {
                return pgd1.getSecond().compareToIgnoreCase(pgd2.getSecond()); // if no priority sort naturally by name
            }
            return Integer.compare(priority1, priority2); // if prioritised then sort by priority
        });

        return available;

    }

    boolean restrictAccessByTag(final String customerTags, final String restrictToCustomerTags) {

        if (StringUtils.isNotBlank(restrictToCustomerTags)) {
            final List<String> allowedTags = Arrays.asList(StringUtils.split(restrictToCustomerTags, ','));
            if (StringUtils.isNotBlank(customerTags)) {
                final List<String> existingTags = Arrays.asList(StringUtils.split(customerTags, ' '));
                for (final String existingTag : existingTags) {
                    if (allowedTags.contains(existingTag)) {
                        return false; // matched
                    }
                }
            }
            return true; // no match
        }
        return false; // no restrictions
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOrderPaymentSuccessful(final CustomerOrder customerOrder) {

        if (customerOrder == null) {
            return false;
        }

        // AUTH or AUTH_CAPTURE for full order amount with successful result
        final List<CustomerOrderPayment> payments =
                customerOrderPaymentService.findPayments(customerOrder.getOrdernum(), null,
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

        final Set<String> allPromos = new HashSet<>();

        if (StringUtils.isNotBlank(customerOrder.getAppliedPromo())) {
            allPromos.addAll(Arrays.asList(StringUtils.split(customerOrder.getAppliedPromo(), ',')));
        }
        return allPromos;

    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getOrderShippingPromoCodes(final CustomerOrderDelivery orderDelivery) {

        final Set<String> allPromos = new HashSet<>();

        if (StringUtils.isNotBlank(orderDelivery.getAppliedPromo())) {
            allPromos.addAll(Arrays.asList(StringUtils.split(orderDelivery.getAppliedPromo(), ',')));
        }

        return allPromos;
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getOrderItemPromoCodes(final CartItem orderDeliveryDet) {

        final Set<String> allPromos = new HashSet<>();

        if (StringUtils.isNotBlank(orderDeliveryDet.getAppliedPromo())) {
            allPromos.addAll(Arrays.asList(StringUtils.split(orderDeliveryDet.getAppliedPromo(), ',')));
        }

        return allPromos;

    }

    /** {@inheritDoc} */
    @Override
    public CartValidityModel validateCart(final ShoppingCart shoppingCart) {
        return customerOrderService.validateCart(shoppingCart);
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
        return ReportDescriptors.reportDelivery();
    }

    /** {@inheritDoc} */
    @Override
    public void printOrderByReference(final String reference, final OutputStream outputStream) {

        final CustomerOrder order = customerOrderService.findByReference(reference);
        if (order != null) {
            final Pair data = new Pair(order, order.getDelivery());

            final Map<String, Object> values = new HashMap<>();
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
