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
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.util.DomainApiUtils;
import org.yes.cart.util.TimeContext;
import org.yes.cart.utils.HQLUtils;

import java.time.LocalDateTime;
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
    @Override
    public Category getRootCategory() {
        return categoryDao.findSingleByNamedQuery("ROOTCATEGORY");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getCategoryLinks(final long categoryId) {
        return (List) categoryDao.findQueryObjectByNamedQuery("LINKED.CATEGORY.IDS.BY.ID", categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
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
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public boolean isCategoryHasChildren(final long categoryId) {
        final List<Long> id = getCategoryIdAndLinkId(categoryId);
        if (id != null) {
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
    @Override
    public List<Category> getChildCategories(final long categoryId) {

        final Category current = proxy().getById(categoryId);
        if (current != null) {
            if (current.getLinkToId() != null) {
                final List<Category> cats = new ArrayList<>();
                cats.addAll(findChildCategoriesWithAvailability(current.getLinkToId(), true));
                cats.addAll(findChildCategoriesWithAvailability(categoryId, true));
                return cats;
            }

            return findChildCategoriesWithAvailability(categoryId, true);
        }
        return new ArrayList<>(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> findChildCategoriesWithAvailability(final long categoryId, final boolean withAvailability) {

        final List<Category> cats = new ArrayList<>(categoryDao.findByNamedQuery(
                "CATEGORIES.BY.PARENTID.WITHOUT.DATE.FILTERING",
                categoryId
        ));
        if (withAvailability) {

            final LocalDateTime now = now();
            cats.removeIf(cat -> !DomainApiUtils.isObjectAvailableNow(!cat.isDisabled(), cat.getAvailablefrom(), cat.getAvailableto(), now));

        }
        return cats;

    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Category> getChildCategoriesRecursive(final long categoryId) {
        final Category thisCat = proxy().getById(categoryId);
        if (thisCat != null) {
            final Set<Category> all = new HashSet<>();
            all.add(thisCat);
            loadChildCategoriesRecursiveInternal(all, thisCat);
            return all;
        }
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getChildCategoriesRecursiveIds(final long categoryId) {
        final Set<Category> cats = proxy().getChildCategoriesRecursive(categoryId);
        if (cats.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(transformToId(cats));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getChildCategoriesRecursiveIdsAndLinkIds(final long categoryId) {
        final Set<Category> cats = proxy().getChildCategoriesRecursive(categoryId);
        if (cats.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(transformToIdAndLinkId(cats));
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

        final String codeP = HQLUtils.criteriaIlikeAnywhere(code);
        final String nameP = HQLUtils.criteriaIlikeAnywhere(name);
        final String uriP = HQLUtils.criteriaIlikeAnywhere(uri);

        final Category root = proxy().getRootCategory();
        List<Category> cats;
        if ((codeP != null || nameP != null) && uriP != null) {
            cats = getGenericDao().findRangeByNamedQuery("CATEGORIES.BY.CODE.NAME.URI", page * pageSize, pageSize, codeP, nameP, uriP);
        } else if (codeP == null && nameP == null && uriP != null) {
            cats = getGenericDao().findRangeByNamedQuery("CATEGORIES.BY.URI", page * pageSize, pageSize, uriP);
        } else {
            cats = getGenericDao().findRangeByCriteria(null, page * pageSize, pageSize);
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
    @Override
    public List<Long> getCategoryIdAndLinkId(final long categoryId) {
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
    @Override
    public boolean isCategoryHasSubcategory(final long topCategoryId, final long subCategoryId) {
        final Category start = proxy().getById(subCategoryId);
        if (start != null) {
            if (subCategoryId == topCategoryId) {
                return true;
            } else {
                final List<Category> list = new ArrayList<>();
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
    @Override
    public Category getById(final long pk) {
        final Category cat = getGenericDao().findById(pk);
        Hibernate.initialize(cat);
        return cat;
    }

    private List<Long> transformToId(final Collection<Category> categories) {
        final Set<Long> result = new LinkedHashSet<>(categories.size());
        for (Category category : categories) {
            result.add(category.getCategoryId());
        }
        return new ArrayList<>(result);
    }

    private List<Long> transformToIdAndLinkId(final Collection<Category> categories) {
        final Set<Long> result = new LinkedHashSet<>(categories.size());
        for (Category category : categories) {
            result.add(category.getCategoryId());
            if (category.getLinkToId() != null) {
                result.add(category.getLinkToId());
            }
        }
        return new ArrayList<>(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
