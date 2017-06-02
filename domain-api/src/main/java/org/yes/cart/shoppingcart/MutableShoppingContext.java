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

package org.yes.cart.shoppingcart;

import java.io.Serializable;
import java.util.List;

/**
 * User: denispavlov
 * Date: 05/11/2014
 * Time: 09:52
 */
public interface MutableShoppingContext extends ShoppingContext, Serializable {


    /**
     * Set customer email.
     * @param customerEmail customer email.
     */
    void setCustomerEmail(String customerEmail);

    /**
     * Set customer name.
     *
     * @param customerName customer name.
     */
    void setCustomerName(String customerName);

    /**
     * Set customer active shops.
     *
     * @param shops customer active shops
     */
    void setCustomerShops(List<String> shops);

    /**
     * Set current shop id.
     *
     * @param shopId current shop id.
     */
    void setShopId(long shopId);

    /**
     * Set current shop code.
     *
     * @param shopCode current shop code.
     */
    void setShopCode(String shopCode);

    /**
     * Set current customer shop id.
     *
     * @param shopId current customer shop id.
     */
    void setCustomerShopId(long shopId);

    /**
     * Set current customer shop code.
     *
     * @param shopCode current customer shop code.
     */
    void setCustomerShopCode(String shopCode);

    /**
     * Set current country code.
     *
     * @param countryCode current country code.
     */
    void setCountryCode(String countryCode);

    /**
     * Set current state code.
     *
     * @param stateCode current state code.
     */
    void setStateCode(String stateCode);

    /**
     * Set flag to indicate if tax info view change is enabled.
     *
     * @param taxInfoChangeViewEnabled true if enabled
     */
    void setTaxInfoChangeViewEnabled(final boolean taxInfoChangeViewEnabled);

    /**
     * Set flag to indicate if tax info is enabled.
     *
     * @param taxInfoEnabled true if enabled
     */
    void setTaxInfoEnabled(final boolean taxInfoEnabled);

    /**
     * Set flag to indicate to use net prices to display.
     *
     * @param taxInfoUseNet true for net, false for gross
     */
    void setTaxInfoUseNet(final boolean taxInfoUseNet);

    /**
     * Set flag to indicate to display amount of tax.
     *
     * @param taxInfoShowAmount true to display amount, false to display rate
     */
    void setTaxInfoShowAmount(final boolean taxInfoShowAmount);

    /**
     * Set flag to indicate to display/hide prices.
     *
     * @param hidePrices true to hide prices, false to show prices
     */
    void setHidePrices(final boolean hidePrices);

    /**
     * Set shopper ip address.
     *
     * @param resolvedIp resolved ip address.
     */
    void setResolvedIp(String resolvedIp);

    /**
     * Set latest viewed sku codes.
     *
     * @param latestViewedSkus latest viewed skus.
     */
    void setLatestViewedSkus(List<String> latestViewedSkus);

    /**
     * Set last viewed categories.
     *
     * TODO: YC-360
     *
     * @param latestViewedCategories comma separated list of category ids.
     */
    void setLatestViewedCategories(List<String> latestViewedCategories);

    /**
     * Clear context.
     */
    void clearContext();
}
