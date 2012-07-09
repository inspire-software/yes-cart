package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.service.dto.DtoShopService;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/5/12
 * Time: 9:22 PM
 */
public class DtoShopServiceImplTezt extends BaseCoreDBTestCase {

    private DtoShopService dtoShopService ;

    @Before
    public void setUp() throws Exception {
        dtoShopService = (DtoShopService) ctx().getBean(ServiceSpringKeys.DTO_SHOP_SERVICE);
    }

    @Test
    public void testSetSupportedCurrencies() throws Exception {

        final ShopDTO dto = dtoShopService.getById(10L);

        final String oldcurr = dtoShopService.getSupportedCurrencies(10L);

        dtoShopService.updateSupportedCurrencies(10L, oldcurr + ",ZZZ");

        final String newcurr = dtoShopService.getSupportedCurrencies(10L);

        assertEquals("Updated currency must contains ZZZ currency", oldcurr+ ",ZZZ", newcurr);

    }


}
