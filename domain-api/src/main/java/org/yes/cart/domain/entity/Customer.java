package org.yes.cart.domain.entity;

import java.util.Collection;
import java.util.List;


/**
 * Customer / Shopper.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Customer extends RegisteredPerson, Auditable {

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
     * Get orders
     *
     * @return customer orders.
     */
    public Collection<CustomerOrder> getOrders();

    /**
     * Set customer orders.
     *
     * @param orders orders to set
     */
    public void setOrders(Collection<CustomerOrder> orders);


    /**
     * Wish list.
     *
     * @return wish list.
     */
    public Collection<CustomerWishList> getWishList();

    /**
     * Set wish list
     *
     * @param wishList wish list.
     */
    public void setWishList(Collection<CustomerWishList> wishList);

    /**
     * Get all customer attributes.
     *
     * @return collection of customer attributes.
     */
    Collection<AttrValueCustomer> getAttribute();

    /**
     * Get all customer attributes filtered by given attribute code.
     *
     * @param attributeCode code of attribute
     * @return collection of customer attributes filtered by
     *         attribute name or empty collection if no attribute were found.
     */
    Collection<AttrValueCustomer> getAttributesByCode(String attributeCode);

    /**
     * Get single attribute.
     *
     * @param attributeCode code of attribute
     * @return single {@link AttrValue} or null if not found.
     */
    AttrValueCustomer getAttributeByCode(String attributeCode);


    /**
     * Get customer addreese by given type.
     *
     * @param addressType address type
     * @return list of addresses.
     */
    List<Address> getAddresses(String addressType);

    /**
     * Get default address wuth given type.
     *
     * @param addressType address type
     * @return default address
     */
    Address getDefaultAddress(String addressType);


    /**
     * Set collection of customer attributes.
     *
     * @param attribute collection of customer  attributes
     */
    void setAttribute(Collection<AttrValueCustomer> attribute);

    /**
     * Get all customer addresses.
     *
     * @return customer addresses
     */
    Collection<Address> getAddress();

    /**
     * Set customer addresses.
     *
     * @param address customer addresses.
     */
    void setAddress(Collection<Address> address);


    /**
     * Get assigned shops.
     *
     * @return shops
     */
    Collection<CustomerShop> getShops();

    /**
     * Set assigned shops.
     *
     * @param shops shops
     */
    void setShops(Collection<CustomerShop> shops);

}


