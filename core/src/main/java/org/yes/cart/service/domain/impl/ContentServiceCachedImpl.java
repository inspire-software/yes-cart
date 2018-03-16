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
import org.yes.cart.dao.ResultsIteratorCallback;
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
    @Override
    @Cacheable(value = "contentService-rootContent")
    public Category getRootContent(final long shopId) {
        return contentService.getRootContent(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = "contentService-rootContent")
    public Category createRootContent(final long shopId) {
        return contentService.createRootContent(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "contentService-contentTemplate" )
    public String getContentTemplate(final long contentId) {
        return contentService.getContentTemplate(contentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "contentService-contentBody")
    public String getContentBody(final long contentId, final String locale) {
        return contentService.getContentBody(contentId, locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "contentService-contentBody")
    public String getContentBody(final String contentUri, final String locale) {
        return contentService.getContentBody(contentUri, locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDynamicContentBody(final long contentId, final String locale, final Map<String, Object> context) {
        return contentService.getDynamicContentBody(contentId, locale, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDynamicContentBody(final String contentUri, final String locale, final Map<String, Object> context) {
        return contentService.getDynamicContentBody(contentUri, locale, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "contentService-contentAttributeRecursive")
    public String getContentAttributeRecursive(final String locale, final long contentId, final String attributeName, final String defaultValue) {
        return contentService.getContentAttributeRecursive(locale, contentId, attributeName, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "contentService-contentAttributesRecursive" )
    public String[] getContentAttributeRecursive(final String locale, final long contentId, final String[] attributeNames) {
        return contentService.getContentAttributeRecursive(locale, contentId, attributeNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "contentService-childContent")
    public List<Category> getChildContent(final long contentId) {
        return contentService.getChildContent(contentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> findChildContentWithAvailability(final long contentId, final boolean withAvailability) {
        return contentService.findChildContentWithAvailability(contentId, withAvailability);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "contentService-childContentRecursive")
    public Set<Category> getChildContentRecursive(final long contentId) {
        return contentService.getChildContentRecursive(contentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> findBy(final long shopId, final String code, final String name, final String uri, final int page, final int pageSize) {
        return contentService.findBy(shopId, code, name, uri, page, pageSize);
    }



    /**
     * {@inheritDoc} Just to cache
     */
    @Override
    @Cacheable(value = "contentService-byId")
    public Category getById(final long categoryId) {
        return contentService.getById(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findContentIdBySeoUri(final String seoUri) {
        return contentService.findContentIdBySeoUri(seoUri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findContentIdByGUID(final String guid) {
        return contentService.findContentIdByGUID(guid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findSeoUriByContentId(final Long contentId) {
        return contentService.findSeoUriByContentId(contentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "contentService-contentHasSubcontent")
    public boolean isContentHasSubcontent(final long topContentId, final long subContentId) {
        return contentService.isContentHasSubcontent(topContentId, subContentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> findAll() {
        return contentService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<Category> callback) {
        contentService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Category findById(final long pk) {
        return contentService.findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
    public List<Category> findByCriteria(final String eCriteria, final Object... parameters) {
        return contentService.findByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return contentService.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Category findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return contentService.findSingleByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericDAO<Category, Long> getGenericDao() {
        return contentService.getGenericDao();
    }
}
