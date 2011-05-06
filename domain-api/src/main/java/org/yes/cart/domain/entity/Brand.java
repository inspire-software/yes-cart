
package org.yes.cart.domain.entity;

import java.util.Collection;

/**
 * Brand
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Brand extends Auditable {

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
     * Get brand's attributes.
     * @return brand's attributes.
     */
    Collection<AttrValueBrand> getAttribute();

    /**
     * Set brand's attributes.
     * @param attribute brand's attributes.
     */
    void setAttribute(Collection<AttrValueBrand> attribute);



}


