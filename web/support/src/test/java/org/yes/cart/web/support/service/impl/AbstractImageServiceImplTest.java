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
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Attributable;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.misc.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: denispavlov
 * Date: 17/10/2014
 * Time: 12:13
 */
public class AbstractImageServiceImplTest {

    private final Mockery context = new JUnit4Mockery();

    private AbstractImageServiceImpl service;

    @Before
    public void setUp() throws Exception {

        service = new AbstractImageServiceImpl() {
            @Override
            protected String getImageRepositoryUrlPattern(final Object attributableOrStrategy) {
                return "/pat/";
            }

            @Override
            protected String getImageAttributePrefix(final Object attributableOrStrategy) {
                return "MY_IMAGE";
            }
        };

    }

    @Test
    public void testGetImageAttributeFileNamesNone() throws Exception {



        final Attributable attributable = context.mock(Attributable.class, "attributable");

        final List<AttrValue> none = Collections.emptyList();

        context.checking(new Expectations() {{
            one(attributable).getAllAttributes(); will(returnValue(none));
        }});

        final List<Pair<String, String>> pairs = service.getImageAttributeFileNamesInternal(attributable, "en", "MY_IMAGE");

        assertNotNull(pairs);
        assertEquals(1, pairs.size());
        assertEquals("MY_IMAGE0", pairs.get(0).getFirst());
        assertEquals(Constants.NO_IMAGE, pairs.get(0).getSecond());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetImageAttributeFileNamesNoImages() throws Exception {



        final Attributable attributable = context.mock(Attributable.class, "attributable");

        final AttrValue av1 = context.mock(AttrValue.class, "av1");

        final Attribute a1 = context.mock(Attribute.class, "a1");

        final List<AttrValue> avs = Arrays.asList(av1);

        context.checking(new Expectations() {{
            one(attributable).getAllAttributes(); will(returnValue(avs));
            one(av1).getAttribute(); will(returnValue(a1));
            one(a1).getCode(); will(returnValue("NON_IMAGE"));
        }});

        final List<Pair<String, String>> pairs = service.getImageAttributeFileNamesInternal(attributable, "en", "MY_IMAGE");

        assertNotNull(pairs);
        assertEquals(1, pairs.size());
        assertEquals("MY_IMAGE0", pairs.get(0).getFirst());
        assertEquals(Constants.NO_IMAGE, pairs.get(0).getSecond());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetImageAttributeFileNamesNoneInLanguage() throws Exception {



        final Attributable attributable = context.mock(Attributable.class, "attributable");

        final AttrValue av1 = context.mock(AttrValue.class, "av1");
        final AttrValue av2 = context.mock(AttrValue.class, "av2");
        final AttrValue av3 = context.mock(AttrValue.class, "av3");
        final AttrValue av4 = context.mock(AttrValue.class, "av4");

        final Attribute a1 = context.mock(Attribute.class, "a1");
        final Attribute a2 = context.mock(Attribute.class, "a2");
        final Attribute a3 = context.mock(Attribute.class, "a3");
        final Attribute a4 = context.mock(Attribute.class, "a4");

        final List<AttrValue> avs = Arrays.asList(av1, av2, av3, av4);

        context.checking(new Expectations() {{
            one(attributable).getAllAttributes(); will(returnValue(avs));
            one(av1).getAttribute(); will(returnValue(a1));
            one(a1).getCode(); will(returnValue("NON_IMAGE"));
            one(av2).getAttribute(); will(returnValue(a2));
            one(a2).getCode(); will(returnValue("MY_IMAGE2"));
            exactly(2).of(av2).getVal(); will(returnValue("image2.jpg"));
            one(av3).getAttribute(); will(returnValue(a3));
            one(a3).getCode(); will(returnValue("MY_IMAGE1"));
            exactly(2).of(av3).getVal(); will(returnValue("image1.jpg"));
            one(av4).getAttribute(); will(returnValue(a4));
            one(a4).getCode(); will(returnValue("NON_IMAGE"));

        }});

        final List<Pair<String, String>> pairs = service.getImageAttributeFileNamesInternal(attributable, "en", "MY_IMAGE");
        assertNotNull(pairs);
        assertEquals(2, pairs.size());
        assertEquals("MY_IMAGE1", pairs.get(0).getFirst());
        assertEquals("image1.jpg", pairs.get(0).getSecond());
        assertEquals("MY_IMAGE2", pairs.get(1).getFirst());
        assertEquals("image2.jpg", pairs.get(1).getSecond());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetImageAttributeFileNames() throws Exception {



        final Attributable attributable = context.mock(Attributable.class, "attributable");

        final AttrValue av1 = context.mock(AttrValue.class, "av1");
        final AttrValue av2 = context.mock(AttrValue.class, "av2");
        final AttrValue av3 = context.mock(AttrValue.class, "av3");
        final AttrValue av4 = context.mock(AttrValue.class, "av4");

        final Attribute a1 = context.mock(Attribute.class, "a1");
        final Attribute a2 = context.mock(Attribute.class, "a2");
        final Attribute a3 = context.mock(Attribute.class, "a3");
        final Attribute a4 = context.mock(Attribute.class, "a4");

        final List<AttrValue> avs = Arrays.asList(av1, av2, av3, av4);

        context.checking(new Expectations() {{
            one(attributable).getAllAttributes(); will(returnValue(avs));
            one(av1).getAttribute(); will(returnValue(a1));
            one(a1).getCode(); will(returnValue("MY_IMAGE1_en"));
            exactly(2).of(av1).getVal(); will(returnValue("image1_en.jpg"));
            one(av2).getAttribute(); will(returnValue(a2));
            one(a2).getCode(); will(returnValue("MY_IMAGE2"));
            exactly(2).of(av2).getVal(); will(returnValue("image2.jpg"));
            one(av3).getAttribute(); will(returnValue(a3));
            one(a3).getCode(); will(returnValue("MY_IMAGE1"));
            one(av3).getVal(); will(returnValue("image1.jpg"));
            one(av4).getAttribute(); will(returnValue(a4));
            one(a4).getCode(); will(returnValue("MY_IMAGE2_en"));
            exactly(2).of(av4).getVal(); will(returnValue("image2_en.jpg"));

        }});

        final List<Pair<String, String>> pairs = service.getImageAttributeFileNamesInternal(attributable, "en", "MY_IMAGE");
        assertNotNull(pairs);
        assertEquals(2, pairs.size());
        assertEquals("MY_IMAGE1_en", pairs.get(0).getFirst());
        assertEquals("image1_en.jpg", pairs.get(0).getSecond());
        assertEquals("MY_IMAGE2_en", pairs.get(1).getFirst());
        assertEquals("image2_en.jpg", pairs.get(1).getSecond());

        context.assertIsSatisfied();

    }



    @Test
    public void testGetImageAttributeFileNamesWithDefaults() throws Exception {



        final Attributable attributable = context.mock(Attributable.class, "attributable");

        final AttrValue av1 = context.mock(AttrValue.class, "av1");
        final AttrValue av2 = context.mock(AttrValue.class, "av2");
        final AttrValue av3 = context.mock(AttrValue.class, "av3");
        final AttrValue av4 = context.mock(AttrValue.class, "av4");

        final Attribute a1 = context.mock(Attribute.class, "a1");
        final Attribute a2 = context.mock(Attribute.class, "a2");
        final Attribute a3 = context.mock(Attribute.class, "a3");
        final Attribute a4 = context.mock(Attribute.class, "a4");

        final List<AttrValue> avs = Arrays.asList(av1, av2, av3, av4);

        context.checking(new Expectations() {{
            one(attributable).getAllAttributes(); will(returnValue(avs));
            one(av1).getAttribute(); will(returnValue(a1));
            one(a1).getCode(); will(returnValue("MY_IMAGE1_en"));
            exactly(2).of(av1).getVal(); will(returnValue("image1_en.jpg"));
            one(av2).getAttribute(); will(returnValue(a2));
            one(a2).getCode(); will(returnValue("MY_IMAGE2"));
            exactly(2).of(av2).getVal(); will(returnValue("image2.jpg"));
            one(av3).getAttribute(); will(returnValue(a3));
            one(a3).getCode(); will(returnValue("MY_IMAGE1"));
            one(av3).getVal(); will(returnValue("image1.jpg"));
            one(av4).getAttribute(); will(returnValue(a4));
            one(a4).getCode(); will(returnValue("MY_IMAGE3_en"));
            exactly(2).of(av4).getVal(); will(returnValue("image3_en.jpg"));

        }});

        final List<Pair<String, String>> pairs = service.getImageAttributeFileNamesInternal(attributable, "en", "MY_IMAGE");
        assertNotNull(pairs);
        assertEquals(3, pairs.size());
        assertEquals("MY_IMAGE1_en", pairs.get(0).getFirst());
        assertEquals("image1_en.jpg", pairs.get(0).getSecond());
        assertEquals("MY_IMAGE2", pairs.get(1).getFirst());
        assertEquals("image2.jpg", pairs.get(1).getSecond());
        assertEquals("MY_IMAGE3_en", pairs.get(2).getFirst());
        assertEquals("image3_en.jpg", pairs.get(2).getSecond());

        context.assertIsSatisfied();

    }


}
