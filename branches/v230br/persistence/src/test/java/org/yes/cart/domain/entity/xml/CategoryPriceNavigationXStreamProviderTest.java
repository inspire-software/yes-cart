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

package org.yes.cart.domain.entity.xml;

import org.junit.Test;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 12-08-05
 * Time: 10:33 PM
 */
public class CategoryPriceNavigationXStreamProviderTest {
    @Test
    public void testProvide() throws Exception {


        final XStreamProvider<PriceTierTree> provider = new CategoryPriceNavigationXStreamProvider();

        final String xml = new Scanner(new File("src/test/resources/price_tier_example.xml")).useDelimiter("\\Z").next();
        final PriceTierTree tierTree = provider.fromXML(xml);

        assertNotNull(tierTree);
        final List<PriceTierNode> eur = tierTree.getPriceTierNodes("EUR");
        assertNotNull(eur);
        assertEquals(5, eur.size());

        assertTrue(new BigDecimal(0).compareTo(eur.get(0).getFrom()) == 0);
        assertTrue(new BigDecimal(100).compareTo(eur.get(0).getTo()) == 0);
        assertTrue(new BigDecimal(1000).compareTo(eur.get(4).getFrom()) == 0);
        assertTrue(new BigDecimal(200000).compareTo(eur.get(4).getTo()) == 0);

        final String xmlStr = provider.toXML(tierTree);

        assertNotNull(xmlStr);

        final PriceTierTree tierTree2 = provider.fromXML(xmlStr);

        assertNotNull(tierTree2);
        final List<PriceTierNode> eur2 = tierTree2.getPriceTierNodes("EUR");
        assertNotNull(eur2);
        assertEquals(5, eur2.size());

        assertTrue(new BigDecimal(0).compareTo(eur2.get(0).getFrom()) == 0);
        assertTrue(new BigDecimal(100).compareTo(eur2.get(0).getTo()) == 0);
        assertTrue(new BigDecimal(1000).compareTo(eur2.get(4).getFrom()) == 0);
        assertTrue(new BigDecimal(200000).compareTo(eur2.get(4).getTo()) == 0);

    }
}
