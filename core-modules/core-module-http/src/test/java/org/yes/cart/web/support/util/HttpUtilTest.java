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

package org.yes.cart.web.support.util;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;
import java.util.*;

import static org.junit.Assert.*;

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

        assertTrue(value.contains("choice=expanded"));
        assertTrue(value.contains("contextMenu=left"));
        assertTrue(value.contains("header=headerValue"));
        assertTrue(value.contains("cookie=yankee dudle"));

        assertEquals("#dumpRequest request is null", HttpUtil.dumpRequest(null));

    }

    @Test
    public void testGetParameters() throws Exception {

        String request = "/category/abc/sku/xyz?a=a1&a=a2&b=b1";

        Map<String, List<String>> parameters;
        Iterator<Map.Entry<String, List<String>>> parametersIt;
        Map.Entry<String, List<String>> p1, p2, p3, p4;

        parameters = HttpUtil.getParameters(request, Collections.emptySet());

        assertEquals(2, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("a", p1.getKey());
        assertEquals(2, p1.getValue().size());
        assertEquals("a1", p1.getValue().get(0));
        assertEquals("a2", p1.getValue().get(1));

        p2 = parametersIt.next();
        assertEquals("b", p2.getKey());
        assertEquals(1, p2.getValue().size());
        assertEquals("b1", p2.getValue().get(0));

        parameters = HttpUtil.getParameters(request, new HashSet<>(Collections.singletonList("category")));

        assertEquals(3, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(1, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));

        p2 = parametersIt.next();
        assertEquals("a", p2.getKey());
        assertEquals(2, p2.getValue().size());
        assertEquals("a1", p2.getValue().get(0));
        assertEquals("a2", p2.getValue().get(1));

        p3 = parametersIt.next();
        assertEquals("b", p3.getKey());
        assertEquals(1, p3.getValue().size());
        assertEquals("b1", p3.getValue().get(0));

        parameters = HttpUtil.getParameters(request, new HashSet<>(Arrays.asList("sku", "category")));

        assertEquals(4, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(1, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));

        p2 = parametersIt.next();
        assertEquals("sku", p2.getKey());
        assertEquals(1, p2.getValue().size());
        assertEquals("xyz", p2.getValue().get(0));

        p3 = parametersIt.next();
        assertEquals("a", p3.getKey());
        assertEquals(2, p3.getValue().size());
        assertEquals("a1", p3.getValue().get(0));
        assertEquals("a2", p3.getValue().get(1));

        p4 = parametersIt.next();
        assertEquals("b", p4.getKey());
        assertEquals(1, p4.getValue().size());
        assertEquals("b1", p4.getValue().get(0));

    }

    @Test
    public void testGetParametersWithDuplicatesPreserve() throws Exception {

        String request = "/category/abc/sku/xyz/category/abc/sku/xyz?a=a1&a=a2&b=b1&a=a1&a=a2&b=b1";

        Map<String, List<String>> parameters;
        Iterator<Map.Entry<String, List<String>>> parametersIt;
        Map.Entry<String, List<String>> p1, p2, p3, p4;

        parameters = HttpUtil.getParameters(request, Collections.emptySet());

        assertEquals(2, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("a", p1.getKey());
        assertEquals(4, p1.getValue().size());
        assertEquals("a1", p1.getValue().get(0));
        assertEquals("a2", p1.getValue().get(1));
        assertEquals("a1", p1.getValue().get(2));
        assertEquals("a2", p1.getValue().get(3));

        p2 = parametersIt.next();
        assertEquals("b", p2.getKey());
        assertEquals(2, p2.getValue().size());
        assertEquals("b1", p2.getValue().get(0));
        assertEquals("b1", p2.getValue().get(1));

        parameters = HttpUtil.getParameters(request, new HashSet<>(Collections.singletonList("category")));

        assertEquals(3, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(2, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));
        assertEquals("abc", p1.getValue().get(1));

        p2 = parametersIt.next();
        assertEquals("a", p2.getKey());
        assertEquals(4, p2.getValue().size());
        assertEquals("a1", p2.getValue().get(0));
        assertEquals("a2", p2.getValue().get(1));
        assertEquals("a1", p2.getValue().get(2));
        assertEquals("a2", p2.getValue().get(3));

        p3 = parametersIt.next();
        assertEquals("b", p3.getKey());
        assertEquals(2, p3.getValue().size());
        assertEquals("b1", p3.getValue().get(0));
        assertEquals("b1", p3.getValue().get(1));

        parameters = HttpUtil.getParameters(request, new HashSet<>(Arrays.asList("sku", "category")));

        assertEquals(4, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(2, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));
        assertEquals("abc", p1.getValue().get(1));

        p2 = parametersIt.next();
        assertEquals("sku", p2.getKey());
        assertEquals(2, p2.getValue().size());
        assertEquals("xyz", p2.getValue().get(0));
        assertEquals("xyz", p2.getValue().get(1));

        p3 = parametersIt.next();
        assertEquals("a", p3.getKey());
        assertEquals(4, p3.getValue().size());
        assertEquals("a1", p3.getValue().get(0));
        assertEquals("a2", p3.getValue().get(1));
        assertEquals("a1", p3.getValue().get(2));
        assertEquals("a2", p3.getValue().get(3));

        p4 = parametersIt.next();
        assertEquals("b", p4.getKey());
        assertEquals(2, p4.getValue().size());
        assertEquals("b1", p4.getValue().get(0));
        assertEquals("b1", p4.getValue().get(1));

    }


    @Test
    public void testGetParametersWithDuplicatesRemove() throws Exception {

        String request = "/category/abc/sku/xyz/category/abc/sku/xyz?a=a1&a=a2&b=b1&a=a1&a=a2&b=b1";

        Map<String, List<String>> parameters;
        Iterator<Map.Entry<String, List<String>>> parametersIt;
        Map.Entry<String, List<String>> p1, p2, p3, p4;

        parameters = HttpUtil.getParameters(request, Collections.emptySet(), true);

        assertEquals(2, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("a", p1.getKey());
        assertEquals(2, p1.getValue().size());
        assertEquals("a1", p1.getValue().get(0));
        assertEquals("a2", p1.getValue().get(1));

        p2 = parametersIt.next();
        assertEquals("b", p2.getKey());
        assertEquals(1, p2.getValue().size());
        assertEquals("b1", p2.getValue().get(0));

        parameters = HttpUtil.getParameters(request, new HashSet<>(Collections.singletonList("category")), true);

        assertEquals(3, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(1, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));

        p2 = parametersIt.next();
        assertEquals("a", p2.getKey());
        assertEquals(2, p2.getValue().size());
        assertEquals("a1", p2.getValue().get(0));
        assertEquals("a2", p2.getValue().get(1));

        p3 = parametersIt.next();
        assertEquals("b", p3.getKey());
        assertEquals(1, p3.getValue().size());
        assertEquals("b1", p3.getValue().get(0));

        parameters = HttpUtil.getParameters(request, new HashSet<>(Arrays.asList("sku", "category")), true);

        assertEquals(4, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(1, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));

        p2 = parametersIt.next();
        assertEquals("sku", p2.getKey());
        assertEquals(1, p2.getValue().size());
        assertEquals("xyz", p2.getValue().get(0));

        p3 = parametersIt.next();
        assertEquals("a", p3.getKey());
        assertEquals(2, p3.getValue().size());
        assertEquals("a1", p3.getValue().get(0));
        assertEquals("a2", p3.getValue().get(1));

        p4 = parametersIt.next();
        assertEquals("b", p4.getKey());
        assertEquals(1, p4.getValue().size());
        assertEquals("b1", p4.getValue().get(0));

    }


    @Test
    public void testGetParameters2() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/category/abc/sku/xyz");
        request.setQueryString("a=a1&a=a2&b=b1");

        Map<String, List<String>> parameters;
        Iterator<Map.Entry<String, List<String>>> parametersIt;
        Map.Entry<String, List<String>> p1, p2, p3, p4;

        parameters = HttpUtil.getParameters(request, Collections.emptySet());

        assertEquals(2, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("a", p1.getKey());
        assertEquals(2, p1.getValue().size());
        assertEquals("a1", p1.getValue().get(0));
        assertEquals("a2", p1.getValue().get(1));

        p2 = parametersIt.next();
        assertEquals("b", p2.getKey());
        assertEquals(1, p2.getValue().size());
        assertEquals("b1", p2.getValue().get(0));

        parameters = HttpUtil.getParameters(request, new HashSet<>(Collections.singletonList("category")));

        assertEquals(3, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(1, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));

        p2 = parametersIt.next();
        assertEquals("a", p2.getKey());
        assertEquals(2, p2.getValue().size());
        assertEquals("a1", p2.getValue().get(0));
        assertEquals("a2", p2.getValue().get(1));

        p3 = parametersIt.next();
        assertEquals("b", p3.getKey());
        assertEquals(1, p3.getValue().size());
        assertEquals("b1", p3.getValue().get(0));

        parameters = HttpUtil.getParameters(request, new HashSet<>(Arrays.asList("sku", "category")));

        assertEquals(4, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(1, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));

        p2 = parametersIt.next();
        assertEquals("sku", p2.getKey());
        assertEquals(1, p2.getValue().size());
        assertEquals("xyz", p2.getValue().get(0));

        p3 = parametersIt.next();
        assertEquals("a", p3.getKey());
        assertEquals(2, p3.getValue().size());
        assertEquals("a1", p3.getValue().get(0));
        assertEquals("a2", p3.getValue().get(1));

        p4 = parametersIt.next();
        assertEquals("b", p4.getKey());
        assertEquals(1, p4.getValue().size());
        assertEquals("b1", p4.getValue().get(0));

    }

    @Test
    public void testGetParameters2WithDuplicatesPreserve() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/category/abc/sku/xyz/category/abc/sku/xyz");
        request.setQueryString("a=a1&a=a2&b=b1&a=a1&a=a2&b=b1");

        Map<String, List<String>> parameters;
        Iterator<Map.Entry<String, List<String>>> parametersIt;
        Map.Entry<String, List<String>> p1, p2, p3, p4;

        parameters = HttpUtil.getParameters(request, Collections.emptySet());

        assertEquals(2, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("a", p1.getKey());
        assertEquals(4, p1.getValue().size());
        assertEquals("a1", p1.getValue().get(0));
        assertEquals("a2", p1.getValue().get(1));
        assertEquals("a1", p1.getValue().get(2));
        assertEquals("a2", p1.getValue().get(3));

        p2 = parametersIt.next();
        assertEquals("b", p2.getKey());
        assertEquals(2, p2.getValue().size());
        assertEquals("b1", p2.getValue().get(0));
        assertEquals("b1", p2.getValue().get(1));

        parameters = HttpUtil.getParameters(request, new HashSet<>(Collections.singletonList("category")));

        assertEquals(3, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(2, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));
        assertEquals("abc", p1.getValue().get(1));

        p2 = parametersIt.next();
        assertEquals("a", p2.getKey());
        assertEquals(4, p2.getValue().size());
        assertEquals("a1", p2.getValue().get(0));
        assertEquals("a2", p2.getValue().get(1));
        assertEquals("a1", p2.getValue().get(2));
        assertEquals("a2", p2.getValue().get(3));

        p3 = parametersIt.next();
        assertEquals("b", p3.getKey());
        assertEquals(2, p3.getValue().size());
        assertEquals("b1", p3.getValue().get(0));
        assertEquals("b1", p3.getValue().get(1));

        parameters = HttpUtil.getParameters(request, new HashSet<>(Arrays.asList("sku", "category")));

        assertEquals(4, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(2, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));
        assertEquals("abc", p1.getValue().get(1));

        p2 = parametersIt.next();
        assertEquals("sku", p2.getKey());
        assertEquals(2, p2.getValue().size());
        assertEquals("xyz", p2.getValue().get(0));
        assertEquals("xyz", p2.getValue().get(1));

        p3 = parametersIt.next();
        assertEquals("a", p3.getKey());
        assertEquals(4, p3.getValue().size());
        assertEquals("a1", p3.getValue().get(0));
        assertEquals("a2", p3.getValue().get(1));
        assertEquals("a1", p3.getValue().get(2));
        assertEquals("a2", p3.getValue().get(3));

        p4 = parametersIt.next();
        assertEquals("b", p4.getKey());
        assertEquals(2, p4.getValue().size());
        assertEquals("b1", p4.getValue().get(0));
        assertEquals("b1", p4.getValue().get(1));

    }

    @Test
    public void testGetParameters2WithDuplicatesRemove() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/category/abc/sku/xyz/category/abc/sku/xyz");
        request.setQueryString("a=a1&a=a2&b=b1&a=a1&a=a2&b=b1");

        Map<String, List<String>> parameters;
        Iterator<Map.Entry<String, List<String>>> parametersIt;
        Map.Entry<String, List<String>> p1, p2, p3, p4;

        parameters = HttpUtil.getParameters(request, Collections.emptySet(), true);

        assertEquals(2, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("a", p1.getKey());
        assertEquals(2, p1.getValue().size());
        assertEquals("a1", p1.getValue().get(0));
        assertEquals("a2", p1.getValue().get(1));

        p2 = parametersIt.next();
        assertEquals("b", p2.getKey());
        assertEquals(1, p2.getValue().size());
        assertEquals("b1", p2.getValue().get(0));

        parameters = HttpUtil.getParameters(request, new HashSet<>(Collections.singletonList("category")), true);

        assertEquals(3, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(1, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));

        p2 = parametersIt.next();
        assertEquals("a", p2.getKey());
        assertEquals(2, p2.getValue().size());
        assertEquals("a1", p2.getValue().get(0));
        assertEquals("a2", p2.getValue().get(1));

        p3 = parametersIt.next();
        assertEquals("b", p3.getKey());
        assertEquals(1, p3.getValue().size());
        assertEquals("b1", p3.getValue().get(0));

        parameters = HttpUtil.getParameters(request, new HashSet<>(Arrays.asList("sku", "category")), true);

        assertEquals(4, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(1, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));

        p2 = parametersIt.next();
        assertEquals("sku", p2.getKey());
        assertEquals(1, p2.getValue().size());
        assertEquals("xyz", p2.getValue().get(0));

        p3 = parametersIt.next();
        assertEquals("a", p3.getKey());
        assertEquals(2, p3.getValue().size());
        assertEquals("a1", p3.getValue().get(0));
        assertEquals("a2", p3.getValue().get(1));

        p4 = parametersIt.next();
        assertEquals("b", p4.getKey());
        assertEquals(1, p4.getValue().size());
        assertEquals("b1", p4.getValue().get(0));

    }

    @Test
    public void testAllParameters() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/category/abc/sku/xyz");
        request.setQueryString("a=a1");
        request.addParameter("a", "a2");
        request.addParameter("b", "b1");

        Map<String, List<String>> parameters;
        Iterator<Map.Entry<String, List<String>>> parametersIt;
        Map.Entry<String, List<String>> p1, p2, p3, p4;

        parameters = HttpUtil.allParameters(request, Collections.emptySet());

        assertEquals(2, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("a", p1.getKey());
        assertEquals(2, p1.getValue().size());
        assertEquals("a1", p1.getValue().get(0));
        assertEquals("a2", p1.getValue().get(1));

        p2 = parametersIt.next();
        assertEquals("b", p2.getKey());
        assertEquals(1, p2.getValue().size());
        assertEquals("b1", p2.getValue().get(0));

        parameters = HttpUtil.allParameters(request, new HashSet<>(Collections.singletonList("category")));

        assertEquals(3, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(1, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));

        p2 = parametersIt.next();
        assertEquals("a", p2.getKey());
        assertEquals(2, p2.getValue().size());
        assertEquals("a1", p2.getValue().get(0));
        assertEquals("a2", p2.getValue().get(1));

        p3 = parametersIt.next();
        assertEquals("b", p3.getKey());
        assertEquals(1, p3.getValue().size());
        assertEquals("b1", p3.getValue().get(0));

        parameters = HttpUtil.allParameters(request, new HashSet<>(Arrays.asList("sku", "category")));

        assertEquals(4, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(1, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));

        p2 = parametersIt.next();
        assertEquals("sku", p2.getKey());
        assertEquals(1, p2.getValue().size());
        assertEquals("xyz", p2.getValue().get(0));

        p3 = parametersIt.next();
        assertEquals("a", p3.getKey());
        assertEquals(2, p3.getValue().size());
        assertEquals("a1", p3.getValue().get(0));
        assertEquals("a2", p3.getValue().get(1));

        p4 = parametersIt.next();
        assertEquals("b", p4.getKey());
        assertEquals(1, p4.getValue().size());
        assertEquals("b1", p4.getValue().get(0));

    }

    @Test
    public void testAllParametersWithDuplicatesPreserve() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/category/abc/sku/xyz/category/abc/sku/xyz");
        request.setQueryString("a=a1&a=a1");
        request.addParameter("a", "a2");
        request.addParameter("a", "a2");
        request.addParameter("b", "b1");
        request.addParameter("b", "b1");

        Map<String, List<String>> parameters;
        Iterator<Map.Entry<String, List<String>>> parametersIt;
        Map.Entry<String, List<String>> p1, p2, p3, p4;

        parameters = HttpUtil.allParameters(request, Collections.emptySet());

        assertEquals(2, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("a", p1.getKey());
        assertEquals(4, p1.getValue().size());
        assertEquals("a1", p1.getValue().get(0));
        assertEquals("a1", p1.getValue().get(1));
        assertEquals("a2", p1.getValue().get(2));
        assertEquals("a2", p1.getValue().get(3));

        p2 = parametersIt.next();
        assertEquals("b", p2.getKey());
        assertEquals(2, p2.getValue().size());
        assertEquals("b1", p2.getValue().get(0));
        assertEquals("b1", p2.getValue().get(1));

        parameters = HttpUtil.allParameters(request, new HashSet<>(Collections.singletonList("category")));

        assertEquals(3, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(2, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));
        assertEquals("abc", p1.getValue().get(1));

        p2 = parametersIt.next();
        assertEquals("a", p2.getKey());
        assertEquals(4, p2.getValue().size());
        assertEquals("a1", p2.getValue().get(0));
        assertEquals("a1", p2.getValue().get(1));
        assertEquals("a2", p2.getValue().get(2));
        assertEquals("a2", p2.getValue().get(3));

        p3 = parametersIt.next();
        assertEquals("b", p3.getKey());
        assertEquals(2, p3.getValue().size());
        assertEquals("b1", p3.getValue().get(0));
        assertEquals("b1", p3.getValue().get(1));

        parameters = HttpUtil.allParameters(request, new HashSet<>(Arrays.asList("sku", "category")));

        assertEquals(4, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(2, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));
        assertEquals("abc", p1.getValue().get(1));

        p2 = parametersIt.next();
        assertEquals("sku", p2.getKey());
        assertEquals(2, p2.getValue().size());
        assertEquals("xyz", p2.getValue().get(0));
        assertEquals("xyz", p2.getValue().get(1));

        p3 = parametersIt.next();
        assertEquals("a", p3.getKey());
        assertEquals(4, p3.getValue().size());
        assertEquals("a1", p3.getValue().get(0));
        assertEquals("a1", p3.getValue().get(1));
        assertEquals("a2", p3.getValue().get(2));
        assertEquals("a2", p3.getValue().get(3));

        p4 = parametersIt.next();
        assertEquals("b", p4.getKey());
        assertEquals(2, p4.getValue().size());
        assertEquals("b1", p4.getValue().get(0));
        assertEquals("b1", p4.getValue().get(1));

    }

    @Test
    public void testAllParametersWithDuplicatesRemove() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/category/abc/sku/xyz/category/abc/sku/xyz");
        request.setQueryString("a=a1&a=a1");
        request.addParameter("a", "a2");
        request.addParameter("a", "a2");
        request.addParameter("b", "b1");
        request.addParameter("b", "b1");

        Map<String, List<String>> parameters;
        Iterator<Map.Entry<String, List<String>>> parametersIt;
        Map.Entry<String, List<String>> p1, p2, p3, p4;

        parameters = HttpUtil.allParameters(request, Collections.emptySet(), true);

        assertEquals(2, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("a", p1.getKey());
        assertEquals(2, p1.getValue().size());
        assertEquals("a1", p1.getValue().get(0));
        assertEquals("a2", p1.getValue().get(1));

        p2 = parametersIt.next();
        assertEquals("b", p2.getKey());
        assertEquals(1, p2.getValue().size());
        assertEquals("b1", p2.getValue().get(0));

        parameters = HttpUtil.allParameters(request, new HashSet<>(Collections.singletonList("category")), true);

        assertEquals(3, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(1, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));

        p2 = parametersIt.next();
        assertEquals("a", p2.getKey());
        assertEquals(2, p2.getValue().size());
        assertEquals("a1", p2.getValue().get(0));
        assertEquals("a2", p2.getValue().get(1));

        p3 = parametersIt.next();
        assertEquals("b", p3.getKey());
        assertEquals(1, p3.getValue().size());
        assertEquals("b1", p3.getValue().get(0));

        parameters = HttpUtil.allParameters(request, new HashSet<>(Arrays.asList("sku", "category")), true);

        assertEquals(4, parameters.size());

        // Make sure order is preserved!!!!
        parametersIt = parameters.entrySet().iterator();

        p1 = parametersIt.next();
        assertEquals("category", p1.getKey());
        assertEquals(1, p1.getValue().size());
        assertEquals("abc", p1.getValue().get(0));

        p2 = parametersIt.next();
        assertEquals("sku", p2.getKey());
        assertEquals(1, p2.getValue().size());
        assertEquals("xyz", p2.getValue().get(0));

        p3 = parametersIt.next();
        assertEquals("a", p3.getKey());
        assertEquals(2, p3.getValue().size());
        assertEquals("a1", p3.getValue().get(0));
        assertEquals("a2", p3.getValue().get(1));

        p4 = parametersIt.next();
        assertEquals("b", p4.getKey());
        assertEquals(1, p4.getValue().size());
        assertEquals("b1", p4.getValue().get(0));

    }



    @Test
    public void testGetSingleValue() throws Exception {

        assertEquals("a", HttpUtil.getSingleValue("a"));
        assertEquals("a", HttpUtil.getSingleValue(new String [] {"a", "b", "c"} ));
        assertEquals("a", HttpUtil.getSingleValue(new ArrayList<String>() {{ add("a"); add("b"); add("c"); }} ));
        assertEquals("a", HttpUtil.getSingleValue(new HashSet<String>() {{ add("a"); }} ));
        assertNull(HttpUtil.getSingleValue(Collections.emptyList()));
        assertNull(HttpUtil.getSingleValue(new String [0] ));

    }


    @Test
    public void testDecodeUtf8UriParam() throws Exception {

        assertEquals("Z+R", HttpUtil.decodeUtf8UriParam("Z+R"));
        assertEquals("Z+R", HttpUtil.decodeUtf8UriParam("Z%2BR"));
        assertEquals("Z R", HttpUtil.decodeUtf8UriParam("Z%20R"));
        assertEquals("белый", HttpUtil.decodeUtf8UriParam("%D0%B1%D0%B5%D0%BB%D1%8B%D0%B9"));
        assertEquals("белый", HttpUtil.decodeUtf8UriParam("бе%D0%BB%D1%8B%D0%B9"));
        assertEquals("бе/лый", HttpUtil.decodeUtf8UriParam("бе/%D0%BB%D1%8B%D0%B9"));
        assertEquals("бе/лый", HttpUtil.decodeUtf8UriParam("бе%2F%D0%BB%D1%8B%D0%B9"));
        assertEquals("бе{/}лый", HttpUtil.decodeUtf8UriParam("бе{/}%D0%BB%D1%8B%D0%B9"));
        assertEquals("бе{/}лый", HttpUtil.decodeUtf8UriParam("бе%7B%2F%7D%D0%BB%D1%8B%D0%B9"));

    }

    @Test
    public void testEncodeUtf8UriParam() throws Exception {


        assertEquals("Z%2BR", HttpUtil.encodeUtf8UriParam("Z+R"));
        assertEquals("Z%25R", HttpUtil.encodeUtf8UriParam("Z%R"));
        assertEquals("Z%2FR", HttpUtil.encodeUtf8UriParam("Z/R"));
        assertEquals("Z%23R", HttpUtil.encodeUtf8UriParam("Z#R"));
        assertEquals("Z%20R", HttpUtil.encodeUtf8UriParam("Z R"));
        assertEquals("белый", HttpUtil.encodeUtf8UriParam("белый"));
        assertEquals("бе%2Fлый", HttpUtil.encodeUtf8UriParam("бе/лый"));
        assertEquals("бе%7B%2F%7Dлый", HttpUtil.encodeUtf8UriParam("бе{/}лый"));

    }
}
