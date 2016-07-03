/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.domain.dto.ProductTypeDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoProductTypeService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 23/02/2016
 * Time: 15:36
 */
public class DtoProductTypeServiceImplTezt extends BaseCoreDBTestCase {

    private DtoProductTypeService dtoService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() {
        dtoService = (DtoProductTypeService) ctx().getBean(DtoServiceSpringKeys.DTO_PRODUCT_TYPE_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(DtoServiceSpringKeys.DTO_FACTORY);
        super.setUp();
    }

    @Test
    public void testFindProductTypes() throws Exception {

        List<ProductTypeDTO> list = dtoService.findProductTypes("Play");
        assertFalse(list.isEmpty());

        for (final ProductTypeDTO typeDTO : list) {
            assertTrue(
                    (typeDTO.getName() != null && typeDTO.getName().toLowerCase().contains("play")) ||
                            (typeDTO.getDescription() != null && typeDTO.getDescription().toLowerCase().contains("play"))
            );
        }

    }


    @Test
    public void testFindByAttributeCode() throws Exception {

        List<ProductTypeDTO> list = dtoService.findByAttributeCode("MATERIAL");
        assertFalse(list.isEmpty());

        final Set<String> names = new HashSet<String>();
        for (final ProductTypeDTO type : list) {
            names.add(type.getName());
        }

        assertTrue(names.contains("Robots"));

    }
}