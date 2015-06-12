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

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.DataGroup;
import org.yes.cart.service.domain.DataGroupService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 01/06/2015
 * Time: 17:35
 */
public class DataGroupServiceImplTest extends BaseCoreDBTestCase {

    private DataGroupService dataGroupService;


    @Before
    public void setUp() {
        dataGroupService = (DataGroupService) ctx().getBean("dataGroupService");
        super.setUp();
    }


    @Test
    public void testFindByType() throws Exception {

        assertTrue(dataGroupService.findByType(DataGroup.TYPE_IMPORT).isEmpty());

        final DataGroup group001 = dataGroupService.getGenericDao().getEntityFactory().getByIface(DataGroup.class);

        group001.setName("G001");
        group001.setType(DataGroup.TYPE_IMPORT);
        group001.setDescriptors("d1,d2,d3");

        dataGroupService.create(group001);

        final DataGroup group002 = dataGroupService.getGenericDao().getEntityFactory().getByIface(DataGroup.class);

        group002.setName("G002");
        group002.setType("OTHER");
        group002.setDescriptors("d1,d2,d3");

        dataGroupService.create(group002);

        final List<DataGroup> g001search = dataGroupService.findByType(DataGroup.TYPE_IMPORT);
        assertFalse(g001search.isEmpty());

        final DataGroup g001 = g001search.get(0);
        assertEquals("G001", g001.getName());

        final List<DataGroup> g002search = dataGroupService.findByType("OTHER");
        assertFalse(g002search.isEmpty());

        final DataGroup g002 = g002search.get(0);
        assertEquals("G002", g002.getName());

    }

    @Test
    public void testFindByName() throws Exception {

        assertNull(dataGroupService.findByName("G005"));

        final DataGroup group005 = dataGroupService.getGenericDao().getEntityFactory().getByIface(DataGroup.class);

        group005.setName("G005");
        group005.setType("FindByName");
        group005.setDescriptors("d1,d2,d3");

        dataGroupService.create(group005);

        final DataGroup g005search = dataGroupService.findByName("G005");
        assertNotNull(g005search);
        assertEquals("G005", g005search.getName());

    }


}
