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
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.vo.VoContentService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 23/09/2019
 * Time: 17:30
 */
public class VoContentServiceImplTest extends BaseCoreDBTestCase {

    private VoContentService voContentService;

    @Before
    public void setUp() {
        voContentService = (VoContentService) ctx().getBean("voContentService");
        super.setUp();
    }

    @Test
    public void testGetContent() throws Exception {

        List<VoContent> contentAll = voContentService.getAll(20L);
        assertNotNull(contentAll);

        VoSearchContext ctxNone = new VoSearchContext();
        ctxNone.setSize(10);

        VoSearchResult<VoContent> contentNoFilter = voContentService.getFilteredContent(20L, ctxNone);
        assertNotNull(contentNoFilter);
        assertFalse(contentNoFilter.getItems().isEmpty());

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(createSearchContextParams("filter", "SHOIP2 Content 0021"));
        ctxFind.setSize(10);

        VoSearchResult<VoContent> contentFind = voContentService.getFilteredContent(20L, ctxFind);
        assertNotNull(contentFind);
        assertFalse(contentFind.getItems().isEmpty());

        final VoContent c10108 = contentFind.getItems().get(0);
        assertEquals(10108L, c10108.getContentId());
        assertEquals("SHOIP2 Content 0021", c10108.getName());

        final List<Long> path = voContentService.getBranchesPaths(20L, Collections.singletonList(c10108.getContentId()));
        assertTrue("Paths: " + path, Arrays.asList(10105L, 10107L, 10108L).containsAll(path));

        final List<VoContent> branch10105L = voContentService.getBranch(20L, 10105L, path);
        assertEquals(1, branch10105L.size());
        assertEquals(4, branch10105L.get(0).getChildren().size());
        for (final VoContent leaf : branch10105L.get(0).getChildren()) {
            assertTrue("Content: " + leaf.getContentId(), Arrays.asList(10106L, 10107L, 10110L, 10111L).contains(leaf.getContentId()));
            if (leaf.getContentId() == 10107L) {
                assertNotNull(leaf.getChildren());
                for (final VoContent subLeaf : leaf.getChildren()) {
                    assertTrue("Content: " + leaf.getContentId(), Arrays.asList(10108L, 10109L).contains(subLeaf.getContentId()));
                }
            } else {
                assertNull(leaf.getChildren());
            }
        }

        VoSearchContext ctxFindUri = new VoSearchContext();
        ctxFindUri.setParameters(createSearchContextParams("filter", "@SHOIP1_menu_item_1_1"));
        ctxFindUri.setSize(10);
        VoSearchResult<VoContent> contentByURI = voContentService.getFilteredContent(10L, ctxFindUri);
        assertNotNull(contentByURI);
        assertEquals(1, contentByURI.getTotal());
        assertEquals("SHOIP1_menu_item_1_1", contentByURI.getItems().get(0).getUri());

        VoSearchContext ctxFindByParent = new VoSearchContext();
        ctxFindByParent.setParameters(createSearchContextParams("filter", "^SHOIP1_menu_item_1"));
        ctxFindByParent.setSize(10);
        VoSearchResult<VoContent> contentSubTree = voContentService.getFilteredContent(10L, ctxFindByParent);
        assertNotNull(contentSubTree);
        assertEquals(3, contentSubTree.getTotal());
        assertTrue(contentSubTree.getItems().stream().allMatch(cat -> Arrays.asList("SHOIP1_menu_item_1", "SHOIP1_menu_item_11", "SHOIP1_menu_item_12").contains(cat.getGuid())));


    }

    @Test
    public void testContentCRUD() throws Exception {

        final VoContentWithBody category = new VoContentWithBody();
        category.setParentId(10107L);
        category.setName("TEST CRUD");

        final VoContentWithBody created = voContentService.createContent(category);
        assertTrue(created.getContentId() > 0L);
        assertEquals(10107L, category.getParentId());

        final VoContentWithBody afterCreated = voContentService.getContentById(created.getContentId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());

        created.setName("TEST CRUD UPDATE");

        final VoContentWithBody updated = voContentService.updateContent(created);
        assertEquals("TEST CRUD UPDATE", updated.getName());

        VoSearchContext ctx = new VoSearchContext();
        ctx.setParameters(createSearchContextParams("filter", "TEST CRUD UPDATE"));
        ctx.setSize(10);

        assertTrue(voContentService.getFilteredContent(20L, ctx).getTotal() > 0);

        final List<VoAttrValueContent> attributes = voContentService.getContentAttributes(updated.getContentId());
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());

        final VoAttrValueContent updateAttribute = attributes.stream().filter(
                attr -> "CATEGORY_DESCRIPTION_en".equals(attr.getAttribute().getCode())
        ).findFirst().get();

        assertNull(updateAttribute.getVal());
        updateAttribute.setVal("ABC");
        updateAttribute.setDisplayVals(Collections.singletonList(MutablePair.of("en", "Abc value")));

        final List<VoAttrValueContent> attributesAfterCreate = voContentService.updateContentAttributes(Collections.singletonList(MutablePair.of(updateAttribute, Boolean.FALSE)));
        final VoAttrValueContent attributeAfterCreate = attributesAfterCreate.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())).findFirst().get();
        assertEquals("ABC", attributeAfterCreate.getVal());
        assertEquals("en", attributeAfterCreate.getDisplayVals().get(0).getFirst());
        assertEquals("Abc value", attributeAfterCreate.getDisplayVals().get(0).getSecond());

        final List<VoAttrValueContent> attributesAfterRemove = voContentService.updateContentAttributes(Collections.singletonList(MutablePair.of(attributeAfterCreate, Boolean.TRUE)));
        final VoAttrValueContent attributeAfterRemove = attributesAfterRemove.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())).findFirst().get();
        assertNull(attributeAfterRemove.getVal());

        final VoContentWithBody contentBody = voContentService.getContentById(created.getContentId());
        assertNotNull(contentBody);
        assertFalse(contentBody.getContentBodies().isEmpty());
        for (final VoContentBody body : contentBody.getContentBodies()) {
            assertNotNull(body.getLang());
            assertEquals("", body.getText());
            body.setText("Text " + body.getLang());
        }

        final VoContentWithBody updatedContentBody = voContentService.updateContent(contentBody);
        assertNotNull(updatedContentBody);
        assertFalse(updatedContentBody.getContentBodies().isEmpty());
        for (final VoContentBody body : updatedContentBody.getContentBodies()) {
            assertNotNull(body.getLang());
            assertEquals("Text " + body.getLang(), body.getText());
        }

        voContentService.removeContent(updated.getContentId());

        assertFalse(voContentService.getFilteredContent(20L, ctx).getTotal() > 0);

    }
}