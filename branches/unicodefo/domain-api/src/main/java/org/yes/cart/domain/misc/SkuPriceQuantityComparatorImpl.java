package org.yes.cart.domain.misc;

import org.yes.cart.domain.entity.SkuPrice;

import java.util.Comparator;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 14-Sep-2011
 * Time: 14:21:21
 */
public class SkuPriceQuantityComparatorImpl implements Comparator<SkuPrice> {
    public int compare(final SkuPrice skuPrice1, final SkuPrice skuPrice2) {
        return skuPrice1.getQuantity().compareTo(skuPrice2.getQuantity());
    }
}

