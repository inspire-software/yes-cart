package org.yes.cart.service.domain;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface HashHelper {

    /**
     * Get the string from given password.
     *
     * @param password given password
     * @return md5 password string
     * @throws java.security.NoSuchAlgorithmException
     *          NoSuchAlgorithmException
     * @throws java.io.UnsupportedEncodingException
     *          UnsupportedEncodingException
     */
    String getHash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException;


}
