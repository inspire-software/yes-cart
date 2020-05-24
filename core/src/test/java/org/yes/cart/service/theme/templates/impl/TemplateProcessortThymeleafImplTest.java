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

package org.yes.cart.service.theme.templates.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.thymeleaf.cache.ExpressionCacheKey;
import org.thymeleaf.cache.TemplateCacheKey;
import org.yes.cart.service.theme.templates.TemplateSupport;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 17/02/2019
 * Time: 20:26
 */
public class TemplateProcessortThymeleafImplTest {

    final Mockery context = new JUnit4Mockery();



    @Test
    public void testProcessTemplateBasic() throws Exception {

        final CacheManager cacheManager = context.mock(CacheManager.class);
        final Cache cache = context.mock(Cache.class);

        final TemplateCacheKey tcc = new TemplateCacheKey(null, "[[${name}]] is awesome!", null, 0, 0, null, null);
        final ExpressionCacheKey ecc = new ExpressionCacheKey("expr", "${name}");
        final ExpressionCacheKey ecc2 = new ExpressionCacheKey("spel", "name");

        context.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get(tcc); will(returnValue(null));
            allowing(cache).put(with(equal(tcc)), with(any(Object.class)));
            allowing(cache).get(ecc); will(returnValue(null));
            allowing(cache).put(with(equal(ecc)), with(any(Object.class)));
            allowing(cache).get(ecc2); will(returnValue(null));
            allowing(cache).put(with(equal(ecc2)), with(any(Object.class)));
        }});

        final TemplateSupport templates = new ThymeleafTemplateSupportImpl(cacheManager);
        final TemplateProcessortThymeleafImpl support = new TemplateProcessortThymeleafImpl(templates);

        final String out = support.processTemplate("[[${name}]] is awesome!", "en", new HashMap<String, Object>() {{
            put("name", "YC");
        }});

        assertEquals("YC is awesome!", out);
        context.assertIsSatisfied();
    }

    @Test
    public void testProcessTemplateContext() throws Exception {

        final CacheManager cacheManager = context.mock(CacheManager.class);
        final Cache cache = context.mock(Cache.class);

        final TemplateCacheKey tcc = new TemplateCacheKey(null, "[[${context.name}]] is awesome!", null, 0, 0, null, null);
        final ExpressionCacheKey ecc = new ExpressionCacheKey("expr", "${context.name}");
        final ExpressionCacheKey ecc2 = new ExpressionCacheKey("spel", "context.name");

        context.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get(tcc); will(returnValue(null));
            allowing(cache).put(with(equal(tcc)), with(any(Object.class)));
            allowing(cache).get(ecc); will(returnValue(null));
            allowing(cache).put(with(equal(ecc)), with(any(Object.class)));
            allowing(cache).get(ecc2); will(returnValue(null));
            allowing(cache).put(with(equal(ecc2)), with(any(Object.class)));
            allowing(cache).get(any(String.class)); will(returnValue(null));
        }});

        final TemplateSupport templates = new ThymeleafTemplateSupportImpl(cacheManager);
        final TemplateProcessortThymeleafImpl support = new TemplateProcessortThymeleafImpl(templates);

        final String out = support.processTemplate("[[${context.name}]] is awesome!", "en", new HashMap<String, Object>() {{
            put("name", "YC");
        }});

        assertEquals("YC is awesome!", out);
        context.assertIsSatisfied();

    }

    @Test
    public void testProcessTemplateUrl() throws Exception {

        final CacheManager cacheManager = context.mock(CacheManager.class);
        final Cache cache = context.mock(Cache.class);

        final TemplateCacheKey tcc = new TemplateCacheKey(null, "[[${isAwesome.func(name)}]]", null, 0, 0, null, null);
        final ExpressionCacheKey ecc = new ExpressionCacheKey("expr", "${isAwesome.func(name)}");
        final ExpressionCacheKey ecc2 = new ExpressionCacheKey("spel", "isAwesome.func(name)");

        context.checking(new Expectations() {{
            allowing(cacheManager).getCache("contentService-templateSupport"); will(returnValue(cache));
            allowing(cache).get(tcc); will(returnValue(null));
            allowing(cache).put(with(equal(tcc)), with(any(Object.class)));
            allowing(cache).get(ecc); will(returnValue(null));
            allowing(cache).put(with(equal(ecc)), with(any(Object.class)));
            allowing(cache).get(ecc2); will(returnValue(null));
            allowing(cache).put(with(equal(ecc2)), with(any(Object.class)));
            allowing(cache).get(any(String.class)); will(returnValue(null));
        }});

        final TemplateSupport templates = new ThymeleafTemplateSupportImpl(cacheManager);
        final TemplateProcessortThymeleafImpl support = new TemplateProcessortThymeleafImpl(templates);

        support.registerFunction("isAwesome", params -> {
            assertEquals("YC", params[0]);
            assertEquals("en", params[1]);
            assertTrue(params[2] instanceof Map);
            return "YC is awesome!";
        });

        final String out = support.processTemplate("[[${isAwesome.func(name)}]]", "en", new HashMap<String, Object>() {{
            put("name", "YC");
        }});

        assertEquals("YC is awesome!", out);
        context.assertIsSatisfied();
    }


    @Test
    public void lazyLoad() throws Exception {

        final TemplateProcessortThymeleafImpl support = new TemplateProcessortThymeleafImpl(null);

        final Locale locale = support.lazyLoad("en");
        assertNotNull(locale);
        assertEquals("en", locale.getLanguage());
        assertSame(locale, support.lazyLoad("en"));

    }
}