package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Unique;

import java.io.Serializable;

/**
 * Group of attributes.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttributeGroupDTO extends Unique {

    /**
     * Get pk value.
     *
     * @return pk value.
     */
    long getAttributegroupId();

    /**
     * Set pk value.
     *
     * @param attributegroupId pk value
     */
    void setAttributegroupId(long attributegroupId);

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

}
