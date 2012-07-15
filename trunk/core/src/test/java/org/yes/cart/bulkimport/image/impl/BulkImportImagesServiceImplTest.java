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

import net.sf.ehcache.Cache;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

import static junit.framework.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/12/11
 * Time: 4:18 PM
 */

public class BulkImportImagesServiceImplTest extends BaseCoreDBTestCase {


    private String[] fileNames = {
            "some_seo_image_file-name_PRODUCT-or-SKU-CODE_a.jpeg",
            "some_seo_image_file-name_PRODUCT1_b.jpeg",
            "some_seo_image_file-name_PROD+UCT1_c.jpeg",
            "some_seo_image_file_name_КОД-ПРОДУКТА_d.jpeg",
            "some-seo-image-file-name_ЕЩЕ-КОД-пРОДУКТА_e.jpg",
            "очень-вкусная-сосиска-с-камнями-от-Сваровски_-КОД-Сосики-_f.jpg",
            "Очень-Вкусная-Сосиска3-с-камнями-от-Булыжникова_ЕЩЕ-КОД-ПРОДУКТА!_g.jpg",
            "cat-a4_К80-721-5779Y5008-9с_h.jpeg"
    };


    /**
     * File name expectations are following:
     * some_seo-image_file-name_PRODUCTorSKU-CODE_[a|b|c....].ext
     * i.e. file name must contain _C!O-D+E_, where code not contain _ underscore.
     */
    @Test
    public void testFileNamesRegularExpression() {
        Pattern pattern = Pattern.compile("_([.[^_]]*)_[a-z]\\.jpe?g$");
        List<String> expectation = new ArrayList<String>() {{
            add("PRODUCT-or-SKU-CODE");
            add("PRODUCT1");
            add("PROD+UCT1");
            add("КОД-ПРОДУКТА");
            add("ЕЩЕ-КОД-пРОДУКТА");
            add("-КОД-Сосики-");
            add("ЕЩЕ-КОД-ПРОДУКТА!");
            add("К80-721-5779Y5008-9с");
        }};


        for (String fileName : fileNames) {
            Matcher matcher = pattern.matcher(fileName);

            assertTrue("Not find " + fileName, matcher.find());
            assertFalse("Contains _ " + fileName, matcher.group(1).indexOf('_') > -1);
            assertTrue(fileName + " not in expectations", expectation.contains(matcher.group(1)));
            assertTrue(expectation.remove(matcher.group(1)));

        }

        assertTrue(expectation.isEmpty());


    }

    @Test
    public void testgetImageAttributeSuffixName() {
        BulkImportImagesServiceImpl service = (BulkImportImagesServiceImpl)
                createContext().getBean("bulkImportImageService");
        int idx = 0;
        for (String fileName : fileNames) {
            Integer attrIdx = Integer.valueOf(service.getImageAttributeSuffixName(fileName));
            assertEquals("Wrong suffix for " + fileName, idx, attrIdx.intValue());
            idx++;
        }
    }


    private BulkImportImagesServiceImpl getBulkImportService() {
        BulkImportImagesServiceImpl service = (BulkImportImagesServiceImpl)
                createContext().getBean("bulkImportImageService");
        service.setPathToImportFolder("/zzz/yyy/");
        return service;

    }


    @Test
    public void testDoImportProductImage() {

        ProductService productService = (ProductService) createContext().getBean("productService");
        Product product = productService.getProductById(9998L, true);
        assertNotNull(product);
        assertNull(product.getAttributeByCode("IMAGE0")); // product has not IMAGE0 attribute

        BulkImportImagesServiceImpl service = getBulkImportService();
        StringBuilder errorReport = new StringBuilder();
        boolean rez = service.doImportProductImage(
                errorReport,
                "im-image-file_BENDER-ua_a.jpeg",
                "BENDER-ua",
                "0");
        assertTrue(rez);
        assertFalse(errorReport.toString().indexOf("ERROR") > -1);

        clearCache();
        product = productService.getProductById(9998L, true);
        assertNotNull(product);
        assertNotNull(product.getAttributeByCode("IMAGE0")); // image was imported as IMAGE0 attribute
        assertEquals("im-image-file_BENDER-ua_a.jpeg", product.getAttributeByCode("IMAGE0").getVal());

    }


    @Test
    public void testDoImportProductSkuImage() {

        ProductService productService = (ProductService) createContext().getBean("productService");
        Product product = productService.getProductById(10000L, true); //SOBOT
        assertNotNull(product);
        for (ProductSku productSku : product.getSku()) {
            if (productSku.getCode().equals("SOBOT-BEER")) {
                assertNull(product.getAttributeByCode("SKUIMAGE0")); // at this point sku has not images
                assertNull(product.getAttributeByCode("SKUIMAGE1")); // at this point sku has not images
                assertNull(product.getAttributeByCode("SKUIMAGE2")); // at this point sku has not images
            }
        }


        BulkImportImagesServiceImpl service = getBulkImportService();
        StringBuilder errorReport = new StringBuilder();
        boolean rez = service.doImportProductSkuImage(
                errorReport,
                "im-image1-file_SOBOT-BEER_a.jpeg",
                "SOBOT-BEER",
                "0");
        rez &= service.doImportProductSkuImage(
                errorReport,
                "im-image2-file_SOBOT-BEER_b.jpeg",
                "SOBOT-BEER",
                "1");
        rez &= service.doImportProductSkuImage(
                errorReport,
                "im-image-file_SOBOT-BEER_c.jpeg",
                "SOBOT-BEER",
                "2");
        assertTrue(errorReport.toString(), rez);
        assertFalse(errorReport.toString().indexOf("ERROR") > -1);

        clearCache();
        product = productService.getProductById(10000L);
        assertNotNull(product);
        assertNotNull(product);
        for (ProductSku productSku : product.getSku()) {
            if (productSku.getCode().equals("SOBOT-BEER")) {
                assertNotNull(productSku.getAttributeByCode("SKUIMAGE0")); // at this point sku has not images
                assertNotNull(productSku.getAttributeByCode("SKUIMAGE1")); // at this point sku has not images
                assertNotNull(productSku.getAttributeByCode("SKUIMAGE2")); // at this point sku has not images

                assertEquals("im-image1-file_SOBOT-BEER_a.jpeg", productSku.getAttributeByCode("SKUIMAGE0").getVal());
                assertEquals("im-image2-file_SOBOT-BEER_b.jpeg", productSku.getAttributeByCode("SKUIMAGE1").getVal());
                assertEquals("im-image-file_SOBOT-BEER_c.jpeg", productSku.getAttributeByCode("SKUIMAGE2").getVal());
            }
        }
    }

    @Test
    public void testDoImport() {

        ProductService productService = (ProductService) createContext().getBean("productService");
        Product product = productService.getProductById(10000L, true);
        assertNotNull(product);
        assertNull(product.getAttributeByCode("IMAGE2")); // product has not IMAGE2 attribute

        BulkImportImagesServiceImpl service = getBulkImportService();
        StringBuilder errorReport = new StringBuilder();
        service.doImport(
                new File("src/test/resources/import/im-image-file_SOBOT-BEER_c.jpeg"),
                errorReport,
                new HashSet<String>());

        assertFalse(errorReport.toString().indexOf("ERROR") > -1);

        clearCache();
        product = productService.getProductById(10000L, true);
        assertNotNull(product);


        for (ProductSku productSku : product.getSku()) {
            if (productSku.getCode().equals("SOBOT-BEER")) {
                assertNotNull(productSku.getAttributeByCode("SKUIMAGE2"));

                assertEquals("im-image-file_SOBOT-BEER_c.jpeg", productSku.getAttributeByCode("SKUIMAGE2").getVal());
            }
        }
    }

    private void clearCache() {
        for (Cache cache : getCacheMap().values()) {
            cache.removeAll();
        }
    }

    private Map<String, Cache> getCacheMap() {
        Map<String, Cache> cacheMap = createContext().getBeansOfType(Cache.class);
        return cacheMap;
    }

}
