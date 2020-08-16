/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.domain.entity.ProductTypeAttr;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.vo.VoProductTypeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 27/09/2019
 * Time: 17:18
 */
public class VoProductTypeServiceImplTest extends BaseCoreDBTestCase {

    private VoProductTypeService voProductTypeService;

    @Before
    public void setUp() {
        voProductTypeService = (VoProductTypeService) ctx().getBean("voProductTypeService");
        super.setUp();
    }

    @Test
    public void testGetProductTypes() throws Exception {

        VoSearchContext ctx;

        ctx = new VoSearchContext();
        ctx.setStart(0);
        ctx.setSize(10);
        VoSearchResult<VoProductTypeInfo> ptNoFilter = voProductTypeService.getFilteredTypes(ctx);
        assertNotNull(ptNoFilter);
        assertFalse(ptNoFilter.getItems().isEmpty());

        ctx = new VoSearchContext();
        ctx.setParameters(createSearchContextParams("filter", "Laser"));
        ctx.setStart(0);
        ctx.setSize(10);
        VoSearchResult<VoProductTypeInfo> ptFind = voProductTypeService.getFilteredTypes(ctx);
        assertNotNull(ptFind);
        assertFalse(ptFind.getItems().isEmpty());

        final VoProductTypeInfo pt6 = ptFind.getItems().get(0);
        assertEquals("6", pt6.getGuid());
        assertEquals("Blaster", pt6.getName());

        ctx = new VoSearchContext();
        ctx.setParameters(createSearchContextParams("filter", "!MP3 Player"));
        ctx.setStart(0);
        ctx.setSize(10);
        VoSearchResult<VoProductTypeInfo> ptExact = voProductTypeService.getFilteredTypes(ctx);
        assertNotNull(ptExact);
        assertEquals(1, ptExact.getTotal());
        assertEquals("2", ptExact.getItems().get(0).getGuid());

        ctx = new VoSearchContext();
        ctx.setParameters(createSearchContextParams("filter", "#POWERSUPPLY"));
        ctx.setStart(0);
        ctx.setSize(10);
        VoSearchResult<VoProductTypeInfo> ptByAttrCode = voProductTypeService.getFilteredTypes(ctx);
        assertNotNull(ptByAttrCode);
        assertFalse(ptByAttrCode.getItems().isEmpty());
        assertTrue(ptByAttrCode.getItems().stream().allMatch(pt -> "6".equals(pt.getGuid())));

    }

    @Test
    public void testProductTypeCRUD() throws Exception {

        final VoProductType productType = new VoProductType();
        productType.setName("TEST CRUD");
        productType.setDisplayNames(Collections.singletonList(MutablePair.of("en", "Test")));

        final VoProductType created = voProductTypeService.createType(productType);
        assertTrue(created.getProducttypeId() > 0L);

        final VoProductType afterCreated = voProductTypeService.getTypeById(created.getProducttypeId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());
        assertNotNull(afterCreated.getViewGroups());
        assertTrue(afterCreated.getViewGroups().isEmpty());

        afterCreated.setName("TEST CRUD UPDATE");
        final VoProductTypeViewGroup grp = new VoProductTypeViewGroup();
        grp.setName("Test");
        grp.setDisplayNames(Collections.singletonList(MutablePair.of("en", "Test")));
        grp.setAttrCodeList(Arrays.asList("MATERIAL", "POWERSUPPLY"));
        afterCreated.setViewGroups(Collections.singletonList(grp));

        final VoProductType updated = voProductTypeService.updateType(afterCreated);
        assertEquals("TEST CRUD UPDATE", updated.getName());
        assertNotNull(afterCreated.getViewGroups());
        assertEquals(1, afterCreated.getViewGroups().size());

        VoSearchContext ctx = new VoSearchContext();
        ctx.setParameters(createSearchContextParams("filter", "!TEST CRUD UPDATE"));
        ctx.setSize(10);

        assertTrue(voProductTypeService.getFilteredTypes(ctx).getTotal() > 0);

        final List<VoProductTypeAttr> attributesEmpty = voProductTypeService.getTypeAttributes(updated.getProducttypeId());
        assertNotNull(attributesEmpty);
        assertTrue(attributesEmpty.isEmpty());

        final VoProductTypeAttr attr1 = new VoProductTypeAttr();
        final VoAttribute attribute1 = new VoAttribute();
        attribute1.setAttributeId(2003L);
        attribute1.setCode("MATERIAL");
        attr1.setProducttypeId(updated.getProducttypeId());
        attr1.setAttribute(attribute1);
        attr1.setNavigationType(ProductTypeAttr.NAVIGATION_TYPE_RANGE);
        final VoProductTypeAttrNavigationRanges ranges = new VoProductTypeAttrNavigationRanges();
        final VoProductTypeAttrNavigationRange range = new VoProductTypeAttrNavigationRange();
        range.setRange("1-1000");
        range.setDisplayVals(Collections.singletonList(MutablePair.of("en", "Test")));
        ranges.setRanges(Collections.singletonList(range));
        attr1.setRangeNavigation(ranges);

        final VoProductTypeAttr attr2 = new VoProductTypeAttr();
        final VoAttribute attribute2 = new VoAttribute();
        attribute2.setAttributeId(2004L);
        attribute2.setCode("BATTERY_TYPE");
        attr2.setProducttypeId(updated.getProducttypeId());
        attr2.setAttribute(attribute2);
        attr2.setNavigationType(ProductTypeAttr.NAVIGATION_TYPE_SINGLE);

        final List<VoProductTypeAttr> attributes = voProductTypeService.updateTypeAttributes(Arrays.asList(MutablePair.of(attr1, Boolean.FALSE), MutablePair.of(attr2, Boolean.FALSE)));

        final VoProductTypeAttr updateAttribute = attributes.stream().filter(
                att2 -> "MATERIAL".equals(att2.getAttribute().getCode())
        ).findFirst().get();

        final List<VoProductTypeAttr> attributesAfterRemove = voProductTypeService.updateTypeAttributes(Collections.singletonList(MutablePair.of(updateAttribute, Boolean.TRUE)));
        assertTrue(attributesAfterRemove.stream().noneMatch(att2 -> att2.getAttribute().getCode().equals(updateAttribute.getAttribute().getCode())));

        voProductTypeService.removeType(updated.getProducttypeId());

        assertFalse(voProductTypeService.getFilteredTypes(ctx).getTotal() > 0);

    }

    @Test
    public void testCopy() throws Exception {

        final VoProductType copy = voProductTypeService.copyType(1L, null);

        assertNotNull(copy);
        assertNotEquals("1", copy.getGuid());
        assertEquals("Robots", copy.getName());
        assertFalse(copy.getDisplayNames().isEmpty());

        assertFalse(copy.getViewGroups().isEmpty());
        assertFalse(voProductTypeService.getTypeAttributes(copy.getProducttypeId()).isEmpty());

        voProductTypeService.removeType(copy.getProducttypeId());

        final VoProductTypeInfo base = new VoProductTypeInfo();
        base.setName("Another");
        final VoProductType copy2 = voProductTypeService.copyType(1L, base);

        assertNotNull(copy2);
        assertNotEquals("1", copy2.getGuid());
        assertEquals("Another", copy2.getName());
        assertTrue(copy2.getDisplayNames().isEmpty());

        assertFalse(copy2.getViewGroups().isEmpty());
        assertFalse(voProductTypeService.getTypeAttributes(copy2.getProducttypeId()).isEmpty());

        voProductTypeService.removeType(copy2.getProducttypeId());

    }
}