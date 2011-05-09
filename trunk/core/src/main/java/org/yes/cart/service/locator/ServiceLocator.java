package org.yes.cart.service.locator;

/**
 *
 * Need to locate services, that can be in external modules or in local spring context.
 * Supports: web services, ejb services, locat spring context services.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface ServiceLocator {

/**
     * Get service instance or proxy or home interface.
     * ATM the nature of service is dual: web or ejb, so caller of this method must cast to particular service interface.
     * Particular service instantiation depends from his url. Distinguish between url must be non ambiguity. Example
     * http(s):/ will lead to web service creation
     * java:/ ldap:/ ejb service
     *
     * For web services following operation will be performed: <code>
     *       return javax.xml.ws.Service.create(serviceWsdlUrl, qname).getPort(ServiceInterface class);
     * </code>
     *
     * For Ejb: <code>
     *      Object oRef = initialContext.lookup( jndiName );
     *      return PortableRemoteObject.narrow( oRef, ServiceInterface class );
     * </code>
     *
     * Corba support not implemented but can be easily added.
     *
     * @param iface service interface
     * @param seriveUrl service url to find
     * @param loginName login name to service
     * @param password  passwrd to service
     * @throws RuntimeException in case if service can not be instantiated  or  founded
     * @return service instance.
     */
    <T> T getServiceInstance(String seriveUrl, Class<T> iface, String loginName, String password) throws RuntimeException;


}
