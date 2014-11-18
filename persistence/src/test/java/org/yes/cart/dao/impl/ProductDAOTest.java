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

import org.apache.lucene.search.Query;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.*;
import org.yes.cart.domain.query.SearchQueryBuilder;
import org.yes.cart.domain.query.impl.ProductSkuCodeSearchQueryBuilder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
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

        super.setUp();
    }

    @Test
    public void testCreateNewProductNoSkuNoCategory() throws InterruptedException {

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

                // Need to call index as we are not committing the transaction
                productDao.fullTextSearchReindex(pk, false);

                final SearchQueryBuilder queryBuilder = new ProductSkuCodeSearchQueryBuilder();
                final Query query = queryBuilder.createStrictQuery(0L, null, Arrays.asList("SONY_PRODUCT_CODE", "SONY_PRODUCT_CODE2"));

                // There are no SKU and inventory - should not be in index
                assertEquals(0, productDao.getResultCount(query));

                status.setRollbackOnly();

            }
        });

    }


    @Test
    public void testCreateNewProductWithSkuAndCategoryAndStandardAvailability() throws InterruptedException {


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
                product = productDao.saveOrUpdate(product);
                productSku = productSkuDao.saveOrUpdate(productSku);

                // add quantity on warehouses
                SkuWarehouse skuWarehouse = new SkuWarehouseEntity();
                skuWarehouse.setSku(productSku);
                skuWarehouse.setQuantity(BigDecimal.ONE);
                skuWarehouse.setWarehouse(warehouseDao.findById(2L));

                skuWarehouse = skuWareHouseDao.create(skuWarehouse);
                productSku.getQuantityOnWarehouse().add(skuWarehouse);

                // assign it to category
                ProductCategory productCategory = assignToCategory(product, 128L);

                List<Product> products = null;

                final SearchQueryBuilder queryBuilder = new ProductSkuCodeSearchQueryBuilder();
                final Query query = queryBuilder.createStrictQuery(0L, null, Arrays.asList("SONY_PRODUCT_CODE"));

                products = productDao.fullTextSearch(query);
                assertEquals("Product must be found. Failed query [" + query + "]", 1, products.size());
                assertEquals(pk, products.get(0).getProductId());

                productCategoryDao.delete(productCategory);
                productCategoryDao.flush();  // make changes visible
                product = productDao.findById(product.getProductId());

                // Need to call index as we are not committing the transaction
                productDao.fullTextSearchReindex(product.getProductId(), false);

                //search in particular category
                assertEquals("Failed search for sku [" + query + "] product was unassigned", 0, productDao.getResultCount(query));

                status.setRollbackOnly();

            }
        });

    }

    @Test
    public void testCreateNewProductWithStandardAvailabilityAndThenMakeItOutOfStock() throws InterruptedException {

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
                product = productDao.saveOrUpdate(product);
                productSku = productSkuDao.saveOrUpdate(productSku);

                // add quantity on warehouses
                SkuWarehouse skuWarehouse = new SkuWarehouseEntity();
                skuWarehouse.setSku(productSku);
                skuWarehouse.setQuantity(BigDecimal.ONE);
                skuWarehouse.setWarehouse(warehouseDao.findById(2L));

                skuWarehouse = skuWareHouseDao.create(skuWarehouse);

                productSkuDao.refresh(productSku);

                // assign it to category
                ProductCategory productCategory = assignToCategory(product, 128L);

                List<Product> products = null;

                final SearchQueryBuilder queryBuilder = new ProductSkuCodeSearchQueryBuilder();
                final Query query = queryBuilder.createStrictQuery(0L, null, Arrays.asList("SONY_PRODUCT_CODE"));

                products = productDao.fullTextSearch(query);
                assertEquals("Product must be found . Failed query [" + query + "]", 1, products.size());
                assertEquals(pk, products.get(0).getProductId());

                skuWarehouse = skuWareHouseDao.findById(skuWarehouse.getId());
                skuWareHouseDao.delete(skuWarehouse);
                skuWareHouseDao.flush(); // flush to make changes visible on SKU

                // Need to call index as we are not committing the transaction
                productDao.fullTextSearchReindex(product.getProductId(), false);

                // on site global. must be empty, because quantity is 0
                assertEquals("Failed SKU search [" + query + "] because products are out of stock", 0, productDao.getResultCount(query));

                status.setRollbackOnly();

            }
        });

    }

    @Test
    public void testCreateNewProductWithPreorderAvailabilityOutOfStock() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false);

                Product product = new ProductEntity();
                product.setAvailability(Product.AVAILABILITY_PREORDER);
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
                final Query query = queryBuilder.createStrictQuery(0L, null, Arrays.asList("SONY_PRODUCT_CODE"));

                products = productDao.fullTextSearch(query);
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

                productDao.fullTextSearchReindex(false);

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

                final SearchQueryBuilder queryBuilder = new ProductSkuCodeSearchQueryBuilder();
                final Query query = queryBuilder.createStrictQuery(0L, null, Arrays.asList("SONY_PRODUCT_CODE"));

                products = productDao.fullTextSearch(query);
                assertEquals("Product must be found because although products are out of stock it is preorderable. Failed query [" + query + "]", 1, products.size());
                assertEquals(pk, products.get(0).getProductId());

                status.setRollbackOnly();

            }
        });

    }

    @Test
    public void testCreateNewProductWithAlwaysAvailabilityOutOfStock() throws InterruptedException {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productDao.fullTextSearchReindex(false);

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
                final Query query = queryBuilder.createStrictQuery(0L, null, Arrays.asList("SONY_PRODUCT_CODE"));

                products = productDao.fullTextSearch(query);
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
     * Test for PRODUCTS.ATTR.CODE.VALUES.BY.ASSIGNED.CATEGORIES named query
     */
    @Test
    public void testGetUniqueBrandsByCategoriesTest() throws InterruptedException {


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
                List<Object[]> brands = productDao.findQueryObjectsByNamedQuery(
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
    public void testGetUniqueAttribValues() {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {


                List<Object> list = productDao.findQueryObjectByNamedQuery("PRODUCTS.ATTRIBUTE.VALUES.BY.CODE.PRODUCTTYPEID",
                        1L,
                        "MATERIAL");
                assertNotNull(list);
                assertEquals(2, list.size());
                assertTrue(list.contains("Plastik"));
                assertTrue(list.contains("metal"));
                list = productDao.findQueryObjectByNamedQuery("PRODUCTS.ATTRIBUTE.VALUES.BY.CODE.PRODUCTTYPEID",
                        1L,
                        "BATTERY_TYPE");
                assertNotNull(list);
                assertNotNull(list);
                assertEquals(1, list.size());
                assertTrue(list.contains("Plutonium"));

                status.setRollbackOnly();

            }
        });
    }

    @Test
    public void testGetRankedUniqueCodeAttribValues() {


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
                assertTrue(list.isEmpty());

                // no need to check for sorting by rank since this is done in code,
                // in fact it should only be done in code as raking sort in SQL has low
                // performance

                status.setRollbackOnly();

            }
        });

    }

}
