package org.yes.cart.payment.persistence.entity;


import java.io.Serializable;

/**
 * Payment gateway configuratin parameters like merchantid, url, supported currencies, is it enabled etc.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface PaymentGatewayParameter extends Serializable, Descriptor {

    /**
     * @return pk value
     */
    long getPaymentGatewayParameterId();

    /**
     * @param paymentGatewayParameterId pk value
     */
    void setPaymentGatewayParameterId(long paymentGatewayParameterId);


    /**
     * Get value.
     *
     * @return value
     */
    String getValue();

    /**
     * Set value.
     *
     * @param value value to set.
     */
    void setValue(String value);

    /**
     * GEt payment gateway label.
     *
     * @return get pg label.
     */
    String getPgLabel();


    /**
     * Set pg label.
     *
     * @param pgLabel label.
     */
    void setPgLabel(String pgLabel);

}
