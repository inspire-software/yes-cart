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

import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CategoryDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.AttrValueEntityCategory;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.ShopCategoryService;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCategoryService;
import org.yes.cart.utils.impl.AttrValueDTOComparatorImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCategoryServiceImpl
        extends AbstractDtoServiceImpl<CategoryDTO, CategoryDTOImpl, Category>
        implements DtoCategoryService {

    private final GenericService<Seo> seoGenericService;
    private final GenericService<ShopCategory> shopCategoryGenericService;
    private final GenericService<ProductType> productTypeService;

    private final GenericService<Attribute> attributeService;
    private final DtoAttributeService dtoAttributeService;
    private final GenericDAO<AttrValueEntityCategory, Long> attrValueEntityCategoryDao;

    private final DTOAssembler attrValueAssembler;
    private final DTOAssembler shopCategoryAssembler;

    private final ImageService imageService;

    /**
     * Construct base remote service.
     *
     * @param dtoFactory             {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param categoryGenericService category     {@link org.yes.cart.service.domain.GenericService}
     * @param seoGenericService      seo {@link org.yes.cart.service.domain.GenericService}
     * @param imageService {@link ImageService} to manipulate  related images. 
     */
    public DtoCategoryServiceImpl(final DtoFactory dtoFactory,
                                  final GenericService<Category> categoryGenericService,
                                  final GenericService<Seo> seoGenericService,
                                  final GenericService<ShopCategory> shopCategoryGenericService,
                                  final GenericService<ProductType> productTypeService,
                                  final DtoAttributeService dtoAttributeService,
                                  final GenericDAO<AttrValueEntityCategory, Long> attrValueEntityCategoryDao,
                                  final ImageService imageService) {
        super(dtoFactory, categoryGenericService, null);


        this.seoGenericService = seoGenericService;
        //this.shopGenericService = shopGenericService;
        this.shopCategoryGenericService = shopCategoryGenericService;
        this.productTypeService = productTypeService;
        this.attrValueEntityCategoryDao = attrValueEntityCategoryDao;
        this.dtoAttributeService = dtoAttributeService;

        this.attributeService = dtoAttributeService.getService();




        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueCategoryDTO.class),
                attributeService.getGenericDao().getEntityFactory().getImplClass(AttrValueCategory.class));

        this.shopCategoryAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(ShopCategoryDTO.class),
                attributeService.getGenericDao().getEntityFactory().getImplClass(ShopCategory.class));

        this.imageService = imageService;
        

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return getAllWithAvailabilityFilter(false);
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getAllWithAvailabilityFilter(final boolean withAvailalilityFiltering)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        CategoryService categoryService = (CategoryService) service;
        Category root = categoryService.getRootCategory();
        CategoryDTO rootDTO = getById(root.getCategoryId());
        getAllFromRoot(rootDTO, withAvailalilityFiltering);
        return Collections.singletonList(rootDTO);
    }


    private List<CategoryDTO> getAllFromRoot(final CategoryDTO rootDTO, final boolean withAvailalilityFiltering)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final CategoryService categoryService = (CategoryService) service;
        final List<Category> childCategories = categoryService.getChildCategoriesWithAvailability(
                rootDTO.getCategoryId(),
                withAvailalilityFiltering);
        final List<CategoryDTO> childCategoriesDTO = new ArrayList<CategoryDTO>(childCategories.size());
        fillDTOs(childCategories, childCategoriesDTO);
        rootDTO.setChildren(childCategoriesDTO);
        for (CategoryDTO dto : childCategoriesDTO) {
            dto.setChildren(getAllFromRoot(dto, withAvailalilityFiltering));
        }
        return childCategoriesDTO;
    }



    /**
     * {@inheritDoc}
     */
    public CategoryDTO create(final CategoryDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Category category = getEntityFactory().getByIface(Category.class);
        assembler.assembleEntity(instance, category, null, dtoFactory);
        bindDictionaryData(instance, category);
        category = service.create(category);
        return getById(category.getCategoryId());
    }

    /**
     * {@inheritDoc}
     */
    public CategoryDTO update(final CategoryDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Category category = service.getById(instance.getCategoryId());
        assembler.assembleEntity(instance, category, null, dtoFactory);
        bindDictionaryData(instance, category);
        category = service.update(category);
        return getById(category.getCategoryId());

    }

    /**
     * Bind data from dictionaries to category.
     *
     * @param instance category dto to collec data from
     * @param category category to set dictionary data to.
     */
    private void bindDictionaryData(final CategoryDTO instance, final Category category) {
        if (instance.getSeoId() != null && instance.getSeoId() > 0) {
            category.setSeo(seoGenericService.getById(instance.getSeoId()));
        } else {
            category.setSeo(null);
        }
        if (instance.getProductTypeId() != null && instance.getProductTypeId() > 0) {
            category.setProductType(productTypeService.getById(instance.getProductTypeId()));
        } else {
            category.setProductType(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(final long id) {
        ((ShopCategoryService) shopCategoryGenericService).deleteAll(service.getById(id));
        super.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getAllByShopId(final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Category> categories = ((CategoryService) service).getAllByShopId(shopId);
        final List<CategoryDTO> dtos = new ArrayList<CategoryDTO>(categories.size());
        fillDTOs(categories, dtos);
        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    public List<CategoryDTO> getByProductId(final long productId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Category> categories = ((CategoryService) service).getByProductId(productId);
        final List<CategoryDTO> dtos = new ArrayList<CategoryDTO>(categories.size());
        fillDTOs(categories, dtos);
        return dtos;        
    }

    /**
     * Assign category to shop.
     *
     * @param categoryId category id
     * @param shopId     shop id
     * @return {@link ShopCategory}
     */
    public ShopCategoryDTO assignToShop(final long categoryId, final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final ShopCategory shopCategory = ((CategoryService) service).assignToShop(categoryId, shopId);
        ShopCategoryDTO dto = dtoFactory.getByIface(ShopCategoryDTO.class);
        shopCategoryAssembler.assembleDto(dto, shopCategory, null, dtoFactory);
        return dto;
    }

    /**
     * Unassign category from shop.
     *
     * @param categoryId category id
     * @param shopId     shop id
     */
    public void unassignFromShop(final long categoryId, final long shopId) {
        ((CategoryService) service).unassignFromShop(categoryId, shopId);

    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<CategoryDTO> getDtoIFace() {
        return CategoryDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<CategoryDTOImpl> getDtoImpl() {
        return CategoryDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<Category> getEntityIFace() {
        return Category.class;
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<AttrValueCategoryDTO> result = new ArrayList<AttrValueCategoryDTO>();
        result.addAll(getById(entityPk).getAttribute());
        final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                AttributeGroupNames.CATEGORY,
                getCodes(result));
        for (AttributeDTO attributeDTO : availableAttributeDTOs) {
            AttrValueCategoryDTO attrValueCategoryDTO = getDtoFactory().getByIface(AttrValueCategoryDTO.class);
            attrValueCategoryDTO.setAttributeDTO(attributeDTO);
            attrValueCategoryDTO.setCategoryId(entityPk);
            result.add(attrValueCategoryDTO);
        }
        Collections.sort(result, new AttrValueDTOComparatorImpl());
        return result;
    }

    /**
     * Update attribute value.
     *
     * @param attrValueDTO value to update
     * @return updated value
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final AttrValueEntityCategory valueEntityCategory = attrValueEntityCategoryDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityCategory, null, dtoFactory);
        attrValueEntityCategoryDao.update(valueEntityCategory);
        return attrValueDTO;

    }


    /**
     * Delete attribute value by given pk value.
     *
     * @param attributeValuePk given pk value.
     */
    public void deleteAttributeValue(final long attributeValuePk) {
        final AttrValueEntityCategory valueEntityCategory = attrValueEntityCategoryDao.findById(attributeValuePk);
        if (Etype.IMAGE_BUSINESS_TYPE.equals(valueEntityCategory.getAttribute().getEtype().getBusinesstype())) {
            imageService.deleteImage(valueEntityCategory.getVal());
        }

        attrValueEntityCategoryDao.delete(valueEntityCategory);
    }

    /**
     * Create attribute value
     *
     * @param attrValueDTO value to persist
     * @return created value
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        AttrValueCategory valueEntityCategory = getEntityFactory().getByIface(AttrValueCategory.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityCategory, null, dtoFactory);
        Attribute atr = attributeService.getById(attrValueDTO.getAttributeDTO().getAttributeId());
        valueEntityCategory.setAttribute(atr);
        valueEntityCategory.setCategory(service.getById(((AttrValueCategoryDTO) attrValueDTO).getCategoryId()));
        valueEntityCategory = attrValueEntityCategoryDao.create((AttrValueEntityCategory) valueEntityCategory);
        attrValueDTO.setAttrvalueId(valueEntityCategory.getAttrvalueId());
        return attrValueDTO;

    }
}
