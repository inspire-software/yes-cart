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

    public BasicCallbackInfoImpl(final String orderGuid,
                                 final CallbackAware.CallbackOperation operation,
                                 final BigDecimal amount,
                                 final Map parameters) {
        this.orderGuid = orderGuid;
        this.operation = operation;
        this.amount = amount;
        this.parameters = parameters;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BasicCallbackInfoImpl{" +
                "orderGuid='" + orderGuid + '\'' +
                ", operation=" + operation +
                ", amount=" + amount +
                '}';
    }
}
