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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.model.ImportFile;
import org.yes.cart.bulkimport.service.ImportService;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.impl.AttrValueEntityProduct;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/12/11
 * Time: 4:18 PM
 */
public class ImagesBulkImportServiceImplTest extends BaseCoreDBTestCase {

    private final Mockery mockery = new JUnit4Mockery();

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
        Pattern pattern = Pattern.compile("^(.*)_([.[^_]]*)_[a-z]\\.(jpe?g|png)$");
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

            assertTrue("No match " + fileName, matcher.matches());
            assertFalse("Contains _ " + fileName, matcher.group(2).indexOf('_') > -1);
            assertTrue(fileName + " not in expectations", expectation.contains(matcher.group(2)));
            assertTrue(expectation.remove(matcher.group(2)));

        }

        assertTrue(expectation.isEmpty());


    }

    @Test
    public void testgetImageAttributeSuffixName() {
        ImagesBulkImportServiceImpl service =
                new ImagesBulkImportServiceImpl(null, null, null);
        int idx = 0;
        for (String fileName : fileNames) {
            Integer attrIdx = Integer.valueOf(service.getImageAttributeSuffixName(fileName));
            assertEquals("Wrong suffix for " + fileName, idx, attrIdx.intValue());
            idx++;
        }
    }


    @Test
    public void testDoImport() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                try {
                    ProductService productService = createContext().getBean("productService", ProductService.class);
                    Product product = productService.getProductById(10000L, true);
                    assertNotNull(product);
                    assertNull(product.getAttributeByCode("IMAGE2")); // product has not IMAGE2 attribute

                    final XStreamProvider<ImportDescriptor> xml =
                            createContext().getBean("importDescriptorXStreamProvider", XStreamProvider.class);
                    final ImportDescriptor descriptor = xml.fromXML(new FileInputStream(new File("src/test/resources/import/productimages.xml")));
                    descriptor.setImportDirectory(new File("src/test/resources/import/").getAbsolutePath());

                    final JobContext context = mockery.mock(JobContext.class, "context");
                    final JobStatusListener listener = mockery.mock(JobStatusListener.class, "listener");

                    final Set<String> imported = new HashSet<String>();

                    mockery.checking(new Expectations() {{
                        allowing(context).getListener(); will(returnValue(listener));
                        allowing(context).getAttribute(JobContextKeys.IMPORT_FILE_SET); will(returnValue(imported));
                        allowing(context).getAttribute(JobContextKeys.IMPORT_FILE); will(returnValue(new File("src/test/resources/import/im-image-file_SOBOT-BEER_c.jpeg").getAbsolutePath()));
                        allowing(context).getAttribute(JobContextKeys.IMAGE_VAULT_PATH); will(returnValue("file:" + File.separator + File.separator + "target" + File.separator));
                        allowing(context).getAttribute(JobContextKeys.IMPORT_DESCRIPTOR_NAME); will(returnValue("productimages.xml"));
                        allowing(context).getAttribute(JobContextKeys.IMPORT_DESCRIPTOR); will(returnValue(descriptor));
                        allowing(listener).notifyMessage(with(any(String.class)));
                        allowing(listener).notifyPing("Processed 1 of 1 images");
                    }});

                    ImportService service = (ImportService) createContext().getBean("imagesBulkImportService");
                    service.doImport(context);

                    clearCache();
                    product = productService.getProductById(10000L, true);
                    assertNotNull(product);


                    final GenericDAO<AttrValueEntityProduct, Long> attrValueEntityProductDao = createContext().getBean("attrValueEntityProductDao", GenericDAO.class);

                    for (ProductSku productSku : product.getSku()) {
                        if (productSku.getCode().equals("SOBOT-BEER")) {
                            assertNotNull(productSku.getAttributeByCode("IMAGE2"));
                            assertEquals("im-image-file_SOBOT-BEER_c.jpeg", productSku.getAttributeByCode("IMAGE2").getVal());
                            attrValueEntityProductDao.delete(productSku.getAttributeByCode("IMAGE2"));
                        }
                    }

                    status.setRollbackOnly();

                    mockery.assertIsSatisfied();
                } catch (Exception exp) {
                    fail(exp.getMessage());
                }

            }
        });


    }



}
