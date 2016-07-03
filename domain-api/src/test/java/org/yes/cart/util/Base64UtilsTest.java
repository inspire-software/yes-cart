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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 11/05/2016
 * Time: 09:00
 */
public class Base64UtilsTest {

    @Test
    public void testEncodeDecode() throws Exception {

        final String original = "Java 8 now supports BASE64 Encoding and Decoding. Why are we even writing this? Ah, Java 7";

        final String encoded = Base64Utils.encode(original);

        assertNotNull(encoded);
        assertFalse(encoded.equals(original));

        final String decoded = Base64Utils.decode(encoded);

        assertNotNull(decoded);
        assertEquals(decoded, original);

    }

}