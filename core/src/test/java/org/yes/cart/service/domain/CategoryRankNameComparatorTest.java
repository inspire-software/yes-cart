package org.yes.cart.service.domain;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Category;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 28/08/2015
 * Time: 09:55
 */
public class CategoryRankNameComparatorTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testCompareSame() throws Exception {

        final Category cat1 = context.mock(Category.class, "cat1");

        context.checking(new Expectations() {{
            allowing(cat1).getRank(); will(returnValue(0));
            allowing(cat1).getDisplayName(); will(returnValue("en#~#Name 1#~#ru#~#Name 1"));
            allowing(cat1).getName(); will(returnValue("name 1"));
            allowing(cat1).getCategoryId(); will(returnValue(1L));
        }});

        final SortedSet<Category> set = new TreeSet<Category>(new CategoryRankNameComparator());
        set.addAll(Arrays.asList(cat1, cat1));

        final List<Category> list = new ArrayList<Category>(set);
        assertEquals(1, list.size());
        assertTrue(cat1 == list.get(0));

    }

    @Test
    public void testCompareByRank() throws Exception {

        final Category cat1 = context.mock(Category.class, "cat1");
        final Category cat2 = context.mock(Category.class, "cat2");

        context.checking(new Expectations() {{
            allowing(cat1).getRank(); will(returnValue(0));
            allowing(cat1).getName(); will(returnValue("name 1"));
            allowing(cat1).getCategoryId(); will(returnValue(1L));
            allowing(cat2).getRank(); will(returnValue(1));
        }});

        final SortedSet<Category> set = new TreeSet<Category>(new CategoryRankNameComparator());
        set.addAll(Arrays.asList(cat1, cat2));

        final List<Category> list = new ArrayList<Category>(set);
        assertTrue(cat1 == list.get(0));
        assertTrue(cat2 == list.get(1));

    }

    @Test
    public void testCompareByName() throws Exception {

        final Category cat1 = context.mock(Category.class, "cat1");
        final Category cat2 = context.mock(Category.class, "cat2");

        context.checking(new Expectations() {{
            allowing(cat1).getRank(); will(returnValue(0));
            allowing(cat1).getName(); will(returnValue("name 2"));
            allowing(cat1).getCategoryId(); will(returnValue(1L));
            allowing(cat2).getRank(); will(returnValue(0));
            allowing(cat2).getName(); will(returnValue("name 1"));
        }});

        final SortedSet<Category> set = new TreeSet<Category>(new CategoryRankNameComparator());
        set.addAll(Arrays.asList(cat1, cat2));

        final List<Category> list = new ArrayList<Category>(set);
        assertTrue(cat2 == list.get(0));
        assertTrue(cat1 == list.get(1));

    }

    @Test
    public void testCompareByDisplayName() throws Exception {

        final Category cat1 = context.mock(Category.class, "cat1");
        final Category cat2 = context.mock(Category.class, "cat2");

        context.checking(new Expectations() {{
            allowing(cat1).getRank(); will(returnValue(0));
            allowing(cat1).getName(); will(returnValue("name 2"));
            allowing(cat1).getCategoryId(); will(returnValue(1L));
            allowing(cat2).getRank(); will(returnValue(0));
            allowing(cat2).getName(); will(returnValue("name 1"));
        }});

        final SortedSet<Category> set = new TreeSet<Category>(new CategoryRankNameComparator());
        set.addAll(Arrays.asList(cat1, cat2));

        final List<Category> list = new ArrayList<Category>(set);
        assertTrue(cat2 == list.get(0));
        assertTrue(cat1 == list.get(1));

    }

    @Test
    public void testCompareByNameDifferentCase() throws Exception {

        final Category cat1 = context.mock(Category.class, "cat1");
        final Category cat2 = context.mock(Category.class, "cat2");

        context.checking(new Expectations() {{
            allowing(cat1).getRank(); will(returnValue(0));
            allowing(cat1).getName(); will(returnValue("Name 1"));
            allowing(cat1).getCategoryId(); will(returnValue(1L));
            allowing(cat2).getRank(); will(returnValue(0));
            allowing(cat2).getName(); will(returnValue("name 1"));
            allowing(cat2).getCategoryId(); will(returnValue(2L));
        }});

        final SortedSet<Category> set = new TreeSet<Category>(new CategoryRankNameComparator());
        set.addAll(Arrays.asList(cat1, cat2));

        final List<Category> list = new ArrayList<Category>(set);
        assertTrue(cat1 == list.get(0));
        assertTrue(cat2 == list.get(1));

    }
}