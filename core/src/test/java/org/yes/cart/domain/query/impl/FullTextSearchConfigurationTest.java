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

package org.yes.cart.domain.query.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.GenericFullTextSearchCapableDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.dao.impl.AbstractTestDAO;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.queryobject.FilteredNavigationRecordRequest;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.domain.queryobject.impl.FilteredNavigationRecordRequestImpl;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductSkuService;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 14/11/2014
 * Time: 10:25
 */
public class FullTextSearchConfigurationTest extends AbstractTestDAO {

    private GenericFullTextSearchCapableDAO<Product, Long> productDao;
    private GenericFullTextSearchCapableDAO<ProductSku, Long> productSkuDao;
    private GenericDAO<Brand, Long> brandDao;
    private GenericDAO<ProductType, Long> productTypeDao;
    private GenericDAO<ProductCategory, Long> productCategoryDao;
    private GenericDAO<Category, Long> categoryDao;
    private GenericDAO<SkuWarehouse, Long> skuWareHouseDao;
    private GenericDAO<Warehouse, Long> warehouseDao;
    private GenericDAO<Shop, Long> shopDao;
    private GenericDAO<SkuPrice, Long> skuPriceDao;

    private ProductService productService;
    private ProductSkuService productSkuService;
    private LuceneQueryFactory luceneQueryFactory;

    @Before
    public void setUp()  {
        productDao = (GenericFullTextSearchCapableDAO<Product, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_DAO);
        productSkuDao = (GenericFullTextSearchCapableDAO<ProductSku, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_SKU_DAO);
        brandDao = (GenericDAO<Brand, Long>) ctx().getBean(DaoServiceBeanKeys.BRAND_DAO);
        productTypeDao = (GenericDAO<ProductType, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_TYPE_DAO);
        productCategoryDao = (GenericDAO<ProductCategory, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_CATEGORY_DAO);
        categoryDao = (GenericDAO<Category, Long>) ctx().getBean(DaoServiceBeanKeys.CATEGORY_DAO);
        skuWareHouseDao = (GenericDAO<SkuWarehouse, Long>) ctx().getBean(DaoServiceBeanKeys.SKU_WAREHOUSE_DAO);
        warehouseDao = (GenericDAO<Warehouse, Long>) ctx().getBean(DaoServiceBeanKeys.WAREHOUSE_DAO);
        shopDao = (GenericDAO<Shop, Long>) ctx().getBean(DaoServiceBeanKeys.SHOP_DAO);
        skuPriceDao = (GenericDAO<SkuPrice, Long>) ctx().getBean(DaoServiceBeanKeys.SKU_PRICE_DAO);


        productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);

        luceneQueryFactory = ctx().getBean(ServiceSpringKeys.LUCENE_QUERY_FACTORY, LuceneQueryFactory.class);

        super.setUp();
    }

    private void assertProduct(final List<Product> products, final String hasCode) {
        if (products.isEmpty()) {
            fail("No " + hasCode + ", empty list");
        } else {
            for (final Product product : products) {
                if (hasCode.equals(product.getCode())) {
                    return;
                }
            }
            fail("No " + hasCode + " was not in the list");
        }
    }


    private void assertNoProduct(final List<Product> products, final String hasCode) {
        if (!products.isEmpty()) {
            for (final Product product : products) {
                if (hasCode.equals(product.getCode())) {
                    fail(hasCode + " was in the list");
                }
            }
        }
    }


    @Test
    public void testCategoryQuerySearch() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

                NavigationContext context;

                // basic search
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                List<Product> products = productDao.fullTextSearch(context.getProductQuery());
                assertTrue("Failed [" + context.getProductQuery().toString() +"]", !products.isEmpty());
                // search by name exact
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("Bender Bending Rodriguez")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse(products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());
                // search by Sku code (must be exact)
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L, 313L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST4")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("This is fuzzy but we have exact match for 4, if this fails with 9 then is is relaxed query", 1, products.size());
                assertEquals("CC_TEST4 is best (exact) match", "CC_TEST4", products.get(0).getCode());
                // Search by SKU in sub category
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(300L), true,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST4")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("This is fuzzy so we see all CC_TESTX which are in 104 (a sub of 101)", 1, products.size());
                assertEquals("CC_TEST4 is best match", "CC_TEST4", products.get(0).getCode());
                // Search by SKU in current category with sub categories flag on
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(313L), true,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST4")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("This is fuzzy so we see all CC_TESTX which are in 104 a sub of 101", 1, products.size());
                assertEquals("CC_TEST4 is best match", "CC_TEST4", products.get(0).getCode());
                // search by Sku code (misspelt)
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L, 313L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TESTX")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("This is fuzzy so we see all CC_TESTX", 10, products.size());
                // search by Sku code with stems
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L, 313L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("cc_test4")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Relaxed search should give all cc_test skus", 10, products.size());
                assertEquals("CC_TEST4 is best match", "CC_TEST4", products.get(0).getCode());
                // search by sku id
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.SKU_ID_FIELD, (List) Arrays.asList("11004")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(1, products.size());
                assertEquals("PRODUCT5", products.get(0).getSku().iterator().next().getCode());
                //test fuzzy search
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("blender")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse(products.isEmpty());
                assertTrue("BENDER is best match", Arrays.asList("BENDER", "BENDER-ua").contains(products.get(0).getCode()));
                //test search by description
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("Rodriguez Bending")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertTrue(!products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("DiMaggio")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse(products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());
                // search on empty string
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse(products.isEmpty()); //return all product in described categories

                // search on brand
                // product with code "PRODUCT1" also future robotics, but not assigned to any category
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("FutureRobots")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(2, products.size());

                // search on primary attribute MATERIAL ('metal')
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("metal")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(1, products.size());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());
                // search on primary attribute MATERIAL no fuzzy! ('metall')
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("metall")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(0, products.size());


                // query search via linked category (price must be set for shop 70)
                context = luceneQueryFactory.getFilteredNavigationQueryChain(70L, Arrays.asList(313L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST4")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(1, products.size());
                assertEquals("CC_TEST4 is best match", "CC_TEST4", products.get(0).getCode());

                // basic search in a sub shop
                context = luceneQueryFactory.getFilteredNavigationQueryChain(1010L, Arrays.asList(101L, 104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertTrue("Failed bender price is inherited [" + context.getProductQuery().toString() + "]", !products.isEmpty());
                assertProduct(products, "BENDER");

                context = luceneQueryFactory.getFilteredNavigationQueryChain(1010L, Arrays.asList(104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST1")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertTrue("Failed CC_TEST1 price is inherited [" + context.getProductQuery().toString() + "]", !products.isEmpty());
                assertProduct(products, "CC_TEST1");

                context = luceneQueryFactory.getFilteredNavigationQueryChain(1010L, Arrays.asList(104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST5")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertTrue("Failed CC_TEST5 price is inherited [" + context.getProductQuery().toString() + "]", !products.isEmpty());
                assertProduct(products, "CC_TEST5");

                // basic search in a sub shop (strict)
                context = luceneQueryFactory.getFilteredNavigationQueryChain(1011L, Arrays.asList(101L, 104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertTrue("Failed bender is availability SHOWROOM [" + context.getProductQuery().toString() + "]", !products.isEmpty());
                assertProduct(products, "BENDER");

                context = luceneQueryFactory.getFilteredNavigationQueryChain(1011L, Arrays.asList(104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST1")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertTrue("Failed CC_TEST1 price is in 1011 [" + context.getProductQuery().toString() + "]", !products.isEmpty());
                assertProduct(products, "CC_TEST1");

                context = luceneQueryFactory.getFilteredNavigationQueryChain(1011L, Arrays.asList(104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST5")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertTrue("Failed CC_TEST5 price is not in 1011 [" + context.getProductQuery().toString() + "]", !products.isEmpty());
                assertNoProduct(products, "CC_TEST5");

                status.setRollbackOnly();
            }
        });
    }


    @Test
    public void testShopQuerySearch() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

                NavigationContext context;

                // basic search
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                List<Product> products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse("Product must be found in 10 shop. Failed [" + context.getProductQuery().toString() + "]", products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());

                // search by name exact
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("Bender Bending Rodriguez")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("BENDER and BENDER-ua are best matches", 2, products.size());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(20L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse("The same categories assigned to 20 shop. Failed [" + context.getProductQuery().toString() + "]", products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(30L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertTrue("Product not present on 30 shop. Failed [" + context.getProductQuery().toString() + "]", products.isEmpty());

                // search exact by Sku code
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.SKU_PRODUCT_CODE_FIELD, (List) Arrays.asList("CC_TEST4")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("CC_TEST1 - CC_TEST9 are in this shop " + context.getProductQuery().toString(), 1, products.size());
                assertEquals("CC_TEST4 is best match", "CC_TEST4", products.get(0).getCode());

                // search by Sku code (exact match)
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST4")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("CC_TEST1 - CC_TEST9 are in this shop " + context.getProductQuery().toString(), 1, products.size());
                assertEquals("CC_TEST4 is best match", "CC_TEST4", products.get(0).getCode());

                // search fuzzy by Sku code
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TESTX")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("CC_TEST1 - CC_TEST9 + CC_TEST5-NOINV are in this shop " + context.getProductQuery().toString(), 10, products.size());

                //test fuzzy search
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("blender")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse(products.isEmpty());
                assertTrue("BENDER is best match", Arrays.asList("BENDER", "BENDER-ua").contains(products.get(0).getCode()));

                //test search by description
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("Rodriguez Bending")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse(products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("DiMaggio")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse(products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());

                // search on empty string
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse(products.isEmpty()); //return all product in described categories

                // search on brand
                // product with code "PRODUCT1" also future robotics, but not assigned to any category,
                // and can not be found via shop, because it has qty on warehouse, but category not assigned to 20 shop
                context = luceneQueryFactory.getFilteredNavigationQueryChain(20L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("FutureRobots")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(context.getProductQuery().toString(), 2, products.size());

                // search on primary attribute MATERIAL ('metal')
                context = luceneQueryFactory.getFilteredNavigationQueryChain(20L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("metal")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(1, products.size());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());
                // search on primary attribute MATERIAL no fuzzy! ('metall')
                context = luceneQueryFactory.getFilteredNavigationQueryChain(20L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("metall")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(0, products.size());


                // query search via linked category
                context = luceneQueryFactory.getFilteredNavigationQueryChain(70L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST4")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(1, products.size());
                assertEquals("CC_TEST4 is best match", "CC_TEST4", products.get(0).getCode());


                // basic search in a sub shop
                context = luceneQueryFactory.getFilteredNavigationQueryChain(1010L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse("Product must be found in 10 shop. Failed [" + context.getProductQuery().toString() + "]", products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());

                // basic search in a sub shop strict price
                context = luceneQueryFactory.getFilteredNavigationQueryChain(1011L, null, false,
                        Collections.<String, List>emptyMap());
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse("Product must be found in 10 shop. Failed [" + context.getProductQuery().toString() + "]", products.isEmpty());
                assertEquals("There are only 3 products with price + 1 show room", 4, products.size());


                status.setRollbackOnly();
            }
        });
    }


    @Test
    public void testFuzzySearch() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

                NavigationContext context;

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                List<Product> products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse("Failed [" + context.getProductQuery().toString() + "]", products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());

                // search by Sku code
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST99")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse("Failed [" + context.getProductQuery().toString() + "]", products.isEmpty());
                assertEquals("CC_TEST9 is best match but also 2 digit ones applicable 10,11,12", "CC_TEST9", products.get(0).getCode());
                assertEquals(4, products.size());
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEZT9")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Misspelling does not increase the score and should find a match", 1, products.size());
                assertEquals("CC_TEST9 is best match", "CC_TEST9", products.get(0).getCode());
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("cc_test99")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse(products.isEmpty());
                assertEquals("CC_TEST9 is best match", "CC_TEST9", products.get(0).getCode());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("CC_TEST99")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse(products.isEmpty());
                assertEquals("CC_TEST9 is best match", "CC_TEST9", products.get(0).getCode());

                // Fuzzy in sub shops
                context = luceneQueryFactory.getFilteredNavigationQueryChain(1010L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("bender")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertFalse("Failed [" + context.getProductQuery().toString() + "]", products.isEmpty());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());


                status.setRollbackOnly();
            }
        });
    }


    @Test
    public void testSkuRelevancySearch() throws InterruptedException {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);
                productSkuDao.fullTextSearchReindex(false, 1000);

                NavigationContext pContext;
                NavigationContext sContext;

                // Search for Sobot Light (it is name of SKU in Sobot product) only indexed attributes are considered

                pContext = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("Sobot Light")));

                // Get product search result
                ProductSearchResultPageDTO page = productService.getProductSearchResultDTOByQuery(pContext.getProductQuery(), 0, 100, null, false);
                List<ProductSearchResultDTO> products = page.getResults();
                assertFalse("Failed [" + pContext.getProductQuery().toString() + "]", products.isEmpty());
                assertEquals("SOBOT is best match", "SOBOT", products.get(0).getCode());

                sContext = luceneQueryFactory.getSkuSnowBallQuery(pContext, products);

                // Get relevant SKU
                List<ProductSkuSearchResultDTO> skus = productSkuService.getProductSkuSearchResultDTOByQuery(sContext.getProductSkuQuery());
                assertFalse("Failed [" + sContext.getProductSkuQuery().toString() + "]", skus.isEmpty());
                assertEquals("SOBOT is best match", products.get(0).getId(), skus.get(0).getProductId());
                assertEquals("There are 4 SKU", 4, skus.size());
                assertEquals("SOBOT-LIGHT is best match", "SOBOT-LIGHT", skus.get(0).getCode());


                // Search for Sobot xxl (it is size of a SOBOT-ORIG SKU in Sobot product) only indexed attributes are considered

                pContext = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("Sobot xxl")));

                // Get product search result
                page = productService.getProductSearchResultDTOByQuery(pContext.getProductQuery(), 0, 100, null, false);
                products = page.getResults();
                assertFalse("Failed [" + pContext.getProductQuery().toString() + "]", products.isEmpty());
                assertEquals("SOBOT is best match", "SOBOT", products.get(0).getCode());

                sContext = luceneQueryFactory.getSkuSnowBallQuery(pContext, products);

                // Get relevant SKU
                skus = productSkuService.getProductSkuSearchResultDTOByQuery(sContext.getProductSkuQuery());
                assertFalse("Failed [" + sContext.getProductSkuQuery().toString() + "]", skus.isEmpty());
                assertEquals("SOBOT is best match", products.get(0).getId(), skus.get(0).getProductId());
                assertEquals("There are 4 SKU", 4, skus.size());
                assertEquals("SOBOT-ORIG is best match", "SOBOT-ORIG", skus.get(0).getCode());


                // Search for Sobot small (it is size of a SOBOT-PINK SKU in Sobot product) only indexed attributes are considered

                pContext = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, (List) Arrays.asList("Sobot large")));

                // Get product search result
                page = productService.getProductSearchResultDTOByQuery(pContext.getProductQuery(), 0, 100, null, false);
                products = page.getResults();
                assertFalse("Failed [" + pContext.getProductQuery().toString() + "]", products.isEmpty());
                assertEquals("SOBOT is best match", "SOBOT", products.get(0).getCode());

                sContext = luceneQueryFactory.getSkuSnowBallQuery(pContext, products);

                // Get relevant SKU
                skus = productSkuService.getProductSkuSearchResultDTOByQuery(sContext.getProductSkuQuery());
                assertFalse("Failed [" + sContext.getProductSkuQuery().toString() + "]", skus.isEmpty());
                assertEquals("SOBOT is best match", products.get(0).getId(), skus.get(0).getProductId());
                assertEquals("There are 4 SKU", 4, skus.size());
                assertEquals("SOBOT-PINK is best match", "SOBOT-PINK", skus.get(0).getCode());

                status.setRollbackOnly();
            }
        });

    }


    @Test
    public void testBrandFaceting() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

                NavigationContext context;
                Map<String, List<Pair<String, Integer>>> facets;
                List<Pair<String, Integer>> brandFacetResults;
                final FilteredNavigationRecordRequest brands =
                        new FilteredNavigationRecordRequestImpl(
                                "brandFacet",
                                ProductSearchQueryBuilder.BRAND_FIELD
                        );


                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L), false, null);
                facets = productDao.fullTextSearchNavigation(context.getProductQuery(), Collections.singletonList(brands));
                assertEquals("Failed [" + context.getProductQuery().toString() +"]", 1, facets.size());

                brandFacetResults = facets.get("brandFacet");
                assertNotNull(brandFacetResults);
                assertEquals(2, brandFacetResults.size());

                final List<Pair<String, Integer>> expectedInCategory = Arrays.asList(
                        new Pair<String, Integer>("futurerobots", 1),
                        new Pair<String, Integer>("unknown", 1)
                );

                for (final Pair<String, Integer> brandFacetResultItem : brandFacetResults) {
                    assertTrue("Unexpected pair: " + brandFacetResultItem, expectedInCategory.contains(brandFacetResultItem));
                }

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false, null);
                facets = productDao.fullTextSearchNavigation(context.getProductQuery(), Collections.singletonList(brands));
                assertEquals("Failed [" + context.getProductQuery().toString() +"]", 1, facets.size());

                brandFacetResults = facets.get("brandFacet");
                assertNotNull(brandFacetResults);
                assertEquals(6, brandFacetResults.size());

                final List<Pair<String, Integer>> expectedInShop = Arrays.asList(
                        new Pair<String, Integer>("cc tests", 13),
                        new Pair<String, Integer>("futurerobots", 3),
                        new Pair<String, Integer>("samsung", 2),
                        new Pair<String, Integer>("sony", 1),
                        new Pair<String, Integer>("lg", 1),
                        new Pair<String, Integer>("unknown", 1)
                );

                for (final Pair<String, Integer> brandFacetResultItem : brandFacetResults) {
                    assertTrue("Unexpected pair: " + brandFacetResultItem, expectedInShop.contains(brandFacetResultItem));
                }


                // Brand faceting in a sub shop

                context = luceneQueryFactory.getFilteredNavigationQueryChain(1010L, Arrays.asList(101L), false, null);
                facets = productDao.fullTextSearchNavigation(context.getProductQuery(), Collections.singletonList(brands));
                assertEquals("Failed [" + context.getProductQuery().toString() +"]", 1, facets.size());

                brandFacetResults = facets.get("brandFacet");
                assertNotNull(brandFacetResults);
                assertEquals(2, brandFacetResults.size());

                final List<Pair<String, Integer>> subExpectedInCategory = Arrays.asList(
                        new Pair<String, Integer>("futurerobots", 1),
                        new Pair<String, Integer>("unknown", 1)
                );

                for (final Pair<String, Integer> brandFacetResultItem : brandFacetResults) {
                    assertTrue("Unexpected pair: " + brandFacetResultItem, subExpectedInCategory.contains(brandFacetResultItem));
                }



                status.setRollbackOnly();

            }
        });

    }


    @Test
    public void testPriceFaceting() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

                NavigationContext context;
                Map<String, List<Pair<String, Integer>>> facets;
                List<Pair<String, Integer>> priceFacetResults;

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 131L, 132L), false, null);

                // There should be the following products:
                // PRODUCT1  15.00 EUR
                // PRODUCT2  30.00 EUR
                // PRODUCT3 250.00 EUR
                // PRODUCT4 400.00 EUR
                // PRODUCT5 420.00 EUR

                final FilteredNavigationRecordRequest priceInCategories =
                        new FilteredNavigationRecordRequestImpl(
                                "priceFacet",
                                "facet_price_10_EUR",
                                new ArrayList<Pair<String, String>>() {{
                                    add(new Pair<String, String>("00000000", "00001500")); // test exclusive hi PRODUCT1
                                    add(new Pair<String, String>("00001500", "00001600")); // test inclusive lo PRODUCT1
                                    add(new Pair<String, String>("00001600", "00030000")); // 2 in range, overlapping PRODUCT3
                                    add(new Pair<String, String>("00025000", "00030000")); // overlapping PRODUCT3
                                    add(new Pair<String, String>("00030000", "00040000")); // blank bucket
                                    add(new Pair<String, String>("00030000", "01000000")); // 2 in range
                                }}
                        );

                facets = productDao.fullTextSearchNavigation(context.getProductQuery(), Collections.singletonList(priceInCategories));
                assertEquals("Failed [" + context.getProductQuery().toString() +"]", 1, facets.size());

                priceFacetResults = facets.get("priceFacet");
                assertNotNull(priceFacetResults);
                assertEquals(6, priceFacetResults.size());
                assertEquals("[00000000, 00001500)", priceFacetResults.get(0).getFirst());
                assertEquals(Integer.valueOf(0), priceFacetResults.get(0).getSecond());
                assertEquals("[00001500, 00001600)", priceFacetResults.get(1).getFirst());
                assertEquals(Integer.valueOf(1), priceFacetResults.get(1).getSecond());
                assertEquals("[00001600, 00030000)", priceFacetResults.get(2).getFirst());
                assertEquals(Integer.valueOf(2), priceFacetResults.get(2).getSecond());
                assertEquals("[00025000, 00030000)", priceFacetResults.get(3).getFirst());
                assertEquals(Integer.valueOf(1), priceFacetResults.get(3).getSecond());
                assertEquals("[00030000, 00040000)", priceFacetResults.get(4).getFirst());
                assertEquals(Integer.valueOf(0), priceFacetResults.get(4).getSecond());
                assertEquals("[00030000, 01000000]", priceFacetResults.get(5).getFirst());
                assertEquals(Integer.valueOf(2), priceFacetResults.get(5).getSecond());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false, null);

                // final Pair<List<Object[]>, Integer> products = productDao.fullTextSearch(context.getProductQuery(), 0, Integer.MAX_VALUE, null, false, ProductSearchQueryBuilder.PRODUCT_CODE_FIELD);
                // for (final Object[] product : products.getFirst()) {
                //     java.lang.System.out.println(product[0]);
                // }

                // There should be the following products:
                // BENDER-ua  99.99 EUR
                // BENDER     --.--
                // SOBOT
                //    BEER   150.85 EUR
                //    PINK   150.85 EUR
                //    LIGHT  145.00 EUR <- lowest
                //    ORIG   150.85 EUR
                // CC_TEST4
                //           123.00 EUR
                //          1230.00 EUR
                //            99.99 EUR <- lowest
                //           990.99 EUR
                // CC_TEST10
                //            23.10 EUR <- lowest
                //            99.09 EUR
                // CC_TEST5
                //            12.00 EUR <- lowest
                //            99.09 EUR
                // CC_TEST5-NOINV
                //            12.00 EUR <- lowest
                // CC_TEST6
                //            55.17 EUR <- lowest
                //            80.99 EUR
                // CC_TEST7
                //            55.17 EUR <- lowest
                //            80.99 EUR
                // CC_TEST1
                //            19.99 EUR
                //           190.99 EUR
                //            19.00 EUR
                //           190.01 EUR
                //            18.00 EUR <- lowest
                //           180.00 EUR
                // CC_TEST2
                //            29.99 EUR
                //           290.99 EUR
                //            22.17 EUR <- lowest
                //           220.17 EUR
                // CC_TEST3
                //             7.99 EUR
                //            70.95 EUR
                //             7.00 EUR <- lowest
                //            70.00 EUR
                // CC_TEST12
                //             7.99 EUR
                //            70.95 EUR
                //             7.00 EUR <- lowest
                //            70.00 EUR
                // CC_TEST11
                //            10.10 EUR <- lowest
                //            17.15 EUR
                // CC_TEST8
                //            55.17 EUR <- lowest
                //            87.99 EUR
                // CC_TEST9
                //            65.17 EUR <- lowest
                //            88.99 EUR
                // PRODUCT1   15.00 EUR
                // PRODUCT2   30.00 EUR
                // PRODUCT3  250.00 EUR
                // PRODUCT4  400.00 EUR
                // PRODUCT5  420.00 EUR

                final FilteredNavigationRecordRequest priceInShop1 =
                        new FilteredNavigationRecordRequestImpl(
                                "priceFacet",
                                "facet_price_10_EUR",
                                new ArrayList<Pair<String, String>>() {{
                                    add(new Pair<String, String>("00000000", "00001500"));
                                    add(new Pair<String, String>("00001500", "00001600"));
                                    add(new Pair<String, String>("00001600", "00030000"));
                                    add(new Pair<String, String>("00025000", "00030000"));
                                    add(new Pair<String, String>("00030000", "00040000"));
                                    add(new Pair<String, String>("00030000", "01000000"));
                                }}
                        );


                facets = productDao.fullTextSearchNavigation(context.getProductQuery(), Collections.singletonList(priceInShop1));
                assertEquals("Failed [" + context.getProductQuery().toString() +"]", 1, facets.size());

                priceFacetResults = facets.get("priceFacet");
                assertNotNull(priceFacetResults);
                assertEquals(6, priceFacetResults.size());
                assertEquals("[00000000, 00001500)", priceFacetResults.get(0).getFirst());
                assertEquals(Integer.valueOf(5), priceFacetResults.get(0).getSecond());
                assertEquals("[00001500, 00001600)", priceFacetResults.get(1).getFirst());
                assertEquals(Integer.valueOf(1), priceFacetResults.get(1).getSecond());
                assertEquals("[00001600, 00030000)", priceFacetResults.get(2).getFirst());
                assertEquals(Integer.valueOf(12), priceFacetResults.get(2).getSecond());
                assertEquals("[00025000, 00030000)", priceFacetResults.get(3).getFirst());
                assertEquals(Integer.valueOf(1), priceFacetResults.get(3).getSecond());
                assertEquals("[00030000, 00040000)", priceFacetResults.get(4).getFirst());
                assertEquals(Integer.valueOf(0), priceFacetResults.get(4).getSecond());
                assertEquals("[00030000, 01000000]", priceFacetResults.get(5).getFirst());
                assertEquals(Integer.valueOf(2), priceFacetResults.get(5).getSecond());

                final FilteredNavigationRecordRequest priceInShop2 =
                        new FilteredNavigationRecordRequestImpl(
                                "priceFacet",
                                "facet_price_10_EUR",
                                new ArrayList<Pair<String, String>>() {{
                                    add(new Pair<String, String>("00000000", "00001000"));
                                    add(new Pair<String, String>("00001000", "00001500"));
                                    add(new Pair<String, String>("00001500", "00002000"));
                                    add(new Pair<String, String>("00002000", "00002500"));
                                    add(new Pair<String, String>("00002500", "00006000"));
                                    add(new Pair<String, String>("00006000", "00010000"));
                                    add(new Pair<String, String>("00010000", "01000000"));
                                }}
                        );

                facets = productDao.fullTextSearchNavigation(context.getProductQuery(), Collections.singletonList(priceInShop2));
                assertEquals("Failed [" + context.getProductQuery().toString() +"]", 1, facets.size());

                priceFacetResults = facets.get("priceFacet");
                assertNotNull(priceFacetResults);
                assertEquals(7, priceFacetResults.size());
                assertEquals("[00000000, 00001000)", priceFacetResults.get(0).getFirst());
                assertEquals(Integer.valueOf(2), priceFacetResults.get(0).getSecond());
                assertEquals("[00001000, 00001500)", priceFacetResults.get(1).getFirst());
                assertEquals(Integer.valueOf(3), priceFacetResults.get(1).getSecond());
                assertEquals("[00001500, 00002000)", priceFacetResults.get(2).getFirst());
                assertEquals(Integer.valueOf(2), priceFacetResults.get(2).getSecond());
                assertEquals("[00002000, 00002500)", priceFacetResults.get(3).getFirst());
                assertEquals(Integer.valueOf(1), priceFacetResults.get(3).getSecond());
                assertEquals("[00002500, 00006000)", priceFacetResults.get(4).getFirst());
                assertEquals(Integer.valueOf(5), priceFacetResults.get(4).getSecond());
                assertEquals("[00006000, 00010000)", priceFacetResults.get(5).getFirst());
                assertEquals(Integer.valueOf(2), priceFacetResults.get(5).getSecond());
                assertEquals("[00010000, 01000000]", priceFacetResults.get(6).getFirst());
                assertEquals(Integer.valueOf(5), priceFacetResults.get(6).getSecond());



                // Sub shop price facting

                context = luceneQueryFactory.getFilteredNavigationQueryChain(1010L, Arrays.asList(129L, 130L, 131L, 132L), false, null);

                // There should be the following products:
                // PRODUCT1  15.00 EUR
                // PRODUCT2  30.00 EUR
                // PRODUCT3 250.00 EUR
                // PRODUCT4 400.00 EUR
                // PRODUCT5 420.00 EUR

                final FilteredNavigationRecordRequest subPriceInCategories =
                        new FilteredNavigationRecordRequestImpl(
                                "priceFacet",
                                "facet_price_1010_EUR",
                                new ArrayList<Pair<String, String>>() {{
                                    add(new Pair<String, String>("00000000", "00001500")); // test exclusive hi PRODUCT1
                                    add(new Pair<String, String>("00001500", "00001600")); // test inclusive lo PRODUCT1
                                    add(new Pair<String, String>("00001600", "00030000")); // 2 in range, overlapping PRODUCT3
                                    add(new Pair<String, String>("00025000", "00030000")); // overlapping PRODUCT3
                                    add(new Pair<String, String>("00030000", "00040000")); // blank bucket
                                    add(new Pair<String, String>("00030000", "01000000")); // 2 in range
                                }}
                        );

                facets = productDao.fullTextSearchNavigation(context.getProductQuery(), Collections.singletonList(subPriceInCategories));
                assertEquals("Failed [" + context.getProductQuery().toString() +"]", 1, facets.size());

                priceFacetResults = facets.get("priceFacet");
                assertNotNull(priceFacetResults);
                assertEquals(6, priceFacetResults.size());
                assertEquals("[00000000, 00001500)", priceFacetResults.get(0).getFirst());
                assertEquals(Integer.valueOf(0), priceFacetResults.get(0).getSecond());
                assertEquals("[00001500, 00001600)", priceFacetResults.get(1).getFirst());
                assertEquals(Integer.valueOf(1), priceFacetResults.get(1).getSecond());
                assertEquals("[00001600, 00030000)", priceFacetResults.get(2).getFirst());
                assertEquals(Integer.valueOf(2), priceFacetResults.get(2).getSecond());
                assertEquals("[00025000, 00030000)", priceFacetResults.get(3).getFirst());
                assertEquals(Integer.valueOf(1), priceFacetResults.get(3).getSecond());
                assertEquals("[00030000, 00040000)", priceFacetResults.get(4).getFirst());
                assertEquals(Integer.valueOf(0), priceFacetResults.get(4).getSecond());
                assertEquals("[00030000, 01000000]", priceFacetResults.get(5).getFirst());
                assertEquals(Integer.valueOf(2), priceFacetResults.get(5).getSecond());




                status.setRollbackOnly();

            }
        });

    }

    @Test
    public void testAttributeFaceting() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

                NavigationContext context;
                Map<String, List<Pair<String, Integer>>> facets;
                List<Pair<String, Integer>> materialFacetResults;
                List<Pair<String, Integer>> sizeFacetResults;
                final FilteredNavigationRecordRequest material =
                        new FilteredNavigationRecordRequestImpl("MATERIAL", "facet_MATERIAL");
                final FilteredNavigationRecordRequest size =
                        new FilteredNavigationRecordRequestImpl("SIZE", "facet_SIZE", true);


                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L), false, null);
                facets = productDao.fullTextSearchNavigation(context.getProductQuery(), Arrays.asList(material, size));
                assertEquals("Failed [" + context.getProductQuery().toString() +"]", 2, facets.size());

                materialFacetResults = facets.get("MATERIAL");
                assertNotNull(materialFacetResults);
                assertEquals(2, materialFacetResults.size());

                final List<Pair<String, Integer>> expectedMaterialInCategory = Arrays.asList(
                        new Pair<String, Integer>("Plastik", 1),
                        new Pair<String, Integer>("metal", 1)
                );

                for (final Pair<String, Integer> materialFacetResultItem : materialFacetResults) {
                    assertTrue("Unexpected pair: " + materialFacetResultItem, expectedMaterialInCategory.contains(materialFacetResultItem));
                }

                sizeFacetResults = facets.get("SIZE");
                assertNotNull(sizeFacetResults);
                assertEquals(4, sizeFacetResults.size());

                final List<Pair<String, Integer>> expectedSizeInCategory = Arrays.asList(
                        new Pair<String, Integer>("small", 1),
                        new Pair<String, Integer>("medium", 1),
                        new Pair<String, Integer>("large", 1),
                        new Pair<String, Integer>("xxl", 1)
                );

                for (final Pair<String, Integer> sizeFacetResultItem : sizeFacetResults) {
                    assertTrue("Unexpected pair: " + sizeFacetResultItem, expectedSizeInCategory.contains(sizeFacetResultItem));
                }


                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false, null);
                facets = productDao.fullTextSearchNavigation(context.getProductQuery(), Arrays.asList(material, size));
                assertEquals("Failed [" + context.getProductQuery().toString() +"]", 2, facets.size());


                materialFacetResults = facets.get("MATERIAL");
                assertNotNull(materialFacetResults);
                assertEquals(2, materialFacetResults.size());

                final List<Pair<String, Integer>> expectedMaterialInShop = Arrays.asList(
                        new Pair<String, Integer>("Plastik", 1),
                        new Pair<String, Integer>("metal", 1)
                );

                for (final Pair<String, Integer> materialFacetResultItem : materialFacetResults) {
                    assertTrue("Unexpected pair: " + materialFacetResultItem, expectedMaterialInShop.contains(materialFacetResultItem));
                }

                sizeFacetResults = facets.get("SIZE");
                assertNotNull(sizeFacetResults);
                assertEquals(4, sizeFacetResults.size());

                final List<Pair<String, Integer>> expectedSizeInShop = Arrays.asList(
                        new Pair<String, Integer>("small", 1),
                        new Pair<String, Integer>("medium", 1),
                        new Pair<String, Integer>("large", 1),
                        new Pair<String, Integer>("xxl", 1)
                );

                for (final Pair<String, Integer> sizeFacetResultItem : sizeFacetResults) {
                    assertTrue("Unexpected pair: " + sizeFacetResultItem, expectedSizeInShop.contains(sizeFacetResultItem));
                }


                // Sub shop attribute faceting

                context = luceneQueryFactory.getFilteredNavigationQueryChain(1010L, Arrays.asList(101L), false, null);
                facets = productDao.fullTextSearchNavigation(context.getProductQuery(), Arrays.asList(material, size));
                assertEquals("Failed [" + context.getProductQuery().toString() +"]", 2, facets.size());

                materialFacetResults = facets.get("MATERIAL");
                assertNotNull(materialFacetResults);
                assertEquals(2, materialFacetResults.size());

                final List<Pair<String, Integer>> subExpectedMaterialInCategory = Arrays.asList(
                        new Pair<String, Integer>("Plastik", 1),
                        new Pair<String, Integer>("metal", 1)
                );

                for (final Pair<String, Integer> materialFacetResultItem : materialFacetResults) {
                    assertTrue("Unexpected pair: " + materialFacetResultItem, subExpectedMaterialInCategory.contains(materialFacetResultItem));
                }

                sizeFacetResults = facets.get("SIZE");
                assertNotNull(sizeFacetResults);
                assertEquals(4, sizeFacetResults.size());

                final List<Pair<String, Integer>> subExpectedSizeInCategory = Arrays.asList(
                        new Pair<String, Integer>("small", 1),
                        new Pair<String, Integer>("medium", 1),
                        new Pair<String, Integer>("large", 1),
                        new Pair<String, Integer>("xxl", 1)
                );

                for (final Pair<String, Integer> sizeFacetResultItem : sizeFacetResults) {
                    assertTrue("Unexpected pair: " + sizeFacetResultItem, subExpectedSizeInCategory.contains(sizeFacetResultItem));
                }



                status.setRollbackOnly();

            }
        });

    }


    @Test
    public void testMultiValueSpecificFieldSearch() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

                NavigationContext context;

                // search by sku id with multiple values
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.SKU_ID_FIELD, (List) Arrays.asList("11003", "11004")));
                List<Product> products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(2, products.size());

                final List<String> expected = Arrays.asList("PRODUCT4", "PRODUCT5");
                for (final Product product : products) {
                    assertTrue(expected.contains(product.getSku().iterator().next().getCode()));
                }

                status.setRollbackOnly();

            }
        });

    }


    @Test
    public void testCategoryBrowsing() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                productDao.fullTextSearchReindex(false, 1000);

                NavigationContext context;

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L), false, null);
                List<Product> products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Failed [" + context.getProductQuery().toString() +"]", 2, products.size());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 200L, 123L, 2435L), false, null);
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Failed [" + context.getProductQuery().toString() + "]", 2, products.size());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 104L), false, null);
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Failed [" + context.getProductQuery().toString() +"]", 15, products.size());

                status.setRollbackOnly();

            }
        });


    }

    @Test
    public void testTagBrowsingTest() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                productDao.fullTextSearchReindex(false, 1000);

                NavigationContext context;

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD, (List) Arrays.asList("newarrival")));
                List<Product> products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Failed [" + context.getProductQuery().toString() +"] expected 2 products", 2, products.size());
                assertTrue("Must have tag", products.get(0).getTag().contains("newarrival"));
                assertTrue("Must have tag", products.get(1).getTag().contains("newarrival"));

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD, (List) Arrays.asList("sale")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Failed [" + context.getProductQuery().toString() + "] expected 2 products", 2, products.size());
                assertTrue("Must have tag", products.get(0).getTag().contains("sale"));
                assertTrue("Must have tag", products.get(1).getTag().contains("sale"));

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD, (List) Arrays.asList("specialpromo")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Failed [" + context.getProductQuery().toString() + "] expected 2 products", 2, products.size());
                assertTrue("Must have tag", products.get(0).getTag().contains("specialpromo"));
                assertTrue("Must have tag", products.get(1).getTag().contains("specialpromo"));

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD, (List) Arrays.asList("sali")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertTrue("Failed [" + context.getProductQuery().toString() +"] expected 0 products, tags are exact match", products.isEmpty());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(104L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_TAG_FIELD, (List) Arrays.asList("sale")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Failed [" + context.getProductQuery().toString() +"] expected 1 products", 1, products.size());
                assertTrue("Must have tag", products.get(0).getTag().contains("sale"));

                status.setRollbackOnly();

            }
        });

    }


    @Test
    public void testSingleValueAttributeNavigation() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                productDao.fullTextSearchReindex(false, 1000);

                NavigationContext context;

                // Test that we able to find Bender by his material in category where he exists
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("metal")));
                List<Product> products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Query [" + context.getProductQuery().toString() + "] failed", 1, products.size());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());

                // Test that we able to find Bender by his material in  list of categories where he exists
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L, 200L), false,
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("metal")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(1, products.size());
                assertEquals("BENDER is best match", "BENDER", products.get(0).getCode());



                // Test that we able to find Sobot by his material in category where he exists
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L), false,
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("Plastik")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(1, products.size());
                assertEquals("SOBOT is best match", "SOBOT", products.get(0).getCode());
                //No category limitation, so we expect all plastic robots
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("Plastik")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(context.getProductQuery().toString(), 1, products.size());
                assertEquals("SOBOT is best match", "SOBOT", products.get(0).getCode());
                // Robot from plastic not in 104 category
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(104L), false,
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("Plastik")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(0, products.size());


                //We are unable to find products manufactured from bananas
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L), false,
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("banana")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(0, products.size());
                //We are unable to find products manufactured from bananas
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap("MATERIAL", (List) Arrays.asList("banana")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals(0, products.size());


                // search by sku attribute value
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, null, false,
                        Collections.singletonMap("SMELL", (List) Arrays.asList("apple")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertTrue("Failed [" + context.getProductQuery() + "] SMELL is not nav attr, so we return all", products.size() > 1);



                // sub shop search by sku attribute value
                context = luceneQueryFactory.getFilteredNavigationQueryChain(1010L, null, false,
                        Collections.singletonMap("SMELL", (List) Arrays.asList("apple")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertTrue("Failed [" + context.getProductQuery() + "] SMELL is not nav attr, so we return all", products.size() > 1);


                status.setRollbackOnly();

            }
        });


    }


    @Test
    public void testGetRangeAttributeValueNavigation() throws InterruptedException {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

                NavigationContext context;

                // range values are excluding high value
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L), false,
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("0.001-_-2.3")));
                List<Product> products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Range search with query [" + context.getProductQuery() + "] incorrect", 2, products.size());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L), false,
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("0.001-_-2.31")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Range search with query [" + context.getProductQuery() + "] incorrect", 3, products.size());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L), false,
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.1-_-2.31")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Range search with query [" + context.getProductQuery() + "] incorrect", 3, products.size());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L), false,
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.35-_-2.36")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Range search with query [" + context.getProductQuery() + "] incorrect", 1, products.size());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L), false,
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.34-_-2.36")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Range search with query [" + context.getProductQuery() + "] incorrect", 1, products.size());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L), false,
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.35-_-2.38")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Range search with query [" + context.getProductQuery() + "] incorrect", 1, products.size());

                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(130L, 131L, 132L), false,
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.4-_-2.49")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Range search with query [" + context.getProductQuery() + "] incorrect", 0, products.size());

                // products not assigned to this category
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(100L), false,
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.1-_-2.5")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Range search with query [" + context.getProductQuery() + "] incorrect", 0, products.size());

                // products not assigned to this category
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(100L, 101L, 102L), false,
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("2.1-_-2.5")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Range search with query [" + context.getProductQuery() + "] incorrect", 0, products.size());


                // sub shops
                // range values are excluding high value
                context = luceneQueryFactory.getFilteredNavigationQueryChain(1010L, Arrays.asList(130L, 131L, 132L), false,
                        Collections.singletonMap("WEIGHT", (List) Arrays.asList("0.001-_-2.3")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertEquals("Range search with query [" + context.getProductQuery() + "] incorrect", 2, products.size());


                status.setRollbackOnly();

            }
        });
    }

    @Test
    public void testBrandNavigation() {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                createProduct(102L, "LG_DVD_PLAYER", "product lg dvd player", 3L, 134L, 10L);
                createProduct(104L, "SAM_DVD_PLAYER", "product sam mp3 player", 3L, 134L, 10L);
                createProduct(102L, "LG_MP3_PLAYER", "product lg mp3 player", 2L, 135L, 10L);
                createProduct(103L, "SONY_MP3_PLAYER", "product sony mp3 player", 2L, 135L, 10L);
                createProduct(104L, "SAM_MP3_PLAYER", "product sam mp3 player", 2L, 136L, 10L);
                // productDao.fullTextSearchReindex(false, 1000);
                List<Product> foundProducts;
                NavigationContext context;
                List<Long> categories = new ArrayList<Long>();

                //existing LG product in category 134
                categories.clear();
                categories.add(134L);
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, categories, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.BRAND_FIELD, (List) Arrays.asList("LG")));
                foundProducts = productDao.fullTextSearch(context.getProductQuery());
                assertNotNull(foundProducts);
                assertEquals(context.getProductQuery().toString(), 1, foundProducts.size());
                assertEquals("Must be correct brand", "LG", foundProducts.get(0).getBrand().getName());

                //existing two LG products in category 135 135
                categories.clear();
                categories.add(134L);
                categories.add(135L);
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, categories, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.BRAND_FIELD, (List) Arrays.asList("LG")));
                foundProducts = productDao.fullTextSearch(context.getProductQuery());
                assertNotNull(foundProducts);
                assertEquals(context.getProductQuery().toString(), 2, foundProducts.size());
                assertEquals("Must be correct brand", "LG", foundProducts.get(0).getBrand().getName());
                assertEquals("Must be correct brand", "LG", foundProducts.get(1).getBrand().getName());

                //only one Sony product in categories 135, 134,136
                categories.clear();
                categories.add(134L);
                categories.add(135L);
                categories.add(136L);
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, categories, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.BRAND_FIELD, (List) Arrays.asList("Sony")));
                foundProducts = productDao.fullTextSearch(context.getProductQuery());
                assertNotNull(foundProducts);
                assertEquals(context.getProductQuery().toString(), 1, foundProducts.size());
                assertEquals("Must be correct brand", "Sony", foundProducts.get(0).getBrand().getName());

                //LG prod not exists in 136 category
                categories.clear();
                categories.add(136L);
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, categories, false,
                        Collections.singletonMap(ProductSearchQueryBuilder.BRAND_FIELD, (List) Arrays.asList("LG")));
                foundProducts = productDao.fullTextSearch(context.getProductQuery());
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

                productDao.fullTextSearchReindex(false, 1000);

                NavigationContext context;

                // test that price border is excluding upper limit in search
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 131L, 132L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-12-_-15")));
                List<Product> products = productDao.fullTextSearch(context.getProductQuery());
                assertNotNull(context.getProductQuery().toString(), products);
                assertTrue(context.getProductQuery().toString(), products.isEmpty());
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 131L, 132L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-12-_-15.01")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertNotNull(context.getProductQuery().toString(), products);
                assertFalse(context.getProductQuery().toString(), products.isEmpty());
                assertEquals(1, products.size());

                // Test that filter by categories works. Categories 131 and 132
                // not contains product with price 15.00
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(131L, 132L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-15-_-15")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertNotNull(context.getProductQuery().toString(), products);
                assertTrue(context.getProductQuery().toString(), products.isEmpty());

                // prices less than 15
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 131L, 132L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-0-_-14")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertNotNull(context.getProductQuery().toString(), products);
                assertTrue(context.getProductQuery().toString(), products.isEmpty());

                // Test, that we able to all products,
                // that match search criteria
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 131L, 132L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-0-_-1000")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertNotNull(context.getProductQuery().toString(), products);
                assertTrue(context.getProductQuery().toString(), !products.isEmpty());
                assertEquals(5, products.size()); //Here 2 product have 0 quantity on warehouse

                // no price 250 in given categories
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 132L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-250-_-421")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertNotNull(context.getProductQuery().toString(), products);
                assertTrue(context.getProductQuery().toString(), !products.isEmpty());
                assertEquals(2, products.size());

                // Test, that filter by currency code works ok
                context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(129L, 130L, 131L, 132L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("USD-_-0-_-1000")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertNotNull(context.getProductQuery().toString(), products);
                assertTrue(context.getProductQuery().toString(), products.isEmpty());

                // Test, that filter by shop id works
                context = luceneQueryFactory.getFilteredNavigationQueryChain(20L, Arrays.asList(129L, 130L, 131L, 132L), false,
                        Collections.singletonMap(ProductSearchQueryBuilder.PRODUCT_PRICE, (List) Arrays.asList("EUR-_-0-_-1000")));
                products = productDao.fullTextSearch(context.getProductQuery());
                assertNotNull(context.getProductQuery().toString(), products);
                assertTrue(context.getProductQuery().toString(), products.isEmpty());

                status.setRollbackOnly();
            }
        });

    }

    private long createProduct(long brandId, String productCode, String productName, long productTypeId, long productCategoryId, long shopId) {
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
        // add quantity on warehouses
        SkuWarehouse skuWarehouse = new SkuWarehouseEntity();
        skuWarehouse.setSkuCode(productSku.getCode());
        skuWarehouse.setQuantity(BigDecimal.ONE);
        skuWarehouse.setWarehouse(warehouseDao.findById(2L));
        skuWareHouseDao.create(skuWarehouse);
        // add prices
        SkuPrice skuPrice = new SkuPriceEntity();
        skuPrice.setSkuCode(product.getCode());
        skuPrice.setQuantity(BigDecimal.ONE);
        skuPrice.setCurrency("EUR");
        skuPrice.setRegularPrice(new BigDecimal("9.99"));
        skuPrice.setShop(shopDao.findById(shopId));
        skuPriceDao.create(skuPrice);
        // reindex
        productDao.fullTextSearchReindex(product.getProductId());
        skuWareHouseDao.flushClear();
        return pk;
    }

    @Test
    public void testProductAvailability() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                productDao.fullTextSearchReindex(false, 1000);

                List<Product> products = productDao.fullTextSearch(luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(212L), false, null).getProductQuery());
                assertEquals("Only 5 product available in 212 category", 5, products.size());
                assertNull(getProductByCode(products, "PAT_PRODUCT_ON_STOCK_ONLY_1"));
                assertNotNull(getProductByCode(products, "PAT_PRODUCT_ON_STOCK_ONLY_2"));
                assertNull(getProductByCode(products, "PAT_PRODUCT_ON_STOCK_ONLY_3"));
                assertNotNull(getProductByCode(products, "PAT_PRODUCT_PREORDER"));
                assertNotNull(getProductByCode(products, "PAT_PRODUCT_BACKORDER"));
                assertNotNull(getProductByCode(products, "PAT_PRODUCT_ALWAYS"));
                assertNotNull(getProductByCode(products, "PAT_PRODUCT_ALWAYS2"));

                // sub shops availability
                products = productDao.fullTextSearch(luceneQueryFactory.getFilteredNavigationQueryChain(1010L, Arrays.asList(212L), false, null).getProductQuery());
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
    }

}
