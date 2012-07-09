package org.yes.cart.payment.impl;

import java.io.OutputStream;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 11:24 AM
 */
public class Retest {

    public static void main(String [] args) {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>   \n  " +
                "<notification-acknowledgment serial-number=\"819877072897395-00001-7\" xmlns=\"http://checkout.google.com/schema/2\"/>";
        xml = xml.replace("\n", " ").replaceAll(">\\s*<","><");
        System.out.println(xml);


    }

}
