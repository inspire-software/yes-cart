package org.yes.cart.service.locator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public interface InstantiationStrategy {

    /**
     * Get service instance or proxy or home interface.
     * ATM the nature of service is dual: web or ejb, so caller of this method must cast to particular service interface.
     * Particular service instantiation depends from his url. Distinguish between url must be non ambiguity. Example
     * http(s):/ will lead to web service creation
     * java:/ ejb service
     * <p/>
     * For web services following operation will be performed: <code>
     * return javax.xml.ws.Service.create(serviceWsdlUrl, qname).getPort(ServiceInterface class);
     * </code>
     * <p/>
     * For Ejb: <code>
     * Object oRef = initialContext.lookup( jndiName );
     * return PortableRemoteObject.narrow( oRef, ServiceInterface class );
     * </code>
     *
     * <p/>
     * if no protocol specified in URL the local spring context will be used to search the service.
     * <p/>
     * Corba support not implemented but can be easily added.
     * For more information see particular strategy
     *
     * @param iface      service interface
     * @param serviceUrl service URL
     * @param loginName login name to service
     * @param password  passwrd to service
     * @return service instance
     * @throws RuntimeException in case if service instance can not be created   or  incorrect name or address
     */
    <T> T getInstance(String serviceUrl, Class<T> iface, String loginName, String password) throws RuntimeException;

}
