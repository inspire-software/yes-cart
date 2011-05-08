package org.yes.cart.domain.entity;

/**
 * Attribute view groups. Attributes, that will be shown on web
 * page can be groupped. For example:
 * size group will have height, width, lenght attributes.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttributeViewGroup extends Auditable {

    /**
     * Pk value.
     *
     * @return pk value.
     */
    long getAttributeViewGroupId();

    /**
     * Set pk value.
     *
     * @param attributeViewGroupId pk value.
     */
    void setAttributeViewGroupId(long attributeViewGroupId);

    /**
     * Get name.
     *
     * @return name of view group.
     */
    String getName();

    /**
     * Set name of view group.
     *
     * @param name name.
     */
    void setName(String name);

    /**
     * View group description.
     *
     * @return view group descrition.
     */
    String getDescription();

    /**
     * Set view groiup description.
     *
     * @param description description.
     */
    void setDescription(String description);


}
