package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.BrandDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoBrandService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoBrandServiceImplTest extends BaseCoreDBTestCase {

    private DtoBrandService dtoService = null;
    private DtoFactory dtoFactory = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dtoService = (DtoBrandService) ctx.getBean(ServiceSpringKeys.DTO_BRAND_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);

    }

    @Test
    public void testCreate() {
        BrandDTO dto = getDto();
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getBrandId() > 0);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }


    @Test
    public void testUpdate() {
        BrandDTO dto = getDto();
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getBrandId() > 0);
            long id = dto.getBrandId();
            dto.setDescription("Yeah, well, I'm gonna go build my own theme park, with blackjack and hookers. In fact, forget the park!");
            dtoService.update(dto);
            dto = dtoService.getById(id);
            assertEquals("Yeah, well, I'm gonna go build my own theme park, with blackjack and hookers. In fact, forget the park!",
                    dto.getDescription());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testGetAll() {
        try {
            List<BrandDTO> list = dtoService.getAll();
            assertFalse(list.isEmpty());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }


    @Test
    public void testRemove() {
        BrandDTO dto = getDto();
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getBrandId() > 0);
            long id = dto.getBrandId();
            dtoService.remove(id);
            dto = dtoService.getById(id);
            assertNull(dto);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    private BrandDTO getDto() {
        BrandDTO dto = dtoFactory.getByIface(BrandDTO.class);
        dto.setName("AutoZAZ");
        dto.setDescription("Eared Car Producer");
        return dto;
    }

}
