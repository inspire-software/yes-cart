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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.yes.cart.domain.entity.xml.converter.MapConverter;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.misc.navigation.price.impl.PriceTierNodeImpl;
import org.yes.cart.domain.misc.navigation.price.impl.PriceTierTreeImpl;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12-08-05
 * Time: 5:36 PM
 */
public class CategoryPriceNavigationXStreamProvider implements XStreamProvider<PriceTierTree> {

    private XStream xStream;

    /** {@inheritDoc} */
    public PriceTierTree fromXML(final String xml) {
        return (PriceTierTree) provide().fromXML(xml);
    }

    /** {@inheritDoc} */
    public PriceTierTree fromXML(final InputStream is) {
        return (PriceTierTree) provide().fromXML(is);
    }

    /** {@inheritDoc} */
    public String toXML(final PriceTierTree object) {
        return provide().toXML(object);
    }

    private XStream provide() {
        if (this.xStream == null) {
            XStream xStream = new XStream(new DomDriver());
            xStream.alias("price-navigation", PriceTierTreeImpl.class);
            xStream.aliasField("currencies", PriceTierTreeImpl.class, "priceMap");
            xStream.alias("currency", Map.Entry.class);
            xStream.alias("name", String.class);
            xStream.alias("price-tiers", List.class);
            xStream.alias("price-tier", PriceTierNode.class);
            xStream.registerConverter(new MapConverter(xStream.getMapper(), "currency", "name", "price-tiers"), 10001);
            xStream.addDefaultImplementation(PriceTierNodeImpl.class, PriceTierNode.class);
            this.xStream = xStream;
        }
        return this.xStream;
    }
}
