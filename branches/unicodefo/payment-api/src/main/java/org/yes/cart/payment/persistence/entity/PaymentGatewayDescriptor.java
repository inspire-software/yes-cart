package org.yes.cart.payment.persistence.entity;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface PaymentGatewayDescriptor extends Descriptor {


    /**
     * Get paymnet gateway priority.
     *
     * @return piority.
     */
    int getPriority();


    /**
     * Get the payment gateway url. Url can be a web service url,
     * spring name bean, jndi name. For more information see servise locator from core.
     *
     * @return url of payment gateway.
     */
    String getUrl();


    /**
     * Get service login name.
     *
     * @return login name.
     */
    String getLogin();

    /**
     * Set service login name.
     *
     * @param login login name.
     */
    void setLogin(String login);

    /**
     * Get service passwd.
     *
     * @return passwd.
     */
    String getPassword();

    /**
     * Set service passwd.
     *
     * @param password passwrd.
     */
    void setPassword(String password);


}
