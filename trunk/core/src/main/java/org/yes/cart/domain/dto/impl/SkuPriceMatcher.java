package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.adapter.DtoToEntityMatcher;
import org.yes.cart.domain.dto.SkuPriceDTO;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.util.MoneyUtils;

/**
 * .
 * <p/>
 * User: dogma
 * Date: Jan 26, 2011
 * Time: 2:54:21 PM
 */
public class SkuPriceMatcher implements DtoToEntityMatcher<SkuPriceDTO, SkuPrice> {

    /**
     * {@inheritDoc}
     */
    public boolean match(final SkuPriceDTO priceDTO, final SkuPrice skuPrice) {

        return priceDTO != null && skuPrice != null
                && priceDTO.getCurrency() != null && skuPrice.getCurrency() != null
                && priceDTO.getCurrency().equals(skuPrice.getCurrency())
                && MoneyUtils.isFirstEqualToSecond(priceDTO.getQuantityTier(), skuPrice.getQuantity());
    }
}
