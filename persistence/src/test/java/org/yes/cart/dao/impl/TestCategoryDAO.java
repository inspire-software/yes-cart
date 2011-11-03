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

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class TestCategoryDAO extends AbstractTestDAO {
    private GenericDAO<Category, Long> categoryDao;
    private GenericDAO<Shop, Long> shopDao;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        shopDao = (GenericDAO<Shop, Long>) ctx.getBean(DaoServiceBeanKeys.SHOP_DAO);
        categoryDao = (GenericDAO<Category, Long>) ctx.getBean(DaoServiceBeanKeys.CATEGORY_DAO);
    }

    public void cleanUp() {
        // nothing to do
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
        Shop shop = shopDao.findSingleByNamedQuery("SHOP.BY.URL", "gadget.npa.com");
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
     * Test, that available from and available to work correctrly
     */
    public void availableCriteriaTest() {
        Shop shop = shopDao.findSingleByNamedQuery("SHOP.BY.URL", "gadget.npa.com");
        assertNotNull("Shop must be resolved by URL", shop);
        List<Category> assignedCategories =
                categoryDao.findByNamedQuery("TOPCATEGORIES.BY.SHOPID", shop.getShopId(), new Date());
        Date date = new Date();

        for (Category category : assignedCategories) {
            assertTrue((category.getAvailablefrom() == null) || (category.getAvailablefrom().getTime() > date.getTime()));
            assertTrue((category.getAvailabletill() == null) || (category.getAvailabletill().getTime() < date.getTime()));
        }

    }


}
