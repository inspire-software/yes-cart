package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.BrandDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteBrandService;
import org.yes.cart.service.dto.DtoBrandService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteBrandServiceImpl
        extends AbstractRemoteService<BrandDTO>
        implements RemoteBrandService {

    private final DtoBrandService dtoBrandService;


    /**
     * Construct remote service.
     *
     * @param dtoBrandService dto service to use.
     */
    public RemoteBrandServiceImpl(final DtoBrandService dtoBrandService) {
        super(dtoBrandService);
        this.dtoBrandService = dtoBrandService;
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoBrandService.getEntityAttributes(entityPk);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        return dtoBrandService.updateEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        return dtoBrandService.createEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteAttributeValue(final long attributeValuePk) {
        dtoBrandService.deleteAttributeValue(attributeValuePk);
    }
}
