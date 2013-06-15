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
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.domain.dto.RoleDTO;
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
        int numberOfAllManagers = managementService.getManagers(null, null, null).size();
        managementService.addUser("bender@futurama.com", "Bender", "Rodríguez");
        managementService.addUser("optimus.prime@transformers.com", "Optimus", "Prime");
        managementService.addUser("megatron.beast@transformers.com", "Megatron", "Beast");
        assertEquals(3, managementService.getManagers(null, null, null).size() - numberOfAllManagers);
    }

    @Test
    public void testDeleteUser() throws Exception {
        int numberOfAllManagers = managementService.getManagers(null, null, null).size();
        managementService.addUser("bender2@futurama.com", "Bender2", "Rodriguez2");
        assertEquals(1, managementService.getManagers(null, null, null).size() - numberOfAllManagers);
        managementService.deleteUser("bender2@futurama.com");
        assertEquals(0, managementService.getManagers(null, null, null).size() - numberOfAllManagers);
    }

    @Test
    public void testUpdateUser() throws Exception {
        managementService.addUser("bender3@futurama.com", "Bender3", "Rodriguez3");
        assertEquals(1, managementService.getManagers("bender3@futurama.com", null, null).size());
        managementService.updateUser("bender3@futurama.com", "Bender3", "Rodríguez3");
        assertEquals("Rodríguez3", managementService.getManagers("bender3@futurama.com", null, null).get(0).getLastName());
    }

    @Test
    public void testResetPassword() throws Exception {
        managementService.addUser("bender4@futurama.com", "Bender4", "Rodríguez4");
        String pwdHash = managerService.findAll().get(0).getPassword();
        assertNotNull("password can not be null ", pwdHash);
        assertTrue("password can not be empty ", pwdHash.length() > 0);
        managementService.resetPassword("bender4@futurama.com");
        String newPwdHash = managerService.findByEmail("bender4@futurama.com").get(0).getPassword();
        assertNotNull("new password can not be null ", newPwdHash);
        assertTrue("new password can not be empty ", newPwdHash.length() > 0);
        assertTrue("old and new pwd can not be the same " + newPwdHash, !pwdHash.equals(newPwdHash));
    }

    @Test
    public void testGetManagers() throws Exception {
        managementService.addUser("bender5@futurama.com", "Bender5", "Rodríguez");
        managementService.addUser("optimus.prime5@transformers.com", "Optimus", "Prime5");
        managementService.addUser("megatron.beast5@transformers.com", "Megatron", "Beast");
        assertEquals(3, managementService.getManagers("5", null, null).size());
        List<ManagerDTO> rez;
        rez = managementService.getManagers("asd", null, "");
        assertEquals(0, rez.size());
        rez = managementService.getManagers(null, "Bender5", null);
        assertEquals(1, rez.size());
        rez = managementService.getManagers(null, null, "Prime5");
        assertEquals(1, rez.size());
        rez = managementService.getManagers(null, "us", "me5");
        assertEquals(1, rez.size());
    }

    @Test
    public void testAddRole() throws Exception {
        managementService.addRole("ROLE_ZZZ", null);
        assertEquals(1, managementService.getRolesList().size());
        managementService.deleteRole("ROLE_ZZZ");
    }

    @Test
    public void testUpdateRole() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    managementService.addRole("ROLE_XXX", null);
                    assertEquals(1, managementService.getRolesList().size());
                    managementService.updateRole("ROLE_XXX", "xxx");
                    assertEquals("xxx", managementService.getRolesList().get(0).getDescription());
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
                    managementService.addUser("bende11r@futurama.com", "Bender", "Rodríguez");
                    managementService.addRole("ROLE_CCC", null);
                    managementService.grantRole("bende11r@futurama.com", "ROLE_CCC");
                    assertEquals("ROLE_CCC", managementService.getAssignedManagerRoles("bende11r@futurama.com").get(0).getCode());
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
                    managementService.addUser("bender12@futurama.com", "Bender", "Rodríguez");
                    managementService.addRole("ROLE_VVV", null);
                    managementService.grantRole("bender12@futurama.com", "ROLE_VVV");
                    assertEquals("ROLE_VVV", managementService.getAssignedManagerRoles("bender12@futurama.com").get(0).getCode());
                    managementService.revokeRole("bender12@futurama.com", "ROLE_VVV");
                    assertEquals(0, managementService.getAssignedManagerRoles("bender@futurama.com").size());
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
                    managementService.addUser("bender13@futurama.com", "Bender", "Rodríguez");
                    managementService.addRole("ROLE_AAA", null);
                    managementService.grantRole("bender13@futurama.com", "ROLE_AAA");
                    assertEquals("ROLE_AAA", managementService.getAssignedManagerRoles("bender13@futurama.com").get(0).getCode());
                    managementService.deleteRole("ROLE_AAA");
                    for (RoleDTO role : managementService.getRolesList()) {
                        assertFalse("ROLE_AAA".equals(role.getCode()));
                    }
                    assertEquals(0, managementService.getAssignedManagerRoles("bender13@futurama.com").size());
                }   catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();

            }
        });


    }
}

