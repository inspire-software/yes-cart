package org.yes.cart.utils.impl;

import org.junit.Test;
import org.yes.cart.domain.misc.Pair;

import java.util.Date;

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
