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

package org.yes.cart.web.service.ws.client;

/**
 * JAX-WS is not thread safe as per standard requirement. WsClientFactory
 * creates a client that wraps JAX-WS service bean and therefore it is
 * also assumed to be not thread safe.
 *
 * However each web service client is specific to a particular user using
 * the application. It is also safe to assume that
 *
 * User: denispavlov
 * Date: 13-10-09
 * Time: 10:53 PM
 */
public interface WsClientFactory<T> {

    /**
     * Get client for web service.
     *
     * @return proxy client for given service
     */
    T getService();

    /**
     * Release service back to pool.
     *
     * @param service
     */
    void release(T service);

}
