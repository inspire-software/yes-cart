package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.ProductAssociationDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteProductAssociationService;
import org.yes.cart.service.dto.DtoProductAssociationService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteProductAssociationServiceImpl
        extends AbstractRemoteService<ProductAssociationDTO>
        implements RemoteProductAssociationService {

    private final DtoProductAssociationService dtoProductAssociationService;

    /**
     * Construct service.
     *
     * @param productAssociationDTOGenericDTOService
     *         dto service to use.
     */
    public RemoteProductAssociationServiceImpl(final GenericDTOService<ProductAssociationDTO> productAssociationDTOGenericDTOService) {
        super(productAssociationDTOGenericDTOService);
        this.dtoProductAssociationService = (DtoProductAssociationService) productAssociationDTOGenericDTOService;
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductAssociationDTO> getProductAssociations(final long productId)

            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoProductAssociationService.getProductAssociations(productId);
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductAssociationDTO> getProductAssociationsByProductAssociationType(
            final long productId,
            final String accosiationCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoProductAssociationService.getProductAssociationsByProductAssociationType(productId, accosiationCode);
    }
}
