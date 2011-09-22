package org.yes.cart.dao.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopUrl;
import org.yes.cart.domain.entity.impl.ShopEntity;
import org.yes.cart.domain.entity.impl.ShopUrlEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class TestShopDAO extends AbstractTestDAO {

    private GenericDAO<Shop,Long> shopDao;
    private Set<Long> cleanupPks = new HashSet<Long>();
    private static final String URL1 = "www.shop1.npa.com";
    private static final String URL2 = "shop1.npa.com";

    @Before
    public void setUp() throws Exception {
        super.setUp();

        shopDao = (GenericDAO<Shop,Long>) ctx.getBean(DaoServiceBeanKeys.SHOP_DAO);

    }

    @After
    public void tearDown() {
        super.tearDown();

        shopDao = null;

    }

    @Test
    public void testShopDao() {
        Shop shop = new ShopEntity();
        shop.setCode("TESTSHOP");
        shop.setName("test shop");
        shop.setDescription("test shop description");
        shop.setFspointer("/npa/data");
        shop.setImageVaultFolder("/npa/data/imgvault");

        ShopUrl url;
        url = new ShopUrlEntity();
        url.setUrl(URL1);
        url.setShop(shop);
        shop.getShopUrl().add(url);
        url = new ShopUrlEntity ();
        url.setUrl(URL2);
        url.setShop(shop);
        shop.getShopUrl().add(url);

        shop = shopDao.create(shop);

        assertNotNull(shop);
        
        assertEquals(2, shop.getShopUrl().size());


        cleanupPks.add(shop.getShopId());

        updateShop();

        resolveShopByURL();

        cleanUp();
    }


    public void updateShop() {
        for (Long pk : cleanupPks) {
            Shop shop = shopDao.findById(pk);
            assertNotNull(shop);
            String newName = shop.getName() + "1";
            shop.setName(newName);
            shopDao.update(shop);
            shop = shopDao.findById(pk);
            assertEquals(newName,shop.getName());
        }
    }

    /**
     * Test, that we are able resolve shop by his domain name
     */
    public void resolveShopByURL() {

        List <Shop> shopList0= shopDao.findAll();

        List <Shop> shopList = shopDao.findByNamedQuery(
                "SHOP.BY.URL", "gadget.npa.com"  );

        assertEquals(1, shopList.size());

        shopList = shopDao.findByNamedQuery(
                "SHOP.BY.URL",  URL2 );

        assertEquals(1, shopList.size());

    }

    public void cleanUp() {
        for (Long pk : cleanupPks) {
            shopDao.delete(shopDao.findById(pk));
        }

        for (Long pk : cleanupPks) {
            assertNull(shopDao.findById(pk));
        }

    }


}
