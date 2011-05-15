package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.ProductDTO;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.exception.ObjectNotFoundException;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnableToWrapObjectException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteProductService;
import org.yes.cart.service.dto.DtoProductService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteProductServiceImpl
        extends AbstractRemoteService<ProductDTO>
        implements RemoteProductService {


    /**
     * Construct remote service.
     *
     * @param dtoProductService dto service.
     */
    public RemoteProductServiceImpl(final DtoProductService dtoProductService) {
        super(dtoProductService);
    }

    /**
     * {@inheritDoc}
     */
    public ProductSkuDTO getProductSkuByCode(final String skuCode)
            throws ObjectNotFoundException, UnableToWrapObjectException {
        return ((DtoProductService) getGenericDTOService()).getProductSkuByCode(skuCode);
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductDTO> getProductByCategory(final long categoryId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoProductService) getGenericDTOService()).getProductByCategory(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductDTO> getProductByCategoryWithPaging(final long categoryId, final int firtsResult, final int maxResults)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoProductService) getGenericDTOService()).getProductByCategoryWithPaging(categoryId, firtsResult, maxResults);
    }


    /**
     * {@inheritDoc}
     */
    public List<ProductDTO> getProductByConeNameBrandType(
            final String code,
            final String name,
            final long brandId,
            final long productTypeId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoProductService) getGenericDTOService()).getProductByConeNameBrandType(code, name, brandId, productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoProductService) getGenericDTOService()).getEntityAttributes(entityPk);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        return ((DtoProductService) getGenericDTOService()).updateEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        return ((DtoProductService) getGenericDTOService()).createEntityAttributeValue(attrValueDTO);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteAttributeValue(final long attributeValuePk) {
        ((DtoProductService) getGenericDTOService()).deleteAttributeValue(attributeValuePk);
    }
}
