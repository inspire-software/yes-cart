package org.yes.cart.service.domain.impl;

import org.junit.Test;
import org.yes.cart.service.domain.HashHelper;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class Test_MD5HashHelperImpl {

    @Test
    public void testGetMD5Hash() throws Exception {
        HashHelper hashHelper = new MD5HashHelperImpl();
        assertEquals("Get unexpected hash",
                "5f4dcc3b5aa765d61d8327deb882cf99",
                hashHelper.getHash("password"));
    }
}
