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
    public void testSplitCommonUseCases() {

        List<String> rez;

        rez = SearchUtil.splitForSearch("HP Prelude Toploader-Tasche, 15,6 Zoll", 2);
        assertEquals(6, rez.size());
        assertEquals("HP", rez.get(0));
        assertEquals("Prelude", rez.get(1));
        assertEquals("Toploader", rez.get(2));
        assertEquals("Tasche", rez.get(3));
        assertEquals("15,6", rez.get(4));
        assertEquals("Zoll", rez.get(5));

        rez = SearchUtil.splitForSearch("HP 410A - Schwarz - Original - LaserJet - Tonerpatrone (CF410A) - für Color LaserJet Pro M452, LaserJet Pro MFP M377, MFP M477", 2);
        assertEquals(18, rez.size());
        assertEquals("HP", rez.get(0));
        assertEquals("410A", rez.get(1));
        assertEquals("Schwarz", rez.get(2));
        assertEquals("Original", rez.get(3));
        assertEquals("LaserJet", rez.get(4));
        assertEquals("Tonerpatrone", rez.get(5));
        assertEquals("CF410A", rez.get(6));
        assertEquals("für", rez.get(7));
        assertEquals("Color", rez.get(8));
        assertEquals("LaserJet", rez.get(9));
        assertEquals("Pro", rez.get(10));
        assertEquals("M452", rez.get(11));
        assertEquals("LaserJet", rez.get(12));
        assertEquals("Pro", rez.get(13));
        assertEquals("MFP", rez.get(14));
        assertEquals("M377", rez.get(15));
        assertEquals("MFP", rez.get(16));
        assertEquals("M477", rez.get(17));

        rez = SearchUtil.splitForSearch("1y SecureDoc WinEntr Supp 5K+ E-LTU", 2);
        assertEquals(6, rez.size());
        assertEquals("1y", rez.get(0));
        assertEquals("SecureDoc", rez.get(1));
        assertEquals("WinEntr", rez.get(2));
        assertEquals("Supp", rez.get(3));
        assertEquals("5K+", rez.get(4));
        assertEquals("E-LTU", rez.get(5));

        rez = SearchUtil.splitForSearch("HP Monitor EliteDisplay E271i / 68,5cm (27\") IPS LED / 1920 x 1080 (16:9) / DVI-D Display Port VGA / 1000:1 / 250cd/m2 / 7ms", 2);
        assertEquals(18, rez.size());
        assertEquals("HP", rez.get(0));
        assertEquals("Monitor", rez.get(1));
        assertEquals("EliteDisplay", rez.get(2));
        assertEquals("E271i", rez.get(3));
        assertEquals("68,5cm", rez.get(4));
        assertEquals("27\"", rez.get(5));
        assertEquals("IPS", rez.get(6));
        assertEquals("LED", rez.get(7));
        assertEquals("1920", rez.get(8));
        assertEquals("1080", rez.get(9));
        assertEquals("16:9", rez.get(10));
        assertEquals("DVI-D", rez.get(11));
        assertEquals("Display", rez.get(12));
        assertEquals("Port", rez.get(13));
        assertEquals("VGA", rez.get(14));
        assertEquals("1000:1", rez.get(15));
        assertEquals("250cd/m2", rez.get(16));
        assertEquals("7ms", rez.get(17));

        rez = SearchUtil.splitForSearch("iPad Wi-Fi + Cellular 128GB - Gold", 2);
        assertEquals(5, rez.size());
        assertEquals("iPad", rez.get(0));
        assertEquals("Wi-Fi", rez.get(1));
        assertEquals("Cellular", rez.get(2));
        assertEquals("128GB", rez.get(3));
        assertEquals("Gold", rez.get(4));

    }

    @Test
    public void testSplitBrutal() {

        List<String> rez;

        // we do not split double dash 'phraze--to', or full stops inside words (e.g. software versions)
        rez = SearchUtil.splitForSearch(" | just, phraze--to split;for.test,,hehe++wow", 0);
        assertEquals(4, rez.size());
        assertEquals("just", rez.get(0));
        assertEquals("phraze--to", rez.get(1));
        assertEquals("split", rez.get(2));
        assertEquals("for.test,,hehe++wow", rez.get(3));

        // we do split dashed words if they are words
        rez = SearchUtil.splitForSearch(" | just, phraze-to split;for.test,,hehe++wow", 0);
        assertEquals(5, rez.size());
        assertEquals("just", rez.get(0));
        assertEquals("phraze", rez.get(1));
        assertEquals("to", rez.get(2));
        assertEquals("split", rez.get(3));
        assertEquals("for.test,,hehe++wow", rez.get(4));

        // we do split dashed words if they are words
        rez = SearchUtil.splitForSearch(" | just, two-phraze split;for.test,,hehe++wow", 3);
        assertEquals(5, rez.size());
        assertEquals("just", rez.get(0));
        assertEquals("two", rez.get(1));
        assertEquals("phraze", rez.get(2));
        assertEquals("split", rez.get(3));
        assertEquals("for.test,,hehe++wow", rez.get(4));

        // we do not split dashed words if they are too short (potentially weird model numbers with dash L-001)
        rez = SearchUtil.splitForSearch(" | just, phraze-to split;for.test,,hehe++wow", 3);
        assertEquals(4, rez.size());
        assertEquals("just", rez.get(0));
        assertEquals("phraze-to", rez.get(1));
        assertEquals("split", rez.get(2));
        assertEquals("for.test,,hehe++wow", rez.get(3));

        // we do not split dashed words if they are too short (potentially weird model numbers with dash L-001)
        rez = SearchUtil.splitForSearch(" | just, t-phraze split;for.test,,hehe++wow", 3);
        assertEquals(4, rez.size());
        assertEquals("just", rez.get(0));
        assertEquals("t-phraze", rez.get(1));
        assertEquals("split", rez.get(2));
        assertEquals("for.test,,hehe++wow", rez.get(3));
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
        assertEquals(1, rez.size());
        assertEquals(",,++", rez.get(0));
        rez = SearchUtil.splitForSearch("  ,,++||-- + - ", 0);
        assertEquals(4, rez.size());
        assertEquals(",,++", rez.get(0));
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
