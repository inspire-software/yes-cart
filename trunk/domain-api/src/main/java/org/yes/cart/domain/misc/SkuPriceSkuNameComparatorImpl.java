package org.yes.cart.domain.misc;

import org.yes.cart.domain.entity.SkuPrice;

import java.util.Comparator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public class SkuPriceSkuNameComparatorImpl implements Comparator<SkuPrice> {
    public int compare(SkuPrice skuPrice1, SkuPrice skuPrice2) {
        return skuPrice1.getSku().getName().compareTo(skuPrice2.getSku().getName());
    }
}
