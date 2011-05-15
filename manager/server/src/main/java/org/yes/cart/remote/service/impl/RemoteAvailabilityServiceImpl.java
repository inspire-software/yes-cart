package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.AvailabilityDTO;
import org.yes.cart.remote.service.RemoteAvailabilityService;
import org.yes.cart.service.dto.DtoAvailabilityService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteAvailabilityServiceImpl
        extends AbstractRemoteService<AvailabilityDTO>
        implements RemoteAvailabilityService {


    /**
     * Construct remote service.
     *
     * @param dtoAvailabilityService dto service to use.
     */
    public RemoteAvailabilityServiceImpl(final DtoAvailabilityService dtoAvailabilityService) {
        super(dtoAvailabilityService);
    }


}
