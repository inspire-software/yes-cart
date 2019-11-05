package org.yes.cart.payment.dto.impl;

import org.yes.cart.payment.CallbackAware;

import java.math.BigDecimal;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 15/12/2017
 * Time: 17:32
 */
public class BasicCallbackInfoImpl implements CallbackAware.Callback {

    private final String orderGuid;
    private final CallbackAware.CallbackOperation operation;
    private final BigDecimal amount;
    private final Map parameters;
    private final boolean validated;

    public BasicCallbackInfoImpl(final String orderGuid,
                                 final CallbackAware.CallbackOperation operation,
                                 final BigDecimal amount,
                                 final Map parameters,
                                 final boolean validated) {
        this.orderGuid = orderGuid;
        this.operation = operation;
        this.amount = amount;
        this.parameters = parameters;
        this.validated = validated;
    }

    /** {@inheritDoc} */
    @Override
    public String getOrderGuid() {
        return orderGuid;
    }

    /** {@inheritDoc} */
    @Override
    public CallbackAware.CallbackOperation getOperation() {
        return operation;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    /** {@inheritDoc} */
    @Override
    public Map getParameters() {
        return parameters;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValidated() {
        return validated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BasicCallbackInfoImpl{" +
                "orderGuid='" + orderGuid + '\'' +
                ", operation=" + operation +
                ", amount=" + amount +
                ", validated=" + validated +
                '}';
    }
}
