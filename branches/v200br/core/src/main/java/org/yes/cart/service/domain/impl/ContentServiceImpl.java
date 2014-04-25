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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ContentServiceTemplateSupport;

import java.util.*;

/**
 * User: Denis Pavlov
 * Date: 15-June-2013
 */
public class ContentServiceImpl extends BaseGenericServiceImpl<Category> implements ContentService {

    private final GenericDAO<Category, Long> categoryDao;

    private final GenericDAO<ShopCategory, Long> shopCategoryDao;

    private final GenericDAO<Shop, Long> shopDao;

    private final ContentServiceTemplateSupport templateSupport;

    /**
     * Construct service to manage categories
     *
     * @param categoryDao     category dao to use
     * @param shopCategoryDao shop category dao to use
     * @param shopDao         shop dao
     * @param templateSupport template support
     */
    public ContentServiceImpl(
            final GenericDAO<Category, Long> categoryDao,
            final GenericDAO<ShopCategory, Long> shopCategoryDao,
            final GenericDAO<Shop, Long> shopDao,
            final ContentServiceTemplateSupport templateSupport) {
        super(categoryDao);
        this.categoryDao = categoryDao;
        this.shopCategoryDao = shopCategoryDao;
        this.shopDao = shopDao;
        this.templateSupport = templateSupport;

        this.templateSupport.registerFunction("include", new ContentServiceTemplateSupport.FunctionProvider() {
            @Override
            public Object doAction(final Object... params) {

                if (params != null && params.length == 3) {

                    final String uri = String.valueOf(params[0]);

                    final Long contentId = proxy().findContentIdBySeoUri(uri);

                    if (contentId != null) {
                        final String locale = String.valueOf(params[1]);
                        final Map<String, Object> context = (Map<String, Object>) params[2];


                        return proxy().getDynamicContentBody(contentId, locale, context);

                    }

                }

                return "";
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-rootContent")
    public Category getRootContent(final long shopId) {
        if (shopId <= 0) {
            throw new IllegalArgumentException("Shop must not be null or transient");
        }
        return categoryDao.findSingleByNamedQuery("ROOTCONTENT.BY.SHOP.ID", shopId);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = "contentService-rootContent")
    public Category createRootContent(final long shopId) {
        final List<Object> shops = categoryDao.findQueryObjectByNamedQuery("SHOPCODE.BY.SHOP.ID", shopId);
        if (shops != null && shops.size() == 1) {
            return createContentRootForShop((String) shops.get(0));
        }
        throw new IllegalArgumentException("Unidentified shop id");
    }

    private Category createContentRootForShop(final String shopcode) {
        final Category root = categoryDao.getEntityFactory().getByIface(Category.class);
        root.setGuid(shopcode);
        root.setName(shopcode);
        root.setParentId(0L);
        root.setUitemplate("content");
        root.setAvailablefrom(new Date(0L));
        root.setAvailableto(new Date(7226600400000L));
        root.setNavigationByPrice(false);
        root.setNavigationByPriceTiers("");
        root.setNavigationByBrand(false);
        root.setNavigationByAttributes(false);
        categoryDao.saveOrUpdate(root);
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentTemplateVariation" /*, key = "content.getCategoryId()"*/)
    public String getContentTemplateVariation(final Category content) {
        String variation = null;
        if (StringUtils.isBlank(content.getUitemplate())) {
            if (!content.isRoot()) {
                Category parentCategory =
                        proxy().getById(content.getParentId());
                variation = proxy().getContentTemplateVariation(parentCategory);
            }
        } else {
            variation = content.getUitemplate();
        }
        return variation;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentTemplate" )
    public String getContentTemplate(final long contentId) {
        List<Object> count = categoryDao.findQueryObjectByNamedQuery("TEMPLATE.BY.CATEGORY.ID", contentId);
        if (count != null && count.size() == 1) {
            final String template = (String) count.get(0);
            if (!StringUtils.isBlank(template)) {
                return template;
            }
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-itemsPerPage" /*, key = "content.getCategoryId()"*/)
    public List<String> getItemsPerPage(final Category content) {
        final List<String> rez;
        if (content == null) {
            rez = Constants.DEFAULT_ITEMS_ON_PAGE;
        } else {
            final String val = proxy().getContentAttributeRecursive(null, content, AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null);
            if (val == null) {
                rez = Constants.DEFAULT_ITEMS_ON_PAGE;
            } else {
                rez = Arrays.asList(val.split(","));
            }
        }
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentBody"/* , key = "content.getCategoryId()"*/)
    public String getContentBody(final long contentId, final String locale) {
        final String attributeKey = "CONTENT_BODY_" + locale + "_%";
        final List<Object> bodyList = categoryDao.findQueryObjectByNamedQuery("CONTENTBODY.BY.CONTENTID", contentId, attributeKey, new Date());
        if (bodyList != null && bodyList.size() > 0) {
            final StringBuilder content = new StringBuilder();
            for (final Object bodyPart : bodyList) {
                if (StringUtils.isNotBlank((String) bodyPart)) {
                    content.append(bodyPart);
                }
            }
            return content.toString();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDynamicContentBody(final long contentId, final String locale, final Map<String, Object> context) {

        final String rawContent = proxy().getContentBody(contentId, locale);

        if (rawContent != null) {

            return this.templateSupport.processTemplate(rawContent, locale, context);

        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentAttributeRecursive"/* , key = "content.getCategoryId()"*/)
    public String getContentAttributeRecursive(final String locale, final Category content, final String attributeName, final String defaultValue) {
        final String value = getContentAttributeRecursive(locale, content, attributeName);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentAttributesRecursive" )
    public String[] getContentAttributeRecursive(final String locale, final Category incontent, final String[] attributeNames) {
        final String[] rez;
        final Category content;
        
        if (incontent == null) {
            return null;
        } else {
            content = incontent;
        }

        final AttrValue attrValue = content.getAttributeByCode(attributeNames[0]);
        if (attrValue == null
                ||
                StringUtils.isBlank(attrValue.getVal())) {
            if (content.isRoot()) {
                rez = null; //root of hierarchy
            } else {
                final Category parentCategory =
                        proxy().getById(content.getParentId());
                rez = getContentAttributeRecursive(null, parentCategory, attributeNames);
            }
        } else {
            rez = new String[attributeNames.length];
            int idx = 0;
            for (String attrName : attributeNames) {
                final AttrValue av = content.getAttributeByCode(attrName);
                if (av != null) {
                    rez[idx] = av.getVal();
                } else {
                    rez[idx] = null;

                }
                idx ++;


            }
        }

        return rez;
    }

    /**
     * Get the value of given attribute. If value not present in given category failover to parent category will be used.
     *
     * @param locale        locale for localisable value (or null for raw)
     * @param category      given category
     * @param attributeName attribute name
     * @return value of given attribute name or null if value not found in category hierarchy
     */
    private String getContentAttributeRecursive(final String locale, final Category category, final String attributeName) {

        if (category == null || attributeName == null) {
            return null;
        }

        final AttrValue attrValue = category.getAttributeByCode(attributeName);
        if (attrValue != null) {
            final String val;
            if (locale == null) {
                val = attrValue.getVal();
            } else {
                val = new FailoverStringI18NModel(attrValue.getDisplayVal(), attrValue.getVal()).getValue(locale);
            }
            if (!StringUtils.isBlank(val)) {
                return val;
            }
        }

        if (category.isRoot()) {
            return null; //root of hierarchy
        }
        final Category parentCategory =
                proxy().getById(category.getParentId());
        return getContentAttributeRecursive(locale, parentCategory, attributeName);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-childContent")
    public List<Category> getChildContent(final long contentId) {
        return findChildContentWithAvailability(contentId, true);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findChildContentWithAvailability(final long contentId, final boolean withAvailability) {
        if (withAvailability) {
            return categoryDao.findByNamedQuery(
                    "CATEGORIES.BY.PARENTID",
                    contentId,
                    new Date()
            );
        } else {
            return categoryDao.findByNamedQuery(
                    "CATEGORIES.BY.PARENTID.WITHOUT.DATE.FILTERING",
                    contentId
            );
        }
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-childContentRecursive")
    public Set<Category> getChildContentRecursive(final long contentId) {
        final Category thisCon = proxy().getById(contentId);
        if (thisCon != null) {
            final Set<Category> all = new HashSet<Category>();
            all.add(thisCon);
            loadChildContentRecursiveInternal(all, thisCon);
            return all;
        }
        return Collections.emptySet();
    }


    private void loadChildContentRecursiveInternal(final Set<Category> result, final Category category) {
        List<Category> categories = proxy().getChildContent(category.getCategoryId());
        result.addAll(categories);
        for (Category subCategory : categories) {
            loadChildContentRecursiveInternal(result, subCategory);
        }
    }


    /**
     * {@inheritDoc} Just to cache
     */
    @Cacheable(value = "contentService-byId")
    public Category getById(final long pk) {
        return getGenericDao().findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Long> transform(final Collection<Category> categories) {
        final Set<Long> result = new LinkedHashSet<Long>(categories.size());
        for (Category category : categories) {
            result.add(category.getCategoryId());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Long findContentIdBySeoUri(final String seoUri) {
        List<Object> list = categoryDao.findQueryObjectByNamedQuery("CATEGORY.ID.BY.SEO.URI", seoUri);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String findSeoUriByContentId(final Long contentId) {
        List<Object> list = categoryDao.findQueryObjectByNamedQuery("SEO.URI.BY.CATEGORY.ID", contentId);
        if (list != null && !list.isEmpty()) {
            final Object uri = list.get(0);
            if (uri instanceof String) {
                return (String) uri;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "contentService-contentHasSubcontent")
    public boolean isContentHasSubcontent(final long topContentId, final long subContentId) {
        final Category start = proxy().getById(subContentId);
        if (start != null) {
            if (subContentId == topContentId) {
                return true;
            } else {
                final List<Category> list = new ArrayList<Category>();
                list.add(start);
                addParent(list, topContentId);
                return list.get(list.size() - 1).getCategoryId() == topContentId;
            }
        }
        return false;
    }

    private void addParent(final List<Category> categoryChain, final long categoryIdStopAt) {
        final Category cat = categoryChain.get(categoryChain.size() - 1);
        if (!cat.isRoot()) {
            final Category parent = proxy().getById(cat.getParentId());
            if (parent != null) {
                categoryChain.add(parent);
                if (parent.getCategoryId() != categoryIdStopAt) {
                    addParent(categoryChain, categoryIdStopAt);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "contentService-rootContent",
            "contentService-contentAttributeRecursive",
            "contentService-contentAttributesRecursive",
            "contentService-childContent",
            "contentService-childContentRecursive",
            "contentService-byId",
            "contentService-contentHasSubcontent"

    },allEntries = true)
    public Category create(Category instance) {
        return super.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "contentService-rootContent",
            "contentService-contentTemplateVariation",
            "contentService-contentTemplate",
            "contentService-itemsPerPage",
            "contentService-contentBody" ,
            "contentService-contentAttributeRecursive",
            "contentService-contentAttributesRecursive",
            "contentService-childContent",
            "contentService-childContentRecursive",
            "contentService-byId",
            "contentService-contentHasSubcontent"
    }, allEntries = true)
    public Category update(Category instance) {
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value ={
            "contentService-rootContent",
            "contentService-contentTemplateVariation",
            "contentService-contentTemplate",
            "contentService-itemsPerPage",
            "contentService-contentBody" ,
            "contentService-contentAttributeRecursive",
            "contentService-contentAttributesRecursive",
            "contentService-childContent",
            "contentService-childContentRecursive",
            "contentService-byId",
            "contentService-contentHasSubcontent"
    }, allEntries = true)
    public void delete(Category instance) {
        super.delete(instance);
    }

    private ContentService proxy;

    private ContentService proxy() {
        if (proxy == null) {
            proxy = getSelf();
        }
        return proxy;
    }

    /**
     * @return self proxy to reuse AOP caching
     */
    public ContentService getSelf() {
        // Spring lookup method to get self proxy
        return null;
    }


}
