package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AssociationDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoAssociationService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAssociationServiceImplTest extends BaseCoreDBTestCase {

    private DtoAssociationService dtoAssociationService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() throws Exception {
        dtoAssociationService = (DtoAssociationService) ctx.getBean(ServiceSpringKeys.DTO_ASSOCIATION_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
    }

    @Test
    public void testCreate() throws Exception {
        AssociationDTO dto = getDto();
        dto = dtoAssociationService.create(dto);
        assertTrue(dto.getAssociationId() > 0);
        dtoAssociationService.remove(dto.getAssociationId());
    }

    @Test
    public void testUpdate() throws Exception {
        AssociationDTO dto = getDto();
        dto = dtoAssociationService.create(dto);
        assertTrue(dto.getAssociationId() > 0);
        dto.setCode("code2");
        dto.setName("name2");
        dto.setDescription("description2");
        dto = dtoAssociationService.update(dto);
        assertEquals("code2", dto.getCode());
        assertEquals("name2", dto.getName());
        assertEquals("description2", dto.getDescription());
        dtoAssociationService.remove(dto.getAssociationId());
    }

    @Test
    public void testGetAll() throws Exception {
        List<AssociationDTO> list = dtoAssociationService.getAll();
        assertEquals(4, list.size());
    }

    @Test
    public void testRemove() throws Exception {
        AssociationDTO dto = getDto();
        dto = dtoAssociationService.create(dto);
        assertTrue(dto.getAssociationId() > 0);
        long pk = dto.getAssociationId();
        dtoAssociationService.remove(pk);
        dto = dtoAssociationService.getById(pk);
        assertNull(dto);
    }

    private AssociationDTO getDto() {
        AssociationDTO dto = dtoFactory.getByIface(AssociationDTO.class);
        dto.setCode("code");
        dto.setName("name");
        dto.setDescription("description");
        return dto;
    }
}
