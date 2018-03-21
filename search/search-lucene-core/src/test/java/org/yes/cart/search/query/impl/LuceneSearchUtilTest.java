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

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class LuceneSearchUtilTest {

    @Test
    public void testAnalyse() throws Exception {

        List<String> rez;

        rez = LuceneSearchUtil.analyseForSearch("de", "HP 410A - Schwarz - Original - LaserJet - Tonerpatrone (CF410A) - für Color LaserJet Pro M452, LaserJet Pro MFP M377, MFP M477");
        // de removed some ending and stop words
        assertArrayEquals(new String[] {"hp", "410a", "schwarz", "original", "laserjet", "tonerpatron" /* no [e] */, "cf410a", /* removed -> "für", */ "color", "laserjet", "pro", "m452", "laserjet", "pro", "mfp", "m377", "mfp", "m477"}, rez.toArray());

        // en uses starndard analyser (lower case) as English one produces too short stems
        rez = LuceneSearchUtil.analyseForSearch("en", "HP 410A - Schwarz - Original - LaserJet - Tonerpatrone (CF410A) - für Color LaserJet Pro M452, LaserJet Pro MFP M377, MFP M477");
        assertArrayEquals(new String[] {"hp", "410a", "schwarz", "original", "laserjet", "tonerpatrone", "cf410a", "für", "color", "laserjet", "pro", "m452", "laserjet", "pro", "mfp", "m377", "mfp", "m477"}, rez.toArray());
        // use en version of text
        rez = LuceneSearchUtil.analyseForSearch("en", "HP 410A - Black - Original LaserJet Toner Cartridge (CF410A) - for Color LaserJet Pro M452, LaserJet Pro MFP M377, MFP M477");
        assertArrayEquals(new String[] {"hp", "410a", "black", "original", "laserjet", "toner", "cartridge", "cf410a", /* removed -> "for", */ "color", "laserjet", "pro", "m452", "laserjet", "pro", "mfp", "m377", "mfp", "m477"}, rez.toArray());

        // ru/uk analyses works as standard as there are de words
        rez = LuceneSearchUtil.analyseForSearch("ru", "HP 410A - Schwarz - Original - LaserJet - Tonerpatrone (CF410A) - für Color LaserJet Pro M452, LaserJet Pro MFP M377, MFP M477");
        assertArrayEquals(new String[] {"hp", "410a", "schwarz", "original", "laserjet", "tonerpatrone", "cf410a", "für", "color", "laserjet", "pro", "m452", "laserjet", "pro", "mfp", "m377", "mfp", "m477"}, rez.toArray());
        // ru/uk removed some ending and stop words
        rez = LuceneSearchUtil.analyseForSearch("ru", "HP 410A - черный - оригинальный картридж с тонером LaserJet (CF410A) - для Color LaserJet Pro M452, LaserJet Pro MFP M377, MFP M477");
        assertArrayEquals(new String[] {"hp", "410a", "черн" /* no [ый]*/, "оригинальн" /* no [ый]*/, "картридж", /* removed -> "с", */ "тонер" /* no [ом]*/, "laserjet", "cf410a",  /* removed -> "для", */ "color", "laserjet", "pro", "m452", "laserjet", "pro", "mfp", "m377", "mfp", "m477"}, rez.toArray());


        // de removed some ending and stop words
        rez = LuceneSearchUtil.analyseForSearch("de", "HP Prelude Toploader-Tasche, 15,6 Zoll");
        assertArrayEquals(new String[] {"hp", "prelud" /* no [e] */, "topload" /* no [er] */, "tasch" /* no [e] */, "15,6", "zoll"}, rez.toArray());
        // ru/uk analyses works as standard as there are de words
        rez = LuceneSearchUtil.analyseForSearch("ru", "HP Prelude Toploader-Tasche, 15,6 Zoll");
        assertArrayEquals(new String[] {"hp", "prelude", "toploader", "tasche", "15,6", "zoll"}, rez.toArray());

        // de analyses works as standard as there are de words
        rez = LuceneSearchUtil.analyseForSearch("de", "1y SecureDoc WinEntr Supp 5K+ E-LTU");
        assertArrayEquals(new String[] {"1y", "securedoc", "winentr", "supp", "5k", "e", "ltu"}, rez.toArray());
        // ru/uk analyses works as standard as there are de words
        rez = LuceneSearchUtil.analyseForSearch("ru", "1y SecureDoc WinEntr Supp 5K+ E-LTU");
        assertArrayEquals(new String[] {"1y", "securedoc", "winentr", "supp", "5k", "e", "ltu"}, rez.toArray());

        // ru/uk removed some ending and stop words
        rez = LuceneSearchUtil.analyseForSearch("ru", "черная компьтерная мышка ABC-001 с USB");
        assertArrayEquals(new String[] {"черн" /* no [ая]*/, "компьтерн" /* no [ая]*/, "мышк" /* no [а]*/, "abc", "001",  /* removed -> "с", */ "usb"}, rez.toArray());
        rez = LuceneSearchUtil.analyseForSearch("uk", "черная компьтерная мышка ABC-001 с USB");
        assertArrayEquals(new String[] {"черн" /* no [ая]*/, "компьтерн" /* no [ая]*/, "мышк" /* no [а]*/, "abc", "001",  /* removed -> "с", */ "usb"}, rez.toArray());


        // fallback to standard
        rez = LuceneSearchUtil.analyseForSearch("zz", "HP Prelude Toploader-Tasche, 15,6 Zoll");
        assertArrayEquals(new String[] {"hp", "prelude", "toploader", "tasche", "15,6", "zoll"}, rez.toArray());

        // fallback to standard
        rez = LuceneSearchUtil.analyseForSearch(null, "HP Prelude Toploader-Tasche, 15,6 Zoll");
        assertArrayEquals(new String[] {"hp", "prelude", "toploader", "tasche", "15,6", "zoll"}, rez.toArray());

    }



}
