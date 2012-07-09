package org.yes.cart.payment.persistence.entity.impl;

import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;

/**
 * Payment gateway descriptor.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:33:53
 */
public class PaymentGatewayDescriptorImpl extends DescriptorImpl implements PaymentGatewayDescriptor {

    private static final long serialVersionUID = 20100714L;

    private String url;
    private String login;
    private String password;
    private int priority;


    /**
     * Construct payment gateway descriptior.
     *
     * @param name        name
     * @param description description
     * @param label       label
     * @param url         url
     */
    public PaymentGatewayDescriptorImpl(final String name,
                                        final String description,
                                        final String label, final String url) {
        super(name, description, label);
        this.url = url;
    }


    /**
     * {@inheritDoc}
     */
    public int getPriority() {
        return priority;
    }

    /**
     * {@inheritDoc}
     */
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    /**
     * Defaiult constructor.
     */
    public PaymentGatewayDescriptorImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set url.
     *
     * @param url url
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * {@inheritDoc}
     */
    public String getLogin() {
        return login;
    }

    /**
     * {@inheritDoc}
     */
    public void setLogin(final String login) {
        this.login = login;
    }

    /**
     * {@inheritDoc}
     */
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     */
    public void setPassword(final String password) {
        this.password = password;
    }
}
