package org.yes.cart.service.domain;


import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.misc.Pair;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface AttributeService extends GenericService<Attribute> {

    /**
     * Get all attrubute codes.
     *
     * @return set of attribute codes.
     */
    List<String> getAllAttributeCodes();

    /**
     * Get attrubute names.
     *
     * @param codes attribute codes
     * @return map of attribute code - name.
     */
    Map<String, String> getAttributeNamesByCodes(List<String> codes);


    /**
     * Get list of attributes by given group code.
     *
     * @param attributeGroupCode given group code
     * @return list of {@link Attribute}
     */
    List<Attribute> findByAttributeGroupCode(String attributeGroupCode);

    /**
     * Get attribute by given code.
     *
     * @param attributeCode given  code
     * @return instance {@link Attribute} if fount, otherwise null
     */
    Attribute findByAttributeCode(String attributeCode);

    /**
     * Get list of available attributes within given <code>attributeGroupCode</code>, that can be assigned to business entity.
     *
     * @param attributeGroupCode     see AttributeGroup#code
     * @param assignedAttributeCodes list of codes, that already assinged to entity
     * @return list of available attributes to fill with values.
     */
    List<Attribute> findAvailableAttributes(String attributeGroupCode, List<String> assignedAttributeCodes);


    /**
     * Find all attibutes in given group , that allow to have several attributes
     *
     * @param attributeGroupCode group of attributes
     * @return list of attibutes with allowed multiple values or null if no such attributes found
     */
    List<Attribute> findAttributesWithMultipleValues(String attributeGroupCode);


    /**
     * Perform merge atttribute values operation.
     * @param to merge to collection
     * @param from merge from collection
     * @return result of merge
     */
    List<Pair<String, List<AttrValue>>> merge(List<Pair<String, List<AttrValue>>> to, List<Pair<String, List<AttrValue>>> from);

    /**
     * Remove attr value from given list by given name
     * @param values list to remove from
     * @param attrName name to remove
     * @return removed {@link AttrValue} if found, otherwise null
     */
    AttrValue removeAttrValue(List<AttrValue> values, String attrName);

    /**
     * Remove section from given lisy by name.
     * @param fromList list to remove section
     * @param sectionName section name to remove.
     * @return removed section if found, otherwise null
     */
    List<AttrValue> removeAttrValues(List<Pair<String, List<AttrValue>>> fromList, String sectionName);

}
