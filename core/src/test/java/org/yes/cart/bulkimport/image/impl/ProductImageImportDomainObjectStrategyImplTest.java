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
import org.yes.cart.domain.entity.Product;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.ProductService;

import static junit.framework.Assert.*;

/**
 * User: denispavlov
 * Date: 01/09/2014
 * Time: 23:33
 */
public class ProductImageImportDomainObjectStrategyImplTest extends BaseCoreDBTestCase {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testDoImageImport() throws Exception {

        ProductService productService = (ProductService) createContext().getBean("productService");
        Product product = productService.getProductById(9998L, true);
        assertNotNull(product);
        assertNull(product.getAttributeByCode("IMAGE0")); // product has no IMAGE0 attribute

        final JobStatusListener listener = mockery.mock(JobStatusListener.class, "listener");

        mockery.checking(new Expectations() {{
            allowing(listener).notifyMessage(with(any(String.class)));
        }});


        ImageImportDomainObjectStrategy service = (ImageImportDomainObjectStrategy) createContext().getBean("productImageImportDomainObjectStrategy");

        boolean rez = service.doImageImport(
                listener,
                "im-image-file_BENDER-ua_a.jpeg",
                "BENDER-ua",
                "0");
        assertTrue(rez);

        clearCache();
        product = productService.getProductById(9998L, true);
        assertNotNull(product);
        assertNotNull(product.getAttributeByCode("IMAGE0")); // image was imported as IMAGE0 attribute
        assertEquals("im-image-file_BENDER-ua_a.jpeg", product.getAttributeByCode("IMAGE0").getVal());

        mockery.assertIsSatisfied();


    }
}
