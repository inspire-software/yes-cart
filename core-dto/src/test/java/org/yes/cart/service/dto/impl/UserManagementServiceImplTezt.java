/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.dto.RoleDTO;
import org.yes.cart.domain.entity.Manager;
import org.yes.cart.service.domain.ManagerService;
import org.yes.cart.service.dto.ManagementService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class UserManagementServiceImplTezt extends BaseCoreDBTestCase {

    private ManagementService managementService;
    private ManagerService managerService;

    @Before
    public void setUp() {
        managementService = (ManagementService) ctx().getBean(ServiceSpringKeys.USER_MANAGMENT_SERVICE);
        managerService = (ManagerService) ctx().getBean(ServiceSpringKeys.MANAGER_SERVICE);
        super.setUp();
    }

    @Test
    public void testAddUser() throws Exception {
        int numberOfAllManagers = managementService.findManagers(createSearchContext(0, 1)).getTotal();
        managementService.addUser("bender", "bender@futurama.com", "Bender", "Rodríguez", null, null, null, "SHOIP1");
        managementService.addUser("optimus.prime", "optimus.prime@transformers.com", "Optimus", "Prime", null, null, null, "SHOIP1");
        managementService.addUser("megatron.beast", "megatron.beast@transformers.com", "Megatron", "Beast", null, null, null, "SHOIP1");
        assertEquals(3, managementService.findManagers(createSearchContext(0, 1)).getTotal() - numberOfAllManagers);
    }

    @Test
    public void testDeleteUser() throws Exception {
        int numberOfAllManagers = managementService.findManagers(createSearchContext(0, 1)).getTotal();
        managementService.addUser("bender2", "bender2@futurama.com", "Bender2", "Rodriguez2", null, null, null, "SHOIP1");
        assertEquals(1, managementService.findManagers(createSearchContext(0, 1)).getTotal() - numberOfAllManagers);
        managementService.deleteUser("bender2");
        assertEquals(0, managementService.findManagers(createSearchContext(0, 1)).getTotal() - numberOfAllManagers);
    }

    @Test
    public void testUpdateUser() throws Exception {
        managementService.addUser("bender3", "bender3@futurama.com", "Bender3", "Rodriguez3", null, null, null, "SHOIP1");
        assertEquals(1, managementService.findManagers(createSearchContext(0, 1,
                "filter", "bender3@futurama.com"
            )).getTotal());
        managementService.updateUser("bender3", "bender3@futurama.com", "Bender3", "Rodríguez3", null, null, null);
        assertEquals("Rodríguez3", managementService.findManagers(createSearchContext(0, 1,
                "filter", "bender3@futurama.com"
        )).getItems().get(0).getLastName());
    }


    @Test
    public void testUpdateUserId() throws Exception {
        managementService.addUser("bender3333", "bender3@futurama.com", "Bender3", "Rodriguez3", null, null, null, "SHOIP1");
        assertEquals(1, managementService.findManagers(createSearchContext(0, 1,
                "filter", "bender3333"
        )).getTotal());
        managementService.addRole("ROLE_3333", null);
        managementService.grantRole("bender3333", "ROLE_3333");
        managementService.updateUserId("bender3333", "bender4444");
        final ManagerDTO manager3333 = managementService.getManagerByLogin("bender3333");
        assertNull(manager3333);
        final ManagerDTO manager4444 = managementService.getManagerByLogin("bender4444");
        assertNotNull(manager4444);
        assertEquals("ROLE_3333", managementService.getAssignedManagerRoles("bender4444").get(0).getCode());
        managementService.deleteUser("bender4444");
        managementService.deleteRole("ROLE_3333");
    }


    @Test
    public void testResetPassword() throws Exception {
        managementService.addUser("bender4", "bender4@futurama.com", "Bender4", "Rodríguez4", null, null, null, "SHOIP1");
        String pwdHash = managerService.findAll().get(0).getPassword();
        assertNotNull("password can not be null ", pwdHash);
        assertTrue("password can not be empty ", pwdHash.length() > 0);
        managementService.resetPassword("bender4");
        String newPwdHash = managerService.findByLogin("bender4").getPassword();
        String newPwdHash2 = managerService.findByEmail("bender4@futurama.com").get(0).getPassword();
        assertEquals(newPwdHash, newPwdHash2);
        assertNotNull("new password can not be null ", newPwdHash);
        assertTrue("new password can not be empty ", newPwdHash.length() > 0);
        assertTrue("old and new pwd can not be the same " + newPwdHash, !pwdHash.equals(newPwdHash));
    }

    @Test
    public void testGetManagers() throws Exception {
        managementService.addUser("bender5", "bender5@futurama.com", "Bender5", "Rodríguez", null, null, null, "SHOIP1");
        managementService.addUser("optimus.prime5", "optimus.prime5@transformers.com", "Optimus", "Prime5", null, null, null, "SHOIP1");
        managementService.addUser("megatron.beast5", "megatron.beast5@transformers.com", "Megatron", "Beast", null, null, null, "SHOIP1");
        assertEquals(3, managementService.findManagers(createSearchContext(0, 1,
                "filter", "5"
        )).getTotal());
        List<ManagerDTO> rez;
        rez = managementService.findManagers(createSearchContext(0, 10,
                "filter", "asd"
        )).getItems();
        assertEquals(0, rez.size());
        rez = managementService.findManagers(createSearchContext(0, 10,
                "filter", "Bender5"
        )).getItems();
        assertEquals(1, rez.size());
        rez = managementService.findManagers(createSearchContext(0, 10,
                "filter", "Prime5"
        )).getItems();
        assertEquals(1, rez.size());
    }

    @Test
    public void testAddRole() throws Exception {
        final int init = managementService.getRolesList().size();
        managementService.addRole("ROLE_ZZZ", null);
        assertEquals(init + 1, managementService.getRolesList().size());
        managementService.deleteRole("ROLE_ZZZ");
    }

    @Test
    public void testUpdateRole() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    final int init = managementService.getRolesList().size();
                    managementService.addRole("ROLE_XXX", null);
                    assertEquals(init + 1, managementService.getRolesList().size());
                    managementService.updateRole("ROLE_XXX", "xxx");
                    assertEquals("xxx", managementService.getRolesList().stream().filter(role -> "ROLE_XXX".equals(role.getCode())).findFirst().get().getDescription());
                }   catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();

            }
        });


    }

    @Test
    public void testGrantRole() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    managementService.addUser("bende11r", "bende11r@futurama.com", "Bender", "Rodríguez", null, null, null, "SHOIP1");
                    managementService.addRole("ROLE_CCC", null);
                    managementService.grantRole("bende11r", "ROLE_CCC");
                    assertEquals("ROLE_CCC", managementService.getAssignedManagerRoles("bende11r").get(0).getCode());
                }   catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();

            }
        });

    }

    @Test
    public void testRevokeRole() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    managementService.addUser("bender12", "bender12@futurama.com", "Bender", "Rodríguez", null, null, null, "SHOIP1");
                    managementService.addRole("ROLE_VVV", null);
                    managementService.grantRole("bender12", "ROLE_VVV");
                    assertEquals("ROLE_VVV", managementService.getAssignedManagerRoles("bender12").get(0).getCode());
                    managementService.revokeRole("bender12", "ROLE_VVV");
                    assertEquals(0, managementService.getAssignedManagerRoles("bender12").size());
                }   catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();

            }
        });


    }

    @Test
    public void testDeleteRole() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    managementService.addUser("bender13", "bender13@futurama.com", "Bender", "Rodríguez", null, null, null, "SHOIP1");
                    managementService.addRole("ROLE_AAA", null);
                    managementService.grantRole("bender13", "ROLE_AAA");
                    assertEquals("ROLE_AAA", managementService.getAssignedManagerRoles("bender13").get(0).getCode());
                    managementService.deleteRole("ROLE_AAA");
                    for (RoleDTO role : managementService.getRolesList()) {
                        assertFalse("ROLE_AAA".equals(role.getCode()));
                    }
                    assertEquals(0, managementService.getAssignedManagerRoles("bender13").size());
                }   catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();

            }
        });


    }
}

