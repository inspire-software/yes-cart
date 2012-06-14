package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AttributeGroupDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoAttributeGroupService;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAttributeGroupServiceImplTezt extends BaseCoreDBTestCase {

    private DtoAttributeGroupService dtoAttributeGroupService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() throws Exception {
        dtoAttributeGroupService = (DtoAttributeGroupService) ctx().getBean(ServiceSpringKeys.DTO_ATTRIBUTE_GROUP_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
    }

    @Test
    public void testGetAttributeGroupByCode() throws Exception {
        AttributeGroupDTO attributeGroupDTO = dtoAttributeGroupService.getAttributeGroupByCode("CATEGORY");
        assertNotNull(attributeGroupDTO);
        assertEquals("CATEGORY", attributeGroupDTO.getCode());
        attributeGroupDTO = dtoAttributeGroupService.getAttributeGroupByCode("NOTEXISTINGGROUP");
        assertNull(attributeGroupDTO);
    }

    @Test
    public void testCreate() throws Exception {
        AttributeGroupDTO attributeGroupDTO = getDto();
        attributeGroupDTO = dtoAttributeGroupService.create(attributeGroupDTO);
        assertTrue(attributeGroupDTO.getAttributegroupId() > 0);
        dtoAttributeGroupService.remove(attributeGroupDTO.getAttributegroupId());
    }

    @Test
    public void testUpdate() throws Exception {
        AttributeGroupDTO attributeGroupDTO = getDto();
        attributeGroupDTO = dtoAttributeGroupService.create(attributeGroupDTO);
        assertTrue(attributeGroupDTO.getAttributegroupId() > 0);
        attributeGroupDTO.setName("other name");
        attributeGroupDTO = dtoAttributeGroupService.update(attributeGroupDTO);
        assertEquals("other name", attributeGroupDTO.getName());
        dtoAttributeGroupService.remove(attributeGroupDTO.getAttributegroupId());
    }

    @Test
    public void testRemove() throws Exception {
        AttributeGroupDTO attributeGroupDTO = getDto();
        attributeGroupDTO = dtoAttributeGroupService.create(attributeGroupDTO);
        assertTrue(attributeGroupDTO.getAttributegroupId() > 0);
        long id = attributeGroupDTO.getAttributegroupId();
        dtoAttributeGroupService.remove(id);
        attributeGroupDTO = dtoAttributeGroupService.getById(id);
        assertNull(attributeGroupDTO);
    }

    private AttributeGroupDTO getDto() {
        AttributeGroupDTO attributeGroupDTO = dtoFactory.getByIface(AttributeGroupDTO.class);
        attributeGroupDTO.setCode("TESTCODE");
        attributeGroupDTO.setName("Test name");
        return attributeGroupDTO;
    }
}
