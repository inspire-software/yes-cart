package org.yes.cart.payment.persistence.entity;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface Descriptor {

    /**
     * Get name.
     *
     * @return name.
     */
    String getName();

    /**
     * Get description.
     *
     * @return description.
     */
    String getDescription();


    /**
     * Get label.
     *
     * @return label.
     */
    String getLabel();


    /**
     * @param name name
     */
    void setName(String name);

    /**
     * @param description description
     */
    void setDescription(String description);

    /**
     * @param label label
     */
    void setLabel(String label);


}
