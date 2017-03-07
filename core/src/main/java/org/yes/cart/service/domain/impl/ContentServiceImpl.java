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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ContentServiceTemplateSupport;
import org.yes.cart.util.DomainApiUtils;

import java.util.*;

/**
 * User: Denis Pavlov
 * Date: 15-June-2013
 */
public class ContentServiceImpl extends BaseGenericServiceImpl<Category> implements ContentService {

    private static final Logger LOG = LoggerFactory.getLogger(ContentServiceImpl.class);

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
    public Category getRootContent(final long shopId) {
        if (shopId <= 0) {
            throw new IllegalArgumentException("Shop must not be null or transient");
        }
        return categoryDao.findSingleByNamedQuery("ROOTCONTENT.BY.SHOP.ID", shopId);
    }

    /**
     * {@inheritDoc}
     */
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
    public String getContentTemplate(final long contentId) {
        final Category content = proxy().findById(contentId);
        if (content != null && !content.isRoot()) {
            if (StringUtils.isBlank(content.getUitemplate())) {
                return proxy().getContentTemplate(content.getParentId());
            } else {
                return content.getUitemplate();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
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
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public String getContentBody(final String contentUri, final String locale) {
        final Long id = findContentIdBySeoUri(contentUri);
        if (id != null) {
            return proxy().getContentBody(id, locale);
        }
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public String getDynamicContentBody(final long contentId, final String locale, final Map<String, Object> context) {

        final String rawContent = proxy().getContentBody(contentId, locale);

        if (StringUtils.isNotBlank(rawContent)) {

            return this.templateSupport.processTemplate(rawContent, locale, context);

        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    public String getDynamicContentBody(final String contentUri, final String locale, final Map<String, Object> context) {

        final String rawContent = proxy().getContentBody(contentUri, locale);

        if (StringUtils.isNotBlank(rawContent)) {

            return this.templateSupport.processTemplate(rawContent, locale, context);

        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    public String getContentAttributeRecursive(final String locale, final long contentId, final String attributeName, final String defaultValue) {

        final Category content = proxy().getById(contentId);

        if (content == null || attributeName == null || content.isRoot()) {
            return defaultValue;
        }

        final AttrValue attrValue = content.getAttributeByCode(attributeName);
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

        return proxy().getContentAttributeRecursive(locale, content.getParentId(), attributeName, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getContentAttributeRecursive(final String locale, final long contentId, final String[] attributeNames) {

        final Category content;

        if (contentId > 0L && attributeNames != null && attributeNames.length > 0) {
            content = proxy().getById(contentId);
        } else {
            return null;
        }

        if (content == null) {
            return null;
        }

        final String[] rez = new String[attributeNames.length];
        boolean hasValue = false;
        for (int i = 0; i < attributeNames.length; i++) {
            final String attributeName = attributeNames[i];
            final String val = proxy().getContentAttributeRecursive(locale, contentId, attributeName, null);
            if (val != null) {
                hasValue = true;
            }
            rez[i] = val;
        }

        if (hasValue) {
            return rez;
        }
        return null;

    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getChildContent(final long contentId) {
        return findChildContentWithAvailability(contentId, true);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findChildContentWithAvailability(final long contentId, final boolean withAvailability) {

        final List<Category> cats = new ArrayList<Category>(categoryDao.findByNamedQuery(
                "CATEGORIES.BY.PARENTID.WITHOUT.DATE.FILTERING",
                contentId
        ));
        if (withAvailability) {

            final Date now = new Date();
            final Iterator<Category> it = cats.iterator();
            while (it.hasNext()) {

                final Category cat = it.next();

                if (!DomainApiUtils.isObjectAvailableNow(true, cat.getAvailablefrom(), cat.getAvailableto(), now)) {

                    it.remove();

                }

            }

        }
        return cats;

    }


    /**
     * {@inheritDoc}
     */
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
     * {@inheritDoc}
     */
    public List<Category> findBy(final long shopId, final String code, final String name, final String uri, final int page, final int pageSize) {

        final String codeP = StringUtils.isNotBlank(code) ? "%" + code.toLowerCase() + "%" : null;
        final String nameP = StringUtils.isNotBlank(name) ? "%" + name.toLowerCase() + "%" : null;
        final String uriP = StringUtils.isNotBlank(uri) ? "%" + uri.toLowerCase() + "%" : null;

        final Category root = proxy().getRootContent(shopId);
        List<Category> cats = new ArrayList<Category>();
        if ((codeP != null || nameP != null) && uriP != null) {
            cats = getGenericDao().findRangeByNamedQuery("CATEGORIES.BY.CODE.NAME.URI", page * pageSize, pageSize, codeP, nameP, uriP);
        } else if (codeP == null && nameP == null && uriP != null) {
            cats = getGenericDao().findRangeByNamedQuery("CATEGORIES.BY.URI", page * pageSize, pageSize, uriP);
        } else {
            cats = getGenericDao().findAll();
        }

        final Iterator<Category> catsIt = cats.iterator();
        while (catsIt.hasNext()) {
            Category category = catsIt.next();
            if (category.isRoot() && category.getCategoryId() != root.getCategoryId()) {
                catsIt.remove();
            } else {
                final long currentCatId = category.getCategoryId();
                while (category.getParentId() != root.getCategoryId()) {
                    if (category.isRoot()) {
                        // if this is root and not shop root matches then this is not this shop's content
                        catsIt.remove();
                        break;
                    }
                    category = proxy().findById(category.getParentId());
                    if (category == null) {
                        // could have happened if import created some reassignments and we loose path to root
                        catsIt.remove();
                        LOG.warn("Found orphan content {}", currentCatId);
                        break;
                    }
                }
            }
        }
        return cats;
    }



    /**
     * {@inheritDoc} Just to cache
     */
    public Category getById(final long pk) {
        return getGenericDao().findById(pk);
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
    public Long findContentIdByGUID(final String guid) {
        List<Object> list = categoryDao.findQueryObjectByNamedQuery("CATEGORY.ID.BY.GUID", guid);
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
            final Object[] uriAndId = (Object[]) list.get(0);
            if (uriAndId[0] instanceof String) {
                return (String) uriAndId[0];
            }
            return String.valueOf(uriAndId[1]);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
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
