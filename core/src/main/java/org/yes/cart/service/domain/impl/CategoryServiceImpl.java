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
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.support.ShopCategoryRelationshipSupport;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.TimeContext;

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

    private final ShopCategoryRelationshipSupport shopCategoryRelationshipSupport;

    /**
     * Construct service to manage categories
     *
     * @param categoryDao     category dao to use
     * @param shopCategoryRelationshipSupport support
     */
    public CategoryServiceImpl(final GenericDAO<Category, Long> categoryDao,
                               final ShopCategoryRelationshipSupport shopCategoryRelationshipSupport) {
        super(categoryDao);
        this.categoryDao = categoryDao;
        this.shopCategoryRelationshipSupport = shopCategoryRelationshipSupport;
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
            final int qty = categoryDao.findCountByNamedQuery("CATEGORY.SUBCATEGORY.COUNT", id);
            return qty > 0;
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
            cats.removeIf(cat -> !cat.isAvailable(now));

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
    public Set<Long> getAllCategoryIds(final Collection<String> guids) {
        List<Long> list = (List) categoryDao.findQueryObjectByNamedQuery("CATEGORY.ID.BY.GUIDs", guids);
        final Set<Long> all = new HashSet<>();

        list.forEach(branchId -> {
            all.addAll(shopCategoryRelationshipSupport.getCatalogCategoriesIds(branchId));
        });

        return all;
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

        final Set<Long> result = new LinkedHashSet<>(cats.size());
        for (Category category : cats) {
            result.add(category.getCategoryId());
        }
        return Collections.unmodifiableList(new ArrayList<>(result));
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

        final Set<Long> result = new LinkedHashSet<>(cats.size());
        for (Category category : cats) {
            result.add(category.getCategoryId());
            if (category.getLinkToId() != null) {
                result.addAll(getChildCategoriesRecursiveIdsAndLinkIds(category.getLinkToId()));
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(result));
    }

    private void loadChildCategoriesRecursiveInternal(final Set<Category> result, final Category category) {
        List<Category> categories = proxy().getChildCategories(category.getCategoryId());
        result.addAll(categories);
        for (Category subCategory : categories) {
            loadChildCategoriesRecursiveInternal(result, subCategory);
        }
    }


    private Pair<String, Object[]> findCategoryQuery(final boolean count,
                                                     final String sort,
                                                     final boolean sortDescending,
                                                     final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(c.categoryId) from CategoryEntity c ");
        } else {
            hqlCriteria.append("select c from CategoryEntity c ");
        }

        final List categoryIds = currentFilter != null ? currentFilter.remove("categoryIds") : null;
        if (categoryIds != null) {
            hqlCriteria.append(" where (c.categoryId in (?1)) ");
            params.add(categoryIds);
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "c", currentFilter);


        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by c." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }




    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> findCategories(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findCategoryQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCategoryCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findCategoryQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
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
