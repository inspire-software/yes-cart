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
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.util.DomainApiUtils;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CategoryServiceImpl extends BaseGenericServiceImpl<Category> implements CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final GenericDAO<Category, Long> categoryDao;

    /**
     * Construct service to manage categories
     *
     * @param categoryDao     category dao to use
     */
    public CategoryServiceImpl(final GenericDAO<Category, Long> categoryDao) {
        super(categoryDao);
        this.categoryDao = categoryDao;
    }


    /**
     * {@inheritDoc}
     */
    public Category getRootCategory() {
        return categoryDao.findSingleByNamedQuery("ROOTCATEGORY");
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> getCategoryLinks(final long categoryId) {
        return (List) categoryDao.findQueryObjectByNamedQuery("LINKED.CATEGORY.IDS.BY.ID", categoryId);
    }

    /**
     * {@inheritDoc}
     */
    public String getCategoryTemplate(final long categoryId) {
        final Category category = proxy().findById(categoryId);
        if (category != null && !category.isRoot()) {
            if (StringUtils.isBlank(category.getUitemplate())) {
                return proxy().getCategoryTemplate(category.getParentId());
            } else {
                return category.getUitemplate();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getCategorySearchTemplate(final long categoryId) {
        final Category category = proxy().findById(categoryId);
        if (category != null && !category.isRoot()) {

            final String template = category.getProductType() != null ? category.getProductType().getUisearchtemplate() : null;

            if (StringUtils.isBlank(template)) {
                return proxy().getCategorySearchTemplate(category.getParentId());
            } else {
                return template;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Long getCategoryProductTypeId(final long categoryId) {
        final Category category = proxy().findById(categoryId);
        if (category != null && !category.isRoot()) {
            if (category.getProductType() == null) {
                return proxy().getCategoryProductTypeId(category.getParentId());
            } else {
                return category.getProductType().getProducttypeId();
            }
        }
        return null;
    }

    /**
     * Get the value of given attribute. If value not present in given category
     * failover to parent category will be used.
     *
     *
     * @param locale        locale for localisable value (or null for raw)
     * @param categoryId    given category
     * @param attributeName attribute name
     * @param defaultValue  default value will be returned if value not found in hierarchy
     * @return value of given attribute name or defaultValue if value not found in category hierarchy
     */
    public String getCategoryAttributeRecursive(final String locale, final long categoryId, final String attributeName, final String defaultValue) {

        final Category category = proxy().getById(categoryId);

        if (category == null || attributeName == null || category.isRoot()) {
            return defaultValue;
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

        return proxy().getCategoryAttributeRecursive(locale, category.getParentId(), attributeName, defaultValue);

    }

    /**
     * Get the values of given attributes. If value not present in given category
     * failover to parent category will be used.  In case if attribute value for first
     * attribute will be found, the rest values also will be collected form the same category.
     *
     *
     * @param locale           locale for localisable value (or null for raw)
     * @param categoryId       given category
     * @param attributeNames set of attributes, to collect values.
     * @return value of given attribute name or defaultValue if value not found in category hierarchy
     */
    public String[] getCategoryAttributeRecursive(final String locale, final long categoryId, final String[] attributeNames) {

        final Category category;

        if (categoryId > 0L && attributeNames != null && attributeNames.length > 0) {
            category = proxy().getById(categoryId);
        } else {
            return null;
        }

        if (category == null) {
            return null;
        }

        final String[] rez = new String[attributeNames.length];
        boolean hasValue = false;
        for (int i = 0; i < attributeNames.length; i++) {
            final String attributeName = attributeNames[i];
            final String val = proxy().getCategoryAttributeRecursive(locale, categoryId, attributeName, null);
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
    public boolean isCategoryHasChildren(final long categoryId) {
        final Category category = proxy().getById(categoryId);
        if (category != null) {
            final long id = category.getLinkToId() != null ? category.getLinkToId() : categoryId;
            List<Object> count = categoryDao.findQueryObjectByNamedQuery("CATEGORY.SUBCATEGORY.COUNT", id);
            if (count != null && count.size() == 1) {
                int qty = ((Number) count.get(0)).intValue();
                if (qty > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getChildCategories(final long categoryId) {

        final Category current = proxy().getById(categoryId);
        if (current != null) {
            if (current.getLinkToId() != null) {
                return findChildCategoriesWithAvailability(current.getLinkToId(), true);
            }

            return findChildCategoriesWithAvailability(categoryId, true);
        }
        return new ArrayList<Category>(0);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findChildCategoriesWithAvailability(final long categoryId, final boolean withAvailability) {

        final List<Category> cats = new ArrayList<Category>(categoryDao.findByNamedQuery(
                "CATEGORIES.BY.PARENTID.WITHOUT.DATE.FILTERING",
                categoryId
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
    public Set<Category> getChildCategoriesRecursive(final long categoryId) {
        final Category thisCat = proxy().getById(categoryId);
        if (thisCat != null) {
            final Set<Category> all = new HashSet<Category>();
            all.add(thisCat);
            loadChildCategoriesRecursiveInternal(all, thisCat);
            return all;
        }
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> getChildCategoriesRecursiveIds(final long categoryId) {
        final Set<Category> cats = proxy().getChildCategoriesRecursive(categoryId);
        if (cats.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(transform(cats));
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> getChildCategoriesRecursiveIdsWithLinks(final long categoryId) {
        final Set<Category> cats = proxy().getChildCategoriesRecursive(categoryId);
        if (cats.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(transformWithLinks(cats));
    }

    private void loadChildCategoriesRecursiveInternal(final Set<Category> result, final Category category) {
        List<Category> categories = proxy().getChildCategories(category.getCategoryId());
        result.addAll(categories);
        for (Category subCategory : categories) {
            loadChildCategoriesRecursiveInternal(result, subCategory);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> findBy(final String code, final String name, final String uri, final int page, final int pageSize) {

        final String codeP = StringUtils.isNotBlank(code) ? "%" + code.toLowerCase() + "%" : null;
        final String nameP = StringUtils.isNotBlank(name) ? "%" + name.toLowerCase() + "%" : null;
        final String uriP = StringUtils.isNotBlank(uri) ? "%" + uri.toLowerCase() + "%" : null;

        final Category root = proxy().getRootCategory();
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
            if (category.isRoot()) {
                catsIt.remove();
            } else {
                final long currentCatId = category.getCategoryId();
                while (category.getParentId() != root.getCategoryId()) {
                    if (category.isRoot()) {
                        // if this is root and not category root matches then this is content
                        catsIt.remove();
                        break;
                    }
                    category = proxy().findById(category.getParentId());
                    if (category == null) {
                        // could have happened if import created some reassignments and we loose path to root
                        catsIt.remove();
                        LOG.warn("Found orphan category {}", currentCatId);
                        break;
                    }
                }
            }
        }
        return cats;
    }



    /**
     * {@inheritDoc}
     */
    public List<Long> getCategoryIdsWithLinks(final long categoryId) {
        final Category category = proxy().getById(categoryId);
        if (category != null) {
            if (category.getLinkToId() != null) {
                return Arrays.asList(categoryId, category.getLinkToId());
            } else {
                return Collections.singletonList(categoryId);
            }

        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCategoryHasSubcategory(final long topCategoryId, final long subCategoryId) {
        final Category start = proxy().getById(subCategoryId);
        if (start != null) {
            if (subCategoryId == topCategoryId) {
                return true;
            } else {
                final List<Category> list = new ArrayList<Category>();
                list.add(start);
                addParent(list, topCategoryId);
                return list.get(list.size() - 1).getCategoryId() == topCategoryId;
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
     * {@inheritDoc} Just to cache
     */
    public Category getById(final long pk) {
        final Category cat = getGenericDao().findById(pk);
        Hibernate.initialize(cat);
        return cat;
    }

    private List<Long> transform(final Collection<Category> categories) {
        final Set<Long> result = new LinkedHashSet<Long>(categories.size());
        for (Category category : categories) {
            result.add(category.getCategoryId());
        }
        return new ArrayList<Long>(result);
    }

    private List<Long> transformWithLinks(final Collection<Category> categories) {
        final Set<Long> result = new LinkedHashSet<Long>(categories.size());
        for (Category category : categories) {
            result.add(category.getCategoryId());
            if (category.getLinkToId() != null) {
                result.add(category.getLinkToId());
            }
        }
        return new ArrayList<Long>(result);
    }

    /**
     * {@inheritDoc}
     */
    public Long findCategoryIdBySeoUri(final String seoUri) {
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
    public Long findCategoryIdByGUID(final String guid) {
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
    public String findSeoUriByCategoryId(final Long categoryId) {
        List<Object> list = categoryDao.findQueryObjectByNamedQuery("SEO.URI.BY.CATEGORY.ID", categoryId);
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
    public Category findCategoryIdBySeoUriOrGuid(final String seoUriOrGuid) {

        final Category category = getGenericDao().findSingleByNamedQuery("CATEGORY.BY.SEO.URI", seoUriOrGuid);
        if (category == null) {
            return getGenericDao().findSingleByNamedQuery("CATEGORY.BY.GUID", seoUriOrGuid);
        }
        return category;
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> findByProductId(final long productId) {
        return categoryDao.findByNamedQuery(
                "CATEGORIES.BY.PRODUCTID",
                productId
        );
    }

    private CategoryService proxy;

    private CategoryService proxy() {
        if (proxy == null) {
            proxy = getSelf();
        }
        return proxy;
    }

    /**
     * @return self proxy to reuse AOP caching
     */
    public CategoryService getSelf() {
        // Spring lookup method to get self proxy
        return null;
    }

}
