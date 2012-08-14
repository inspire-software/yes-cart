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

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.service.domain.AttributeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestAttributeServiceImpl extends BaseCoreDBTestCase {

    private AttributeService attributeService;

    @Before
    public void setUp() throws Exception {
        attributeService = (AttributeService) ctx().getBean(ServiceSpringKeys.ATTRIBUTE_SERVICE);
    }

    // TODO fix to not depend on order or running
    @Test
    public void testFindByAttributeGroupCode() {
        List<Attribute> attrs = attributeService.findByAttributeGroupCode(AttributeGroupNames.CUSTOMER);
        assertEquals(1, attrs.size());
    }

    @Test
    public void testFindByAttributeCode() {
        Attribute attrs = attributeService.findByAttributeCode(AttributeNamesKeys.CUSTOMER_PHONE);
        assertNotNull(attrs);
    }

    @Test
    public void testFindAttributesWithMultipleValues1() {
        assertEquals(5,
                attributeService.findAttributesWithMultipleValues(AttributeGroupNames.PRODUCT).size());
    }

    @Test
    public void testFindAvailableAttributes1() {
        List<Attribute> attrs = attributeService.findAvailableAttributes(AttributeGroupNames.PRODUCT, null); // getByKey all
        assertEquals(20, attrs.size());
        List<String> assignedAttributes = new ArrayList<String>();
        for (Attribute attr : attrs) {
            assignedAttributes.add(attr.getCode());
        }
        attrs = attributeService.findAvailableAttributes(AttributeGroupNames.PRODUCT, assignedAttributes);
        assertEquals(0, attrs.size());
    }

    /**
     * Prove of availability to getByKey list of attributes, that can have mupliple values within given
     * <code>attributeGroupCode</code>.
     */
    @Test
    public void testFindAttributesWithMultipleValues2() {
        //product has 5 attributes with allowed multiple values
        List<Attribute> list = attributeService.findAttributesWithMultipleValues(AttributeGroupNames.PRODUCT);
        assertNotNull(list);
        assertEquals(5, list.size());
        //shop has not attributes with multiple values
        list = attributeService.findAttributesWithMultipleValues(AttributeGroupNames.SHOP);
        assertNull(list);
    }

    /**
     * Prove of availability to getByKey list of available attributes within given
     * <code>attributeGroupCode</code>, that can be assigned to business entity.
     */
    @Test
    public void testFindAvailableAttributes2() {
        List<String> allCodes = Arrays.asList("URI", "CATEGORY_ITEMS_PER_PAGE", "CATEGORY_IMAGE_RETRIEVE_STRATEGY");
        // getByKey all attributes available for category
        List<Attribute> attributes = attributeService.findAvailableAttributes(AttributeGroupNames.CATEGORY, null);
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());
        for (Attribute attr : attributes) {
            assertTrue(allCodes.contains(attr.getCode()));
        }
        // category already has all attributes
        attributes = attributeService.findAvailableAttributes(AttributeGroupNames.CATEGORY, allCodes);
        assertNotNull(attributes);
        assertTrue(attributes.isEmpty());
        // category already has all attributes
        attributes = attributeService.findAvailableAttributes(AttributeGroupNames.CATEGORY, Arrays.asList("URI"));
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());
        assertEquals("CATEGORY_ITEMS_PER_PAGE", attributes.get(0).getCode());
    }

    @Test
    public void testAttributeService() {
        List<String> codes = attributeService.getAllAttributeCodes();
        assertNotNull(codes);
        assertFalse(codes.isEmpty());
        Map<String, String> map = attributeService.getAttributeNamesByCodes(codes);
        assertNotNull(map);
    }

}
