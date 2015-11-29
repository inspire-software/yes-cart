/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
 * User: denispavlov
 * Date: 01/06/2015
 * Time: 12:01
 */
public interface DataGroup extends Auditable {

    String TYPE_IMPORT = "IMPORT";
    String TYPE_EXPORT = "EXPORT";

    /**
     * Get data group pk.
     *
     * @return data group pk.
     */
    long getDatagroupId();

    /**
     * Set data group pk.
     *
     * @param datagroupId data group pk.
     */
    void setDatagroupId(long datagroupId);

    /**
     * Get Data group name.
     *
     * @return Data group name
     */
    String getName();

    /**
     * Set Data group name.
     *
     * @param name Data group name
     */
    void setName(String name);

    /**
     * Get name.
     *
     * @return localisable name of carrier Data group.
     */
    String getDisplayName();

    /**
     * Set name of carrier Data group.
     *
     * @param name localisable name.
     */
    void setDisplayName(String name);


    /**
     * Group qualifier.
     *
     * @return qualifier
     */
    String getQualifier();

    /**
     * Group qualifier.
     *
     * @param qualifier qualifier
     */
    void setQualifier(String qualifier);

    /**
     * Group type.
     *
     * @return type
     */
    String getType();

    /**
     * Group type.
     *
     * @param type type
     */
    void setType(String type);

    /**
     * Group descriptors.
     *
     * @return descriptors
     */
    String getDescriptors();

    /**
     * Group descriptors.
     *
     * @param descriptors descriptors
     */
    void setDescriptors(String descriptors);


}
