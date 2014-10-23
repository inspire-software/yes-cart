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

package org.yes.cart.bulkimport.image.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkimport.image.ImageImportDomainObjectStrategy;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValueBrand;
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.BrandService;

import java.util.Collection;

import static junit.framework.Assert.*;

/**
 * User: denispavlov
 * Date: 01/09/2014
 * Time: 23:33
 */
public class BrandImageImportDomainObjectStrategyImplTest extends BaseCoreDBTestCase {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testDoImageImport() throws Exception {

        BrandService brandService = (BrandService) createContext().getBean("brandService");
        Brand brand = brandService.findByNameOrGuid("FutureRobots");
        assertNotNull(brand);
        final Collection<AttrValueBrand> attributesBefore = brand.getAttributes();
        if (attributesBefore != null) {
            for (AttrValueBrand attrValue : attributesBefore) {
                if (attrValue.getAttribute() != null && AttributeNamesKeys.Brand.BRAND_IMAGE.equals(attrValue.getAttribute().getCode())) {
                    fail("Has image - it should not");
                    break;
                }
            }
        }

        final JobStatusListener listener = mockery.mock(JobStatusListener.class, "listener");

        mockery.checking(new Expectations() {{
            allowing(listener).notifyMessage(with(any(String.class)));
        }});


        ImageImportDomainObjectStrategy service = (ImageImportDomainObjectStrategy) createContext().getBean("brandImageImportDomainObjectStrategy");

        boolean rez = service.doImageImport(
                listener, "im-image-file_FutureRobots_a.jpeg", "FutureRobots", "0",
                null);
        assertTrue(rez);

        clearCache();
        brand = brandService.findByNameOrGuid("FutureRobots");
        assertNotNull(brand);

        AttrValueBrand saved = null;
        final Collection<AttrValueBrand> attributesAfter = brand.getAttributes();
        if (attributesAfter != null) {
            for (AttrValueBrand attrValue : attributesAfter) {
                if (attrValue.getAttribute() != null && AttributeNamesKeys.Brand.BRAND_IMAGE.equals(attrValue.getAttribute().getCode())) {
                    saved = attrValue;
                    break;
                }
            }
        }

        assertNotNull(saved); // image was imported
        assertEquals("im-image-file_FutureRobots_a.jpeg", saved.getVal());

        mockery.assertIsSatisfied();


    }
}
