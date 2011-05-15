package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.AttributeGroupDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteAttributeGroupService;
import org.yes.cart.service.dto.DtoAttributeGroupService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteAttributeGroupServiceImpl
        extends AbstractRemoteService<AttributeGroupDTO>
        implements RemoteAttributeGroupService {


    /**
     * Construct remote service.
     *
     * @param dtoAttributeGroupService dto service.
     */
    public RemoteAttributeGroupServiceImpl(final DtoAttributeGroupService dtoAttributeGroupService) {
        super(dtoAttributeGroupService);
    }

    /**
     * {@inheritDoc}
     */
    public AttributeGroupDTO getAttributeGroupByCode(final String code)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        return ((DtoAttributeGroupService) getGenericDTOService()).getAttributeGroupByCode(code);
    }

    /**
     * {@inheritDoc}
     */
    public AttributeGroupDTO create(final String code, final String name, final String description) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoAttributeGroupService) getGenericDTOService()).create(code, name, description);
    }

    /**
     * {@inheritDoc}
     */
    public AttributeGroupDTO update(final String code, final String name, final String description) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoAttributeGroupService) getGenericDTOService()).update(code, name, description);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final String code) {
        ((DtoAttributeGroupService) getGenericDTOService()).remove(code);
    }


}
