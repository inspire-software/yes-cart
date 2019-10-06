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

package org.yes.cart.bulkexport.csv.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvImpExTuple;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 30/11/2015
 * Time: 22:23
 */
public class CsvProductPriceValueAdapterImpl implements CsvValueAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(CsvProductPriceValueAdapterImpl.class);

    private final PriceService priceService;
    private final ShopService shopService;

    public CsvProductPriceValueAdapterImpl(final PriceService priceService,
                                           final ShopService shopService) {
        this.priceService = priceService;
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object fromRaw(final Object rawValue, final String requiredType, final CsvImpExColumn csvImpExColumn, final CsvImpExTuple tuple) {
        final String shopCode = csvImpExColumn.getParentDescriptor().getContext().getShopCode();
        final Shop shop = shopCode != null ? shopService.getShopByCode(shopCode) : null;
        if (shop != null) {
            final Long fallbackId;
            if (shop.getMaster() != null && !shop.isB2BStrictPriceActive()) {
                fallbackId = shop.getMaster().getShopId();
            } else {
                fallbackId = null;
            }
            final String currency = csvImpExColumn.getContext();
            SkuPrice price = null;
            if (rawValue instanceof Long) {
                // product ID
                price = priceService.getMinimalPrice((Long) rawValue, null, shop.getShopId(), fallbackId, currency, BigDecimal.ONE, false, null, null);
            } else if (rawValue instanceof String) {
                // SKU
                price = priceService.getMinimalPrice(null, (String) rawValue, shop.getShopId(), fallbackId, currency, BigDecimal.ONE, false, null, null);
            }
            if (price != null && price.getSkuPriceId() > 0L) {
                final BigDecimal lowest = MoneyUtils.secondOrFirst(price.getSalePriceForCalculation());
                if (lowest != null) {
                    return lowest.toPlainString();
                }
            }
        } else {
            LOG.warn("Unable to determine price since export descriptor does not specify valid shop code");
        }
        return null;
    }

}
