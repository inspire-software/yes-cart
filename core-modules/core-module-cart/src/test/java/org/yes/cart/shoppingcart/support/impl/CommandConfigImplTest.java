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

package org.yes.cart.shoppingcart.support.impl;

import org.junit.Test;
import org.yes.cart.shoppingcart.support.impl.CommandConfigImpl;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 28/03/2015
 * Time: 15:35
 */
public class CommandConfigImplTest {

    @Test
    public void testIsCommandKey() throws Exception {

        final CommandConfigImpl config = new CommandConfigImpl();

        config.setCmdKeys(Arrays.asList(
                "cmd1", "cmd2", "cmd3", "cmd4", "cmd5"
        ));
        config.setCmdInternalKeys(Arrays.asList(
                "cmd2", "cmd4"
        ));

        assertTrue(config.isCommandKey("cmd1"));
        assertTrue(config.isCommandKey("cmd2"));
        assertTrue(config.isCommandKey("cmd3"));
        assertTrue(config.isCommandKey("cmd4"));
        assertTrue(config.isCommandKey("cmd5"));
        assertFalse(config.isCommandKey("cmd6"));
        assertFalse(config.isCommandKey("aaa"));

        assertFalse(config.isInternalCommandKey("cmd1"));
        assertTrue(config.isInternalCommandKey("cmd2"));
        assertFalse(config.isInternalCommandKey("cmd3"));
        assertTrue(config.isInternalCommandKey("cmd4"));
        assertFalse(config.isInternalCommandKey("cmd5"));
        assertFalse(config.isInternalCommandKey("cmd6"));
        assertFalse(config.isInternalCommandKey("aaa"));

    }
}
