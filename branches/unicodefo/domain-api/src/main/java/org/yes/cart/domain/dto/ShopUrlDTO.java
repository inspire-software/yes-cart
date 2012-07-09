package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * Shop URLs DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopUrlDTO extends Identifiable {

    /**
     * @return primary key value.
     */
    long getStoreUrlId();

    void setStoreUrlId(long storeUrlId);

    /**
     * @return {@link org.yes.cart.domain.entity.Shop}
     */
    long getShopId();

    void setShopId(long shopId);


    /**
     * @return shop url.
     */
    String getUrl();

    void setUrl(String url);


}
