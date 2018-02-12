/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.bulkjob.product;

import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValueProduct;
import org.yes.cart.domain.entity.AttrValueProductSku;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductAssociation;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.*;
import org.yes.cart.util.DateUtils;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 23/09/2017
 * Time: 00:23
 */
public class RemoveObsoleteProductProcessorImplTest extends BaseCoreDBTestCase {


    @Test
    public void testRun() throws Exception {

        final long _1999_01_01 = DateUtils.iParseSDT("1999-01-01").toEpochMilli();

        final int days = (int) Math.ceil(((double) (System.currentTimeMillis() - _1999_01_01)) / ((double) (1000*60*60*24)));

        final ProductService productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        final ProductSkuService productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {
                createProducts(
                        new Pair<String, String>("OBS-001", "1999-01-01"),
                        new Pair<String, String>("OBS-002", "1999-01-05"),
                        new Pair<String, String>("OBS-003", "1999-01-10"),
                        new Pair<String, String>("OBS-004", "1999-01-15")
                );
            }
        });

        

        assertNotNull(productService.getProductBySkuCode("OBS-001"));
        assertNotNull(productSkuService.getProductSkuBySkuCode("OBS-001"));
        assertNotNull(productService.getProductBySkuCode("OBS-002"));
        assertNotNull(productSkuService.getProductSkuBySkuCode("OBS-002"));
        assertNotNull(productService.getProductBySkuCode("OBS-003"));
        assertNotNull(productSkuService.getProductSkuBySkuCode("OBS-003"));
        assertNotNull(productService.getProductBySkuCode("OBS-004"));
        assertNotNull(productSkuService.getProductSkuBySkuCode("OBS-004"));

        getTx().execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {

                getRemoveObsoleteProductProcessor(days - 5, 1).run();

            }

        });

        assertNull(productService.getProductBySkuCode("OBS-001"));
        assertNull(productSkuService.getProductSkuBySkuCode("OBS-001"));
        assertNotNull(productService.getProductBySkuCode("OBS-002"));
        assertNotNull(productSkuService.getProductSkuBySkuCode("OBS-002"));
        assertNotNull(productService.getProductBySkuCode("OBS-003"));
        assertNotNull(productSkuService.getProductSkuBySkuCode("OBS-003"));
        assertNotNull(productService.getProductBySkuCode("OBS-004"));
        assertNotNull(productSkuService.getProductSkuBySkuCode("OBS-004"));

        getTx().execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {

                getRemoveObsoleteProductProcessor(days - 12, 10).run();

            }

        });

        assertNull(productService.getProductBySkuCode("OBS-001"));
        assertNull(productSkuService.getProductSkuBySkuCode("OBS-001"));
        assertNull(productService.getProductBySkuCode("OBS-002"));
        assertNull(productSkuService.getProductSkuBySkuCode("OBS-002"));
        assertNull(productService.getProductBySkuCode("OBS-003"));
        assertNull(productSkuService.getProductSkuBySkuCode("OBS-003"));
        assertNotNull(productService.getProductBySkuCode("OBS-004"));
        assertNotNull(productSkuService.getProductSkuBySkuCode("OBS-004"));

    }

    private RemoveObsoleteProductProcessorImpl getRemoveObsoleteProductProcessor(final int minDays, final int batch) {

        final ProductService productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        final ProductSkuService productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        final ProductCategoryService productCategoryService = (ProductCategoryService) ctx().getBean(ServiceSpringKeys.PRODUCT_CATEGORY_SERVICE);
        final GenericDAO<AttrValueProduct, Long> productAvDao = (GenericDAO<AttrValueProduct, Long>) ctx().getBean("attrValueEntityProductDao");
        final GenericDAO<AttrValueProductSku, Long> productSkuAvDao = (GenericDAO<AttrValueProductSku, Long>) ctx().getBean("attrValueEntityProductSkuDao");
        final GenericDAO<ProductAssociation, Long> productAssociationDao = (GenericDAO<ProductAssociation, Long>) ctx().getBean("productAssociationDao");

        return new RemoveObsoleteProductProcessorImpl(
                productService,
                productCategoryService,
                productAvDao,
                productSkuService,
                productSkuAvDao,
                productAssociationDao, null
        ) {

            @Override
            protected int getObsoleteMinDays() {
                return minDays;
            }

            @Override
            protected int getObsoleteBatchSize() {
                return batch;
            }

            @Override
            public RemoveObsoleteProductProcessorInternal getSelf() {
                return this;
            }

        };
    }


    private void createProducts(Pair<String, String> ... namesAndAvailabilities) {

        ProductTypeService productTypeService = (ProductTypeService) ctx().getBean(ServiceSpringKeys.PRODUCT_TYPE_SERVICE);
        BrandService brandService = (BrandService) ctx().getBean(ServiceSpringKeys.BRAND_SERVICE);
        ProductService productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        GenericDAO<AttrValueProduct, Long> productAvDao = (GenericDAO<AttrValueProduct, Long>) ctx().getBean("attrValueEntityProductDao");
        GenericDAO<AttrValueProductSku, Long> productSkuAvDao = (GenericDAO<AttrValueProductSku, Long>) ctx().getBean("attrValueEntityProductSkuDao");

        for (final Pair<String, String> pair : namesAndAvailabilities) {

            EntityFactory entityFactory = productService.getGenericDao().getEntityFactory();
            Product product = entityFactory.getByIface(Product.class);
            product.setCode(pair.getFirst());
            product.setName(pair.getFirst());
            product.setDescription("description");
            product.setProducttype(productTypeService.findById(1L));
            product.setAvailability(Product.AVAILABILITY_ALWAYS);
            product.setAvailableto(DateUtils.ldtParseSDT(pair.getSecond()));
            product.setBrand(brandService.findById(101L));
            product = productService.create(product);
            assertTrue(product.getProductId() > 0);
            //test that default sku is created
            assertFalse(product.getSku().isEmpty());
            //code the same
            assertEquals(product.getCode(), product.getSku().iterator().next().getCode());

            AttrValueProduct avp = entityFactory.getByIface(AttrValueProduct.class);
            avp.setProduct(product);
            avp.setAttributeCode("CODE1");
            avp.setVal("VAL1");
            productAvDao.saveOrUpdate(avp);

            AttrValueProductSku avps = entityFactory.getByIface(AttrValueProductSku.class);
            avps.setProductSku(product.getDefaultSku());
            avps.setAttributeCode("CODE2");
            avps.setVal("VAL2");
            productSkuAvDao.saveOrUpdate(avps);
        }
    }

}