package org.yes.cart.domain.dto;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 * Brand interface.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface BrandDTO extends Serializable {

    /**
     * Get brand pk.
     * @return brand pk.
     */
    long getBrandId();

    /**
     * Set brand pk.
     * @param brandId brand pk.
     */
    void setBrandId(long brandId);

    /**
     * Brand name.
     * @return name.
     */
    String getName();

    /**
     * Set brand name.
     * @param name name
     */
    void setName(String name);

    /**
     * Get descrition.
     * @return description.
     */
    String getDescription();

    /**
     * Set description.
     * @param description description
     */
    void setDescription(String description);

    /**
     * Get the brand attributes.
     * @return brand attributes.
     */
    Collection<AttrValueBrandDTO> getAttribute();

    /** Set brand attributes.
     * @param attribute brand attributes to set.
     * */
    void setAttribute(Collection<AttrValueBrandDTO> attribute);

}
