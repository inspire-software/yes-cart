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

package org.yes.cart.bulkimport.image.impl;

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.bulkimport.image.ImageImportDomainObjectStrategy;
import org.yes.cart.service.federation.FederationFacade;

/**
 * User: denispavlov
 * Date: 14/10/2014
 * Time: 12:17
 */
public abstract class AbstractImageImportDomainObjectStrategyImpl implements ImageImportDomainObjectStrategy {

    private final FederationFacade federationFacade;

    protected AbstractImageImportDomainObjectStrategyImpl(final FederationFacade federationFacade) {
        this.federationFacade = federationFacade;
    }

    /**
     * Verify access of the current user.
     *
     * @param object target object to update
     *
     * @throws org.springframework.security.access.AccessDeniedException
     */
    protected void validateAccessBeforeUpdate(final Object object, final Class objectType) throws AccessDeniedException {
        if (!federationFacade.isManageable(object, objectType)) {
            throw new AccessDeniedException("access denied");
        }
    }

}
