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
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.BrandService;
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

    @Override
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
        product.setBrand(brandService.findById(101L));
        product = productService.create(product);
        assertTrue(product.getProductId() > 0);
        //test that default sku is created
        assertFalse(product.getSku().isEmpty());
        //code the same
        assertEquals(product.getCode(), product.getSku().iterator().next().getCode());
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
    public void testGetDefaultImage() {
        assertEquals("sobot-picture.jpeg", productService.getDefaultImage(10000L));
        assertNull("sobot-picture.jpeg", productService.getDefaultImage(9999L));

    }

    @Test
    public void testNoneAttributesView() {

        final ProductAttributesModel model =
                productService.getProductAttributes( 0L, 0L, 1L);

        assertNotNull(model);
        assertEquals(0, model.getGroups().size());

    }

    @Test
    public void testProductAttributesView() {

        // bender product
        final ProductAttributesModel model =
                productService.getProductAttributes( 9998L, 0L, 1L);

        assertNotNull(model);
        assertFalse(model.getGroups().isEmpty());
        final ProductAttributesModelGroup grp = model.getGroup("3");
        assertNotNull(grp);
        assertEquals("DVD Players view group", grp.getDisplayName("en"));
        final List<ProductAttributesModelAttribute> attrs = model.getAttributes("WEIGHT");
        assertEquals(1, attrs.size());
        assertEquals("Weight", attrs.get(0).getDisplayName("en"));
        final ProductAttributesModelAttribute attr = grp.getAttribute("WEIGHT");
        assertNotNull(attr);
        assertEquals("Weight", attr.getDisplayName("en"));
        final List<ProductAttributesModelValue> values = attr.getValues();
        assertEquals(1, values.size());
        assertEquals("1.15", values.get(0).getVal());

    }

    @Test
    public void testProductSkuAttributesView() {

        // bender sku
        final ProductAttributesModel model =
                productService.getProductAttributes(0L, 9998L, 1L);

        assertNotNull(model);
        assertFalse(model.getGroups().isEmpty());
        final ProductAttributesModelGroup grp = model.getGroup("3");
        assertNotNull(grp);
        assertEquals("DVD Players view group", grp.getDisplayName("en"));
        final List<ProductAttributesModelAttribute> attrs = model.getAttributes("WEIGHT");
        assertEquals(1, attrs.size());
        assertEquals("Weight", attrs.get(0).getDisplayName("en"));
        final ProductAttributesModelAttribute attr = grp.getAttribute("WEIGHT");
        assertNotNull(attr);
        assertEquals("Weight", attr.getDisplayName("en"));
        final List<ProductAttributesModelValue> values = attr.getValues();
        assertEquals(1, values.size());
        assertEquals("1.16", values.get(0).getVal());

    }

    @Test
    public void testCompareAttributesView() throws Exception {

        // bender vs bender-ua sku
        final ProductCompareModel model =
                productService.getCompareAttributes(Collections.singletonList(9999L), Collections.singletonList(9998L));

        assertNotNull(model);
        assertFalse(model.getGroups().isEmpty());
        final ProductCompareModelGroup grp = model.getGroup("3");
        assertNotNull(grp);
        assertEquals("DVD Players view group", grp.getDisplayName("en"));
        final List<ProductCompareModelAttribute> attrs = model.getAttributes("WEIGHT");
        assertEquals(1, attrs.size());
        assertEquals("Weight", attrs.get(0).getDisplayName("en"));
        final ProductCompareModelAttribute attr = grp.getAttribute("WEIGHT");
        assertNotNull(attr);
        assertEquals("Weight", attr.getDisplayName("en"));
        final Map<String, List<ProductCompareModelValue>> values = attr.getValues();
        assertEquals(2, values.size());

        final List<ProductCompareModelValue> values9998 = values.get("s_9998");
        assertEquals(1, values9998.size());
        assertEquals("1.16", values9998.get(0).getVal());

        final List<ProductCompareModelValue> values9999 = values.get("p_9999");
        assertEquals(1, values9999.size());
        assertEquals("1.1", values9999.get(0).getVal());
        
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


    @Test
    public void testFindConfigurableProduct() throws Exception {

        final Product product = productService.getProductById(15500L);

        assertNotNull(product);
        assertTrue(product.getOptions().isConfigurable());
        assertEquals(3, product.getSku().size());

        final ProductOptions options = product.getOptions();
        assertNotNull(options);

        final Product pwa = productService.getProductById(11004L);
        assertNotNull(pwa.getProductAssociations());

        getTx().execute(tx -> {

            final Object obj = productService.getGenericDao().findSingleByQuery(" select poe from ProductOptionEntity poe where poe.product.productId = ?1", 15510L);
            return false;
        });

        final List<ProductOption> cfg01opts = options.getConfigurationOptionForSKU("001_CFG01");
        assertNotNull(cfg01opts);
        assertEquals(1, cfg01opts.size());
        assertEquals("MATERIAL", cfg01opts.get(0).getAttributeCode());
        assertEquals(Arrays.asList("001_CFG_OPT1_A", "001_CFG_OPT1_B"), cfg01opts.get(0).getOptionSkuCodes());


    }
}
