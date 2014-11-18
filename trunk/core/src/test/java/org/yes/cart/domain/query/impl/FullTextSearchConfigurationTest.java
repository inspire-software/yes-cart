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

package org.yes.cart.domain.query.impl;

import org.apache.lucene.search.Query;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.dao.impl.AbstractTestDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.ProductCategoryEntity;
import org.yes.cart.domain.entity.impl.ProductEntity;
import org.yes.cart.domain.entity.impl.ProductSkuEntity;
import org.yes.cart.domain.entity.impl.SkuWarehouseEntity;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 14/11/2014
 * Time: 10:25
 */
public class FullTextSearchConfigurationTest extends AbstractTestDAO {

    private GenericDAO<Product, Long> productDao;
    private GenericDAO<ProductSku, Long> productSkuDao;
    private GenericDAO<Brand, Long> brandDao;
    private GenericDAO<ProductType, Long> productTypeDao;
    private GenericDAO<ProductCategory, Long> productCategoryDao;
    private GenericDAO<Category, Long> categoryDao;
    private GenericDAO<SkuWarehouse, Long> skuWareHouseDao;
    private GenericDAO<Warehouse, Long> warehouseDao;

    private LuceneQueryFactory luceneQueryFactory;

    @Before
    public void setUp()  {
        productDao = (GenericDAO<Product, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_DAO);
        productSkuDao = (GenericDAO<ProductSku, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_SKU_DAO);
        brandDao = (GenericDAO<Brand, Long>) ctx().getBean(DaoServiceBeanKeys.BRAND_DAO);
        productTypeDao = (GenericDAO<ProductType, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_TYPE_DAO);
        productCategoryDao = (GenericDAO<ProductCategory, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_CATEGORY_DAO);
        categoryDao = (GenericDAO<Category, Long>) ctx().getBean(DaoServiceBeanKeys.CATEGORY_DAO);
        skuWareHouseDao = (GenericDAO<SkuWarehouse, Long>) ctx().getBean(DaoServiceBeanKeys.SKU_WAREHOUSE_DAO);
        warehouseDao = (GenericDAO<Warehouse, Long>) ctx().getBean(DaoServiceBeanKeys.WAREHOUSE_DAO);


        luceneQueryFactory = ctx().getBean(ServiceSpringKeys.LUCENE_QUERY_FACTORY, LuceneQueryFactory.class);

        super.setUp();
    }

    @Test
    public void testCategoryQuerySearch() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false);

                Query query;

                query = luceneQueryFactory.getFilteredNavigationQueryChain(0L, Arrays.asList(101L, 104L),
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                List<Product> products = productDao.fullTextSearch(query);
                assertTrue("Failed [" + query.toString() +"]", !products.isEmpty());
                // search by Sku code
                query = luceneQueryFactory.getFilteredNavigationQueryChain(0L, Arrays.asList(101L, 104L, 313L),
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST4")));
                products = productDao.fullTextSearch(query);
                assertEquals("This is fuzzy so we see all CC_TESTX", 9, products.size());
                assertEquals("CC_TEST4 is best match", "CC_TEST4", products.get(0).getCode());
                // search by Sku code with stems
                query = luceneQueryFactory.getFilteredNavigationQueryChain(0L, Arrays.asList(101L, 104L, 313L),
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("cc_test4")));
                products = productDao.fullTextSearch(query);
                assertEquals("Relaxed search should give all cc_test skus", 12, products.size());
                assertEquals("CC_TEST4 is best match", "CC_TEST4", products.get(0).getCode());
                // search by sku id
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.SKU_ID_FIELD, (List) Arrays.asList("11004")));
                products = productDao.fullTextSearch(query);
                assertEquals(1, products.size());
                assertEquals("PRODUCT5", products.get(0).getSku().iterator().next().getCode());
                //test fuzzy search
                query = luceneQueryFactory.getFilteredNavigationQueryChain(0L, Arrays.asList(101L, 104L),
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("blender")));
                products = productDao.fullTextSearch(query);
                assertFalse(products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());
                //test search by description
                query = luceneQueryFactory.getFilteredNavigationQueryChain(0L, Arrays.asList(101L, 104L),
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("Rodriguez Bending")));
                products = productDao.fullTextSearch(query);
                assertTrue(!products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());
                query = luceneQueryFactory.getFilteredNavigationQueryChain(0L, Arrays.asList(101L, 104L),
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("DiMaggio")));
                products = productDao.fullTextSearch(query);
                assertFalse(products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());
                // search on empty string
                query = luceneQueryFactory.getFilteredNavigationQueryChain(0L, Arrays.asList(101L, 104L),
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("")));
                products = productDao.fullTextSearch(query);
                assertFalse(products.isEmpty()); //return all product in described categories

                // search on brand
                // product with code "PRODUCT1" also future robotics, but not assigned to any category
                query = luceneQueryFactory.getFilteredNavigationQueryChain(0L, Arrays.asList(101L, 104L),
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("FutureRobots")));
                products = productDao.fullTextSearch(query);
                assertEquals(2, products.size());

                status.setRollbackOnly();
            }
        });
    }


    @Test
    public void testShopQuerySearch() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false);

                Query query;

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                List<Product> products = productDao.fullTextSearch(query);
                assertFalse("Product must be found in 10 shop. Failed [" + query.toString() + "]", products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());

                query = luceneQueryFactory.getFilteredNavigationQueryChain(20L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                products = productDao.fullTextSearch(query);
                assertFalse("The same categories assigned to 20 shop. Failed [" + query.toString() +"]", products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());

                query = luceneQueryFactory.getFilteredNavigationQueryChain(30L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                products = productDao.fullTextSearch(query);
                assertTrue("Product not present on 30 shop. Failed [" + query.toString() +"]", products.isEmpty());

                // search exact by Sku code
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.SKU_PRODUCT_CODE_FIELD, (List) Arrays.asList("CC_TEST4")));
                products = productDao.fullTextSearch(query);
                assertEquals("CC_TEST1 - CC_TEST9 are in this shop " + query.toString(), 1, products.size());
                assertEquals("CC_TEST4 is best match", "CC_TEST4", products.get(0).getCode());

                // search fuzzy by Sku code
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST4")));
                products = productDao.fullTextSearch(query);
                assertEquals("CC_TEST1 - CC_TEST9 are in this shop " + query.toString(), 9, products.size());
                assertEquals("CC_TEST4 is best match", "CC_TEST4", products.get(0).getCode());

                //test fuzzy search
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("blender")));
                products = productDao.fullTextSearch(query);
                assertFalse(products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());

                //test search by description
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("Rodriguez Bending")));
                products = productDao.fullTextSearch(query);
                assertFalse(products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("DiMaggio")));
                products = productDao.fullTextSearch(query);
                assertFalse(products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());

                // search on empty string
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("")));
                products = productDao.fullTextSearch(query);
                assertFalse(products.isEmpty()); //return all product in described categories

                // search on brand
                // product with code "PRODUCT1" also future robotics, but not assigned to any category,
                // and can not be found via shop, because it has qty on warehouse, but category not assigned to 20 shop
                query = luceneQueryFactory.getFilteredNavigationQueryChain(20L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("FutureRobots")));
                products = productDao.fullTextSearch(query);
                assertEquals(query.toString(), 2, products.size());

                status.setRollbackOnly();
            }
        });
    }


    @Test
    public void testFuzzySearch() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false);

                Query query;

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                List<Product> products = productDao.fullTextSearch(query);
                assertFalse("Failed [" + query.toString() + "]", products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());

                // search by Sku code
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST99")));
                products = productDao.fullTextSearch(query);
                assertEquals("CC_TEST9 is best match", "CC_TEST9", products.get(0).getCode());
                assertEquals(1, products.size());
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEZT9")));
                products = productDao.fullTextSearch(query);
                assertEquals("Misspelling does not increase the score and should find a match", 1, products.size());
                assertEquals("CC_TEST9 is best match", "CC_TEST9", products.get(0).getCode());
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("cc_test99")));
                products = productDao.fullTextSearch(query);
                assertFalse(products.isEmpty());
                assertEquals("CC_TEST9 is best match", "CC_TEST9", products.get(0).getCode());

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L),
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST99")));
                products = productDao.fullTextSearch(query);
                assertFalse(products.isEmpty());
                assertEquals("CC_TEST9 is best match", "CC_TEST9", products.get(0).getCode());

                status.setRollbackOnly();
            }
        });
    }

    @Test
    public void testCategoryBrowsing() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                productDao.fullTextSearchReindex(false);

                Query query;

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L), null);
                List<Product> products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query.toString() +"]", 2, products.size());

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 200L, 123L, 2435L), null);
                products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query.toString() +"]", 2, products.size());

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L), null);
                products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query.toString() +"]", 14, products.size());

                status.setRollbackOnly();

            }
        });

    }

    @Test
    public void testTagBrowsingTest() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                productDao.fullTextSearchReindex(false);

                Query query;

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD, (List) Arrays.asList("newarrival")));
                List<Product> products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query.toString() +"] expected 2 products", 2, products.size());
                assertTrue("Must have tag", products.get(0).getTag().contains("newarrival"));
                assertTrue("Must have tag", products.get(1).getTag().contains("newarrival"));

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD, (List) Arrays.asList("sale")));
                products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query.toString() +"] expected 2 products", 2, products.size());
                assertTrue("Must have tag", products.get(0).getTag().contains("sale"));
                assertTrue("Must have tag", products.get(1).getTag().contains("sale"));

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD, (List) Arrays.asList("specialpromo")));
                products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query.toString() +"] expected 2 products", 2, products.size());
                assertTrue("Must have tag", products.get(0).getTag().contains("specialpromo"));
                assertTrue("Must have tag", products.get(1).getTag().contains("specialpromo"));

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD, (List) Arrays.asList("sali")));
                products = productDao.fullTextSearch(query);
                assertTrue("Failed [" + query.toString() +"] expected 0 products, tags are exact match", products.isEmpty());

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(104L),
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD, (List) Arrays.asList("sale")));
                products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query.toString() +"] expected 1 products", 1, products.size());
                assertTrue("Must have tag", products.get(0).getTag().contains("sale"));

                status.setRollbackOnly();

            }
        });

    }


    @Test
    public void testSingleValueAttributeNavigation() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                productDao.fullTextSearchReindex(false);

                Query query;

                // Test that we able to find Bender by his material in category where he exists
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("metal")));
                List<Product> products = productDao.fullTextSearch(query);
                assertEquals("Query [" + query.toString() + "] failed", 1, products.size());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());

                // Test that we able to find Bender by his material in  list of categories where he exists
                query = luceneQueryFactory.getFilteredNavigationQueryChain(0L, Arrays.asList(101L, 200L),
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("metal")));
                products = productDao.fullTextSearch(query);
                assertEquals(1, products.size());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());



                // Test that we able to find Sobot by his material in category where he exists
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L),
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("Plastik")));
                products = productDao.fullTextSearch(query);
                assertEquals(1, products.size());
                assertEquals("SOBOT is best match", "SOBOT", products.get(0).getCode());
                //No category limitation, so we expect all plastic robots
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("Plastik")));
                products = productDao.fullTextSearch(query);
                assertEquals(query.toString(), 1, products.size());
                assertEquals("SOBOT is best match", "SOBOT", products.get(0).getCode());
                // Robot from plastic not in 104 category
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(104L),
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("Plastik")));
                products = productDao.fullTextSearch(query);
                assertEquals(0, products.size());


                //We are unable to find products manufactured from bananas
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L),
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("banana")));
                products = productDao.fullTextSearch(query);
                assertEquals(0, products.size());
                //We are unable to find products manufactured from bananas
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("banana")));
                products = productDao.fullTextSearch(query);
                assertEquals(0, products.size());


                // search by sku attribute value
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null,
                        Collections.singletonMap("SMELL", (List) Arrays.asList("apple")));
                products = productDao.fullTextSearch(query);
                assertTrue("Failed [" + query + "] SMELL is not nav attr, so we return all", products.size() > 1);


                status.setRollbackOnly();

            }
        });


    }


    @Test
    public void getRangeAttributeValueNavigation() throws InterruptedException {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false);

                Query query;

                // range values are excluding high value
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L),
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("0.001-_-2.3")));
                List<Product> products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 2, products.size());

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L),
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("0.001-_-2.31")));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 3, products.size());

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L),
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.1-_-2.31")));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 3, products.size());

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L),
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.35-_-2.36")));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 1, products.size());

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L),
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.34-_-2.36")));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 1, products.size());

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L),
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.35-_-2.38")));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 1, products.size());

                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L),
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.4-_-2.49")));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 0, products.size());

                // products not assigned to this category
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(100L),
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.1-_-2.5")));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 0, products.size());

                // products not assigned to this category
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(100L, 101L, 102L),
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.1-_-2.5")));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 0, products.size());

                status.setRollbackOnly();

            }
        });
    }

    @Test
    public void testBrandNavigation() {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                createProduct(102L, "LG_DVD_PLAYER", "product lg dvd player", 3L, 134L);
                createProduct(104L, "SAM_DVD_PLAYER", "product sam mp3 player", 3L, 134L);
                createProduct(102L, "LG_MP3_PLAYER", "product lg mp3 player", 2L, 135L);
                createProduct(103L, "SONY_MP3_PLAYER", "product sony mp3 player", 2L, 135L);
                createProduct(104L, "SAM_MP3_PLAYER", "product sam mp3 player", 2L, 136L);
                // productDao.fullTextSearchReindex(false);
                List<Product> foundProducts;
                Query query;
                List<Long> categories = new ArrayList<Long>();

                //existing LG product in category 134
                categories.clear();
                categories.add(134L);
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, categories,
                        Collections.singletonMap(ProductSearchQueryBuilder.BRAND_FIELD, (List) Arrays.asList("LG")));
                foundProducts = productDao.fullTextSearch(query);
                assertNotNull(foundProducts);
                assertEquals(query.toString(), 1, foundProducts.size());
                assertEquals("Must be correct brand", "LG", foundProducts.get(0).getBrand().getName());

                //existing two LG products in category 135 135
                categories.clear();
                categories.add(134L);
                categories.add(135L);
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, categories,
                        Collections.singletonMap(ProductSearchQueryBuilder.BRAND_FIELD, (List) Arrays.asList("LG")));
                foundProducts = productDao.fullTextSearch(query);
                assertNotNull(foundProducts);
                assertEquals(query.toString(), 2, foundProducts.size());
                assertEquals("Must be correct brand", "LG", foundProducts.get(0).getBrand().getName());
                assertEquals("Must be correct brand", "LG", foundProducts.get(1).getBrand().getName());

                //only one Sony product in categories 135, 134,136
                categories.clear();
                categories.add(134L);
                categories.add(135L);
                categories.add(136L);
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, categories,
                        Collections.singletonMap(ProductSearchQueryBuilder.BRAND_FIELD, (List) Arrays.asList("Sony")));
                foundProducts = productDao.fullTextSearch(query);
                assertNotNull(foundProducts);
                assertEquals(query.toString(), 1, foundProducts.size());
                assertEquals("Must be correct brand", "Sony", foundProducts.get(0).getBrand().getName());

                //LG prod not exists in 136 category
                categories.clear();
                categories.add(136L);
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, categories,
                        Collections.singletonMap(ProductSearchQueryBuilder.BRAND_FIELD, (List) Arrays.asList("LG")));
                foundProducts = productDao.fullTextSearch(query);
                assertNotNull(foundProducts);
                assertEquals(0, foundProducts.size());

                status.setRollbackOnly();

            }
        });

    }


    @Test
    public void testPriceNavigation() throws Exception {



        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false);

                Query query;

                // test that price border is excluding upper limit in search
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 131L, 132L),
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-12-_-15")));
                List<Product> products = productDao.fullTextSearch(query);
                assertNotNull(query.toString(), products);
                assertTrue(query.toString(), products.isEmpty());
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 131L, 132L),
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-12-_-15.01")));
                products = productDao.fullTextSearch(query);
                assertNotNull(query.toString(), products);
                assertFalse(query.toString(), products.isEmpty());
                assertEquals(1, products.size());

                // Test that filter by categories works. Categories 131 and 132
                // not contains product with price 15.00
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(131L, 132L),
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-15-_-15")));
                products = productDao.fullTextSearch(query);
                assertNotNull(query.toString(), products);
                assertTrue(query.toString(), products.isEmpty());

                // prices less than 15
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 131L, 132L),
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-0-_-14")));
                products = productDao.fullTextSearch(query);
                assertNotNull(query.toString(), products);
                assertTrue(query.toString(), products.isEmpty());

                // Test, that we able to all products,
                // that match search criteria
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 131L, 132L),
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-0-_-1000")));
                products = productDao.fullTextSearch(query);
                assertNotNull(query.toString(), products);
                assertTrue(query.toString(), !products.isEmpty());
                assertEquals(5, products.size()); //Here 2 product have 0 quantity on warehouse

                // no price 250 in given categories
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 132L),
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-250-_-421")));
                products = productDao.fullTextSearch(query);
                assertNotNull(query.toString(), products);
                assertTrue(query.toString(), !products.isEmpty());
                assertEquals(2, products.size());

                // Test, that filter by currency code works ok
                query = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 131L, 132L),
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("USD-_-0-_-1000")));
                products = productDao.fullTextSearch(query);
                assertNotNull(query.toString(), products);
                assertTrue(query.toString(), products.isEmpty());

                // Test, that filter by shop id works
                query = luceneQueryFactory.getFilteredNavigationQueryChain(20L, Arrays.asList(129L, 130L, 131L, 132L),
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-0-_-1000")));
                products = productDao.fullTextSearch(query);
                assertNotNull(query.toString(), products);
                assertTrue(query.toString(), products.isEmpty());

                status.setRollbackOnly();
            }
        });

    }

    private long createProduct(long brandId, String productCode, String productName, long productTypeId, long productCategoryId) {
        Product product = new ProductEntity();
        product.setAvailability(Product.AVAILABILITY_STANDARD);
        Brand brand = brandDao.findById(brandId);
        assertNotNull(brand);
        product.setBrand(brand);
        product.setCode(productCode);
        product.setName(productName);
        ProductType productType = productTypeDao.findById(productTypeId);
        assertNotNull(productType);
        product.setProducttype(productType);
        long pk = productDao.create(product).getProductId();
        assertTrue(pk > 0L);
        ProductCategory productCategory = new ProductCategoryEntity();
        productCategory.setProduct(product);
        productCategory.setCategory(categoryDao.findById(productCategoryId));
        productCategory.setRank(0);
        product.getProductCategory().add(productCategory);
        productCategory = productCategoryDao.create(productCategory);

        productDao.saveOrUpdate(product);
        assertNotNull(productCategory);
        ProductSku productSku = new ProductSkuEntity();
        productSku.setCode(product.getCode());
        productSku.setName(product.getName());
        productSku.setProduct(product);
        product.getSku().add(productSku);
        productDao.saveOrUpdate(product);
        productSkuDao.saveOrUpdate(productSku);
        productSkuDao.saveOrUpdate(productSku);
        // add quantity on warehouses
        SkuWarehouse skuWarehouse = new SkuWarehouseEntity();
        skuWarehouse.setSku(productSku);
        skuWarehouse.setQuantity(BigDecimal.ONE);
        skuWarehouse.setWarehouse(warehouseDao.findById(2L));
        skuWareHouseDao.create(skuWarehouse);
        productSku.getQuantityOnWarehouse().add(skuWarehouse);
        productDao.fullTextSearchReindex(product.getProductId());
        skuWareHouseDao.flushClear();
        return pk;
    }

    @Test
    public void testProductAvailability() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                productDao.fullTextSearchReindex(false);

                List<Product> products = productDao.fullTextSearch(luceneQueryFactory.getFilteredNavigationQueryChain(0L, Arrays.asList(212L), null));
                assertEquals("Only 5 product available in 212 category", 5, products.size());
                assertNull(getProductByCode(products, "PAT_PRODUCT_ON_STOCK_ONLY_1"));
                assertNotNull(getProductByCode(products, "PAT_PRODUCT_ON_STOCK_ONLY_2"));
                assertNull(getProductByCode(products, "PAT_PRODUCT_ON_STOCK_ONLY_3"));
                assertNotNull(getProductByCode(products, "PAT_PRODUCT_PREORDER"));
                assertNotNull(getProductByCode(products, "PAT_PRODUCT_BACKORDER"));
                assertNotNull(getProductByCode(products, "PAT_PRODUCT_ALWAYS"));
                assertNotNull(getProductByCode(products, "PAT_PRODUCT_ALWAYS2"));

                status.setRollbackOnly();

            }
        });
    }

    private Product getProductByCode(final List<Product> products, final String skuCode) {
        for (Product prod : products) {
            if (skuCode.equals(prod.getCode())) {
                return prod;
            }
        }
        return null;
    }}
