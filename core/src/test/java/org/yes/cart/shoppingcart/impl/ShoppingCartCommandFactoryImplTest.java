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

package org.yes.cart.shoppingcart.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.shoppingcart.ConfigurableShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 26/08/2014
 * Time: 19:57
 */
public class ShoppingCartCommandFactoryImplTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testRemapCommandChainEmpty() throws Exception {

        final List<ConfigurableShoppingCartCommand> commands = Collections.EMPTY_LIST;

        final ShoppingCartCommand[] chain =
                new ShoppingCartCommandFactoryImpl(null).remapCommandChain(commands);

        assertNotNull(chain);
        assertEquals(0, chain.length);

    }

    @Test
    public void testRemapCommandChain1() throws Exception {

        final ConfigurableShoppingCartCommand cmd1 = mockery.mock(ConfigurableShoppingCartCommand.class, "cmd1");


        final List<ConfigurableShoppingCartCommand> commands = Arrays.asList(cmd1);

        final ShoppingCartCommand[] chain =
                new ShoppingCartCommandFactoryImpl(null).remapCommandChain(commands);

        assertNotNull(chain);
        assertEquals(1, chain.length);
        assertSame(cmd1, chain[0]);


    }

    @Test
    public void testRemapCommandChainMany() throws Exception {

        final ConfigurableShoppingCartCommand cmd1 = mockery.mock(ConfigurableShoppingCartCommand.class, "cmd1");
        final ConfigurableShoppingCartCommand cmd2 = mockery.mock(ConfigurableShoppingCartCommand.class, "cmd2");
        final ConfigurableShoppingCartCommand cmd3 = mockery.mock(ConfigurableShoppingCartCommand.class, "cmd3");

        mockery.checking(new Expectations() {{
            allowing(cmd1).getPriority(); will(returnValue(0));
            allowing(cmd2).getPriority(); will(returnValue(1));
            allowing(cmd3).getPriority(); will(returnValue(2));
        }});

        final List<ConfigurableShoppingCartCommand> commands = Arrays.asList(cmd3, cmd2, cmd1);

        final ShoppingCartCommand[] chain =
                new ShoppingCartCommandFactoryImpl(null).remapCommandChain(commands);

        assertNotNull(chain);
        assertEquals(3, chain.length);
        assertSame(cmd1, chain[0]);
        assertSame(cmd2, chain[1]);
        assertSame(cmd3, chain[2]);


    }

    @Test
    public void testRemapCommandChainManyDuplicate() throws Exception {

        final ConfigurableShoppingCartCommand cmd1 = mockery.mock(ConfigurableShoppingCartCommand.class, "cmd1");
        final ConfigurableShoppingCartCommand cmd2 = mockery.mock(ConfigurableShoppingCartCommand.class, "cmd2");
        final ConfigurableShoppingCartCommand cmd3 = mockery.mock(ConfigurableShoppingCartCommand.class, "cmd3");
        final ConfigurableShoppingCartCommand cmd4 = mockery.mock(ConfigurableShoppingCartCommand.class, "cmd4");

        mockery.checking(new Expectations() {{
            allowing(cmd1).getPriority(); will(returnValue(0));
            allowing(cmd2).getPriority(); will(returnValue(1));
            allowing(cmd3).getPriority(); will(returnValue(1));
            allowing(cmd4).getPriority(); will(returnValue(2));
        }});

        final List<ConfigurableShoppingCartCommand> commands = Arrays.asList(cmd2, cmd1, cmd3, cmd4);

        final ShoppingCartCommand[] chain =
                new ShoppingCartCommandFactoryImpl(null).remapCommandChain(commands);

        assertNotNull(chain);
        assertEquals(4, chain.length);
        assertSame(cmd1, chain[0]);
        assertSame(cmd2, chain[1]);
        assertSame(cmd3, chain[2]);
        assertSame(cmd4, chain[3]);


    }
}
