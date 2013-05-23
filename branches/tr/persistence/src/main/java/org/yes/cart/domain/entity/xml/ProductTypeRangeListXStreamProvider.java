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
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.domain.misc.navigation.range.RangeNode;
import org.yes.cart.domain.misc.navigation.range.impl.RangeListImpl;
import org.yes.cart.domain.misc.navigation.range.impl.RangeNodeImpl;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 12-08-05
 * Time: 5:36 PM
 */
public class ProductTypeRangeListXStreamProvider implements XStreamProvider<RangeList> {

    private XStream xStream;

    /** {@inheritDoc} */
    public RangeList fromXML(final String xml) {
        return (RangeList) provide().fromXML(xml);
    }

    /** {@inheritDoc} */
    public RangeList fromXML(final InputStream is) {
        return (RangeList) provide().fromXML(is);
    }

    /** {@inheritDoc} */
    public String toXML(final RangeList object) {
        return provide().toXML(object);
    }

    private XStream provide() {
        if (this.xStream == null) {
            XStream xStream = new XStream(new DomDriver());
            xStream.alias("range-list", RangeListImpl.class);
            xStream.addDefaultImplementation(ArrayList.class, List.class);
            xStream.addDefaultImplementation(RangeNodeImpl.class, RangeNode.class);
            xStream.alias("range", RangeNodeImpl.class);
            this.xStream = xStream;
        }
        return this.xStream;
    }
}
