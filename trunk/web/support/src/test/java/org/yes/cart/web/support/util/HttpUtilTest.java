package org.yes.cart.web.support.util;

import org.apache.commons.lang.StringUtils;
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
