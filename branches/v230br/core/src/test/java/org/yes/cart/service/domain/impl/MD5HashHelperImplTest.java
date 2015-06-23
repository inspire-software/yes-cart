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

package org.yes.cart.service.domain.impl;

import org.junit.Test;
import org.yes.cart.service.domain.HashHelper;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class MD5HashHelperImplTest {

    @Test
    public void testGetMD5Hash() throws Exception {
        HashHelper hashHelper = new MD5HashHelperImpl();

        assertEquals("Get unexpected hash",
                "5f4dcc3b5aa765d61d8327deb882cf99",
                hashHelper.getHash("password"));

        assertEquals("Get unexpected hash",
                "fcea920f7412b5da7be0cf42b8c93759",
                hashHelper.getHash("1234567"));
    }

    @Test
    public void testGetMD5HashWithSalt() throws Exception {
        MD5HashHelperImpl hashHelper = new MD5HashHelperImpl();
        hashHelper.setSalt("YCPWSALT");

        assertEquals("Get unexpected hash",
                "f71b3d55bd4b4a0b3f18f65349750c21",
                hashHelper.getHash("password"));

        assertEquals("Get unexpected hash",
                "d89c77010dedf89c10d1293bd02b53c7",
                hashHelper.getHash("1234567"));
    }
}
