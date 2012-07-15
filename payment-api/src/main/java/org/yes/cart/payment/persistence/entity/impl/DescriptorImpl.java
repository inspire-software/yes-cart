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

package org.yes.cart.payment.persistence.entity.impl;

import org.yes.cart.payment.persistence.entity.Descriptor;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:33:53
 */
public class DescriptorImpl implements Descriptor {

    private static final long serialVersionUID = 20100714L;

    protected String name;
    protected String description;
    protected String label;


    /**
     * Name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Name
     *
     * @param name name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Description.
     *
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Description
     *
     * @param description Description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Label.
     *
     * @return label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Label.
     *
     * @param label label
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * Construct descriptor.
     *
     * @param name        name
     * @param description description
     * @param label       label
     */
    public DescriptorImpl(final String name, final String description, final String label) {
        this.name = name;
        this.description = description;
        this.label = label;
    }

    /**
     * Default constructor.
     */
    public DescriptorImpl() {
        this.name = null;
        this.description = null;
        this.label = null;

    }
}
