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

package org.yes.cart.payment.impl;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayInternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.impl.CustomerOrderPaymentEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.payment.impl.PaymentProcessorImpl;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This is surrogate for real processor, need to keep common processing logic during different pg tests.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentProcessorSurrogate extends PaymentProcessorImpl {

    private final CustomerOrderPaymentService customerOrderPaymentService;


    /**
     * Construct payment processor.
     *
     * @param customerOrderPaymentService generic service to use.
     */
    public PaymentProcessorSurrogate(final CustomerOrderPaymentService customerOrderPaymentService) {
        super(customerOrderPaymentService);
        this.customerOrderPaymentService = customerOrderPaymentService;
    }

    /**
     * Construct payment processor.
     *
     * @param customerOrderPaymentService generic service to use.
     */
    public PaymentProcessorSurrogate(CustomerOrderPaymentService customerOrderPaymentService,
                                     PaymentGatewayInternalForm paymentGateway) {
        super(customerOrderPaymentService);
        setPaymentGateway(paymentGateway);
        this.customerOrderPaymentService = customerOrderPaymentService;
    }


    /**
     * AuthCapture or immediate sale operation will be be used if payment gateway does not support normal flow authorize - delivery - capture.
     *
     * @param order  to authorize payments.
     * @param params for payment gateway to create template from. Also if this map contains key
     *               forceSinglePayment, only one payment will be created (hack to support pay pal express).
     * @return status of operation.
     */
    public String authorizeCapture(final CustomerOrder order, final Map params) {

        return super.authorizeCapture(order, params);
    }




    /**
     * Reverse authorized payments. This can be when one of the payments from whole set is failed.
     * Reverse authorization will be applied to authorized payments only
     *
     * @param orderNum order with some authorized payments
     */
    public void reverseAuthorizations(final String orderNum) {

        super.reverseAuthorizations(orderNum);

    }

    /**
     * {@inheritDoc}
     */
    public String shipmentComplete(final CustomerOrder order, final String orderShipmentNumber) {
        return shipmentComplete(order, orderShipmentNumber, (BigDecimal) null);
    }

    /**
     * Particular shipment is complete. Funds can be captured.
     * In case of multiple delivery and single payment, capture on last delivery.
     *
     * @param order               order
     * @param orderShipmentNumber internal shipment number.
     *                            Each order has at least one delivery.
     * @param addToPayment        amount to add for each payment if it not null
     * @return status of operation.
     */
    public String shipmentComplete(final CustomerOrder order, final String orderShipmentNumber, final BigDecimal addToPayment) {

        if (getPaymentGateway().getPaymentGatewayFeatures().isSupportAuthorize()) {

            final boolean isMultiplePaymentsSupports = getPaymentGateway().getPaymentGatewayFeatures().isSupportAuthorizePerShipment();

            final List<CustomerOrderPayment> paymentsToCapture =
                    determineOpenAuthorisations(order.getOrdernum(), isMultiplePaymentsSupports ? orderShipmentNumber : order.getOrdernum());

            final Logger log = ShopCodeContext.getLog(this);
            log.debug("Attempting to capture funds for Order num {} Shipment num {}", order.getOrdernum(), orderShipmentNumber);

            if (paymentsToCapture.size() > 1) {
                log.warn( //must be only one record
                        MessageFormat.format(
                                "Payment gateway {0} with features {1}. Found {2} records to capture, but expected 1 only. Order num {3} Shipment num {4}",
                                getPaymentGateway().getLabel(), getPaymentGateway().getPaymentGatewayFeatures(), paymentsToCapture.size(), order.getOrdernum(), orderShipmentNumber
                        )
                );
            } else if (paymentsToCapture.isEmpty()) {
                log.debug( //this could be a single payment PG and it was already captured
                        MessageFormat.format(
                                "Payment gateway {0} with features {1}. Found 0 records to capture, possibly already captured all payments. Order num {2} Shipment num {3}",
                                getPaymentGateway().getLabel(), getPaymentGateway().getPaymentGatewayFeatures(), order.getOrdernum(), orderShipmentNumber
                        )
                );

            }

            final boolean forceManualProcessing = false; // Boolean.TRUE.equals(params.get("forceManualProcessing"));
            final String forceManualProcessingMessage = null; // (String) params.get("forceManualProcessingMessage");
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
                payment.setPaymentAmount(payment.getPaymentAmount().add(addToPayment).setScale(2, BigDecimal.ROUND_HALF_UP));


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
                    ShopCodeContext.getLog(this).error("Cannot capture " + payment, th);

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
    public String cancelOrder(final CustomerOrder order, boolean useRefund) {

        if (!CustomerOrder.ORDER_STATUS_CANCELLED.equals(order.getOrderStatus()) &&
                !CustomerOrder.ORDER_STATUS_RETURNED.equals(order.getOrderStatus())) {

            reverseAuthorizations(order.getOrdernum());

            final boolean forceManualProcessing = false; // Boolean.TRUE.equals(params.get("forceManualProcessing"));
            final String forceManualProcessingMessage = null; // (String) params.get("forceManualProcessingMessage");
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
                        if (useRefund /* customerOrderPayment.isPaymentProcessorBatchSettlement()*/) {
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
                    ShopCodeContext.getLog(this).error(
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
        ShopCodeContext.getLog(this).warn("Can refund canceled order  {}",
                order.getOrdernum()
        );
        return Payment.PAYMENT_STATUS_FAILED;
    }


}
