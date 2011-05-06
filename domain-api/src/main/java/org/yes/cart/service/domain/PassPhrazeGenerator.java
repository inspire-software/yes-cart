package org.yes.cart.service.domain;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface PassPhrazeGenerator {
    /**
     * Generate a random pass phraze string with a random password.
     * @return generated pass phraze
     */
    String getNextPassPhrase();
}
