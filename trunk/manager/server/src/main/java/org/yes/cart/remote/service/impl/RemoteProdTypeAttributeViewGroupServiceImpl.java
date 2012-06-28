package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.ProdTypeAttributeViewGroupDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteProdTypeAttributeViewGroupService;
import org.yes.cart.service.dto.DtoProdTypeAttributeViewGroupService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/28/12
 * Time: 10:59 PM
 */
public class RemoteProdTypeAttributeViewGroupServiceImpl
        extends AbstractRemoteService<ProdTypeAttributeViewGroupDTO>
        implements RemoteProdTypeAttributeViewGroupService {


    /**
     * Construct remote service.
     * @param prodTypeAttributeViewGroupDTOGenericDTOService   dto service to use.
     */
    public RemoteProdTypeAttributeViewGroupServiceImpl(final GenericDTOService<ProdTypeAttributeViewGroupDTO> prodTypeAttributeViewGroupDTOGenericDTOService) {
        super(prodTypeAttributeViewGroupDTOGenericDTOService);
    }


    /** {@inheritDoc} */
    public List<ProdTypeAttributeViewGroupDTO> getByProductTypeId(long productTypeId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoProdTypeAttributeViewGroupService)getGenericDTOService()).getByProductTypeId(productTypeId);
    }
}
