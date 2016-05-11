/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.util;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

/**
 * Wrapper for Base64. Since we support JDK7, have to use commons.
 * TODO: change to JDK8 Base64
 *
 * User: denispavlov
 * Date: 11/05/2016
 * Time: 08:38
 */
public class Base64Utils {

    /**
     * Encode string to base64 string
     *
     * @param input input
     *
     * @return base64 (URL non-safe)
     */
    public static String encode(String input) {

        return Base64.encodeBase64String(input.getBytes(StandardCharsets.UTF_8));

    }

    /**
     * Decode base64 string to its original.
     *
     * @param encoded encoded string
     *
     * @return original
     */
    public static String decode(String encoded) {

        return new String(Base64.decodeBase64(encoded));

    }

}
