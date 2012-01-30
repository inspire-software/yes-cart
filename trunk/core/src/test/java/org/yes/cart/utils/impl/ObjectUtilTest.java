package org.yes.cart.utils.impl;

import org.junit.Test;
import org.yes.cart.domain.misc.Pair;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 *
 * User: iazarny@yahoo.com
 * Date: 29-Jan-2012
 * Time: 12:24 PM
 */
public class ObjectUtilTest {

    @Test (expected = IllegalArgumentException.class)
    public void testToObjectArrayFailed() throws Exception {
        ObjectUtil.toObjectArray(null);
    }

    @Test
    public void testToObjectArrayOk() throws Exception {
        Object [] objarr = ObjectUtil.toObjectArray(new Pair<String, Integer>("first", 10));
        assertEquals("first", objarr[1]);
        assertEquals(10, objarr[2]);
    }


}
