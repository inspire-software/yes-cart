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

package org.yes.cart.service.payment.impl;

import org.apache.commons.lang.SerializationUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
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
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentProcessorImpl implements PaymentProcessor {

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
     * Set payment gateway to use.
     *
     * @param paymentGateway see PaymentGatewayInternalForm to use.
     */
    public void setPaymentGateway(final PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }


    /**
     * AuthCapture or immediate sale operation wil be use if payment gateway not supports normal flow authorize - delivery - capture.
     *
     * @param order  to authorize payments.
     * @param params for payment gateway to create template from. Also if this map contains key
     *               forceSinglePayment, only one payment will be created (hack to support pay pal express).
     * @return status of operation.
     */
    String authorizeCapture(final CustomerOrder order, final Map params) {

        final List<Payment> paymentsToAuthorize = createPaymentsToAuthorize(
                order,
                params.containsKey("forceSinglePayment"),
                params,
                PaymentGateway.AUTH_CAPTURE);

        String paymentResult = null;

        for (Payment payment : paymentsToAuthorize) {
            try {
                payment = getPaymentGateway().authorizeCapture(payment);
                paymentResult = payment.getPaymentProcessorResult();
            } catch (Throwable th) {
                paymentResult = Payment.PAYMENT_STATUS_FAILED;
                payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                payment.setTransactionOperationResultMessage(th.getMessage());

            } finally {
                final CustomerOrderPayment customerOrderPayment = new CustomerOrderPaymentEntity();
                //customerOrderPaymentService.getGenericDao().getEntityFactory().getByIface(CustomerOrderPayment.class);
                BeanUtils.copyProperties(payment, customerOrderPayment); //from PG object to persisted
                customerOrderPayment.setPaymentProcessorResult(paymentResult);
                customerOrderPaymentService.create(customerOrderPayment);
            }

        }

        return paymentResult;


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

            for (Payment payment : paymentsToAuthorize) {
                String paymentResult = null;
                try {
                    payment = getPaymentGateway().authorize(payment);
                    paymentResult = payment.getPaymentProcessorResult();
                } catch (Throwable th) {
                    paymentResult = Payment.PAYMENT_STATUS_FAILED;
                    payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                    payment.setTransactionOperationResultMessage(th.getMessage());
                } finally {
                    final CustomerOrderPayment customerOrderPayment = new CustomerOrderPaymentEntity();
                    //customerOrderPaymentService.getGenericDao().getEntityFactory().getByIface(CustomerOrderPayment.class);
                    BeanUtils.copyProperties(payment, customerOrderPayment); //from PG object to persisted
                    customerOrderPayment.setPaymentProcessorResult(paymentResult);
                    customerOrderPaymentService.create(customerOrderPayment);
                    if (Payment.PAYMENT_STATUS_FAILED.equals(paymentResult)) {
                        reverseAuthorizatios(order.getOrdernum());
                        return Payment.PAYMENT_STATUS_FAILED;
                    }
                }
            }
            return Payment.PAYMENT_STATUS_OK;
        } else if (getPaymentGateway().getPaymentGatewayFeatures().isSupportAuthorizeCapture()) {
            return authorizeCapture(order, params);
        }
        throw new RuntimeException(
                MessageFormat.format(
                        "Payment gateway {0}  must suports authorize and/ authorize capture operations",
                        getPaymentGateway().getLabel()
                )
        );
    }


    /**
     * Check is reverse auth operation available for given auth op.
     * Auth can be reversed in case if op has not other syccessful operation.
     *
     * @param authToReverce order payment to check
     * @param checkRevAuth  set set of all operations with ok status.
     * @return true if revrse operation can be performed.
     */
    boolean canPerformReverseAuth(final CustomerOrderPayment authToReverce, final List<CustomerOrderPayment> checkRevAuth) {
        final String shipmentNo = authToReverce.getOrderShipment();
        for (CustomerOrderPayment paymentOp : checkRevAuth) {
            if (shipmentNo.equals(paymentOp.getOrderShipment())) {
                if (!PaymentGateway.AUTH.equals(paymentOp.getTransactionOperation())) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Reverse authorized payments. This can be when one of the payments from whole set is failed.
     * Revese authorization will applyed to authorazed payments only
     *
     * @param orderNum order with some authorized payments
     */
    public void reverseAuthorizatios(final String orderNum) {
        if (getPaymentGateway().getPaymentGatewayFeatures().isSupportReverseAuthorization()) {
            final List<CustomerOrderPayment> paymentsToRevAuth = customerOrderPaymentService.findBy(
                    orderNum,
                    null,
                    Payment.PAYMENT_STATUS_OK,
                    PaymentGateway.AUTH
            );

            final List<CustomerOrderPayment> checkRevAuth = customerOrderPaymentService.findBy(
                    orderNum,
                    null,
                    Payment.PAYMENT_STATUS_OK,
                    null
            );

            for (CustomerOrderPayment customerOrderPayment : paymentsToRevAuth) {
                if (canPerformReverseAuth(customerOrderPayment, checkRevAuth)) {
                    Payment payment = new PaymentImpl();
                    BeanUtils.copyProperties(customerOrderPayment, payment); //from persisted to PG object

                    String paymentResult = null;
                    try {
                        payment = getPaymentGateway().reverseAuthorization(payment); //pass "original" to perform reverse autghorization.
                        paymentResult = payment.getPaymentProcessorResult();
                    } catch (Throwable th) {
                        paymentResult = Payment.PAYMENT_STATUS_FAILED;
                        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                        payment.setTransactionOperationResultMessage(th.getMessage());

                    } finally {
                        final CustomerOrderPayment authReversedOrderPayment = new CustomerOrderPaymentEntity();
                        //customerOrderPaymentService.getGenericDao().getEntityFactory().getByIface(CustomerOrderPayment.class);
                        BeanUtils.copyProperties(payment, authReversedOrderPayment); //from PG object to persisted
                        authReversedOrderPayment.setPaymentProcessorResult(paymentResult);
                        customerOrderPaymentService.create(authReversedOrderPayment);
                    }

                }
            }
        }
    }

    /**
     * Particular shimment is complete. Funds can be captured.
     * In case of multiple delivery and single payment, capture on last delivery.
     *
     * @param order               order
     * @param orderShipmentNumber internal shipment number.
     *                            Each order has at least one delivery.
     * @return status of operation.
     */
    public String shipmentComplete(final CustomerOrder order, final String orderShipmentNumber) {

        final boolean isMultiplePaymentsSupports = getPaymentGateway().getPaymentGatewayFeatures().isSupportAuthorizePerShipment();
        final List<CustomerOrderPayment> paymentsToCapture = customerOrderPaymentService.findBy(
                order.getOrdernum(),
                isMultiplePaymentsSupports ? orderShipmentNumber : order.getOrdernum(),
                Payment.PAYMENT_STATUS_OK,
                PaymentGateway.AUTH
        );
        if (
                paymentsToCapture.size() > 1
                        ||
                        (paymentsToCapture.isEmpty() && !getPaymentGateway().getPaymentGatewayFeatures().isSupportAuthorize())) {
            ShopCodeContext.getLog(this).warn( //must be only one record
                    MessageFormat.format(
                            "Payment gateway {0} with features {1}. Found {2} records to capture, but expected 1 only. Order num {3} Shipment num {4}",
                            getPaymentGateway().getLabel(), getPaymentGateway().getPaymentGatewayFeatures(), paymentsToCapture.size(), order.getOrdernum(), orderShipmentNumber
                    )
            );
        }

        boolean wasError = false;
        String paymentResult = null;

        if (isMultiplePaymentsSupports || isLastShipmentComplete(order)) { //each completed delivery or last in case of single pay for several delivery
            for (CustomerOrderPayment paymentToCapture : paymentsToCapture) {
                Payment payment = new PaymentImpl();
                BeanUtils.copyProperties(paymentToCapture, payment); //from persisted to PG object
                payment.setTransactionOperation(PaymentGateway.CAPTURE);


                try {
                    payment = getPaymentGateway().capture(payment); //pass "original" to perform fund capture.
                    paymentResult = payment.getPaymentProcessorResult();
                } catch (Throwable th) {
                    paymentResult = Payment.PAYMENT_STATUS_FAILED;
                    payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_FAILED);
                    payment.setTransactionOperationResultMessage(th.getMessage());
                    ShopCodeContext.getLog(this).error("Cannot capture " + payment, th);

                } finally {
                    final CustomerOrderPayment authReversedOrderPayment = new CustomerOrderPaymentEntity();
                    //customerOrderPaymentService.getGenericDao().getEntityFactory().getByIface(CustomerOrderPayment.class);
                    BeanUtils.copyProperties(payment, authReversedOrderPayment); //from PG object to persisted
                    authReversedOrderPayment.setPaymentProcessorResult(paymentResult);
                    customerOrderPaymentService.create(authReversedOrderPayment);
                }
                if (!Payment.PAYMENT_STATUS_OK.equals(paymentResult)) {
                    wasError = true;
                }
            }
        }

        return wasError ? Payment.PAYMENT_STATUS_FAILED : Payment.PAYMENT_STATUS_OK;
    }


    /**
     * {@inheritDoc}
     */
    public String cancelOrder(final CustomerOrder order) {

        if (!CustomerOrder.ORDER_STATUS_CANCELLED.equals(order.getOrderStatus())) {

            boolean wasError = false;

            final List<CustomerOrderPayment> paymentsToRollBack = new ArrayList<CustomerOrderPayment>();

            paymentsToRollBack.addAll(
                    customerOrderPaymentService.findBy(order.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH_CAPTURE));
            paymentsToRollBack.addAll(
                    customerOrderPaymentService.findBy(order.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.CAPTURE));

            reverseAuthorizatios(order.getOrdernum());

            for (CustomerOrderPayment customerOrderPayment : paymentsToRollBack) {
                Payment payment = null;
                String paymentResult = null;
                try {
                    payment = new PaymentImpl();
                    BeanUtils.copyProperties(customerOrderPayment, payment); //from persisted to PG object
                    if (customerOrderPayment.isPaymentProcessorBatchSettlement()) {
                        // refund
                        payment.setTransactionOperation(PaymentGateway.REFUND);
                        payment = getPaymentGateway().refund(payment);
                        paymentResult = payment.getPaymentProcessorResult();
                    } else {
                        //void
                        payment.setTransactionOperation(PaymentGateway.VOID_CAPTURE);
                        payment = getPaymentGateway().voidCapture(payment);
                        paymentResult = payment.getPaymentProcessorResult();
                    }
                } catch (Throwable th) {
                    ShopCodeContext.getLog(this).error(
                            MessageFormat.format(
                                    "Can not perform roll back operation on payment record {0} payment {1}",
                                    customerOrderPayment.getCustomerOrderPaymentId(),
                                    payment
                            ), th
                    );
                    wasError = true;
                } finally {
                    final CustomerOrderPayment authReversedOrderPayment = new CustomerOrderPaymentEntity();
                    //customerOrderPaymentService.getGenericDao().getEntityFactory().getByIface(CustomerOrderPayment.class);
                    BeanUtils.copyProperties(payment, authReversedOrderPayment); //from PG object to persisted
                    authReversedOrderPayment.setPaymentProcessorResult(paymentResult);
                    customerOrderPaymentService.create(authReversedOrderPayment);
                }
                if (!Payment.PAYMENT_STATUS_OK.equals(paymentResult)) {
                    wasError = true;
                }


            }

            return wasError ? Payment.PAYMENT_STATUS_FAILED : Payment.PAYMENT_STATUS_OK;
        }
        ShopCodeContext.getLog(this).warn("Can not payment cancelation on canceled order  {}",
                order.getOrdernum()
        );
        return Payment.PAYMENT_STATUS_FAILED;
    }


    /**
     * Create list of payment to authorize.
     *
     * @param order                order
     * @param transactionOperation operation in term of payment processor
     * @param forceSinglePaymentIn flag is true for authCapture operation, when paymeng gateway not supports several payments per
     *                             order
     * @return list of  payments with details
     */
    public List<Payment> createPaymentsToAuthorize(
            final CustomerOrder order,
            final boolean forceSinglePaymentIn,
            final Map params,
            final String transactionOperation) {

        Assert.notNull(order, "Customer order expected");

        final boolean forceSinglePayment = forceSinglePaymentIn || params.containsKey("forceSinglePayment");

        final Payment templatePayment = fillPaymentPrototype(
                order,
                getPaymentGateway().createPaymentPrototype(params),
                transactionOperation,
                getPaymentGateway().getLabel());

        final List<Payment> rez = new ArrayList<Payment>();
        if (forceSinglePayment || !getPaymentGateway().getPaymentGatewayFeatures().isSupportAuthorizePerShipment()) {
            //TODO: YC-145 remove single payment per multiple delivery
            Payment payment = (Payment) SerializationUtils.clone(templatePayment);
            for (CustomerOrderDelivery delivery : order.getDelivery()) {
                fillPayment(order, delivery, payment, true);
            }
            rez.add(payment);

        } else {
            for (CustomerOrderDelivery delivery : order.getDelivery()) {
                Payment payment = (Payment) SerializationUtils.clone(templatePayment);
                fillPayment(order, delivery, payment, false);
                rez.add(payment);
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
     */
    private void fillPayment(final CustomerOrder order, final CustomerOrderDelivery delivery, final Payment payment, final boolean singlePay) {

        if (payment.getTransactionReferenceId() == null) {
            // can be set by external payment gateway
            payment.setTransactionReferenceId(delivery.getDeliveryNum());
        }


        payment.setOrderShipment(singlePay ? order.getOrdernum() : delivery.getDeliveryNum());

        fillPaymentItems(delivery, payment);
        fillPaymentShipment(delivery, payment);
        fillPaymentAmount(order, delivery, payment);
    }

    /**
     * Calculate delivery amount according to shipment sla cost and items in patricular delivery.
     *
     * @param order    order
     * @param delivery delivery
     * @param payment  payment
     */
    private void fillPaymentAmount(final CustomerOrder order,
                                   final CustomerOrderDelivery delivery,
                                   final Payment payment) {
        //TODO: YC-145 more sophisticated, include discount and free shipping per one and multiple delivery
        //TODO: YC-145 need calculate at shopping cart !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        BigDecimal rez = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);
        for (PaymentLine paymentLine : payment.getOrderItems()) {
            rez = rez.add(paymentLine.getQuantity().multiply(paymentLine.getUnitPrice()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
        }
        payment.setPaymentAmount(rez);
        payment.setOrderCurrency(order.getCurrency());
    }

    private void fillPaymentShipment(final CustomerOrderDelivery delivery, final Payment payment) {
        payment.getOrderItems().add(
                new PaymentLineImpl(
                        delivery.getCarrierSla() == null ? "N/A" : String.valueOf(delivery.getCarrierSla().getCarrierslaId()),
                        delivery.getCarrierSla() == null ? "No SLA" : delivery.getCarrierSla().getName(),
                        BigDecimal.ONE,
                        delivery.getPrice(),
                        BigDecimal.ZERO,
                        true
                )
        );
    }

    private void fillPaymentItems(final CustomerOrderDelivery delivery, final Payment payment) {
        for (CustomerOrderDeliveryDet deliveryDet : delivery.getDetail()) {
            payment.getOrderItems().add(
                    new PaymentLineImpl(
                            deliveryDet.getSku().getCode(),
                            deliveryDet.getSku().getName(),  // TODO: localise whilst YC-67
                            deliveryDet.getQty(),
                            deliveryDet.getPrice(),
                            BigDecimal.ZERO
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

        final Customer customer = order.getCustomer();

        if (customer != null) {

            Address shippingAddr = customer.getDefaultAddress(Address.ADDR_TYPE_SHIPING);
            Address billingAddr = customer.getDefaultAddress(Address.ADDR_TYPE_BILLING);

            if (billingAddr == null) {
                billingAddr = shippingAddr;
            }

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

            templatePayment.setBillingEmail(customer.getEmail());

        }


        templatePayment.setOrderDate(order.getOrderTimestamp());
        templatePayment.setOrderCurrency(order.getCurrency());
        templatePayment.setOrderNumber(order.getOrdernum());

        templatePayment.setTransactionOperation(transactionOperation);
        templatePayment.setTransactionGatewayLabel(transactionGatewayLabel);

        return templatePayment;
    }

    /**
     * Is all shipments were compilted and given the last one, that completed.
     *
     * @param order order
     * @return true in case if all shipments,
     */

    boolean isLastShipmentComplete(final CustomerOrder order) {
        for (CustomerOrderDelivery delivery : order.getDelivery()) {
            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED.equals(delivery.getDeliveryStatus())) {
                return false;
            }
        }
        return true;
    }

}
