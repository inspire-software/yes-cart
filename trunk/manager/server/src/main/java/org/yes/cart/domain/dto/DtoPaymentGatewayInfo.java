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

/**
 *
 * Just transfet info about gateways.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/27/12
 * Time: 7:52 AM
 */
public interface DtoPaymentGatewayInfo {


    /**
     * Get name.
     * @return  name
     */
    String getName();

    /**
     * Set name
     * @param name name to set
     */
    void setName(String name);

    /**
     * Payment gateway label.
     * @return pg label
     */
    String getLabel();

    /**
     * Set pg label.
     * @param label label
     */
    void setLabel(String label);

    /**
     * Is active or not.
     * @return active or not .
     */
    boolean isActive();

    /**
     * Set active flag.
     * @param active active flag
     */
    void setActive(boolean active);

}
