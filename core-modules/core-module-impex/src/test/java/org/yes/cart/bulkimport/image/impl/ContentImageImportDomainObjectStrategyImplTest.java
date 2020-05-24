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

package org.yes.cart.bulkimport.image.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkimport.image.ImageImportDomainObjectStrategy;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ContentService;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 01/09/2014
 * Time: 23:33
 */
public class ContentImageImportDomainObjectStrategyImplTest extends BaseCoreDBTestCase {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testDoImageImport() throws Exception {

        ContentService contentService = (ContentService) createContext().getBean("contentServiceCMS3");
        Content content = contentService.getById(10106L);
        assertNotNull(content);
        assertNull(content.getAttributeByCode(AttributeNamesKeys.Category.CATEGORY_IMAGE)); // content has no image

        final JobStatusListener listener = mockery.mock(JobStatusListener.class, "listener");

        mockery.checking(new Expectations() {{
            allowing(listener).notifyMessage(with(any(String.class)), with(any(Object[].class)));
            allowing(listener).count(with(any(String.class)));
        }});


        ImageImportDomainObjectStrategy service = (ImageImportDomainObjectStrategy) createContext().getBean("contentImageImportDomainObjectStrategy");

        boolean rez = service.doImageImport(
                listener, "im-image-file_SHOIP2-001_a.jpeg", "SHOIP2-001", "0",
                null);
        assertTrue(rez);

        clearCache();
        content = contentService.getById(10106L);
        assertNotNull(content);
        assertNotNull(content.getAttributeByCode(AttributeNamesKeys.Category.CATEGORY_IMAGE)); // image was imported
        assertEquals("im-image-file_SHOIP2-001_a.jpeg", content.getAttributeValueByCode(AttributeNamesKeys.Category.CATEGORY_IMAGE));

        mockery.assertIsSatisfied();


    }
}
