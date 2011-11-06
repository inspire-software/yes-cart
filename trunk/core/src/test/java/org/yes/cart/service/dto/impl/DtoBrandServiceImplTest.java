package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.BrandDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoBrandService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoBrandServiceImplTest extends BaseCoreDBTestCase {

    private DtoBrandService dtoService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() throws Exception {
        dtoService = (DtoBrandService) ctx().getBean(ServiceSpringKeys.DTO_BRAND_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
    }

    @Test
    public void testCreate() throws Exception {
        BrandDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getBrandId() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        BrandDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getBrandId() > 0);
        long id = dto.getBrandId();
        dto.setDescription("Yeah, well, I'm gonna go build my own theme park, with blackjack and hookers. In fact, forget the park!");
        dtoService.update(dto);
        dto = dtoService.getById(id);
        assertEquals("Yeah, well, I'm gonna go build my own theme park, with blackjack and hookers. In fact, forget the park!",
                dto.getDescription());
    }

    @Test
    public void testGetAll() throws Exception {
        List<BrandDTO> list = dtoService.getAll();
        assertFalse(list.isEmpty());
    }

    @Test
    public void testRemove() throws Exception {
        BrandDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getBrandId() > 0);
        long id = dto.getBrandId();
        dtoService.remove(id);
        dto = dtoService.getById(id);
        assertNull(dto);
    }

    private BrandDTO getDto() {
        BrandDTO dto = dtoFactory.getByIface(BrandDTO.class);
        dto.setName("AutoZAZ");
        dto.setDescription("Eared Car Producer");
        return dto;
    }
}
