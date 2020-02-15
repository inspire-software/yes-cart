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
import org.yes.cart.domain.vo.VoAttrValueBrand;
import org.yes.cart.domain.vo.VoBrand;
import org.yes.cart.domain.vo.VoSearchContext;
import org.yes.cart.domain.vo.VoSearchResult;
import org.yes.cart.service.vo.VoBrandService;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 22/09/2019
 * Time: 18:02
 */
public class VoBrandServiceImplTest extends BaseCoreDBTestCase {

    private VoBrandService voBrandService;

    @Before
    public void setUp() {
        voBrandService = (VoBrandService) ctx().getBean("voBrandService");
        super.setUp();
    }

    @Test
    public void testGetBrands() throws Exception {

        VoSearchContext ctxNoFilter = new VoSearchContext();
        ctxNoFilter.setSize(10);
        VoSearchResult<VoBrand> brandNoFilter = voBrandService.getFilteredBrands(ctxNoFilter);
        assertNotNull(brandNoFilter);
        assertFalse(brandNoFilter.getItems().isEmpty());

        VoSearchContext ctxFind = new VoSearchContext();
        ctxFind.setParameters(createSearchContextParams("filter", "FutureRobots"));
        ctxFind.setSize(10);
        VoSearchResult<VoBrand> brandFind = voBrandService.getFilteredBrands(ctxFind);
        assertNotNull(brandFind);
        assertFalse(brandFind.getItems().isEmpty());

        final VoBrand c101 = brandFind.getItems().get(0);
        assertEquals(101L, c101.getBrandId());
        assertEquals("FutureRobots", c101.getName());

    }

    @Test
    public void testBrandCRUD() throws Exception {

        final VoBrand brand = new VoBrand();
        brand.setName("TEST CRUD");

        final VoBrand created = voBrandService.createBrand(brand);
        assertTrue(created.getBrandId() > 0L);

        final VoBrand afterCreated = voBrandService.getBrandById(created.getBrandId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());

        created.setName("TEST CRUD UPDATE");

        final VoBrand updated = voBrandService.updateBrand(created);
        assertEquals("TEST CRUD UPDATE", updated.getName());

        VoSearchContext ctx = new VoSearchContext();
        ctx.setParameters(createSearchContextParams("filter", "TEST CRUD UPDATE"));
        ctx.setSize(10);

        assertTrue(voBrandService.getFilteredBrands(ctx).getTotal() > 0);

        final List<VoAttrValueBrand> attributes = voBrandService.getBrandAttributes(updated.getBrandId());
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());

        final VoAttrValueBrand updateAttribute = attributes.stream().filter(
                attr -> "BRAND_IMAGE0".equals(attr.getAttribute().getCode())
        ).findFirst().get();

        assertNull(updateAttribute.getVal());
        updateAttribute.setVal("ABC.jpg");
        updateAttribute.setValBase64Data("data:image/jpeg;base64,AAAA");
        updateAttribute.setDisplayVals(Collections.singletonList(MutablePair.of("en", "Abc value")));

        final List<VoAttrValueBrand> attributesAfterCreate = voBrandService.updateBrandAttributes(Collections.singletonList(MutablePair.of(updateAttribute, Boolean.FALSE)));
        final VoAttrValueBrand attributeAfterCreate = attributesAfterCreate.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())).findFirst().get();
        assertEquals("abc_TEST-CRUD-UPDATE_a.jpg", attributeAfterCreate.getVal());
        assertEquals("data:image/jpeg;base64,AA==", attributeAfterCreate.getValBase64Data());
        assertEquals("en", attributeAfterCreate.getDisplayVals().get(0).getFirst());
        assertEquals("Abc value", attributeAfterCreate.getDisplayVals().get(0).getSecond());

        final List<VoAttrValueBrand> attributesAfterRemove = voBrandService.updateBrandAttributes(Collections.singletonList(MutablePair.of(attributeAfterCreate, Boolean.TRUE)));
        final VoAttrValueBrand attributeAfterRemove = attributesAfterRemove.stream().filter(attr -> attr.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())).findFirst().get();
        assertNull(attributeAfterRemove.getVal());

        voBrandService.removeBrand(updated.getBrandId());

        assertTrue(voBrandService.getFilteredBrands(ctx).getTotal() == 0);

    }
}