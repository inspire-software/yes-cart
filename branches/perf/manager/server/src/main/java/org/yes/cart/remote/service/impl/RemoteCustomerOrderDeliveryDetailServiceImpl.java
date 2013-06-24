package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.remote.service.RemoteCustomerOrderDeliveryDetailService;
import org.yes.cart.service.dto.GenericDTOService;

/**
 * User: igora Igor Azarny
 * Date: 12/12/12
 * Time: 9:39 AM
 */
public class RemoteCustomerOrderDeliveryDetailServiceImpl
        extends AbstractRemoteService<CustomerOrderDeliveryDetailDTO>
        implements RemoteCustomerOrderDeliveryDetailService {

    /**
     * Construct remote service to edit delivery details.
     * @param customerOrderDeliveryDetailDTOGenericDTOService dto service to use.
     */
    public RemoteCustomerOrderDeliveryDetailServiceImpl(final GenericDTOService<CustomerOrderDeliveryDetailDTO> customerOrderDeliveryDetailDTOGenericDTOService) {
        super(customerOrderDeliveryDetailDTOGenericDTOService);
    }


}
