package org.yes.cart.service.dto.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ProductTypeAttrDTO;
import org.yes.cart.domain.dto.ProductTypeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoProductTypeAttrService;
import org.yes.cart.service.dto.DtoProductTypeService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductTypeAttrServiceImplTest  extends BaseCoreDBTestCase {

    
    private DtoProductTypeAttrService dtoService = null;
    private DtoProductTypeService dtoProductTypeService = null;
    private DtoAttributeService dtoAttributeService = null;
    private DtoFactory dtoFactory = null;

    @Before
    public void setUp()  throws Exception {
        super.setUp();
        dtoService = (DtoProductTypeAttrService) ctx.getBean(ServiceSpringKeys.DTO_PRODUCT_TYPE_ATTR_SERVICE);
        dtoProductTypeService = (DtoProductTypeService) ctx.getBean(ServiceSpringKeys.DTO_PRODUCT_TYPE_SERVICE);
        dtoAttributeService = (DtoAttributeService) ctx.getBean(ServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);

    }

    @After
    public void tearDown() {
        dtoService = null;
        dtoFactory = null;
        dtoProductTypeService = null;
        dtoAttributeService = null;
        super.tearDown();
    }

    @Test
    public void testCreate() throws UnmappedInterfaceException, UnableToCreateInstanceException {

        ProductTypeAttrDTO dtoProductTypeAttr = getDto();

        dtoProductTypeAttr = dtoService.create(dtoProductTypeAttr);
        assertTrue(dtoProductTypeAttr.getProductTypeAttrId() > 0);

    }

    @Test
    public void testUpdate()  throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final String rangeNav = "<rangeList serialization=\"custom\"><unserializable-parents/><list><default><size>10</size></default><int>10</int><range><range><first class=\"string\">0.10</first><second class=\"string\">1.00</second></range></range><range><range><first class=\"string\">1.00</first><second class=\"string\">2.00</second></range></range><range><range><first class=\"string\">2.00</first><second class=\"string\">3.00</second></range></range><range><range><first class=\"string\">3.00</first><second class=\"string\">4.00</second></range></range><range><range><first class=\"string\">4.00</first><second class=\"string\">5.00</second></range></range><range><range><first class=\"string\">5.00</first><second class=\"string\">6.00</second></range></range><range><range><first class=\"string\">6.00</first><second class=\"string\">7.00</second></range></range><range><range><first class=\"string\">7.00</first><second class=\"string\">8.00</second></range></range><range><range><first class=\"string\">8.00</first><second class=\"string\">10.00</second></range></range><range><range><first class=\"string\">10.00</first><second class=\"string\">20.00</second></range></range></list></rangeList>";

        ProductTypeAttrDTO dtoProductTypeAttr = getDto();

        dtoProductTypeAttr = dtoService.create(dtoProductTypeAttr);
        assertTrue(dtoProductTypeAttr.getProductTypeAttrId() > 0);

        dtoProductTypeAttr.setNavigation(true);
        dtoProductTypeAttr.setNavigationType("R");
        dtoProductTypeAttr.setProducttypeId(
                1L
        );
        dtoProductTypeAttr.setRangeNavigation(rangeNav);
        dtoProductTypeAttr.setSimulariry(true);
        dtoProductTypeAttr.setVisible(true);

        dtoProductTypeAttr = dtoService.update(dtoProductTypeAttr);

        assertTrue(dtoProductTypeAttr.isNavigation());
        assertTrue(dtoProductTypeAttr.isSimulariry());
        assertTrue(dtoProductTypeAttr.isVisible());

        assertEquals("R", dtoProductTypeAttr.getNavigationType());
        assertEquals(1, dtoProductTypeAttr.getProducttypeId());
        assertEquals(rangeNav, dtoProductTypeAttr.getRangeNavigation());


    }


    private ProductTypeAttrDTO getDto() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductTypeAttrDTO dtoProductTypeAttr = dtoFactory.getByIface(ProductTypeAttrDTO.class);
        dtoProductTypeAttr.setAttributeDTO(
                dtoAttributeService.getById(7000L)
        );
        dtoProductTypeAttr.setNavigation(false);
        dtoProductTypeAttr.setNavigationType("S");
        final ProductTypeDTO productTypeDTO = dtoProductTypeService.getById(1L);
        dtoProductTypeAttr.setProducttypeId(
                productTypeDTO.getProducttypeId()
        );
        dtoProductTypeAttr.setRangeNavigation(null);
        dtoProductTypeAttr.setSimulariry(false);
        dtoProductTypeAttr.setVisible(false);
        return dtoProductTypeAttr;
    }


}
