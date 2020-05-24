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

package org.yes.cart.domain.entity.bridge;

import org.junit.Test;
import org.yes.cart.domain.entity.xml.CategoryPriceNavigationXStreamProvider;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.misc.navigation.price.impl.PriceTierNodeImpl;
import org.yes.cart.domain.misc.navigation.price.impl.PriceTierTreeImpl;
import org.yes.cart.stream.xml.XStreamProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 * <p/>
 * This test just prove, that we can serialize / deserialize Price Tier Object.
 */
public class PriceTierTreeTest {

    @Test
    public void testXmlSerialization() {
        PriceTierTree tree = new PriceTierTreeImpl();

        List<PriceTierNode> list = new ArrayList<>();
        list.add(new PriceTierNodeImpl(BigDecimal.ZERO, BigDecimal.ONE));
        list.add(new PriceTierNodeImpl(BigDecimal.ONE, BigDecimal.TEN));
        tree.addPriceTierNode("EUR", list);

        list = new ArrayList<>();
        list.add(new PriceTierNodeImpl(BigDecimal.ONE, new BigDecimal(2)));
        list.add(new PriceTierNodeImpl(new BigDecimal(2), new BigDecimal(9)));
        tree.addPriceTierNode("USD", list);

        final XStreamProvider<PriceTierTree> provider = new CategoryPriceNavigationXStreamProvider();

        String result = provider.toXML(tree);
        assertNotNull(result);
        assertTrue(result.contains("USD"));
        assertTrue(result.contains("EUR"));
        assertFalse(result.contains("UAH"));
    }
}
