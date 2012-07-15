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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.report.impl.ReportObjectStreamFactory;

import java.io.ObjectOutputStream;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/1/12
 * Time: 11:59 AM
 */
public class ReportObjectStreamFactoryTest extends BaseCoreDBTestCase {

    private EntityFactory entityFactory;

    @Before
    public void setUp() throws Exception {
        entityFactory = (EntityFactory) ctx().getBean("internalEntityFactory");
    }


    @Test
    public void testGetObjectOutputStream() throws Exception {

        final StringWriter sw = new StringWriter();
        ObjectOutputStream stream  = ReportObjectStreamFactory.getObjectOutputStream(sw);
        stream.writeObject(getCustomer("test-"));
        stream.writeObject(getCustomer("text-"));
        stream.close();
        assertTrue(sw.toString(), sw.toString().contains("test-customer@shopdomain.com"));
        assertTrue(sw.toString(), sw.toString().contains(">test-Firsname<"));
        assertTrue(sw.toString(), sw.toString().contains("<lastname>test-Lastname</lastname>"));
        assertTrue(sw.toString(), sw.toString().contains("<customer>"));
        assertTrue(sw.toString(), sw.toString().contains("</customer>"));
        assertTrue(sw.toString(), sw.toString().contains("<yes-report>"));
        assertTrue(sw.toString(), sw.toString().contains("</yes-report>"));
        assertTrue(sw.toString(), sw.toString().contains("<customerId>0</customerId>"));
        
        System.out.print(sw.toString());


    }

    private Customer getCustomer(String prefix) {
        Customer customer = entityFactory.getByIface(Customer.class);
        customer.setEmail(prefix + "customer@shopdomain.com");
        customer.setFirstname(prefix + "Firsname");
        customer.setLastname(prefix + "Lastname");
        customer.setPassword("rawpassword");
        return customer;
    }

}
