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

package org.yes.cart.dao.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class CategoryDAOTest extends AbstractTestDAO {

    private GenericDAO<Category, Long> categoryDao;
    private GenericDAO<Shop, Long> shopDao;

    @Before
    public void setUp() throws Exception {
        shopDao = (GenericDAO<Shop, Long>) ctx().getBean(DaoServiceBeanKeys.SHOP_DAO);
        categoryDao = (GenericDAO<Category, Long>) ctx().getBean(DaoServiceBeanKeys.CATEGORY_DAO);
    }

    @Test
    public void testCategoryDAO() {
        resovleCategoriesbyShop();
        availableCriteriaTest();
    }

    /**
     * Test, that we can resolve categories by shop
     * and thea are ranked.
     */
    public void resovleCategoriesbyShop() {
        Shop shop = shopDao.findSingleByNamedQuery("SHOP.BY.URL", "gadget.yescart.org");
        assertNotNull("Shop must be resolved by URL", shop);
        List<Category> assignedCategories =
                categoryDao.findByNamedQuery("TOPCATEGORIES.BY.SHOPID", shop.getShopId(), new Date());
        assertNotNull("Test shop must have assigned categories", assignedCategories);
        assertFalse("Assigned categories not empty", assignedCategories.isEmpty());
        int currentRank = Integer.MIN_VALUE;
        Iterator<ShopCategory> shopCategoryIterator = shop.getShopCategory().iterator();
        Iterator<Category> categoryIterator = assignedCategories.iterator();
        // because categories can be out of available scope
        // assertTrue(assignedCategories.size() == shop.getShopCategory().size());
        List<Long> allAssignedCategories = new ArrayList<Long>();
        for (ShopCategory allShopCat : shop.getShopCategory()) {
            allAssignedCategories.add(allShopCat.getCategory().getCategoryId());
        }
        List<Long> allAssignedAvailableCategories = new ArrayList<Long>();
        for (Category allShopCat : assignedCategories) {
            allAssignedAvailableCategories.add(allShopCat.getCategoryId());
        }
        assertTrue(allAssignedCategories.containsAll(allAssignedAvailableCategories));
        while (shopCategoryIterator.hasNext()) {
            ShopCategory sc = shopCategoryIterator.next();
            assertTrue("Assigned category is ranked ", currentRank <= sc.getRank());
            currentRank = sc.getRank();
        }
    }

    /**
     * Test, that available from and available to work correctly
     */
    public void availableCriteriaTest() {
        Shop shop = shopDao.findSingleByNamedQuery("SHOP.BY.URL", "gadget.yescart.org");
        assertNotNull("Shop must be resolved by URL", shop);
        List<Category> assignedCategories =
                categoryDao.findByNamedQuery("TOPCATEGORIES.BY.SHOPID", shop.getShopId(), new Date());
        Date date = new Date();
        for (Category category : assignedCategories) {
            assertTrue((category.getAvailablefrom() == null) || (category.getAvailablefrom().getTime() > date.getTime()));
            assertTrue((category.getAvailableto() == null) || (category.getAvailableto().getTime() < date.getTime()));
        }
    }
}
