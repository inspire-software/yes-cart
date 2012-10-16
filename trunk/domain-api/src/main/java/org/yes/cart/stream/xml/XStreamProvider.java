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

package org.yes.cart.stream.xml;

import java.io.InputStream;

/**
 * Interface to encapsulate creation of XStreams.
 *
 * XStream instantiation is an expensive process so this method should
 * use as much cacheing as possible. XStream instance itself is thread-safe
 * therefore it can be safely re-used after it had been configured once.
 *
 * User: denispavlov
 * Date: 12-08-03
 * Time: 8:13 AM
 */
public interface XStreamProvider<T> {

    /**
     * @param xml representation of the object
     * @return object
     */
    T fromXML(String xml);

    /**
     * @param is xml representation of the object
     * @return object
     */
    T fromXML(InputStream is);

    /**
     * @param object object to serialize
     * @return xml representation
     */
    String toXML(T object);

}
