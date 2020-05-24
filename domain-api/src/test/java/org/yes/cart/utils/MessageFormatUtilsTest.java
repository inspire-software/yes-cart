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

package org.yes.cart.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 18/05/2019
 * Time: 16:05
 */
public class MessageFormatUtilsTest {

    @Test
    public void format() throws Exception {

        assertEquals("", MessageFormatUtils.format(null));
        assertEquals("just a message", MessageFormatUtils.format("just a message"));
        assertEquals("just a message", MessageFormatUtils.format("{} a message", "just"));
        assertEquals("just a message", MessageFormatUtils.format("just a {}", "message"));
        assertEquals("just a message", MessageFormatUtils.format("{} a {}", "just", "message"));
        assertEquals("This is just a message.", MessageFormatUtils.format("This is {} a {}.", "just", "message"));
        assertEquals("This is another text.", MessageFormatUtils.format("This is {} {}.", "another", "text"));
        assertEquals("Result: {1} is 2x2=4.", MessageFormatUtils.format("Result: {1} is {}={}.", "2x2", "4"));

    }

}