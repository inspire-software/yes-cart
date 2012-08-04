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

package org.yes.cart.web.service.ws.client;

import org.junit.Test;
import org.yes.cart.web.service.ws.BackdoorService;

import static org.junit.Assert.assertNotNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/29/12
 * Time: 9:30 PM
 */
public class BackdoorServiceClientFactoryTest {

    @Test
    public void testGetBackdoorService() throws Exception {

        BackdoorServiceClientFactory factory = new BackdoorServiceClientFactory();
        BackdoorService serv = factory.getBackdoorService(
                "admin@yes-cart.com",
                "1234567",
                "http://localhost:8080/yes-shop/services/backdoor");
        assertNotNull(serv);

        // TODO: why is this test written in such a way? are we expecting an exception? why just not test for that?
        // javax.xml.ws.WebServiceException maybe?
        try {
            serv.reindexAllProducts();
        } catch (Exception ex) {
            ex.printStackTrace();
            //nothing al all
        }

    }


}
