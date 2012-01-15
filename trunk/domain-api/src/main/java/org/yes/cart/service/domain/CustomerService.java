package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface CustomerService extends GenericService<Customer> {

    /**
     * Find customer by given serach criteria. Serch will be performed using like operation.
     *
     * @param email      optional email
     * @param firstname  optional first name
     * @param lastname   optional last name
     * @param middlename optional middlename
     * @return list of persons, that match search criteria or empty list if nobody found or null if no search criteria provided.
     */
    List<Customer> findCustomer(String email, String firstname, String lastname, String middlename);

    /**
     * Get customer by email.
     *
     * @param email email
     * @return {@link Customer} or null if custome not found
     */
    Customer findCustomer(String email);

    /**
     * check is given email unique.
     *
     * @param email email to check
     * @return true in case if provided email not present in databse.
     */
    boolean isEmailUnique(String email);

    /**
     * Check is customer already registered.
     *
     * @param email email to check
     * @return true in case if email unique.
     */
    boolean isCustomerExists(String email);

    /**
     * Check is provided password for customer valid.
     *
     * @param email    email to check
     * @param password password
     * @return true in case if email unique.
     */
    boolean isPasswordValid(String email, String password);

    /**
     * Reset password to given user and send generated password via email.
     *
     * @param customer customer to create
     * @param shop     shop to assing
     */
    void resetPassword(Customer customer, final Shop shop);


    /**
     * Create customer and assing it to particular shop
     *
     * @param customer customer to create
     * @param shop     shop to assing
     * @return customer instance
     */
    Customer create(final Customer customer, final Shop shop);


    /**
     * Get sorted by attribute rank collection of customer attributes.
     * Not all customes attributes can be filled ot new attributes can
     * be added, so the result list will contans filled values and
     * posible values to fill.
     *
     * @param customer customer
     * @return sorted by attribute.
     */
    List<AttrValueCustomer> getRankedAttributeValues(Customer customer);

    /**
     * Add new attribute to customer. If attribute already exists, his value will be changed.
     * This method not perform any actions to persist changes.  Blank value will not be added
     * @param customer given customer
     * @param attributeCode given attribute code
     * @param attributeValue given attribute value
     */
    void addAttribute(final Customer customer, final String attributeCode, final String attributeValue);


}
