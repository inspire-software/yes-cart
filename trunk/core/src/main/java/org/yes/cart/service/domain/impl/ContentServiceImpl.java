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
import org.yes.cart.cache.Cacheable;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.ContentService;

import java.util.*;

/**
 * User: Denis Pavlov
 * Date: 15-June-2013
 */
public class ContentServiceImpl extends BaseGenericServiceImpl<Category> implements ContentService {

    private static final String CACHE_NAME = "contentServiceImplMethodCache";

    private final GenericDAO<Category, Long> categoryDao;

    private final GenericDAO<ShopCategory, Long> shopCategoryDao;

    private final GenericDAO<Shop, Long> shopDao;

    /**
     * Construct service to manage categories
     *
     * @param categoryDao     category dao to use
     * @param shopCategoryDao shop category dao to use
     * @param shopDao         shop dao
     */
    public ContentServiceImpl(
            final GenericDAO<Category, Long> categoryDao,
            final GenericDAO<ShopCategory, Long> shopCategoryDao,
            final GenericDAO<Shop, Long> shopDao) {
        super(categoryDao);
        this.categoryDao = categoryDao;
        this.shopCategoryDao = shopCategoryDao;
        this.shopDao = shopDao;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
    public Category getRootContent(final long shopId) {
        if (shopId <= 0) {
            throw new IllegalArgumentException("Shop must not be null or transient");
        }
        final Category shopContentRoot = categoryDao.findSingleByNamedQuery("ROOTCONTENT.BY.SHOP.ID", shopId);
        if (shopContentRoot == null) {
            final List<Object> shops = categoryDao.findQueryObjectByNamedQuery("SHOPCODE.BY.SHOP.ID", shopId);
            if (shops != null && shops.size() == 1) {
                return createContentRootForShop((String) shops.get(0));
            }
            throw new IllegalArgumentException("Unidentified shop id");
        }
        return shopContentRoot;
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
    @Cacheable(value = CACHE_NAME)
    public String getContentTemplateVariation(final Category content) {
        String variation = null;
        if (StringUtils.isBlank(content.getUitemplate())) {
            if (content.getParentId() != 0) {
                Category parentCategory =
                        categoryDao.findById(content.getParentId());
                variation = getContentTemplateVariation(parentCategory);
            }
        } else {
            variation = content.getUitemplate();
        }
        return variation;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
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
    @Cacheable(value = CACHE_NAME)
    public List<String> getItemsPerPage(final Category content) {
        final List<String> rez;
        if (content == null) {
            rez = Constants.DEFAULT_ITEMS_ON_PAGE;
        } else {
            final String val = getContentAttributeRecursive(null, content, AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null);
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
    @Cacheable(value = CACHE_NAME)
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
    @Cacheable(value = CACHE_NAME)
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
    @Cacheable(value = CACHE_NAME)
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
            if (content.getCategoryId() == content.getParentId()) {
                rez = null; //root of hierarchy
            } else {
                final Category parentCategory =
                        categoryDao.findById(content.getParentId());
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

        if (category.getCategoryId() == category.getParentId()) {
            return null; //root of hierarchy
        }
        final Category parentCategory =
                categoryDao.findById(category.getParentId());
        return getContentAttributeRecursive(locale, parentCategory, attributeName);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
    public List<Category> getChildContent(final long contentId) {
        return getChildContentWithAvailability(contentId, true);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getChildContentWithAvailability(final long contentId, final boolean withAvailability) {
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
    @Cacheable(value = CACHE_NAME)
    public Set<Category> getChildContentRecursive(final long contentId) {
        Set<Category> result = new HashSet<Category>();
        List<Category> categories = getChildContent(contentId);
        result.addAll(categories);
        for (Category category : categories) {
            if (category.getCategoryId() != category.getParentId()) {
                result.addAll(getChildContentRecursive(category.getCategoryId()));
            }
        }
        result.add(getById(contentId));
        return result;
    }

    /**
     * {@inheritDoc} Just to cache
     */
    @Cacheable(value = CACHE_NAME)
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
    public Long getContentIdBySeoUri(final String seoUri) {
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
    public String getSeoUriByContentId(final Long contentId) {
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
    @Cacheable(value = CACHE_NAME)
    public boolean isContentHasSubcontent(final long topContentId, final long subContentId) {
        final Category start = getById(subContentId);
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
        if (cat.getParentId() != cat.getCategoryId()) {
            final Category parent = getById(cat.getParentId());
            if (parent != null) {
                categoryChain.add(parent);
                if (parent.getCategoryId() != categoryIdStopAt) {
                    addParent(categoryChain, categoryIdStopAt);
                }
            }
        }
    }


}
