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

package org.yes.cart.utils.impl;

import org.junit.Test;
import org.yes.cart.domain.misc.Pair;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 *
 * User: iazarny@yahoo.com
 * Date: 29-Jan-2012
 * Time: 12:24 PM
 */
public class ObjectUtilTest {

    @Test
    public void testToObjectArrayOk() throws Exception {

        assertNotNull(ObjectUtil.toObjectArray(null));


        Object [] objarr = ObjectUtil.toObjectArray(new Pair<String, Integer>("first", 10));
        assertEquals("first", objarr[1]);
        assertEquals(10, objarr[2]);

        assertEquals(1, ObjectUtil.toObjectArray(12).length);
        assertEquals("12" , ObjectUtil.toObjectArray(12)[0]);
    }


}
