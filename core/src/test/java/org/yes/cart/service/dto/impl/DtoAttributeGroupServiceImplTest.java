package org.yes.cart.service.dto.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AttributeGroupDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoAttributeGroupService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAttributeGroupServiceImplTest extends BaseCoreDBTestCase {

    private DtoAttributeGroupService dtoAttributeGroupService = null;
    private DtoFactory dtoFactory = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dtoAttributeGroupService = (DtoAttributeGroupService) ctx.getBean(ServiceSpringKeys.DTO_ATTRIBUTE_GROUP_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
    }

    @After
    public void tearDown() {
        dtoAttributeGroupService = null;
        dtoFactory = null;
        super.tearDown();
    }

    @Test
    public void testGetAttributeGroupByCode() {
        try {
            AttributeGroupDTO attributeGroupDTO = dtoAttributeGroupService.getAttributeGroupByCode("CATEGORY");
            assertNotNull(attributeGroupDTO);
            assertEquals("CATEGORY", attributeGroupDTO.getCode());

            attributeGroupDTO = dtoAttributeGroupService.getAttributeGroupByCode("NOTEXISTINGGROUP");
            assertNull(attributeGroupDTO);


        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testCreate() {
        AttributeGroupDTO attributeGroupDTO = getDto();
        try {
            attributeGroupDTO = dtoAttributeGroupService.create(attributeGroupDTO);
            assertTrue(attributeGroupDTO.getAttributegroupId() > 0);
            dtoAttributeGroupService.remove(attributeGroupDTO.getAttributegroupId());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testUpdate() {
        AttributeGroupDTO attributeGroupDTO = getDto();
        try {
            attributeGroupDTO = dtoAttributeGroupService.create(attributeGroupDTO);
            assertTrue(attributeGroupDTO.getAttributegroupId() > 0);
            attributeGroupDTO.setName("other name");
            attributeGroupDTO = dtoAttributeGroupService.update(attributeGroupDTO);
            assertEquals("other name", attributeGroupDTO.getName());
            dtoAttributeGroupService.remove(attributeGroupDTO.getAttributegroupId());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }


    @Test
    public void testRemove() {
        AttributeGroupDTO attributeGroupDTO = getDto();
        try {
            attributeGroupDTO = dtoAttributeGroupService.create(attributeGroupDTO);
            assertTrue(attributeGroupDTO.getAttributegroupId() > 0);
            long id = attributeGroupDTO.getAttributegroupId();
            dtoAttributeGroupService.remove(id);
            attributeGroupDTO = dtoAttributeGroupService.getById(id);
            assertNull(attributeGroupDTO);            
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    private AttributeGroupDTO getDto() {
        AttributeGroupDTO attributeGroupDTO = dtoFactory.getByIface(AttributeGroupDTO.class);
        attributeGroupDTO.setCode("TESTCODE");
        attributeGroupDTO.setName("Test name");
        return attributeGroupDTO;
    }



}
