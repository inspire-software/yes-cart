/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
