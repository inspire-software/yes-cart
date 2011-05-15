package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.EtypeDTO;
import org.yes.cart.remote.service.RemoteEtypeService;
import org.yes.cart.service.dto.DtoEtypeService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteEtypeServiceImpl
        extends AbstractRemoteService<EtypeDTO>
        implements RemoteEtypeService {

    /**
     * Construct service.
     *
     * @param dtoEtypeService dto service
     */
    public RemoteEtypeServiceImpl(final DtoEtypeService dtoEtypeService) {
        super(dtoEtypeService);
    }


}
