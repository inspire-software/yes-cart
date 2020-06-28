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
    public void doActionNoParamCtx() throws Exception {

        final HttpServletRequest rq = this.context.mock(HttpServletRequest.class);

        final WicketUtil util = new WicketUtil() {
            @Override
            public HttpServletRequest getHttpServletRequest() {
                return rq;
            }
        };

        this.context.checking(new Expectations() {{
            allowing(rq).getContextPath(); will(returnValue("/ctx"));
        }});

        final WicketUrlTemplateFunctionProviderImpl func = new WicketUrlTemplateFunctionProviderImpl(util);

        assertEquals("/ctx", func.doAction(null, "en", Collections.emptyMap()));
        assertEquals("/ctx", func.doAction("", "en", Collections.emptyMap()));
        assertEquals("/ctx/1", func.doAction("1", "en", Collections.emptyMap()));
        assertEquals("/ctx/1", func.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("/ctx/1/2", func.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("/ctx/1/2/3", func.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

    @Test
    public void doActionNoParamRoot() throws Exception {

        final HttpServletRequest rq = this.context.mock(HttpServletRequest.class);

        final WicketUtil util = new WicketUtil() {
            @Override
            public HttpServletRequest getHttpServletRequest() {
                return rq;
            }
        };

        this.context.checking(new Expectations() {{
            allowing(rq).getContextPath(); will(returnValue(""));
        }});

        final WicketUrlTemplateFunctionProviderImpl func = new WicketUrlTemplateFunctionProviderImpl(util);

        assertEquals("/", func.doAction(null, "en", Collections.emptyMap()));
        assertEquals("/", func.doAction("", "en", Collections.emptyMap()));
        assertEquals("/1", func.doAction("1", "en", Collections.emptyMap()));
        assertEquals("/1", func.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("/1/2", func.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("/1/2/3", func.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

    @Test
    public void doActionOneParamCtx() throws Exception {

        final HttpServletRequest rq = this.context.mock(HttpServletRequest.class);

        final WicketUtil util = new WicketUtil() {
            @Override
            public HttpServletRequest getHttpServletRequest() {
                return rq;
            }
        };

        this.context.checking(new Expectations() {{
            allowing(rq).getContextPath(); will(returnValue("/ctx"));
        }});

        final WicketUrlTemplateFunctionProviderImpl func = new WicketUrlTemplateFunctionProviderImpl(util, "one");

        assertEquals("/ctx", func.doAction(null, "en", Collections.emptyMap()));
        assertEquals("/ctx", func.doAction("", "en", Collections.emptyMap()));
        assertEquals("/ctx/one/1", func.doAction("1", "en", Collections.emptyMap()));
        assertEquals("/ctx/one/1", func.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("/ctx/one/1/2", func.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("/ctx/one/1/2/3", func.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

    @Test
    public void doActionOneParamRoot() throws Exception {

        final HttpServletRequest rq = this.context.mock(HttpServletRequest.class);

        final WicketUtil util = new WicketUtil() {
            @Override
            public HttpServletRequest getHttpServletRequest() {
                return rq;
            }
        };

        this.context.checking(new Expectations() {{
            allowing(rq).getContextPath(); will(returnValue(""));
        }});

        final WicketUrlTemplateFunctionProviderImpl func = new WicketUrlTemplateFunctionProviderImpl(util, "one");

        assertEquals("/", func.doAction(null, "en", Collections.emptyMap()));
        assertEquals("/", func.doAction("", "en", Collections.emptyMap()));
        assertEquals("/one/1", func.doAction("1", "en", Collections.emptyMap()));
        assertEquals("/one/1", func.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("/one/1/2", func.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("/one/1/2/3", func.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

    @Test
    public void doActionTwoParamsCtx() throws Exception {

        final HttpServletRequest rq = this.context.mock(HttpServletRequest.class);

        final WicketUtil util = new WicketUtil() {
            @Override
            public HttpServletRequest getHttpServletRequest() {
                return rq;
            }
        };

        this.context.checking(new Expectations() {{
            allowing(rq).getContextPath(); will(returnValue("/ctx"));
        }});

        final WicketUrlTemplateFunctionProviderImpl func = new WicketUrlTemplateFunctionProviderImpl(util, "one", "two");

        assertEquals("/ctx", func.doAction(null, "en", Collections.emptyMap()));
        assertEquals("/ctx", func.doAction("", "en", Collections.emptyMap()));
        assertEquals("/ctx", func.doAction("1", "en", Collections.emptyMap()));
        assertEquals("/ctx", func.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("/ctx/one/1/two/2", func.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("/ctx/one/1/two/2/3", func.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

    @Test
    public void doActionTwoParamsRoot() throws Exception {

        final HttpServletRequest rq = this.context.mock(HttpServletRequest.class);

        final WicketUtil util = new WicketUtil() {
            @Override
            public HttpServletRequest getHttpServletRequest() {
                return rq;
            }
        };

        this.context.checking(new Expectations() {{
            allowing(rq).getContextPath(); will(returnValue(""));
        }});

        final WicketUrlTemplateFunctionProviderImpl func = new WicketUrlTemplateFunctionProviderImpl(util, "one", "two");

        assertEquals("/", func.doAction(null, "en", Collections.emptyMap()));
        assertEquals("/", func.doAction("", "en", Collections.emptyMap()));
        assertEquals("/", func.doAction("1", "en", Collections.emptyMap()));
        assertEquals("/", func.doAction(Collections.singleton("1"), "en", Collections.emptyMap()));
        assertEquals("/one/1/two/2", func.doAction(Arrays.asList("1", "2"), "en", Collections.emptyMap()));
        assertEquals("/one/1/two/2/3", func.doAction(Arrays.asList("1", "2", "3"), "en", Collections.emptyMap()));

    }

}