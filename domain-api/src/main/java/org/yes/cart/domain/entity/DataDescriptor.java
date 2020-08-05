/*
 * Copyright 2009 Inspire-Software.com
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
 * Time: 12:05
 */
public interface DataDescriptor extends Auditable {

    /**
     * @deprecated use TYPE_WEBINF_XML_CSV instead
     */
    @Deprecated
    String TYPE_WEBINF_XML = "WEBINF_XML";

    /**
     * @deprecated use TYPE_RAWINF_XML_CSV instead
     */
    @Deprecated
    String TYPE_RAWINF_XML = "RAWINF_XML";

    // WebInf XML descriptor for CSV service
    String TYPE_WEBINF_XML_CSV = "WEBINF_XML/CSV";

    // Raw XML descriptor for CSV service
    String TYPE_RAW_XML_CSV = "RAW_XML/CSV";

    // WebInf XML descriptor for XML service
    String TYPE_WEBINF_XML_XML = "WEBINF_XML/XML";

    // Raw XML descriptor for XML service
    String TYPE_RAW_XML_XML = "RAW_XML/XML";


    /**
     * Get data descriptor pk.
     *
     * @return data descriptor pk.
     */
    long getDatadescriptorId();

    /**
     * Set data descriptor pk.
     *
     * @param datadescriptorId data descriptor pk.
     */
    void setDatadescriptorId(long datadescriptorId);

    /**
     * Get Data descriptor name.
     *
     * @return Data descriptor name
     */
    String getName();

    /**
     * Set Data descriptor name.
     *
     * @param name Data descriptor name
     */
    void setName(String name);

    /**
     * Descriptor type.
     *
     * @return type
     */
    String getType();

    /**
     * Descriptor type.
     *
     * @param type type
     */
    void setType(String type);

    /**
     * Get descriptor value.
     *
     * @return value
     */
    String getValue();

    /**
     * Set descriptor value.
     *
     * @param value value
     */
    void setValue(String value);
}
