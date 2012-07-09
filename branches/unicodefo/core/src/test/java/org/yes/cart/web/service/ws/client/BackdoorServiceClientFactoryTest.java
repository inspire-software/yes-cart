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
        try {
            serv.reindexAllProducts();
        } catch (Exception ex) {
            ex.printStackTrace();
            //nothing al all
        }

    }


}
