/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
