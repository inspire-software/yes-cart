package org.yes.cart.shoppingcart.impl;

import com.rsa.certj.cert.extensions.BuiltInDomainDefinedAttributes;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.AmountCalculationResult;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingContext;

import java.math.BigDecimal;
import java.util.List;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 29/11/11
 * Time: 14:22
 */
public class DefaultAmountCalculationStrategy implements AmountCalculationStrategy {

    private final BigDecimal tax;

    /**
     * Construct default amount calculator with included tax.
     *
     * @param tax vat value in percents
     */
    public DefaultAmountCalculationStrategy(final BigDecimal tax) {
        this.tax = tax.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getTax() {
        return tax;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTaxIncluded() {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public AmountCalculationResult calculate(final ShoppingContext shoppingContext,
                                             final List<? extends CartItem> items,
                                             final CustomerOrderDelivery orderDelivery) {
        final AmountCalculationResult rez = new AmountCalculationResultImpl();

        rez.setSubTotal(calculateSubTotal(items));
        rez.setSubTotalTax(calculateTax(rez.getSubTotal()));
        rez.setSubTotalAmount(calculateAmount(rez.getSubTotal(), rez.getSubTotalTax()));

        rez.setDelivery(calculateDelievery(orderDelivery));
        rez.setDeliveryTax(calculateTax(rez.getDelivery()));
        rez.setDeliveryAmount(calculateAmount(rez.getDelivery(), rez.getDeliveryTax()));

        rez.setTotal(rez.getSubTotal().add(rez.getDelivery()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
        rez.setTotalTax(calculateTax(rez.getTotal()));
        rez.setTotalAmount(calculateAmount(rez.getTotal(), rez.getTotalTax()));

        return rez;
    }

    /**
     * Calculate sub total of shopping cart by given list of {@link CartItem}.
     *
     * @param items given list of cart items.
     * @return cart sub total.
     */
    BigDecimal calculateSubTotal(final List<? extends CartItem> items) {
        BigDecimal cartSubTotal = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        if (items != null) {
            for (CartItem cartItem : items) {
                final BigDecimal price = cartItem.getPrice();
                cartSubTotal = cartSubTotal.add(price.multiply(cartItem.getQty()).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP));
            }
        }
        return cartSubTotal;
    }

    /**
     * Calculate delivery price.
     *
     * @param orderDelivery optional order delivery
     * @return delivery price.
     */
    BigDecimal calculateDelievery(final CustomerOrderDelivery orderDelivery) {
        if (orderDelivery != null) {
            return orderDelivery.getPrice().setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * Calculate subtotal tax by given sub total.
     *
     * @param money to claculate tax.
     * @return tax.
     */
    BigDecimal calculateTax(final BigDecimal money) {
        return money.multiply(tax).divide(tax.add(new BigDecimal("100.00"))).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * Calulate sub total amount.
     *
     * @param money given sub total.
     * @param tax   sub total tax.
     * @return cart sub total amount.
     */
    BigDecimal calculateAmount(final BigDecimal money, final BigDecimal tax) {
        return money; // tax already included;
    }


}
