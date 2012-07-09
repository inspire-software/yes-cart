package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Restrictions;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopCategoryService;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ShopCategoryServiceImpl extends BaseGenericServiceImpl<ShopCategory> implements ShopCategoryService {

    private final GenericDAO<ShopCategory, Long> shopCategoryDao;

    public ShopCategoryServiceImpl(
            final GenericDAO<ShopCategory, Long> shopCategoryDao) {
        super(shopCategoryDao);
        this.shopCategoryDao = shopCategoryDao;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteAll(final Category caterogy) {
        final Collection<ShopCategory> shopCategories = shopCategoryDao.findByCriteria(
                Restrictions.eq("category",caterogy));
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

}
