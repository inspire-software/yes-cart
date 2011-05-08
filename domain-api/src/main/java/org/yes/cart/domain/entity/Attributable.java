package org.yes.cart.domain.entity;

import java.util.Collection;

/**
 * Mark objects as attributable.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Attributable {

    /**
     * Get all attibute values.
     *
     * @return all attr values.
     */
    Collection<AttrValue> getAllAttibutes();


    /**
     * Get all  attributes filtered by given attribute code.
     *
     * @param attributeCode code of attribute
     * @return collection of  attributes filtered by attribute name or empty collection if no attribute were found.
     */
    Collection/*<AttrValue>*/ getAttributesByCode(String attributeCode);

    /**
     * Get single attribute.
     *
     * @param attributeCode code of attribute
     * @return single {@link AttrValue} or null if not found.
     */
    AttrValue getAttributeByCode(String attributeCode);


    /**
     * Get category name.
     *
     * @return category name.
     */
    String getName();

    /**
     * Set category name.
     *
     * @param name category name.
     */
    void setName(String name);

    /**
     * Get category description.
     *
     * @return category decription.
     */
    String getDescription();

    /**
     * Set description
     *
     * @param description description
     */
    void setDescription(String description);


}
