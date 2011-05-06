package org.yes.cart.dao;

/**
 *
 * Entity factory interface.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface EntityFactory {

    /**
     * Create new entity instance by his interface
     * @param interfaceClass interface class.
     * @param <T> type
     * @return new entity
     */
    <T> T getByIface(Class interfaceClass);

    /**
     * @param entityBeanKey string key reference to the bean required
     * @return new domain entity instance
     */
    <T> T getByKey(String entityBeanKey);

    /**
     * Get the implementation class by given interface.
     * @param interfaceClass given interface
     * @return implementation class
     */
    Class getImplClass(Class interfaceClass);

    /**
     * Get the implementation class by given interface name.
     * @param entityBeanKey given interface name
     * @return implementation class
     */
    Class getImplClass(String entityBeanKey);


}
