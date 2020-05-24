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

package org.yes.cart.domain.entity.impl;

import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 22/04/2019
 * Time: 08:35
 */
public class ContentCategoryAdapterTest {


    @Test
    public void testContentBody() throws Exception {

        final CategoryEntity category = createCat("test");
        assertTrue(category.getAllAttributes().isEmpty());

        final ContentCategoryAdapter adapter = new ContentCategoryAdapter(category);

        final String en = loadSampleContent("en");
        adapter.putBody("en", en);
        assertFalse(category.getAllAttributes().isEmpty());
        assertEquals(4, category.getAllAttributes().size());
        final String de = loadSampleContent("de");
        adapter.putBody("de", de);
        assertEquals(8, category.getAllAttributes().size());
        final String uk = loadSampleContent("uk");
        adapter.putBody("uk", uk);
        assertEquals(12, category.getAllAttributes().size());

        assertEquals(en, adapter.getBody("en"));
        assertEquals(de, adapter.getBody("de"));
        assertEquals(uk, adapter.getBody("uk"));

        adapter.setBodies(null);
        assertTrue(category.getAllAttributes().isEmpty());

        assertNull(adapter.getBody("en"));
        assertNull(adapter.getBody("de"));
        assertNull(adapter.getBody("uk"));

        final Map<String, String> bodies = new HashMap<>();
        bodies.put("en", en);
        bodies.put("de", de);
        bodies.put("uk", uk);

        adapter.setBodies(bodies);
        assertFalse(category.getAllAttributes().isEmpty());
        assertEquals(12, category.getAllAttributes().size());

        assertEquals(en, adapter.getBody("en"));
        assertEquals(de, adapter.getBody("de"));
        assertEquals(uk, adapter.getBody("uk"));

    }

    private String loadSampleContent(final String lang) throws Exception {

        final String text = new Scanner(new File("src/test/resources/lorem-ipsum.txt")).useDelimiter("\\Z").next();

        return "Language: " + lang + "\n\n" + text;

    }

    private CategoryEntity createCat(final String name) {

        final CategoryEntity cat = new CategoryEntity();

        cat.setName(name);

        return cat;

    }

}