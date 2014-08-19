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

package org.yes.cart.web.application;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Shop;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: denispavlov
 * Date: 04/08/2014
 * Time: 16:35
 */
public class ApplicationDirectorTest {


    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testGetCurrentThemeChainWhenExists() throws Exception {

        final Shop shopDefault = mockery.mock(Shop.class, "default");
        final Shop shopTheme1 = mockery.mock(Shop.class, "theme1,default");
        final Shop shopTheme2 = mockery.mock(Shop.class, "theme2,theme1,default");


        mockery.checking(new Expectations() {{
            allowing(shopDefault).getFspointer(); will(returnValue("default"));
            allowing(shopTheme1).getFspointer(); will(returnValue("theme1;default"));
            allowing(shopTheme2).getFspointer(); will(returnValue("theme2;theme1;default"));
        }});


        ApplicationDirector.setCurrentShop(null);
        final List<String> chain0 = ApplicationDirector.getCurrentThemeChain();
        assertNotNull(chain0);
        assertEquals(1, chain0.size());
        assertEquals("default", chain0.get(0));
        ApplicationDirector.clear();

        ApplicationDirector.setCurrentShop(shopDefault);
        final List<String> chain1 = ApplicationDirector.getCurrentThemeChain();
        assertNotNull(chain1);
        assertEquals(1, chain1.size());
        assertEquals("default", chain1.get(0));
        ApplicationDirector.clear();

        ApplicationDirector.setCurrentShop(shopTheme1);
        final List<String> chain2 = ApplicationDirector.getCurrentThemeChain();
        assertNotNull(chain2);
        assertEquals(2, chain2.size());
        assertEquals("theme1", chain2.get(0));
        assertEquals("default", chain2.get(1));
        ApplicationDirector.clear();

        ApplicationDirector.setCurrentShop(shopTheme2);
        final List<String> chain3 = ApplicationDirector.getCurrentThemeChain();
        assertNotNull(chain3);
        assertEquals(3, chain3.size());
        assertEquals("theme2", chain3.get(0));
        assertEquals("theme1", chain3.get(1));
        assertEquals("default", chain3.get(2));
        ApplicationDirector.clear();

    }
}
