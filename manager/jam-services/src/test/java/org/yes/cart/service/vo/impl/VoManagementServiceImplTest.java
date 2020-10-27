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
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.vo.VoManagementService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 24/09/2019
 * Time: 16:42
 */
public class VoManagementServiceImplTest extends BaseCoreDBTestCase {

    private VoManagementService voManagementService;

    @Before
    public void setUp() {
        voManagementService = (VoManagementService) ctx().getBean("voManagementService");
        super.setUp();
    }

    @Test
    public void testGetManagers() throws Exception {

        VoSearchContext ctxNoFilter = new VoSearchContext();
        ctxNoFilter.setSize(10);
        List<VoManagerInfo> managers = voManagementService.getFilteredManagers(ctxNoFilter).getItems();
        assertNotNull(managers);
        assertFalse(managers.isEmpty());


        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(createSearchContextParams("filter", "test@test.com"));
        ctxFind.setSize(10);
        managers = voManagementService.getFilteredManagers(ctxFind).getItems();
        assertNotNull(managers);
        assertFalse(managers.isEmpty());
        assertEquals("test@test.com", managers.get(0).getEmail());

    }

    @Test
    public void testManagerUpdatedWithLoginChange() throws Exception {

        final String login = UUID.randomUUID().toString();
        final String email = login + "@test-crud2.com";

        final VoManager manager = new VoManager();
        manager.setLogin(login);
        manager.setEmail(email);
        manager.setFirstName("FN");
        manager.setLastName("LN");
        manager.setEnabled(true);
        final VoManagerShop shop = new VoManagerShop();
        shop.setManagerId(manager.getManagerId());
        shop.setShopId(10L);
        manager.setManagerShops(Collections.singletonList(shop));
        final VoManagerRole role = new VoManagerRole();
        role.setManagerId(manager.getManagerId());
        role.setCode("ROLE_OTHER");
        manager.setManagerRoles(Collections.singletonList(role));
        final VoManagerSupplierCatalog cat = new VoManagerSupplierCatalog();
        cat.setManagerId(manager.getManagerId());
        cat.setCode("CAT0004");
        manager.setManagerSupplierCatalogs(Collections.singletonList(cat));
        final VoManagerCategoryCatalog mst = new VoManagerCategoryCatalog();
        mst.setManagerId(manager.getManagerId());
        mst.setCategoryId(102L);
        mst.setCode("102");
        mst.setName("Flying Machines");
        manager.setManagerCategoryCatalogs(Collections.singletonList(mst));

        final VoManager created = voManagementService.createManager(manager);
        assertTrue(created.getManagerId() > 0L);

        assertEquals(1, created.getManagerRoles().size());
        assertEquals("ROLE_OTHER", created.getManagerRoles().get(0).getCode());
        assertEquals(1, created.getManagerSupplierCatalogs().size());
        assertEquals("CAT0004", created.getManagerSupplierCatalogs().get(0).getCode());
        assertEquals(1, created.getManagerCategoryCatalogs().size());
        assertEquals("102", created.getManagerCategoryCatalogs().get(0).getCode());

        created.setLogin(email);
        final VoManagerRole newRole = new VoManagerRole();
        newRole.setManagerId(manager.getManagerId());
        newRole.setCode("ROLE_SMADMIN");
        created.getManagerRoles().add(newRole);

        final VoManager updated = voManagementService.updateManager(created);
        assertEquals(email, updated.getLogin());
        assertNotNull(updated.getManagerShops());
        assertEquals(10L, updated.getManagerShops().get(0).getShopId());
        assertNotNull(updated.getManagerRoles());
        assertEquals("ROLE_OTHER", updated.getManagerRoles().get(0).getCode());
        assertEquals("ROLE_SMADMIN", updated.getManagerRoles().get(1).getCode());
        assertNotNull(updated.getManagerSupplierCatalogs());
        assertEquals("CAT0004", updated.getManagerSupplierCatalogs().get(0).getCode());
        assertNotNull(updated.getManagerCategoryCatalogs());
        assertEquals("102", updated.getManagerCategoryCatalogs().get(0).getCode());



    }

    @Test
    public void testManagerCRUD() throws Exception {

        final String email = UUID.randomUUID().toString() + "@test-crud.com";

        final VoManager manager = new VoManager();
        manager.setLogin(email);
        manager.setEmail(email);
        manager.setFirstName("FN");
        manager.setLastName("LN");
        manager.setEnabled(true);
        final VoManagerShop shop = new VoManagerShop();
        shop.setManagerId(manager.getManagerId());
        shop.setShopId(10L);
        manager.setManagerShops(Collections.singletonList(shop));

        final VoManager created = voManagementService.createManager(manager);
        assertTrue(created.getManagerId() > 0L);
        assertFalse(created.getEnabled());

        VoManager afterCreated = voManagementService.getManagerById(created.getManagerId());
        assertNotNull(afterCreated);
        assertEquals("FN", afterCreated.getFirstName());
        assertNotNull(afterCreated.getManagerRoles());
        assertTrue(afterCreated.getManagerRoles().isEmpty());
        assertNotNull(afterCreated.getManagerShops());
        assertEquals(1, afterCreated.getManagerShops().size());

        final VoManagerRole role = new VoManagerRole();
        role.setManagerId(created.getManagerId());
        role.setCode("ROLE_OTHER");
        afterCreated.setManagerRoles(Collections.singletonList(role));
        final VoManagerSupplierCatalog cat = new VoManagerSupplierCatalog();
        cat.setManagerId(created.getManagerId());
        cat.setCode("CAT0004");
        afterCreated.setManagerSupplierCatalogs(Collections.singletonList(cat));
        final VoManagerCategoryCatalog mst = new VoManagerCategoryCatalog();
        mst.setManagerId(created.getManagerId());
        mst.setCategoryId(102L);
        mst.setCode("102");
        mst.setName("Flying Machines");
        afterCreated.setManagerCategoryCatalogs(Collections.singletonList(mst));

        final VoManager updated = voManagementService.updateManager(afterCreated);
        assertNotNull(updated.getManagerShops());
        assertEquals(10L, updated.getManagerShops().get(0).getShopId());
        assertNotNull(updated.getManagerRoles());
        assertEquals("ROLE_OTHER", updated.getManagerRoles().get(0).getCode());
        assertNotNull(updated.getManagerSupplierCatalogs());
        assertEquals("CAT0004", updated.getManagerSupplierCatalogs().get(0).getCode());
        assertNotNull(updated.getManagerCategoryCatalogs());
        assertEquals("102", updated.getManagerCategoryCatalogs().get(0).getCode());

        final VoSearchContext ctx = new VoSearchContext();
        ctx.setSize(100);
        assertTrue(voManagementService.getFilteredManagers(ctx).getItems().stream().anyMatch(mng -> mng.getManagerId() == updated.getManagerId()));

        voManagementService.updateDisabledFlag(updated.getManagerId(), true);

        final VoManager disabled = voManagementService.updateManager(created);
        assertFalse(disabled.getEnabled());
        assertNotNull(disabled.getManagerShops());
        assertFalse(disabled.getManagerShops().isEmpty());
        assertNotNull(disabled.getManagerRoles());
        assertTrue(disabled.getManagerRoles().isEmpty());
        assertNotNull(disabled.getManagerSupplierCatalogs());
        assertTrue(disabled.getManagerSupplierCatalogs().isEmpty());
        assertNotNull(disabled.getManagerCategoryCatalogs());
        assertTrue(disabled.getManagerCategoryCatalogs().isEmpty());


    }

}