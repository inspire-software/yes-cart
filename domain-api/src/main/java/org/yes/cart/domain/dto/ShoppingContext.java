package org.yes.cart.domain.dto;

import org.springframework.security.core.context.SecurityContext;

import java.io.Serializable;

/**
 *
 * Responsible to hold shopping context data like viewved products and categories, security context, geo data and
 * other stuff
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 12-May-2011
 * Time: 10:31:36
 */
public interface ShoppingContext extends Serializable {


    /**
     * Get {@link SecurityContext}.
     * @return Security Context
     */
    SecurityContext getSecurityContext();

    /**
     * Set securityContext
     * @param securityContext context to use.
     */
    void setSecurityContext(SecurityContext securityContext);

    /**
     * Get lastest viewed sku codes.
     *
     * @return comma separated string of viewed skus.
     */
    String getLatestViewedSkus();

    /**
     * Set latest viewed sku codes.
     *
     * @param latestViewedSkus latest viewed skus.
     */
    void setLatestViewedSkus(String latestViewedSkus);


    /**
     * Get lastest viewed categories.
     *
     * @return comma separated string of category ids.
     */
    String getLatestViewedCategories();

    /**
     * Get lastest viewed categories.
     * @param latestViewedCategories comma separated list of category ids.
     */
    void setLatestViewedCategories(String latestViewedCategories);

    /**
     * Get customer name.
     *
     * @return customer name or null if customer is anonymous
     */
    String getCustomerName();

    /**
     * Set customer name.
     *
     * @param customerName customer name.
     */
    void setCustomerName(String customerName);



    /**
     * Clear context.
     */
    void clearContext();



}
