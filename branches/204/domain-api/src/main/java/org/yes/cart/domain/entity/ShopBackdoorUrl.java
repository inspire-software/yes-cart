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

package org.yes.cart.domain.entity;


/**
 * Back door urls. Must be inaccessible outside DMZ.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 18-May-2013
 * Time: 11:12:54
 */
public interface ShopBackdoorUrl extends Auditable {

    public final static String URL_TYPE_CACHE_DIRECTOR = "cacheDirector";

    public final static String URL_TYPE_REINDEX_DOOR = "reindexDoor";


    /**
     * Get url type.
     * @return url type.
     */
    String getUrlType();

    /**
     * Set url type.
     * @param urlType
     */
    void setUrlType(String urlType);


        /**
        * @return primary key value.
        */
    long getShopBackdoorUrlId();

    /**
     * Set pk value.
     * @param shopBackdoorUrlId pk value.
     */
    void setShopBackdoorUrlId(long shopBackdoorUrlId);

    /**
     * Get back door url.
     * @return shop url.
     */
    String getUrl();

    /**
     * Set url.
     * @param url url
     */
    void setUrl(String url);

    /**
     * @return {@link org.yes.cart.domain.entity.Shop}
     */
    Shop getShop();

    void setShop(Shop shop);

}


