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
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.ProductService;

import static junit.framework.Assert.*;

/**
 * User: denispavlov
 * Date: 01/09/2014
 * Time: 23:36
 */
public class ProductSkuImageImportDomainObjectStrategyImplTest extends BaseCoreDBTestCase {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testDoImageImport() throws Exception {

        ProductService productService = (ProductService) createContext().getBean("productService");
        Product product = productService.getProductById(10000L, true); //SOBOT
        assertNotNull(product);
        for (ProductSku productSku : product.getSku()) {
            if (productSku.getCode().equals("SOBOT-BEER")) {
                assertNull(productSku.getAttributeByCode("IMAGE0")); // at this point sku has no images
                assertNull(productSku.getAttributeByCode("IMAGE1")); // at this point sku has no images
                //assertNull(productSku.getAttributeByCode("IMAGE2")); // at this point sku has no images
            }
        }


        final JobStatusListener listener = mockery.mock(JobStatusListener.class, "listener");

        mockery.checking(new Expectations() {{
            allowing(listener).notifyMessage(with(any(String.class)));
        }});


        ImageImportDomainObjectStrategy service = (ImageImportDomainObjectStrategy) createContext().getBean("productSkuImageImportDomainObjectStrategy");

        boolean rez = service.doImageImport(
                listener,
                "im-image1-file_SOBOT-BEER_a.jpeg",
                "SOBOT-BEER",
                "0");
        rez &= service.doImageImport(
                listener,
                "im-image2-file_SOBOT-BEER_b.jpeg",
                "SOBOT-BEER",
                "1");
        rez &= service.doImageImport(
                listener,
                "im-image-file_SOBOT-BEER_c.jpeg",
                "SOBOT-BEER",
                "2");
        assertTrue(rez);

        clearCache();
        product = productService.getProductById(10000L);
        assertNotNull(product);
        for (ProductSku productSku : product.getSku()) {
            if (productSku.getCode().equals("SOBOT-BEER")) {
                assertNotNull(productSku.getAttributeByCode("IMAGE0")); // at this point sku has images
                assertNotNull(productSku.getAttributeByCode("IMAGE1")); // at this point sku has images
                assertNotNull(productSku.getAttributeByCode("IMAGE2")); // at this point sku has images

                assertEquals("im-image1-file_SOBOT-BEER_a.jpeg", productSku.getAttributeByCode("IMAGE0").getVal());
                assertEquals("im-image2-file_SOBOT-BEER_b.jpeg", productSku.getAttributeByCode("IMAGE1").getVal());
                assertEquals("im-image-file_SOBOT-BEER_c.jpeg", productSku.getAttributeByCode("IMAGE2").getVal());
            }
        }

        mockery.assertIsSatisfied();

    }
}
