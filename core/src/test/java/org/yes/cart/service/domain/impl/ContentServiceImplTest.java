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

package org.yes.cart.service.domain.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.config.ConfigurationRegistry;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.service.domain.ContentService;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Denis Pavlov
 * Date: 15-June-2013
 */
@RunWith(Parameterized.class)
public class ContentServiceImplTest extends BaseCoreDBTestCase {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<String> data() {
        return Arrays.asList("contentServiceCMS1", "contentServiceCMS3");
    }


    private ContentService contentService;

    private String cmsMode;

    public ContentServiceImplTest(final String cmsMode) {
        this.cmsMode = cmsMode;
    }

    @Override
    @Before
    public void setUp() {
        contentService = (ContentService) ctx().getBean(ServiceSpringKeys.CONTENT_SERVICE);
        final ContentService mode = (ContentService) ctx().getBean(cmsMode);
        ((ConfigurationRegistry) contentService).register("CMS", mode);
        super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        ((ConfigurationRegistry) contentService).register("CMS", null);
        super.tearDown();
    }

    @Test
    public void testGetRootContent() {
        Content rootContent = contentService.getRootContent(20L); //SHOIP2
        assertNotNull(rootContent);
        assertEquals(rootContent.getGuid(), "SHOIP2");
    }

    @Test
    public void testGetRootContentCreate() {
        Content rootContent = contentService.getRootContent(30L); //SHOIP3
        assertNull(rootContent);
        contentService.createRootContent(30L);
        rootContent = contentService.getRootContent(30L); //SHOIP3
        assertNotNull(rootContent);
        assertEquals(rootContent.getGuid(), "SHOIP3");
    }

    @Test
    public void testGetCategoryAttributeRecursive() {
        String val = contentService.getContentAttributeRecursive(null, 10105L, "SOME_NOT_EXISTING_ATTR", null);
        assertNull(val);
        val = contentService.getContentAttributeRecursive(null, 10105L, AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null);
        assertNull(val); // This is root, not editable in admin, so must not use
        val = contentService.getContentAttributeRecursive(null, 10107L, AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null);
        assertEquals("6,12,24", val);
    }

    @Test
    public void testGetCategoryAttributeRecursiveMulti() {
        String[] val = contentService.getContentAttributeRecursive(null, 10107L, new String[] { "SOME_NOT_EXISTING_ATTR",  "SOME_NOT_EXISTING_ATTR_2" });
        assertNull(val);
        val = contentService.getContentAttributeRecursive(null, 10107L, new String[] { "SOME_NOT_EXISTING_ATTR", AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE });
        assertNotNull(val);
        assertNull(val[0]);
        assertEquals("6,12,24", val[1]);
    }

    @Test
    public void testGetUIVariationTestNoFailover() {
        Content content = contentService.findById(10106L);
        assertNotNull(content);
        assertNull(content.getUitemplate());
        String uiVariation = contentService.getContentTemplate(10106L);
        assertNull(uiVariation);
    }

    @Test
    public void testGetUIVariationTestExists() {
        Content content = contentService.findById(10107L);
        assertNotNull(content);
        assertEquals("content", content.getUitemplate());
        String uiVariation = contentService.getContentTemplate(10107L);
        assertEquals(content.getUitemplate(), uiVariation);
    }

    @Test
    public void testGetUIVariationTestFailover() {
        Content content = contentService.findById(10108L);
        assertNotNull(content);
        assertNull(content.getUitemplate());
        String uiVariation = contentService.getContentTemplate(10108L);
        assertEquals("content", uiVariation);
    }


    @Test
    public void testGetChildCategoriesRecursiveTest() {
        Set<Long> contentIds = new HashSet<>();
        contentIds.addAll(Arrays.asList(10105L, 10106L, 10107L, 10108L, 10109L));
        Set<Content> contents = contentService.getChildContentRecursive(10105L);
        for (Content content : contents) {
            assertTrue(contentIds.contains(content.getContentId()));
        }
    }

    @Test
    public void testGetChildCategoriesRecursiveNullTest() {
        Set<Content> contents = contentService.getChildContentRecursive(0L);
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

    @Test
    public void testFindContent() throws Exception {

        final Map<String, List> filter = Collections.singletonMap("name", Collections.singletonList("SHOIP1_mail_customer-registered.html"));
        assertEquals(1, contentService.findContentCount(filter));
        final List<Content> cms = contentService.findContent(0, 10, "name", false, filter);
        assertFalse(cms.isEmpty());
        assertEquals("SHOIP1_mail_customer-registered.html", cms.get(0).getName());
    }
}
