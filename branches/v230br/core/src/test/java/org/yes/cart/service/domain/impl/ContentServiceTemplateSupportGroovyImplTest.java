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

package org.yes.cart.service.domain.impl;

import org.junit.Test;
import org.yes.cart.service.domain.ContentServiceTemplateSupport;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 25/04/2014
 * Time: 13:10
 */
public class ContentServiceTemplateSupportGroovyImplTest {

    @Test
    public void testProcessTemplateBasic() throws Exception {

        final ContentServiceTemplateSupportGroovyImpl support = new ContentServiceTemplateSupportGroovyImpl();

        final String out = support.processTemplate("${name} is awesome!", "en", new HashMap<String, Object>() {{
            put("name", "YC");
        }});

        assertEquals("YC is awesome!", out);

    }

    @Test
    public void testProcessTemplateContext() throws Exception {

        final ContentServiceTemplateSupportGroovyImpl support = new ContentServiceTemplateSupportGroovyImpl();

        final String out = support.processTemplate("${context.name} is awesome!", "en", new HashMap<String, Object>() {{
            put("name", "YC");
        }});

        assertEquals("YC is awesome!", out);

    }

    @Test
    public void testProcessTemplateUrl() throws Exception {

        final ContentServiceTemplateSupportGroovyImpl support = new ContentServiceTemplateSupportGroovyImpl();

        support.registerFunction("isAwesome", new ContentServiceTemplateSupport.FunctionProvider() {
            @Override
            public Object doAction(final Object... params) {
                assertEquals("YC", params[0]);
                assertEquals("en", params[1]);
                assertTrue(params[2] instanceof Map);
                return "YC is awesome!";
            }
        });

        final String out = support.processTemplate("${isAwesome(name)}", "en", new HashMap<String, Object>() {{
            put("name", "YC");
        }});

        assertEquals("YC is awesome!", out);

    }



}
