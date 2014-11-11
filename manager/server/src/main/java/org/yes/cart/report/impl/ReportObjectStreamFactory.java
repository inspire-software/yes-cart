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

package org.yes.cart.report.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.yes.cart.domain.dto.impl.CustomerOrderDTOImpl;
import org.yes.cart.domain.dto.impl.CustomerOrderDeliveryDTOImpl;
import org.yes.cart.domain.dto.impl.CustomerOrderDeliveryDetailDTOImpl;
import org.yes.cart.domain.entity.impl.*;
import org.yes.cart.domain.misc.Pair;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * Use this factory to get configured xStream object or configured object output stream
 * to perform transformation of domain objects to xml.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/1/12
 * Time: 11:55 AM
 */
public class ReportObjectStreamFactory {
    
    
    private static final String ROOT_NODE = "yes-report";

    /**
     * Get configured xstream object.
     * @return {@link XStream}
     */
    public static XStream getXStream() {

        final XStream xStream = new XStream(new DomDriver());

        xStream.alias("customer", CustomerEntity.class);
        xStream.alias("order", CustomerOrderEntity.class);
        xStream.alias("wishlist", CustomerWishListEntity.class);
        xStream.alias("customer-av", AttrValueEntityCustomer.class);
        xStream.alias("address", AddressEntity.class);
        xStream.alias("customer-shop", CustomerShopEntity.class);

        xStream.alias("shop", ShopEntity.class);

        xStream.alias("shopurl", ShopUrlEntity.class);

        xStream.alias("exchangerates", ShopExchangeRateEntity.class);

        xStream.alias("pair", Pair.class);
        xStream.alias("orderDto", CustomerOrderDTOImpl.class);
        xStream.alias("orderDeliveryDto", CustomerOrderDeliveryDTOImpl.class);
        xStream.alias("orderLineDto", CustomerOrderDeliveryDetailDTOImpl.class);

        xStream.registerConverter(new Converter() {



            @Override
            public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
                if (source == null) {
                    writer.setValue("");
                } else {
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTime((Date) source);
                    try {
                        writer.setValue(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar).toXMLFormat());
                    } catch (DatatypeConfigurationException e) {
                        writer.setValue("");
                    }
                }

            }

            @Override
            public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
                return null; // not needed
            }

            @Override
            public boolean canConvert(final Class type) {
                return Date.class.isAssignableFrom(type);
            }
        });

        return xStream;

    }


    /**
     * Get configured object output stream.
     * @param writer given writer
     * @return {@link ObjectOutputStream}
     */
    public static ObjectOutputStream getObjectOutputStream(final Writer writer) throws IOException {
        
        return getXStream().createObjectOutputStream(writer, ROOT_NODE);
        
    }

}
