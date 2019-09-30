/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.service.vo.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.vo.VoBrandService;
import org.yes.cart.service.vo.VoProductService;
import org.yes.cart.service.vo.VoProductTypeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 25/09/2019
 * Time: 11:28
 */
public class VoProductServiceImplTest extends BaseCoreDBTestCase {

    private VoProductService voProductService;
    private VoProductTypeService voProductTypeService;
    private VoBrandService voBrandService;

    @Before
    public void setUp() {
        voProductService = (VoProductService) ctx().getBean("voProductService");
        voBrandService = (VoBrandService) ctx().getBean("voBrandService");
        voProductTypeService = (VoProductTypeService) ctx().getBean("voProductTypeService");
        super.setUp();
    }

    @Test
    public void testGetAssociations() throws Exception {

        final List<VoAssociation> associations = voProductService.getAllAssociations();
        assertNotNull(associations);
        assertFalse(associations.isEmpty());

    }


    @Test
    public void testGetProducts() throws Exception {

        final List<VoProduct> productsNoFilter = voProductService.getFilteredProducts(null, 10);
        assertNotNull(productsNoFilter);
        assertFalse(productsNoFilter.isEmpty());

        final List<VoProduct> productsFind = voProductService.getFilteredProducts("CC_TEST", 10);
        assertNotNull(productsFind);
        assertFalse(productsFind.isEmpty());
        assertTrue(productsFind.get(0).getCode().contains("CC_TEST"));

        final List<VoProduct> productsById = voProductService.getFilteredProducts("*9998", 10);
        assertNotNull(productsById);
        assertEquals(1, productsById.size());
        assertEquals(9998L, productsById.get(0).getProductId());

        final List<VoProduct> productsByCodeExact = voProductService.getFilteredProducts("!BENDER-ua", 10);
        assertNotNull(productsByCodeExact);
        assertEquals(1, productsByCodeExact.size());
        assertEquals("BENDER-ua", productsByCodeExact.get(0).getCode());

        final List<VoProduct> productsByCode = voProductService.getFilteredProducts("#BENDER", 10);
        assertNotNull(productsByCode);
        assertEquals(2, productsByCode.size());
        assertTrue(productsByCode.get(0).getCode().contains("BENDER"));

        final List<VoProduct> productsByBrandOrType = voProductService.getFilteredProducts("?Robots", 10);
        assertNotNull(productsByBrandOrType);
        assertFalse(productsByBrandOrType.isEmpty());
        assertEquals("Robots", productsByBrandOrType.get(0).getProductType().getName());

        final List<VoProduct> productsInCat = voProductService.getFilteredProducts("^Big Boys Gadgets", 10);
        assertNotNull(productsInCat);
        assertFalse(productsInCat.isEmpty());
        assertEquals("Big Boys Gadgets", productsInCat.get(0).getProductCategories().iterator().next().getCategoryName());

    }

    @Test
    public void testProductCRUD() throws Exception {

        final VoProduct product = new VoProduct();
        product.setCode("TESTCRUD");
        product.setName("TEST CRUD");
        product.setDisplayNames(Collections.singletonList(MutablePair.of("en", "Test")));
        product.setBrand(voBrandService.getBrandById(102L));
        product.setProductType(voProductTypeService.getTypeById(1L));

        final VoProductCategory pcat = new VoProductCategory();
        pcat.setCategoryId(120L);
        pcat.setCategoryCode("120");
        product.setProductCategories(new HashSet<>(Arrays.asList(pcat)));

        final VoProductWithLinks created = voProductService.createProduct(product);
        assertTrue(created.getProductId() > 0L);
        assertNotNull(created.getSku());
        assertEquals(1, created.getSku().size());

        VoProductWithLinks afterCreated = voProductService.getProductById(created.getProductId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());

        afterCreated.setName("TEST CRUD UPDATE");

        final VoProductWithLinks updated = voProductService.updateProduct(afterCreated);
        assertEquals("TEST CRUD UPDATE", updated.getName());

        assertFalse(voProductService.getFilteredProducts("TEST CRUD UPDATE", 10).isEmpty());

        final List<VoAttrValueProduct> attributes = voProductService.getProductAttributes(updated.getProductId());
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());

        final VoAttrValueProduct updateAttribute = attributes.stream().filter(
                attr -> "PRODUCT_DESCRIPTION_en".equals(attr.getAttribute().getCode())
        ).findFirst().get();

        assertNull(updateAttribute.getVal());
        updateAttribute.setVal("ABC");
        updateAttribute.setDisplayVals(Collections.singletonList(MutablePair.of("en", "Abc value")));

        final List<VoAttrValueProduct> attributesAfterCreate = voProductService.updateProductAttributes(Collections.singletonList(MutablePair.of(updateAttribute, Boolean.FALSE)));
        final VoAttrValueProduct attributeAfterCreate = attributesAfterCreate.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())).findFirst().get();
        assertEquals("ABC", attributeAfterCreate.getVal());
        assertEquals("en", attributeAfterCreate.getDisplayVals().get(0).getFirst());
        assertEquals("Abc value", attributeAfterCreate.getDisplayVals().get(0).getSecond());

        final List<VoAttrValueProduct> attributesAfterRemove = voProductService.updateProductAttributes(Collections.singletonList(MutablePair.of(attributeAfterCreate, Boolean.TRUE)));
        final VoAttrValueProduct attributeAfterRemove = attributesAfterRemove.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())).findFirst().get();
        assertNull(attributeAfterRemove.getVal());

        voProductService.removeProduct(updated.getProductId());

        assertTrue(voProductService.getFilteredProducts("TEST CRUD UPDATE", 10).isEmpty());

    }

    @Test
    public void testSkuCRUD() throws Exception {

        VoProductSku product = new VoProductSku();
        product.setCode("TESTCRUD");
        product.setName("TEST CRUD");
        product.setDisplayNames(Collections.singletonList(MutablePair.of("en", "Test")));
        product.setProductId(9998L);

        final VoProductSku created = voProductService.createSku(product);
        assertTrue(created.getProductId() > 0L);

        List<VoProductSku> afterCreated = voProductService.getProductSkuAll(created.getProductId());
        assertNotNull(afterCreated);
        assertEquals(2, afterCreated.size());

        created.setName("TEST CRUD UPDATE");

        final VoProductSku updated = voProductService.updateSku(created);
        assertEquals("TEST CRUD UPDATE", updated.getName());

        assertFalse(voProductService.getFilteredProductSkus("TEST CRUD UPDATE", 10).isEmpty());

        final List<VoAttrValueProductSku> attributes = voProductService.getSkuAttributes(updated.getSkuId());
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());

        final VoAttrValueProductSku updateAttribute = attributes.stream().filter(
                attr -> "PRODUCT_DESCRIPTION_en".equals(attr.getAttribute().getCode())
        ).findFirst().get();

        assertNull(updateAttribute.getVal());
        updateAttribute.setVal("ABC");
        updateAttribute.setDisplayVals(Collections.singletonList(MutablePair.of("en", "Abc value")));

        final List<VoAttrValueProductSku> attributesAfterCreate = voProductService.updateSkuAttributes(Collections.singletonList(MutablePair.of(updateAttribute, Boolean.FALSE)));
        final VoAttrValueProductSku attributeAfterCreate = attributesAfterCreate.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())).findFirst().get();
        assertEquals("ABC", attributeAfterCreate.getVal());
        assertEquals("en", attributeAfterCreate.getDisplayVals().get(0).getFirst());
        assertEquals("Abc value", attributeAfterCreate.getDisplayVals().get(0).getSecond());

        final List<VoAttrValueProductSku> attributesAfterRemove = voProductService.updateSkuAttributes(Collections.singletonList(MutablePair.of(attributeAfterCreate, Boolean.TRUE)));
        final VoAttrValueProductSku attributeAfterRemove = attributesAfterRemove.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())).findFirst().get();
        assertNull(attributeAfterRemove.getVal());

        voProductService.removeSku(updated.getSkuId());

        assertTrue(voProductService.getFilteredProducts("TEST CRUD UPDATE", 10).isEmpty());


    }

}