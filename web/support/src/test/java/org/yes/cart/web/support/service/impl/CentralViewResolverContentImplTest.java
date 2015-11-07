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

package org.yes.cart.web.support.service.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 10/04/2015
 * Time: 18:13
 */
public class CentralViewResolverContentImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testResolveMainPanelRendererLabelContentNA() throws Exception {

        final ContentService contentService = context.mock(ContentService.class, "contentService");

        CentralViewResolverContentImpl resolver = new CentralViewResolverContentImpl(contentService);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put("someparam", "1");
        }});

        assertNull(resolved);
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelContentNaN() throws Exception {

        final ContentService contentService = context.mock(ContentService.class, "contentService");

        CentralViewResolverContentImpl resolver = new CentralViewResolverContentImpl(contentService);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.CONTENT_ID, "abc");
        }});

        assertNull(resolved);
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelContentNoTemplate() throws Exception {

        final ContentService contentService = context.mock(ContentService.class, "contentService");

        context.checking(new Expectations() {{
            one(contentService).getContentTemplate(10L); will(returnValue(""));
        }});

        CentralViewResolverContentImpl resolver = new CentralViewResolverContentImpl(contentService);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.CONTENT_ID, "10");
        }});

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.CONTENT, resolved.getFirst());
        assertEquals(CentralViewLabel.CONTENT, resolved.getSecond());
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelContentTemplate() throws Exception {

        final ContentService contentService = context.mock(ContentService.class, "contentService");

        context.checking(new Expectations() {{
            one(contentService).getContentTemplate(10L); will(returnValue(CentralViewLabel.DYNOCONTENT));
        }});

        CentralViewResolverContentImpl resolver = new CentralViewResolverContentImpl(contentService);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.CONTENT_ID, "10");
        }});

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.DYNOCONTENT, resolved.getFirst());
        assertEquals(CentralViewLabel.DYNOCONTENT, resolved.getSecond());

        context.assertIsSatisfied();

    }


}
