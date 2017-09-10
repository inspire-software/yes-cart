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

package org.yes.cart.dao.impl;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.GenericFTSCapableDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.*;
import org.yes.cart.search.query.SearchQueryBuilder;
import org.yes.cart.search.query.impl.ProductShopInStockSearchQueryBuilder;
import org.yes.cart.search.query.impl.ProductSkuCodeSearchQueryBuilder;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class ProductDAOTest extends AbstractTestDAO {

    private GenericFTSCapableDAO<Product, Long, Object> productDao;
    private GenericFTSCapableDAO<ProductSku, Long, Object> productSkuDao;
    private GenericDAO<Brand, Long> brandDao;
    private GenericDAO<ProductType, Long> productTypeDao;
    private GenericDAO<ProductCategory, Long> productCategoryDao;
    private GenericDAO<Category, Long> categoryDao;
    private GenericDAO<Attribute, Long> attributeDao;
    private GenericDAO<SkuWarehouse, Long> skuWareHouseDao;
    private GenericDAO<Warehouse, Long> warehouseDao;

    @Before
    public void setUp()  {
        productDao = (GenericFTSCapableDAO<Product, Long, Object>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_DAO);
        productSkuDao = (GenericFTSCapableDAO<ProductSku, Long, Object>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_SKU_DAO);
        brandDao = (GenericDAO<Brand, Long>) ctx().getBean(DaoServiceBeanKeys.BRAND_DAO);
        productTypeDao = (GenericDAO<ProductType, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_TYPE_DAO);
        productCategoryDao = (GenericDAO<ProductCategory, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_CATEGORY_DAO);
        categoryDao = (GenericDAO<Category, Long>) ctx().getBean(DaoServiceBeanKeys.CATEGORY_DAO);
        attributeDao = (GenericDAO<Attribute, Long>) ctx().getBean(DaoServiceBeanKeys.ATTRIBUTE_DAO);
        skuWareHouseDao = (GenericDAO<SkuWarehouse, Long>) ctx().getBean(DaoServiceBeanKeys.SKU_WAREHOUSE_DAO);
        warehouseDao = (GenericDAO<Warehouse, Long>) ctx().getBean(DaoServiceBeanKeys.WAREHOUSE_DAO);

        super.setUp();
    }

    @Test
    public void testCreateNewProductNoSkuNoCategory() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

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
                AttrValueProduct attrValueProduct = new AttrValueEntityProduct();
                //attrValueProduct.setAttrvalueId(33L);
                attrValueProduct.setProduct(product);
                attrValueProduct.setVal("100");
                attrValueProduct.setAttributeCode("WEIGHT");
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
                attrValueProduct = new AttrValueEntityProduct();
                attrValueProduct.setProduct(product);
                attrValueProduct.setVal("asdfasdf");
                attrValueProduct.setAttributeCode("WEIGHT");
                product.getAttributes().add(attrValueProduct);
                pk = productDao.create(product).getProductId();
                assertTrue(pk > 0L);

                // Need to call index as we are not committing the transaction
                productDao.fullTextSearchReindex(pk, false);

                final SearchQueryBuilder<Query> queryBuilder = new ProductSkuCodeSearchQueryBuilder();
                final List<Query> query = queryBuilder.createQueryChain(null, null, Arrays.asList("SONY_PRODUCT_CODE", "SONY_PRODUCT_CODE2"));

                // There are no SKU and inventory - should not be in index
                assertEquals(0, productDao.fullTextSearchCount(query.get(0)));

                status.setRollbackOnly();

            }
        });

    }


    @Test
    public void testCreateNewProductWithSkuAndCategoryAndStandardAvailability() throws InterruptedException {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

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
                product = productDao.saveOrUpdate(product);
                productSku = productSkuDao.saveOrUpdate(productSku);

                // add quantity on warehouses
                SkuWarehouse skuWarehouse = new SkuWarehouseEntity();
                skuWarehouse.setSkuCode(productSku.getCode());
                skuWarehouse.setQuantity(BigDecimal.ONE);
                skuWarehouse.setWarehouse(warehouseDao.findById(2L));

                skuWarehouse = skuWareHouseDao.create(skuWarehouse);

                // assign it to category
                ProductCategory productCategory = assignToCategory(product, 128L);

                List<Product> products = null;

                final SearchQueryBuilder<Query> queryBuilder = new ProductSkuCodeSearchQueryBuilder();
                final List<Query> query = queryBuilder.createQueryChain(null, null, Collections.singletonList("SONY_PRODUCT_CODE"));

                products = productDao.fullTextSearch(query.get(0));
                assertEquals("Product must be found. Failed query [" + query + "]", 1, products.size());
                assertEquals(pk, products.get(0).getProductId());

                product.getProductCategory().remove(productCategory);
                productCategoryDao.delete(productCategory);
                productCategoryDao.flush();  // make changes visible
                product = productDao.findById(product.getProductId());

                // Need to call index as we are not committing the transaction
                productDao.fullTextSearchReindex(product.getProductId(), false);

                //search in particular category
                assertEquals("Failed search for sku [" + query + "] product was unassigned", 0, productDao.fullTextSearchCount(query.get(0)));

                status.setRollbackOnly();

            }
        });

    }

    @Test
    public void testCreateNewProductWithStandardAvailabilityAndThenMakeItOutOfStock() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

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
                product = productDao.saveOrUpdate(product);
                productSku = productSkuDao.saveOrUpdate(productSku);

                // add quantity on warehouses
                SkuWarehouse skuWarehouse = new SkuWarehouseEntity();
                skuWarehouse.setSkuCode(productSku.getCode());
                skuWarehouse.setQuantity(BigDecimal.ONE);
                skuWarehouse.setWarehouse(warehouseDao.findById(2L));

                skuWarehouse = skuWareHouseDao.create(skuWarehouse);

                // assign it to category
                ProductCategory productCategory = assignToCategory(product, 128L);

                List<Product> products = null;

                final SearchQueryBuilder<Query> skuCodeQueryBuilder = new ProductSkuCodeSearchQueryBuilder();
                final List<Query> queryBySku = skuCodeQueryBuilder.createQueryChain(null, null, Collections.singletonList("SONY_PRODUCT_CODE"));

                final SearchQueryBuilder<Query> inStockQueryBuilder = new ProductShopInStockSearchQueryBuilder();
                final List<Query> queryInStock = inStockQueryBuilder.createQueryChain(null, null, 10L);

                final BooleanQuery.Builder skuInStockQueryBuilder = new BooleanQuery.Builder();
                skuInStockQueryBuilder.add(queryBySku.get(0), BooleanClause.Occur.MUST);
                skuInStockQueryBuilder.add(queryInStock.get(0), BooleanClause.Occur.MUST);
                final BooleanQuery skuInStockQuery = skuInStockQueryBuilder.build();

                products = productDao.fullTextSearch(queryBySku.get(0));
                assertEquals("Product must be found . Failed query [" + queryBySku + "]", 1, products.size());
                assertEquals(pk, products.get(0).getProductId());

                products = productDao.fullTextSearch(skuInStockQuery);
                assertEquals("Product must be found . Failed query [" + skuInStockQuery + "]", 1, products.size());
                assertEquals(pk, products.get(0).getProductId());

                skuWarehouse = skuWareHouseDao.findById(skuWarehouse.getId());
                skuWareHouseDao.delete(skuWarehouse);
                skuWareHouseDao.flush(); // flush to make changes visible on SKU

                // Need to call index as we are not committing the transaction
                productDao.fullTextSearchReindex(product.getProductId(), false);

                // Product is removed from index because if it is standard product we must have stock
                assertEquals("Failed SKU search [" + queryBySku + "] because products are out of stock", 0, productDao.fullTextSearchCount(queryBySku.get(0)));
                // on site global. must be empty, because quantity is 0
                assertEquals("Failed SKU search [" + skuInStockQuery + "] because products are out of stock", 0, productDao.fullTextSearchCount(skuInStockQuery));

                status.setRollbackOnly();

            }
        });

    }

    @Test
    public void testCreateNewProductWithPreorderAvailabilityOutOfStock() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

                Product product = new ProductEntity();
                product.setAvailability(Product.AVAILABILITY_PREORDER);
                Brand brand = brandDao.findById(100L);
                assertNotNull(brand);
                product.setBrand(brand);
                product.setCode("SONY_PRODUCT_CODE");
                product.setName("product sony name");
                product.setDescription("Description ");
                try {
                    // Preorders are only preorders if available to date is in future, otherwise standard
                    product.setAvailablefrom(new SimpleDateFormat("yyyy-MM-dd").parse("2099-01-01"));
                } catch (ParseException pe) {
                    fail(pe.getMessage());
                }

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
                product = productDao.saveOrUpdate(product);
                productSku = productSkuDao.saveOrUpdate(productSku);

                // assign it to category
                ProductCategory productCategory = assignToCategory(product, 128L);

                List<Product> products = null;

                final SearchQueryBuilder<Query> queryBuilder = new ProductSkuCodeSearchQueryBuilder();
                final List<Query> query = queryBuilder.createQueryChain(null, null, Collections.singletonList("SONY_PRODUCT_CODE"));

                products = productDao.fullTextSearch(query.get(0));
                assertEquals("Product must not be found because preorderable items must have inventory record. Failed query [" + query + "]", 0, products.size());

                // add quantity on warehouses
                SkuWarehouse skuWarehouse = new SkuWarehouseEntity();
                skuWarehouse.setSkuCode(productSku.getCode());
                skuWarehouse.setQuantity(BigDecimal.ZERO);
                skuWarehouse.setWarehouse(warehouseDao.findById(2L));

                skuWarehouse = skuWareHouseDao.create(skuWarehouse);
                productDao.fullTextSearchReindex(product.getProductId());

                products = productDao.fullTextSearch(query.get(0));
                assertEquals("Product must be found because although products are out of stock it is preorderable. Failed query [" + query + "]", 1, products.size());
                assertEquals(pk, products.get(0).getProductId());

                status.setRollbackOnly();

            }
        });

    }

    @Test
    public void testCreateNewProductWithBackorderAvailabilityOutOfStock() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

                Product product = new ProductEntity();
                product.setAvailability(Product.AVAILABILITY_BACKORDER);
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
                product = productDao.saveOrUpdate(product);
                productSku = productSkuDao.saveOrUpdate(productSku);

                // assign it to category
                ProductCategory productCategory = assignToCategory(product, 128L);

                List<Product> products = null;

                final SearchQueryBuilder<Query> queryBuilder = new ProductSkuCodeSearchQueryBuilder();
                final List<Query> query = queryBuilder.createQueryChain(null, null, Collections.singletonList("SONY_PRODUCT_CODE"));

                products = productDao.fullTextSearch(query.get(0));
                assertEquals("Product must not be found because back order items. Failed query [" + query + "]", 0, products.size());

                // add quantity on warehouses
                SkuWarehouse skuWarehouse = new SkuWarehouseEntity();
                skuWarehouse.setSkuCode(productSku.getCode());
                skuWarehouse.setQuantity(BigDecimal.ZERO);
                skuWarehouse.setWarehouse(warehouseDao.findById(2L));

                skuWarehouse = skuWareHouseDao.create(skuWarehouse);
                productDao.fullTextSearchReindex(product.getProductId());

                products = productDao.fullTextSearch(query.get(0));
                assertEquals("Product must be found because although products are out of stock it is backorderable. Failed query [" + query + "]", 1, products.size());
                assertEquals(pk, products.get(0).getProductId());

                status.setRollbackOnly();

            }
        });

    }

    @Test
    public void testCreateNewProductWithAlwaysAvailabilityOutOfStock() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false, 1000);

                Product product = new ProductEntity();
                product.setAvailability(Product.AVAILABILITY_ALWAYS);
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
                product = productDao.saveOrUpdate(product);
                productSku = productSkuDao.saveOrUpdate(productSku);

                // assign it to category
                ProductCategory productCategory = assignToCategory(product, 128L);

                List<Product> products = null;

                final SearchQueryBuilder queryBuilder = new ProductSkuCodeSearchQueryBuilder();
                final List<Query> query = queryBuilder.createQueryChain(null, null, Collections.singletonList("SONY_PRODUCT_CODE"));

                products = productDao.fullTextSearch(query.get(0));
                assertEquals("Product must be found because although products are out of stock it is preorderable. Failed query [" + query + "]", 1, products.size());
                assertEquals(pk, products.get(0).getProductId());

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
     * Test for PRODUCTS.BRANDS.ALL named query
     */
    @Test
    public void testGetUniqueBrandsByCategoriesTest() throws InterruptedException {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                //productDao.fullTextSearchReindex(false);

                List<Object[]> brands = productDao.findQueryObjectsByNamedQuery("PRODUCTS.BRANDS.ALL");

                assertNotNull(brands);
                assertFalse(brands.isEmpty());

                final Set<String> brandNames = new HashSet<String>();
                for (final Object[] rec : brands) {
                    brandNames.add(String.valueOf(rec[0]));
                }

                assertTrue(brandNames.containsAll(Arrays.asList("Unknown", "FutureRobots", "LG", "Sony", "Samsung", "cc tests", "PreorderCompany")));

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
        skuWarehouse.setSkuCode(productSku.getCode());
        skuWarehouse.setQuantity(BigDecimal.ONE);
        skuWarehouse.setWarehouse(warehouseDao.findById(2L));
        skuWareHouseDao.create(skuWarehouse);
        productDao.fullTextSearchReindex(product.getProductId());
        skuWareHouseDao.flushClear();
        return pk;
    }


    @Test
    public void testGetRankedUniqueCodeAttribValues() {


        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                List<Object[]> codesByProductId = productDao.findQueryObjectsByNamedQuery("ATTRIBUTE.CODES.AND.RANK.SINGLE.NAVIGATION.UNIQUE.BY.PRODUCTTYPE.ID",
                        1L, true, true);
                final Map<String, Integer> map = new HashMap<String, Integer>();
                for (final Object[] codeAndRank : codesByProductId) {
                    map.put((String) codeAndRank[0], (Integer) codeAndRank[1]);
                }

                List<Object[]> list;
                list = productDao.findQueryObjectsByNamedQuery("PRODUCTS.ATTR.CODE.VALUES.BY.ATTRCODES",
                        map.keySet());
                assertNotNull(list);
                final Map<String, List<String>> expected = new HashMap<String, List<String>>();
                expected.put("MATERIAL", Arrays.asList("Plastik", "metal"));
                expected.put("BATTERY_TYPE", Arrays.asList("Plutonium"));
                assertEquals(3, list.size());
                for (final Object[] value : list) {
                    final List<String> exp = expected.get(value[0]);
                    assertNotNull(exp);
                    assertTrue(exp.contains(value[1]));
                }

                list = productDao.findQueryObjectsByNamedQuery("PRODUCTSKUS.ATTR.CODE.VALUES.BY.ATTRCODES",
                        map.keySet());
                assertNotNull(list);
                final Map<String, List<String>> expectedSku = new HashMap<String, List<String>>();
                expectedSku.put("SIZE", Arrays.asList("small", "large", "medium", "xxl"));
                assertEquals(4, list.size());
                for (final Object[] value : list) {
                    final List<String> exp = expectedSku.get(value[0]);
                    assertNotNull(exp);
                    assertTrue(exp.contains(value[1]));
                }

                // no need to check for sorting by rank since this is done in code,
                // in fact it should only be done in code as raking sort in SQL has low
                // performance

                status.setRollbackOnly();

            }
        });

    }

}
