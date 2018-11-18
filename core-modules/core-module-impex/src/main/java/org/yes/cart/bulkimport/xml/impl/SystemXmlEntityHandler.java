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

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.CustomAttributeType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.SystemType;
import org.yes.cart.domain.entity.System;
import org.yes.cart.service.domain.SystemService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class SystemXmlEntityHandler extends AbstractXmlEntityHandler<SystemType, System> implements XmlEntityImportHandler<SystemType> {

    private SystemService systemService;

    public SystemXmlEntityHandler() {
        super("system");
    }

    @Override
    protected void delete(final System system) {
        throw new UnsupportedOperationException("Deleting the system entry in not supported");
    }

    @Override
    protected void saveOrUpdate(final System domain, final SystemType xmlType, final EntityImportModeType mode) {

        if (xmlType.getCustomAttributes() != null) {

            for (final CustomAttributeType customAttribute : xmlType.getCustomAttributes().getCustomAttribute()) {

                this.systemService.updateAttributeValue(customAttribute.getAttribute(), customAttribute.getCustomValue());

            }

        }

    }

    @Override
    protected System getOrCreate(final SystemType xmlType) {
        return null;
    }

    @Override
    protected EntityImportModeType determineImportMode(final SystemType xmlType) {
        return EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final System domain) {
        return false;
    }

    /**
     * Spring IoC.
     *
     * @param systemService system service
     */
    public void setSystemService(final SystemService systemService) {
        this.systemService = systemService;
    }
}
