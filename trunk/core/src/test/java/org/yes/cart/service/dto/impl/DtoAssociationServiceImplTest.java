package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AssociationDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoAssociationService;

import java.util.List;

/**
 * * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAssociationServiceImplTest extends BaseCoreDBTestCase {

    private DtoAssociationService dtoAssociationService = null;
    private DtoFactory dtoFactory = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dtoAssociationService = (DtoAssociationService) ctx.getBean(ServiceSpringKeys.DTO_ASSOCIATION_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
    }

    @Test
    public void testCreate() {
        AssociationDTO dto = getDto();

        try {
            dto = dtoAssociationService.create(dto);
            assertTrue(dto.getAssociationId() > 0);
            dtoAssociationService.remove(dto.getAssociationId());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }


    }


    @Test
    public void testUpdate() {
        AssociationDTO dto = getDto();

        try {
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


        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testGetAll() {
        try {
            List<AssociationDTO> list = dtoAssociationService.getAll();
            assertEquals(4, list.size());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testRemove() {
        AssociationDTO dto = getDto();
        try {
            dto = dtoAssociationService.create(dto);
            assertTrue(dto.getAssociationId() > 0);

            long pk = dto.getAssociationId();

            dtoAssociationService.remove(pk);

            dto = dtoAssociationService.getById(pk);
            assertNull(dto);

        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    private AssociationDTO getDto() {
        AssociationDTO dto = dtoFactory.getByIface(AssociationDTO.class);
        dto.setCode("code");
        dto.setName("name");
        dto.setDescription("description");
        return dto;
    }


}
