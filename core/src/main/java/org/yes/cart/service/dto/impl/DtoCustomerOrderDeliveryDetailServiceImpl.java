package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CustomerOrderDeliveryDetailDTOImpl;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoCustomerOrderDeliveryDetailService;

/**
 * User: igora Igor Azarny
 * Date: 12/12/12
 * Time: 9:21 AM
 */
public class DtoCustomerOrderDeliveryDetailServiceImpl extends AbstractDtoServiceImpl<CustomerOrderDeliveryDetailDTO, CustomerOrderDeliveryDetailDTOImpl, CustomerOrderDeliveryDet>
        implements DtoCustomerOrderDeliveryDetailService {

    /**
     * Construct base remote service to work with delivery details.
     *
     * @param dtoFactory         {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param customerOrderDeliveryDetGenericService            {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoCustomerOrderDeliveryDetailServiceImpl(final DtoFactory dtoFactory,
                                                     final GenericService<CustomerOrderDeliveryDet> customerOrderDeliveryDetGenericService,
                                                     final AdaptersRepository adaptersRepository) {
        super(dtoFactory, customerOrderDeliveryDetGenericService, adaptersRepository);
    }

    /** {@inheritDoc */
    public Class<CustomerOrderDeliveryDetailDTO> getDtoIFace() {
        return CustomerOrderDeliveryDetailDTO.class;
    }

    /** {@inheritDoc */
    public Class<CustomerOrderDeliveryDetailDTOImpl> getDtoImpl() {
        return CustomerOrderDeliveryDetailDTOImpl.class;
    }

    /** {@inheritDoc */
    public Class<CustomerOrderDeliveryDet> getEntityIFace() {
        return CustomerOrderDeliveryDet.class;
    }
}
