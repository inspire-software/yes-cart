package org.yes.cart.domain.entity;

import java.util.Set;

/**
 * Attribute group interface.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttributeGroup extends Auditable {


    /**
     * Get primary key.
     *
     * @return primary key.
     */
    long getAttributegroupId();

    /**
     * Set primary key.
     *
     * @param attributegroupId pk value
     */
    void setAttributegroupId(long attributegroupId);


    /**
     * Get attribute group code.
     *
     * @return attribute group code.
     */
    String getCode();

    /**
     * Set code value
     *
     * @param code code value.
     */
    void setCode(String code);

    /**
     * Get attribute group name.
     *
     * @return
     */
    String getName();

    /**
     * Set name.
     *
     * @param name name value.
     */
    void setName(String name);

    /**
     * Get attribute group description.
     *
     * @return
     */
    String getDescription();

    /**
     * Set description.
     *
     * @param description description value.
     */
    void setDescription(String description);

    /**
     * Get list of slave attributes
     *
     * @return
     */
    Set<Attribute> getAttributes();

    /**
     * Set attributes.
     *
     * @param attributes attributes definition.
     */
    void setAttributes(Set<Attribute> attributes);


}


