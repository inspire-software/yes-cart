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

package org.yes.cart.util.spring;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 31/03/2018
 * Time: 19:12
 */
public class ArrayListBeanTest {

    @Test
    public void setExtensionListExtend() throws Exception {

        final ArrayListBean<String> parent = new ArrayListBean<>(Arrays.asList("a", "b", "c"));
        final ArrayListBean<String> child1 = new ArrayListBean<>(parent);
        child1.setExtensionList(Arrays.asList("d", "e"));
        final ArrayListBean<String> child2 = new ArrayListBean<>(parent);
        child2.setExtensionList(Arrays.asList("f", "g"));

        assertTrue(parent.containsAll(Arrays.asList("a", "b", "c", "d", "e", "f", "g")));
        assertEquals(7, parent.size());

        assertTrue(child1.containsAll(Arrays.asList("a", "b", "c", "d", "e")));
        assertEquals(5, child1.size());

        assertTrue(child2.containsAll(Arrays.asList("a", "b", "c", "d", "e", "f", "g")));
        assertEquals(7, child2.size());

    }

}