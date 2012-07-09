package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteCustomerService;
import org.yes.cart.service.dto.DtoCustomerService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteCustomerServiceImpl
        extends AbstractRemoteService<CustomerDTO>
        implements RemoteCustomerService {

    private final DtoCustomerService dtoCustomerService;

    /**
     * Construct service to manage the users.
     *
     * @param customerDTOGenericService dto service to use
     */
    public RemoteCustomerServiceImpl(final GenericDTOService<CustomerDTO> customerDTOGenericService) {
        super(customerDTOGenericService);
        dtoCustomerService = (DtoCustomerService) customerDTOGenericService;
    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerDTO> findCustomer(final String email,
                                          final String firstname,
                                          final String lastname,
                                          final String middlename)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoCustomerService.findCustomer(email, firstname, lastname, middlename);
    }

    /**
     * {@inheritDoc}
     */
    public void remoteResetPassword(final CustomerDTO customer, final long shopId) {
        dtoCustomerService.remoteResetPassword(customer, shopId);
    }


    /**
     * {@inheritDoc}
     */
    public void deleteAttributeValue(final long attributeValuePk) {
        dtoCustomerService.deleteAttributeValue(attributeValuePk);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        return dtoCustomerService.createEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        return dtoCustomerService.updateEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoCustomerService.getEntityAttributes(entityPk);
    }
}
