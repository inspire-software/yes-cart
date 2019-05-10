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
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttrValueProductDTOImpl;
import org.yes.cart.domain.dto.impl.ProductDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.ObjectNotFoundException;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnableToWrapObjectException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.dto.*;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.impl.AttrValueDTOComparatorImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Default implementation of {@link DtoProductService}. Uses
 * {@link org.yes.cart.service.domain.ProductService} to retrieve data and
 * {@link com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler} to perform deep
 * conversion of domain objects into DTO.
 * <p/>
 * User: dogma
 * Date: Jan 24, 2011
 * Time: 12:33:31 PM
 */
public class DtoProductServiceImpl
        extends AbstractDtoServiceImpl<ProductDTO, ProductDTOImpl, Product>
        implements DtoProductService {

    private static final AttrValueDTOComparatorImpl ATTR_VALUE_DTO_COMPARATOR = new AttrValueDTOComparatorImpl();

    private final ProductService productService;
    private final DtoFactory dtoFactory;

    private final DtoAttributeService dtoAttributeService;
    private final DtoAttributeGroupService dtoAttributeGroupService;
    private final DtoEtypeService dtoEtypeService;
    private final DtoProductCategoryService dtoProductCategoryService;
    private DtoProductSkuService dtoProductSkuService;
    private final GenericService<Attribute> attributeService;

    private final GenericDAO<AttrValueProduct, Long> attrValueEntityProductDao;
    private final GenericDAO<ProductAssociation, Long> productAssociationDao;

    private final ProductTypeAttrService productTypeAttrService;
    private final DtoProductTypeAttrService dtoProductTypeAttrService;


    private final Assembler productSkuDTOAssembler;
    private final Assembler attrValueAssembler;
    private final ImageService imageService;
    private final FileService fileService;
    private final SystemService systemService;

    private final LanguageService languageService;


    /**
     * IoC constructor.
     *  @param dtoFactory         factory for creating DTO object instances
     * @param productService     domain objects product service
     * @param adaptersRepository value converter repository
     * @param dtoAttributeGroupService attribute group service
     * @param dtoEtypeService    etype service
     * @param imageService       {@link ImageService} to manipulate  related images.
     * @param fileService {@link FileService} to manipulate related files
     * @param productAssociationDao  dao
     * @param systemService      system service
     */
    public DtoProductServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<Product> productService,
            final AdaptersRepository adaptersRepository,
            final DtoAttributeService dtoAttributeService,
            final DtoAttributeGroupService dtoAttributeGroupService,
            final DtoEtypeService dtoEtypeService,
            final GenericDAO<AttrValueProduct, Long> attrValueEntityProductDao,
            final GenericDAO<ProductAssociation, Long> productAssociationDao,
            final ImageService imageService,
            final FileService fileService,
            final DtoProductTypeAttrService dtoProductTypeAttrService,
            final DtoProductCategoryService dtoProductCategoryService,
            final SystemService systemService,
            final LanguageService languageService) {
        super(dtoFactory, productService, adaptersRepository);
        this.dtoAttributeGroupService = dtoAttributeGroupService;
        this.dtoEtypeService = dtoEtypeService;

        this.imageService = imageService;
        this.fileService = fileService;
        this.productAssociationDao = productAssociationDao;
        this.systemService = systemService;


        this.productService = (ProductService) productService;
        this.dtoFactory = dtoFactory;
        this.dtoProductCategoryService = dtoProductCategoryService;
        this.dtoAttributeService = dtoAttributeService;


        this.attrValueEntityProductDao = attrValueEntityProductDao;
        this.attributeService = dtoAttributeService.getService();

        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueProductDTO.class),
                attributeService.getGenericDao().getEntityFactory().getImplClass(AttrValueProduct.class)
        );
        this.productSkuDTOAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(ProductSkuDTO.class),
                ProductSku.class);

        this.dtoProductTypeAttrService = dtoProductTypeAttrService;

        this.productTypeAttrService = (ProductTypeAttrService) dtoProductTypeAttrService.getService();

        this.languageService = languageService;

    }

    public DtoProductSkuService getDtoProductSkuService() {
        if (dtoProductSkuService == null) {
            dtoProductSkuService = lookupDtoProductSkuService();
        }
        return dtoProductSkuService;
    }

    /**
     * @return Spring lookup method to prevent cyclic reference
     */
    public DtoProductSkuService lookupDtoProductSkuService() {
        return null;
    }

    private final static char[] TAG_OR_CODE_OR_BRAND_OR_TYPE = new char[] { '#', '?', '!', '^', '*' };
    private final static char[] AVAILABILITY = new char[] { '@' };
    static {
        Arrays.sort(TAG_OR_CODE_OR_BRAND_OR_TYPE);
        Arrays.sort(AVAILABILITY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ProductDTO> findBy(final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<ProductDTO> dtos = new ArrayList<>();

        List<Product> entities = Collections.emptyList();

        if (StringUtils.isNotBlank(filter)) {

            final Pair<LocalDateTime, LocalDateTime> dateSearch = ComplexSearchUtils.checkDateRangeSearch(filter);

            if (dateSearch != null) {

                entities = getService().getGenericDao().findRangeByCriteria(
                        " where (?1 is null or e.availablefrom <= ?1) and (?2 is null or e.availableto >= ?2) order by e.code",
                        page * pageSize, pageSize,
                        dateSearch.getFirst(),
                        dateSearch.getSecond()
                );

            } else {

                final Pair<String, String> tagOrCodeOrBrandOrType = ComplexSearchUtils.checkSpecialSearch(filter, TAG_OR_CODE_OR_BRAND_OR_TYPE);

                if (tagOrCodeOrBrandOrType != null) {

                    if ("*".equals(tagOrCodeOrBrandOrType.getFirst())) {

                        // If this by PK then to by PK
                        final long byPk = NumberUtils.toLong(tagOrCodeOrBrandOrType.getSecond());
                        if (page == 0 && byPk > 0) {
                            final ProductDTO product = getById(byPk);
                            if (product != null) {
                                dtos.add(product);
                            }
                        }
                        return dtos;

                    } else if ("!".equals(tagOrCodeOrBrandOrType.getFirst())) {

                        entities = getService().getGenericDao().findRangeByCriteria(
                                " where lower(e.guid) like ?1 or lower(e.code) like ?1 or lower(e.manufacturerCode) like ?1 or lower(e.manufacturerPartCode) like ?1 or lower(e.supplierCode) like ?1 or lower(e.pimCode) like ?1 or lower(e.tag) like ?1 order by e.code",
                                page * pageSize, pageSize,
                                HQLUtils.criteriaIeq(tagOrCodeOrBrandOrType.getSecond())
                        );

                    } else if ("#".equals(tagOrCodeOrBrandOrType.getFirst())) {

                        entities = getService().getGenericDao().findRangeByCriteria(
                                " where lower(e.guid) like ?1 or lower(e.code) like ?1 or lower(e.manufacturerCode) like ?1 or lower(e.manufacturerPartCode) like ?1 or lower(e.supplierCode) like ?1 or lower(e.pimCode) like ?1 or lower(e.tag) like ?1 order by e.code",
                                page * pageSize, pageSize,
                                HQLUtils.criteriaIlikeAnywhere(tagOrCodeOrBrandOrType.getSecond())
                        );

                    } else if ("?".equals(tagOrCodeOrBrandOrType.getFirst())) {

                        entities = getService().getGenericDao().findRangeByCriteria(
                                " where lower(e.brand.guid) like ?1 or lower(e.brand.name) like ?1 or lower(e.producttype.guid) like ?1 or lower(e.producttype.name) like ?1 order by e.code",
                                page * pageSize, pageSize,
                                HQLUtils.criteriaIlikeAnywhere(tagOrCodeOrBrandOrType.getSecond())
                        );

                    } else if ("^".equals(tagOrCodeOrBrandOrType.getFirst())) {

                        final List<Object> categoryIds = productService.getGenericDao().findQueryObjectByNamedQuery("CATEGORY.IDS.BY.NAME.OR.GUID", HQLUtils.criteriaIlikeAnywhere(tagOrCodeOrBrandOrType.getSecond()));
                        if (CollectionUtils.isEmpty(categoryIds)) {
                            return Collections.emptyList();
                        }

                        entities = getService().getGenericDao().findRangeByNamedQuery(
                                "PRODUCTS.BY.CATEGORYIDS.ALL",
                                page * pageSize, pageSize,
                                categoryIds
                        );

                    }

                } else {

                    entities = getService().getGenericDao().findRangeByCriteria(
                            " where lower(e.code) like ?1 or lower(e.manufacturerCode) like ?1 or lower(e.manufacturerPartCode) like ?1 or lower(e.supplierCode) like ?1 or lower(e.pimCode) like ?1 or lower(e.name) like ?1 order by e.code",
                            page * pageSize, pageSize,
                            HQLUtils.criteriaIlikeAnywhere(filter)
                    );

                }
            }

        }

        fillDTOs(entities, dtos);

        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ProductSkuDTO getProductSkuByCode(final String skuCode)
            throws ObjectNotFoundException, UnableToWrapObjectException {

        final ProductSku domainSku = productService.getProductSkuByCode(skuCode);
        if (domainSku == null) {
            return null;
        }
        try {
            final ProductSkuDTO dtoSku = dtoFactory.getByIface(ProductSkuDTO.class);
            productSkuDTOAssembler.assembleDto(dtoSku, domainSku, getAdaptersRepository(), dtoFactory);
            return dtoSku;
        } catch (Exception exp) {
            throw new UnableToWrapObjectException(ProductSku.class, ProductSkuDTO.class, exp);
        }
    }

    private void cleanProductOrderQuantities(final ProductDTO productDTO) {
        if (MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, productDTO.getMinOrderQuantity())) {
            productDTO.setMinOrderQuantity(null);
        }
        if (MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, productDTO.getMaxOrderQuantity())) {
            productDTO.setMaxOrderQuantity(null);
        }
        if (MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, productDTO.getStepOrderQuantity())) {
            productDTO.setStepOrderQuantity(null);
        }
    }


    /**
     * {@inheritDoc}
     * Default product sku will be also created.
     */
    @Override
    public ProductDTO create(final ProductDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        cleanProductOrderQuantities(instance);
        return super.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductDTO update(final ProductDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        cleanProductOrderQuantities(instance);
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createPostProcess(final ProductDTO dto, final Product entity) {
        ensureBlankUriIsNull(entity);
        ensureTagsAreLowercase(entity);
        super.createPostProcess(dto, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updatePostProcess(final ProductDTO dto, final Product entity) {
        ensureBlankUriIsNull(entity);
        ensureTagsAreLowercase(entity);
        super.updatePostProcess(dto, entity);
    }

    private void ensureBlankUriIsNull(final Seoable entity) {
        if (entity.getSeo() != null && entity.getSeo().getUri() != null && StringUtils.isBlank(entity.getSeo().getUri())) {
            entity.getSeo().setUri(null);
        }
    }

    private void ensureTagsAreLowercase(final Taggable entity) {
        if (StringUtils.isNotBlank(entity.getTag())) {
            entity.setTag(entity.getTag().toLowerCase());
        }
    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    @Override
    public Class<ProductDTO> getDtoIFace() {
        return ProductDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    @Override
    public Class<ProductDTOImpl> getDtoImpl() {
        return ProductDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    @Override
    public Class<Product> getEntityIFace() {
        return Product.class;
    }


    /**
     * Get products, that assigned to given category id.
     *
     * @param categoryId given category id
     * @return List of assigned product DTOs
     */
    @Override
    public List<ProductDTO> getProductByCategory(final long categoryId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Product> products = ((ProductService) service).findProductByCategory(categoryId);
        final List<ProductDTO> dtos = new ArrayList<>(products.size());
        fillDTOs(products, dtos);
        return dtos;
    }


    /**
     * Get the all products in category without product availability dependancy
     *
     * @param categoryId  category id
     * @param firstResult index of first result
     * @param maxResults  quantity results to return
     * @return list of products
     * @throws UnableToCreateInstanceException
     *                                    in case of reflection problem
     * @throws UnmappedInterfaceException in case of configuration problem
     */
    @Override
    public List<ProductDTO> getProductByCategoryWithPaging(
            final long categoryId,
            final int firstResult,
            final int maxResults) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Product> products = ((ProductService) service).findProductByCategory(categoryId, firstResult, maxResults);
        final List<ProductDTO> dtos = new ArrayList<>(products.size());
        fillDTOs(products, dtos);
        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductDTO> getProductByCodeNameBrandType(
            final String code,
            final String name,
            final long brandId,
            final long productTypeId) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        Long brand = null;
        Long productType = null;
        if (brandId > 0) {
            brand = brandId;
        }
        if (productTypeId > 0) {
            productType = productTypeId;
        }
        final List<Product> products = ((ProductService) service).findProductByCodeNameBrandType(
                code, name, brand, productType);

        final List<ProductDTO> dtos = new ArrayList<>(products.size());
        fillDTOs(products, dtos);
        return dtos;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final ProductDTO productDTO = getById(entityPk);
        final List<AttrValueProductDTO> productAttrs = new ArrayList<>(productDTO.getAttributes());

        final List<AttributeDTO> ptList = dtoAttributeService.findAvailableAttributesByProductTypeId(
                productDTO.getProductTypeDTO().getProducttypeId()
        );

        final List<AttributeDTO> images = dtoAttributeService.findAvailableImageAttributesByGroupCode(
                AttributeGroupNames.PRODUCT
        );

        ptList.addAll(images);

        final List<AttributeDTO> mandatory = dtoAttributeService.findAvailableAttributesByGroupCodeStartsWith(
                AttributeGroupNames.PRODUCT, AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX
        );

        ptList.addAll(mandatory);

        final Set<String> existingAttrValueCodes = new HashSet<>();
        for (final AttrValueProductDTO value : productAttrs) {
            existingAttrValueCodes.add(value.getAttributeDTO().getCode());
        }

        final List<AttrValueProductDTO> full = new ArrayList<>(ptList.size());
        for (final AttributeDTO available : ptList) {
            if (!existingAttrValueCodes.contains(available.getCode())) {
                // add blank value for available attribute
                final AttrValueProductDTO attrValueDTO = getAssemblerDtoFactory().getByIface(AttrValueProductDTO.class);
                attrValueDTO.setAttributeDTO(available);
                attrValueDTO.setProductId(entityPk);
                full.add(attrValueDTO);
            }
        }

        full.addAll(productAttrs); // add all the rest values that are specified for this product

        CollectionUtils.filter(
                full,
                object -> ((AttrValueDTO) object).getAttributeDTO() != null
        );

        full.sort(ATTR_VALUE_DTO_COMPARATOR);
        return full;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final AttrValueProduct attrValue = attrValueEntityProductDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, attrValue, getAdaptersRepository(), dtoFactory);
        attrValueEntityProductDao.update(attrValue);
        return attrValueDTO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {

        final Attribute atr = attributeService.findById(attrValueDTO.getAttributeDTO().getAttributeId());
        final boolean multivalue = atr.isAllowduplicate();
        final Product product = service.findById(((AttrValueProductDTO) attrValueDTO).getProductId());
        if (!multivalue) {
            for (final AttrValueProduct avp : product.getAttributes()) {
                if (avp.getAttributeCode().equals(atr.getCode())) {
                    // this is a duplicate, so need to update
                    attrValueDTO.setAttrvalueId(avp.getAttrvalueId());
                    return updateEntityAttributeValue(attrValueDTO);
                }
            }
        }

        AttrValueProduct valueEntity = getPersistenceEntityFactory().getByIface(AttrValueProduct.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntity, getAdaptersRepository(), dtoFactory);
        valueEntity.setAttributeCode(atr.getCode());
        valueEntity.setProduct(product);
        valueEntity = attrValueEntityProductDao.create(valueEntity);
        attrValueDTO.setAttrvalueId(valueEntity.getAttrvalueId());
        return attrValueDTO;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO createAndBindAttrVal(final long entityPk, final String attrName, final String attrValue)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        Attribute attribute = attributeService.findSingleByCriteria(" where e.code = ?1", attrName);
        if (attribute == null) {
            attribute = attributeService.findSingleByCriteria(" where e.name = ?1", attrName);
        }

        AttributeDTO attrDto;

        if (attribute == null) {

            final Map<String, String> displayNames = new TreeMap<>();
            for (String lang : languageService.getSupportedLanguages()) {
                displayNames.put(lang, attrName);
            }

            final AttributeGroupDTO groupDTO = dtoAttributeGroupService.getAttributeGroupByCode(AttributeGroupNames.PRODUCT);
            final Etype etype = (Etype) dtoEtypeService.getService().findSingleByCriteria(" where e.businesstype = ?1", Etype.STRING_BUSINESS_TYPE);

            attrDto = dtoFactory.getByIface(AttributeDTO.class);
            attrDto.setName(attrName);
            attrDto.setCode(attrName.replaceAll(" ", "-"));
            attrDto.setDisplayNames(displayNames);
            attrDto.setAllowfailover(true);
            attrDto.setAttributegroupId(groupDTO.getAttributegroupId());
            attrDto.setEtypeId(etype.getEtypeId());

            attrDto = dtoAttributeService.create(attrDto);


            ProductType productType = productService.findById(entityPk).getProducttype();
            ProductTypeAttrDTO productTypeAttrDTO = dtoFactory.getByIface(ProductTypeAttrDTO.class);
            productTypeAttrDTO.setAttributeDTO(attrDto);
            productTypeAttrDTO.setProducttypeId(productType.getProducttypeId());
            productTypeAttrDTO.setVisible(true);
            productTypeAttrDTO.setNavigationType(ProductTypeAttr.NAVIGATION_TYPE_SINGLE);
            dtoProductTypeAttrService.create(productTypeAttrDTO);

        } else {

            attrDto = dtoAttributeService.getById(attribute.getAttributeId());


        }

        AttrValueProductDTO attrValueDTO = getAssemblerDtoFactory().getByIface(AttrValueProductDTO.class);
        attrValueDTO.setAttributeDTO(attrDto);
        attrValueDTO.setProductId(entityPk);
        attrValueDTO.setVal(attrValue);

        return createEntityAttributeValue(attrValueDTO);


    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(long id) {
        dtoProductCategoryService.removeByProductIds(id);
        getDtoProductSkuService().removeAllInventory(id);
        getDtoProductSkuService().removeAllPrices(id);

        Product product = getService().findById(id);

        if (product == null) {
            return;
        }

        final List<Long> avIds = new ArrayList<>();
        for (final AttrValueProduct av : product.getAttributes()) {
            avIds.add(av.getAttrvalueId());
        }
        final List<Long> skus = new ArrayList<>();
        for (final ProductSku sku : product.getSku()) {
            skus.add(sku.getSkuId());
        }
        final List<Long> assoc = new ArrayList<>();
        for (final ProductAssociation productAssociation : product.getProductAssociations()) {
            assoc.add(productAssociation.getProductassociationId());
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

        for (final Long sku : skus) {
            try {
                getDtoProductSkuService().remove(sku);
            } catch (Exception exp) {
                // OK
            }
        }

        getService().getGenericDao().flushClear(); // ensure we flush delete and clear session

        for (final Long aid : assoc) {
            productAssociationDao.delete(productAssociationDao.findById(aid));
        }

        getService().getGenericDao().flushClear(); // ensure we flush delete and clear session

        super.remove(id);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public long deleteAttributeValue(final long attributeValuePk)
            throws UnableToCreateInstanceException, UnmappedInterfaceException {
        final AttrValueProduct attrValue = attrValueEntityProductDao.findById(attributeValuePk);
        final AttributeDTO attributeDTO = dtoAttributeService.findByAttributeCode(attrValue.getAttributeCode());
        if (Etype.IMAGE_BUSINESS_TYPE.equals(attributeDTO.getEtypeName())) {
            imageService.deleteImage(attrValue.getVal(),
                    Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN, systemService.getImageRepositoryDirectory());
        } else if (Etype.FILE_BUSINESS_TYPE.equals(attributeDTO.getEtypeName())) {
            fileService.deleteFile(attrValue.getVal(),
                    Constants.PRODUCT_FILE_REPOSITORY_URL_PATTERN, systemService.getFileRepositoryDirectory());
        }
        attrValueEntityProductDao.delete(attrValue);
        return attrValue.getProduct().getProductId();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUriAvailableForProduct(final String seoUri, final Long productId) {

        final Long prodId = ((ProductService) service).findProductIdBySeoUri(seoUri);
        return prodId == null || prodId.equals(productId);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGuidAvailableForProduct(final String guid, final Long productId) {

        final Long prodId = ((ProductService) service).findProductIdByGUID(guid);
        return prodId == null || prodId.equals(productId);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCodeAvailableForProduct(final String code, final Long productId) {

        final Long prodId = ((ProductService) service).findProductIdByCode(code);
        return prodId == null || prodId.equals(productId);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUriAvailableForProductSku(final String seoUri, final Long productSkuId) {

        final Long skuId = ((ProductService) service).findProductSkuIdBySeoUri(seoUri);
        return skuId == null || skuId.equals(productSkuId);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGuidAvailableForProductSku(final String guid, final Long productSkuId) {

        final Long skuId = ((ProductService) service).findProductSkuIdByGUID(guid);
        return skuId == null || skuId.equals(productSkuId);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCodeAvailableForProductSku(final String code, final Long productSkuId) {

        final Long skuId = ((ProductService) service).findProductSkuIdByCode(code);
        return skuId == null || skuId.equals(productSkuId);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO getNewAttribute(final long entityPk) throws UnableToCreateInstanceException, UnmappedInterfaceException {
        final AttrValueProductDTO dto = new AttrValueProductDTOImpl();
        dto.setProductId(entityPk);
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
