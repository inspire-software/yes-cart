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

package org.yes.cart.web.support.service.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Attributable;
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
            protected String getRepositoryUrlPattern(final Object attributableOrStrategy) {
                return "/pat/";
            }

            @Override
            protected String getAttributePrefix(final Object attributableOrStrategy) {
                return "MY_IMAGE";
            }
        };

    }

    @Test
    public void testGetImageAttributeFileNamesNoneOf() throws Exception {



        final Attributable attributable = context.mock(Attributable.class, "attributable");

        final List<AttrValue> none = Collections.emptyList();

        context.checking(new Expectations() {{
            oneOf(attributable).getAllAttributes(); will(returnValue(none));
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

        final List<AttrValue> avs = Collections.singletonList(av1);

        context.checking(new Expectations() {{
            oneOf(attributable).getAllAttributes(); will(returnValue(avs));
            oneOf(av1).getAttributeCode(); will(returnValue("NON_IMAGE"));
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

        final List<AttrValue> avs = Arrays.asList(av1, av2, av3, av4);

        context.checking(new Expectations() {{
            oneOf(attributable).getAllAttributes(); will(returnValue(avs));
            oneOf(av1).getAttributeCode(); will(returnValue("NON_IMAGE"));
            oneOf(av2).getAttributeCode(); will(returnValue("MY_IMAGE2"));
            exactly(2).of(av2).getVal(); will(returnValue("image2.jpg"));
            oneOf(av3).getAttributeCode(); will(returnValue("MY_IMAGE1"));
            exactly(2).of(av3).getVal(); will(returnValue("image1.jpg"));
            oneOf(av4).getAttributeCode(); will(returnValue("NON_IMAGE"));

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

        final List<AttrValue> avs = Arrays.asList(av1, av2, av3, av4);

        context.checking(new Expectations() {{
            oneOf(attributable).getAllAttributes(); will(returnValue(avs));
            oneOf(av1).getAttributeCode(); will(returnValue("MY_IMAGE1_en"));
            exactly(2).of(av1).getVal(); will(returnValue("image1_en.jpg"));
            oneOf(av2).getAttributeCode(); will(returnValue("MY_IMAGE2"));
            exactly(2).of(av2).getVal(); will(returnValue("image2.jpg"));
            oneOf(av3).getAttributeCode(); will(returnValue("MY_IMAGE1"));
            oneOf(av3).getVal(); will(returnValue("image1.jpg"));
            oneOf(av4).getAttributeCode(); will(returnValue("MY_IMAGE2_en"));
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

        final List<AttrValue> avs = Arrays.asList(av1, av2, av3, av4);

        context.checking(new Expectations() {{
            oneOf(attributable).getAllAttributes(); will(returnValue(avs));
            oneOf(av1).getAttributeCode(); will(returnValue("MY_IMAGE1_en"));
            exactly(2).of(av1).getVal(); will(returnValue("image1_en.jpg"));
            oneOf(av2).getAttributeCode(); will(returnValue("MY_IMAGE2"));
            exactly(2).of(av2).getVal(); will(returnValue("image2.jpg"));
            oneOf(av3).getAttributeCode(); will(returnValue("MY_IMAGE1"));
            oneOf(av3).getVal(); will(returnValue("image1.jpg"));
            oneOf(av4).getAttributeCode(); will(returnValue("MY_IMAGE3_en"));
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




    @Test
    public void testGetImageAttributeFileNamesMoreThan10() throws Exception {



        final Attributable attributable = context.mock(Attributable.class, "attributable");

        final AttrValue av1 = context.mock(AttrValue.class, "av1");
        final AttrValue av1en = context.mock(AttrValue.class, "av1en");
        final AttrValue av2 = context.mock(AttrValue.class, "av2");
        final AttrValue av2de = context.mock(AttrValue.class, "av2de");
        final AttrValue av3 = context.mock(AttrValue.class, "av3");
        final AttrValue av4 = context.mock(AttrValue.class, "av4");
        final AttrValue av4en = context.mock(AttrValue.class, "av4en");
        final AttrValue av5 = context.mock(AttrValue.class, "av5");
        final AttrValue av6 = context.mock(AttrValue.class, "av6");
        final AttrValue av7 = context.mock(AttrValue.class, "av7");
        final AttrValue av8 = context.mock(AttrValue.class, "av8");
        final AttrValue av9 = context.mock(AttrValue.class, "av9");
        final AttrValue av10 = context.mock(AttrValue.class, "av10");
        final AttrValue av11 = context.mock(AttrValue.class, "av11");

        final List<AttrValue> avs = Arrays.asList(av1, av1en, av2, av2de, av3, av4, av4en, av5, av6, av7, av8, av9, av10, av11);

        context.checking(new Expectations() {{
            oneOf(attributable).getAllAttributes(); will(returnValue(avs));
            oneOf(av1).getAttributeCode(); will(returnValue("MY_IMAGE0"));
            exactly(2).of(av1).getVal(); will(returnValue("image0.jpg"));
            oneOf(av1en).getAttributeCode(); will(returnValue("MY_IMAGE0_en"));
            exactly(2).of(av1en).getVal(); will(returnValue("image0_en.jpg"));
            oneOf(av2).getAttributeCode(); will(returnValue("MY_IMAGE1"));
            exactly(2).of(av2).getVal(); will(returnValue("image1.jpg"));
            oneOf(av2de).getAttributeCode(); will(returnValue("MY_IMAGE1_de"));
            exactly(1).of(av2de).getVal(); will(returnValue("image1_de.jpg"));
            oneOf(av3).getAttributeCode(); will(returnValue("MY_IMAGE2"));
            exactly(2).of(av3).getVal(); will(returnValue("image2.jpg"));
            oneOf(av4).getAttributeCode(); will(returnValue("MY_IMAGE3"));
            exactly(2).of(av4).getVal(); will(returnValue("image3.jpg"));
            oneOf(av4en).getAttributeCode(); will(returnValue("MY_IMAGE3_en"));
            exactly(2).of(av4en).getVal(); will(returnValue("image3_en.jpg"));
            oneOf(av5).getAttributeCode(); will(returnValue("MY_IMAGE4"));
            exactly(2).of(av5).getVal(); will(returnValue("image4.jpg"));
            oneOf(av6).getAttributeCode(); will(returnValue("MY_IMAGE5"));
            exactly(2).of(av6).getVal(); will(returnValue("image5.jpg"));
            oneOf(av7).getAttributeCode(); will(returnValue("MY_IMAGE6"));
            exactly(2).of(av7).getVal(); will(returnValue("image6.jpg"));
            oneOf(av8).getAttributeCode(); will(returnValue("MY_IMAGE7"));
            exactly(2).of(av8).getVal(); will(returnValue("image7.jpg"));
            oneOf(av9).getAttributeCode(); will(returnValue("MY_IMAGE8"));
            exactly(2).of(av9).getVal(); will(returnValue("image8.jpg"));
            oneOf(av10).getAttributeCode(); will(returnValue("MY_IMAGE9"));
            exactly(2).of(av10).getVal(); will(returnValue("image9.jpg"));
            oneOf(av11).getAttributeCode(); will(returnValue("MY_IMAGE10"));
            exactly(2).of(av11).getVal(); will(returnValue("image10.jpg"));
        }});

        final List<Pair<String, String>> pairs = service.getImageAttributeFileNamesInternal(attributable, "en", "MY_IMAGE");
        assertNotNull(pairs);
        assertEquals(11, pairs.size());
        assertEquals("MY_IMAGE0_en", pairs.get(0).getFirst());
        assertEquals("image0_en.jpg", pairs.get(0).getSecond());
        assertEquals("MY_IMAGE1", pairs.get(1).getFirst());
        assertEquals("image1.jpg", pairs.get(1).getSecond());
        assertEquals("MY_IMAGE2", pairs.get(2).getFirst());
        assertEquals("image2.jpg", pairs.get(2).getSecond());
        assertEquals("MY_IMAGE3_en", pairs.get(3).getFirst());
        assertEquals("image3_en.jpg", pairs.get(3).getSecond());
        assertEquals("MY_IMAGE4", pairs.get(4).getFirst());
        assertEquals("image4.jpg", pairs.get(4).getSecond());
        assertEquals("MY_IMAGE5", pairs.get(5).getFirst());
        assertEquals("image5.jpg", pairs.get(5).getSecond());
        assertEquals("MY_IMAGE6", pairs.get(6).getFirst());
        assertEquals("image6.jpg", pairs.get(6).getSecond());
        assertEquals("MY_IMAGE7", pairs.get(7).getFirst());
        assertEquals("image7.jpg", pairs.get(7).getSecond());
        assertEquals("MY_IMAGE8", pairs.get(8).getFirst());
        assertEquals("image8.jpg", pairs.get(8).getSecond());
        assertEquals("MY_IMAGE9", pairs.get(9).getFirst());
        assertEquals("image9.jpg", pairs.get(9).getSecond());
        assertEquals("MY_IMAGE10", pairs.get(10).getFirst());
        assertEquals("image10.jpg", pairs.get(10).getSecond());

        context.assertIsSatisfied();

    }


}
