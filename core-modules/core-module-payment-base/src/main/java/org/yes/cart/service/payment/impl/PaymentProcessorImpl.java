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

package org.yes.cart.service.payment.impl;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentAddress;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentAddressImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.dto.impl.PaymentLineImpl;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.impl.CustomerOrderPaymentEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.log.Markers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentProcessorImpl implements PaymentProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentProcessorImpl.class);

    private PaymentGateway paymentGateway;
    private final CustomerOrderPaymentService customerOrderPaymentService;


    /**
     * Construct payment processor.
     *
     * @param customerOrderPaymentService generic service to use.
     */
    public PaymentProcessorImpl(final CustomerOrderPaymentService customerOrderPaymentService) {
        this.customerOrderPaymentService = customerOrderPaymentService;
    }

    /**
     * {@inheritDoc}
     */
    public PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPaymentGatewayEnabled() {
        return paymentGateway != null;
    }

    /**
     * Set payment gateway to use.
     *
     * @param paymentGateway see PaymentGatewayInternalForm to use.
     */
    public void setPaymentGateway(final PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }


    /**
     * AuthCapture or immediate sale operation will be be used if payment gateway does not support normal flow authorize - delivery - capture.
     *
     * @param order  to authorize payments.
     * @param params for payment gateway to create template from. Also if this map contains key
     *               forceSinglePayment, only one payment will be created (hack to support pay pal express).
     * @return status of operation.
     */
    protected String authorizeCapture(final CustomerOrder order, final Map params) {

        final List<Payment> paymentsToAuthorize = createPaymentsToAuthorize(
                order,
                params.containsKey("forceSinglePayment"),
                params,
                PaymentGateway.AUTH_CAPTURE);

        String paymentResult = null;

        boolean atLeastOneProcessing = false;
        boolean atLeastOneOk = false;
        boolean atLeastOneError = false;
        for (Payment payment : paymentsToAuthorize) {
            try {
                payment = getPaymentGateway().authorizeCapture(payment);
                paymentResult = payment.getPaymentProcessorResult();
            } catch (Throwable th) {
                paymentResult = Payment.PAYMENT_STATUS_FAILED;
                payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                payment.setPaymentProcessorBatchSettlement(false);
                payment.setTransactionOperationResultMessage(th.getMessage());

            } finally {
                final CustomerOrderPayment authCaptureOrderPayment = new CustomerOrderPaymentEntity();
                //customerOrderPaymentService.getGenericDao().getEntityFactory().getByIface(CustomerOrderPayment.class);
                BeanUtils.copyProperties(payment, authCaptureOrderPayment); //from PG object to persisted
                authCaptureOrderPayment.setPaymentProcessorResult(paymentResult);
                authCaptureOrderPayment.setShopCode(order.getShop().getCode());
                customerOrderPaymentService.create(authCaptureOrderPayment);
                if (Payment.PAYMENT_STATUS_PROCESSING.equals(paymentResult)) {
                    atLeastOneProcessing = true;
                } else if (!Payment.PAYMENT_STATUS_OK.equals(paymentResult)) {
                    // all other statuses - we fail
                    atLeastOneError = true;
                } else {
                    atLeastOneOk = true;
                }
            }

        }

        if (atLeastOneError) {
            if (atLeastOneOk) {
                // we need to put this in processing since this will move order to waiting payment
                // from there we have cancellation flow (with manual refund)
                return Payment.PAYMENT_STATUS_PROCESSING;
            }
            return Payment.PAYMENT_STATUS_FAILED;
        }

        return atLeastOneProcessing ? Payment.PAYMENT_STATUS_PROCESSING : Payment.PAYMENT_STATUS_OK;

    }


    /**
     * {@inheritDoc}
     */
    public String authorize(final CustomerOrder order, final Map params) {

        if (getPaymentGateway().getPaymentGatewayFeatures().isSupportAuthorize()) {

            final List<Payment> paymentsToAuthorize = createPaymentsToAuthorize(
                    order,
                    params.containsKey("forceSinglePayment"),
                    params,
                    PaymentGateway.AUTH);

            boolean atLeastOneProcessing = false;
            boolean atLeastOneError = false;

            for (Payment payment : paymentsToAuthorize) {
                String paymentResult = null;
                try {
                    if (atLeastOneError) {
                        // no point in order auths as we reverse all in case of at least one failure
                        paymentResult = Payment.PAYMENT_STATUS_FAILED;
                        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                        payment.setPaymentProcessorBatchSettlement(false);
                        payment.setTransactionOperationResultMessage("skipped due to previous errors");
                    } else {
                        payment = getPaymentGateway().authorize(payment);
                        paymentResult = payment.getPaymentProcessorResult();
                    }
                } catch (Throwable th) {
                    paymentResult = Payment.PAYMENT_STATUS_FAILED;
                    payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                    payment.setPaymentProcessorBatchSettlement(false);
                    payment.setTransactionOperationResultMessage(th.getMessage());
                } finally {
                    final CustomerOrderPayment authOrderPayment = new CustomerOrderPaymentEntity();
                    //customerOrderPaymentService.getGenericDao().getEntityFactory().getByIface(CustomerOrderPayment.class);
                    BeanUtils.copyProperties(payment, authOrderPayment); //from PG object to persisted
                    authOrderPayment.setPaymentProcessorResult(paymentResult);
                    authOrderPayment.setShopCode(order.getShop().getCode());
                    customerOrderPaymentService.create(authOrderPayment);
                    if (Payment.PAYMENT_STATUS_PROCESSING.equals(paymentResult)) {
                        atLeastOneProcessing = true;
                    } else if (!Payment.PAYMENT_STATUS_OK.equals(paymentResult)) {
                        // all other statuses - we fail
                        atLeastOneError = true;
                    }
                }
            }
            if (atLeastOneError) {
                reverseAuthorizations(order.getOrdernum());
                return Payment.PAYMENT_STATUS_FAILED;
            }
            return atLeastOneProcessing ? Payment.PAYMENT_STATUS_PROCESSING : Payment.PAYMENT_STATUS_OK;

        } else if (getPaymentGateway().getPaymentGatewayFeatures().isSupportAuthorizeCapture()) {

            return authorizeCapture(order, params);

        }
        throw new RuntimeException(
                MessageFormat.format(
                        "Payment gateway {0}  must supports 'authorize' or 'authorize-capture' operations",
                        getPaymentGateway().getLabel()
                )
        );
    }


    /**
     * Reverse authorized payments. This can be when one of the payments from whole set is failed.
     * Reverse authorization will be applied to authorized payments only
     *
     * @param orderNum order with some authorized payments
     */
    protected void reverseAuthorizations(final String orderNum) {

        if (getPaymentGateway().getPaymentGatewayFeatures().isSupportReverseAuthorization()) {

            final List<CustomerOrderPayment> paymentsToRevAuth = determineOpenAuthorisations(orderNum, null);

            for (CustomerOrderPayment customerOrderPayment : paymentsToRevAuth) {
                Payment payment = new PaymentImpl();
                BeanUtils.copyProperties(customerOrderPayment, payment); //from persisted to PG object

                String paymentResult = null;
                try {
                    payment = getPaymentGateway().reverseAuthorization(payment); //pass "original" to perform reverse authorization.
                    paymentResult = payment.getPaymentProcessorResult();
                } catch (Throwable th) {
                    paymentResult = Payment.PAYMENT_STATUS_FAILED;
                    payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                    payment.setPaymentProcessorBatchSettlement(false);
                    payment.setTransactionOperationResultMessage(th.getMessage());

                } finally {
                    final CustomerOrderPayment authReversedOrderPayment = new CustomerOrderPaymentEntity();
                    //customerOrderPaymentService.getGenericDao().getEntityFactory().getByIface(CustomerOrderPayment.class);
                    BeanUtils.copyProperties(payment, authReversedOrderPayment); //from PG object to persisted
                    authReversedOrderPayment.setPaymentProcessorResult(paymentResult);
                    authReversedOrderPayment.setShopCode(customerOrderPayment.getShopCode());
                    customerOrderPaymentService.create(authReversedOrderPayment);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String shipmentComplete(final CustomerOrder order, final String orderShipmentNumber, final Map params) {

        if (getPaymentGateway().getPaymentGatewayFeatures().isSupportAuthorize()) {

            final boolean isMultiplePaymentsSupports = getPaymentGateway().getPaymentGatewayFeatures().isSupportAuthorizePerShipment();

            final List<CustomerOrderPayment> paymentsToCapture =
                    determineOpenAuthorisations(order.getOrdernum(), isMultiplePaymentsSupports ? orderShipmentNumber : order.getOrdernum());

            LOG.info("Attempting to capture funds for Order num {} Shipment num {}", order.getOrdernum(), orderShipmentNumber);

            if (paymentsToCapture.size() > 1) {
                LOG.warn( //must be only one record
                        MessageFormat.format(
                                "Payment gateway {0} with features {1}. Found {2} records to capture, but expected 1 only. Order num {3} Shipment num {4}",
                                getPaymentGateway().getLabel(), getPaymentGateway().getPaymentGatewayFeatures(), paymentsToCapture.size(), order.getOrdernum(), orderShipmentNumber
                        )
                );
            } else if (paymentsToCapture.isEmpty()) {
                LOG.debug( //this could be a single payment PG and it was already captured
                        MessageFormat.format(
                                "Payment gateway {0} with features {1}. Found 0 records to capture, possibly already captured all payments. Order num {2} Shipment num {3}",
                                getPaymentGateway().getLabel(), getPaymentGateway().getPaymentGatewayFeatures(), order.getOrdernum(), orderShipmentNumber
                        )
                );

            }

            final boolean forceManualProcessing = Boolean.TRUE.equals(params.get("forceManualProcessing"));
            final String forceManualProcessingMessage = (String) params.get("forceManualProcessingMessage");
            boolean wasError = false;
            String paymentResult = null;

            // We always attempt to Capture funds at this stage.
            // Funds are captured either:
            // 1. for delivery for authorise per shipment PG; or
            // 2. captured as soon as first delivery is shipped (thereafter there will be no AUTHs to CAPTURE,
            // so all subsequent deliveries will not have any paymentsToCapture)

            for (CustomerOrderPayment paymentToCapture : paymentsToCapture) {
                Payment payment = new PaymentImpl();
                BeanUtils.copyProperties(paymentToCapture, payment); //from persisted to PG object
                payment.setTransactionOperation(PaymentGateway.CAPTURE);


                try {
                    if (forceManualProcessing) {
                        payment.setTransactionReferenceId(UUID.randomUUID().toString());
                        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
                        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
                        payment.setPaymentProcessorBatchSettlement(true);
                        payment.setTransactionGatewayLabel("forceManualProcessing");
                        payment.setTransactionOperationResultCode("forceManualProcessing");
                        payment.setTransactionOperationResultMessage(forceManualProcessingMessage);
                    } else {
                        payment = getPaymentGateway().capture(payment); //pass "original" to perform fund capture.
                    }
                    paymentResult = payment.getPaymentProcessorResult();
                } catch (Throwable th) {
                    paymentResult = Payment.PAYMENT_STATUS_FAILED;
                    payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                    payment.setPaymentProcessorBatchSettlement(false);
                    payment.setTransactionOperationResultMessage(th.getMessage());
                    LOG.error(Markers.alert(), "CAPTURE transaction [" + payment.getTransactionReferenceId() + "] failed, cause: " + th.getMessage());
                    LOG.error("CAPTURE transaction [" + payment + "] failed, cause: " + payment, th);
                } finally {
                    final CustomerOrderPayment captureOrderPayment = new CustomerOrderPaymentEntity();
                    //customerOrderPaymentService.getGenericDao().getEntityFactory().getByIface(CustomerOrderPayment.class);
                    BeanUtils.copyProperties(payment, captureOrderPayment); //from PG object to persisted
                    captureOrderPayment.setPaymentProcessorResult(paymentResult);
                    captureOrderPayment.setShopCode(paymentToCapture.getShopCode());
                    customerOrderPaymentService.create(captureOrderPayment);
                }
                if (!Payment.PAYMENT_STATUS_OK.equals(paymentResult)) {
                    wasError = true;
                }
            }

            return wasError ? Payment.PAYMENT_STATUS_FAILED : Payment.PAYMENT_STATUS_OK;
        }
        return Payment.PAYMENT_STATUS_OK;
    }


    /**
     * {@inheritDoc}
     */
    public String cancelOrder(final CustomerOrder order, final Map params) {

        if (!CustomerOrder.ORDER_STATUS_CANCELLED.equals(order.getOrderStatus()) &&
                !CustomerOrder.ORDER_STATUS_RETURNED.equals(order.getOrderStatus())) {

            reverseAuthorizations(order.getOrdernum());

            final boolean forceManualProcessing = Boolean.TRUE.equals(params.get("forceManualProcessing"));
            final String forceManualProcessingMessage = (String) params.get("forceManualProcessingMessage");
            boolean wasError = false;

            final List<CustomerOrderPayment> paymentsToRollBack = determineOpenCaptures(order.getOrdernum(), null);

            /*
               We do NOT need to check for features (isSupportRefund(), isSupportVoid()). PG must create payments with
               Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED for audit purposes and manual flow support.
            */

            for (CustomerOrderPayment customerOrderPayment : paymentsToRollBack) {
                Payment payment = null;
                String paymentResult = null;
                try {
                    payment = new PaymentImpl();
                    BeanUtils.copyProperties(customerOrderPayment, payment); //from persisted to PG object
                    if (forceManualProcessing) {
                        payment.setTransactionOperation(PaymentGateway.REFUND);
                        payment.setTransactionReferenceId(UUID.randomUUID().toString());
                        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
                        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);
                        payment.setPaymentProcessorBatchSettlement(false);
                        payment.setTransactionGatewayLabel("forceManualProcessing");
                        payment.setTransactionOperationResultCode("forceManualProcessing");
                        payment.setTransactionOperationResultMessage(forceManualProcessingMessage);
                    } else {
                        if (customerOrderPayment.isPaymentProcessorBatchSettlement()) {
                            // refund
                            payment.setTransactionOperation(PaymentGateway.REFUND);
                            payment = getPaymentGateway().refund(payment);
                        } else {
                            //void
                            payment.setTransactionOperation(PaymentGateway.VOID_CAPTURE);
                            payment = getPaymentGateway().voidCapture(payment);
                        }
                    }
                    paymentResult = payment.getPaymentProcessorResult();
                } catch (Throwable th) {
                    LOG.error(
                            MessageFormat.format(
                                    "Can not perform roll back operation on payment record {0} payment {1}",
                                    customerOrderPayment.getCustomerOrderPaymentId(),
                                    payment
                            ), th
                    );
                    paymentResult = Payment.PAYMENT_STATUS_FAILED;
                    wasError = true;
                } finally {
                    final CustomerOrderPayment captureReversedOrderPayment = new CustomerOrderPaymentEntity();
                    //customerOrderPaymentService.getGenericDao().getEntityFactory().getByIface(CustomerOrderPayment.class);
                    BeanUtils.copyProperties(payment, captureReversedOrderPayment); //from PG object to persisted
                    captureReversedOrderPayment.setPaymentProcessorResult(paymentResult);
                    captureReversedOrderPayment.setShopCode(customerOrderPayment.getShopCode());
                    customerOrderPaymentService.create(captureReversedOrderPayment);
                }
                if (!Payment.PAYMENT_STATUS_OK.equals(paymentResult)) {
                    wasError = true;
                }


            }

            return wasError ? Payment.PAYMENT_STATUS_FAILED : Payment.PAYMENT_STATUS_OK;
        }
        LOG.warn("Cannot refund canceled order  {}",
                order.getOrdernum()
        );
        return Payment.PAYMENT_STATUS_FAILED;
    }


    /**
     * Create list of payment to authorize.
     *
     * @param order                order
     * @param params               parameters
     * @param transactionOperation operation in term of payment processor
     * @param forceSinglePaymentIn flag is true for authCapture operation, when payment gateway not supports several payments per
     *                             order
     * @return list of  payments with details
     */
    public List<Payment> createPaymentsToAuthorize(final CustomerOrder order,
                                                   final boolean forceSinglePaymentIn,
                                                   final Map params,
                                                   final String transactionOperation) {

        Assert.notNull(order, "Customer order expected");

        final boolean forceSinglePayment = forceSinglePaymentIn || params.containsKey("forceSinglePayment");

        final Payment templatePayment = fillPaymentPrototype(
                order,
                getPaymentGateway().createPaymentPrototype(transactionOperation, params),
                transactionOperation,
                getPaymentGateway().getLabel());

        final List<Payment> rez = new ArrayList<Payment>();
        if (forceSinglePayment || !getPaymentGateway().getPaymentGatewayFeatures().isSupportAuthorizePerShipment()) {

            final List<CustomerOrderPayment> existing = customerOrderPaymentService.findBy(order.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, transactionOperation);
            if (existing.isEmpty()) {

                Payment payment = (Payment) SerializationUtils.clone(templatePayment);
                BigDecimal runningTotal = Total.ZERO;
                BigDecimal runningTotalTax = Total.ZERO;
                final Iterator<CustomerOrderDelivery> deliveryIt = order.getDelivery().iterator();
                while (deliveryIt.hasNext()) {
                    final CustomerOrderDelivery delivery = deliveryIt.next();
                    final Pair<BigDecimal, BigDecimal> amountAndTax = fillPayment(order, delivery, payment, true, runningTotal, runningTotalTax, !deliveryIt.hasNext());
                    runningTotal = runningTotal.add(amountAndTax.getFirst());
                    runningTotalTax = runningTotalTax.add(amountAndTax.getSecond());
                }
                rez.add(payment);

            }

        } else {

            BigDecimal runningTotal = Total.ZERO;
            BigDecimal runningTotalTax = Total.ZERO;
            final Iterator<CustomerOrderDelivery> deliveryIt = order.getDelivery().iterator();
            while (deliveryIt.hasNext()) {
                final CustomerOrderDelivery delivery = deliveryIt.next();
                final List<CustomerOrderPayment> existing = customerOrderPaymentService.findBy(order.getOrdernum(), delivery.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, transactionOperation);
                if (existing.isEmpty()) {
                    Payment payment = (Payment) SerializationUtils.clone(templatePayment);
                    final Pair<BigDecimal, BigDecimal> amountAndTax = fillPayment(order, delivery, payment, false, runningTotal, runningTotalTax, !deliveryIt.hasNext());
                    runningTotal = runningTotal.add(amountAndTax.getFirst());
                    runningTotalTax = runningTotalTax.add(amountAndTax.getSecond());
                    rez.add(payment);
                } else {
                    for (final CustomerOrderPayment existingPayment : existing) {
                        runningTotal = runningTotal.add(existingPayment.getPaymentAmount());
                        runningTotalTax = runningTotalTax.add(existingPayment.getTaxAmount());
                    }
                }
            }
        }
        return rez;
    }

    /**
     * Fill single payment with data
     *
     * @param order     order
     * @param delivery  delivery
     * @param payment   payment to fill
     * @param singlePay is it single pay for whole order
     * @param runningTotal total amount for created payments so far
     * @param runningTotalTax total tax amount for created payments so far
     * @param lastDelivery last delivery in this order
     *
     * @return amount for current payment
     */
    private Pair<BigDecimal, BigDecimal> fillPayment(final CustomerOrder order,
                                                     final CustomerOrderDelivery delivery,
                                                     final Payment payment,
                                                     final boolean singlePay,
                                                     final BigDecimal runningTotal,
                                                     final BigDecimal runningTotalTax,
                                                     final boolean lastDelivery) {

        if (payment.getTransactionReferenceId() == null) {
            // can be set by external payment gateway
            payment.setTransactionReferenceId(delivery.getDeliveryNum());
        }


        payment.setOrderShipment(singlePay ? order.getOrdernum() : delivery.getDeliveryNum());

        fillPaymentItems(delivery, payment);
        fillPaymentShipment(order, delivery, payment);
        return fillPaymentAmount(order, delivery, payment, singlePay, runningTotal, runningTotalTax, lastDelivery);
    }

    /**
     * Calculate delivery amount according to shipment sla cost and items in particular delivery.
     *
     * @param order    order
     * @param delivery delivery
     * @param payment  payment
     * @param singlePay is it single pay for whole order
     * @param runningTotal total amount for created payments so far
     * @param runningTotalTax total amount tax for created payments so far
     * @param lastDelivery last delivery in this order
     *
     * @return amount for current payment
     */
    private Pair<BigDecimal, BigDecimal> fillPaymentAmount(final CustomerOrder order,
                                                           final CustomerOrderDelivery delivery,
                                                           final Payment payment,
                                                           final boolean singlePay,
                                                           final BigDecimal runningTotal,
                                                           final BigDecimal runningTotalTax,
                                                           final boolean lastDelivery) {
        BigDecimal itemsAndShipping = Total.ZERO;
        BigDecimal itemsAndShippingTax = Total.ZERO;
        if (singlePay) {
            // Single pay is just the total
            itemsAndShipping = order.getOrderTotal();
            itemsAndShippingTax = order.getOrderTotalTax();
        } else if (lastDelivery) {
            // For last delivery we apply the remainder to avoid rounding errors
            itemsAndShipping = order.getOrderTotal().subtract(runningTotal);
            itemsAndShippingTax = order.getOrderTotalTax().subtract(runningTotalTax);
        } else {
            BigDecimal itemsOnly = Total.ZERO;
            BigDecimal itemsOnlyTax = Total.ZERO;
            BigDecimal shippingOnly = Total.ZERO;
            BigDecimal shippingOnlyTax = Total.ZERO;
            // Calculate sum of all payment lines (which include shipping as well)
            for (PaymentLine paymentLine : payment.getOrderItems()) {
                if (paymentLine.isShipment()) {
                    // shipping price already includes shipping level promotions
                    final BigDecimal shipping = paymentLine.getUnitPrice().setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
                    final BigDecimal shippingTax = paymentLine.getTaxAmount().setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
                    itemsAndShipping = itemsAndShipping.add(shipping);
                    itemsAndShippingTax = itemsAndShippingTax.add(shippingTax);
                    shippingOnly = shippingOnly.add(shipping);
                    shippingOnlyTax = shippingOnlyTax.add(shippingTax);
                } else {
                    // unit price already includes item level promotions
                    final BigDecimal item = paymentLine.getQuantity().multiply(paymentLine.getUnitPrice()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
                    final BigDecimal itemTax = paymentLine.getTaxAmount();
                    itemsAndShipping = itemsAndShipping.add(item);
                    itemsAndShippingTax = itemsAndShippingTax.add(itemTax);
                    itemsOnly = itemsOnly.add(item);
                    itemsOnlyTax = itemsOnlyTax.add(itemTax);
                }
            }
            // Order promotions are applied to all items in all deliveries so we scale the amounts equally
            if (order.isPromoApplied()) {
                // work out the percentage of order level promotion per delivery

                // work out the real sub total using item promotional prices
                // DO NOT use the order.getListPrice() as this is the list price in catalog and we calculate
                // promotions against sale price
                BigDecimal orderTotalList = BigDecimal.ZERO;
                for (final CustomerOrderDet detail : order.getOrderDetail()) {
                    orderTotalList = orderTotalList.add(detail.getQty().multiply(detail.getGrossPrice()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
                }

                final BigDecimal orderTotal = order.getGrossPrice();
                // take the list price (sub total of items using list price)
                final BigDecimal discount = orderTotalList.subtract(orderTotal).divide(orderTotalList, 10, RoundingMode.HALF_UP);
                // scale delivery items without shipping total in accordance with order level discount percentage
                itemsAndShipping = itemsOnly.multiply(BigDecimal.ONE.subtract(discount)).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
                itemsAndShippingTax = itemsOnlyTax.multiply(BigDecimal.ONE.subtract(discount)).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
                // add unscaled shipping
                itemsAndShipping = itemsAndShipping.add(shippingOnly);
                itemsAndShippingTax = itemsAndShippingTax.add(shippingOnlyTax);
            }
        }
        payment.setPaymentAmount(itemsAndShipping);
        payment.setTaxAmount(itemsAndShippingTax);
        payment.setOrderCurrency(order.getCurrency());
        payment.setOrderLocale(order.getLocale());

        return new Pair<BigDecimal, BigDecimal>(itemsAndShipping, itemsAndShippingTax);
    }

    private void fillPaymentShipment(final CustomerOrder order, final CustomerOrderDelivery delivery, final Payment payment) {
        payment.getOrderItems().add(
                new PaymentLineImpl(
                        delivery.getCarrierSla() == null ? "N/A" : String.valueOf(delivery.getCarrierSla().getGuid()),
                        delivery.getCarrierSla() == null ? "No SLA" :
                                new FailoverStringI18NModel(
                                        delivery.getCarrierSla().getDisplayName(),
                                        delivery.getCarrierSla().getName()).getValue(order.getLocale()),
                        BigDecimal.ONE,
                        delivery.getGrossPrice(),
                        delivery.getGrossPrice().subtract(delivery.getNetPrice()),
                        true
                )
        );
    }

    private void fillPaymentItems(final CustomerOrderDelivery delivery, final Payment payment) {
        for (CustomerOrderDeliveryDet deliveryDet : delivery.getDetail()) {
            payment.getOrderItems().add(
                    new PaymentLineImpl(
                            deliveryDet.getProductSkuCode(),
                            deliveryDet.getProductName(),
                            deliveryDet.getQty(),
                            deliveryDet.getGrossPrice(),
                            deliveryDet.getGrossPrice().subtract(deliveryDet.getNetPrice()).multiply(deliveryDet.getQty())
                    )
            );
        }
    }

    /**
     * Add information to template payment object.
     *
     * @param templatePayment         template payment.
     * @param order                   order
     * @param transactionOperation    operation in term of payment processor
     * @param transactionGatewayLabel label of payment gateway
     * @return payment prototype;
     */
    private Payment fillPaymentPrototype(final CustomerOrder order,
                                         final Payment templatePayment,
                                         final String transactionOperation,
                                         final String transactionGatewayLabel) {

        Address shippingAddr = order.getShippingAddressDetails();
        Address billingAddr = order.getBillingAddressDetails();

        if (billingAddr != null) {
            PaymentAddress addr = new PaymentAddressImpl();
            BeanUtils.copyProperties(billingAddr, addr);
            templatePayment.setBillingAddress(addr);
        }

        if (shippingAddr != null) {
            PaymentAddress addr = new PaymentAddressImpl();
            BeanUtils.copyProperties(shippingAddr, addr);
            templatePayment.setShippingAddress(addr);
        }

        templatePayment.setBillingAddressString(order.getBillingAddress());
        templatePayment.setShippingAddressString(order.getShippingAddress());

        templatePayment.setBillingEmail(order.getEmail());

        templatePayment.setOrderDate(order.getOrderTimestamp());
        templatePayment.setOrderCurrency(order.getCurrency());
        templatePayment.setOrderLocale(order.getLocale());
        templatePayment.setOrderNumber(order.getOrdernum());

        templatePayment.setTransactionOperation(transactionOperation);
        templatePayment.setTransactionGatewayLabel(transactionGatewayLabel);

        return templatePayment;
    }



    protected List<CustomerOrderPayment> determineOpenAuthorisations(final String orderNumber, final String orderShipmentNumber) {

        final List<CustomerOrderPayment> paymentsToCapture = new ArrayList<CustomerOrderPayment>(customerOrderPaymentService.findBy(
                orderNumber,
                orderShipmentNumber,
                Payment.PAYMENT_STATUS_OK,
                PaymentGateway.AUTH
        ));

        if (!paymentsToCapture.isEmpty()) {

            final List<CustomerOrderPayment> paymentsCapturedOrReversedAuth = new ArrayList<CustomerOrderPayment>(customerOrderPaymentService.findBy(
                    orderNumber,
                    orderShipmentNumber,
                    new String[] { Payment.PAYMENT_STATUS_OK, Payment.PAYMENT_STATUS_PROCESSING },
                    new String[] { PaymentGateway.CAPTURE, PaymentGateway.REVERSE_AUTH }
            ));

            filterOutAlreadyProcessed(paymentsToCapture, paymentsCapturedOrReversedAuth);

        }

        return paymentsToCapture;
    }


    protected List<CustomerOrderPayment> determineOpenCaptures(final String orderNumber, final String orderShipmentNumber) {

        final List<CustomerOrderPayment> paymentsCaptured = new ArrayList<CustomerOrderPayment>(customerOrderPaymentService.findBy(
                orderNumber,
                orderShipmentNumber,
                new String[] { Payment.PAYMENT_STATUS_OK },
                new String[] { PaymentGateway.CAPTURE, PaymentGateway.AUTH_CAPTURE }
        ));

        final List<CustomerOrderPayment> paymentsProcessing = new ArrayList<CustomerOrderPayment>(customerOrderPaymentService.findBy(
                orderNumber,
                orderShipmentNumber,
                new String[] { Payment.PAYMENT_STATUS_PROCESSING },
                new String[] { PaymentGateway.CAPTURE, PaymentGateway.AUTH_CAPTURE }
        ));

        if (!paymentsProcessing.isEmpty()) {

            final List<CustomerOrderPayment> paymentsFailed = new ArrayList<CustomerOrderPayment>(customerOrderPaymentService.findBy(
                    orderNumber,
                    orderShipmentNumber,
                    new String[] { Payment.PAYMENT_STATUS_FAILED, Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED },
                    new String[] { PaymentGateway.CAPTURE, PaymentGateway.AUTH_CAPTURE }
            ));

            // Remove all captured from processing
            filterOutAlreadyProcessed(paymentsProcessing, paymentsCaptured);
            // Remove all failed from processing
            filterOutAlreadyProcessed(paymentsProcessing, paymentsFailed);
            // Add all processing to captured
            paymentsCaptured.addAll(paymentsProcessing);

        }

        if (!paymentsCaptured.isEmpty()) {

            final List<CustomerOrderPayment> paymentsRefunded = new ArrayList<CustomerOrderPayment>(customerOrderPaymentService.findBy(
                    orderNumber,
                    orderShipmentNumber,
                    new String[] { Payment.PAYMENT_STATUS_OK },
                    new String[] { PaymentGateway.VOID_CAPTURE, PaymentGateway.REFUND }
            ));

            filterOutAlreadyProcessed(paymentsCaptured, paymentsRefunded);

        }

        return paymentsCaptured;

    }


    private void filterOutAlreadyProcessed(final List<CustomerOrderPayment> candidates, final List<CustomerOrderPayment> processed) {

        final Iterator<CustomerOrderPayment> candidatesIt = candidates.iterator();
        while(candidatesIt.hasNext()) {

            final CustomerOrderPayment candidate = candidatesIt.next();

            for (final CustomerOrderPayment payment : processed) {

                if (candidate.getOrderNumber().equals(payment.getOrderNumber())
                        && candidate.getOrderShipment().equals(payment.getOrderShipment())
                        && MoneyUtils.isFirstEqualToSecond(candidate.getPaymentAmount(), payment.getPaymentAmount())) {
                    // This is an exact match of already processed payment - need to remove it
                    candidatesIt.remove();
                    break;
                }

            }

        }
    }


}
