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

package org.yes.cart.service.domain.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.CategoryService;

import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 16:04
 */
public class CategoryServiceCachedImpl implements CategoryService {

    private final CategoryService categoryService;

    public CategoryServiceCachedImpl(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-rootCategory")
    public Category getRootCategory() {
        return categoryService.getRootCategory();
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> getCategoryLinks(final long categoryId) {
        return categoryService.getCategoryLinks(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryTemplate")
    public String getCategoryTemplate(final long categoryId) {
        return categoryService.getCategoryTemplate(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categorySearchTemplate")
    public String getCategorySearchTemplate(final long categoryId) {
        return categoryService.getCategorySearchTemplate(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryProductTypeId")
    public Long getCategoryProductTypeId(final long categoryId) {
        return categoryService.getCategoryProductTypeId(categoryId);
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryHasChildren")
    public boolean isCategoryHasChildren(final long categoryId) {
        return categoryService.isCategoryHasChildren(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-childCategories")
    public List<Category> getChildCategories(final long categoryId) {
        return categoryService.getChildCategories(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findChildCategoriesWithAvailability(final long categoryId, final boolean withAvailability) {
        return categoryService.findChildCategoriesWithAvailability(categoryId, withAvailability);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-childCategoriesRecursive")
    public Set<Category> getChildCategoriesRecursive(final long categoryId) {
        return categoryService.getChildCategoriesRecursive(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-childCategoriesRecursiveIds")
    public List<Long> getChildCategoriesRecursiveIds(final long categoryId) {
        return categoryService.getChildCategoriesRecursiveIds(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-childCategoriesRecursiveIdsWithLinks")
    public List<Long> getChildCategoriesRecursiveIdsAndLinkIds(final long categoryId) {
        return categoryService.getChildCategoriesRecursiveIdsAndLinkIds(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findBy(final String code, final String name, final String uri, final int page, final int pageSize) {
        return categoryService.findBy(code, name, uri, page, pageSize);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryIdsWithLinks")
    public List<Long> getCategoryIdAndLinkId(final long categoryId) {
        return categoryService.getCategoryIdAndLinkId(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "categoryService-categoryHasSubcategory")
    public boolean isCategoryHasSubcategory(final long topCategoryId, final long subCategoryId) {
        return categoryService.isCategoryHasSubcategory(topCategoryId, subCategoryId);
    }

    /**
     * {@inheritDoc} Just to cache
     */
    @Cacheable(value = "categoryService-byId")
    public Category getById(final long categoryId) {
        return categoryService.getById(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    public Long findCategoryIdBySeoUri(final String seoUri) {
        return categoryService.findCategoryIdBySeoUri(seoUri);
    }

    /**
     * {@inheritDoc}
     */
    public Long findCategoryIdByGUID(final String guid) {
        return categoryService.findCategoryIdByGUID(guid);
    }

    /**
     * {@inheritDoc}
     */
    public String findSeoUriByCategoryId(final Long categoryId) {
        return categoryService.findSeoUriByCategoryId(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    public Category findCategoryIdBySeoUriOrGuid(final String seoUriOrGuid) {
        return categoryService.findCategoryIdBySeoUriOrGuid(seoUriOrGuid);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findByProductId(final long productId) {
        return categoryService.findByProductId(productId);
    }


    /**
     * {@inheritDoc}
     */
    public List<Category> findAll() {
        return categoryService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public Category findById(final long pk) {
        return categoryService.findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "categoryService-topLevelCategories",
            "categoryService-currentCategoryMenu",
            "breadCrumbBuilder-breadCrumbs",
            "categoryService-rootCategory",
            "categoryService-categoryHasChildren",
            "categoryService-childCategories",
            "categoryService-childCategoriesRecursive",
            "categoryService-childCategoriesRecursiveIds",
            "categoryService-childCategoriesRecursiveIdsWithLinks",
            "categoryService-categoryIdsWithLinks",
            "categoryService-categoryHasSubcategory",
            "categoryService-byId",
            "categoryService-relationById",
            "categoryService-categoryParentsIds",
            "shopService-allCategoriesIdsMap",
            "shopService-shopCategoriesIds",
            "shopService-shopAllCategoriesIds"
    }, allEntries = true)
    public Category create(final Category instance) {
        return categoryService.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "categoryService-topLevelCategories",
            "categoryService-currentCategoryMenu",
            "breadCrumbBuilder-breadCrumbs",
            "categoryService-rootCategory",
            "shopService-shopCategoryParentId",
            "categoryService-categoryTemplate",
            "shopService-shopCategoryTemplate",
            "categoryService-categorySearchTemplate",
            "shopService-shopCategorySearchTemplate",
            "categoryService-categoryProductTypeId",
            "shopService-shopCategoryProductTypeId",
            "categoryService-searchCategoriesIds",
            "categoryService-categoryNewArrivalLimit",
            "categoryService-categoryNewArrivalDate",
            "categoryService-categoryHasChildren",
            "categoryService-childCategories",
            "categoryService-childCategoriesRecursive",
            "categoryService-childCategoriesRecursiveIds",
            "categoryService-childCategoriesRecursiveIdsWithLinks",
            "categoryService-categoryIdsWithLinks",
            "categoryService-categoryHasSubcategory",
            "categoryService-byId",
            "categoryService-relationById",
            "categoryService-categoryParentsIds",
            "shopService-allCategoriesIdsMap",
            "shopService-shopCategoriesIds",
            "shopService-shopAllCategoriesIds"
    }, allEntries = true)
    public Category update(final Category instance) {
        return categoryService.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "categoryService-topLevelCategories",
            "categoryService-currentCategoryMenu",
            "breadCrumbBuilder-breadCrumbs",
            "categoryService-rootCategory",
            "shopService-shopCategoryParentId",
            "categoryService-categoryTemplate",
            "shopService-shopCategoryTemplate",
            "categoryService-categorySearchTemplate",
            "shopService-shopCategorySearchTemplate",
            "categoryService-categoryProductTypeId",
            "shopService-shopCategoryProductTypeId",
            "categoryService-searchCategoriesIds",
            "categoryService-categoryNewArrivalLimit",
            "categoryService-categoryNewArrivalDate",
            "categoryService-categoryHasChildren",
            "categoryService-childCategories",
            "categoryService-childCategoriesRecursive",
            "categoryService-childCategoriesRecursiveIds",
            "categoryService-childCategoriesRecursiveIdsWithLinks",
            "categoryService-categoryIdsWithLinks",
            "categoryService-categoryHasSubcategory",
            "categoryService-byId",
            "categoryService-relationById",
            "categoryService-categoryParentsIds"
    }, allEntries = true)
    public void delete(final Category instance) {
        categoryService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findByCriteria(final String eCriteria, final Object... parameters) {
        return categoryService.findByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return categoryService.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public Category findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return categoryService.findSingleByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public GenericDAO<Category, Long> getGenericDao() {
        return categoryService.getGenericDao();
    }
}
