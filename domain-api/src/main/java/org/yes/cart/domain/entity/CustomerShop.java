package org.yes.cart.domain.entity;

/**
 * Represent relation between customer and shop to support multistore.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerShop {

    /**
     * Get pk;
     *
     * @return pk value
     */
    long getCustomerShopId();

    /**
     * Set pk value.
     *
     * @param customerShopId pk value.
     */
    void setCustomerShopId(long customerShopId);

    /**
     * Get Customer.
     *
     * @return customer.
     */
    Customer getCustomer();

    /**
     * Set Customer
     *
     * @param customer customer.
     */
    void setCustomer(Customer customer);

    /**
     * Get shop.
     *
     * @return shop.
     */
    Shop getShop();

    /**
     * Set the shop.
     *
     * @param shop shop
     */
    void setShop(Shop shop);


}


