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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.ContentService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * User: Denis Pavlov
 * Date: 15-June-2013
 */
public class ContentServiceImplTest extends BaseCoreDBTestCase {



    private ContentService contentService;

    @Before
    public void setUp() {
        contentService = (ContentService) ctx().getBean(ServiceSpringKeys.CONTENT_SERVICE);
        super.setUp();
    }

    @Test
    public void testGetRootContent() {
        Category rootContent = contentService.getRootContent(20L); //SHOIP2
        assertNotNull(rootContent);
        assertEquals(rootContent.getGuid(), "SHOIP2");
    }

    @Test
    public void testGetRootContentCreate() {
        Category rootContent = contentService.getRootContent(10L); //SHOIP1
        assertNull(rootContent);
        contentService.createRootContent(10L);
        rootContent = contentService.getRootContent(10L); //SHOIP1
        assertNotNull(rootContent);
        assertEquals(rootContent.getGuid(), "SHOIP1");
    }

    @Test
    public void testGetCategoryAttributeRecursive() {
        String val = contentService.getContentAttributeRecursive(null, 10105L, "SOME_NOT_EXISTING_ATTR", null);
        assertNull(val);
        val = contentService.getContentAttributeRecursive(null, 10105L, AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null);
        assertEquals("10,20,50", val);
        val = contentService.getContentAttributeRecursive(null, 10107L, AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null);
        assertEquals("6,12,24", val);
    }

    @Test
    public void testGetCategoryAttributeRecursiveMulti() {
        String[] val = contentService.getContentAttributeRecursive(null, 10105L, new String[] { "SOME_NOT_EXISTING_ATTR",  "SOME_NOT_EXISTING_ATTR_2" });
        assertNull(val);
        val = contentService.getContentAttributeRecursive(null, 10105L, new String[] { "SOME_NOT_EXISTING_ATTR", AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE });
        assertNotNull(val);
        assertNull(val[0]);
        assertEquals("10,20,50", val[1]);
    }

    @Test
    public void testGetUIVariationTestNoFailover() {
        Category category = contentService.findById(10106L);
        assertNotNull(category);
        assertNull(category.getUitemplate());
        String uiVariation = contentService.getContentTemplate(10106L);
        assertNull(uiVariation);
    }

    @Test
    public void testGetUIVariationTestExists() {
        Category category = contentService.findById(10107L);
        assertNotNull(category);
        assertEquals("content", category.getUitemplate());
        String uiVariation = contentService.getContentTemplate(10107L);
        assertEquals(category.getUitemplate(), uiVariation);
    }

    @Test
    public void testGetUIVariationTestFailover() {
        Category category = contentService.findById(10108L);
        assertNotNull(category);
        assertNull(category.getUitemplate());
        String uiVariation = contentService.getContentTemplate(10108L);
        assertEquals("content", uiVariation);
    }


    @Test
    public void testGetChildCategoriesRecursiveTest() {
        Set<Long> contentIds = new HashSet<Long>();
        contentIds.addAll(Arrays.asList(10105L, 10106L, 10107L, 10108L, 10109L));
        Set<Category> contents = contentService.getChildContentRecursive(10105L);
        for (Category content : contents) {
            assertTrue(contentIds.contains(content.getCategoryId()));
        }
    }

    @Test
    public void testGetChildCategoriesRecursiveNullTest() {
        Set<Category> contents = contentService.getChildContentRecursive(0l);
        assertTrue(contents.isEmpty());
    }

    @Test
    public void testIsContentHasSubcontentTrue() throws Exception {
        assertTrue(contentService.isContentHasSubcontent(10105L, 10109L));
    }

    @Test
    public void testIsContentHasSubcontentFalse() throws Exception {
        assertFalse(contentService.isContentHasSubcontent(10106L, 10109L));
    }

}
