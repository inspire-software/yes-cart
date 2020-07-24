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

package org.yes.cart.domain.i18n.impl;

import org.junit.Test;
import org.yes.cart.domain.i18n.I18NModel;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Date: 24/07/2020
 * Time: 17:31
 */
public class Immutable18NModelTest {

    @Test
    public void testString() throws Exception {
        final I18NModel model = new Immutable18NModel(new StringI18NModel(Collections.singletonMap(I18NModel.DEFAULT, "Some text")));;
        assertNotNull(model.getAllValues());
        assertEquals(1, model.getAllValues().size());
        assertEquals("Some text", model.getValue("EN"));
        assertEquals("Some text", model.getValue("RU"));
        assertEquals("Some text", model.getValue(I18NModel.DEFAULT));
    }

    @Test
    public void testGetNull() throws Exception {

        assertEquals("Some text", new Immutable18NModel(new NonI18NModel("Some text")).getValue(null));
    }

    @Test
    public void testEquals() throws Exception {

        final I18NModel m1 = new Immutable18NModel(new NonI18NModel("Some text"));
        final I18NModel m2 = new Immutable18NModel(new NonI18NModel("Some text"));

        assertTrue(m1.equals(m2));
        assertTrue(m2.equals(m1));
        assertEquals(m2.hashCode(), m1.hashCode());

        final I18NModel m3 = new Immutable18NModel(new NonI18NModel("Some text 2"));

        assertFalse(m3.equals(m2));
        assertFalse(m2.equals(m3));
        assertNotEquals(m2.hashCode(), m3.hashCode());

    }


    @Test
    public void testCopy() throws Exception {

        final I18NModel model = new NonI18NModel("Some text");
        final I18NModel copy = model.copy();

        assertEquals(copy, model);

        copy.putValue(I18NModel.DEFAULT, "Changed");
        assertEquals("Some text", model.getValue("EN"));
        assertEquals("Changed", copy.getValue("EN"));


    }

    @Test
    public void testToString() throws Exception {

        assertEquals("xx#~#some text#~#", new Immutable18NModel(new NonI18NModel("some text")).toString());

    }

}