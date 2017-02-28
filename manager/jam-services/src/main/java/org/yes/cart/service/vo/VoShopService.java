/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo;

import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * Created by iazarnyi on 1/19/16.
 */
public interface VoShopService {

    /**
     * Get all manageable shops.
     *
     * @return list of all manageble shops.
     * @throws Exception
     */
    List<VoShop> getAll() throws Exception;

    /**
     * Get all manageable shops.
     *
     * @return list of all manageble shops.
     * @throws Exception
     */
    List<VoShop> getAllSubs(long masterId) throws Exception;

    /**
     * Get shop by id.
     *
     * @param id
     * @return shop vo
     * @throws Exception
     */
    VoShop getById(long id) throws Exception;

    /**
     * Update given shop.
     *
     * @param vo shop to update
     * @return updated instance
     * @throws Exception
     */
    VoShop update(VoShop vo) throws Exception;

    /**
     * Create new shop
     *
     * @param vo given instance to persist
     * @return persisted instance
     * @throws Exception
     */
    VoShop create(VoShop vo) throws Exception;

    /**
     * Create new shop
     *
     * @param vo given instance to persist
     * @return persisted instance
     * @throws Exception
     */
    VoShop createSub(VoSubShop vo) throws Exception;

    /**
     * Get shop by id.
     *
     * @param id
     * @throws Exception
     */
    void remove(long id) throws Exception;

    /**
     * Get summary information for given shop.
     *
     * @param summary summary object to fill data for
     * @param shopId given shop
     * @param lang locale for localised names
     *
     * @throws Exception
     */
    void fillShopSummaryDetails(VoShopSummary summary, long shopId, String lang) throws Exception;

    /**
     * Get localization information for given shop.
     *
     * @param shopId given shop
     * @return localization information
     * @throws Exception
     */
    VoShopSeo getShopLocale(long shopId) throws Exception;

    /**
     * Update localization information.
     *
     * @param vo vo
     * @return localization information
     * @throws Exception
     */
    VoShopSeo update(final VoShopSeo vo) throws Exception;

    /**
     * Get urls for shop.
     *
     * @param shopId pk
     * @return urls
     * @throws Exception
     */
    VoShopUrl getShopUrls(long shopId) throws Exception;

    /**
     * Update shop urls
     *
     * @param vo urls
     * @return updated version of shop urls
     * @throws Exception
     */
    VoShopUrl update(VoShopUrl vo) throws Exception;

    /**
     * Get aliases for shop.
     *
     * @param shopId pk
     * @return aliases
     * @throws Exception
     */
    VoShopAlias getShopAliases(long shopId) throws Exception;

    /**
     * Update shop aliases
     *
     * @param vo aliases
     * @return updated version of shop aliases
     * @throws Exception
     */
    VoShopAlias update(VoShopAlias vo) throws Exception;

    /**
     * GEt supprted currencies.
     *
     * @param shopId given shop id
     * @return supported currencies
     * @throws Exception
     */
    VoShopSupportedCurrencies getShopCurrencies(long shopId) throws Exception;


    /**
     * Update supported currencies.
     *
     * @param vo currencies
     * @return updated version of supported currencies.
     * @throws Exception
     */
    VoShopSupportedCurrencies update(VoShopSupportedCurrencies vo) throws Exception;

    /**
     * Get supported languages by given shop
     * @param shopId given shop id
     * @return langs
     * @throws Exception
     */
    VoShopLanguages getShopLanguages(long shopId) throws Exception;

    /**
     * Update supported languages.
     * @param vo languages
     * @return updated version of supported langs.
     * @throws Exception
     */
    VoShopLanguages update(VoShopLanguages vo) throws Exception;

    /**
     * Get supported locations by given shop
     * @param shopId given shop id
     * @return locations
     * @throws Exception
     */
    VoShopLocations getShopLocations(long shopId) throws Exception;

    /**
     * Update supported locations.
     * @param vo languages
     * @return updated version of supported locations.
     * @throws Exception
     */
    VoShopLocations update(VoShopLocations vo) throws Exception;

    /**
     * Update the shop disabled flag.
     *
     * @param shopId shop PK
     * @param disabled true if shop is disabled
     * @return shop dto if found otherwise null.
     * @throws Exception
     */
    VoShop updateDisabledFlag(long shopId, boolean disabled) throws Exception;


    /**
     * Get supported attributes by given shop
     * @param shopId given shop id
     * @return attributes
     * @throws Exception
     */
    List<VoAttrValueShop> getShopAttributes(long shopId) throws Exception;


    /**
     * Update the shop attributes.
     *
     * @param vo shop attributes to update, boolean indicates if this attribute is to be removed (true) or not (false)
     * @return shop attributes.
     * @throws Exception
     */
    List<VoAttrValueShop> update(List<MutablePair<VoAttrValueShop, Boolean>> vo) throws Exception;


    /**
     * Get supported attributes by given shop
     * @param locale locale
     * @return customer types
     * @throws Exception
     */
    List<MutablePair<String, String>> getAvailableShopsCustomerTypes(String locale) throws Exception;

}
