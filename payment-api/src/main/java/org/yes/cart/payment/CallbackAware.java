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
