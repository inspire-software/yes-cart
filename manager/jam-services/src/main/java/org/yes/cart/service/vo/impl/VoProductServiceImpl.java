/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.vo.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.entity.Etype;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.dto.*;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoIOSupport;
import org.yes.cart.service.vo.VoProductService;

import java.util.*;

/**
 * User: denispavlov
 * Date: 26/09/2016
 * Time: 09:30
 */
public class VoProductServiceImpl implements VoProductService {

    private final DtoAssociationService dtoAssociationService;
    private final DtoProductService dtoProductService;
    private final DtoProductSkuService dtoProductSkuService;
    private final DtoProductAssociationService dtoProductAssociationService;
    private final DtoProductOptionService dtoProductOptionService;
    private final DtoProductCategoryService dtoProductCategoryService;
    private final DtoBrandService dtoBrandService;
    private final DtoProductTypeService dtoProductTypeService;
    private final DtoAttributeService dtoAttributeService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;
    private final VoIOSupport voIOSupport;

    private final VoAttributesCRUDTemplate<VoAttrValueProduct, AttrValueProductDTO> voProductAttributesCRUDTemplate;
    private final VoAttributesCRUDTemplate<VoAttrValueProductSku, AttrValueProductSkuDTO> voSkuAttributesCRUDTemplate;

    public VoProductServiceImpl(final DtoAssociationService dtoAssociationService,
                                final DtoProductService dtoProductService,
                                final DtoProductSkuService dtoProductSkuService,
                                final DtoProductAssociationService dtoProductAssociationService,
                                final DtoProductOptionService dtoProductOptionService,
                                final DtoProductCategoryService dtoProductCategoryService,
                                final DtoBrandService dtoBrandService,
                                final DtoProductTypeService dtoProductTypeService,
                                final DtoAttributeService dtoAttributeService,
                                final FederationFacade federationFacade,
                                final VoAssemblySupport voAssemblySupport,
                                final VoIOSupport voIOSupport) {
        this.dtoAssociationService = dtoAssociationService;
        this.dtoProductService = dtoProductService;
        this.dtoProductSkuService = dtoProductSkuService;
        this.dtoProductAssociationService = dtoProductAssociationService;
        this.dtoProductOptionService = dtoProductOptionService;
        this.dtoProductCategoryService = dtoProductCategoryService;
        this.dtoBrandService = dtoBrandService;
        this.dtoProductTypeService = dtoProductTypeService;
        this.dtoAttributeService = dtoAttributeService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
        this.voIOSupport = voIOSupport;

        this.voProductAttributesCRUDTemplate =
                new VoAttributesCRUDTemplate<VoAttrValueProduct, AttrValueProductDTO>(
                        VoAttrValueProduct.class,
                        AttrValueProductDTO.class,
                        this.dtoProductService,
                        this.dtoAttributeService,
                        this.voAssemblySupport,
                        this.voIOSupport
                )
                {
                    @Override
                    protected boolean skipAttributesInView(final String code, final boolean includeSecure) {
                        return false;
                    }

                    @Override
                    protected long determineObjectId(final VoAttrValueProduct vo) {
                        return vo.getProductId();
                    }

                    @Override
                    protected Pair<Boolean, String> verifyAccessAndDetermineObjectCode(final long objectId, final boolean includeSecure) throws Exception {
                        boolean accessible = federationFacade.isManageable(objectId, ProductDTO.class);
                        if (!accessible) {
                            return new Pair<>(false, null);
                        }
                        final ProductDTO product = dtoProductService.getById(objectId);
                        return new Pair<>(true, product.getCode());
                    }
                };


        this.voSkuAttributesCRUDTemplate =
                new VoAttributesCRUDTemplate<VoAttrValueProductSku, AttrValueProductSkuDTO>(
                        VoAttrValueProductSku.class,
                        AttrValueProductSkuDTO.class,
                        this.dtoProductSkuService,
                        this.dtoAttributeService,
                        this.voAssemblySupport,
                        this.voIOSupport
                )
                {
                    @Override
                    protected boolean skipAttributesInView(final String code, final boolean includeSecure) {
                        return false;
                    }

                    @Override
                    protected long determineObjectId(final VoAttrValueProductSku vo) {
                        return vo.getSkuId();
                    }

                    @Override
                    protected Pair<Boolean, String> verifyAccessAndDetermineObjectCode(final long objectId, final boolean includeSecure) throws Exception {
                        final ProductSkuDTO sku = dtoProductSkuService.getById(objectId);
                        boolean accessible = sku != null && federationFacade.isManageable(sku.getProductId(), ProductDTO.class);
                        if (!accessible) {
                            return new Pair<>(false, null);
                        }
                        return new Pair<>(true, sku.getCode());
                    }
                };

    }


    /** {@inheritDoc} */
    @Override
    public List<VoAssociation> getAllAssociations() throws Exception {
        return voAssemblySupport.assembleVos(VoAssociation.class, AssociationDTO.class, dtoAssociationService.getAll());
    }

    /** {@inheritDoc} */
    @Override
    public VoSearchResult<VoProduct> getFilteredProducts(final VoSearchContext filter) throws Exception {

        final VoSearchResult<VoProduct> result = new VoSearchResult<>();
        final List<VoProduct> results = new ArrayList<>();
        result.setSearchContext(filter);
        result.setItems(results);

        final Map<String, List> params = new HashMap<>();
        if (filter.getParameters() != null) {
            params.putAll(filter.getParameters());
        }
        if (!federationFacade.isCurrentUserSystemAdmin()) {
            params.put("supplierCatalogCodes", new ArrayList(federationFacade.getAccessibleSupplierCatalogCodesByCurrentManager()));
        }

        final SearchContext searchContext = new SearchContext(
                params,
                filter.getStart(),
                filter.getSize(),
                filter.getSortBy(),
                filter.isSortDesc(),
                "filter", "supplierCatalogCodes"
        );

        final SearchResult<ProductDTO> batch = dtoProductService.findProducts(searchContext);
        results.addAll(voAssemblySupport.assembleVos(VoProduct.class, ProductDTO.class, batch.getItems()));
        result.setTotal(batch.getTotal());

        return result;

    }

    /** {@inheritDoc} */
    @Override
    public VoProductWithLinks getProductById(final long id) throws Exception {

        if (federationFacade.isManageable(id, ProductDTO.class)) {

            final ProductDTO dto = dtoProductService.getById(id);

            final VoProductWithLinks vo = voAssemblySupport
                    .assembleVo(VoProductWithLinks.class, ProductDTO.class, new VoProductWithLinks(), dto);

            vo.setAssociations(voAssemblySupport
                    .assembleVos(VoProductAssociation.class, ProductAssociationDTO.class,
                            dtoProductAssociationService.getProductAssociations(id)));

            return vo;
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc} */
    @Override
    public VoProductWithLinks updateProduct(final VoProductWithLinks vo) throws Exception {

        if (!federationFacade.isManageable(vo.getProductId(), ProductDTO.class)) {
            throw new AccessDeniedException("Access is denied");
        }

        final Map<Long, VoProductCategory> keepCategories = new HashMap<>();
        if (CollectionUtils.isNotEmpty(vo.getProductCategories())) {
            for (final VoProductCategory productCategory : vo.getProductCategories()) {
                keepCategories.put(productCategory.getCategoryId(), productCategory);
            }
        }

        ProductDTO origin = dtoProductService.getById(vo.getProductId());

        ProductDTO dto = voAssemblySupport.assembleDto(ProductDTO.class, VoProductWithLinks.class,
                origin, vo);

        dto.setBrandDTO(dtoBrandService.getById(vo.getBrand().getBrandId()));
        dto.setProductTypeDTO(dtoProductTypeService.getById(vo.getProductType().getProducttypeId()));

        dto = dtoProductService.update(voAssemblySupport.assembleDto(ProductDTO.class, VoProduct.class, dto, vo));

        if (CollectionUtils.isNotEmpty(dto.getProductCategoryDTOs())) {
            // Remove disassociated categories
            for (final ProductCategoryDTO productCategory : dto.getProductCategoryDTOs()) {
                final VoProductCategory update = keepCategories.get(productCategory.getCategoryId());
                keepCategories.remove(productCategory.getCategoryId());
                if (update == null && federationFacade.isManageable(productCategory.getCategoryId(), CategoryDTO.class)) {
                    dtoProductCategoryService.remove(productCategory.getProductCategoryId());
                } else if (update != null && federationFacade.isManageable(productCategory.getCategoryId(), CategoryDTO.class)) {
                    dtoProductCategoryService.update(
                            voAssemblySupport.assembleDto(ProductCategoryDTO.class, VoProductCategory.class, productCategory, update)
                    );
                }
            }
        }

        for (final Map.Entry<Long, VoProductCategory> categoryToAdd : keepCategories.entrySet()) {
            // Add new accessible ones only
            if (federationFacade.isManageable(categoryToAdd.getKey(), CategoryDTO.class)) {
                final ProductCategoryDTO assoc = dtoProductCategoryService.getNew();
                dtoProductCategoryService.create(
                        voAssemblySupport.assembleDto(ProductCategoryDTO.class, VoProductCategory.class, assoc, categoryToAdd.getValue())
                );
            }

        }


        final Map<Long, VoProductAssociation> keepAssoc = new HashMap<>();
        final List<VoProductAssociation> addAssoc = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(vo.getAssociations())) {
            for (final VoProductAssociation productAssociation : vo.getAssociations()) {
                if (productAssociation.getProductassociationId() > 0L) {
                    keepAssoc.put(productAssociation.getProductassociationId(), productAssociation);
                } else {
                    addAssoc.add(productAssociation);
                }
            }
        }

        final List<ProductAssociationDTO> existingAssoc = dtoProductAssociationService.getProductAssociations(vo.getProductId());
        if (CollectionUtils.isNotEmpty(existingAssoc)) {
            for (final ProductAssociationDTO existing : existingAssoc) {
                final VoProductAssociation update = keepAssoc.get(existing.getProductassociationId());
                keepAssoc.remove(existing.getProductassociationId());
                if (update == null) {
                    dtoProductAssociationService.remove(existing.getProductassociationId());
                } else {
                    dtoProductAssociationService.update(
                            voAssemblySupport.assembleDto(ProductAssociationDTO.class, VoProductAssociation.class, existing, update)
                    );
                }
            }
        }

        for (final VoProductAssociation assocToAdd : addAssoc) {
            final ProductAssociationDTO assoc = dtoProductAssociationService.getNew();
            dtoProductAssociationService.create(
                    voAssemblySupport.assembleDto(ProductAssociationDTO.class, VoProductAssociation.class, assoc, assocToAdd)
            );

            if (assocToAdd.isBidirectional()) {
                final Long productId = ((ProductService) dtoProductService.getService()).findProductIdByCode(assocToAdd.getAssociatedCode());

                if (productId != null) {

                    final List<ProductAssociationDTO> existingAssoc2 = dtoProductAssociationService.getProductAssociations(productId);
                    boolean duplicate = false;
                    if (CollectionUtils.isNotEmpty(existingAssoc2)) {
                        for (final ProductAssociationDTO existing : existingAssoc2) {
                            if (existing.getAssociatedCode().equals(dto.getCode())) {
                                duplicate = true;
                                break;
                            }
                        }
                    }

                    if (!duplicate) {
                        final VoProductAssociation reverse = new VoProductAssociation();
                        reverse.setAssociationId(assocToAdd.getAssociationId());
                        reverse.setAssociatedCode(dto.getCode());
                        reverse.setAssociatedName(dto.getName());
                        reverse.setProductId(productId);
                        reverse.setRank(assocToAdd.getRank());
                        final ProductAssociationDTO assoc2 = dtoProductAssociationService.getNew();
                        dtoProductAssociationService.create(
                                voAssemblySupport.assembleDto(ProductAssociationDTO.class, VoProductAssociation.class, assoc2, reverse)
                        );
                    }
                }
            }
        }



        final Map<Long, VoProductOption> keepOpts = new HashMap<>();
        final List<VoProductOption> addOpts = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(vo.getConfigurationOptions())) {
            for (final VoProductOption productOption : vo.getConfigurationOptions()) {
                if (productOption.getProductoptionId() > 0L) {
                    keepOpts.put(productOption.getProductoptionId(), productOption);
                } else {
                    addOpts.add(productOption);
                }
            }
        }

        final List<ProductOptionDTO> existingOpts = origin.getConfigurationOptions();
        if (CollectionUtils.isNotEmpty(existingOpts)) {
            for (final ProductOptionDTO existing : existingOpts) {
                final VoProductOption update = keepOpts.get(existing.getProductoptionId());
                keepOpts.remove(existing.getProductoptionId());
                if (update == null) {
                    dtoProductOptionService.remove(existing.getProductoptionId());
                } else {
                    dtoProductOptionService.update(
                            voAssemblySupport.assembleDto(ProductOptionDTO.class, VoProductOption.class, existing, update)
                    );
                }
            }
        }

        for (final VoProductOption optToAdd : addOpts) {
            final ProductOptionDTO opt = dtoProductOptionService.getNew();
            dtoProductOptionService.create(
                    voAssemblySupport.assembleDto(ProductOptionDTO.class, VoProductOption.class, opt, optToAdd)
            );
        }

        return getProductById(dto.getProductId());
    }

    /** {@inheritDoc} */
    @Override
    public VoProductWithLinks createProduct(final VoProduct vo) throws Exception {

        if (CollectionUtils.isNotEmpty(vo.getProductCategories())) {
            for (final VoProductCategory productCategory : vo.getProductCategories()) {
                if (!federationFacade.isManageable(productCategory.getCategoryId(), CategoryDTO.class)) {
                    // Not manageable!
                    throw new AccessDeniedException("Access is denied");
                }
            }
        } else if (!federationFacade.isCurrentUserSystemAdmin()) {
            // Only sys admins can add products without categories
            throw new AccessDeniedException("Access is denied");
        }

        ProductDTO dto = dtoProductService.getNew();

        dto.setBrandDTO(dtoBrandService.getById(vo.getBrand().getBrandId()));
        dto.setProductTypeDTO(dtoProductTypeService.getById(vo.getProductType().getProducttypeId()));

        dto = dtoProductService.create(voAssemblySupport.assembleDto(ProductDTO.class, VoProduct.class, dto, vo));

        if (CollectionUtils.isNotEmpty(vo.getProductCategories())) {
            for (final VoProductCategory productCategory : vo.getProductCategories()) {
                final ProductCategoryDTO catDto = voAssemblySupport.assembleDto(
                        ProductCategoryDTO.class, VoProductCategory.class, dtoProductCategoryService.getNew(), productCategory);
                catDto.setProductId(dto.getProductId());
                dtoProductCategoryService.create(catDto);
            }
        }

        return getProductById(dto.getProductId());
    }

    /** {@inheritDoc} */
    @Override
    public VoProductWithLinks copyProduct(final long id, final VoProduct vo) throws Exception {

        if (!federationFacade.isManageable(id, ProductDTO.class)) {
            throw new AccessDeniedException("Access is denied");
        }

        final VoProductWithLinks existing = getProductById(id);
        if (existing == null) {
            throw new AccessDeniedException("Access is denied");
        }

        final VoProduct main = vo != null ? vo : existing;
        ProductDTO dto = voAssemblySupport.assembleDto(ProductDTO.class, VoProduct.class, dtoProductService.getNew(), main);
        if (vo == null || vo.getGuid() == null) {
            dto.setGuid(UUID.randomUUID().toString());
            dto.setCode(dto.getGuid());
        }
        if (dto.getUri() != null && dto.getUri().equals(existing.getUri())) {
            dto.setUri(dto.getCode() + "-" + dto.getUri());
        }

        dto.setBrandDTO(dtoBrandService.getById(main.getBrand().getBrandId()));
        dto.setProductTypeDTO(dtoProductTypeService.getById(main.getProductType().getProducttypeId()));

        dto = dtoProductService.create(dto);

        if (CollectionUtils.isNotEmpty(existing.getProductCategories())) {
            for (final VoProductCategory productCategory : existing.getProductCategories()) {
                final ProductCategoryDTO catDto = voAssemblySupport.assembleDto(
                        ProductCategoryDTO.class, VoProductCategory.class, dtoProductCategoryService.getNew(), productCategory);
                catDto.setProductId(dto.getProductId());
                dtoProductCategoryService.create(catDto);
            }
        }

        final List<VoProductAssociation> existingAssoc = existing.getAssociations();
        if (CollectionUtils.isNotEmpty(existingAssoc)) {
            for (final VoProductAssociation assocToAdd : existingAssoc) {
                ProductAssociationDTO assoc = dtoProductAssociationService.getNew();
                assoc = voAssemblySupport.assembleDto(ProductAssociationDTO.class, VoProductAssociation.class, assoc, assocToAdd);
                assoc.setProductId(dto.getProductId());
                dtoProductAssociationService.create(assoc);
            }
        }

        final List<VoAttrValueProduct> prodAttrsToAdd = getProductAttributes(existing.getProductId());
        if (CollectionUtils.isNotEmpty(prodAttrsToAdd)) {
            final List<MutablePair<VoAttrValueProduct, Boolean>> attrsToAddList = new ArrayList<>();
            for (final VoAttrValueProduct attrToAdd : prodAttrsToAdd) {
                if (StringUtils.isNotEmpty(attrToAdd.getVal())) {
                    attrToAdd.setAttrvalueId(0L);
                    attrToAdd.setProductId(dto.getProductId());
                    if (Etype.IMAGE_BUSINESS_TYPE.equals(attrToAdd.getAttribute().getEtype())) {
                        final int codePos = attrToAdd.getVal().indexOf(existing.getCode());
                        final int extPos = attrToAdd.getVal().lastIndexOf('.');
                        attrToAdd.setVal(attrToAdd.getVal().substring(0, codePos - 1) + attrToAdd.getVal().substring(extPos)); // remove original code part
                    }
                    attrsToAddList.add(MutablePair.of(attrToAdd, false));
                }
            }
            if (!attrsToAddList.isEmpty()) {
                updateProductAttributes(attrsToAddList);
            }
        }

        // Align first SKU
        Pair<String, String> skuPair = null;
        if (CollectionUtils.isNotEmpty(existing.getSku())) {
            final VoProductSku sku1 = existing.getSku().get(0);
            ProductSkuDTO sku1copy = dto.getSku().get(0);
            final String skuCode1copy = sku1copy.getCode();
            final String skuGuid1copy = sku1copy.getGuid();
            skuPair = new Pair<>(sku1.getCode(), skuCode1copy);

            sku1copy = voAssemblySupport.assembleDto(ProductSkuDTO.class, VoProductSku.class, sku1copy, sku1);

            sku1copy.setProductId(dto.getProductId());
            sku1copy.setGuid(skuGuid1copy);
            sku1copy.setCode(skuCode1copy);
            if (sku1copy.getUri() != null && sku1copy.getUri().equals(sku1.getUri())) {
                sku1copy.setUri(skuCode1copy + "-" + sku1.getUri());
            }

            sku1copy = dtoProductSkuService.update(sku1copy);

            final List<VoAttrValueProductSku> skuAttrsToAdd = getSkuAttributes(sku1.getSkuId());
            if (CollectionUtils.isNotEmpty(skuAttrsToAdd)) {
                final List<MutablePair<VoAttrValueProductSku, Boolean>> attrsToAddList = new ArrayList<>();
                for (final VoAttrValueProductSku attrToAdd : skuAttrsToAdd) {
                    if (StringUtils.isNotEmpty(attrToAdd.getVal())) {
                        attrToAdd.setAttrvalueId(0L);
                        attrToAdd.setSkuId(sku1copy.getSkuId());
                        if (Etype.IMAGE_BUSINESS_TYPE.equals(attrToAdd.getAttribute().getEtype())) {
                            final int codePos = attrToAdd.getVal().indexOf(skuPair.getFirst());
                            final int extPos = attrToAdd.getVal().lastIndexOf('.');
                            attrToAdd.setVal(attrToAdd.getVal().substring(0, codePos - 1) + attrToAdd.getVal().substring(extPos)); // remove original code part
                        }
                        attrsToAddList.add(MutablePair.of(attrToAdd, false));
                    }
                }
                if (!attrsToAddList.isEmpty()) {
                    updateSkuAttributes(attrsToAddList);
                }
            }

        }


        final List<VoProductOption> existingOpts = existing.getConfigurationOptions();
        if (CollectionUtils.isNotEmpty(existingOpts)) {
            for (final VoProductOption optToAdd : existingOpts) {
                if (optToAdd.getSkuCode() == null || (skuPair != null && optToAdd.getSkuCode().equals(skuPair.getFirst()))) {
                    final ProductOptionDTO opt = voAssemblySupport.assembleDto(ProductOptionDTO.class, VoProductOption.class, dtoProductOptionService.getNew(), optToAdd);
                    opt.setProductId(dto.getProductId());
                    if (optToAdd.getSkuCode() != null) {
                        optToAdd.setSkuCode(skuPair.getSecond());
                    }
                    dtoProductOptionService.create(opt);
                }
            }
        }


        return getProductById(dto.getProductId());

    }

    /** {@inheritDoc} */
    @Override
    public void removeProduct(final long id) throws Exception {

        if (federationFacade.isManageable(id, ProductDTO.class)) {
            dtoProductService.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc} */
    @Override
    public List<VoAttrValueProduct> getProductAttributes(final long productId) throws Exception {
        return this.voProductAttributesCRUDTemplate.verifyAccessAndGetAttributes(productId, true);
    }

    /** {@inheritDoc} */
    @Override
    public List<VoAttrValueProduct> updateProductAttributes(final List<MutablePair<VoAttrValueProduct, Boolean>> vo) throws Exception {

        final long productId = this.voProductAttributesCRUDTemplate.verifyAccessAndUpdateAttributes(vo, true);

        return getProductAttributes(productId);

    }

    /** {@inheritDoc} */
    @Override
    public List<VoProductSku> getProductSkuAll(final long productId) throws Exception {

        final List<VoProductSku> vos = new ArrayList<>();

        if (federationFacade.isManageable(productId, ProductDTO.class)) {

            return voAssemblySupport.assembleVos(
                    VoProductSku.class, ProductSkuDTO.class,
                    dtoProductSkuService.getAllProductSkus(productId));

        }

        return vos;

    }

    /** {@inheritDoc} */
    @Override
    public VoSearchResult<VoProductSku> getFilteredProductSkus(final VoSearchContext filter) throws Exception {


        final VoSearchResult<VoProductSku> result = new VoSearchResult<>();
        final List<VoProductSku> results = new ArrayList<>();
        result.setSearchContext(filter);
        result.setItems(results);

        final Map<String, List> params = new HashMap<>();
        if (filter.getParameters() != null) {
            params.putAll(filter.getParameters());
        }
        if (!federationFacade.isCurrentUserSystemAdmin()) {
            params.put("supplierCatalogCodes", new ArrayList(federationFacade.getAccessibleSupplierCatalogCodesByCurrentManager()));
        }

        final SearchContext searchContext = new SearchContext(
                params,
                filter.getStart(),
                filter.getSize(),
                filter.getSortBy(),
                filter.isSortDesc(),
                "filter", "supplierCatalogCodes"
        );

        final SearchResult<ProductSkuDTO> batch = dtoProductSkuService.findProductSkus(searchContext);
        results.addAll(voAssemblySupport.assembleVos(VoProductSku.class, ProductSkuDTO.class, batch.getItems()));
        result.setTotal(batch.getTotal());

        return result;

    }

    /** {@inheritDoc} */
    @Override
    public VoProductSku getSkuById(final long id) throws Exception {

        final ProductSkuDTO sku = dtoProductSkuService.getById(id);
        if (sku != null && federationFacade.isManageable(sku.getProductId(), ProductDTO.class)) {

            return voAssemblySupport.assembleVo(VoProductSku.class, ProductSkuDTO.class, new VoProductSku(), sku);

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc} */
    @Override
    public VoProductSku updateSku(final VoProductSku vo) throws Exception {

        ProductSkuDTO dto = dtoProductSkuService.getById(vo.getSkuId());

        if (dto != null && federationFacade.isManageable(dto.getProductId(), ProductDTO.class)) {

            dto = dtoProductSkuService.update(
                    voAssemblySupport.assembleDto(ProductSkuDTO.class, VoProductSku.class, dto, vo)
            );

            return getSkuById(dto.getSkuId());

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc} */
    @Override
    public VoProductSku createSku(final VoProductSku vo) throws Exception {

        if (vo != null && federationFacade.isManageable(vo.getProductId(), ProductDTO.class)) {

            ProductSkuDTO dto = dtoProductSkuService.getNew();

            dto = dtoProductSkuService.create(
                    voAssemblySupport.assembleDto(ProductSkuDTO.class, VoProductSku.class, dto, vo)
            );

            return getSkuById(dto.getSkuId());

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc} */
    @Override
    public VoProductSku copySku(final long id, final VoProductSku vo) throws Exception {

        final VoProductSku existing = getSkuById(id);

        if (existing != null && federationFacade.isManageable(existing.getProductId(), ProductDTO.class)) {

            final VoProductSku main = vo != null ? vo : existing;

            ProductSkuDTO dto = voAssemblySupport.assembleDto(ProductSkuDTO.class, VoProductSku.class, dtoProductSkuService.getNew(), main);
            if (vo == null || vo.getGuid() == null) {
                dto.setGuid(UUID.randomUUID().toString());
                dto.setCode(dto.getGuid());
            }
            if (dto.getUri() != null && dto.getUri().equals(existing.getUri())) {
                dto.setUri(dto.getCode() + "-" + dto.getUri());
            }

            dto = dtoProductSkuService.create(dto);

            final List<VoAttrValueProductSku> skuAttrsToAdd = getSkuAttributes(existing.getSkuId());
            if (CollectionUtils.isNotEmpty(skuAttrsToAdd)) {
                final List<MutablePair<VoAttrValueProductSku, Boolean>> attrsToAddList = new ArrayList<>();
                for (final VoAttrValueProductSku attrToAdd : skuAttrsToAdd) {
                    if (StringUtils.isNotEmpty(attrToAdd.getVal())) {
                        attrToAdd.setAttrvalueId(0L);
                        attrToAdd.setSkuId(dto.getSkuId());
                        if (Etype.IMAGE_BUSINESS_TYPE.equals(attrToAdd.getAttribute().getEtype())) {
                            final int codePos = attrToAdd.getVal().indexOf(existing.getCode());
                            final int extPos = attrToAdd.getVal().lastIndexOf('.');
                            attrToAdd.setVal(attrToAdd.getVal().substring(0, codePos - 1) + attrToAdd.getVal().substring(extPos)); // remove original code part
                        }
                        attrsToAddList.add(MutablePair.of(attrToAdd, false));
                    }
                }
                if (!attrsToAddList.isEmpty()) {
                    updateSkuAttributes(attrsToAddList);
                }
            }

            return getSkuById(dto.getSkuId());

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    /** {@inheritDoc} */
    @Override
    public void removeSku(final long id) throws Exception {

        getSkuById(id); // check access
        dtoProductSkuService.remove(id);

    }

    /** {@inheritDoc} */
    @Override
    public List<VoAttrValueProductSku> getSkuAttributes(final long skuId) throws Exception {
        return this.voSkuAttributesCRUDTemplate.verifyAccessAndGetAttributes(skuId, true);
    }

    /** {@inheritDoc} */
    @Override
    public List<VoAttrValueProductSku> updateSkuAttributes(final List<MutablePair<VoAttrValueProductSku, Boolean>> vo) throws Exception {

        final long skuId = this.voSkuAttributesCRUDTemplate.verifyAccessAndUpdateAttributes(vo, true);

        return getSkuAttributes(skuId);

    }
}
