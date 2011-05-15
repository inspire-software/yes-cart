package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.ProductTypeDTO;
import org.yes.cart.remote.service.RemoteProductTypeService;
import org.yes.cart.service.dto.DtoProductTypeService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteProductTypeServiceImpl
        extends AbstractRemoteService<ProductTypeDTO>
        implements RemoteProductTypeService {


    /**
     * Construct remote service.
     *
     * @param dtoProductTypeService dto service to use.
     */
    public RemoteProductTypeServiceImpl(final DtoProductTypeService dtoProductTypeService) {
        super(dtoProductTypeService);
    }


}
