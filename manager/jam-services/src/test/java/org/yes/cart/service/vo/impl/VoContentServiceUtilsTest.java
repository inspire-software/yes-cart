/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.service.vo.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 12/09/2016
 * Time: 15:24
 */
public class VoContentServiceUtilsTest {

    @Test
    public void testEnsureDynamicContentIsValid() throws Exception {


        final String cms =
                "<div> <% if (x) { %> <% def dyno1 = 10 % 3; \n" +
                        "%> text here <% for (int i = 0; i < 10; i++) { %>\n" +
                        "<span>in loop ${ i % 2 < 3 ? 'odd' : 'even'}</span>" +
                        "<% }  } else { %> no loop <% } %></div>";

        final String edited = VoContentServiceUtils.ensureDynamicContentIsVisible(cms);

        assertEquals("<div> &lt;% if (x) { %&gt; &lt;% def dyno1 = 10 % 3; \n" +
                "%&gt; text here &lt;% for (int i = 0; i &lt; 10; i++) { %&gt;\n" +
                "<span>in loop ${ i % 2 &lt; 3 ? 'odd' : 'even'}</span>&lt;% }  } else { %&gt; no loop &lt;% } %&gt;</div>", edited);

        assertEquals(cms, VoContentServiceUtils.ensureDynamicContentIsValid(edited));

    }

    @Test
    public void testEnsureDynamicContentIsValid2() throws Exception {


        final String cms =
                "<% print(x); %>";

        final String edited = VoContentServiceUtils.ensureDynamicContentIsVisible(cms);

        assertEquals("&lt;% print(x); %&gt;", edited);

        assertEquals(cms, VoContentServiceUtils.ensureDynamicContentIsValid(edited));

    }

    @Test
    public void testEnsureDynamicContentIsValid3() throws Exception {


        final String cms =
                "<span>${x>1?'ok':'not ok'}</span>";

        final String edited = VoContentServiceUtils.ensureDynamicContentIsVisible(cms);

        assertEquals("<span>${x&gt;1?'ok':'not ok'}</span>", edited);

        assertEquals(cms, VoContentServiceUtils.ensureDynamicContentIsValid(edited));

    }

    @Test
    public void testEnsureDynamicContentIsBroken() throws Exception {


        final String cms =
                "<div> <% if (x) { %> <% def dyno1 = 10 % 3; \n" +
                        "%> text here <% for (int i = 0; i < 10; i++) { %>\n" +
                        "<span>in loop ${ i % 2 < 3 ? 'odd' : 'even'}</span>" +
                        "<% }  } else { %> no loop } %></div>";

        final String edited = VoContentServiceUtils.ensureDynamicContentIsVisible(cms);

        assertEquals("<div> &lt;% if (x) { %&gt; &lt;% def dyno1 = 10 % 3; \n" +
                "%&gt; text here &lt;% for (int i = 0; i &lt; 10; i++) { %&gt;\n" +
                "<span>in loop ${ i % 2 &lt; 3 ? 'odd' : 'even'}</span>&lt;% }  } else { %&gt; no loop } %></div>", edited);

        assertEquals(cms, VoContentServiceUtils.ensureDynamicContentIsValid(edited));

    }

}