package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AvailabilityDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoAvailabilityService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAvailabilityServiceImplTest extends BaseCoreDBTestCase {

    private DtoAvailabilityService dtoService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() throws Exception {
        dtoService = (DtoAvailabilityService) ctx().getBean(ServiceSpringKeys.DTO_AVAILABILITY_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
    }

    @Ignore("AvailabilityDTO can not be created")
    @Test
    public void testCreate() throws Exception {
        AvailabilityDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getAvailabilityId() > 0);
    }


    @Ignore("AvailabilityDTO can not be created")
    @Test
    public void testUpdate() throws Exception {
        AvailabilityDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getAvailabilityId() > 0);
        long id = dto.getAvailabilityId();
        dto.setName("name2");
        dtoService.update(dto);
        dto = dtoService.getById(id);
        assertEquals("name2", dto.getName());
    }

    @Test
    public void testGetAll() throws Exception {
        List<AvailabilityDTO> list = dtoService.getAll();
        assertFalse(list.isEmpty());
    }

    @Ignore("AvailabilityDTO can not be created")
    @Test
    public void testRemove() {
        dtoService.remove(1L);
    }

    private AvailabilityDTO getDto() {
        AvailabilityDTO dto = dtoFactory.getByIface(AvailabilityDTO.class);
        dto.setName("name");
        dto.setDescription("description");
        return dto;
    }
}
