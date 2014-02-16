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

package org.yes.cart.service.domain.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Association;
import org.yes.cart.service.domain.AssociationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AssociationServiceImplTest extends BaseCoreDBTestCase {

    private AssociationService associationService;
    private Set<Long> cleanupPks = new HashSet<Long>();

    @Before
    public void setUp() {
        associationService = (AssociationService) ctx().getBean(ServiceSpringKeys.ASSOCIATION_SERVICE);
        super.setUp();
    }

    @After
    public void cleanUp() {
        for (Long pk : cleanupPks) {
            associationService.delete(associationService.findById(pk));
            assertNull(associationService.findById(pk));
        }
    }

    @Test
    public void testFindAll() {
        List<Association> list = associationService.findAll();
        assertEquals(4, list.size());
    }

    @Test
    public void testCreate() {
        Association association = createAssociation();
        association = associationService.create(association);
        cleanupPks.add(association.getAssociationId());
        assertTrue(association.getAssociationId() > 0);
        List<Association> list = associationService.findAll();
        assertEquals(5, list.size());
    }

    @Test
    public void testUpdate() {
        Association association = createAssociation();
        association = associationService.create(association);
        cleanupPks.add(association.getAssociationId());
        assertTrue(association.getAssociationId() > 0);
        association.setCode("someCode2");
        association.setName("someName2");
        association.setDescription("someDescription2");
        association = associationService.update(association);
        assertEquals("someCode2", association.getCode());
        assertEquals("someName2", association.getName());
        assertEquals("someDescription2", association.getDescription());
    }

    @Test
    public void testDelete() {
        Association association = createAssociation();
        association = associationService.create(association);
        assertTrue(association.getAssociationId() > 0);
        long pk = association.getAssociationId();
        associationService.delete(association);
        association = associationService.findById(pk);
        assertNull(association);
    }

    private Association createAssociation() {
        Association association = associationService.getGenericDao().getEntityFactory().getByIface(Association.class);
        association.setCode("someCode");
        association.setName("someName");
        association.setDescription("someDescription");
        return association;
    }
}
