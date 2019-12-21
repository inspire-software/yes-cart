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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.SkuPrice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Price service.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface PriceService extends GenericService<SkuPrice> {


    /**
     * Get minimal price for given product skus (all), shop, currency and quantity.
     *
     * If no selectedSku is provided the response should be treated as "starts from" prices.
     *
     * enforceTier functionality is primarily used by shipping cost configured by weight, where tier represents
     * weight and cost go up as tier increased (as opposed to default behaviour that expects bulk discounts).
     *
     * pricingPolicy allows to isolate special prices that are available only if specific policy is given to a customer.
     * Default implementation uses customer policy configuration at the customer profile level to achieve this, however
     * the mechanism is generic.
     *
     * supplier allows to allow supplier specific prices to be configured and made available when product SKU supplied
     * through a specific fulfilment centre is used. This is especially effective in market place configurations where
     * each supplier may have their own price for SKU, and also in cases where businesses have reduced price stock.
     *
     * @param productId      optional product to filter the prices. If null the price will be chosen by selectedSku.
     * @param selectedSku    optional sku to filter the prices. if null all product skus will be  considered to
     *                       determine minimal price
     * @param customerShopId shop for which to get the price for
     * @param masterShopId   optional fallback shop (if specified the result will be a merge of prices available in both shops)
     * @param currencyCode   desirable currency
     * @param quantity       quantity tier
     * @param enforceTier    force to pick closest tier price rather than cheapest
     * @param pricingPolicy  optional pricing policy
     * @param supplier       optional supplier
     *
     * @return lowest available sku price
     */
    SkuPrice getMinimalPrice(Long productId,
                             String selectedSku,
                             long customerShopId,
                             Long masterShopId,
                             String currencyCode,
                             BigDecimal quantity,
                             boolean enforceTier,
                             String pricingPolicy,
                             String supplier);

    /**
     * Get all prices for given product skus (all), shop, currency and quantity.
     *
     * If no specific SKU is selected then prices are treated as "starts from" at each tier.
     *
     * @param productId      optional product to filter the prices. If null the price will be chosen by selectedSku.
     * @param selectedSku    optional sku to filter the prices. if null all product skus will be  considered to
     *                       determine minimal price
     * @param customerShopId shop for which to get the price for
     * @param masterShopId   optional fallback shop (if specified the result will be a merge of prices available in both shops)
     * @param currencyCode   desirable currency
     * @param pricingPolicy  optional pricing policy
     * @param supplier       optional supplier
     *
     * @return lowest available sku price
     */
    List<SkuPrice> getAllCurrentPrices(Long productId,
                                       String selectedSku,
                                       long customerShopId,
                                       Long masterShopId,
                                       String currencyCode,
                                       String pricingPolicy,
                                       String supplier);


    /**
     * Get all prices gor given product skus (all), no date or shop filtering.
     *
     * @param productId    optional product to filter the prices. If null the price will be chosen by selectedSku.
     * @param selectedSku  optional sku to filter the prices. if null all product skus will be  considered.
     * @param currencyCode desirable currency
     *
     * @return all sku prices
     */
    List<SkuPrice> getAllPrices(Long productId,
                                String selectedSku,
                                String currencyCode);



    /**
     * Find prices by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return list of prices.
     */
    List<SkuPrice> findPrices(int start,
                              int offset,
                              String sort,
                              boolean sortDescending,
                              Map<String, List> filter);

    /**
     * Find prices by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return count
     */
    int findPriceCount(Map<String, List> filter);




}
