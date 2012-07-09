package org.yes.cart.domain.misc;

import org.yes.cart.domain.entity.ProductSku;

import java.util.Comparator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public class ProductSkuNameCodeComparatorImpl implements Comparator<ProductSku> {
    /**
     * {@inheritDoc}
     */
    public int compare(final ProductSku sku1, final ProductSku sku2) {
        return (sku1.getName() + sku1.getCode()).compareTo(sku2.getName() + sku2.getCode());
    }
}
