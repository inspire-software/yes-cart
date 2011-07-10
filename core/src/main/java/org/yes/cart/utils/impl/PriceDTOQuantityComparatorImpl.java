package org.yes.cart.utils.impl;

import org.yes.cart.domain.dto.SkuPriceDTO;

import java.util.Comparator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PriceDTOQuantityComparatorImpl implements Comparator<SkuPriceDTO> {
    /**
     * {@inheritDoc}
     */
    public int compare(final SkuPriceDTO priceDTO1, final SkuPriceDTO priceDTO2) {
        return priceDTO1.getQuantityTier().compareTo(priceDTO2.getQuantityTier());
    }
}
