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

package org.yes.cart.web.support.service.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.service.CentralViewResolver;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/26/11
 * Time: 8:36 AM
 */
public class CentralViewResolverFactoryImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testResolveMainPanelRendererLabelWithResolver1() throws Exception {

        final CentralViewResolver r1 = context.mock(CentralViewResolver.class, "r1");
        final CentralViewResolver r2 = context.mock(CentralViewResolver.class, "r2");
        final CentralViewResolver r3 = context.mock(CentralViewResolver.class, "r3");

        final Map params = context.mock(Map.class, "params");

        context.checking(new Expectations() {{
            one(r1).resolveMainPanelRendererLabel(params); will(returnValue(new Pair<String, String>("1", "2")));
        }});

        CentralViewResolverFactoryImpl resolver = new CentralViewResolverFactoryImpl(Arrays.asList(r1, r2, r3));

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(params);

        assertNotNull(resolved);
        assertEquals("1", resolved.getFirst());
        assertEquals("2", resolved.getSecond());

    }

    @Test
    public void testResolveMainPanelRendererLabelWithResolver2() throws Exception {

        final CentralViewResolver r1 = context.mock(CentralViewResolver.class, "r1");
        final CentralViewResolver r2 = context.mock(CentralViewResolver.class, "r2");
        final CentralViewResolver r3 = context.mock(CentralViewResolver.class, "r3");

        final Map params = context.mock(Map.class, "params");

        context.checking(new Expectations() {{
            one(r1).resolveMainPanelRendererLabel(params); will(returnValue(null));
            one(r2).resolveMainPanelRendererLabel(params); will(returnValue(new Pair<String, String>("1", "2")));
        }});

        CentralViewResolverFactoryImpl resolver = new CentralViewResolverFactoryImpl(Arrays.asList(r1, r2, r3));

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(params);

        assertNotNull(resolved);
        assertEquals("1", resolved.getFirst());
        assertEquals("2", resolved.getSecond());

    }

    @Test
    public void testResolveMainPanelRendererLabelWithResolver3() throws Exception {

        final CentralViewResolver r1 = context.mock(CentralViewResolver.class, "r1");
        final CentralViewResolver r2 = context.mock(CentralViewResolver.class, "r2");
        final CentralViewResolver r3 = context.mock(CentralViewResolver.class, "r3");

        final Map params = context.mock(Map.class, "params");

        context.checking(new Expectations() {{
            one(r1).resolveMainPanelRendererLabel(params); will(returnValue(null));
            one(r2).resolveMainPanelRendererLabel(params); will(returnValue(null));
            one(r3).resolveMainPanelRendererLabel(params); will(returnValue(new Pair<String, String>("1", "2")));
        }});

        CentralViewResolverFactoryImpl resolver = new CentralViewResolverFactoryImpl(Arrays.asList(r1, r2, r3));

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(params);

        assertNotNull(resolved);
        assertEquals("1", resolved.getFirst());
        assertEquals("2", resolved.getSecond());

    }

    @Test
    public void testResolveMainPanelRendererLabelWithFailover() throws Exception {

        final CentralViewResolver r1 = context.mock(CentralViewResolver.class, "r1");
        final CentralViewResolver r2 = context.mock(CentralViewResolver.class, "r2");
        final CentralViewResolver r3 = context.mock(CentralViewResolver.class, "r3");

        final Map params = context.mock(Map.class, "params");

        context.checking(new Expectations() {{
            one(r1).resolveMainPanelRendererLabel(params); will(returnValue(null));
            one(r2).resolveMainPanelRendererLabel(params); will(returnValue(null));
            one(r3).resolveMainPanelRendererLabel(params); will(returnValue(null));
        }});

        CentralViewResolverFactoryImpl resolver = new CentralViewResolverFactoryImpl(Arrays.asList(r1, r2, r3));

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(params);

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.DEFAULT, resolved.getFirst());
        assertEquals(CentralViewLabel.DEFAULT, resolved.getSecond());

    }

}
