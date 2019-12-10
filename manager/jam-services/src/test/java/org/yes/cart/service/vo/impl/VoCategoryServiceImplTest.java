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
import org.yes.cart.domain.vo.VoAttrValueCategory;
import org.yes.cart.domain.vo.VoCategory;
import org.yes.cart.domain.vo.VoSearchContext;
import org.yes.cart.domain.vo.VoSearchResult;
import org.yes.cart.service.vo.VoCategoryService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Igor_Azarny on 4/13/2016.
 */
public class VoCategoryServiceImplTest extends BaseCoreDBTestCase {

    private VoCategoryService voCategoryService;

    @Before
    public void setUp() {
        voCategoryService = (VoCategoryService) ctx().getBean("voCategoryService");
        super.setUp();
    }

    @Test
    public void testGetCategories() throws Exception {

        TestAdminFederationFacadeImpl.IS_SYSADMIN = false;

        try {
            List<VoCategory> categoryAll = voCategoryService.getAll();
            assertNotNull(categoryAll);

            for (int start = 0; start < 20; start++) { // Test paging till the end
                VoSearchContext ctxNoFilter = new VoSearchContext();
                ctxNoFilter.setStart(start);
                ctxNoFilter.setSize(10);
                VoSearchResult<VoCategory> categoryNoFilter = voCategoryService.getFilteredCategories(ctxNoFilter);
                assertNotNull(categoryNoFilter);
                if (start * 10 < categoryNoFilter.getTotal()) {
                    assertEquals(10, categoryNoFilter.getItems().size());
                } else {
                    break;
                }
                start++;
            }

            VoSearchContext ctxFind = new VoSearchContext();
            ctxFind.setParameters(Collections.singletonMap("filter", Collections.singletonList("Flying Machines")));
            ctxFind.setSize(10);
            VoSearchResult<VoCategory> categoryFind = voCategoryService.getFilteredCategories(ctxFind);
            assertNotNull(categoryFind);
            assertFalse(categoryFind.getItems().isEmpty());

            final VoCategory c102 = categoryFind.getItems().get(0);
            assertEquals("102", c102.getGuid());
            assertEquals("Flying Machines", c102.getName());

            final List<Long> path = voCategoryService.getBranchesPaths(Collections.singletonList(c102.getCategoryId()));
            assertTrue("Paths: " + path, Arrays.asList(100L, 101L, 102L).containsAll(path));

            final List<VoCategory> branch101L = voCategoryService.getBranch(101L, path);
            assertEquals(1, branch101L.size());
            assertEquals(4, branch101L.get(0).getChildren().size());
            for (final VoCategory leaf : branch101L.get(0).getChildren()) {
                assertTrue("Category: " + leaf.getCategoryId(), Arrays.asList("102", "103", "104", "105").contains(leaf.getGuid()));
                if ("102".equals(leaf.getGuid())) {
                    assertNotNull(leaf.getChildren());
                    for (final VoCategory subLeaf : leaf.getChildren()) {
                        assertTrue("Category: " + leaf.getCategoryId(), Arrays.asList("143", "144").contains(subLeaf.getGuid()));
                    }
                } else {
                    assertNull(leaf.getChildren());
                }
            }

            VoSearchContext ctxFindUri = new VoSearchContext();
            ctxFindUri.setParameters(Collections.singletonMap("filter", Collections.singletonList("@big-boys-Gadgets")));
            ctxFindUri.setSize(10);
            VoSearchResult<VoCategory> categoryByURI = voCategoryService.getFilteredCategories(ctxFindUri);
            assertNotNull(categoryByURI);
            assertEquals(1, categoryByURI.getTotal());
            assertEquals("101", categoryByURI.getItems().get(0).getGuid());

            VoSearchContext ctxFindByParent = new VoSearchContext();
            ctxFindByParent.setParameters(Collections.singletonMap("filter", Collections.singletonList("^101")));
            ctxFindByParent.setSize(10);
            VoSearchResult<VoCategory> categorySubTree = voCategoryService.getFilteredCategories(ctxFindByParent);
            assertNotNull(categorySubTree);
            assertFalse(categorySubTree.getItems().isEmpty());
            assertTrue(categorySubTree.getItems().stream().allMatch(cat -> Arrays.asList("101", "102", "103", "104", "105").contains(cat.getGuid())));

        } finally {
            TestAdminFederationFacadeImpl.IS_SYSADMIN = true;
        }
    }

    @Test
    public void testCategoryCRUD() throws Exception {

        final VoCategory category = new VoCategory();
        category.setParentId(102L);
        category.setName("TEST CRUD");

        final VoCategory created = voCategoryService.createCategory(category);
        assertTrue(created.getCategoryId() > 0L);
        assertEquals(102L, category.getParentId());

        final VoCategory afterCreated = voCategoryService.getCategoryById(created.getCategoryId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());

        created.setName("TEST CRUD UPDATE");

        final VoCategory updated = voCategoryService.updateCategory(created);
        assertEquals("TEST CRUD UPDATE", updated.getName());

        VoSearchContext ctx = new VoSearchContext();
        ctx.setParameters(Collections.singletonMap("filter", Collections.singletonList("TEST CRUD UPDATE")));
        ctx.setSize(10);

        assertTrue(voCategoryService.getFilteredCategories(ctx).getTotal() > 0);

        final List<VoAttrValueCategory> attributes = voCategoryService.getCategoryAttributes(updated.getCategoryId());
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());

        final VoAttrValueCategory updateAttribute = attributes.stream().filter(
                attr -> "CATEGORY_DESCRIPTION_en".equals(attr.getAttribute().getCode())
        ).findFirst().get();

        assertNull(updateAttribute.getVal());
        updateAttribute.setVal("ABC");
        updateAttribute.setDisplayVals(Collections.singletonList(MutablePair.of("en", "Abc value")));

        final List<VoAttrValueCategory> attributesAfterCreate = voCategoryService.updateCategoryAttributes(Collections.singletonList(MutablePair.of(updateAttribute, Boolean.FALSE)));
        final VoAttrValueCategory attributeAfterCreate = attributesAfterCreate.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())).findFirst().get();
        assertEquals("ABC", attributeAfterCreate.getVal());
        assertEquals("en", attributeAfterCreate.getDisplayVals().get(0).getFirst());
        assertEquals("Abc value", attributeAfterCreate.getDisplayVals().get(0).getSecond());

        final List<VoAttrValueCategory> attributesAfterRemove = voCategoryService.updateCategoryAttributes(Collections.singletonList(MutablePair.of(attributeAfterCreate, Boolean.TRUE)));
        final VoAttrValueCategory attributeAfterRemove = attributesAfterRemove.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())).findFirst().get();
        assertNull(attributeAfterRemove.getVal());

        voCategoryService.removeCategory(updated.getCategoryId());

        assertFalse(voCategoryService.getFilteredCategories(ctx).getTotal() > 0);

    }
}