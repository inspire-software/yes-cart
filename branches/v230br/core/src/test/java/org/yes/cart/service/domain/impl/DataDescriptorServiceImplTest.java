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
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.service.domain.DataDescriptorService;
import org.yes.cart.service.domain.DataGroupService;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 01/06/2015
 * Time: 17:45
 */
public class DataDescriptorServiceImplTest extends BaseCoreDBTestCase {

    private DataDescriptorService dataDescriptorService;


    @Before
    public void setUp() {
        dataDescriptorService = (DataDescriptorService) ctx().getBean("dataDescriptorService");
        super.setUp();
    }


    @Test
    public void testFindByNames() throws Exception {

        assertTrue(dataDescriptorService.findByNames(Arrays.asList("D001", "D002")).isEmpty());

        final DataDescriptor desc001 = dataDescriptorService.getGenericDao().getEntityFactory().getByIface(DataDescriptor.class);

        desc001.setName("D001");
        desc001.setType(DataDescriptor.TYPE_WEBINF_XML);
        desc001.setValue("attributenames.xml");

        dataDescriptorService.create(desc001);

        final DataDescriptor desc002 = dataDescriptorService.getGenericDao().getEntityFactory().getByIface(DataDescriptor.class);

        desc002.setName("D002");
        desc002.setType(DataDescriptor.TYPE_WEBINF_XML);
        desc002.setValue("attributenames.xml");

        dataDescriptorService.create(desc002);

        final DataDescriptor desc003 = dataDescriptorService.getGenericDao().getEntityFactory().getByIface(DataDescriptor.class);

        desc003.setName("D003");
        desc003.setType(DataDescriptor.TYPE_WEBINF_XML);
        desc003.setValue("attributenames.xml");

        dataDescriptorService.create(desc003);

        final List<DataDescriptor> search = dataDescriptorService.findByNames(Arrays.asList("D001", "D002"));

        assertFalse(search.isEmpty());
        assertEquals(2, search.size());

        for (final DataDescriptor dd : search) {
            assertTrue("D001".equals(dd.getName()) || "D002".equals(dd.getName()));
        }


    }

    @Test
    public void testFindByName() throws Exception {

        assertNull(dataDescriptorService.findByName("D004"));

        final DataDescriptor desc004 = dataDescriptorService.getGenericDao().getEntityFactory().getByIface(DataDescriptor.class);

        desc004.setName("D004");
        desc004.setType(DataDescriptor.TYPE_WEBINF_XML);
        desc004.setValue("attributenames.xml");

        dataDescriptorService.create(desc004);

        final DataDescriptor search = dataDescriptorService.findByName("D004");

        assertNotNull(search);
        assertEquals("D004", search.getName());

    }
}
