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

package org.yes.cart.search.utils;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 20/03/2018
 * Time: 14:27
 */
public class SearchUtilTest {


    @Test
    public void testSplitCommonUseCases() {

        List<String> rez;

        rez = SearchUtil.splitForSearch("HP Prelude Toploader-Tasche, 15,6 Zoll", 2);
        assertArrayEquals(new String[] {"HP", "Prelude", "Toploader", "Tasche", "15,6", "Zoll" }, rez.toArray());

        rez = SearchUtil.splitForSearch("HP 410A - Schwarz - Original - LaserJet - Tonerpatrone (CF410A) - für Color LaserJet Pro M452, LaserJet Pro MFP M377, MFP M477", 2);
        assertArrayEquals(new String[] {"HP", "410A", "Schwarz", "Original", "LaserJet", "Tonerpatrone", "CF410A", "für", "Color", "LaserJet", "Pro", "M452", "LaserJet", "Pro", "MFP", "M377", "MFP", "M477"}, rez.toArray());

        rez = SearchUtil.splitForSearch("1y SecureDoc WinEntr Supp 5K+ E-LTU", 2);
        assertArrayEquals(new String[] {"1y", "SecureDoc", "WinEntr", "Supp", "5K+", "E-LTU" }, rez.toArray());

        rez = SearchUtil.splitForSearch("HP Monitor EliteDisplay E271i / 68,5cm (27\") IPS LED / 1920 x 1080 (16:9) / DVI-D Display Port VGA / 1000:1 / 250cd/m2 / 7ms", 2);
        assertArrayEquals(new String[] {"HP", "Monitor", "EliteDisplay", "E271i", "68,5cm", "27\"", "IPS", "LED", "1920", "1080", "16:9", "DVI-D", "Display", "Port", "VGA", "1000:1", "250cd", "m2", "7ms" }, rez.toArray());

        rez = SearchUtil.splitForSearch("iPad Wi-Fi + Cellular 128GB - Gold", 2);
        assertArrayEquals(new String[] {"iPad", "Wi-Fi", "Cellular", "128GB", "Gold" }, rez.toArray());

        rez = SearchUtil.splitForSearch("TS/ThinkPad T570/i5-7200U/8GB /256GB SSD/15.6\"/Integrated Intel HD Graphics 620/W10P/Black - Kaby Lake", 2);
        assertArrayEquals(new String[] {"TS", "ThinkPad", "T570", "i5", "7200U", "8GB", "256GB", "SSD", "15.6\"", "Integrated", "Intel", "HD", "Graphics", "620", "W10P", "Black", "Kaby", "Lake" }, rez.toArray());

    }

    @Test
    public void testSplitBrutal() {

        List<String> rez;

        // we do not split double dash 'phraze--to', or full stops inside words (e.g. software versions)
        rez = SearchUtil.splitForSearch(" | just, phraze--to split;for.test,,hehe++wow", 0);
        assertArrayEquals(new String[] {"just", "phraze--to", "split", "for.test,,hehe++wow" }, rez.toArray());

        // we do split dashed words if they are words
        rez = SearchUtil.splitForSearch(" | just, phraze-to split;for.test,,hehe++wow", 0);
        assertArrayEquals(new String[] {"just", "phraze", "to", "split", "for.test,,hehe++wow" }, rez.toArray());

        // we do split dashed words if they are words
        rez = SearchUtil.splitForSearch(" | just, two-phraze split;for.test,,hehe++wow", 3);
        assertArrayEquals(new String[] {"just", "two", "phraze", "split", "for.test,,hehe++wow" }, rez.toArray());

        // we do not split dashed words if they are too short (potentially weird model numbers with dash L-001)
        rez = SearchUtil.splitForSearch(" | just, phraze-to split;for.test,,hehe++wow", 3);
        assertArrayEquals(new String[] {"just", "phraze-to", "split", "for.test,,hehe++wow" }, rez.toArray());

        // we do not split dashed words if they are too short (potentially weird model numbers with dash L-001)
        rez = SearchUtil.splitForSearch(" | just, t-phraze split;for.test,,hehe++wow", 3);
        assertArrayEquals(new String[] {"just", "t-phraze", "split", "for.test,,hehe++wow" }, rez.toArray());
    }


    @Test
    public void testSplitNullValue() {

        List<String> rez = SearchUtil.splitForSearch(null, 0);
        assertEquals(0, rez.size());

    }

    @Test
    public void testSplitEmptyValue() {

        List<String> rez = SearchUtil.splitForSearch("", 0);
        assertTrue(rez.isEmpty());
        rez = SearchUtil.splitForSearch("  ,,++||-- + - ", 3);
        assertArrayEquals(new String[] {",,++" }, rez.toArray());
        rez = SearchUtil.splitForSearch("  ,,++||-- + - ", 0);
        assertArrayEquals(new String[] {",,++", "--", "+", "-" }, rez.toArray());

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