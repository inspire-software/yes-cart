package org.yes.cart.payment;

import org.yes.cart.payment.dto.Payment;

import java.math.BigDecimal;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 15/12/2017
 * Time: 17:21
 */
public interface CallbackAware {

    String CALLBACK_PARAM = "__CALLBACK__";

    /**
     * Restore order number by given parameters
     *
     * @param privateCallBackParameters request parameters
     * @return restore order number
     */
    Callback convertToCallback(Map privateCallBackParameters);

    /**
     * Check the result for success attributes.
     *
     * @param callbackResult  call result
     * @return  true in case of success
     */
    CallbackResult getExternalCallbackResult(Map<String, String> callbackResult);

    /**
     * Pre process payment operation. This is a hook to inject additional data using
     * callback object before the operation has been performed.
     *
     * Example use case:
     * if PG does not support refunds but does support callbacks and receives a refund
     * notification it is possible to pre-setup values in Payment object so that we do
     * not end up with {@link Payment#PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED}
     *
     * @param payment payment object
     * @param callback callback received
     * @param processorOperation currently attempted operation
     */
    void preProcess(Payment payment, Callback callback, String processorOperation);


    /**
     * Post process payment operation. This is a hook to inject additional data using
     * callback object after the operation has been performed.
     *
     * Example use case:
     * if PG does not support refunds but does support callbacks and receives a refund
     * notification it is possible to post-setup values in Payment object so that we do
     * not end up with {@link Payment#PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED}
     *
     * @param payment payment object
     * @param callback callback received
     * @param processorOperation currently attempted operation
     */
    void postProcess(Payment payment, Callback callback, String processorOperation);

    /**
     * Basic callback object
     */
    interface Callback {

        /**
         * Original order guid.
         *
         * @return guid (or null)
         */
        String getOrderGuid();

        /**
         * Callback operation.
         *
         * @return operation
         */
        CallbackOperation getOperation();

        /**
         * Amount of this operation.
         *
         * @return amount (or null if full)
         */
        BigDecimal getAmount();

        /**
         * Original parameters.
         *
         * @return parameters
         */
        Map getParameters();

    }

    /**
     * Operation hint
     */
    enum CallbackOperation {

        PAYMENT, // Payment callback (default flow)
        REFUND,  // Refund notification (could be initiated from external system)
        INFO,    // Any other operation (generally this operation is marker for ignoring this callback)
        INVALID, // Bad operation (e.g. not valid signature or malformed)

    }

    /**
     * Callback result
     */
    enum CallbackResult {

        OK(Payment.PAYMENT_STATUS_OK, true),
        UNSETTLED(Payment.PAYMENT_STATUS_OK, false),
        PROCESSING(Payment.PAYMENT_STATUS_PROCESSING, false),
        FAILED(Payment.PAYMENT_STATUS_FAILED, false),
        MANUAL_REQUIRED(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED, false);

        private String status;
        private boolean settled;

        CallbackResult(final String status, final boolean settled) {
            this.status = status;
            this.settled = settled;
        }

        /**
         * @return payment result (see {@link Payment})
         */
        public String getStatus() {
            return status;
        }

        /**
         * @return AUTH_CAPTURE and CAPTURE operations only (denotes if funds were captured)
         */
        public boolean isSettled() {
            return settled;
        }
    }
}
