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

import org.yes.cart.bulkcommon.model.ExtensibleValueAdapter;
import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ValueAdapter;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 30/11/2015
 * Time: 22:23
 */
public class CsvProductPriceValueAdapterImpl implements ValueAdapter {

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
    public Object fromRaw(final Object rawValue, final String requiredType, final ImpExColumn impExColumn) {
        final String shopCode = impExColumn.getParentDescriptor().getContext().getShopCode();
        final Shop shop = shopService.getShopByCode(shopCode);
        if (shop != null) {
            final String currency = impExColumn.getContext();
            SkuPrice price = null;
            if (rawValue instanceof Long) {
                // product ID
                price = priceService.getMinimalPrice((Long) rawValue, null, shop.getShopId(), currency, BigDecimal.ONE, null);
            } else if (rawValue instanceof String) {
                // SKU
                price = priceService.getMinimalPrice(null, (String) rawValue, shop.getShopId(), currency, BigDecimal.ONE, null);
            }
            if (price != null && price.getSkuPriceId() > 0L) {
                final BigDecimal sale = price.getSalePriceForCalculation();
                if (sale != null) {
                    return sale.toPlainString();
                }
                return price.getRegularPrice().toPlainString();
            }
        } else {
            ShopCodeContext.getLog(this).warn("Unable to determine price since export descriptor does not specify valid shop code");
        }
        return null;
    }

    /**
     * Spring IoC.
     *
     * @param extensibleValueAdapter extend
     */
    public void setExtensibleValueAdapter(ExtensibleValueAdapter extensibleValueAdapter) {
        extensibleValueAdapter.extend(this, "PRICE");
    }

}
