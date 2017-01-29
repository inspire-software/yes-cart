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

import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.service.domain.ShopCategoryService;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ShopCategoryServiceImpl extends BaseGenericServiceImpl<ShopCategory> implements ShopCategoryService {

    private final GenericDAO<ShopCategory, Long> shopCategoryDao;

    private final GenericDAO<Category, Long> categoryDao;

    private final GenericDAO<Shop, Long> shopDao;

    /**
     * Construct service to manage categories
     *
     * @param categoryDao     category dao to use
     * @param shopCategoryDao shop category dao to use
     * @param shopDao         shop dao
     */
    public ShopCategoryServiceImpl(final GenericDAO<ShopCategory, Long> shopCategoryDao,
                                   final GenericDAO<Category, Long> categoryDao,
                                   final GenericDAO<Shop, Long> shopDao) {
        super(shopCategoryDao);
        this.shopCategoryDao = shopCategoryDao;
        this.categoryDao = categoryDao;
        this.shopDao = shopDao;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteAll(final Category category) {
        final Collection<ShopCategory> shopCategories = shopCategoryDao.findByCriteria(
                Restrictions.eq("category", category));
        for (ShopCategory shopCategory : shopCategories) {
            shopCategoryDao.delete(shopCategory);
        }        
    }

    /**
     * {@inheritDoc}
     */
    public ShopCategory findByShopCategory(final Shop shop, final Category category) {
        return shopCategoryDao.findSingleByCriteria(
                Restrictions.eq("category", category),
                Restrictions.eq("shop", shop)
        );
    }



    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "categoryService-topLevelCategories",
            "categoryService-currentCategoryMenu",
            "breadCrumbBuilder-breadCrumbs",
            "shopService-shopByCode",
            "shopService-shopById",
            "shopService-shopByDomainName",
            "shopService-allShops",
            "shopService-allShopsMap",
            "shopService-allShopsFulfilmentMap",
            "shopService-allNonSubShops",
            "shopService-subShopsByMaster",
            "shopService-shopCategoriesIds",
            "shopService-shopAllCategoriesIds",
            "categoryService-searchCategoriesIds",
            "categoryService-categoryNewArrivalLimit",
            "categoryService-categoryNewArrivalDate"
    }, allEntries = true)
    public ShopCategory assignToShop(final long categoryId, final long shopId) {
        final ShopCategory shopCategory = shopCategoryDao.getEntityFactory().getByIface(ShopCategory.class);
        shopCategory.setCategory(categoryDao.findById(categoryId));
        shopCategory.setShop(shopDao.findById(shopId));
        return shopCategoryDao.create(shopCategory);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "categoryService-topLevelCategories",
            "categoryService-currentCategoryMenu",
            "breadCrumbBuilder-breadCrumbs",
            "shopService-shopByCode",
            "shopService-shopById",
            "shopService-shopByDomainName",
            "shopService-allShops",
            "shopService-allShopsMap",
            "shopService-allShopsFulfilmentMap",
            "shopService-allNonSubShops",
            "shopService-subShopsByMaster",
            "shopService-shopCategoriesIds",
            "shopService-shopAllCategoriesIds",
            "categoryService-searchCategoriesIds",
            "categoryService-categoryNewArrivalLimit",
            "categoryService-categoryNewArrivalDate"
    }, allEntries = true)
    public void unassignFromShop(final long categoryId, final long shopId) {
        ShopCategory shopCategory = shopCategoryDao.findSingleByNamedQuery(
                "SHOP.CATEGORY",
                categoryId,
                shopId);
        shopCategoryDao.delete(shopCategory);

    }


}
