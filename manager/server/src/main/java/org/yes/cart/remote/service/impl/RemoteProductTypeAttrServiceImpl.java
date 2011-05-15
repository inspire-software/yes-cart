package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.ProductTypeAttrDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteProductTypeAttrService;
import org.yes.cart.service.dto.DtoProductTypeAttrService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteProductTypeAttrServiceImpl extends AbstractRemoteService<ProductTypeAttrDTO>
        implements RemoteProductTypeAttrService {


    /**
     * Construct remote service.
     *
     * @param dtoProductTypeAttrService dto service to use.
     */
    public RemoteProductTypeAttrServiceImpl(final DtoProductTypeAttrService dtoProductTypeAttrService) {
        super(dtoProductTypeAttrService);
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductTypeAttrDTO> getByProductTypeId(final long productTypeId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoProductTypeAttrService) getGenericDTOService()).getByProductTypeId(productTypeId);
    }


}
