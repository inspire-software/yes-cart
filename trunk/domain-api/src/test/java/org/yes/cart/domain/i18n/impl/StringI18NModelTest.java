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

package org.yes.cart.domain.i18n.impl;

import org.junit.Test;
import org.yes.cart.domain.i18n.I18NModel;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 12-08-08
 * Time: 8:43 PM
 */
public class StringI18NModelTest {

    @Test
    public void testStringNull() throws Exception {
        final I18NModel model = new StringI18NModel((String) null);
        assertNotNull(model.getAllValues());
        assertTrue(model.getAllValues().isEmpty());
    }

    @Test
    public void testStringEmpty() throws Exception {
        final I18NModel model = new StringI18NModel("");
        assertNotNull(model.getAllValues());
        assertTrue(model.getAllValues().isEmpty());
    }

    @Test
    public void testStringInvalid() throws Exception {
        final I18NModel model = new StringI18NModel("some text");
        assertNotNull(model.getAllValues());
        assertTrue(model.getAllValues().isEmpty());
    }

    @Test
    public void testString() throws Exception {
        final I18NModel model = new StringI18NModel("EN#~#Some text#~#RU#~#Текст#~#UK#~#");
        assertNotNull(model.getAllValues());
        assertEquals(2, model.getAllValues().size());
        assertEquals("Some text", model.getValue("EN"));
        assertEquals("Текст", model.getValue("RU"));
        assertNull(model.getValue("UK"));
        assertNull(model.getValue("CA"));
    }

    @Test
    public void testStringNoFinalMarker() throws Exception {
        final I18NModel model = new StringI18NModel("EN#~#Some text#~#RU#~#Текст");
        assertNotNull(model.getAllValues());
        assertEquals(2, model.getAllValues().size());
        assertEquals("Some text", model.getValue("EN"));
        assertEquals("Текст", model.getValue("RU"));
        assertNull(model.getValue("UK"));
        assertNull(model.getValue("CA"));
    }
}
