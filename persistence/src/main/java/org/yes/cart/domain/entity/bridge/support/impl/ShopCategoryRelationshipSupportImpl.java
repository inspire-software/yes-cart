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

package org.yes.cart.domain.entity.bridge.support.impl;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.CategoryRelationDTO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.search.dao.support.ShopCategoryRelationshipSupport;
import org.yes.cart.util.log.Markers;

import java.util.*;

/**
 * Extra logic to determine relationship between categories and shops.
 *
 * User: denispavlov
 * Date: 13-10-01
 * Time: 1:55 PM
 */
public class ShopCategoryRelationshipSupportImpl implements ShopCategoryRelationshipSupport {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCategoryRelationshipSupportImpl.class);

    private final GenericDAO<Shop, Long> shopDao;
    private final GenericDAO<Category, Long> categoryDao;

    public ShopCategoryRelationshipSupportImpl(final GenericDAO<Shop, Long> shopDao,
                                               final GenericDAO<Category, Long> categoryDao) {
        this.shopDao = shopDao;
        this.categoryDao = categoryDao;
    }

    /** {@inheritDoc} */
    @Override
    public List<Shop> getAll() {
        return this.shopDao.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Category getCategoryById(final long pk) {
        final Category cat = categoryDao.findById(pk);
        if (cat != null) {
            Hibernate.initialize(cat);
        }
        return cat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryRelationDTO getCategoryRelationById(final long categoryId) {
        final List rez = categoryDao.findQueryObjectByNamedQuery("CATEGORYRELATION.BY.ID", categoryId);
        if (CollectionUtils.isNotEmpty(rez)) {
            return (CategoryRelationDTO) rez.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> getCategoryParentsIds(final long categoryId) {
        final CategoryRelationDTO category = proxy().getCategoryRelationById(categoryId);
        final Set<Long> parents = new HashSet<>();
        if (category != null && !category.isRoot()) {
            parents.addAll(proxy().getCategoryLinkedIds(category.getCategoryId()));
            final CategoryRelationDTO parent = proxy().getCategoryRelationById(category.getParentId());
            if (parent != null && !parent.isRoot()) {
                parents.add(category.getParentId());
                parents.addAll(proxy().getCategoryLinkedIds(category.getParentId()));
            }

        }
        return Collections.unmodifiableSet(parents);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> getCategoryLinkedIds(long categoryId) {
        return Collections.unmodifiableSet(new HashSet(categoryDao.findQueryObjectByNamedQuery("LINKED.CATEGORY.IDS.BY.ID", categoryId)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, Set<Long>> getAllCategoriesIdsMap() {
        return loadCategoryMapping();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> getShopCategoriesIds(final long shopId) {

        final Set<Long> result = new HashSet<>();

        final Map<Long, Set<Long>> map = proxy().getAllCategoriesIdsMap();

        final Shop shop = shopDao.findById(shopId);

        if (shop != null && shop.getShopCategory() != null) {

            for (final ShopCategory shopCategory : shop.getShopCategory()) {

                appendChildren(result, shopCategory.getCategory().getCategoryId(), map);

            }

        } else {
            LOG.warn(Markers.alert(), "Attempted to get shop categories for shop {} but it either does not exist or has null categories", shopId);
        }

        return Collections.unmodifiableSet(result);

    }

    private void appendChildren(final Set<Long> result, final long currentId, final Map<Long, Set<Long>> map) {

        result.add(currentId);

        final Set<Long> immediateChildren = map.get(currentId);
        if (CollectionUtils.isNotEmpty(immediateChildren)) {
            for (final Long child : immediateChildren) {
                appendChildren(result, child, map);
            }
        }

    }

    Map<Long, Set<Long>> loadCategoryMapping() {

        final List<Object[]> idParentLinkList = (List) this.categoryDao.findQueryObjectByNamedQuery("CATEGORY.PARENT.LINK.ALL");

        final Map<Long, Set<Long>> all = new HashMap<>(idParentLinkList.size() + 100);
        final List<Object[]> idParentLinkList2 = new ArrayList<>(idParentLinkList.size() > 100 ? 10 : idParentLinkList.size() / 10);

        for (final Object[] idParentLink : idParentLinkList) {

            final Long id = (Long) idParentLink[0];
            final Long parent = (Long) idParentLink[1];
            final Long link = (Long) idParentLink[2];

            if (parent > 0L && !id.equals(parent)) {

                final Set<Long> children = all.computeIfAbsent(parent, k -> new HashSet<>());
                children.add(id);
                if (link != null) {
                    children.add(link); // Add linked category as a linked child
                }

            }

            if (link != null) {

                idParentLinkList2.add(idParentLink); // save for second pass

            }
        }

        for (final Object[] idParentLink : idParentLinkList2) {

            final Long id = (Long) idParentLink[0];
            final Long parent = (Long) idParentLink[1];
            final Long link = (Long) idParentLink[2];

            Set<Long> linkedChildren = all.get(link);
            if (linkedChildren != null) {
                all.put(id, linkedChildren);
            }

        }

        for (final Map.Entry<Long, Set<Long>> entry : all.entrySet()) {

            all.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));

        }

        return Collections.unmodifiableMap(all);
    }


    private ShopCategoryRelationshipSupport proxy;

    private ShopCategoryRelationshipSupport proxy() {
        if (proxy == null) {
            proxy = getSelf();
        }
        return proxy;
    }

    /**
     * Spring IoC.
     *
     * @return self
     */
    public ShopCategoryRelationshipSupport getSelf() {
        return null;
    }

}
