/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttrValueProductSkuDTOImpl;
import org.yes.cart.domain.dto.impl.ProductSkuDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoProductService;
import org.yes.cart.service.dto.DtoProductSkuService;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.service.dto.AttrValueDTOComparator;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductSkuServiceImpl
        extends AbstractDtoServiceImpl<ProductSkuDTO, ProductSkuDTOImpl, ProductSku>
        implements DtoProductSkuService {

    private static final AttrValueDTOComparator ATTR_VALUE_DTO_COMPARATOR = new AttrValueDTOComparator();

    private DtoProductService dtoProductService;
    private final DtoAttributeService dtoAttributeService;
    private final GenericDAO<AttrValueProductSku, Long> attrValueEntityProductSkuDao;
    private final Assembler attrValueAssembler;
    private final Assembler skuPriceAssembler;
    private final GenericService<Attribute> attributeService;
    private final PriceService priceService;
    private final ImageService imageService;
    private final FileService fileService;
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
     * @param fileService {@link FileService} to manipulate related files
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
            final FileService fileService,
            final SystemService systemService) {
        super(dtoFactory, productSkuGenericService, adaptersRepository);

        this.imageService = imageService;
        this.fileService = fileService;

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


    /**
     * @return Spring lookup method to prevent cyclic reference
     */
    public DtoProductService lookupDtoProductService() {
        return null;
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
    @Override
    public List<ProductSkuDTO> getAllProductSkus(final long productId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Collection<ProductSku> skus = ((ProductSkuService) getService()).getAllProductSkus(productId);
        final List<ProductSkuDTO> result = new ArrayList<>(skus.size());
        fillDTOs(skus, result);
        return result;
    }

    private final static char[] CODE = new char[] { '!', '*' };
    static {
        Arrays.sort(CODE);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductSkuDTO> findBy(final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<ProductSkuDTO> dtos = new ArrayList<>();

        List<ProductSku> entities = Collections.emptyList();

        if (StringUtils.isNotBlank(filter)) {

            final Pair<String, String> code = ComplexSearchUtils.checkSpecialSearch(filter, CODE);

            if (code != null) {

                if ("*".equals(code.getFirst())) {

                    // If this by PK then to by PK
                    final long byPk = NumberUtils.toLong(code.getSecond());
                    if (page == 0 && byPk > 0) {
                        final ProductSkuDTO product = getById(byPk);
                        if (product != null) {
                            dtos.add(product);
                        }
                    }
                    return dtos;

                } else if ("!".equals(code.getFirst())) {

                    entities = getService().getGenericDao().findRangeByCriteria(
                            " where lower(e.guid) like ?1 or lower(e.code) like ?1 or lower(e.manufacturerCode) like ?1 or lower(e.barCode) like ?1 order by e.code",
                            page * pageSize, pageSize,
                            HQLUtils.criteriaIeq(code.getSecond())
                    );

                }

            } else {

                entities = getService().getGenericDao().findRangeByCriteria(
                        " where lower(e.code) like ?1 or lower(e.manufacturerCode) like ?1 or lower(e.name) like ?1 order by e.code",
                        page * pageSize, pageSize,
                        HQLUtils.criteriaIlikeAnywhere(filter)
                );

            }

        }

        fillDTOs(entities, dtos);

        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SkuPriceDTO> getAllProductPrices(final long productId, final String currency, final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<SkuPrice> prices = priceService.getAllPrices(productId, null, currency);
        final List<SkuPriceDTO> result = new ArrayList<>();
        for (final SkuPrice price : prices) {
            if (price.getShop().getShopId() == shopId) {
                final SkuPriceDTO dto = dtoFactory.getByIface(SkuPriceDTO.class);
                skuPriceAssembler.assembleDto(dto, price,
                        getAdaptersRepository(),
                        getAssemblerEntityFactory());
                result.add(dto);
            }
        }
        return result;
    }


    private void ensureNonZeroPrices(final SkuPrice entity) {
        if (!MoneyUtils.isPositive(entity.getSalePrice())) {
            entity.setSalePrice(null);
        }
        if (!MoneyUtils.isPositive(entity.getMinimalPrice())) {
            entity.setMinimalPrice(null);
        }
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public long createSkuPrice(final SkuPriceDTO skuPriceDTO) {
        SkuPrice skuPrice = getService().getGenericDao().getEntityFactory().getByIface(SkuPrice.class);
        skuPriceAssembler.assembleEntity(skuPriceDTO, skuPrice,
                getAdaptersRepository(),
                getAssemblerEntityFactory());

        ensureNonZeroPrices(skuPrice);

        skuPrice.setSkuCode(skuPriceDTO.getSkuCode());
        skuPrice = priceService.create(skuPrice);
        return skuPrice.getSkuPriceId();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public long updateSkuPrice(final SkuPriceDTO skuPriceDTO) {
        final SkuPrice skuPrice = priceService.findById(skuPriceDTO.getSkuPriceId());
        skuPriceAssembler.assembleEntity(skuPriceDTO, skuPrice,
                getAdaptersRepository(),
                getAssemblerEntityFactory());

        ensureNonZeroPrices(skuPrice);

        priceService.update(skuPrice);
        return skuPrice.getSkuPriceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createPostProcess(final ProductSkuDTO dto, final ProductSku entity) {
        ensureBlankUriIsNull(entity);
        super.createPostProcess(dto, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updatePostProcess(final ProductSkuDTO dto, final ProductSku entity) {
        ensureBlankUriIsNull(entity);
        super.updatePostProcess(dto, entity);
    }

    private void ensureBlankUriIsNull(final Seoable entity) {
        if (entity.getSeo() != null && entity.getSeo().getUri() != null && StringUtils.isBlank(entity.getSeo().getUri())) {
            entity.getSeo().setUri(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(long id) {

        ProductSku sku =  getService().findById(id);

        if (sku == null) {
            return;
        }

        ((ProductSkuService)getService()).removeAllInventory(sku);
        ((ProductSkuService)getService()).removeAllPrices(sku);
        ((ProductSkuService)getService()).removeAllWishLists(sku);
        ((ProductSkuService)getService()).removeAllEnsembleOptions(sku);

        final List<Long> avIds = new ArrayList<>();
        for (final AttrValueProductSku av : sku.getAttributes()) {
            avIds.add(av.getAttrvalueId());
        }
        getService().getGenericDao().clear(); // clear session

        for (final Long avId : avIds) {
            try {
                deleteAttributeValue(avId);
            } catch (Exception exp) {
                // OK
            }
        }
        getService().getGenericDao().flushClear(); // ensure we flush delete and clear session

        sku = getService().findById(id); // get sku again (should be without attributes)
        final Product prod = sku.getProduct();
        prod.getSku().remove(sku);
        sku.setProduct(null);
        getService().delete(sku);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SkuPriceDTO getSkuPrice(final long skuPriceId) {
        SkuPrice skuPrice = priceService.findById(skuPriceId);
        SkuPriceDTO skuPriceDTO = getAssemblerDtoFactory().getByIface(SkuPriceDTO.class);
        skuPriceAssembler.assembleDto(skuPriceDTO, skuPrice,
                getAdaptersRepository(),
                getAssemblerEntityFactory());
        return skuPriceDTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public void removeAllPrices(final long productId) {
        ((ProductSkuService) getService()).removeAllPrices(productId);

    }

    /**
     * Remove from all warehouses.
     *
     * @param productId product pk value
     */
    @Override
    public void removeAllInventory(final long productId) {
        ((ProductSkuService) getService()).removeAllInventory(productId);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Class<ProductSkuDTO> getDtoIFace() {
        return ProductSkuDTO.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<ProductSkuDTOImpl> getDtoImpl() {
        return ProductSkuDTOImpl.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<ProductSku> getEntityIFace() {
        return ProductSku.class;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final ProductSkuDTO sku = getById(entityPk);
        final List<AttrValueProductSkuDTO> skuAttrs = new ArrayList<>(sku.getAttributes());

        final ProductDTO product = getDtoProductService().getById(sku.getProductId());
        final List<AttrValueProductDTO> productAttrs = new ArrayList<>(product.getAttributes());

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
        final Map<String, List<AttrValueProductDTO>> existingProductAttrValues = new HashMap<>();
        for (final AttrValueProductDTO value : productAttrs) {
            final String attrCode = value.getAttributeDTO().getCode();
            final List<AttrValueProductDTO> codeValues = existingProductAttrValues.computeIfAbsent(attrCode, k -> new ArrayList<>());
            codeValues.add(value);
        }

        final Set<String> existingSkuAttrValueCodes = new HashSet<>();
        for (final AttrValueProductSkuDTO value : skuAttrs) {
            existingSkuAttrValueCodes.add(value.getAttributeDTO().getCode());
        }

        final List<AttrValueProductSkuDTO> full = new ArrayList<>(ptList.size());
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
                        attrValueDTO.setVal("* " + prodValue.getVal());
                        full.add(attrValueDTO);

                    }
                }
            }
        }

        full.addAll(skuAttrs); // add all the rest values that are specified for this sku

        CollectionUtils.filter(
                full,
                object -> ((AttrValueDTO) object).getAttributeDTO() != null
        );

        Collections.sort(full, ATTR_VALUE_DTO_COMPARATOR);
        return full;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final AttrValueProductSku attrValue = attrValueEntityProductSkuDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, attrValue, getAdaptersRepository(), dtoFactory);
        attrValueEntityProductSkuDao.update(attrValue);
        return attrValueDTO;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {

        final Attribute atr = attributeService.findById(attrValueDTO.getAttributeDTO().getAttributeId());
        final boolean multivalue = atr.isAllowduplicate();
        final ProductSku productSku = service.findById(((AttrValueProductSkuDTO) attrValueDTO).getSkuId());
        if (!multivalue) {
            for (final AttrValueProductSku avp : productSku.getAttributes()) {
                if (avp.getAttributeCode().equals(atr.getCode())) {
                    // this is a duplicate, so need to update
                    attrValueDTO.setAttrvalueId(avp.getAttrvalueId());
                    return updateEntityAttributeValue(attrValueDTO);
                }
            }
        }

        AttrValueProductSku valueEntity = getPersistenceEntityFactory().getByIface(AttrValueProductSku.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntity, getAdaptersRepository(), dtoFactory);
        valueEntity.setAttributeCode(atr.getCode());
        valueEntity.setProductSku(productSku);
        valueEntity = attrValueEntityProductSkuDao.create(valueEntity);
        attrValueDTO.setAttrvalueId(valueEntity.getAttrvalueId());
        return attrValueDTO;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long deleteAttributeValue(final long attributeValuePk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException{
        final AttrValueProductSku attrValue = attrValueEntityProductSkuDao.findById(attributeValuePk);
        final AttributeDTO attributeDTO = dtoAttributeService.findByAttributeCode(attrValue.getAttributeCode());
        if (Etype.IMAGE_BUSINESS_TYPE.equals(attributeDTO.getEtypeName())) {
            imageService.deleteImage(attrValue.getVal(),
                    Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN, systemService.getImageRepositoryDirectory());
        } else if (Etype.FILE_BUSINESS_TYPE.equals(attributeDTO.getEtypeName())) {
            fileService.deleteFile(attrValue.getVal(),
                    Constants.PRODUCT_FILE_REPOSITORY_URL_PATTERN, systemService.getFileRepositoryDirectory());
        }
        attrValueEntityProductSkuDao.delete(attrValue);
        return attrValue.getProductSku().getSkuId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO createAndBindAttrVal(final long entityPk, final String attrName, final String attrValue)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO getNewAttribute(final long entityPk) throws UnableToCreateInstanceException, UnmappedInterfaceException {
        final AttrValueProductSkuDTO dto = new AttrValueProductSkuDTOImpl();
        dto.setSkuId(entityPk);
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getImageRepositoryUrlPattern() {
        return Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileRepositoryUrlPattern() {
        return Constants.PRODUCT_FILE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSysFileRepositoryUrlPattern() {
        return Constants.PRODUCT_SYSFILE_REPOSITORY_URL_PATTERN;
    }


}
