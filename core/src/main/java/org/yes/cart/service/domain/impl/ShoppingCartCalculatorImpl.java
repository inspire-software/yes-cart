package org.yes.cart.service.domain.impl;

import org.yes.cart.service.domain.ShoppingCartCalculator;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 01/10/2015
 * Time: 08:56
 */
public class ShoppingCartCalculatorImpl implements ShoppingCartCalculator {

    private final AmountCalculationStrategy calculationStrategy;

    /**
     * @param calculationStrategy this calculation strategy must match the one used for the shopping cart
     */
    public ShoppingCartCalculatorImpl(final AmountCalculationStrategy calculationStrategy) {
        this.calculationStrategy = calculationStrategy;
    }

    private MutableShoppingCart createNewCart(final ShoppingCart currentCart,
                                              final boolean shoppingContext,
                                              final boolean orderInfo,
                                              final boolean disablePromotions) {

        final ShoppingCartImpl cart = new ShoppingCartImpl();
        cart.setPromotionsDisabled(disablePromotions);
        cart.initialise(this.calculationStrategy);

        cart.setCurrentLocale(currentCart.getCurrentLocale());
        cart.setCurrencyCode(currentCart.getCurrencyCode());

        if (shoppingContext) {
            cart.getShoppingContext().setCustomerName(currentCart.getShoppingContext().getCustomerName());
            cart.getShoppingContext().setShopId(currentCart.getShoppingContext().getShopId());
            cart.getShoppingContext().setShopCode(currentCart.getShoppingContext().getShopCode());
            cart.getShoppingContext().setCustomerShopId(currentCart.getShoppingContext().getCustomerShopId());
            cart.getShoppingContext().setCustomerShopCode(currentCart.getShoppingContext().getCustomerShopCode());
            cart.getShoppingContext().setCountryCode(currentCart.getShoppingContext().getCountryCode());
            cart.getShoppingContext().setStateCode(currentCart.getShoppingContext().getStateCode());
            cart.getShoppingContext().setCustomerEmail(currentCart.getShoppingContext().getCustomerEmail());
            cart.getShoppingContext().setCustomerShops(currentCart.getShoppingContext().getCustomerShops());
            cart.getShoppingContext().setLatestViewedSkus(currentCart.getShoppingContext().getLatestViewedSkus());
            cart.getShoppingContext().setLatestViewedCategories(currentCart.getShoppingContext().getLatestViewedCategories());
            cart.getShoppingContext().setResolvedIp(currentCart.getShoppingContext().getResolvedIp());
        }

        if (orderInfo) {
            cart.getOrderInfo().setPaymentGatewayLabel(currentCart.getOrderInfo().getPaymentGatewayLabel());
            cart.getOrderInfo().setMultipleDelivery(currentCart.getOrderInfo().isMultipleDelivery());
            cart.getOrderInfo().setSeparateBillingAddress(currentCart.getOrderInfo().isSeparateBillingAddress());
            cart.getOrderInfo().setSeparateBillingAddressEnabled(currentCart.getOrderInfo().isSeparateBillingAddressEnabled());
            cart.getOrderInfo().setBillingAddressNotRequired(currentCart.getOrderInfo().isBillingAddressNotRequired());
            cart.getOrderInfo().setDeliveryAddressNotRequired(currentCart.getOrderInfo().isDeliveryAddressNotRequired());
            cart.getOrderInfo().setCarrierSlaId(currentCart.getOrderInfo().getCarrierSlaId());
            cart.getOrderInfo().setBillingAddressId(currentCart.getOrderInfo().getBillingAddressId());
            cart.getOrderInfo().setDeliveryAddressId(currentCart.getOrderInfo().getDeliveryAddressId());
            cart.getOrderInfo().setOrderMessage(currentCart.getOrderInfo().getOrderMessage());
        }

        return cart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PriceModel calculatePrice(final ShoppingCart currentCart,
                                     final String skuCode,
                                     final BigDecimal minimalPrice) {

        final MutableShoppingCart cart = createNewCart(currentCart, true, false, true);

        cart.addProductSkuToCart(skuCode, skuCode, BigDecimal.ONE);
        cart.setProductSkuPrice(skuCode, minimalPrice, minimalPrice);

        cart.recalculate();

        PriceModel model = null;
        for (final CartItem item : cart.getCartItemList()) {
            if (item.getProductSkuCode().equals(skuCode)) {
                model = new DefaultPriceModel(
                        item.getGrossPrice(),
                        item.getNetPrice(),
                        item.getTaxCode(),
                        item.getTaxRate(),
                        item.isTaxExclusiveOfPrice());
            }
        }

        return model;
    }

    public static class DefaultPriceModel implements PriceModel {

        private final BigDecimal grossPrice;
        private final BigDecimal netPrice;
        private final BigDecimal taxAmount;
        private final String taxCode;
        private final BigDecimal taxRate;
        private final boolean taxExclusive;

        public DefaultPriceModel(final BigDecimal grossPrice,
                                 final BigDecimal netPrice,
                                 final String taxCode,
                                 final BigDecimal taxRate,
                                 final boolean taxExclusive) {
            this.grossPrice = grossPrice;
            this.netPrice = netPrice;
            this.taxAmount = grossPrice.subtract(netPrice);
            this.taxCode = taxCode;
            this.taxRate = taxRate;
            this.taxExclusive = taxExclusive;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BigDecimal getGrossPrice() {
            return grossPrice;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BigDecimal getNetPrice() {
            return netPrice;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BigDecimal getTaxAmount() {
            return taxAmount;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getTaxCode() {
            return taxCode;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BigDecimal getTaxRate() {
            return taxRate;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isTaxExclusive() {
            return taxExclusive;
        }
    }

}
