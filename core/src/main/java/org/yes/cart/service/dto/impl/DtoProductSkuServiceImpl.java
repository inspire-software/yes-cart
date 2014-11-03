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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductSkuDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoProductService;
import org.yes.cart.service.dto.DtoProductSkuService;
import org.yes.cart.utils.impl.AttrValueDTOComparatorImpl;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductSkuServiceImpl
        extends AbstractDtoServiceImpl<ProductSkuDTO, ProductSkuDTOImpl, ProductSku>
        implements DtoProductSkuService {


    private DtoProductService dtoProductService;
    private final DtoAttributeService dtoAttributeService;
    private final GenericDAO<AttrValueProductSku, Long> attrValueEntityProductSkuDao;
    private final Assembler attrValueAssembler;
    private final Assembler skuPriceAssembler;
    private final GenericService<Attribute> attributeService;
    private final PriceService priceService;
    private final ImageService imageService;
    private final SystemService systemService;

    /**
     * Construct product sku dto service.
     *
     * @param dtoFactory                   dto factory
     * @param productSkuGenericService     generic product service
     * @param dtoAttributeService          attr service to determinate allowed duplicates for attribute values.
     * @param attrValueEntityProductSkuDao sku attributes dao
     * @param adaptersRepository           value converter
     * @param imageService                 {@link org.yes.cart.service.domain.ImageService} to manipulate  related images.
     * @param systemService                system service
     */
    public DtoProductSkuServiceImpl(
            final DtoFactory dtoFactory,
            final ProductSkuService productSkuGenericService,
            final DtoAttributeService dtoAttributeService,
            final GenericDAO<AttrValueProductSku, Long> attrValueEntityProductSkuDao,
            final AdaptersRepository adaptersRepository,
            final PriceService priceService,
            final ImageService imageService,
            final SystemService systemService) {
        super(dtoFactory, productSkuGenericService, adaptersRepository);

        this.imageService = imageService;

        this.dtoAttributeService = dtoAttributeService;
        this.attrValueEntityProductSkuDao = attrValueEntityProductSkuDao;
        this.systemService = systemService;
        this.attributeService = dtoAttributeService.getService();
        this.priceService = priceService;
        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueProductSkuDTO.class),
                productSkuGenericService.getGenericDao().getEntityFactory().getImplClass(AttrValueProductSku.class)
        );
        this.skuPriceAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(SkuPriceDTO.class),
                productSkuGenericService.getGenericDao().getEntityFactory().getImplClass(SkuPrice.class)
        );
    }

    public DtoProductService getDtoProductService() {
        if (dtoProductService == null) {
            dtoProductService = lookupDtoProductService();
        }
        return dtoProductService;
    }


    /**
     * {@inheritDoc}
     */
    public List<ProductSkuDTO> getAllProductSkus(final long productId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Collection<ProductSku> skus = ((ProductSkuService) getService()).getAllProductSkus(productId);
        final List<ProductSkuDTO> result = new ArrayList<ProductSkuDTO>(skus.size());
        fillDTOs(skus, result);
        return result;
    }

    /**
     * @return Spring lookup method to prevent cyclic reference
     */
    public DtoProductService lookupDtoProductService() {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public long updateSkuPrice(final SkuPriceDTO skuPriceDTO) {
        final SkuPrice skuPrice = priceService.findById(skuPriceDTO.getSkuPriceId());
        skuPriceAssembler.assembleEntity(skuPriceDTO, skuPrice,
                getAdaptersRepository(),
                getAssemblerEntityFactory());
        priceService.update(skuPrice);
        return skuPrice.getSkuPriceId();
    }

    /**
     * {@inheritDoc}
     */
    public void remove(long id) {
        ProductSku sku =  getService().findById(id);
        ((ProductSkuService)getService()).removeAllInventory(sku);
        ((ProductSkuService)getService()).removeAllPrices(sku);
        getService().getGenericDao().evict(sku);

        sku =  getService().findById(id);
        final Product prod = sku.getProduct();
        prod.getSku().remove(sku);
        sku.setProduct(null);
        getService().getGenericDao().evict(prod);
        getService().getGenericDao().delete(sku);
    }

    /**
     * {@inheritDoc}
     */
    public SkuPriceDTO getSkuPrice(final long skuPriceId) {
        SkuPrice skuPrice = priceService.findById(skuPriceId);
        SkuPriceDTO skuPriceDTO = getAssemblerDtoFactory().getByIface(SkuPriceDTO.class);
        skuPriceAssembler.assembleEntity(skuPriceDTO, skuPrice,
                getAdaptersRepository(),
                getAssemblerEntityFactory());
        return skuPriceDTO;
    }

    /**
     * {@inheritDoc}
     */
    public void removeSkuPrice(final long skuPriceId) {
        priceService.delete(
                priceService.findById(skuPriceId)
        );
    }

    /**
     * Remove all sku prices from all shops.
     *
     * @param productId product pk value
     */
    public void removeAllPrices(final long productId) {
        ((ProductSkuService) getService()).removeAllPrices(productId);

    }

    /**
     * Remove from all warehouses.
     *
     * @param productId product pk value
     */
    public void removeAllInventory(final long productId) {
        ((ProductSkuService) getService()).removeAllInventory(productId);

    }


    /**
     * {@inheritDoc}
     */
    public long createSkuPrice(final SkuPriceDTO skuPriceDTO) {
        SkuPrice skuPrice = getService().getGenericDao().getEntityFactory().getByIface(SkuPrice.class);
        skuPriceAssembler.assembleEntity(skuPriceDTO, skuPrice,
                getAdaptersRepository(),
                getAssemblerEntityFactory());
        skuPrice = priceService.create(skuPrice);
        return skuPrice.getSkuPriceId();
    }


    /**
     * {@inheritDoc}
     */
    public Class<ProductSkuDTO> getDtoIFace() {
        return ProductSkuDTO.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<ProductSkuDTOImpl> getDtoImpl() {
        return ProductSkuDTOImpl.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<ProductSku> getEntityIFace() {
        return ProductSku.class;
    }


    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final ProductSkuDTO sku = getById(entityPk);
        final List<AttrValueProductSkuDTO> skuAttrs = new ArrayList<AttrValueProductSkuDTO>(sku.getAttributes());

        final ProductDTO product = getDtoProductService().getById(sku.getProductId());
        final List<AttrValueProductDTO> productAttrs = new ArrayList<AttrValueProductDTO>(product.getAttributes());

        final List<AttributeDTO> ptList = dtoAttributeService.findAvailableAttributesByProductTypeId(
                product.getProductTypeDTO().getProducttypeId()
        );

        final List<AttributeDTO> images = dtoAttributeService.findAvailableImageAttributesByGroupCode(
                AttributeGroupNames.PRODUCT
        );

        ptList.addAll(images);

        final List<AttributeDTO> mandatory = dtoAttributeService.findAvailableAttributesByGroupCodeStartsWith(
                AttributeGroupNames.PRODUCT, AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX
        );

        ptList.addAll(mandatory);

        // code to list as we may have multivalues
        final Map<String, List<AttrValueProductDTO>> existingProductAttrValues = new HashMap<String, List<AttrValueProductDTO>>();
        for (final AttrValueProductDTO value : productAttrs) {
            final String attrCode = value.getAttributeDTO().getCode();
            List<AttrValueProductDTO> codeValues = existingProductAttrValues.get(attrCode);
            if (codeValues == null) {
                codeValues = new ArrayList<AttrValueProductDTO>();
                existingProductAttrValues.put(attrCode, codeValues);
            }
            codeValues.add(value);
        }

        final Set<String> existingSkuAttrValueCodes = new HashSet<String>();
        for (final AttrValueProductSkuDTO value : skuAttrs) {
            existingSkuAttrValueCodes.add(value.getAttributeDTO().getCode());
        }

        final List<AttrValueProductSkuDTO> full = new ArrayList<AttrValueProductSkuDTO>(ptList.size());
        for (final AttributeDTO available : ptList) {
            if (!existingSkuAttrValueCodes.contains(available.getCode())) {

                final List<AttrValueProductDTO> productValues = existingProductAttrValues.get(available.getCode());
                if (productValues == null) {

                    // we do not have product nor sku values, so create blank
                    final AttrValueProductSkuDTO attrValueDTO = getAssemblerDtoFactory().getByIface(AttrValueProductSkuDTO.class);
                    attrValueDTO.setAttributeDTO(available);
                    attrValueDTO.setSkuId(entityPk);
                    full.add(attrValueDTO);

                } else {
                    for (final AttrValueProductDTO prodValue : productValues) {

                        // pre-fill sku value with product value so that we can easily see it
                        AttrValueProductSkuDTO attrValueDTO = getAssemblerDtoFactory().getByIface(AttrValueProductSkuDTO.class);
                        attrValueDTO.setAttributeDTO(available);
                        attrValueDTO.setSkuId(entityPk);
                        attrValueDTO.setVal(prodValue.getVal());
                        full.add(attrValueDTO);

                    }
                }
            }
        }

        full.addAll(skuAttrs); // add all the rest values that are specified for this sku

        CollectionUtils.filter(
                full,
                new Predicate() {
                    public boolean evaluate(final Object object) {
                        return ((AttrValueDTO) object).getAttributeDTO() != null;
                    }
                }
        );

        Collections.sort(full, new AttrValueDTOComparatorImpl());
        return full;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final AttrValueProductSku attrValue = attrValueEntityProductSkuDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, attrValue, getAdaptersRepository(), dtoFactory);
        attrValueEntityProductSkuDao.update(attrValue);
        return attrValueDTO;

    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {

        final Attribute atr = attributeService.findById(attrValueDTO.getAttributeDTO().getAttributeId());
        final boolean multivalue = atr.isAllowduplicate();
        final ProductSku productSku = service.findById(((AttrValueProductSkuDTO) attrValueDTO).getSkuId());
        if (!multivalue) {
            for (final AttrValueProductSku avp : productSku.getAttributes()) {
                if (avp.getAttribute().getCode().equals(atr.getCode())) {
                    // this is a duplicate, so need to update
                    attrValueDTO.setAttrvalueId(avp.getAttrvalueId());
                    return updateEntityAttributeValue(attrValueDTO);
                }
            }
        }

        AttrValueProductSku valueEntity = getPersistenceEntityFactory().getByIface(AttrValueProductSku.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntity, getAdaptersRepository(), dtoFactory);
        valueEntity.setAttribute(atr);
        valueEntity.setProductSku(productSku);
        valueEntity = attrValueEntityProductSkuDao.create(valueEntity);
        attrValueDTO.setAttrvalueId(valueEntity.getAttrvalueId());
        return attrValueDTO;

    }

    /**
     * {@inheritDoc}
     */
    public long deleteAttributeValue(final long attributeValuePk) {
        final AttrValueProductSku attrValue = attrValueEntityProductSkuDao.findById(attributeValuePk);
        if (Etype.IMAGE_BUSINESS_TYPE.equals(attrValue.getAttribute().getEtype().getBusinesstype())) {
            imageService.deleteImage(attrValue.getVal(),
                    Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN, systemService.getImageRepositoryDirectory());
        }
        attrValueEntityProductSkuDao.delete(attrValue);
        return attrValue.getProductSku().getSkuId();
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(final long entityPk, final String attrName, final String attrValue)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }
}
