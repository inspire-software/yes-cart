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

package org.yes.cart.dao.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;

import java.util.ArrayList;
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
    public void setUp()  {
        shopDao = (GenericDAO<Shop, Long>) ctx().getBean(DaoServiceBeanKeys.SHOP_DAO);
        categoryDao = (GenericDAO<Category, Long>) ctx().getBean(DaoServiceBeanKeys.CATEGORY_DAO);
        super.setUp();
    }


    /**
     * Test, that we can resolve categories by shop
     * and thea are ranked.
     */
    @Test
    public void resolveCategoriesbyShop() {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                Shop shop = shopDao.findSingleByNamedQuery("SHOP.BY.URL", "gadget.yescart.org");
                assertNotNull("Shop must be resolved by URL", shop);
                List<ShopCategory> assignedCategories =
                        (List) categoryDao.findQueryObjectByNamedQuery("ALL.TOPCATEGORIES.BY.SHOPID", shop.getShopId());
                assertNotNull("Test shop must have assigned categories", assignedCategories);
                assertFalse("Assigned categories not empty", assignedCategories.isEmpty());
                int currentRank = Integer.MIN_VALUE;
                Iterator<ShopCategory> shopCategoryIterator = shop.getShopCategory().iterator();
                Iterator<ShopCategory> categoryIterator = assignedCategories.iterator();
                // because categories can be out of available scope
                // assertTrue(assignedCategories.size() == shop.getShopCategory().size());
                List<Long> allAssignedCategories = new ArrayList<Long>();
                for (ShopCategory allShopCat : shop.getShopCategory()) {
                    allAssignedCategories.add(allShopCat.getShopCategoryId());
                }
                List<Long> allAssignedAvailableCategories = new ArrayList<Long>();
                for (ShopCategory allShopCat : assignedCategories) {
                    allAssignedAvailableCategories.add(allShopCat.getShopCategoryId());
                }
                assertTrue(allAssignedCategories.containsAll(allAssignedAvailableCategories));

                status.setRollbackOnly();

            }
        });


    }

}
