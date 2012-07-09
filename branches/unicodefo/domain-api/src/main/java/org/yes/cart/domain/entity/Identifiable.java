package org.yes.cart.domain.entity;

import java.io.Serializable;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 28-May-2011
 * Time: 11:12:54
 */
public interface Identifiable extends Serializable {

    /**
     * Get pk value.
     *
     * @return pk value.
     */
    long getId();


}
