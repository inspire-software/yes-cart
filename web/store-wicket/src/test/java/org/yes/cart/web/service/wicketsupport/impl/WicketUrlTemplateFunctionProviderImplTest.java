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

package org.yes.cart.web.service.wicketsupport.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.web.utils.WicketUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 27/06/2020
 * Time: 22:44
 */
public class WicketUrlTemplateFunctionProviderImplTest {

    final Mockery context = new JUnit4Mockery();

    @Test
    public void doAction() throws Exception {

        final HttpServletRequest rq = this.context.mock(HttpServletRequest.class);

        final WicketUtil util = new WicketUtil() {
            @Override
            public HttpServletRequest getHttpServletRequest() {
                return rq;
            }
        };

        this.context.checking(new Expectations() {{
            allowing(rq).getContextPath(); will(returnValue("ctx"));
        }});

        final WicketUrlTemplateFunctionProviderImpl func1 = new WicketUrlTemplateFunctionProviderImpl(util, "a");

        assertEquals("ctx/a/1", func1.doAction("1", "en", Collections.emptyMap()));
        assertEquals("ctx/a/1", func1.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("ctx/a/1", func1.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("ctx/a/1", func1.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

        final WicketUrlTemplateFunctionProviderImpl func2 = new WicketUrlTemplateFunctionProviderImpl(util, "a", "b");

        assertEquals("ctx", func2.doAction("1", "en", Collections.emptyMap()));
        assertEquals("ctx", func2.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("ctx/a/1/b/2", func2.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("ctx/a/1/b/2", func2.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

}