package org.yes.cart.domain.dto.factory;

import org.yes.cart.domain.dto.adapter.BeanFactory;

import java.io.Serializable;

/**
 * Simple factory for DTO's.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 11:30:25 PM
 */
public interface DtoFactory extends BeanFactory, Serializable {

    /**
     * @param iface interface for which to create an instance.
     * @return new object instace
     * @throws UnmappedInterfaceException thrown when no concrete class is mapped for
     *                                    given interface
     * @throws UnableToCreateInstanceException
     *                                    throw when JVM cannot instanciate new class
     *                                    instance.
     */
    //<T> T getByKey(final Class<T> iface)
     //       throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
