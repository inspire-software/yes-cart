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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * Group of attributes.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttributeGroupDTO extends Identifiable {

    /**
     * Get pk value.
     *
     * @return pk value.
     */
    long getAttributegroupId();

    /**
     * Set pk value.
     *
     * @param attributegroupId pk value
     */
    void setAttributegroupId(long attributegroupId);

    /**
     * Get attribute group code.
     *
     * @return attribute group code.
     */
    String getCode();

    /**
     * Set code value
     *
     * @param code code value.
     */
    void setCode(String code);

    /**
     * Get attribute group name.
     *
     * @return
     */
    String getName();

    /**
     * Set name.
     *
     * @param name name value.
     */
    void setName(String name);

    /**
     * Get attribute group description.
     *
     * @return
     */
    String getDescription();

    /**
     * Set description.
     *
     * @param description description value.
     */
    void setDescription(String description);

}
