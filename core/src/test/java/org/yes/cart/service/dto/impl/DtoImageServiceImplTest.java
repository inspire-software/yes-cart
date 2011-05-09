package org.yes.cart.service.dto.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.SeoImageDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoImageService;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoImageServiceImplTest  extends BaseCoreDBTestCase {

    private DtoImageService dtoService = null;
    private DtoFactory dtoFactory = null;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        dtoService =  (DtoImageService) ctx.getBean(ServiceSpringKeys.DTO_IMAGE_SERVICE);
        dtoFactory =  (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);

    }

    @After
    public void tearDown() {
        dtoService = null;
        dtoFactory = null;
        super.tearDown();
    }

    @Test
    public void testCreate() {
        try {
            SeoImageDTO dto = getDto();
            dto = dtoService.create(dto);
            assertTrue(dto.getSeoImageId() > 0);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    private SeoImageDTO getDto() throws UnableToCreateInstanceException, UnmappedInterfaceException {
        SeoImageDTO dto = dtoService.getNew();
        dto.setAlt("alt");
        dto.setImageName("image1.jpeg");
        dto.setTitle("title");
        return dto;
    }

    @Test
    public void testUpdate() {
        try {
            SeoImageDTO dto = getDto();
            dto = dtoService.create(dto);
            assertTrue(dto.getSeoImageId() > 0);
            dto.setAlt("alt2");
            dto.setImageName("image1.jpeg2");
            dto.setTitle("title2");
            dto = dtoService.update(dto);
            assertEquals("alt2", dto.getAlt());
            assertEquals("image1.jpeg2", dto.getImageName());
            assertEquals("title2", dto.getTitle());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testRemove() {
        try {
            SeoImageDTO dto = getDto();
            dto = dtoService.create(dto);
            assertTrue(dto.getSeoImageId() > 0);
            long pk = dto.getSeoImageId();
            dtoService.remove(pk);
            dto = dtoService.getById(pk);
            assertNull(dto);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }
}
