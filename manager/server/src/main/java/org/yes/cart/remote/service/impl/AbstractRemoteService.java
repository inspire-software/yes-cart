package org.yes.cart.remote.service.impl;


import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class AbstractRemoteService<TDTOIFACE> implements GenericDTOService<TDTOIFACE> {


    private final GenericDTOService<TDTOIFACE> genericDTOService;

    public AbstractRemoteService(final GenericDTOService<TDTOIFACE> genericDTOService) {
        this.genericDTOService = genericDTOService;
    }

    /**
     * Get generic dto service.
     *
     * @return {@link GenericDTOService}
     */
    public GenericDTOService<TDTOIFACE> getGenericDTOService() {
        return genericDTOService;
    }

    /**
     * {@inheritDoc}
     */
    public GenericService getService() {
        return genericDTOService.getService();
    }


    /**
     * {@inheritDoc}
     */
    public List<TDTOIFACE> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return genericDTOService.getAll();
    }

    /**
     * {@inheritDoc}
     */
    public TDTOIFACE getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return genericDTOService.getById(id);
    }

    /**
     * {@inheritDoc}
     */
    public TDTOIFACE getById(final long id, final Map converters) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return genericDTOService.getById(id, converters);
    }


    /**
     * {@inheritDoc}
     */
    public TDTOIFACE getNew() throws UnableToCreateInstanceException, UnmappedInterfaceException {
        return genericDTOService.getNew();
    }

    /**
     * {@inheritDoc}
     */
    public TDTOIFACE create(final TDTOIFACE instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return genericDTOService.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    public TDTOIFACE update(final TDTOIFACE instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return genericDTOService.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final long id) {
        genericDTOService.remove(id);
    }


}
