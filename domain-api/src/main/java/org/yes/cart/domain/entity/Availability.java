
package org.yes.cart.domain.entity;


/**
 *
 * Product availability.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 *
 */
public interface Availability extends Auditable {

    /**
     * When available on warehouse.
     */
    public static long STANDARD = 1;
    /**
     * For preorder.
     */
    public static long PREORDER = 2;

    /**
     * Available for backorder.
     */
    public static long BACKORDER = 4;
    /**
     * Always available
     */
    public static long ALWAYS = 8;

    /**
     * Get the pk
     * @return pk
     */
    public long getAvailabilityId();

    /**
     * Set pk
     * @param availabilityId pk
     */
    public void setAvailabilityId(long availabilityId);

    /**
     * Get name.
     * @return name
     */
    public String getName();

    /**
     * Set name.
     * @param name name
     */
    public void setName(String name);

    /**
     *
     * Get description.
     *
     * @return description
     */
    public String getDescription();

    /**
     * Set description
     * @param description        description of availability
     */
    public void setDescription(String description);

}


