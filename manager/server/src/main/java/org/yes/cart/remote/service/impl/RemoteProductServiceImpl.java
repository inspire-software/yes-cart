/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttrValueProductDTO;
import org.yes.cart.domain.dto.ProductDTO;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.exception.ObjectNotFoundException;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnableToWrapObjectException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.ReindexService;
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
    
    private final ReindexService reindexService;


    /**
     * Construct remote service.
     *
     * @param dtoProductService dto service.
     * @param reindexService product reindex service
     */
    public RemoteProductServiceImpl(
            final DtoProductService dtoProductService,
            final ReindexService reindexService) {
        super(dtoProductService);
        this.reindexService = reindexService;
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
    public List<ProductDTO> getProductByCodeNameBrandType(
            final String code,
            final String name,
            final long brandId,
            final long productTypeId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoProductService) getGenericDTOService()).getProductByCodeNameBrandType(code, name, brandId, productTypeId);
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
        AttrValueProductDTO rez = (AttrValueProductDTO) ((DtoProductService) getGenericDTOService()).updateEntityAttributeValue(attrValueDTO);
        reindexService.reindexProduct(rez.getProductId());
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        AttrValueProductDTO rez = (AttrValueProductDTO)  ((DtoProductService) getGenericDTOService()).createEntityAttributeValue(attrValueDTO);
        reindexService.reindexProduct(rez.getProductId());
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteAttributeValue(final long attributeValuePk) {
        ((DtoProductService) getGenericDTOService()).deleteAttributeValue(attributeValuePk);
    }

    /**
     * {@inheritDoc}
     */
    public ProductDTO create(final ProductDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductDTO rez =  super.create(instance);
        reindexService.reindexProduct(rez.getProductId());
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public ProductDTO update(final ProductDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductDTO rez =  super.update(instance);
        reindexService.reindexProduct(rez.getProductId());
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final long id) {
        super.remove(id);
        reindexService.reindexProduct(id);
    }
}
