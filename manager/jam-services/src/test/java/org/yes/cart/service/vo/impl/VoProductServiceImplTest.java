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

        VoSearchContext ctxNone = new VoSearchContext();
        ctxNone.setSize(10);
        final VoSearchResult<VoProduct> productsNoFilter = voProductService.getFilteredProducts(ctxNone);
        assertNotNull(productsNoFilter);
        assertFalse(productsNoFilter.getItems().isEmpty());

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(createSearchContextParams("filter", "CC_TEST"));
        ctxFind.setSize(10);
        final VoSearchResult<VoProduct> productsFind = voProductService.getFilteredProducts(ctxFind);
        assertNotNull(productsFind);
        assertFalse(productsFind.getItems().isEmpty());
        assertTrue(productsFind.getItems().get(0).getCode().contains("CC_TEST"));

        VoSearchContext ctxByPk = new VoSearchContext();
        ctxByPk.setParameters(createSearchContextParams("filter", "* 9998"));
        ctxByPk.setSize(10);
        final VoSearchResult<VoProduct> productsById = voProductService.getFilteredProducts(ctxByPk);
        assertNotNull(productsById);
        assertEquals(1, productsById.getTotal());
        assertEquals(9998L, productsById.getItems().get(0).getProductId());

        VoSearchContext ctxByCodeExact = new VoSearchContext();
        ctxByCodeExact.setParameters(createSearchContextParams("filter", "! BENDER-ua"));
        ctxByCodeExact.setSize(10);
        final VoSearchResult<VoProduct> productsByCodeExact = voProductService.getFilteredProducts(ctxByCodeExact);
        assertNotNull(productsByCodeExact);
        assertEquals(1, productsByCodeExact.getTotal());
        assertEquals("BENDER-ua", productsByCodeExact.getItems().get(0).getCode());

        VoSearchContext ctxByCode = new VoSearchContext();
        ctxByCode.setParameters(createSearchContextParams("filter", "#BENDER"));
        ctxByCode.setSize(10);
        final VoSearchResult<VoProduct> productsByCode = voProductService.getFilteredProducts(ctxByCode);
        assertNotNull(productsByCode);
        assertEquals(2, productsByCode.getTotal());
        assertTrue(productsByCode.getItems().get(0).getCode().contains("BENDER"));

        VoSearchContext ctxByBrandOrType = new VoSearchContext();
        ctxByBrandOrType.setParameters(createSearchContextParams("filter", "?Robots"));
        ctxByBrandOrType.setSize(10);
        final VoSearchResult<VoProduct> productsByBrandOrType = voProductService.getFilteredProducts(ctxByBrandOrType);
        assertNotNull(productsByBrandOrType);
        assertTrue(productsByBrandOrType.getTotal() > 0);
        assertEquals("Robots", productsByBrandOrType.getItems().get(0).getProductType().getName());

        VoSearchContext ctxByCategoryExact = new VoSearchContext();
        ctxByCategoryExact.setParameters(createSearchContextParams("filter", "^!101"));
        ctxByCategoryExact.setSize(10);
        final VoSearchResult<VoProduct> productsInCat = voProductService.getFilteredProducts(ctxByCategoryExact);
        assertNotNull(productsInCat);
        assertTrue(productsInCat.getTotal() > 0);
        assertEquals("Big Boys Gadgets", productsInCat.getItems().get(0).getProductCategories().iterator().next().getCategoryName());

        VoSearchContext ctxByCategory = new VoSearchContext();
        ctxByCategory.setParameters(createSearchContextParams("filter", "^101"));
        ctxByCategory.setSize(100);
        final VoSearchResult<VoProduct> productsInCatAll = voProductService.getFilteredProducts(ctxByCategory);
        assertNotNull(productsInCatAll);
        assertTrue(productsInCatAll.getTotal() > 0);
        assertTrue(productsInCatAll.getItems().stream().anyMatch(item -> "Robotics".equals(item.getProductCategories().iterator().next().getCategoryName())));

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

        VoSearchContext ctx = new VoSearchContext();
        ctx.setParameters(createSearchContextParams("filter", "TEST CRUD UPDATE"));
        ctx.setSize(10);

        assertTrue(voProductService.getFilteredProducts(ctx).getTotal() > 0);

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

        assertFalse(voProductService.getFilteredProducts(ctx).getTotal() > 0);

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

        VoSearchContext ctx = new VoSearchContext();
        ctx.setParameters(createSearchContextParams("filter", "TEST CRUD UPDATE"));
        ctx.setSize(10);

        assertTrue(voProductService.getFilteredProductSkus(ctx).getTotal() > 0);

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

        assertFalse(voProductService.getFilteredProducts(ctx).getTotal() > 0);


    }

    @Test
    public void testProductOptions() throws Exception {

        final VoProduct productWithOptions = voProductService.getProductById(15500L);

        assertNotNull(productWithOptions);
        assertTrue(productWithOptions.isConfigurable());
        assertNotNull(productWithOptions.getConfigurationOptions());
        assertEquals(2, productWithOptions.getConfigurationOptions().size());

    }
}