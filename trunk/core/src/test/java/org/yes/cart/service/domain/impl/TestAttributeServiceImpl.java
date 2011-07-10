package org.yes.cart.service.domain.impl;

import org.junit.Test;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.service.domain.AttributeService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestAttributeServiceImpl extends BaseCoreDBTestCase {

    public TestAttributeServiceImpl() {
        super();
    }

    /**
     * Prove of availability to getByKey list of attributes, that can have mupliple values within given
     * <code>attributeGroupCode</code>.
     */
    @Test
    public void testFindAttributesWithMultipleValues() {
        final AttributeService atributeService = (AttributeService) ctx.getBean(ServiceSpringKeys.ATTRIBUTE_SERVICE);

        //product has 5 attributes with allowed multiple values
        List<Attribute> list = atributeService.findAttributesWithMultipleValues(AttributeGroupNames.PRODUCT);
        assertNotNull(list);
        assertEquals(5, list.size());

        //shop has not attibutes with multiple values
        list = atributeService.findAttributesWithMultipleValues(AttributeGroupNames.SHOP);
        assertNull(list);

    }




    /**
     * Prove of availability to getByKey list of available attributes within given
     * <code>attributeGroupCode</code>, that can be assigned to business entity.
     */
    @Test
    public void testFindAvailableAttributes() {

        final List<String> allCodes = Arrays.asList("URI", "CATEGORY_ITEMS_PER_PAGE", "CATEGORY_IMAGE_RETREIVE_STRATEGY");

        final AttributeService atributeService = (AttributeService) ctx.getBean(ServiceSpringKeys.ATTRIBUTE_SERVICE);

        // getByKey all attributes available for category
        List<Attribute> attributes = atributeService.findAvailableAttributes(AttributeGroupNames.CATEGORY, null);
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());
        for (Attribute attr : attributes) {
            assertTrue(allCodes.contains(attr.getCode()));
        }


        // category already has all attributes
        attributes = atributeService.findAvailableAttributes(AttributeGroupNames.CATEGORY, allCodes);
        assertNotNull(attributes);
        assertTrue(attributes.isEmpty());

        // category already has all attributes
        attributes = atributeService.findAvailableAttributes(AttributeGroupNames.CATEGORY, Arrays.asList("URI"));
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());
        assertEquals("CATEGORY_ITEMS_PER_PAGE", attributes.get(0).getCode());

    }

   /**
     * Test .
     */
    @Test
    public void testAttributeService() {

        final AttributeService atributeService = (AttributeService) ctx.getBean(ServiceSpringKeys.ATTRIBUTE_SERVICE);

        List<String> codes = atributeService.getAllAttributeCodes();

        assertNotNull(codes);
        assertFalse(codes.isEmpty());

        Map<String, String> map = atributeService.getAttributeNamesByCodes(codes);

        assertNotNull(map);
        //assertEquals(codes.size(), map.size());

        /*for(Object code : codes) {
            assertNotNull(map.getByKey(code));
        } */


    }

}
