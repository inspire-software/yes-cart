package org.yes.cart.service.domain.impl;

import junit.framework.TestCase;
import org.yes.cart.service.domain.HashHelper;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class MD5HashHelperImplTest extends TestCase {

    /**
     * Test custom  md5 function.
     */
    public void testGetMD5Hash() {
        HashHelper hashHelper = new MD5HashHelperImpl();
        try {
            assertEquals("Get unexpected hash",
                    "5f4dcc3b5aa765d61d8327deb882cf99",
                    hashHelper.getHash("password"));
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

}
