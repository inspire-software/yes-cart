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
    public long getAttributegroupId();

    /**
     * Set primary key.
     *
     * @param attributegroupId pk value
     */
    public void setAttributegroupId(long attributegroupId);


    /**
     * Get attribute group code.
     *
     * @return attribute group code.
     */
    public String getCode();

    /**
     * Set code value
     *
     * @param code code value.
     */
    public void setCode(String code);

    /**
     * Get attribute group name.
     *
     * @return
     */
    public String getName();

    /**
     * Set name.
     *
     * @param name name value.
     */
    public void setName(String name);

    /**
     * Get attribute group description.
     *
     * @return
     */
    public String getDescription();

    /**
     * Set description.
     *
     * @param description description value.
     */
    public void setDescription(String description);

    /**
     * Get list of slave attributes
     *
     * @return
     */
    public Set<Attribute> getAttributes();

    /**
     * Set attributes.
     *
     * @param attributes attributes definition.
     */
    public void setAttributes(Set<Attribute> attributes);


}


