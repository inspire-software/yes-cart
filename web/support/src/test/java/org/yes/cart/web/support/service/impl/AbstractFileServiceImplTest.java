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
public class AbstractFileServiceImplTest {

    private final Mockery context = new JUnit4Mockery();

    private AbstractFileServiceImpl service;

    @Before
    public void setUp() throws Exception {

        service = new AbstractFileServiceImpl() {
            @Override
            protected String getRepositoryUrlPattern(final Object attributableOrStrategy) {
                return "/pat/";
            }

            @Override
            protected String getAttributePrefix(final Object attributableOrStrategy) {
                return "MY_FILE";
            }
        };

    }

    @Test
    public void testGetFileAttributeFileNamesNone() throws Exception {



        final Attributable attributable = context.mock(Attributable.class, "attributable");

        final List<AttrValue> none = Collections.emptyList();

        context.checking(new Expectations() {{
            oneOf(attributable).getAllAttributes(); will(returnValue(none));
        }});

        final List<Pair<String, String>> pairs = service.getFileAttributeFileNamesInternal(attributable, "en", "MY_FILE");

        assertNotNull(pairs);
        assertEquals(1, pairs.size());
        assertEquals("MY_FILE0", pairs.get(0).getFirst());
        assertEquals(Constants.NO_FILE, pairs.get(0).getSecond());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetFileAttributeFileNamesNoFiles() throws Exception {



        final Attributable attributable = context.mock(Attributable.class, "attributable");

        final AttrValue av1 = context.mock(AttrValue.class, "av1");

        final List<AttrValue> avs = Collections.singletonList(av1);

        context.checking(new Expectations() {{
            oneOf(attributable).getAllAttributes(); will(returnValue(avs));
            oneOf(av1).getAttributeCode(); will(returnValue("NON_IMAGE"));
        }});

        final List<Pair<String, String>> pairs = service.getFileAttributeFileNamesInternal(attributable, "en", "MY_FILE");

        assertNotNull(pairs);
        assertEquals(1, pairs.size());
        assertEquals("MY_FILE0", pairs.get(0).getFirst());
        assertEquals(Constants.NO_FILE, pairs.get(0).getSecond());

        context.assertIsSatisfied();

    }

    @Test
    public void testGetFileAttributeFileNamesNoneInLanguage() throws Exception {



        final Attributable attributable = context.mock(Attributable.class, "attributable");

        final AttrValue av1 = context.mock(AttrValue.class, "av1");
        final AttrValue av2 = context.mock(AttrValue.class, "av2");
        final AttrValue av3 = context.mock(AttrValue.class, "av3");
        final AttrValue av4 = context.mock(AttrValue.class, "av4");

        final List<AttrValue> avs = Arrays.asList(av1, av2, av3, av4);

        context.checking(new Expectations() {{
            oneOf(attributable).getAllAttributes(); will(returnValue(avs));
            oneOf(av1).getAttributeCode(); will(returnValue("NON_FILE"));
            oneOf(av2).getAttributeCode(); will(returnValue("MY_FILE2"));
            exactly(2).of(av2).getVal(); will(returnValue("file2.txt"));
            oneOf(av3).getAttributeCode(); will(returnValue("MY_FILE1"));
            exactly(2).of(av3).getVal(); will(returnValue("file1.pdf"));
            oneOf(av4).getAttributeCode(); will(returnValue("NON_FILE"));

        }});

        final List<Pair<String, String>> pairs = service.getFileAttributeFileNamesInternal(attributable, "en", "MY_FILE");
        assertNotNull(pairs);
        assertEquals(2, pairs.size());
        assertEquals("MY_FILE1", pairs.get(0).getFirst());
        assertEquals("file1.pdf", pairs.get(0).getSecond());
        assertEquals("MY_FILE2", pairs.get(1).getFirst());
        assertEquals("file2.txt", pairs.get(1).getSecond());

        context.assertIsSatisfied();

    }


    @Test
    public void testGetFileAttributeFileNames() throws Exception {



        final Attributable attributable = context.mock(Attributable.class, "attributable");

        final AttrValue av1 = context.mock(AttrValue.class, "av1");
        final AttrValue av2 = context.mock(AttrValue.class, "av2");
        final AttrValue av3 = context.mock(AttrValue.class, "av3");
        final AttrValue av4 = context.mock(AttrValue.class, "av4");

        final List<AttrValue> avs = Arrays.asList(av1, av2, av3, av4);

        context.checking(new Expectations() {{
            oneOf(attributable).getAllAttributes(); will(returnValue(avs));
            oneOf(av1).getAttributeCode(); will(returnValue("MY_FILE1_en"));
            exactly(2).of(av1).getVal(); will(returnValue("file1_en.jpg"));
            oneOf(av2).getAttributeCode(); will(returnValue("MY_FILE2"));
            exactly(2).of(av2).getVal(); will(returnValue("file2.jpg"));
            oneOf(av3).getAttributeCode(); will(returnValue("MY_FILE1"));
            oneOf(av3).getVal(); will(returnValue("file1.jpg"));
            oneOf(av4).getAttributeCode(); will(returnValue("MY_FILE2_en"));
            exactly(2).of(av4).getVal(); will(returnValue("file2_en.jpg"));

        }});

        final List<Pair<String, String>> pairs = service.getFileAttributeFileNamesInternal(attributable, "en", "MY_FILE");
        assertNotNull(pairs);
        assertEquals(2, pairs.size());
        assertEquals("MY_FILE1_en", pairs.get(0).getFirst());
        assertEquals("file1_en.jpg", pairs.get(0).getSecond());
        assertEquals("MY_FILE2_en", pairs.get(1).getFirst());
        assertEquals("file2_en.jpg", pairs.get(1).getSecond());

        context.assertIsSatisfied();

    }



    @Test
    public void testGetFileAttributeFileNamesWithDefaults() throws Exception {



        final Attributable attributable = context.mock(Attributable.class, "attributable");

        final AttrValue av1 = context.mock(AttrValue.class, "av1");
        final AttrValue av2 = context.mock(AttrValue.class, "av2");
        final AttrValue av3 = context.mock(AttrValue.class, "av3");
        final AttrValue av4 = context.mock(AttrValue.class, "av4");

        final List<AttrValue> avs = Arrays.asList(av1, av2, av3, av4);

        context.checking(new Expectations() {{
            oneOf(attributable).getAllAttributes(); will(returnValue(avs));
            oneOf(av1).getAttributeCode(); will(returnValue("MY_FILE1_en"));
            exactly(2).of(av1).getVal(); will(returnValue("file1_en.jpg"));
            oneOf(av2).getAttributeCode(); will(returnValue("MY_FILE2"));
            exactly(2).of(av2).getVal(); will(returnValue("file2.jpg"));
            oneOf(av3).getAttributeCode(); will(returnValue("MY_FILE1"));
            oneOf(av3).getVal(); will(returnValue("file1.jpg"));
            oneOf(av4).getAttributeCode(); will(returnValue("MY_FILE3_en"));
            exactly(2).of(av4).getVal(); will(returnValue("file3_en.jpg"));

        }});

        final List<Pair<String, String>> pairs = service.getFileAttributeFileNamesInternal(attributable, "en", "MY_FILE");
        assertNotNull(pairs);
        assertEquals(3, pairs.size());
        assertEquals("MY_FILE1_en", pairs.get(0).getFirst());
        assertEquals("file1_en.jpg", pairs.get(0).getSecond());
        assertEquals("MY_FILE2", pairs.get(1).getFirst());
        assertEquals("file2.jpg", pairs.get(1).getSecond());
        assertEquals("MY_FILE3_en", pairs.get(2).getFirst());
        assertEquals("file3_en.jpg", pairs.get(2).getSecond());

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
            oneOf(av1).getAttributeCode(); will(returnValue("MY_FILE0"));
            exactly(2).of(av1).getVal(); will(returnValue("file0.jpg"));
            oneOf(av1en).getAttributeCode(); will(returnValue("MY_FILE0_en"));
            exactly(2).of(av1en).getVal(); will(returnValue("file0_en.jpg"));
            oneOf(av2).getAttributeCode(); will(returnValue("MY_FILE1"));
            exactly(2).of(av2).getVal(); will(returnValue("file1.jpg"));
            oneOf(av2de).getAttributeCode(); will(returnValue("MY_FILE1_de"));
            exactly(1).of(av2de).getVal(); will(returnValue("file1_de.jpg"));
            oneOf(av3).getAttributeCode(); will(returnValue("MY_FILE2"));
            exactly(2).of(av3).getVal(); will(returnValue("file2.jpg"));
            oneOf(av4).getAttributeCode(); will(returnValue("MY_FILE3"));
            exactly(2).of(av4).getVal(); will(returnValue("file3.jpg"));
            oneOf(av4en).getAttributeCode(); will(returnValue("MY_FILE3_en"));
            exactly(2).of(av4en).getVal(); will(returnValue("file3_en.jpg"));
            oneOf(av5).getAttributeCode(); will(returnValue("MY_FILE4"));
            exactly(2).of(av5).getVal(); will(returnValue("file4.jpg"));
            oneOf(av6).getAttributeCode(); will(returnValue("MY_FILE5"));
            exactly(2).of(av6).getVal(); will(returnValue("file5.jpg"));
            oneOf(av7).getAttributeCode(); will(returnValue("MY_FILE6"));
            exactly(2).of(av7).getVal(); will(returnValue("file6.jpg"));
            oneOf(av8).getAttributeCode(); will(returnValue("MY_FILE7"));
            exactly(2).of(av8).getVal(); will(returnValue("file7.jpg"));
            oneOf(av9).getAttributeCode(); will(returnValue("MY_FILE8"));
            exactly(2).of(av9).getVal(); will(returnValue("file8.jpg"));
            oneOf(av10).getAttributeCode(); will(returnValue("MY_FILE9"));
            exactly(2).of(av10).getVal(); will(returnValue("file9.jpg"));
            oneOf(av11).getAttributeCode(); will(returnValue("MY_FILE10"));
            exactly(2).of(av11).getVal(); will(returnValue("file10.jpg"));
        }});

        final List<Pair<String, String>> pairs = service.getFileAttributeFileNamesInternal(attributable, "en", "MY_FILE");
        assertNotNull(pairs);
        assertEquals(11, pairs.size());
        assertEquals("MY_FILE0_en", pairs.get(0).getFirst());
        assertEquals("file0_en.jpg", pairs.get(0).getSecond());
        assertEquals("MY_FILE1", pairs.get(1).getFirst());
        assertEquals("file1.jpg", pairs.get(1).getSecond());
        assertEquals("MY_FILE2", pairs.get(2).getFirst());
        assertEquals("file2.jpg", pairs.get(2).getSecond());
        assertEquals("MY_FILE3_en", pairs.get(3).getFirst());
        assertEquals("file3_en.jpg", pairs.get(3).getSecond());
        assertEquals("MY_FILE4", pairs.get(4).getFirst());
        assertEquals("file4.jpg", pairs.get(4).getSecond());
        assertEquals("MY_FILE5", pairs.get(5).getFirst());
        assertEquals("file5.jpg", pairs.get(5).getSecond());
        assertEquals("MY_FILE6", pairs.get(6).getFirst());
        assertEquals("file6.jpg", pairs.get(6).getSecond());
        assertEquals("MY_FILE7", pairs.get(7).getFirst());
        assertEquals("file7.jpg", pairs.get(7).getSecond());
        assertEquals("MY_FILE8", pairs.get(8).getFirst());
        assertEquals("file8.jpg", pairs.get(8).getSecond());
        assertEquals("MY_FILE9", pairs.get(9).getFirst());
        assertEquals("file9.jpg", pairs.get(9).getSecond());
        assertEquals("MY_FILE10", pairs.get(10).getFirst());
        assertEquals("file10.jpg", pairs.get(10).getSecond());

        context.assertIsSatisfied();

    }


}
