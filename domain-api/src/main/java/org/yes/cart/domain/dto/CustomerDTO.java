package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Unique;

import java.io.Serializable;
import java.util.Set;

/**
 * Customet DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerDTO extends Unique {

    /**
     * Get person id.
     *
     * @return customer email.
     */
    public String getEmail();

    /**
     * Set customer email
     *
     * @param email email
     */
    public void setEmail(String email);

    /**
     * Get first name.
     *
     * @return first name
     */
    String getFirstname();

    /**
     * Set first name
     *
     * @param firstname value to set
     */
    void setFirstname(String firstname);

    /**
     * Get last name.
     *
     * @return last name
     */
    String getLastname();

    /**
     * Set last name
     *
     * @param lastname value to set
     */
    void setLastname(String lastname);

    /**
     * Get middle name
     *
     * @return middle name
     */
    String getMiddlename();

    /**
     * Set middle name
     *
     * @param middlename value to set
     */
    void setMiddlename(String middlename);


    /**
     * Get primary key.
     *
     * @return pk value.
     */
    public long getCustomerId();

    /**
     * Set pk value.
     *
     * @param customerId pk value to set
     */
    public void setCustomerId(long customerId);

    /**
     * Get attibutes.
     *
     * @return list of attributes
     */
    Set<AttrValueCustomerDTO> getAttribute();

    /**
     * Set list of attributes.
     *
     * @param attribute list of attributes
     */
    void setAttribute(Set<AttrValueCustomerDTO> attribute);


}
