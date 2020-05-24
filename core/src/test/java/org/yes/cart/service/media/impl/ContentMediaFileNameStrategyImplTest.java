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

package org.yes.cart.service.media.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.CacheManager;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.media.MediaFileNameStrategy;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 27/04/2019
 * Time: 15:15
 */
public class ContentMediaFileNameStrategyImplTest extends BaseCoreDBTestCase {

    private ContentService contentService;
    private MediaFileNameStrategy mediaFileNameStrategy;
    private CacheManager cacheManager;

    @Override
    @Before
    public void setUp() {
        mediaFileNameStrategy = (MediaFileNameStrategy) ctx().getBean(ServiceSpringKeys.CONTENT_IMAGE_NAME_STRATEGY);
        contentService = (ContentService) ctx().getBean(ServiceSpringKeys.CONTENT_SERVICE);
        cacheManager = (CacheManager) ctx().getBean("cacheManager");
        super.setUp();
    }


    @Test
    public void testGetCodeFromActualObject() {

        //test case to support file names without code with non-seo category
        assertEquals("SHOIP2-002", mediaFileNameStrategy.resolveObjectCode("content.jpeg"));
        assertEquals("SHOIP2-002", mediaFileNameStrategy.resolveObjectCode("imgvault/content/content.jpeg"));
        assertEquals("SHOIP2-002", mediaFileNameStrategy.resolveObjectCode("imgvault/content/content.jpeg?w=10&h=4"));

        final Content cn10107 = contentService.findById(10107L);
        cn10107.getSeo().setUri("CC-TEST-content");
        contentService.update(cn10107);

        cacheManager.getCache("imageNameStrategy-resolveObjectCode").clear();

        //test case to support file names without code with seo content
        assertEquals("CC-TEST-content", mediaFileNameStrategy.resolveObjectCode("content.jpeg"));
        assertEquals("CC-TEST-content", mediaFileNameStrategy.resolveObjectCode("imgvault/content/content.jpeg"));
        assertEquals("CC-TEST-content", mediaFileNameStrategy.resolveObjectCode("imgvault/content/content.jpeg?w=10&h=4"));

        // test that non-existent are resolved to no image
        assertEquals(Constants.NO_IMAGE, mediaFileNameStrategy.resolveObjectCode("imgvault/content/unknown-content.jpeg"));

    }

}
