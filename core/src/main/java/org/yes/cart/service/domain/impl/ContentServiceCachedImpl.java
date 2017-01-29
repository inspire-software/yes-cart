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

import org.hibernate.criterion.Criterion;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.ContentService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 16:22
 */
public class ContentServiceCachedImpl implements ContentService {

    private final ContentService contentService;

    public ContentServiceCachedImpl(final ContentService contentService) {
        this.contentService = contentService;
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-rootContent")
    public Category getRootContent(final long shopId) {
        return contentService.getRootContent(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = "contentService-rootContent")
    public Category createRootContent(final long shopId) {
        return contentService.createRootContent(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentTemplate" )
    public String getContentTemplate(final long contentId) {
        return contentService.getContentTemplate(contentId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentBody")
    public String getContentBody(final long contentId, final String locale) {
        return contentService.getContentBody(contentId, locale);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentBody")
    public String getContentBody(final String contentUri, final String locale) {
        return contentService.getContentBody(contentUri, locale);
    }

    /**
     * {@inheritDoc}
     */
    public String getDynamicContentBody(final long contentId, final String locale, final Map<String, Object> context) {
        return contentService.getDynamicContentBody(contentId, locale, context);
    }

    /**
     * {@inheritDoc}
     */
    public String getDynamicContentBody(final String contentUri, final String locale, final Map<String, Object> context) {
        return contentService.getDynamicContentBody(contentUri, locale, context);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentAttributeRecursive")
    public String getContentAttributeRecursive(final String locale, final long contentId, final String attributeName, final String defaultValue) {
        return contentService.getContentAttributeRecursive(locale, contentId, attributeName, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentAttributesRecursive" )
    public String[] getContentAttributeRecursive(final String locale, final long contentId, final String[] attributeNames) {
        return contentService.getContentAttributeRecursive(locale, contentId, attributeNames);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-childContent")
    public List<Category> getChildContent(final long contentId) {
        return contentService.getChildContent(contentId);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findChildContentWithAvailability(final long contentId, final boolean withAvailability) {
        return contentService.findChildContentWithAvailability(contentId, withAvailability);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-childContentRecursive")
    public Set<Category> getChildContentRecursive(final long contentId) {
        return contentService.getChildContentRecursive(contentId);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findBy(final long shopId, final String code, final String name, final String uri, final int page, final int pageSize) {
        return contentService.findBy(shopId, code, name, uri, page, pageSize);
    }



    /**
     * {@inheritDoc} Just to cache
     */
    @Cacheable(value = "contentService-byId")
    public Category getById(final long categoryId) {
        return contentService.getById(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    public Long findContentIdBySeoUri(final String seoUri) {
        return contentService.findContentIdBySeoUri(seoUri);
    }

    /**
     * {@inheritDoc}
     */
    public Long findContentIdByGUID(final String guid) {
        return contentService.findContentIdByGUID(guid);
    }

    /**
     * {@inheritDoc}
     */
    public String findSeoUriByContentId(final Long contentId) {
        return contentService.findSeoUriByContentId(contentId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentHasSubcontent")
    public boolean isContentHasSubcontent(final long topContentId, final long subContentId) {
        return contentService.isContentHasSubcontent(topContentId, subContentId);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findAll() {
        return contentService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public Category findById(final long pk) {
        return contentService.findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "contentService-rootContent",
            "categoryService-currentCategoryMenu",
            "breadCrumbBuilder-breadCrumbs",
            "contentService-contentAttributeRecursive",
            "contentService-contentAttributesRecursive",
            "contentService-childContent",
            "contentService-childContentRecursive",
            "contentService-byId",
            "contentService-contentHasSubcontent",
            "shopService-shopContentIds",
            "shopService-shopAllCategoriesIds"

    },allEntries = true)
    public Category create(final Category instance) {
        return contentService.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "contentService-rootContent",
            "categoryService-currentCategoryMenu",
            "breadCrumbBuilder-breadCrumbs",
            "contentService-contentTemplate",
            "contentService-contentBody" ,
            "contentService-contentAttributeRecursive",
            "contentService-contentAttributesRecursive",
            "contentService-childContent",
            "contentService-childContentRecursive",
            "contentService-byId",
            "contentService-contentHasSubcontent",
            "shopService-shopContentIds",
            "shopService-shopAllCategoriesIds"
    }, allEntries = true)
    public Category update(final Category instance) {
        return contentService.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value ={
            "contentService-rootContent",
            "categoryService-currentCategoryMenu",
            "breadCrumbBuilder-breadCrumbs",
            "contentService-contentTemplate",
            "contentService-contentBody" ,
            "contentService-contentAttributeRecursive",
            "contentService-contentAttributesRecursive",
            "contentService-childContent",
            "contentService-childContentRecursive",
            "contentService-byId",
            "contentService-contentHasSubcontent",
            "shopService-shopContentIds",
            "shopService-shopAllCategoriesIds"
    }, allEntries = true)
    public void delete(final Category instance) {
        contentService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findByCriteria(final Criterion... criterion) {
        return contentService.findByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final Criterion... criterion) {
        return contentService.findCountByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return contentService.findCountByCriteria(criteriaTuner, criterion);
    }

    /**
     * {@inheritDoc}
     */
    public Category findSingleByCriteria(final Criterion... criterion) {
        return contentService.findSingleByCriteria(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public GenericDAO<Category, Long> getGenericDao() {
        return contentService.getGenericDao();
    }
}
