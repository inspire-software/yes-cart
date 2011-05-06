
package org.yes.cart.domain.entity;


/**
 * Single shop can serve several urls, for example shop.com.ru, www.shop.com.ru, wap.shop.com.ru.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopUrl extends Auditable {

    /**
     * @return primary key value.
     */
    public long getStoreUrlId();

    public void setStoreUrlId(long storeUrlId);

    /**
     * @return shop url.
     */
    public String getUrl();

    public void setUrl(String url);

    /**
     * @return {@link Shop}
     */
    public Shop getShop();

    public void setShop(Shop shop);

}


