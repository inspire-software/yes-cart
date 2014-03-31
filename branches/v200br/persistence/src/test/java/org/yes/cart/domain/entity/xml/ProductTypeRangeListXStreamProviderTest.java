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
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.File;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: denispavlov
 * Date: 12-08-05
 * Time: 5:39 PM
 */
public class ProductTypeRangeListXStreamProviderTest {

    @Test
    public void testProvide() throws Exception {

        final XStreamProvider<RangeList> provider = new ProductTypeRangeListXStreamProvider();

        final String xml = new Scanner(new File("src/test/resources/weight_filtering_example.xml")).useDelimiter("\\Z").next();
        final RangeList rl = provider.fromXML(xml);

        assertNotNull(rl);
        assertEquals(4, rl.getRanges().size());

        assertEquals("0.10", (rl.getRanges().get(0)).getFrom());
        assertEquals("1.00", (rl.getRanges().get(0)).getTo());

        final String xmlStr = provider.toXML(rl);

        assertNotNull(xmlStr);

        final RangeList rl2 = provider.fromXML(xmlStr);

        assertNotNull(rl2);
        assertEquals(4, rl2.getRanges().size());

        assertEquals("0.10", (rl2.getRanges().get(0)).getFrom());
        assertEquals("1.00", (rl2.getRanges().get(0)).getTo());

    }
}
