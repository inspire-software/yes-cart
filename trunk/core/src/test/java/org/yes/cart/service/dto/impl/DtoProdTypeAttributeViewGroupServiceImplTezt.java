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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ProdTypeAttributeViewGroupDTO;
import org.yes.cart.domain.dto.impl.ProdTypeAttributeViewGroupDTOImpl;
import org.yes.cart.service.dto.DtoProdTypeAttributeViewGroupService;
import org.yes.cart.service.dto.DtoProductTypeService;

import java.util.List;

import static junit.framework.Assert.*;

/**
 * User: igora Igor Azarny
 * Date: 6/28/12
 * Time: 10:19 AM
 */
public class DtoProdTypeAttributeViewGroupServiceImplTezt  extends BaseCoreDBTestCase {


    private DtoProductTypeService dtoProductTypeService;
    private DtoProdTypeAttributeViewGroupService dtoProdTypeAttributeViewGroupService;


    @Before
    public void setUp() throws Exception {

        dtoProductTypeService = (DtoProductTypeService) ctx().getBean(ServiceSpringKeys.DTO_PRODUCT_TYPE_SERVICE);
        dtoProdTypeAttributeViewGroupService = (DtoProdTypeAttributeViewGroupService) ctx().getBean(ServiceSpringKeys.DTO_PRODUCT_TYPE_AV_SERVICE);


    }


    @After
    public void tearDown() throws Exception {
        dtoProductTypeService =  null;
        dtoProdTypeAttributeViewGroupService = null;
    }


    @Test
    public void testGetByProductTypeId() throws Exception {
        List<ProdTypeAttributeViewGroupDTO> rez = dtoProdTypeAttributeViewGroupService.getByProductTypeId(1);
        assertEquals(3, rez.size());

        rez = dtoProdTypeAttributeViewGroupService.getByProductTypeId(5);
        assertEquals(0, rez.size());
    }

    @Test
    public void testCRUD() throws Exception {
        
        ProdTypeAttributeViewGroupDTO dto = new ProdTypeAttributeViewGroupDTOImpl();
        dto.setAttrCodeList("A,B,C");
        dto.setName("test name");
        dto.setProducttypeId(5);
        dto.setRank(300);
        
        dto = dtoProdTypeAttributeViewGroupService.create(dto);
        assertTrue("Dto must be persistent and has id after create operation", dto.getProdTypeAttributeViewGroupId() > 0);

        dto = dtoProdTypeAttributeViewGroupService.getById(dto.getProdTypeAttributeViewGroupId());
        assertNotNull("Dto must be retreived by id", dto);
        
        dto.setRank(1000);
        dto = dtoProdTypeAttributeViewGroupService.update(dto);
        assertEquals(1000, dto.getRank());


        dtoProdTypeAttributeViewGroupService.remove(dto.getId());

        dto = dtoProdTypeAttributeViewGroupService.getById(dto.getProdTypeAttributeViewGroupId());
        assertNull("Dto is deleted and can not be find by id", dto);


    }


}
