/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.utils.spring;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 31/03/2018
 * Time: 19:23
 */
public class LinkedHashMapBeanTest {

    @Test
    public void setExtensionMapExtend() throws Exception {

        final Map<String, String> origin = new HashMap<>();
        origin.put("a", "a");
        origin.put("b", "b");
        origin.put("c", "c");

        final LinkedHashMapBean<String, String> parent = new LinkedHashMapBean<>(origin);
        parent.setBeanName("parent");
        parent.afterPropertiesSet();
        final LinkedHashMapBean<String, String> child1 = new LinkedHashMapBean<>(parent);
        child1.setExtension(Collections.singletonMap("d", "d"));
        child1.setBeanName("child1");
        child1.afterPropertiesSet();
        final LinkedHashMapBean<String, String> child2 = new LinkedHashMapBean<>(parent);
        child2.setExtension(Collections.singletonMap("e", "e"));
        child2.setBeanName("child2");
        child2.afterPropertiesSet();

        assertTrue(parent.keySet().containsAll(Arrays.asList("a", "b", "c", "d", "e")));
        assertTrue(parent.values().containsAll(Arrays.asList("a", "b", "c", "d", "e")));
        assertEquals(5, parent.size());

        assertTrue(child1.keySet().containsAll(Arrays.asList("a", "b", "c", "d")));
        assertTrue(child1.values().containsAll(Arrays.asList("a", "b", "c", "d")));
        assertEquals(4, child1.size());

        assertTrue(child2.keySet().containsAll(Arrays.asList("a", "b", "c", "d", "e")));
        assertTrue(child2.values().containsAll(Arrays.asList("a", "b", "c", "d", "e")));
        assertEquals(5, child2.size());

    }

    @Test
    public void setExtensionMapOverride() throws Exception {

        final Map<String, String> origin = new HashMap<>();
        origin.put("a", "a");
        origin.put("b", "b");
        origin.put("c", "c");

        final Map<String, String> ext1 = new HashMap<>();
        ext1.put("d", "d");

        final Map<String, String> ext2 = new HashMap<>();
        ext2.put("c", "e");
        ext2.put("e", "e");


        final LinkedHashMapBean<String, String> parent = new LinkedHashMapBean<>(origin);
        parent.setBeanName("parent");
        parent.afterPropertiesSet();
        final LinkedHashMapBean<String, String> child1 = new LinkedHashMapBean<>(parent);
        child1.setExtension(ext1);
        child1.setBeanName("child1");
        child1.afterPropertiesSet();
        final LinkedHashMapBean<String, String> child2 = new LinkedHashMapBean<>(parent);
        child2.setExtension(ext2);
        child2.setBeanName("child2");
        child2.afterPropertiesSet();

        assertTrue(parent.keySet().containsAll(Arrays.asList("a", "b", "c", "d", "e")));
        assertTrue(parent.values().containsAll(Arrays.asList("a", "b", "e", "d")));
        assertEquals("e", parent.get("c"));
        assertEquals(5, parent.size());

        assertTrue(child1.keySet().containsAll(Arrays.asList("a", "b", "c", "d")));
        assertTrue(child1.values().containsAll(Arrays.asList("a", "b", "c", "d")));
        assertEquals(4, child1.size());

        assertTrue(child2.keySet().containsAll(Arrays.asList("a", "b", "c", "d", "e")));
        assertTrue(child2.values().containsAll(Arrays.asList("a", "b", "e", "d")));
        assertEquals(5, child2.size());

    }

}