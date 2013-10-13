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

package org.yes.cart.dao.impl;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.query.impl.*;
import org.yes.cart.service.domain.AttributeService;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
// TODO: YC-143 refactor to param test

public class ProductDAOTest extends AbstractTestDAO {

    private GenericDAO<Product, Long> productDao;
    private GenericDAO<ProductSku, Long> productSkuDao;
    private GenericDAO<Brand, Long> brandDao;
    private GenericDAO<ProductType, Long> productTypeDao;
    private GenericDAO<ProductCategory, Long> productCategoryDao;
    private GenericDAO<Category, Long> categoryDao;
    private GenericDAO<Attribute, Long> attributeDao;
    private GenericDAO<SkuWarehouse, Long> skuWareHouseDao;
    private GenericDAO<Warehouse, Long> warehouseDao;

    private AttributeService attributeService;

    private Mockery mockery = new JUnit4Mockery();

    @Before
    public void setUp()  {
        productDao = (GenericDAO<Product, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_DAO);
        productSkuDao = (GenericDAO<ProductSku, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_SKU_DAO);
        brandDao = (GenericDAO<Brand, Long>) ctx().getBean(DaoServiceBeanKeys.BRAND_DAO);
        productTypeDao = (GenericDAO<ProductType, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_TYPE_DAO);
        productCategoryDao = (GenericDAO<ProductCategory, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_CATEGORY_DAO);
        categoryDao = (GenericDAO<Category, Long>) ctx().getBean(DaoServiceBeanKeys.CATEGORY_DAO);
        attributeDao = (GenericDAO<Attribute, Long>) ctx().getBean(DaoServiceBeanKeys.ATTRIBUTE_DAO);
        skuWareHouseDao = (GenericDAO<SkuWarehouse, Long>) ctx().getBean(DaoServiceBeanKeys.SKU_WAREHOUSE_DAO);
        warehouseDao = (GenericDAO<Warehouse, Long>) ctx().getBean(DaoServiceBeanKeys.WAREHOUSE_DAO);


        attributeService = mockery.mock(AttributeService.class);

        super.setUp();
    }

    @Test
    public void testCreateProduct() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                Product product = new ProductEntity();
                product.setAvailability(Product.AVAILABILITY_STANDARD);
                Brand brand = brandDao.findById(100L);
                assertNotNull(brand);
                product.setBrand(brand);
                product.setCode("SONY_PRODUCT_CODE");
                product.setName("product sony name");
                product.setDescription("Description ");
                ProductType productType = productTypeDao.findById(1L);
                assertNotNull(productType);
                product.setProducttype(productType);
                Attribute attribute = attributeDao.findById(2006L);  //WEIGHT
                AttrValueProduct attrValueProduct = new AttrValueEntityProduct();
                //attrValueProduct.setAttrvalueId(33L);
                attrValueProduct.setProduct(product);
                attrValueProduct.setVal("100");
                attrValueProduct.setAttribute(attribute);
                product.getAttributes().add(attrValueProduct);
                long pk = productDao.create(product).getProductId();
                assertTrue(pk > 0L);
                product = new ProductEntity();
                product.setAvailability(Product.AVAILABILITY_STANDARD);
                brand = brandDao.findById(100L);
                assertNotNull(brand);
                product.setBrand(brand);
                product.setCode("SONY_PRODUCT_CODE2");
                product.setName("product sony name 2");
                product.setDescription("Description2");
                productType = productTypeDao.findById(1L);
                assertNotNull(productType);
                product.setProducttype(productType);
                attribute = attributeDao.findById(2004L);  //WEIGHT
                attrValueProduct = new AttrValueEntityProduct();
                attrValueProduct.setProduct(product);
                attrValueProduct.setVal("asdfasdf");
                attrValueProduct.setAttribute(attribute);
                product.getAttributes().add(attrValueProduct);
                pk = productDao.create(product).getProductId();
                assertTrue(pk > 0L);
                productDao.fullTextSearchReindex(false);

                status.setRollbackOnly();

            }
        });


       
    }

    @Test
    public void testSimpleSearchTest() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false);

                final GlobalSearchQueryBuilderImpl queryBuilder = new GlobalSearchQueryBuilderImpl();
                Query query = queryBuilder.createQuerySearchInCategories("bender", Arrays.asList(101L, 104L), false);
                List<Product> products = productDao.fullTextSearch(query);
                assertTrue("Failed [" + query.toString() +"]", !products.isEmpty());
                // search by Sku code
                query = queryBuilder.createQuerySearchInCategory("CC_TEST4", (Long) null, false);
                products = productDao.fullTextSearch(query);
                assertEquals("This is fuzzy so we see all CC_TESTX", 9, products.size());
                // search by Sku code with stems
                query = queryBuilder.createQuerySearchInCategory("cc_test4", (Long) null, true);
                products = productDao.fullTextSearch(query);
                assertEquals("Relaxed search should give all cc_test skus", 12, products.size());
                // search by sku id
                query = new SkuQueryBuilderImpl().createQuery("11004");
                products = productDao.fullTextSearch(query);
                assertEquals(1, products.size());
                assertEquals("PRODUCT5", products.get(0).getSku().iterator().next().getCode());
                //test fuzzy search
                query = queryBuilder.createQuerySearchInCategories("blender", Arrays.asList(101L, 104L), false);
                products = productDao.fullTextSearch(query);
                assertTrue(!products.isEmpty());
                //test search by description
                query = queryBuilder.createQuerySearchInCategories("Rodriguez Bending", Arrays.asList(101L, 104L), false);
                products = productDao.fullTextSearch(query);
                assertTrue(!products.isEmpty());
                query = queryBuilder.createQuerySearchInCategories("DiMaggio", Arrays.asList(101L, 104L), false);
                products = productDao.fullTextSearch(query);
                assertTrue("Description is damaging by introducing a lot of garbage", products.isEmpty());
                query = queryBuilder.createQuerySearchInCategories("dimaggio", Arrays.asList(101L, 104L), true);
                products = productDao.fullTextSearch(query);
                assertTrue(!products.isEmpty());
                // search on empty string
                query = queryBuilder.createQuerySearchInCategories("", Arrays.asList(101L, 104L), false);
                products = productDao.fullTextSearch(query);
                assertTrue(!products.isEmpty()); //return all product in described categories

                // search on brand
                // product with code "PRODUCT1" also future robotics, but not assigned to any category
                query = queryBuilder.createQuerySearchInCategories("FutureRobots", Arrays.asList(101L, 104L), false);
                products = productDao.fullTextSearch(query);
                assertEquals(2, products.size());

                status.setRollbackOnly();
            }
        });
    }


    /**
     * Global search test in shop, instead of categories
     * @throws InterruptedException
     */
    @Test
    public void testSimpleSearchTest2() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false);

                final GlobalSearchQueryBuilderImpl queryBuilder = new GlobalSearchQueryBuilderImpl();

                Query query = queryBuilder.createQuerySearchInShop("bender", 10L, false);
                List<Product> products = productDao.fullTextSearch(query);
                assertTrue("Product must be found in 10 shop. Failed [" + query.toString() +"]", !products.isEmpty());

                query = queryBuilder.createQuerySearchInShop("bender", 20L, false);
                products = productDao.fullTextSearch(query);
                assertTrue("The same warehouses assigned to 20 shop. Failed [" + query.toString() +"]", !products.isEmpty());

                query = queryBuilder.createQuerySearchInShop("bender", 30L, false);
                products = productDao.fullTextSearch(query);
                assertTrue("Product not present on 30 shop. Failed [" + query.toString() +"]", products.isEmpty());

                // search by Sku code
                query = queryBuilder.createQuerySearchInShop("CC_TEST4", 10L, false);
                products = productDao.fullTextSearch(query);
                assertEquals("CC_TEST4 and CC_TEST9 are in this category " + query.toString(), 2, products.size());

                //test fuzzy search
                query = queryBuilder.createQuerySearchInShop("blender", 10L, false);
                products = productDao.fullTextSearch(query);
                assertTrue(!products.isEmpty());

                //test search by description
                query = queryBuilder.createQuerySearchInShop("Rodriguez Bending", 10L, false);
                products = productDao.fullTextSearch(query);
                assertTrue(!products.isEmpty());
                query = queryBuilder.createQuerySearchInShop("DiMaggio", 10L, false);
                products = productDao.fullTextSearch(query);
                assertTrue("Description is damaging by introducing a lot of garbage", products.isEmpty());
                query = queryBuilder.createQuerySearchInShop("dimaggio", 10L, true);
                products = productDao.fullTextSearch(query);
                assertTrue(!products.isEmpty());

                // search on empty string
                query = queryBuilder.createQuerySearchInShop("", 10L, false);
                products = productDao.fullTextSearch(query);
                assertTrue(!products.isEmpty()); //return all product in described categories

                // search on brand
                // product with code "PRODUCT1" also future robotics, but not assigned to any category,
                // and can not be found via shop, because it has qty on warehouse, but category not assigned to 20 shop
                query = queryBuilder.createQuerySearchInShop("FutureRobots", 20L, false);
                products = productDao.fullTextSearch(query);
                assertEquals(query.toString(), 2, products.size());

                status.setRollbackOnly();
            }
        });
    }


    /**
     * Test to prove, that some abatement during search is working, in case if strongly limitation query not return any results.
     * @throws InterruptedException
     */
    @Test
    public void testSimpleSearchTest3() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                
                String okToFind;
                String failToFind;

                productDao.fullTextSearchReindex(false);

                final GlobalSearchQueryBuilderImpl queryBuilder = new GlobalSearchQueryBuilderImpl();
                Query query = queryBuilder.createQuerySearchInCategories("bender", Arrays.asList(101L, 104L), true);
                List<Product> products = productDao.fullTextSearch(query);
                assertTrue("Failed [" + query.toString() +"]", !products.isEmpty());
                // search by Sku code
                query = queryBuilder.createQuerySearchInCategory("CC_TEST99", (Long) null, false);
                products = productDao.fullTextSearch(query);
                assertEquals(1, products.size());
                query = queryBuilder.createQuerySearchInCategory("CC_TEZT9", (Long) null, false);
                products = productDao.fullTextSearch(query);
                assertEquals("Misspelling does not increase the score and should find a match", 1, products.size());
                // search by Sku code
                query = queryBuilder.createQuerySearchInCategory("cc_test99", (Long) null, true);
                okToFind = query.toString();
                products = productDao.fullTextSearch(query);
                assertFalse(products.isEmpty());

                mockery.checking(
                        new Expectations() {{
                            allowing(attributeService).getAllNavigatableAttributeCodes();
                            will(returnValue(new HashSet() {{ add(ProductSearchQueryBuilder.QUERY); }}));
                        } }
                );
                
                
                LuceneQueryFactoryImpl luceneQueryFactory = new LuceneQueryFactoryImpl(
                        null,
                        attributeService,
                        productDao);

                List<BooleanQuery> chain = luceneQueryFactory.getFilteredNavigationQueryChain(
                        10L,
                        Arrays.asList(101L, 104L),
                        Collections.singletonMap(ProductSearchQueryBuilder.QUERY, "CC_TEST99")
                );
                
                BooleanQuery booleanQuery =  luceneQueryFactory.getSnowBallQuery(chain, null);
                failToFind = booleanQuery.toString();



                products = productDao.fullTextSearch(booleanQuery);
                assertFalse(products.isEmpty());

                status.setRollbackOnly();
            }
        });
    }

    @Test
    public void testSearchByCategoryTest() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                productDao.fullTextSearchReindex(false);

                ProductsInCategoryQueryBuilderImpl queryBuilder = new ProductsInCategoryQueryBuilderImpl();
                Query query = queryBuilder.createQuery(Arrays.asList(101L));
                List<Product> products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query.toString() +"]",2, products.size());
                query = queryBuilder.createQuery(Arrays.asList(101L, 200L, 123L, 2435L));
                products = productDao.fullTextSearch(query);
                assertEquals(query.toString(), 2, products.size());
                query = queryBuilder.createQuery(Arrays.asList(101L, 104L));
                products = productDao.fullTextSearch(query);
                assertEquals(4, products.size());

                status.setRollbackOnly();

            }
        });

    }

    @Test
    public void testSearchByTagInCategoryTest() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                productDao.fullTextSearchReindex(false);

                TagSearchQueryBuilder queryBuilder = new TagSearchQueryBuilder();
                Query query = queryBuilder.createQuery(null, "newarrival");
                List<Product> products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query.toString() +"] expected 2 products", 2, products.size());

                query = queryBuilder.createQuery(null, "sale");
                products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query.toString() +"] expected 2 products", 2, products.size());

                query = queryBuilder.createQuery(null, "specialpromo");
                products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query.toString() +"] expected 2 products", 2, products.size());

                query = queryBuilder.createQuery(null, "sali");
                products = productDao.fullTextSearch(query);
                assertTrue("Failed [" + query.toString() +"] expected 0 products", products.isEmpty());

                query = queryBuilder.createQuery(Arrays.asList(104L), "sale");
                products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query.toString() +"] expected 2 products", 1, products.size());

                status.setRollbackOnly();

            }
        });

    }


    @Test
    public void testSearchByAttributeAndValueTest() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                productDao.fullTextSearchReindex(false);

                AttributiveSearchQueryBuilderImpl queryBuilder = new AttributiveSearchQueryBuilderImpl();


                // Test that we able to find Beder by his material in category where he exists

                Query query = queryBuilder.createQuery(Arrays.asList(101L), "MATERIAL", "metal");
                List<Product> products = productDao.fullTextSearch(query);
                assertEquals("Query [" + query.toString() + "] failed", 1, products.size());
                // Test that we able to find Beder by his material in  list of categories where he exists

                query = queryBuilder.createQuery(Arrays.asList(101L, 200L), "MATERIAL", "metal");
                products = productDao.fullTextSearch(query);
                assertEquals(1, products.size());





                // Test that we able to find Sobot by his material in category where he exists
                query = queryBuilder.createQuery(Arrays.asList(101L), "MATERIAL", "Plastik");
                products = productDao.fullTextSearch(query);
                assertEquals(1, products.size());
                //We are unable to getByKey products mafactured from bananas
                query = queryBuilder.createQuery(Arrays.asList(101L), "MATERIAL", "banana");
                products = productDao.fullTextSearch(query);
                assertEquals(0, products.size());
                //We are unable to getByKey products mafactured from bananas
                query = queryBuilder.createQuery(Collections.EMPTY_LIST, "MATERIAL", "banana");
                products = productDao.fullTextSearch(query);
                assertEquals(0, products.size());
                //No category limitation, so we expect all plastic robots
                query = queryBuilder.createQuery(Collections.EMPTY_LIST, "MATERIAL", "Plastik");
                products = productDao.fullTextSearch(query);
                assertEquals(query.toString(), 1, products.size());
                // Robot from plastic not in 104 category
                query = queryBuilder.createQuery(Arrays.asList(104L), "MATERIAL", "Plastik");
                products = productDao.fullTextSearch(query);
                assertEquals(0, products.size());
                // Robot from plastic not in 104 category
                query = queryBuilder.createQuery(Arrays.asList(105L), "MATERIAL", "Plastik");
                products = productDao.fullTextSearch(query);
                assertEquals(0, products.size());


                // search by sku attribute value
                query = queryBuilder.createQuery((List<Long>)null, "SMELL", "apple");
                products = productDao.fullTextSearch(query);
                assertEquals("Failed [" + query + "]", 1, products.size());


                QueryParser qp = new QueryParser(Version.LUCENE_31, "", new AsIsAnalyzer(false));
                Query parsed = null;
                try {
                    parsed = qp.parse("productCategory.category:101 productCategory.category:200 "
                            + "+(attribute.attribute:MATERIAL sku.attribute.attribute:MATERIAL) "
                            + "+(attribute.val:MATERIALmetal          sku.attribute.val:MATERIALmetal)");
                } catch (ParseException e) {
                    assertTrue(false);
                }

                List rez =  productDao.fullTextSearch(
                        parsed
                ) ;
                assertEquals("Feiled [" + parsed + "]",  1, rez.size());

                status.setRollbackOnly();

            }
        });


    }


    @Test
    public void getSearchByAttributeAndValuesRangeTest() throws InterruptedException {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false);
                AttributiveSearchQueryBuilderImpl queryBuilder = new AttributiveSearchQueryBuilderImpl();
                Query query = queryBuilder.createQuery(
                        Arrays.asList(130L, 131L, 132L),
                        "WEIGHT",
                        new Pair<String, String>("0.001", "2.3"));
                List<Product> products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 3, products.size());
                query = queryBuilder.createQuery(
                        Arrays.asList(130L, 131L, 132L),
                        "WEIGHT",
                        new Pair<String, String>("2.1", "2.3"));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 3, products.size());
                query = queryBuilder.createQuery(
                        Arrays.asList(130L, 131L, 132L),
                        "WEIGHT",
                        new Pair<String, String>("2.35", "2.35"));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 1, products.size());
                query = queryBuilder.createQuery(
                        Arrays.asList(130L, 131L, 132L),
                        "WEIGHT",
                        new Pair<String, String>("2.34", "2.35"));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 1, products.size());
                query = queryBuilder.createQuery(
                        Arrays.asList(130L, 131L, 132L),
                        "WEIGHT",
                        new Pair<String, String>("2.35", "2.38"));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 1, products.size());
                query = queryBuilder.createQuery(
                        Arrays.asList(130L, 131L, 132L),
                        "WEIGHT",
                        new Pair<String, String>("2.4", "2.49"));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 0, products.size());
                query = queryBuilder.createQuery(
                        100L,      //products not assigned to this category
                        "WEIGHT",
                        new Pair<String, String>("2.1", "2.5"));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 0, products.size());
                query = queryBuilder.createQuery(
                        Arrays.asList(100L, 101L, 102L),      //products not assigned to this categories.
                        "WEIGHT",
                        new Pair<String, String>("2.1", "2.5"));
                products = productDao.fullTextSearch(query);
                assertEquals("Range search with query [" + query + "] incorrect", 0, products.size());

                status.setRollbackOnly();

            }
        });
    }


    @Test
    public void testCreateNewProductTest() throws InterruptedException {





        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false);

                Product product = new ProductEntity();
                product.setAvailability(Product.AVAILABILITY_STANDARD);
                Brand brand = brandDao.findById(100L);
                assertNotNull(brand);
                product.setBrand(brand);
                product.setCode("SONY_PRODUCT_CODE");
                product.setName("product sony name");
                product.setDescription("Description ");

                ProductType productType = productTypeDao.findById(1L);
                assertNotNull(productType);
                product.setProducttype(productType);
                long pk = productDao.create(product).getProductId();
                assertTrue(pk > 0L);

                // add sku
                ProductSku productSku = new ProductSkuEntity();
                productSku.setProduct(product);
                productSku.setCode("SONY_PRODUCT_CODE");
                productSku.setName("product sony name");
                product.getSku().add(productSku);
                productDao.saveOrUpdate(product);
                productSkuDao.saveOrUpdate(productSku);

                // add quantity on warehoues
                SkuWarehouse skuWarehouse = new SkuWarehouseEntity();
                skuWarehouse.setSku(productSku);
                skuWarehouse.setQuantity(BigDecimal.ONE);
                skuWarehouse.setWarehouse(warehouseDao.findById(2L));

                skuWareHouseDao.create(skuWarehouse);

                // assign it to category
                ProductCategory productCategory = assignToCategory(product, 128L);

                List<Product> products = null;

                GlobalSearchQueryBuilderImpl queryBuilder = new GlobalSearchQueryBuilderImpl();
                Query query = queryBuilder.createQuerySearchInCategories("sony", Arrays.asList(128L), false);
                products = productDao.fullTextSearch(query);
                assertTrue("Product must be found in category with id = 128 . Failed query [" + query + "]", !products.isEmpty());

                products.clear();
                productCategory = null;
                product = productDao.findById(product.getProductId());

                productCategoryDao.delete(product.getProductCategory().iterator().next());


                product.getProductCategory().clear();

                productDao.update(product);

                productDao.fullTextSearchReindex(product.getProductId());


                //search in particular category
                query = queryBuilder.createQuerySearchInCategories("sony", Arrays.asList(128L), false);
                products = productDao.fullTextSearch(query);
                assertTrue("Failed search in particular category [" + query + "]", products.isEmpty());

                status.setRollbackOnly();

            }
        });





    }

    @Test
    public void testCreateNewProductTest2() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false);

                Product product = new ProductEntity();
                product.setAvailability(Product.AVAILABILITY_STANDARD);
                Brand brand = brandDao.findById(100L);
                assertNotNull(brand);
                product.setBrand(brand);
                product.setCode("SONY_PRODUCT_CODE");
                product.setName("product sony name");
                product.setDescription("Description ");

                ProductType productType = productTypeDao.findById(1L);
                assertNotNull(productType);
                product.setProducttype(productType);
                long pk = productDao.create(product).getProductId();
                assertTrue(pk > 0L);

                // add sku
                ProductSku productSku = new ProductSkuEntity();
                productSku.setProduct(product);
                productSku.setCode("SONY_PRODUCT_CODE");
                productSku.setName("product sony name");
                product.getSku().add(productSku);
                productDao.saveOrUpdate(product);
                productSkuDao.saveOrUpdate(productSku);

                // add quantity on warehoues
                SkuWarehouse skuWarehouse = new SkuWarehouseEntity();
                skuWarehouse.setSku(productSku);
                skuWarehouse.setQuantity(BigDecimal.ONE);
                skuWarehouse.setWarehouse(warehouseDao.findById(2L));

                skuWareHouseDao.create(skuWarehouse);

                // assign it to category
                ProductCategory productCategory = assignToCategory(product, 128L);

                List<Product> products = null;

                //productDao.fullTextSearchReindex(false);
                GlobalSearchQueryBuilderImpl queryBuilder = new GlobalSearchQueryBuilderImpl();
                Query query = queryBuilder.createQuerySearchInCategories("sony", Arrays.asList(128L), false);
                products = productDao.fullTextSearch(query);
                assertTrue("Product must be found in category with id = 128 . Failed query [" + query + "]", !products.isEmpty());


                query = queryBuilder.createQuerySearchInCategory("sony", (Long) null, false);
                products = productDao.fullTextSearch(query);
                assertTrue("Failed global search [" + query + "]", !products.isEmpty());
                products.clear();

                skuWarehouse = skuWareHouseDao.findById(skuWarehouse.getId());
                skuWareHouseDao.delete(skuWarehouse);
                //------productDao.fullTextSearchReindex(productCategory.getProduct().getProductId());
                //on site global. must be empty, because quantity is 0
                query = queryBuilder.createQuerySearchInCategories("sony", (List<Long>) null, false);
                products = productDao.fullTextSearch(query);
                assertTrue("Failed global search [" + query + "]", products.isEmpty());

                status.setRollbackOnly();

            }
        });



    }


    
    private ProductCategory assignToCategory(Product product, long categoryId) {
        ProductCategory productCategory = new ProductCategoryEntity();
        productCategory.setProduct(product);
        productCategory.setCategory(categoryDao.findById(categoryId));
        productCategory.setRank(123);
        product.getProductCategory().add(productCategory);

        productCategory = productCategoryDao.create(productCategory);

        product = productDao.update(product);
        productDao.fullTextSearchReindex(product.getProductId());

        return productCategory;
    }

    /**
     * Test for PRODUCTS.ATTR.CODE.VALUES.BY.ASSIGNED.CATEGORIES named query
     */
    @Test
    public void testGetUniqueBrandsByCateroriesTest() throws InterruptedException {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                //productDao.fullTextSearchReindex(false);

                ArrayList<Long> createdProducts = new ArrayList<Long>();
                createdProducts.add(createProduct(102L, "LG_DVD_PLAYER", "product lg dvd player", 3L, 134L));

                createdProducts.add(createProduct(104L, "SAM_DVD_PLAYER", "product sam mp3 player", 3L, 134L));
                createdProducts.add(createProduct(102L, "LG_MP3_PLAYER", "product lg mp3 player", 2L, 135L));
                createdProducts.add(createProduct(103L, "SONY_MP3_PLAYER", "product sony mp3 player", 2L, 135L));
                createdProducts.add(createProduct(104L, "SAM_MP3_PLAYER", "product sam mp3 player", 2L, 136L));


                List<Object> params = new ArrayList<Object>();
                params.add(134L);
                params.add(135L);
                params.add(136L);
                List<Object[]> brands = productDao.findQueryObjectsByNamedQueryWithList(
                        "PRODUCTS.ATTR.CODE.VALUES.BY.ASSIGNED.CATEGORIES",
                        params);

                assertNotNull(brands);
                assertEquals(3, brands.size());
                // test that list is alphabetically ordered
                assertEquals("LG", brands.get(0)[0]);
                assertEquals("Samsung", brands.get(1)[0]);
                assertEquals("Sony", brands.get(2)[0]);


                status.setRollbackOnly();

            }
        });






    }

    @Test
    public void testFindByBrandsInCateroriesTest() {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                ArrayList<Long> createdProducts = new ArrayList<Long>();
                createdProducts.add(createProduct(102L, "LG_DVD_PLAYER", "product lg dvd player", 3L, 134L));
                createdProducts.add(createProduct(104L, "SAM_DVD_PLAYER", "product sam mp3 player", 3L, 134L));
                createdProducts.add(createProduct(102L, "LG_MP3_PLAYER", "product lg mp3 player", 2L, 135L));
                createdProducts.add(createProduct(103L, "SONY_MP3_PLAYER", "product sony mp3 player", 2L, 135L));
                createdProducts.add(createProduct(104L, "SAM_MP3_PLAYER", "product sam mp3 player", 2L, 136L));
                // productDao.fullTextSearchReindex(false);
                List<Product> foundedProducts;
                BooleanQuery query;
                List<Long> categories = new ArrayList<Long>();
                BrandSearchQueryBuilder brandSearchQueryBuilder = new BrandSearchQueryBuilder();
                //exisitng LG product in category 134
                categories.clear();
                categories.add(134L);
                query = brandSearchQueryBuilder.createQuery(categories, "LG");
                foundedProducts = productDao.fullTextSearch(query);
                assertNotNull(foundedProducts);
                assertEquals(query.toString(), 1, foundedProducts.size());
                //exisitng two LG products in category 135 135
                categories.clear();
                categories.add(134L);
                categories.add(135L);
                query = brandSearchQueryBuilder.createQuery(categories, "LG");
                foundedProducts = productDao.fullTextSearch(query);
                assertNotNull(foundedProducts);
                assertEquals(query.toString(), 2, foundedProducts.size());
                //only one Sony product in categories 135, 134,136
                categories.clear();
                categories.add(134L);
                categories.add(135L);
                categories.add(136L);
                query = brandSearchQueryBuilder.createQuery(categories, "Sony");
                foundedProducts = productDao.fullTextSearch(query);
                assertNotNull(foundedProducts);
                assertEquals(query.toString(), 1, foundedProducts.size());
                //LG prod not exists in 136 category
                categories.clear();
                categories.add(136L);
                query = brandSearchQueryBuilder.createQuery(categories, "LG");
                foundedProducts = productDao.fullTextSearch(query);
                assertNotNull(foundedProducts);
                assertEquals(0, foundedProducts.size());

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
        // add quantity on warehoues
        SkuWarehouse skuWarehouse = new SkuWarehouseEntity();
        skuWarehouse.setSku(productSku);
        skuWarehouse.setQuantity(BigDecimal.ONE);
        skuWarehouse.setWarehouse(warehouseDao.findById(2L));
        skuWareHouseDao.create(skuWarehouse);
        productDao.fullTextSearchReindex(product.getProductId());
        skuWareHouseDao.flushClear();
        java.lang.System.out.println("\n");
        return pk;
    }


    @Test
    public void testGetUniqueAttribvaluesTest() {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                List<Object> list = productDao.findQueryObjectByNamedQuery("PRODUCTS.ATTRIBUTE.VALUES.BY.CODE.PRODUCTTYPEID",
                        1L,
                        "MATERIAL");
                assertNotNull(list);
                assertTrue(!list.isEmpty());
                assertTrue(list.contains("Plastik"));
                assertTrue(list.contains("metal"));
                list = productDao.findQueryObjectByNamedQuery("PRODUCTS.ATTRIBUTE.VALUES.BY.CODE.PRODUCTTYPEID",
                        1L,
                        "BATTERY_TYPE");
                assertNotNull(list);
                assertTrue(!list.isEmpty());
                assertTrue(list.contains("Plutonium"));

                status.setRollbackOnly();

            }
        });
    }

    @Test
    public void testGetRankedUniqueCodeAttribvaluesTest() {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                List<Object[]> codesByProductId = productDao.findQueryObjectsByNamedQuery("ATTRIBUTE.CODES.AND.RANK.SINGLE.NAVIGATION.UNIQUE.BY.PRODUCTTYPE.ID",
                        1L, true);
                final Map<String, Integer> map = new HashMap<String, Integer>();
                for (final Object[] codeAndRank : codesByProductId) {
                    map.put((String) codeAndRank[0], (Integer) codeAndRank[1]);
                }

                List<Object[]> list;
                list = productDao.findQueryObjectsByNamedQuery("PRODUCTS.ATTR.CODE.VALUES.BY.ATTRCODES",
                        map.keySet());
                assertNotNull(list);
                assertTrue(!list.isEmpty());

                list = productDao.findQueryObjectsByNamedQuery("PRODUCTSKUS.ATTR.CODE.VALUES.BY.ATTRCODES",
                        map.keySet());
                assertNotNull(list);
                assertTrue(list.isEmpty());

                // no need to check for sorting by rank since this is done in code,
                // in fact it should only be done in code as raking sort in SQL has low
                // performance

                status.setRollbackOnly();

            }
        });

    }

    @Test
    public void testProductAvailability() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                productDao.fullTextSearchReindex(false);

                ProductsInCategoryQueryBuilderImpl productSearchQueryBuilder = new ProductsInCategoryQueryBuilderImpl();
                List<Product> products = productDao.fullTextSearch(productSearchQueryBuilder.createQuery(212L));
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
