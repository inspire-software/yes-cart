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

package org.yes.cart.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 24/08/2014
 * Time: 12:51
 */
public class DomainApiUtilTest {

    @Test
    public void testIsObjectAvailableNow() throws Exception {

        final Date now = new Date();

        assertFalse(DomainApiUtil.isObjectAvailableNow(false, null, null, now));
        assertTrue(DomainApiUtil.isObjectAvailableNow(true, null, null, now));

        assertFalse(DomainApiUtil.isObjectAvailableNow(true, new Date(now.getTime() + 1L), null, now));
        assertTrue(DomainApiUtil.isObjectAvailableNow(true, new Date(now.getTime() - 1L), null, now));

        assertFalse(DomainApiUtil.isObjectAvailableNow(true, null, new Date(now.getTime() - 1L), now));
        assertTrue(DomainApiUtil.isObjectAvailableNow(true, null, new Date(now.getTime() + 1L), now));

        assertTrue(DomainApiUtil.isObjectAvailableNow(true, new Date(now.getTime() - 1L), new Date(now.getTime() + 1L), now));

    }

}
