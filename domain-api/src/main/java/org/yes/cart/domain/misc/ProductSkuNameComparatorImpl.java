package org.yes.cart.domain.misc;

import org.yes.cart.domain.entity.ProductSku;

import java.util.Comparator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public class ProductSkuNameComparatorImpl implements Comparator<ProductSku> {
    /**
     * {@inheritDoc}
     */
    public int compare(final ProductSku sku1, final ProductSku sku2) {
        return sku1.getName().compareTo(sku2.getName());
    }
}
