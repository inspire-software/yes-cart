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

package org.yes.cart.bulkexport.service.impl;

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.bulkcommon.service.ExportService;
import org.yes.cart.service.federation.FederationFacade;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 14:09
 */
public abstract class AbstractExportService implements ExportService {

    protected final FederationFacade federationFacade;

    public AbstractExportService(final FederationFacade federationFacade) {
        this.federationFacade = federationFacade;
    }


    /**
     * Verify access of the current user.
     *
     * @param object target object to update
     *
     * @throws AccessDeniedException errors
     */
    protected void validateAccessBeforeExport(final Object object, final Class objectType) throws AccessDeniedException {
        if (!federationFacade.isManageable(object, objectType)) {
            throw new AccessDeniedException("access denied");
        }
    }

}
