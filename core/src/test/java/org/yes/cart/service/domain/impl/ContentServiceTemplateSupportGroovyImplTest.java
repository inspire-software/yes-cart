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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.service.domain.ContentServiceTemplateSupport;
import org.yes.cart.service.domain.TemplateSupport;

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

    final Mockery context = new JUnit4Mockery();



    @Test
    public void testProcessTemplateBasic() throws Exception {

        final CacheManager cacheManager = context.mock(CacheManager.class);
        final Cache cache = context.mock(Cache.class);

        context.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get("<% \n %>${name} is awesome!"); will(returnValue(null));
            allowing(cache).put(with(equal("<% \n %>${name} is awesome!")), with(any(Object.class)));
        }});

        final TemplateSupport templates = new GroovySimpleTemplateSupportImpl(cacheManager);
        final ContentServiceTemplateSupportGroovyImpl support = new ContentServiceTemplateSupportGroovyImpl(templates);

        final String out = support.processTemplate("${name} is awesome!", "en", new HashMap<String, Object>() {{
            put("name", "YC");
        }});

        assertEquals("YC is awesome!", out);
        context.assertIsSatisfied();
    }

    @Test
    public void testProcessTemplateContext() throws Exception {

        final CacheManager cacheManager = context.mock(CacheManager.class);
        final Cache cache = context.mock(Cache.class);

        context.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get("<% \n %>${context.name} is awesome!"); will(returnValue(null));
            allowing(cache).put(with(equal("<% \n %>${context.name} is awesome!")), with(any(Object.class)));
            allowing(cache).get(any(String.class)); will(returnValue(null));
        }});

        final TemplateSupport templates = new GroovySimpleTemplateSupportImpl(cacheManager);
        final ContentServiceTemplateSupportGroovyImpl support = new ContentServiceTemplateSupportGroovyImpl(templates);

        final String out = support.processTemplate("${context.name} is awesome!", "en", new HashMap<String, Object>() {{
            put("name", "YC");
        }});

        assertEquals("YC is awesome!", out);
        context.assertIsSatisfied();

    }

    @Test
    public void testProcessTemplateUrl() throws Exception {

        final CacheManager cacheManager = context.mock(CacheManager.class);
        final Cache cache = context.mock(Cache.class);

        context.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get("<% \n" +
                    "def isAwesome = {\n" +
                    "   func_isAwesome.doAction(it, locale, context)\n" +
                    "}\n" +
                    " %>${isAwesome(name)}"); will(returnValue(null));
            allowing(cache).put(with(equal("<% \n" +
                    "def isAwesome = {\n" +
                    "   func_isAwesome.doAction(it, locale, context)\n" +
                    "}\n" +
                    " %>${isAwesome(name)}")), with(any(Object.class)));
            allowing(cache).get(any(String.class)); will(returnValue(null));
        }});

        final TemplateSupport templates = new GroovySimpleTemplateSupportImpl(cacheManager);
        final ContentServiceTemplateSupportGroovyImpl support = new ContentServiceTemplateSupportGroovyImpl(templates);

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
        context.assertIsSatisfied();
    }



}
