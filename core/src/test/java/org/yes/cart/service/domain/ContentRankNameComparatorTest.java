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

package org.yes.cart.service.domain;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Content;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 20/04/2019
 * Time: 18:41
 */
public class ContentRankNameComparatorTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testCompareSame() throws Exception {

        final Content cn1 = context.mock(Content.class, "cn1");

        context.checking(new Expectations() {{
            allowing(cn1).getRank(); will(returnValue(0));
            allowing(cn1).getDisplayName(); will(returnValue("en#~#Name 1#~#ru#~#Name 1"));
            allowing(cn1).getName(); will(returnValue("name 1"));
            allowing(cn1).getContentId(); will(returnValue(1L));
        }});

        final SortedSet<Content> set = new TreeSet<>(new ContentRankNameComparator());
        set.addAll(Arrays.asList(cn1, cn1));

        final List<Content> list = new ArrayList<>(set);
        assertEquals(1, list.size());
        assertTrue(cn1 == list.get(0));

    }

    @Test
    public void testCompareByRank() throws Exception {

        final Content cn1 = context.mock(Content.class, "cn1");
        final Content cn2 = context.mock(Content.class, "cn2");

        context.checking(new Expectations() {{
            allowing(cn1).getRank(); will(returnValue(0));
            allowing(cn1).getName(); will(returnValue("name 1"));
            allowing(cn1).getContentId(); will(returnValue(1L));
            allowing(cn2).getRank(); will(returnValue(1));
        }});

        final SortedSet<Content> set = new TreeSet<>(new ContentRankNameComparator());
        set.addAll(Arrays.asList(cn1, cn2));

        final List<Content> list = new ArrayList<>(set);
        assertTrue(cn1 == list.get(0));
        assertTrue(cn2 == list.get(1));

    }

    @Test
    public void testCompareByName() throws Exception {

        final Content cn1 = context.mock(Content.class, "cn1");
        final Content cn2 = context.mock(Content.class, "cn2");

        context.checking(new Expectations() {{
            allowing(cn1).getRank(); will(returnValue(0));
            allowing(cn1).getName(); will(returnValue("name 2"));
            allowing(cn1).getContentId(); will(returnValue(1L));
            allowing(cn2).getRank(); will(returnValue(0));
            allowing(cn2).getName(); will(returnValue("name 1"));
        }});

        final SortedSet<Content> set = new TreeSet<>(new ContentRankNameComparator());
        set.addAll(Arrays.asList(cn1, cn2));

        final List<Content> list = new ArrayList<>(set);
        assertTrue(cn2 == list.get(0));
        assertTrue(cn1 == list.get(1));

    }

    @Test
    public void testCompareByDisplayName() throws Exception {

        final Content cn1 = context.mock(Content.class, "cn1");
        final Content cn2 = context.mock(Content.class, "cn2");

        context.checking(new Expectations() {{
            allowing(cn1).getRank(); will(returnValue(0));
            allowing(cn1).getName(); will(returnValue("name 2"));
            allowing(cn1).getContentId(); will(returnValue(1L));
            allowing(cn2).getRank(); will(returnValue(0));
            allowing(cn2).getName(); will(returnValue("name 1"));
        }});

        final SortedSet<Content> set = new TreeSet<>(new ContentRankNameComparator());
        set.addAll(Arrays.asList(cn1, cn2));

        final List<Content> list = new ArrayList<>(set);
        assertTrue(cn2 == list.get(0));
        assertTrue(cn1 == list.get(1));

    }

    @Test
    public void testCompareByNameDifferentCase() throws Exception {

        final Content cn1 = context.mock(Content.class, "cn1");
        final Content cn2 = context.mock(Content.class, "cn2");

        context.checking(new Expectations() {{
            allowing(cn1).getRank(); will(returnValue(0));
            allowing(cn1).getName(); will(returnValue("Name 1"));
            allowing(cn1).getContentId(); will(returnValue(1L));
            allowing(cn2).getRank(); will(returnValue(0));
            allowing(cn2).getName(); will(returnValue("name 1"));
            allowing(cn2).getContentId(); will(returnValue(2L));
        }});

        final SortedSet<Content> set = new TreeSet<>(new ContentRankNameComparator());
        set.addAll(Arrays.asList(cn1, cn2));

        final List<Content> list = new ArrayList<>(set);
        assertTrue(cn1 == list.get(0));
        assertTrue(cn2 == list.get(1));

    }
}