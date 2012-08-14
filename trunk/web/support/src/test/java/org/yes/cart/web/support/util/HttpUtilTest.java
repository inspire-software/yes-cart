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

package org.yes.cart.web.support.util;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * Test {@link HttpUtilTest}
 */
public class HttpUtilTest {

    @Test
    public void testDumpRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/main.app");
        request.addParameter("choice", "expanded");
        request.addParameter("contextMenu", "left");

        request.addHeader("header", "headerValue");

        request.setCookies(new Cookie("cookie", "yankee dudle"));

        final String value = HttpUtil.dumpRequest(request);

        assertTrue(value.contains("Parameter choice=expanded"));
        assertTrue(value.contains("Parameter contextMenu=left"));
        assertTrue(value.contains("Header header=headerValue"));
        assertTrue(value.contains("Cookie cookie=yankee dudle"));

        assertEquals("#dumpRequest request is null", HttpUtil.dumpRequest(null));

    }

    @Test
    public void testGetSingleValue() throws Exception {

        assertEquals("a", HttpUtil.getSingleValue("a"));
        assertEquals("a", HttpUtil.getSingleValue(new String [] {"a", "b", "c"} ));
        assertEquals("a", HttpUtil.getSingleValue(new ArrayList<String>() {{ add("a"); add("b"); add("c"); }} ));
        assertNull(HttpUtil.getSingleValue(new String [0] ));

    }
}
