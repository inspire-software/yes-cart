/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.service.vo.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoDataDescriptor;
import org.yes.cart.domain.vo.VoDataGroup;
import org.yes.cart.service.vo.VoDataGroupService;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 24/09/2019
 * Time: 15:46
 */
public class VoDataGroupServiceImplTest extends BaseCoreDBTestCase {

    private VoDataGroupService voDataGroupService;

    @Before
    public void setUp() {
        voDataGroupService = (VoDataGroupService) ctx().getBean("voDataGroupService");
        super.setUp();
    }

    @Test
    public void testGroupsCRUD() throws Exception {

        final VoDataGroup dataGroup = new VoDataGroup();
        dataGroup.setName("TEST CRUD");
        dataGroup.setType("TEST CRUD");
        dataGroup.setDisplayNames(Collections.singletonList(MutablePair.of("en", "Test")));
        dataGroup.setDescriptors("a,b,c");

        final VoDataGroup created = voDataGroupService.createDataGroup(dataGroup);
        assertTrue(created.getDatagroupId() > 0L);

        VoDataGroup afterCreated = voDataGroupService.getDataGroupById(created.getDatagroupId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());

        created.setName("TEST CRUD UPDATE");

        final VoDataGroup updated = voDataGroupService.updateDataGroup(created);
        assertEquals("TEST CRUD UPDATE", updated.getName());

        assertTrue(voDataGroupService.getAllDataGroups().stream().anyMatch(group -> group.getDatagroupId() == updated.getDatagroupId()));

        voDataGroupService.removeDataGroup(updated.getDatagroupId());

        assertFalse(voDataGroupService.getAllDataGroups().stream().anyMatch(group -> group.getDatagroupId() == updated.getDatagroupId()));

    }

    @Test
    public void testDescriptorsCRUD() throws Exception {

        final VoDataDescriptor descriptor = new VoDataDescriptor();
        descriptor.setName("TEST CRUD");
        descriptor.setType("TEST CRUD");
        descriptor.setValue("ABC");

        final VoDataDescriptor created = voDataGroupService.createDataDescriptor(descriptor);
        assertTrue(created.getDatadescriptorId() > 0L);

        VoDataDescriptor afterCreated = voDataGroupService.getDataDescriptorById(created.getDatadescriptorId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());

        created.setName("TEST CRUD UPDATE");

        final VoDataDescriptor updated = voDataGroupService.updateDataDescriptor(created);
        assertEquals("TEST CRUD UPDATE", updated.getName());

        assertTrue(voDataGroupService.getAllDataDescriptors().stream().anyMatch(desc -> desc.getDatadescriptorId() == updated.getDatadescriptorId()));

        voDataGroupService.removeDataDescriptor(updated.getDatadescriptorId());

        assertFalse(voDataGroupService.getAllDataDescriptors().stream().anyMatch(desc -> desc.getDatadescriptorId() == updated.getDatadescriptorId()));

    }
}