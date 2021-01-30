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

package org.yes.cart.bulkjob.product;

import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkjob.cron.CronJobProcessor;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.*;
import org.yes.cart.utils.DateUtils;

import java.util.Arrays;
import java.util.Map;

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
        final int days = (int) Math.ceil(((double) (java.lang.System.currentTimeMillis() - _1999_01_01)) / ((double) (1000*60*60*24)));

        final Map<String, Object> ctx5 = configureJobContext("removeObsoleteProductProcessor",
                "process-batch-size=1\nobsolete-timeout-days=" + (days - 5));
        final Map<String, Object> ctx12 = configureJobContext("removeObsoleteProductProcessor",
                "process-batch-size=10\nobsolete-timeout-days=" + (days - 12));


        final ProductService productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        final ProductSkuService productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        final CronJobProcessor removeObsoleteProductProcessor = ctx().getBean("removeObsoleteProductProcessor", CronJobProcessor.class);

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {
                createProducts(
                        new Pair<>("OBS-001", "1999-01-01"),
                        new Pair<>("OBS-002", "1999-01-05"),
                        new Pair<>("OBS-003", "1999-01-10"),
                        new Pair<>("OBS-004", "1999-01-15")
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

        removeObsoleteProductProcessor.process(ctx5);

        final JobStatus status1 = ((JobStatusAware) removeObsoleteProductProcessor).getStatus(null);
        assertNotNull(status1);
        assertTrue(status1.getReport(), status1.getReport().contains("Removing obsolete inventory record for OBS-001"));
        assertTrue(status1.getReport(), status1.getReport().contains("with status OK, err: 0, warn: 0\n" +
                "Counters [Obsolete products: 2, Removed products: 1]"));

        assertNull(productService.getProductBySkuCode("OBS-001"));
        assertNull(productSkuService.getProductSkuBySkuCode("OBS-001"));
        assertNotNull(productService.getProductBySkuCode("OBS-002"));
        assertNotNull(productSkuService.getProductSkuBySkuCode("OBS-002"));
        assertNotNull(productService.getProductBySkuCode("OBS-003"));
        assertNotNull(productSkuService.getProductSkuBySkuCode("OBS-003"));
        assertNotNull(productService.getProductBySkuCode("OBS-004"));
        assertNotNull(productSkuService.getProductSkuBySkuCode("OBS-004"));

        removeObsoleteProductProcessor.process(ctx12);

        final JobStatus status2 = ((JobStatusAware) removeObsoleteProductProcessor).getStatus(null);
        assertNotNull(status2);
        assertTrue(status2.getReport(), status2.getReport().contains("Removing obsolete inventory record for OBS-002"));
        assertTrue(status2.getReport(), status2.getReport().contains("Removing obsolete inventory record for OBS-003"));
        assertTrue(status2.getReport(), status2.getReport().contains("with status OK, err: 0, warn: 0\n" +
                "Counters [Obsolete products: 2, Removed products: 2]"));

        assertNull(productService.getProductBySkuCode("OBS-001"));
        assertNull(productSkuService.getProductSkuBySkuCode("OBS-001"));
        assertNull(productService.getProductBySkuCode("OBS-002"));
        assertNull(productSkuService.getProductSkuBySkuCode("OBS-002"));
        assertNull(productService.getProductBySkuCode("OBS-003"));
        assertNull(productSkuService.getProductSkuBySkuCode("OBS-003"));
        assertNotNull(productService.getProductBySkuCode("OBS-004"));
        assertNotNull(productSkuService.getProductSkuBySkuCode("OBS-004"));

    }


    private void createProducts(Pair<String, String> ... namesAndAvailabilities) {

        final ProductTypeService productTypeService = (ProductTypeService) ctx().getBean(ServiceSpringKeys.PRODUCT_TYPE_SERVICE);
        final BrandService brandService = (BrandService) ctx().getBean(ServiceSpringKeys.BRAND_SERVICE);
        final AssociationService associationService = (AssociationService) ctx().getBean(ServiceSpringKeys.ASSOCIATION_SERVICE);
        final ProductAssociationService productAssociationService = (ProductAssociationService) ctx().getBean("productAssociationService");
        final ProductService productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        final WarehouseService warehouseService = (WarehouseService) ctx().getBean(ServiceSpringKeys.WAREHOUSE_SERVICE);
        final SkuWarehouseService skuWarehouseService = (SkuWarehouseService) ctx().getBean(ServiceSpringKeys.SKU_WAREHOUSE_SERVICE);
        final GenericDAO<AttrValueProduct, Long> productAvDao = (GenericDAO<AttrValueProduct, Long>) ctx().getBean("attrValueEntityProductDao");
        final GenericDAO<AttrValueProductSku, Long> productSkuAvDao = (GenericDAO<AttrValueProductSku, Long>) ctx().getBean("attrValueEntityProductSkuDao");
        final GenericDAO<ProductOption, Long> productOptionDao = (GenericDAO<ProductOption, Long>) ctx().getBean("productOptionDao");
        final EntityFactory productEntityFactory = productService.getGenericDao().getEntityFactory();
        final EntityFactory inventoryEntityFactory = skuWarehouseService.getGenericDao().getEntityFactory();

        for (final Pair<String, String> pair : namesAndAvailabilities) {

            Product product = productEntityFactory.getByIface(Product.class);
            product.setCode(pair.getFirst());
            product.setName(pair.getFirst());
            product.setDescription("description");
            product.setProducttype(productTypeService.findById(1L));
            product.setBrand(brandService.findById(101L));
            product = productService.create(product);
            assertTrue(product.getProductId() > 0);
            //test that default sku is created
            assertFalse(product.getSku().isEmpty());
            //code the same
            assertEquals(product.getCode(), product.getSku().iterator().next().getCode());

            final AttrValueProduct avp = productEntityFactory.getByIface(AttrValueProduct.class);
            avp.setProduct(product);
            avp.setAttributeCode("CODE1");
            avp.setVal("VAL1");
            productAvDao.saveOrUpdate(avp);

            final AttrValueProductSku avps = productEntityFactory.getByIface(AttrValueProductSku.class);
            avps.setProductSku(product.getDefaultSku());
            avps.setAttributeCode("CODE2");
            avps.setVal("VAL2");
            productSkuAvDao.saveOrUpdate(avps);

            final Association assoc = associationService.findById(1L);
            final ProductAssociation pAssoc = productAssociationService.getGenericDao().getEntityFactory().getByIface(ProductAssociation.class);
            pAssoc.setAssociation(assoc);
            pAssoc.setAssociatedSku("CODE3");
            pAssoc.setProduct(product);
            productAssociationService.create(pAssoc);

            final ProductOption pOpt = productOptionDao.getEntityFactory().getByIface(ProductOption.class);
            pOpt.setProduct(product);
            pOpt.setAttributeCode("ABC");
            pOpt.setOptionSkuCodes(Arrays.asList("CODE4", "CODE5"));
            productOptionDao.create(pOpt);

            final SkuWarehouse inventory = inventoryEntityFactory.getByIface(SkuWarehouse.class);
            inventory.setWarehouse(warehouseService.findAll().get(0));
            inventory.setSkuCode(product.getDefaultSku().getCode());
            inventory.setAvailability(SkuWarehouse.AVAILABILITY_ALWAYS);
            inventory.setAvailableto(DateUtils.ldtParseSDT(pair.getSecond()));
            skuWarehouseService.create(inventory);

        }
    }

}