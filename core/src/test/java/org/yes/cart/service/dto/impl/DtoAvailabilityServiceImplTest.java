package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AvailabilityDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoAvailabilityService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAvailabilityServiceImplTest extends BaseCoreDBTestCase {

    private DtoAvailabilityService dtoService = null;
    private DtoFactory dtoFactory = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dtoService = (DtoAvailabilityService) ctx.getBean(ServiceSpringKeys.DTO_AVAILABILITY_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);

    }

    @Test
    public void testCreate() {
        AvailabilityDTO dto = getDto();
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getAvailabilityId() > 0);
        } catch (Exception e) {
            assertFalse(e.getMessage(), false);
        }
    }


    @Test
    public void testUpdate() {
        AvailabilityDTO dto = getDto();
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getAvailabilityId() > 0);
            long id = dto.getAvailabilityId();
            dto.setName("name2");
            dtoService.update(dto);
            dto = dtoService.getById(id);
            assertEquals("name2", dto.getName());
        } catch (Exception e) {
            assertFalse(e.getMessage(), false);
        }

    }

    @Test
    public void testGetAll() {
        try {
            List<AvailabilityDTO> list = dtoService.getAll();
            assertFalse(list.isEmpty());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testRemove() {
        try {
            dtoService.remove(1L);
        } catch (Error e) {
            assertFalse(e.getMessage(), false);
        }
    }

    private AvailabilityDTO getDto() {
        AvailabilityDTO dto = dtoFactory.getByIface(AvailabilityDTO.class);
        dto.setName("name");
        dto.setDescription("description");
        return dto;
    }

}
