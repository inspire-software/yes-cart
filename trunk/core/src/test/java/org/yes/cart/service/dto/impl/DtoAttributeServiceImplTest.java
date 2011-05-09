package org.yes.cart.service.dto.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoAttributeService;

import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAttributeServiceImplTest extends BaseCoreDBTestCase {

    private DtoAttributeService dtoAttributeService = null;
    private DtoFactory dtoFactory = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dtoAttributeService = (DtoAttributeService) ctx.getBean(ServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
    }

    @After
    public void tearDown() {
        dtoAttributeService = null;
        dtoFactory = null;
        super.tearDown();
    }


    @Test
    public void testCreate() {
        AttributeDTO dto = getDto();
        try {
            dto = dtoAttributeService.create(dto);
            assertTrue(dto.getAttributeId() > 0);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }


    @Test
    public void testUpdate() {
        AttributeDTO dto = getDto();
        try {
            dto = dtoAttributeService.create(dto);
            long id = dto.getAttributeId();
            assertTrue(dto.getAttributeId() > 0);
            assertEquals("string value", dto.getVal());
            dto.setVal("i love you meat bags");
            dtoAttributeService.update(dto);
            dto = dtoAttributeService.getById(id);
            assertEquals("i love you meat bags", dto.getVal());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testFindByAttributeGroupCode() {
        try {

            List<AttributeDTO> dtos = dtoAttributeService.findByAttributeGroupCode("CATEGORY");
            assertNotNull(dtos);
            assertEquals(3, dtos.size());

            dtos = dtoAttributeService.findByAttributeGroupCode("NONEXISTINGGROUP");
            assertNotNull(dtos);
            assertTrue(dtos.isEmpty());

        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testFindAvailableAttributes() {
        try {
            List<AttributeDTO> dtos = dtoAttributeService.findAvailableAttributes(
                    "CATEGORY",
                    Collections.singletonList("CATEGORY_ITEMS_PER_PAGE")
                    );
            assertNotNull(dtos);
            assertEquals(2, dtos.size());
            for (AttributeDTO dto : dtos ) {
                assertFalse("CATEGORY_ITEMS_PER_PAGE".equals(dto.getCode()));
            }

        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testFindAttributesWithMultipleValues() {
        //
        try {
            List<AttributeDTO> dtos = dtoAttributeService.findAttributesWithMultipleValues(
                    "PRODUCT");
            assertNotNull(dtos);
            assertFalse(dtos.isEmpty());
            for (AttributeDTO dto : dtos) {
                assertTrue(dto.isAllowduplicate());
            }

            dtos = dtoAttributeService.findAttributesWithMultipleValues(
                    "SYSTEM");
            assertNull(dtos);
            
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    private AttributeDTO getDto() {
        AttributeDTO dto = dtoFactory.getByIface(AttributeDTO.class);
        dto.setCode("TESTCODE");
        dto.setMandatory(true);
        dto.setVal("string value");
        dto.setName("test attr");
        dto.setDescription("test attr description");
        dto.setEtypeId(1000L); //string
        dto.setAttributegroupId(1006L); //customer
        dto.setAllowduplicate(true);
        dto.setAllowfailover(false);
        dto.setRegexp("[a-zA-Z]");
        return dto;
    }


}
