package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.CarrierDTO;
import org.yes.cart.remote.service.RemoteCarrierService;
import org.yes.cart.service.dto.GenericDTOService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteCarrierServiceImpl
        extends AbstractRemoteService<CarrierDTO>
        implements RemoteCarrierService {

    /**
     * Construct remote service.
     *
     * @param carrierDTOGenericDTOService carrier sla dto service to use
     */
    public RemoteCarrierServiceImpl(final GenericDTOService<CarrierDTO> carrierDTOGenericDTOService) {
        super(carrierDTOGenericDTOService);
    }
}
