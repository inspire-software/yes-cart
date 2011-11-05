package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.impl.AttrValueEntityBrand;
import org.yes.cart.domain.entity.impl.AttributeEntity;
import org.yes.cart.domain.misc.Pair;
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
        attributeService = (AttributeService) ctx.getBean(ServiceSpringKeys.ATTRIBUTE_SERVICE);
    }

    /**
     * Prove of availability to getByKey list of attributes, that can have mupliple values within given
     * <code>attributeGroupCode</code>.
     */
    @Test
    public void testFindAttributesWithMultipleValues() {
        //product has 5 attributes with allowed multiple values
        List<Attribute> list = attributeService.findAttributesWithMultipleValues(AttributeGroupNames.PRODUCT);
        assertNotNull(list);
        assertEquals(5, list.size());
        //shop has not attibutes with multiple values
        list = attributeService.findAttributesWithMultipleValues(AttributeGroupNames.SHOP);
        assertNull(list);
    }

    /**
     * Prove of availability to getByKey list of available attributes within given
     * <code>attributeGroupCode</code>, that can be assigned to business entity.
     */
    @Test
    public void testFindAvailableAttributes() {
        final List<String> allCodes = Arrays.asList("URI", "CATEGORY_ITEMS_PER_PAGE", "CATEGORY_IMAGE_RETREIVE_STRATEGY");
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

    @Test
    public void testRemoveAttrValues() {
        List<Pair<String, List<AttrValue>>> to = getTo();
        List<AttrValue> removedList = attributeService.removeAttrValues(to, "section-2");
        assertNull("Section not present", removedList);
        removedList = attributeService.removeAttrValues(to, "section-0");
        assertNotNull("section-0 must be removed", removedList);
        assertEquals("section-0 must have 3 items", 3, removedList.size());
        assertEquals("Only 2 section left", 2, to.size());
    }

    @Test
    public void testRemoveAttrValue() {
        List<AttrValue> removedList = attributeService.removeAttrValues(getTo(), "section-0");
        assertNotNull(removedList);
        assertEquals(3, removedList.size());
        assertNull("Attribute name not present", attributeService.removeAttrValue(removedList, "someattrname"));
        assertNotNull("Attribute name must be removed", attributeService.removeAttrValue(removedList, "section_0_attrib_0"));
        assertNotNull("Attribute name must be removed", attributeService.removeAttrValue(removedList, "section_0_attrib_1"));
        assertNotNull("Attribute name must be removed", attributeService.removeAttrValue(removedList, "section_0_attrib_2"));
        assertTrue("List must be empty, because all attributes was tremoved", removedList.isEmpty());
    }

    @Test
    public void testMerge() {
        List<Pair<String, List<AttrValue>>> to = getTo();
        List<Pair<String, List<AttrValue>>> from = getFrom();
        List<Pair<String, List<AttrValue>>> rez = attributeService.merge(to, from);
        assertEquals("Result must have 5 section", 5, rez.size());
        List<AttrValue> removedList = attributeService.removeAttrValues(rez, "section-0");
        assertEquals("Size must be without changes", 3, removedList.size());
        for (AttrValue attrVal : removedList) {
            assertTrue("Value must be overwritten", attrVal.getVal().indexOf("_newval_") > -1);
        }
        removedList = attributeService.removeAttrValues(rez, "section-1");
        assertEquals("Size must be 1, because merged from list", 1, removedList.size());
        removedList = attributeService.removeAttrValues(rez, "section-2");
        assertEquals("section-2  must be added", 77, removedList.size());
        removedList = attributeService.removeAttrValues(rez, "section-5");
        assertEquals("Size must be the same os original, because not", 4, removedList.size());
        for (AttrValue attrVal : removedList) {
            String val = attrVal.getVal();
            assertTrue(val.equals("section_5_newval_0") ||
                    val.equals("section_5_value_1") ||
                    val.equals("section_5_value_2") ||
                    val.equals("section_5_value_3"));
        }
        removedList = attributeService.removeAttrValues(rez, "section-6");
        assertEquals("Size of section 6 must be 6", 6, removedList.size());
    }

    private List<Pair<String, List<AttrValue>>> getTo() {
        List<Pair<String, List<AttrValue>>> rez = new ArrayList<Pair<String, List<AttrValue>>>();
        rez.add(createAttributeSection(0, 3, "_value_"));
        rez.add(createAttributeSection(1, 0, "_value_"));
        rez.add(createAttributeSection(5, 4, "_value_"));
        return rez;
    }

    private List<Pair<String, List<AttrValue>>> getFrom() {
        List<Pair<String, List<AttrValue>>> rez = new ArrayList<Pair<String, List<AttrValue>>>();
        rez.add(createAttributeSection(0, 3, "_newval_"));
        rez.add(createAttributeSection(1, 1, "_newval_"));
        rez.add(createAttributeSection(2, 77, "_newval_"));
        rez.add(createAttributeSection(5, 1, "_newval_"));
        rez.add(createAttributeSection(6, 6, "_newval_"));
        return rez;
    }

    private Pair<String, List<AttrValue>> createAttributeSection(int idx, int valQty, String valueName) {
        List<AttrValue> attrValues = new ArrayList<AttrValue>();
        for (int i = 0; i < valQty; i++) {
            Attribute attr = new AttributeEntity();
            attr.setName("section_" + idx + "_attrib_" + i);
            AttrValue attrValue = new AttrValueEntityBrand();
            attrValue.setVal("section_" + idx + valueName + i);
            attrValue.setAttribute(attr);
            attrValues.add(attrValue);
        }
        return new Pair<String, List<AttrValue>>("section-" + idx, attrValues);
    }
}
