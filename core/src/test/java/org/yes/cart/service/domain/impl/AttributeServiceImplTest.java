package org.yes.cart.service.domain.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.service.domain.AttributeService;

import java.util.ArrayList;
import java.util.List;

/**
 ** User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AttributeServiceImplTest  extends BaseCoreDBTestCase {

    private AttributeService attributeService = null;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        attributeService = (AttributeService) ctx.getBean(ServiceSpringKeys.ATTRIBUTE_SERVICE);
    }

    @After
    public void tearDown() {
        attributeService = null;
        super.tearDown();
    }

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
    public void testFindAttributesWithMultipleValues() {
        assertEquals(
                5,
                attributeService.findAttributesWithMultipleValues(AttributeGroupNames.PRODUCT).size());
    }

    @Test
    public void testFindAvailableAttributes() {
        List<Attribute> attrs = attributeService.findAvailableAttributes(AttributeGroupNames.PRODUCT, null); // getByKey all
        assertEquals(20, attrs.size());
        List<String> assignedAttributes = new ArrayList<String>();
        for (Attribute attr : attrs) {
            assignedAttributes.add(attr.getCode());
        }
        attrs = attributeService.findAvailableAttributes(AttributeGroupNames.PRODUCT, assignedAttributes);
        assertEquals(0, attrs.size());
    }

}
