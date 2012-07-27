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
package org.yes.cart.domain.dto.impl;

import org.yes.cart.domain.dto.DtoPaymentGatewayInfo;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/27/12
 * Time: 7:52 AM
 */
public class DtoPaymentGatewayInfoImpl implements DtoPaymentGatewayInfo {

    private String name;
    private String label;
    private boolean active;

    /**
     * Get name.
     * @return  name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name
     * @param name name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Payment gateway label.
     * @return pg label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set pg label.
     * @param label label
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * Is active or not.
     * @return active or not .
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set active flag.
     * @param active active flag
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Construct dto object.
     * @param name name
     * @param label label
     * @param active active flag
     */
    public DtoPaymentGatewayInfoImpl(String label, String name, boolean active) {
        this.name = name;
        this.label = label;
        this.active = active;
    }
}
