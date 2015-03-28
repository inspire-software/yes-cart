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

package org.yes.cart.web.support.util;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * User: denispavlov
 * Date: 28/03/2015
 * Time: 15:35
 */
public class CommandUtilsTest extends TestCase {

    public void testIsCommandKey() throws Exception {

        final CommandUtils utils = new CommandUtils();

        utils.setCmdKeys(Arrays.asList(
                "cmd1", "cmd2", "cmd3", "cmd4", "cmd5"
        ));
        utils.setCmdInternalKeys(Arrays.asList(
                "cmd2", "cmd4"
        ));

        assertTrue(CommandUtils.isCommandKey("cmd1"));
        assertTrue(CommandUtils.isCommandKey("cmd2"));
        assertTrue(CommandUtils.isCommandKey("cmd3"));
        assertTrue(CommandUtils.isCommandKey("cmd4"));
        assertTrue(CommandUtils.isCommandKey("cmd5"));
        assertFalse(CommandUtils.isCommandKey("cmd6"));
        assertFalse(CommandUtils.isCommandKey("aaa"));

        assertFalse(CommandUtils.isInternalCommandKey("cmd1"));
        assertTrue(CommandUtils.isInternalCommandKey("cmd2"));
        assertFalse(CommandUtils.isInternalCommandKey("cmd3"));
        assertTrue(CommandUtils.isInternalCommandKey("cmd4"));
        assertFalse(CommandUtils.isInternalCommandKey("cmd5"));
        assertFalse(CommandUtils.isInternalCommandKey("cmd6"));
        assertFalse(CommandUtils.isInternalCommandKey("aaa"));

    }
}
