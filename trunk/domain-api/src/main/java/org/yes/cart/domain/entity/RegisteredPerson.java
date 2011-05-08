package org.yes.cart.domain.entity;

/**
 * Represent any registered person in system: customer, admid, call center , etc.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface RegisteredPerson {

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
     * Get password.
     *
     * @return password.
     */
    String getPassword();

    /**
     * Set password.
     *
     * @param password new password.
     */
    void setPassword(String password);


}
