package org.yes.cart.service.dto.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ManagerDTO;
import org.yes.cart.service.domain.ManagerService;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.ManagementService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class UserManagmentServiceImplTest extends BaseCoreDBTestCase {

    private ManagementService managementService;
    private ManagerService managerService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        managementService = (ManagementService) ctx.getBean(ServiceSpringKeys.USER_MANAGMENT_SERVICE);
        managerService  = (ManagerService) ctx.getBean(ServiceSpringKeys.MANAGER_SERVICE);
    }

    @After
    public void tearDown() {
        managementService = null;
        managerService = null;
        super.tearDown();
    }

    @Test
    public void testAddUser() throws Exception {

        managementService.addUser("bender@futurama.com", "Bender", "Rodríguez");
        managementService.addUser("optimus.prime@transformers.com", "Optimus", "Prime");
        managementService.addUser("megatron.beast@transformers.com", "Megatron", "Beast");

        assertEquals(3, managementService.getManagers(null,null,null).size());

    }

    @Test
    public void testDeleteUser() throws Exception {
        managementService.addUser("bender@futurama.com", "Bender", "Rodriguez");
        assertEquals(1, managementService.getManagers(null,null,null).size());
        managementService.deleteUser("bender@futurama.com");
        assertEquals(0, managementService.getManagers(null,null,null).size());
    }

    @Test
    public void testUpdateUser() throws Exception {

        managementService.addUser("bender@futurama.com", "Bender", "Rodriguez");
        assertEquals(1, managementService.getManagers(null,null,null).size());
        managementService.updateUser("bender@futurama.com", "Bender", "Rodríguez" );
        assertEquals("Rodríguez", managementService.getManagers(null,null,null).get(0).getLastName());
        assertTrue(!"Rodriguez".equals(managementService.getManagers(null,null,null).get(0).getLastName()));

    }

    @Test
    public void testResetPassword() throws Exception {

        managementService.addUser("bender@futurama.com", "Bender", "Rodríguez");
        String pwdHash = managerService.findAll().get(0).getPassword();
        assertNotNull("password can not be null ", pwdHash);
        assertTrue("password can not be empty ", pwdHash.length() > 0);
        managementService.resetPassword("bender@futurama.com");
        String newPwdHash = managerService.findAll().get(0).getPassword();
        assertNotNull("new password can not be null ", newPwdHash);
        assertTrue("new password can not be empty ", newPwdHash.length() > 0);

        assertTrue("old and new pwd can not be the same " + newPwdHash, !pwdHash.equals(newPwdHash));

    }


    @Test
    public void testGetManagers() throws Exception {

        managementService.addUser("bender@futurama.com", "Bender", "Rodríguez");
        managementService.addUser("optimus.prime@transformers.com", "Optimus", "Prime");
        managementService.addUser("megatron.beast@transformers.com", "Megatron", "Beast");

        assertEquals(3, managementService.getManagers(null,null,null).size());
        List<ManagerDTO> rez = managementService.getManagers("der",null,"");
        assertEquals(1,rez.size());
        assertEquals("bender@futurama.com", rez.get(0).getEmail());

        rez = managementService.getManagers("form",null,"");
        assertEquals(2,rez.size());
        assertTrue(!"bender@futurama.com".equals(rez.get(0).getEmail()));
        assertTrue(!"bender@futurama.com".equals(rez.get(1).getEmail()));
        assertTrue(!rez.get(0).getEmail().equals(rez.get(1).getEmail()));

        rez = managementService.getManagers("asd",null,"");
        assertEquals(0,rez.size());


        rez = managementService.getManagers(null,"Bender",null);
        assertEquals(1,rez.size());

        rez = managementService.getManagers(null,null,"Prime");
        assertEquals(1,rez.size());

        rez = managementService.getManagers(null,"us","me");
        assertEquals(1,rez.size());

    }

    @Test
    public void testAddRole() throws Exception {

        managementService.addRole("ROLE_ZZZ", null);
        assertEquals(1, managementService.getRolesList().size());

    }

    @Test
    public void testUpdateRole() throws Exception {

        managementService.addRole("ROLE_ZZZ", null);
        assertEquals(1, managementService.getRolesList().size());


        managementService.updateRole("ROLE_ZZZ", "zzz");
        assertEquals("zzz", managementService.getRolesList().get(0).getDescription());

    }

    @Test
    public void testGrantRole() throws Exception {

        managementService.addUser("bender@futurama.com", "Bender", "Rodríguez");
        managementService.addRole("ROLE_ZZZ", null);
        managementService.grantRole("bender@futurama.com", "ROLE_ZZZ");

        assertEquals("ROLE_ZZZ", managementService.getAssignedManagerRoles("bender@futurama.com").get(0).getCode());


    }

    @Test
    public void testRevokeRole() throws Exception {

        managementService.addUser("bender@futurama.com", "Bender", "Rodríguez");
        managementService.addRole("ROLE_ZZZ", null);
        managementService.grantRole("bender@futurama.com", "ROLE_ZZZ");

        assertEquals("ROLE_ZZZ", managementService.getAssignedManagerRoles("bender@futurama.com").get(0).getCode());
        managementService.revokeRole("bender@futurama.com", "ROLE_ZZZ");
        assertEquals(0, managementService.getAssignedManagerRoles("bender@futurama.com").size());
    }

    @Test
    public void testDeleteRole() throws Exception {

        managementService.addUser("bender@futurama.com", "Bender", "Rodríguez");
        managementService.addRole("ROLE_ZZZ", null);
        managementService.grantRole("bender@futurama.com", "ROLE_ZZZ");

        assertEquals("ROLE_ZZZ", managementService.getAssignedManagerRoles("bender@futurama.com").get(0).getCode());
        managementService.deleteRole("ROLE_ZZZ");
        assertTrue(managementService.getRolesList().isEmpty());
        assertEquals(0, managementService.getAssignedManagerRoles("bender@futurama.com").size());


    }





}

