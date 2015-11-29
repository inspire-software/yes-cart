package org.yes.cart.bulkimport.service.impl;

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
     * @throws AccessDeniedException
     */
    protected void validateAccessBeforeExport(final Object object, final Class objectType) throws AccessDeniedException {
        if (!federationFacade.isManageable(object, objectType)) {
            throw new AccessDeniedException("access denied");
        }
    }

}
