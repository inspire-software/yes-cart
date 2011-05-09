package org.yes.cart.service.dto.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AttrValueCategoryDTO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCategoryService;

import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCategoryServiceImplTest  extends BaseCoreDBTestCase {

    private DtoFactory dtoFactory = null;
    private DtoCategoryService dtoService = null;
    private DtoAttributeService dtoAttrService = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
        dtoService = (DtoCategoryService) ctx.getBean(ServiceSpringKeys.DTO_CATEGORY_SERVICE);
        dtoAttrService  = (DtoAttributeService) ctx.getBean(ServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);


    }

    @After
    public void tearDown() {
        dtoFactory = null;
        dtoService = null;
        dtoAttrService = null;
        super.tearDown();
    }

    @Test
    public void testGetAll() {
        try {
            List<CategoryDTO> list = dtoService.getAll();
            assertFalse(list.isEmpty());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetAllWithAvailabilityFilter() {
        try {
            List<CategoryDTO> list = dtoService.getAllWithAvailabilityFilter(true);
            assertFalse(list.isEmpty());
            assertFalse(isCategoryPresent(list, 141L));  //xmas category 2008
            assertFalse(isCategoryPresent(list, 142L));  //xmas category 2108
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    private boolean isCategoryPresent(final List<CategoryDTO> list, final long pk) {
        for (CategoryDTO dto : list) {
            if (dto.getCategoryId() == pk) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testCreate() {
        CategoryDTO dto = getDto();
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getCategoryId() > 0);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }


    @Test
    public void testUpdate() {
        CategoryDTO dto = getDto();
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getCategoryId() > 0);
            dto.setDescription("description");
            dto = dtoService.update(dto);
            assertEquals("description", dto.getDescription());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testRemove() {
        CategoryDTO dto = getDto();
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getCategoryId() > 0);
            long id = dto.getCategoryId();
            dtoService.remove(id);
            dto = dtoService.getById(id);
            assertNull(dto);

        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetAllByShopId() {
        try {
            List<CategoryDTO> list = dtoService.getAllByShopId(50L);
            assertFalse(list.isEmpty());
            assertEquals(1, list.size());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testAssignToShop() {
        CategoryDTO dto = getDto();
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getCategoryId() > 0);
            dtoService.assignToShop(dto.getCategoryId(), 50L);
            List<CategoryDTO> list = dtoService.getAllByShopId(50L);
            assertEquals(2, list.size());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testUnassignFromShop() {
        CategoryDTO dto = getDto();
        try {
            dto = dtoService.create(dto);
            assertTrue(dto.getCategoryId() > 0);
            dtoService.assignToShop(dto.getCategoryId(), 50L);
            List<CategoryDTO> list = dtoService.getAllByShopId(50L);
            assertEquals(2, list.size());
            dtoService.unassignFromShop(dto.getCategoryId(), 50L);
            list = dtoService.getAllByShopId(50L);
            assertEquals(1, list.size());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testGetEntityAttributes() {
        // Add your code here
        //<TCATEGORYATTRVALUE ATTRVALUE_ID="20" CODE="CATEGORY_ITEMS_PER_PAGE" VAL="6,12,24" CATEGORY_ID="100"/>
        try {
            List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(100L);
            assertEquals(3, list.size());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testUpdateEntityAttributeValue() {
        try {
            List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(100L);

            for (AttrValueDTO dto : list) {
                if (dto.getAttributeDTO().getCode().equals("CATEGORY_ITEMS_PER_PAGE")) {
                    dto.setVal("5,15,35"); // default value in test data is 6,12,24
                    dtoService.updateEntityAttributeValue(dto);
                    break;
                }
            }

            list = dtoService.getEntityAttributes(100L);

            for (AttrValueDTO dto : list) {
                if (dto.getAttributeDTO().getCode().equals("CATEGORY_ITEMS_PER_PAGE")) {
                    assertEquals("5,15,35", dto.getVal());
                }                
            }
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testDeleteAttributeValue() {
        try {
            final int qty = 3;
            List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(100L);
            assertEquals(qty, list.size());
            for (int i = 0; i< qty; i++) {
                AttrValueDTO dto  = list.get(i);
                if (dto.getVal() != null) {
                    dtoService.deleteAttributeValue(dto.getAttrvalueId());
                }
            }

            list = dtoService.getEntityAttributes(100L);
            assertEquals(qty, list.size());
            for (AttrValueDTO dto : list) {
                assertNull(dto.getVal());
            }
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testCreateEntityAttributeValue() {
        try {
            final int qty = 3;
            List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(100L);
            assertEquals(3, list.size());
            for (int i = 0; i< qty; i++) {
                AttrValueDTO dto  = list.get(i);
                if (dto.getVal() != null) {
                    dtoService.deleteAttributeValue(dto.getAttrvalueId());
                }
            }
            
            list = dtoService.getEntityAttributes(100L);
            assertEquals(qty, list.size());
            for (AttrValueDTO dto : list) {
                assertNull(dto.getVal());
            }
            AttrValueCategoryDTO attrValueCategory = dtoFactory.getByIface(AttrValueCategoryDTO.class);
            attrValueCategory.setAttributeDTO(dtoAttrService.getById(1002L));//CATEGORY_ITEMS_PER_PAGE
            attrValueCategory.setVal("1,2,3");
            attrValueCategory.setCategoryId(100L);
            dtoService.createEntityAttributeValue(attrValueCategory);

            attrValueCategory = dtoFactory.getByIface(AttrValueCategoryDTO.class);
            attrValueCategory.setAttributeDTO(dtoAttrService.getById(1004L));//URI
            attrValueCategory.setVal("val2");
            attrValueCategory.setCategoryId(100L);
            dtoService.createEntityAttributeValue(attrValueCategory);

            list = dtoService.getEntityAttributes(100L);
            assertEquals(qty, list.size());
            for (AttrValueDTO dto : list) {
                if (dto.getAttributeDTO().getCode().equals("CATEGORY_ITEMS_PER_PAGE")) {
                    assertEquals("1,2,3", dto.getVal());
                } else if (dto.getAttributeDTO().getCode().equals("URI")) {
                    assertEquals("val2", dto.getVal());

                }
            }

            
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    private CategoryDTO getDto() {
        CategoryDTO dto = dtoFactory.getByIface(CategoryDTO.class);
        dto.setName("testcategory");
        dto.setParentId(100L);
        return dto;
    }


}
