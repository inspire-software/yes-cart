package org.yes.cart.service.domain.impl;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.service.domain.CarrierService;

import java.io.File;
import java.util.List;

/**
 */
public class CarrierSlaServiceImplTest  extends BaseCoreDBTestCase {

    private CarrierService carrierService;

    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(
                new File("../persistence/src/test/resources/initialdata_carrier.xml"),
                false,
                true
        );
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        carrierService = (CarrierService) ctx.getBean(ServiceSpringKeys.CARRIER_SERVICE);

    }

    @After
    public void tearDown() {
        carrierService = null;
        super.tearDown();
    }

    @Test
    public void testFindCarriersFilterByCurrency() {
        List<Carrier> carriers = carrierService.findCarriers(null, null, null, "RUB");
        assertEquals(1,carriers.size());
        assertEquals(1,carriers.get(0).getCarrierSla().size());

        carriers = carrierService.findCarriers(null, null, null, "MWZ"); // no one of carriers use web money


    }

}
