package org.yes.cart.service.domain;

import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;

/**
 * Calculator allows to perform complex calculation that are usually done in cart calculation
 * framework without changing the cart.
 *
 * User: denispavlov
 * Date: 01/10/2015
 * Time: 08:45
 */
public interface ShoppingCartCalculator {

    interface PriceModel {

        /**
         * @return price including taxes
         */
        BigDecimal getGrossPrice();

        /**
         * @return price excluding taxes
         */
        BigDecimal getNetPrice();

        /**
         * @return amount of tax (effectively Gross - Net)
         */
        BigDecimal getTaxAmount();

        /**
         * @return tax code applied
         */
        String getTaxCode();

        /**
         * @return tax rate used
         */
        BigDecimal getTaxRate();

        /**
         * @return flag to indicate inclusive/exclusive taxes
         */
        boolean isTaxExclusive();

    }

    /**
     * Calculate price model for given SKU with minimal price ( {@link PriceService#getMinimalPrice(Long, String, long, String, BigDecimal, String)} ).
     *
     * @param currentCart current cart (to provide additional data such as location)
     * @param skuCode SKU code to calculate price for
     * @param minimalPrice price determined by price service
     *
     * @return price model
     */
    PriceModel calculatePrice(ShoppingCart currentCart,
                              String skuCode,
                              BigDecimal minimalPrice);

}
