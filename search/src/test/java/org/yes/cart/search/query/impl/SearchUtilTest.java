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

package org.yes.cart.search.query.impl;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class SearchUtilTest {

    @Test
    public void testSplit() {

        List<String> rez = SearchUtil.splitForSearch(" | just, phraze--to split;for.test,,hehe++wow", 0);
        assertEquals(6, rez.size());
        assertEquals("just", rez.get(0));
        assertEquals("phraze--to", rez.get(1));
        assertEquals("split", rez.get(2));
        assertEquals("for", rez.get(3));
        assertEquals("test", rez.get(4));
        assertEquals("hehe++wow", rez.get(5));

    }

    @Test
    public void testSplit2() {

        List<String> rez = SearchUtil.splitForSearch("1y SecureDoc WinEntr Supp 5K+ E-LTU", 2);
        assertEquals(6, rez.size());
        assertEquals("1y", rez.get(0));
        assertEquals("SecureDoc", rez.get(1));
        assertEquals("WinEntr", rez.get(2));
        assertEquals("Supp", rez.get(3));
        assertEquals("5K+", rez.get(4));
        assertEquals("E-LTU", rez.get(5));

    }

    @Test
    public void testSplitNullValue() {

        List<String> rez = SearchUtil.splitForSearch(null, 0);
        assertEquals(0, rez.size());

    }

    @Test
    public void testSplitEmptyValue() {

        List<String> rez = SearchUtil.splitForSearch("", 0);
        assertEquals(0, rez.size());
        rez = SearchUtil.splitForSearch("  ,,++||-- + - ", 3);
        assertEquals(0, rez.size());
        rez = SearchUtil.splitForSearch("  ,,++||-- + - ", 0);
        assertEquals(4, rez.size());
        assertEquals("++", rez.get(0));
        assertEquals("--", rez.get(1));
        assertEquals("+", rez.get(2));
        assertEquals("-", rez.get(3));
    }


    @Test
    public void testValToLong() throws Exception {

        assertEquals(Long.valueOf(0L), SearchUtil.valToLong("0", 2));
        assertEquals(Long.valueOf(100000000000000L), SearchUtil.valToLong("1000000000000", 2));
        assertEquals(Long.valueOf(10000000L), SearchUtil.valToLong("100000", 2));
        assertEquals(Long.valueOf(100000L), SearchUtil.valToLong("1000", 2));
        assertEquals(Long.valueOf(100), SearchUtil.valToLong("1", 2));
        assertEquals(Long.valueOf(133), SearchUtil.valToLong("1.33", 2));
        assertEquals(Long.valueOf(3210), SearchUtil.valToLong("32.1", 2));
        assertEquals(Long.valueOf(12), SearchUtil.valToLong(".12", 2));
        assertEquals(Long.valueOf(9800), SearchUtil.valToLong("98", 2));
        assertEquals(Long.valueOf(9822), SearchUtil.valToLong("98.222", 2));
        assertEquals(Long.valueOf(9856), SearchUtil.valToLong("98.555", 2));
        assertNull(SearchUtil.valToLong("98.555zzz", 2));


    }

    @Test
    public void testLongToVal() throws Exception {

        assertEquals("0", SearchUtil.longToVal("0", 2));
        assertEquals("10000000000", SearchUtil.longToVal("1000000000000", 2));
        assertEquals("1000", SearchUtil.longToVal("100000", 2));
        assertEquals("10", SearchUtil.longToVal("1000", 2));
        assertEquals("0.01", SearchUtil.longToVal("1", 2));
        assertEquals("1.33", SearchUtil.longToVal("133", 2));
        assertEquals("3.21", SearchUtil.longToVal("321", 2));
        assertEquals("0.12", SearchUtil.longToVal("12", 2));
        assertEquals("0.98", SearchUtil.longToVal("98", 2));
        assertEquals("98.22", SearchUtil.longToVal("9822", 2));
        assertEquals("98.56", SearchUtil.longToVal("9856", 2));
        assertEquals("0", SearchUtil.longToVal("98.555zzz", 2));


    }

}
