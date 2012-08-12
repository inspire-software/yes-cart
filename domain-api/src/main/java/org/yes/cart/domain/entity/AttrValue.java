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
 * Attr value object.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */

public interface AttrValue extends Auditable {

    /**
     * Get primary key.
     *
     * @return pk value
     */
    long getAttrvalueId();

    /**
     * Set pk value.
     *
     * @param attrvalueId pk value.
     */
    void setAttrvalueId(long attrvalueId);

    /**
     * Get the string representation of attribute value.
     *
     * @return attribute value.
     */
    String getVal();

    /**
     * Set attribute value.
     *
     * @param val value
     */
    void setVal(String val);

    /**
     * Get display value to show on the web. The some
     * correct attributes value does not look good on the web,
     * so need to use display value to eliminate this situation.
     * Need to use val in case if displayVal is empty.
     * Affect UI only.
     *
     * @return display value.
     */
    String getDisplayVal();

    /**
     * Set display value.
     * @param displayVal display value.
     */
    void setDisplayVal(String displayVal);

    /**
     * Get the attribute.
     *
     * @return {@link Attribute}
     */
    Attribute getAttribute();

    /**
     * Set attribute.
     *
     * @param attribute attribute.
     */
    void setAttribute(Attribute attribute);


}


