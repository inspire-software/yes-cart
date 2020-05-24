/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.domain.vo.VoRole;
import org.yes.cart.service.vo.VoRoleService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 29/09/2019
 * Time: 11:09
 */
public class VoRoleServiceImplTest extends BaseCoreDBTestCase {

    private VoRoleService voRoleService;

    @Before
    public void setUp() {
        voRoleService = (VoRoleService) ctx().getBean("voRoleService");
        super.setUp();
    }

    @Test
    public void testGetRoles() throws Exception {

        final List<VoRole> roles = voRoleService.getAllRoles();
        assertNotNull(roles);
        assertFalse(roles.isEmpty());

    }

    @Test
    public void testRoleCRUD() throws Exception {

        final VoRole role = new VoRole();
        role.setCode("TESTCRUD_ROLE");

        final VoRole created = voRoleService.createRole(role);
        assertTrue(created.getRoleId() > 0L);

        VoRole afterCreated = voRoleService.getAllRoles().stream().filter(rl -> rl.getRoleId() == created.getRoleId()).findFirst().get();
        assertNotNull(afterCreated);
        assertEquals("TESTCRUD_ROLE", afterCreated.getCode());
        assertNull(afterCreated.getDescription());

        afterCreated.setDescription("Test");

        final VoRole updated = voRoleService.updateRole(afterCreated);
        assertEquals("Test", updated.getDescription());

        assertTrue(voRoleService.getAllRoles().stream().anyMatch(rl -> rl.getRoleId() == created.getRoleId()));

        voRoleService.removeRole(updated.getCode());

        assertFalse(voRoleService.getAllRoles().stream().anyMatch(rl -> rl.getRoleId() == created.getRoleId()));

    }


}