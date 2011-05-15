package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.AssociationDTO;
import org.yes.cart.remote.service.RemoteAssociationService;
import org.yes.cart.service.dto.GenericDTOService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteAssociationServiceImpl
        extends AbstractRemoteService<AssociationDTO>
        implements RemoteAssociationService {

    /**
     * COnstruct remote service.
     *
     * @param associationDTOGenericDTOService
     *         dto service to use.
     */
    public RemoteAssociationServiceImpl(final GenericDTOService<AssociationDTO> associationDTOGenericDTOService) {
        super(associationDTOGenericDTOService);
    }

}
