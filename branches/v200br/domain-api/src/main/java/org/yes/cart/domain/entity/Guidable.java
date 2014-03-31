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

package org.yes.cart.domain.entity;

/**
 * Entity that have code property to identify them. This property should be used
 * for GUID if this is object has GUID property.
 *
 * This is a marker interface so that interceptors that listen to persistence layer
 * operations can identify these objects.
 *
 * User: denispavlov
 * Date: 12-08-16
 * Time: 6:23 PM
 */
public interface Guidable {

    /**
     * GUID is an auto generated identified that can help in entity synchronisation
     * and global identification.
     *
     * @return guid that uniquely identifies this object.
     */
    String getGuid();

    /**
     * @param guid guid that uniquely identifies this object.
     */
    void setGuid(String guid);

}
