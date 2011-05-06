package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;

import java.util.Set;
import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 *
 */
public interface ShopService extends AttributeManageGenericService<Shop>{

    /**
     * Get the {@link Shop} by given server name.
     * @param serverName the server name
     * @return instance of Shop if it has given server name, otherwise null
     */
    Shop getShopByDomainName(String serverName);

    /**
     * Get the {@link Shop} by given order guid.
     * @param orderGuid the guid of order
     * @return instance of Shop if it has given server name, otherwise null
     */
    Shop getShopByOrderGuid(String orderGuid);

    /**
     * Get the {@link Shop} by given order number.
     * @param orderNum given order number
     * @return instance of Shop if it has given server name, otherwise null
     */
    Shop getShopByOrderNum(String orderNum);


    /**
     * Get all categories including child categories, that belong to given shop.
     * @param shop given shop
     * @return lenear representation of caterory tree
     */
    Set<Category> getShopCategories(Shop shop);

    /**
     * Get all suported currencies by all shops.
     * @return all suported currencies.
     */
    Collection<String> getAllSupportedCurrenciesByShops();

    

}
