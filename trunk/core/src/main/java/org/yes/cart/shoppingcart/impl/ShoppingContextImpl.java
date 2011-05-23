package org.yes.cart.shoppingcart.impl;

import org.yes.cart.domain.dto.ShoppingContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 *
 * Shopping context implementation.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 12-May-2011
 * Time: 10:37:13
 */
public class ShoppingContextImpl implements ShoppingContext {

    private static final long serialVersionUID =  20110509L;

    private SecurityContext securityContext;
    private String latestViewedSkus;
    private String latestViewedCategories;
    private String customerName;

    /** {@inheritDoc} */
    public void clearContext() {
        latestViewedSkus = null;
        latestViewedCategories = null;
        securityContext = null;
    }

    /** {@inheritDoc} */
    public SecurityContext getSecurityContext() {
        if (securityContext == null) {
            securityContext = new SecurityContextImpl();
        }
        return securityContext;
    }

    /** {@inheritDoc} */
    public void setSecurityContext(final SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    /** {@inheritDoc} */
    public String getLatestViewedSkus() {
        return latestViewedSkus;
    }

    /** {@inheritDoc} */
    public void setLatestViewedSkus(final String latestViewedSkus) {
        this.latestViewedSkus = latestViewedSkus;
    }

    /** {@inheritDoc} */
    public String getLatestViewedCategories() {
        return latestViewedCategories;
    }

    /** {@inheritDoc} */
    public void setLatestViewedCategories(final String latestViewedCategories) {
        this.latestViewedCategories = latestViewedCategories;
    }

    /**
     * {@inheritDoc}
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * {@inheritDoc}
     */
    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }
    
}
