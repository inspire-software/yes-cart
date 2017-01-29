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

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericFullTextSearchCapableDAO;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.service.domain.BrandService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductTypeService;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductServiceImplTest extends BaseCoreDBTestCase {

    private ProductService productService;
    private LuceneQueryFactory luceneQueryFactory;

    @Before
    public void setUp() {
        productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        luceneQueryFactory = (LuceneQueryFactory) ctx().getBean(ServiceSpringKeys.LUCENE_QUERY_FACTORY);

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
    public void testCompareAttributesView() throws Exception {

        // bender vs bender-ua sku
        final Map<Pair<String, String>, Map<Pair<String, String>, Map<String, List<Pair<String, String>>>>> attrs =
                productService.getCompareAttributes("en", Arrays.asList(9999L), Arrays.asList(9998L));

        assertNotNull(attrs);
        assertFalse(attrs.isEmpty());
        final Pair<String, String> dvdKey = new Pair<String, String>("3", "DVD Players view group");
        assertTrue(attrs.containsKey(dvdKey));
        final Pair<String, String> weightKey = new Pair<String, String>("WEIGHT", "Weight");
        assertTrue(attrs.get(dvdKey).containsKey(weightKey));
        final Map<String, List<Pair<String, String>>> products = attrs.get(dvdKey).get(weightKey);
        assertEquals(2, products.size());

        final List<Pair<String, String>> values9998 = products.get("s_9998");
        assertEquals(1, values9998.size());
        assertEquals("1.16", values9998.get(0).getSecond());

        final List<Pair<String, String>> values9999 = products.get("p_9999");
        assertEquals(1, values9999.size());
        assertEquals("1.1", values9999.get(0).getSecond());

    }

    @Test
    public void testGetProductSearchResultDTOByQuery() {


        // Single category
        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                ((GenericFullTextSearchCapableDAO) productService.getGenericDao()).fullTextSearchReindex(false, 1000);

                NavigationContext context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L), false, null);
                final ProductSearchResultPageDTO searchRes = productService.getProductSearchResultDTOByQuery(
                        context.getProductQuery(),
                        0,
                        100,
                        ProductSearchQueryBuilder.PRODUCT_NAME_SORT_FIELD,
                        false
                );
                assertEquals("Failed [" + context.toString() + "]", 2, searchRes.getResults().size());
                ProductSearchResultDTO bender = searchRes.getResults().get(0);
                assertEquals("Бендер Згибатель Родригес", bender.getName("ru"));
                assertEquals("Бендер Згинач Родріґес", bender.getName("ua"));

            }
        });

        // Category with subs
        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                ((GenericFullTextSearchCapableDAO) productService.getGenericDao()).fullTextSearchReindex(false, 1000);

                NavigationContext context = luceneQueryFactory.getFilteredNavigationQueryChain(10L, Arrays.asList(101L), true, null);
                final ProductSearchResultPageDTO searchRes = productService.getProductSearchResultDTOByQuery(
                        context.getProductQuery(),
                        0,
                        100,
                        ProductSearchQueryBuilder.PRODUCT_NAME_SORT_FIELD,
                        false
                );
                assertTrue("Failed [" + context.toString() + "]", 2 < searchRes.getResults().size());

                final Set<String> names = new HashSet<String>();
                for (final ProductSearchResultDTO item : searchRes.getResults()) {
                    names.add(item.getCode());
                }

                assertTrue("CC_TEST7 is from 104 with parent 101", names.contains("CC_TEST7"));

            }
        });


    }

    @Test
    public void testFindProductIdsByManufacturerCode() throws Exception {

        final List<Long> idsExist = productService.findProductIdsByManufacturerCode("BENDER-ua");

        assertNotNull(idsExist);
        assertEquals(1, idsExist.size());
        assertEquals(Long.valueOf(9998L), idsExist.get(0));

        final List<Long> idsNone = productService.findProductIdsByManufacturerCode("asdfasdooooo");

        assertNotNull(idsNone);
        assertEquals(0, idsNone.size());

    }

    @Test
    public void testFindProductIdsByBarCode() throws Exception {

        final List<Long> idsExist = productService.findProductIdsByBarCode("001234567905");

        assertNotNull(idsExist);
        assertEquals(1, idsExist.size());
        assertEquals(Long.valueOf(9998L), idsExist.get(0));

        final List<Long> idsNone = productService.findProductIdsByBarCode("asdfasdooooo");

        assertNotNull(idsNone);
        assertEquals(0, idsNone.size());

    }

    @Test
    public void testFindProductIdsByPimCode() throws Exception {

        final List<Long> idsExist = productService.findProductIdsByPimCode("ICE00001");

        assertNotNull(idsExist);
        assertEquals(1, idsExist.size());
        assertEquals(Long.valueOf(9998L), idsExist.get(0));

        final List<Long> idsNone = productService.findProductIdsByPimCode("asdfasdooooo");

        assertNotNull(idsNone);
        assertEquals(0, idsNone.size());

    }

    @Test
    public void testFindProductIdsByAttributeValue() throws Exception {

        final List<Long> idsExist = productService.findProductIdsByAttributeValue("WEIGHT", "1.15");

        assertNotNull(idsExist);
        assertEquals(1, idsExist.size());
        assertEquals(Long.valueOf(9998L), idsExist.get(0));

        final List<Long> idsNone = productService.findProductIdsByAttributeValue("WEIGHT", "zzzz");

        assertNotNull(idsNone);
        assertEquals(0, idsNone.size());

        // Sku attributes are not considered
        final List<Long> idsSku = productService.findProductIdsByAttributeValue("WEIGHT", "1.16");

        assertNotNull(idsSku);
        assertEquals(0, idsSku.size());

    }
}
