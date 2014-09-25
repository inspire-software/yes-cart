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

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * Shop.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */

public interface Shop extends Auditable, Seoable, Codable {

    /**
     * Get shop code.
     *
     * @return shop code.
     */
    String getCode();

    /**
     * Set shop code.
     *
     * @param code shop code.
     */
    void setCode(String code);


    /**
     * Get pk value.
     *
     * @return pk value.
     */
    long getShopId();

    /**
     * Set pk value.
     *
     * @param shopId shop pk value.
     */
    void setShopId(long shopId);

    /**
     * Get shop name.
     *
     * @return shop name.
     */
    String getName();

    /**
     * Set shop name.
     *
     * @param name shop name.
     */
    void setName(String name);

    /**
     * Get shop description.
     *
     * @return description.
     */
    String getDescription();

    /**
     * Set shop description.
     *
     * @param description shop description
     */
    void setDescription(String description);

    /**
     * Get the path to shop theme. Default failover path will be used,
     * if some resource con not be found in shop theme.
     *
     * @return path to shop theme.
     */
    String getFspointer();

    /**
     * Set shop theme path.
     *
     * @param fspointer path to theme.
     */
    void setFspointer(String fspointer);

    /**
     * Get shop supported urls. Example shop.domain, www.shop.domain, www1.shop.domain, wap.shop.domain.
     *
     * @return list of supported urls.
     */
    Set<ShopUrl> getShopUrl();

    /**
     * Set list of supported urls.
     *
     * @param shopUrl supported urls.
     */
    void setShopUrl(Set<ShopUrl> shopUrl);


    /**
     * Get the currency exchange rates. All exchange rates from default to target currency.
     *
     * @return list of exchange rates.
     */
    Collection<ShopExchangeRate> getExchangerates();

    /**
     * Set currency exchange rates.
     *
     * @param exchangerates exchange rates.
     */
    void setExchangerates(Collection<ShopExchangeRate> exchangerates);

    /**
     * Get the named advertizing places.
     *
     * @return named advertizing places.
     */
    Collection<ShopAdvPlace> getAdvertisingPlaces();

    /**
     * Set named advertizing places.
     *
     * @param advertisingPlaces named advertizing places.
     */
    void setAdvertisingPlaces(Collection<ShopAdvPlace> advertisingPlaces);

    /**
     * Get all  attributes.
     *
     * @return collection of product attributes.
     */
    Collection<AttrValueShop> getAttributes();

    /**
     * Get all  attributes filtered by given attribute code.
     *
     * @param attributeCode code of attribute
     * @return collection of product attributes filtered by attribute name or empty collection if no attribute were found.
     */
    Collection<AttrValueShop> getAttributesByCode(String attributeCode);

    /**
     * Is B2B profile set.
     *
     * @return true is B2B profile is set.
     */
    boolean isB2ProfileActive();

    /**
     * TODO: YC-420 Unify all themes into a separate module
     * TODO: This is standard directory - no need for this getter
     * Get markup folder. @see fspointer for more details.
     * @return folder with markup.
     */
    String getMarkupFolder();


    /**
     * Get default shop url. localhost will never be return.
     * @return  default shop url
     */
    String getDefaultShopUrl();


    /**
     * Get single attribute.
     *
     * @param attributeCode code of attribute
     * @return single {@link AttrValue} or null if not found.
     */
    AttrValueShop getAttributeByCode(String attributeCode);


    /**
     * Set collection of  attributes.
     *
     * @param attribute collection of attributes
     */
    void setAttributes(Collection<AttrValueShop> attribute);



    /**
     * Get categories, that assigned to shop.
     *
     * @return categories, that assigned to shop.
     */
    Collection<ShopCategory> getShopCategory();

    /**
     * Set categories, that assigned to shop.
     *
     * @param shopCategory categories, that assigned to shop.
     */
    void setShopCategory(Collection<ShopCategory> shopCategory);

    /**
     * Get the default shop currency.
     *
     * @return default currency code or null is there no settings.
     */
    String getDefaultCurrency();

    /**
     * Get all supported currencies. First in list is default.
     *
     * @return separated by comma all supported currencies by shop or null if not set.
     */
    String getSupportedCurrencies();

    /**
     * Set supported currency
     * @param currencies comma separated list of supported currencies.
     */
    //void updateSupportedCurrencies(String currencies);

    /**
     * Get all supported currencies. First in list is default.
     *
     * @return list of currency codes.
     */
    List<String> getSupportedCurrenciesAsList();

    /**
     * List of supported shipping countries.
     *
     * @return list of country codes
     */
    String getSupportedShippingCountries();

    /**
     * List of supported shipping countries.
     *
     * @return list of country codes
     */
    List<String> getSupportedShippingCountriesAsList();

    /**
     * List of supported billing countries.
     *
     * @return  list of country codes
     */
    String getSupportedBillingCountries();

    /**
     * List of supported billing countries.
     *
     * @return  list of country codes
     */
    List<String> getSupportedBillingCountriesAsList();


}


