package org.yes.cart.report.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.yes.cart.domain.entity.impl.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;

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

    /**
     * Get configured object output stream.
     * @param fileName given file name to write xml
     * @return {@link ObjectOutputStream}
     */
    public static ObjectOutputStream getObjectOutputStream(final String fileName) throws IOException {

        return getXStream().createObjectOutputStream(new FileWriter(fileName), ROOT_NODE);
        
    }

}
