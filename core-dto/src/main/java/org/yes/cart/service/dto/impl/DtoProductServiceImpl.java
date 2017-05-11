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
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.CriteriaTuner;
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
import org.yes.cart.utils.impl.AttrValueDTOComparatorImpl;

import java.math.BigDecimal;
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

    private final ProductService productService;
    private final DtoFactory dtoFactory;

    private final DtoAttributeService dtoAttributeService;
    private final DtoAttributeGroupService dtoAttributeGroupService;
    private final DtoEtypeService dtoEtypeService;
    private final DtoProductCategoryService dtoProductCategoryService;
    private DtoProductSkuService dtoProductSkuService;
    private final GenericService<Attribute> attributeService;

    private final GenericDAO<AttrValueProduct, Long> attrValueEntityProductDao;

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
     *
     * @param dtoFactory         factory for creating DTO object instances
     * @param productService     domain objects product service
     * @param adaptersRepository value converter repository
     * @param dtoAttributeGroupService attribute group service
     * @param dtoEtypeService    etype service
     * @param imageService       {@link org.yes.cart.service.domain.ImageService} to manipulate  related images.
     * @param fileService {@link FileService} to manipulate related files
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
        this.systemService = systemService;


        this.productService = (ProductService) productService;
        this.dtoFactory = dtoFactory;
        this.dtoProductCategoryService = dtoProductCategoryService;
/*
        this.AdaptersRepository = AdaptersRepository.getByKeysAsMap(
                "bigDecimalToFloat",
                "brandDTO2Brand",
                "availabilityDto2Availability",
                "productTypeDTO2ProductType");
*/
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

    private final static char[] TAG_OR_CODE_OR_BRAND_OR_TYPE = new char[] { '#', '?', '!', '^' };
    private final static char[] AVAILABILITY = new char[] { '@' };
    static {
        Arrays.sort(TAG_OR_CODE_OR_BRAND_OR_TYPE);
        Arrays.sort(AVAILABILITY);
    }

    private final static Order[] PRODUCT_ORDER = new Order[] { Order.asc("code") };

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<ProductDTO> findBy(final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<ProductDTO> dtos = new ArrayList<ProductDTO>();

        final List<Criterion> criteria = new ArrayList<Criterion>();
        CriteriaTuner tuner = null;

        if (org.springframework.util.StringUtils.hasLength(filter)) {

            final Pair<Date, Date> dateSearch = ComplexSearchUtils.checkDateRangeSearch(filter);

            if (dateSearch != null) {

                if (dateSearch.getFirst() != null) {

                    criteria.add(Restrictions.le("availablefrom", dateSearch.getFirst()));

                }

                if (dateSearch.getSecond() != null) {

                    criteria.add(Restrictions.ge("availableto", dateSearch.getSecond()));

                }


            } else {

                final Pair<String, String> tagOrCodeOrBrandOrType = ComplexSearchUtils.checkSpecialSearch(filter, TAG_OR_CODE_OR_BRAND_OR_TYPE);

                if (tagOrCodeOrBrandOrType != null) {

                    if ("!".equals(tagOrCodeOrBrandOrType.getFirst())) {

                        criteria.add(Restrictions.or(
                                Restrictions.ilike("guid", tagOrCodeOrBrandOrType.getSecond(), MatchMode.EXACT),
                                Restrictions.ilike("code", tagOrCodeOrBrandOrType.getSecond(), MatchMode.EXACT),
                                Restrictions.ilike("manufacturerCode", tagOrCodeOrBrandOrType.getSecond(), MatchMode.EXACT),
                                Restrictions.ilike("manufacturerPartCode", tagOrCodeOrBrandOrType.getSecond(), MatchMode.EXACT),
                                Restrictions.ilike("supplierCode", tagOrCodeOrBrandOrType.getSecond(), MatchMode.EXACT),
                                Restrictions.ilike("pimCode", tagOrCodeOrBrandOrType.getSecond(), MatchMode.EXACT),
                                Restrictions.ilike("tag", tagOrCodeOrBrandOrType.getSecond(), MatchMode.EXACT)
                        ));

                    } else if ("#".equals(tagOrCodeOrBrandOrType.getFirst())) {

                        criteria.add(Restrictions.or(
                                Restrictions.ilike("guid", tagOrCodeOrBrandOrType.getSecond(), MatchMode.ANYWHERE),
                                Restrictions.ilike("code", tagOrCodeOrBrandOrType.getSecond(), MatchMode.ANYWHERE),
                                Restrictions.ilike("manufacturerCode", tagOrCodeOrBrandOrType.getSecond(), MatchMode.ANYWHERE),
                                Restrictions.ilike("manufacturerPartCode", tagOrCodeOrBrandOrType.getSecond(), MatchMode.ANYWHERE),
                                Restrictions.ilike("supplierCode", tagOrCodeOrBrandOrType.getSecond(), MatchMode.ANYWHERE),
                                Restrictions.ilike("pimCode", tagOrCodeOrBrandOrType.getSecond(), MatchMode.ANYWHERE),
                                Restrictions.ilike("tag", tagOrCodeOrBrandOrType.getSecond(), MatchMode.ANYWHERE)
                        ));

                    } else if ("?".equals(tagOrCodeOrBrandOrType.getFirst())) {

                        tuner = new CriteriaTuner() {
                            @Override
                            public void tune(final Criteria crit) {
                                crit.createAlias("brand", "brand");
                                crit.createAlias("producttype", "producttype");
                            }
                        };

                        criteria.add(Restrictions.or(
                                Restrictions.ilike("brand.guid", tagOrCodeOrBrandOrType.getSecond(), MatchMode.ANYWHERE),
                                Restrictions.ilike("brand.name", tagOrCodeOrBrandOrType.getSecond(), MatchMode.ANYWHERE),
                                Restrictions.ilike("producttype.guid", tagOrCodeOrBrandOrType.getSecond(), MatchMode.ANYWHERE),
                                Restrictions.ilike("producttype.name", tagOrCodeOrBrandOrType.getSecond(), MatchMode.ANYWHERE)
                        ));

                    } else if ("^".equals(tagOrCodeOrBrandOrType.getFirst())) {

                        final List<Object> categoryIds = productService.getGenericDao().findQueryObjectByNamedQuery("CATEGORY.IDS.BY.NAME.OR.GUID", '%' + tagOrCodeOrBrandOrType.getSecond() + '%');
                        if (CollectionUtils.isEmpty(categoryIds)) {
                            return Collections.emptyList();
                        }

                        tuner = new CriteriaTuner() {
                            @Override
                            public void tune(final Criteria crit) {
                                crit.createAlias("productCategory", "productCategory");
                            }
                        };

                        criteria.add(Restrictions.in("productCategory.category.categoryId", categoryIds));

                    }

                } else {

                    criteria.add(Restrictions.or(
                            Restrictions.ilike("code", filter, MatchMode.ANYWHERE),
                            Restrictions.ilike("manufacturerCode", filter, MatchMode.ANYWHERE),
                            Restrictions.ilike("manufacturerPartCode", filter, MatchMode.ANYWHERE),
                            Restrictions.ilike("supplierCode", filter, MatchMode.ANYWHERE),
                            Restrictions.ilike("pimCode", filter, MatchMode.ANYWHERE),
                            Restrictions.ilike("name", filter, MatchMode.ANYWHERE),
                            Restrictions.ilike("description", filter, MatchMode.ANYWHERE)
                    ));

                }
            }

        }

        final List<Product> entities = getService().getGenericDao().findByCriteria(tuner,
                page * pageSize, pageSize, criteria.toArray(new Criterion[criteria.size()]), PRODUCT_ORDER);

        fillDTOs(entities, dtos);

        return dtos;
    }

    /**
     * {@inheritDoc}
     */
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
    public ProductDTO create(final ProductDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        cleanProductOrderQuantities(instance);
        return super.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    public ProductDTO update(final ProductDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        cleanProductOrderQuantities(instance);
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    protected void createPostProcess(final ProductDTO dto, final Product entity) {
        ensureBlankUriIsNull(entity);
        super.createPostProcess(dto, entity);
    }

    /**
     * {@inheritDoc}
     */
    protected void updatePostProcess(final ProductDTO dto, final Product entity) {
        ensureBlankUriIsNull(entity);
        super.updatePostProcess(dto, entity);
    }

    private void ensureBlankUriIsNull(final Seoable entity) {
        if (entity.getSeo() != null && entity.getSeo().getUri() != null && StringUtils.isBlank(entity.getSeo().getUri())) {
            entity.getSeo().setUri(null);
        }
    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<ProductDTO> getDtoIFace() {
        return ProductDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<ProductDTOImpl> getDtoImpl() {
        return ProductDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<Product> getEntityIFace() {
        return Product.class;
    }


    /**
     * Get products, that assigned to given category id.
     *
     * @param categoryId given category id
     * @return List of assined product DTOs
     */
    public List<ProductDTO> getProductByCategory(final long categoryId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Product> products = ((ProductService) service).getProductByCategory(categoryId);
        final List<ProductDTO> dtos = new ArrayList<ProductDTO>(products.size());
        fillDTOs(products, dtos);
        return dtos;
    }


    /**
     * Get the all products in category without product availability dependancy
     *
     * @param categoryId  category id
     * @param firtsResult index of first result
     * @param maxResults  quantity results to return
     * @return list of products
     * @throws UnableToCreateInstanceException
     *                                    in case of reflection problem
     * @throws UnmappedInterfaceException in case of configuration problem
     */
    public List<ProductDTO> getProductByCategoryWithPaging(
            final long categoryId,
            final int firtsResult,
            final int maxResults) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Product> products = ((ProductService) service).getProductByCategory(categoryId, firtsResult, maxResults);
        final List<ProductDTO> dtos = new ArrayList<ProductDTO>(products.size());
        fillDTOs(products, dtos);
        return dtos;
    }

    /**
     * {@inheritDoc}
     */
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
        final List<Product> products = ((ProductService) service).getProductByCodeNameBrandType(
                DEFAULT_SEARCH_CRITERIA_TUNER, code, name, brand, productType);

        final List<ProductDTO> dtos = new ArrayList<ProductDTO>(products.size());
        fillDTOs(products, dtos);
        return dtos;

    }


    /**
     * Default criteria tuner to serrch products
     */
    public static CriteriaTuner DEFAULT_SEARCH_CRITERIA_TUNER = new CriteriaTuner() {

        /** {@inheritDoc} */
        public void tune(final Criteria crit) {
            crit.setFetchMode("productCategory", FetchMode.SELECT);
        }

    };

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final ProductDTO productDTO = getById(entityPk);
        final List<AttrValueProductDTO> productAttrs = new ArrayList<AttrValueProductDTO>(productDTO.getAttributes());

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

        final Set<String> existingAttrValueCodes = new HashSet<String>();
        for (final AttrValueProductDTO value : productAttrs) {
            existingAttrValueCodes.add(value.getAttributeDTO().getCode());
        }

        final List<AttrValueProductDTO> full = new ArrayList<AttrValueProductDTO>(ptList.size());
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
        final AttrValueProduct attrValue = attrValueEntityProductDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, attrValue, getAdaptersRepository(), dtoFactory);
        attrValueEntityProductDao.update(attrValue);
        return attrValueDTO;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {

        final Attribute atr = attributeService.findById(attrValueDTO.getAttributeDTO().getAttributeId());
        final boolean multivalue = atr.isAllowduplicate();
        final Product product = service.findById(((AttrValueProductDTO) attrValueDTO).getProductId());
        if (!multivalue) {
            for (final AttrValueProduct avp : product.getAttributes()) {
                if (avp.getAttribute().getCode().equals(atr.getCode())) {
                    // this is a duplicate, so need to update
                    attrValueDTO.setAttrvalueId(avp.getAttrvalueId());
                    return updateEntityAttributeValue(attrValueDTO);
                }
            }
        }

        AttrValueProduct valueEntity = getPersistenceEntityFactory().getByIface(AttrValueProduct.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntity, getAdaptersRepository(), dtoFactory);
        valueEntity.setAttribute(atr);
        valueEntity.setProduct(product);
        valueEntity = attrValueEntityProductDao.create(valueEntity);
        attrValueDTO.setAttrvalueId(valueEntity.getAttrvalueId());
        return attrValueDTO;

    }


    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(final long entityPk, final String attrName, final String attrValue)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        Attribute attribute = attributeService.findSingleByCriteria(Restrictions.eq("code", attrName));
        if (attribute == null) {
            attribute = attributeService.findSingleByCriteria(Restrictions.eq("name", attrName));
        }

        AttributeDTO attrDto = null;

        if (attribute == null) {

            final Map<String, String> displayNames = new TreeMap<String, String>();
            for (String lang : languageService.getSupportedLanguages()) {
                displayNames.put(lang, attrName);
            }

            final AttributeGroupDTO groupDTO = dtoAttributeGroupService.getAttributeGroupByCode(AttributeGroupNames.PRODUCT);
            final Etype etype = (Etype) dtoEtypeService.getService().findSingleByCriteria(Restrictions.eq("businesstype", Etype.STRING_BUSINESS_TYPE));

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
    public void remove(long id) {
        dtoProductCategoryService.removeByProductIds(id);
        getDtoProductSkuService().removeAllInventory(id);
        getDtoProductSkuService().removeAllPrices(id);
        final Object obj = getService().findById(id);
        getService().getGenericDao().evict(obj);
        super.remove(id);
    }


    /**
     * {@inheritDoc}
     */
    public long deleteAttributeValue(final long attributeValuePk) {
        final AttrValueProduct attrValue = attrValueEntityProductDao.findById(attributeValuePk);
        if (Etype.IMAGE_BUSINESS_TYPE.equals(attrValue.getAttribute().getEtype().getBusinesstype())) {
            imageService.deleteImage(attrValue.getVal(),
                    Constants.PRODUCT_IMAGE_REPOSITORY_URL_PATTERN, systemService.getImageRepositoryDirectory());
        } else if (Etype.FILE_BUSINESS_TYPE.equals(attrValue.getAttribute().getEtype().getBusinesstype())) {
            fileService.deleteFile(attrValue.getVal(),
                    Constants.PRODUCT_FILE_REPOSITORY_URL_PATTERN, systemService.getFileRepositoryDirectory());
        }
        attrValueEntityProductDao.delete(attrValue);
        return attrValue.getProduct().getProductId();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isUriAvailableForProduct(final String seoUri, final Long productId) {

        final Long prodId = ((ProductService) service).findProductIdBySeoUri(seoUri);
        return prodId == null || prodId.equals(productId);

    }

    /**
     * {@inheritDoc}
     */
    public boolean isGuidAvailableForProduct(final String guid, final Long productId) {

        final Long prodId = ((ProductService) service).findProductIdByGUID(guid);
        return prodId == null || prodId.equals(productId);

    }

    /**
     * {@inheritDoc}
     */
    public boolean isCodeAvailableForProduct(final String code, final Long productId) {

        final Long prodId = ((ProductService) service).findProductIdByCode(code);
        return prodId == null || prodId.equals(productId);

    }

    /**
     * {@inheritDoc}
     */
    public boolean isUriAvailableForProductSku(final String seoUri, final Long productSkuId) {

        final Long skuId = ((ProductService) service).findProductSkuIdBySeoUri(seoUri);
        return skuId == null || skuId.equals(productSkuId);

    }


    /**
     * {@inheritDoc}
     */
    public boolean isGuidAvailableForProductSku(final String guid, final Long productSkuId) {

        final Long skuId = ((ProductService) service).findProductSkuIdByGUID(guid);
        return skuId == null || skuId.equals(productSkuId);

    }

    /**
     * {@inheritDoc}
     */
    public boolean isCodeAvailableForProductSku(final String code, final Long productSkuId) {

        final Long skuId = ((ProductService) service).findProductSkuIdByCode(code);
        return skuId == null || skuId.equals(productSkuId);

    }


    /**
     * {@inheritDoc}
     */
    public AttrValueDTO getNewAttribute(final long entityPk) throws UnableToCreateInstanceException, UnmappedInterfaceException {
        final AttrValueProductDTO dto = new AttrValueProductDTOImpl();
        dto.setProductId(entityPk);
        return dto;
    }


}
