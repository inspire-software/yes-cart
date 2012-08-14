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
 * Etype.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Etype extends Auditable {

    // The OOTB business types
    String IMAGE_BUSINESS_TYPE = "Image";

    /**
     * Get primary key.
     *
     * @return pk
     */
    long getEtypeId();

    /**
     * Set pk value
     *
     * @param etypeId pk value.
     */
    void setEtypeId(long etypeId);

    /**
     * Get the full java class name.
     *
     * @return class name.
     */
    String getJavatype();

    /**
     * Set java type.
     *
     * @param javatype java type
     */
    void setJavatype(String javatype);

    /**
     * High level business type
     *
     * @return business type
     */
    String getBusinesstype();

    /**
     * Set business type.
     *
     * @param businesstype business type.
     */
    void setBusinesstype(String businesstype);

    /**
     * Get attributes, that has this type
     *
     * @return attributes.
     */
   // Set<Attribute> getAttributes();

    /**
     * Set attributes.
     *
     * @param attributes attribute collection
     */
    //void setAttributes(Set<Attribute> attributes);

}


