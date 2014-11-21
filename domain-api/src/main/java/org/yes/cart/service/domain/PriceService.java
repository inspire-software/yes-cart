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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;

import java.math.BigDecimal;
import java.util.List;

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
     *
     * @param productId    optional product to filter the prices. If null the price will be chosen by selectedSku.
     * @param selectedSku  optional sku to filter the prices. if null all product skus will be  considered to
     *                     determine minimal price
     * @param shopId       shop
     * @param currencyCode desirable currency
     * @param quantity     quantity
     *
     * @return lowest available sku price
     */
    SkuPrice getMinimalRegularPrice(final Long productId,
                                    final String selectedSku,
                                    final long shopId,
                                    final String currencyCode,
                                    final BigDecimal quantity);

    /**
     * Get all prices for given product skus (all), shop, currency and quantity.
     *
     * @param productId    optional product to filter the prices. If null the price will be chosen by selectedSku.
     * @param selectedSku  optional sku to filter the prices. if null all product skus will be  considered to
     *                     determine minimal price
     * @param shopId       shop
     * @param currencyCode desirable currency
     *
     * @return lowest available sku price
     */
    List<SkuPrice> getAllCurrentPrices(final Long productId,
                                       final String selectedSku,
                                       final long shopId,
                                       final String currencyCode);


    /**
     * Get navigation records for prices
     *
     * @param priceTierTree given price tier tree
     * @param currency      currency code
     * @param shop          currency shop
     * @return list of navigation records for given price tree and currency
     */
    List<FilteredNavigationRecord> getPriceNavigationRecords(
            PriceTierTree priceTierTree,
            String currency,
            Shop shop);

    /**
     * Delete derived prices in given shop.
     *
     * @param shop            given shop id
     * @param derivedCurrency derived currency
     */
    void deleteDerivedPrices(Shop shop, String derivedCurrency);

}
