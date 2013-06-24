/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AttrValueCategoryDTO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCategoryService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCategoryServiceImplTezt extends BaseCoreDBTestCase {

    private DtoFactory dtoFactory;
    private DtoCategoryService dtoService;
    private DtoAttributeService dtoAttrService;
    public static final int QTY = 3;

    @Before
    public void setUp() {
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
        dtoService = (DtoCategoryService) ctx().getBean(ServiceSpringKeys.DTO_CATEGORY_SERVICE);
        dtoAttrService = (DtoAttributeService) ctx().getBean(ServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
        super.setUp();
    }

    @Test
    public void testGetAll() throws Exception {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    List<CategoryDTO> list = dtoService.getAll();
                    assertFalse(list.isEmpty());
                } catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();
            }
        });


    }

    @Test
    public void testGetAllWithAvailabilityFilter() throws Exception {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    List<CategoryDTO> list = dtoService.getAllWithAvailabilityFilter(true);
                    assertFalse(list.isEmpty());
                    assertFalse(isCategoryPresent(list, 141L));  //xmas category 2008
                    assertFalse(isCategoryPresent(list, 142L));  //xmas category 2108
                } catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();
            }
        });


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
    public void testCreate() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    CategoryDTO dto = getDto();
                    dto = dtoService.create(dto);
                    assertTrue(dto.getCategoryId() > 0);
                } catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();
            }
        });


    }

    @Test
    public void testUpdate() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    CategoryDTO dto = getDto();
                    dto = dtoService.create(dto);
                    assertTrue(dto.getCategoryId() > 0);
                    dto.setDescription("description");
                    dto = dtoService.update(dto);
                    assertEquals("description", dto.getDescription());
                } catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();
            }
        });


    }

    @Test
    public void testRemove() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    CategoryDTO dto = getDto();
                    dto = dtoService.create(dto);
                    assertTrue(dto.getCategoryId() > 0);
                    long id = dto.getCategoryId();
                    dtoService.remove(id);
                    dto = dtoService.getById(id);
                    assertNull(dto);
                } catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();
            }
        });


    }

    @Test
    public void testGetAllByShopId() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    List<CategoryDTO> list = dtoService.getAllByShopId(50L);
                    assertFalse(list.isEmpty());
                    assertEquals(1, list.size());
                } catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();
            }
        });


    }

    @Test
    public void testAssignAndUnassignToShop() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    CategoryDTO dto = getDto();
                    dto = dtoService.create(dto);
                    assertTrue(dto.getCategoryId() > 0);
                    dtoService.assignToShop(dto.getCategoryId(), 50L);
                    List<CategoryDTO> list = dtoService.getAllByShopId(50L);
                    assertEquals(2, list.size());
                    dtoService.unassignFromShop(dto.getCategoryId(), 50L);
                    list = dtoService.getAllByShopId(50L);
                    assertEquals(1, list.size());
                    dtoService.remove(dto.getCategoryId());
                } catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                //status.setRollbackOnly();
            }
        });


    }



    @Test
    public void testUpdateEntityAttributeValue() throws Exception {


        final List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(100L);
        for (AttrValueDTO dto : list) {
            if (dto.getAttributeDTO().getCode().equals("CATEGORY_ITEMS_PER_PAGE")) {
                dto.setVal("5,15,35"); // default value in test data is 6,12,24
                AttrValueDTO dtoUpdated = dtoService.updateEntityAttributeValue(dto);
                assertEquals("5,15,35", dtoUpdated.getVal());
                break;
            }
        }



    }

    @Test
    public void testDeleteAttributeValue() throws Exception {

        final List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(100L);
        final int totalSize = list.size();

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {


                    for (int i = 0; i < totalSize; i++) {
                        AttrValueDTO dto = list.get(i);
                        if (dto.getVal() != null) {
                            dtoService.deleteAttributeValue(dto.getAttrvalueId());
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    assertTrue(e.getMessage(), false);

                }

                //status.setRollbackOnly();
            }
        });

        final List<? extends AttrValueDTO> list2 = dtoService.getEntityAttributes(100L);
        //assertEquals(totalSize, list2.size());
        for (AttrValueDTO dto : list2) {
            assertNull(dto.getVal());
        }


    }

    @Test
    public void testzCreateEntityAttributeValue() throws Exception {
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(100L);
        assertEquals(3, list.size());
        for (int i = 0; i < QTY; i++) {
            AttrValueDTO dto = list.get(i);
            if (dto.getVal() != null) {
                dtoService.deleteAttributeValue(dto.getAttrvalueId());
            }
        }
        list = dtoService.getEntityAttributes(100L);
        assertEquals(QTY, list.size());
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
        assertEquals(QTY, list.size());
        for (AttrValueDTO dto : list) {
            if (dto.getAttributeDTO().getCode().equals("CATEGORY_ITEMS_PER_PAGE")) {
                assertEquals("1,2,3", dto.getVal());
            } else if (dto.getAttributeDTO().getCode().equals("URI")) {
                assertEquals("val2", dto.getVal());

            }
        }


        for (AttrValueDTO dto : dtoService.getEntityAttributes(100L)) {
            if (dto.getAttributeDTO().getCode().equals("CATEGORY_ITEMS_PER_PAGE")) {
                dto.setVal("6,12,24");
                dtoService.updateEntityAttributeValue(dto);
                break;
            }
        }
        /* getTx().execute(new TransactionCallbackWithoutResult() {
          public void doInTransactionWithoutResult(TransactionStatus status) {
              try {


              }   catch (Exception e) {
                  assertTrue(e.getMessage(), false);

              }

              status.setRollbackOnly();
          }
      });  */

    }

    private CategoryDTO getDto() {
        CategoryDTO dto = dtoFactory.getByIface(CategoryDTO.class);
        dto.setName("testcategory");
        dto.setParentId(100L);
        return dto;
    }
}