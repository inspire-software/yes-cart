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

package org.yes.cart.service.domain.impl;

import org.apache.lucene.search.Query;
import org.hibernate.LazyInitializationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.impl.ProductsInCategoryQueryBuilderImpl;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.service.domain.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductServiceImplTest extends BaseCoreDBTestCase {

    private ProductService productService;

    @Before
    public void setUp() {
        productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);

        super.setUp();
    }

    @Test
    public void testCreate() {
        ProductTypeService productTypeService = (ProductTypeService) ctx().getBean(ServiceSpringKeys.PRODUCT_TYPE_SERVICE);
        BrandService brandService = (BrandService) ctx().getBean(ServiceSpringKeys.BRAND_SERVICE);
        EntityFactory entityFactory = productService.getGenericDao().getEntityFactory();
        Product product = entityFactory.getByIface(Product.class);
        product.setCode("PROD_CODE_123");
        product.setName("product");
        product.setDescription("description");
        product.setProducttype(productTypeService.findById(1L));
        product.setAvailability(Product.AVAILABILITY_ALWAYS);
        product.setBrand(brandService.findById(101L));
        product = productService.create(product);
        assertTrue(product.getProductId() > 0);
        //test that default sku is created
        assertFalse(product.getSku().isEmpty());
        //code the same
        assertEquals(product.getCode(), product.getSku().iterator().next().getCode());
    }

    @Test
    public void testGetRangeValueNavigationRecords() {

        final List<FilteredNavigationRecord> all = productService.getDistinctAttributeValues("en", 1);
        final List<FilteredNavigationRecord> rez = new ArrayList<FilteredNavigationRecord>();
        for (final FilteredNavigationRecord rec : all) {
            if ("R".equals(rec.getType())) {
                rez.add(rec);
            }
        }

        assertEquals("Ten range navigation records was configured for 32 type", 10, rez.size());

    }

    @Test
    public void testGetProductById() {
        Product product = productService.getProductById(11004L);
        assertNotNull(product);
        assertEquals("PRODUCT5", product.getCode());
        product = productService.getProductById(654321987456L); //not existing product
        assertNull(product);
    }

    @Test
    public void testPricesOnSkuAreLazy() throws Exception {

        final Product product = productService.getProductById(10000L); // Sobot
        final Collection<SkuPrice> prodPrices = product.getSku().iterator().next().getSkuPrice();
        try {
            prodPrices.iterator().next().getRegularPrice();
            fail("Prices must be lazy. We work with prices through priceService with appropriate cache and timing");
        } catch (LazyInitializationException exp) {
            // good we need it lazy - as prices are retrieved from priceService
        }

        final ProductSku productSku = productService.getSkuById(10000L); // Sobot
        final Collection<SkuPrice> skuPrices = productSku.getSkuPrice();
        try {
            skuPrices.iterator().next().getRegularPrice();
            fail("Prices must be lazy. We work with prices through priceService with appropriate cache and timing");
        } catch (LazyInitializationException exp) {
            // good we need it lazy - as prices are retrieved from priceService
        }

    }

    @Test
    public void testInventoryOnSkuAreLazy() throws Exception {

        final Product product = productService.getProductById(10000L); // Sobot
        final Collection<SkuWarehouse> prodInventory = product.getSku().iterator().next().getQuantityOnWarehouse();
        try {
            prodInventory.iterator().next().getQuantity();
            fail("Inventory must be lazy. We work with inventory through skuWarehouseService with appropriate cache and timing");
        } catch (LazyInitializationException exp) {
            // good we need it lazy - as prices are retrieved from priceService
        }

        final ProductSku productSku = productService.getSkuById(10000L); // Sobot
        final Collection<SkuWarehouse> skuInventory = productSku.getQuantityOnWarehouse();
        try {
            skuInventory.iterator().next().getQuantity();
            fail("Inventory must be lazy. We work with inventory through skuWarehouseService with appropriate cache and timing");
        } catch (LazyInitializationException exp) {
            // good we need it lazy - as prices are retrieved from priceService
        }

    }

    @Test
    public void testGetProductByIdList() {
        List<Long> ids = new ArrayList<Long>() {{
            add(12004L);
            add(15004L);
        }};
        List<Product> prods = productService.getProductByIdList(ids);
        assertEquals(2, prods.size());
        ids = new ArrayList<Long>() {{
            add(12004L);
            add(15004L);
            add(777L);
        }};
        prods = productService.getProductByIdList(ids);
        assertEquals(2, prods.size());
        new ArrayList<String>() {{
            add("12004");
            add("15004");
        }};
        prods = productService.getProductByIdList(ids);
        assertEquals(2, prods.size());
    }

    @Test
    public void testGetRandomProductByCategory() {

        CategoryService categoryService = (CategoryService) ctx().getBean(ServiceSpringKeys.CATEGORY_SERVICE);
        Category category = categoryService.findById(211L);
        Set<Long> list = new HashSet<Long>();

        final CacheManager cm = ctx().getBean("cacheManager", CacheManager.class);
        final Cache cache = cm.getCache("productService-randomProductByCategory");

        for (int i = 0; i < 10; i++) {
            /**
             * The value is cached, hence before get new value need to evict related cache "productService-randomProductByCategory"
             */
            cache.clear();
            list.add(productService.getRandomProductByCategory(category).getProductId());
        }
        //assume, that we select at least two different products in ten times
        assertTrue("Set is " + list + " his size is " + list.size() + " but expected more that 1", list.size() > 1);
    }

    @Test
    public void testGetDefaultImage() {
        assertEquals("sobot-picture.jpeg", productService.getDefaultImage(10000L));
        assertNull("sobot-picture.jpeg", productService.getDefaultImage(9999L));

    }

    @Test
    public void testNoneAttributesView() {

        final Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> attrs =
                productService.getProductAttributes("en", 0L, 0L, 1L);

        assertNotNull(attrs);
        assertEquals(0, attrs.size());

    }

    @Test
    public void testProductAttributesView() {

        // bender product
        final Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> attrs =
                productService.getProductAttributes("en", 9998L, 0L, 1L);

        assertNotNull(attrs);
        assertFalse(attrs.isEmpty());
        final Pair<String, String> dvdKey = new Pair<String, String>("3", "DVD Players view group");
        assertTrue(attrs.containsKey(dvdKey));
        final Pair<String, String> weightKey = new Pair<String, String>("WEIGHT", "Weight");
        assertTrue(attrs.get(dvdKey).containsKey(weightKey));
        final List<Pair<String, String>> values = attrs.get(dvdKey).get(weightKey);
        assertEquals(1, values.size());
        assertEquals("1.15", values.get(0).getSecond());

    }

    @Test
    public void testProductSkuAttributesView() {

        // bender sku
        final Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> attrs =
                productService.getProductAttributes("en", 0L, 9998L, 1L);

        assertNotNull(attrs);
        assertFalse(attrs.isEmpty());
        final Pair<String, String> dvdKey = new Pair<String, String>("3", "DVD Players view group");
        assertTrue(attrs.containsKey(dvdKey));
        final Pair<String, String> weightKey = new Pair<String, String>("WEIGHT", "Weight");
        assertTrue(attrs.get(dvdKey).containsKey(weightKey));
        final List<Pair<String, String>> values = attrs.get(dvdKey).get(weightKey);
        assertEquals(1, values.size());
        assertEquals("1.16", values.get(0).getSecond());

    }

    @Test
    public void testGetProductSearchResultDTOByQuery() {



        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                productService.getGenericDao().fullTextSearchReindex(false);

                final ProductsInCategoryQueryBuilderImpl queryBuilder = new ProductsInCategoryQueryBuilderImpl();
                Query query = queryBuilder.createQuery(Arrays.asList(101L));
                final List<ProductSearchResultDTO> searchRes = productService.getProductSearchResultDTOByQuery(
                        query,
                        0,
                        100,
                        null,
                        false
                );
                assertEquals("Failed [" + query.toString() + "]", 2, searchRes.size());
                ProductSearchResultDTO bernder = searchRes.get(0);
                assertEquals("Бендер Згибатель Родригес", bernder.getName("ru"));
                assertEquals("Бендер Згинач Родріґес", bernder.getName("ua"));

            }
        });


    }

}
