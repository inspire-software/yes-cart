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

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 18/09/2019
 * Time: 10:45
 */
public class Null18NModelTest {

    @Test
    public void testString() throws Exception {
        final I18NModel model = new Null18NModel();
        assertNotNull(model.getAllValues());
        assertTrue(model.getAllValues().isEmpty());
        assertNull(model.getValue("EN"));
        assertNull(model.getValue("RU"));
        assertNull(model.getValue(I18NModel.DEFAULT));
    }

    @Test
    public void testGetNull() throws Exception {

        assertNull("Some text", new Null18NModel().getValue(null));
    }

    @Test
    public void testEquals() throws Exception {

        final I18NModel m1 = new Null18NModel();
        final I18NModel m2 = new Null18NModel();

        assertTrue(m1.equals(m2));
        assertTrue(m2.equals(m1));
        assertEquals(m2.hashCode(), m1.hashCode());

    }


    @Test
    public void testCopy() throws Exception {

        final I18NModel model = new Null18NModel();
        final I18NModel copy = model.copy();

        assertEquals(copy, model);

        copy.putValue(I18NModel.DEFAULT, "Changed");
        assertNull(model.getValue("EN"));
        assertNull(copy.getValue("EN"));


    }

}