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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.ProductPriceModel;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.impl.ProductPriceModelImpl;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CarrierService;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.web.support.service.ShippingServiceFacade;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * User: denispavlov
 * Date: 11/01/2014
 * Time: 11:57
 */
public class ShippingServiceFacadeImpl implements ShippingServiceFacade {

    private final CarrierService carrierService;
    private final CarrierSlaService carrierSlaService;
    private final ShopService shopService;

    public ShippingServiceFacadeImpl(final CarrierService carrierService,
                                     final CarrierSlaService carrierSlaService,
                                     final ShopService shopService) {
        this.carrierService = carrierService;
        this.carrierSlaService = carrierSlaService;
        this.shopService = shopService;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSkippableAddress(final ShoppingCart shoppingCart) {
        final List<Carrier> available = findCarriers(shoppingCart);
        for (final Carrier carrier : available) {
            for (final CarrierSla sla : carrier.getCarrierSla()) {
                if (sla.isBillingAddressNotRequired() && sla.isDeliveryAddressNotRequired()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public List<Carrier> findCarriers(final ShoppingCart shoppingCart) {
        final List<Carrier> all = carrierService.getCarriersByShopIdAndCurrency(
                shoppingCart.getShoppingContext().getShopId(),
                shoppingCart.getCurrencyCode());
        filterCarriersForShoppingCart(all, shoppingCart);
        return all;
    }

    private void filterCarriersForShoppingCart(final List<Carrier> all, final ShoppingCart shoppingCart) {
        // CPOINT: shipping logic in most cases it is very business specific and should be put into this method
        // CPOINT: recommended approach is to create Carrier filter strategy bean and delegate filtering to it
    }

    /** {@inheritDoc} */
    @Override
    public Pair<Carrier, CarrierSla> getCarrierSla(final ShoppingCart shoppingCart, final List<Carrier> carriersChoices) {

        final Long slaId = shoppingCart.getCarrierSlaId();

        if (slaId != null) {
            for (Carrier carrier : carriersChoices) {
                for (CarrierSla carrierSla : carrier.getCarrierSla()) {
                    if (slaId == carrierSla.getCarrierslaId()) {
                        return new Pair<Carrier, CarrierSla>(carrier, carrierSla);
                    }
                }
            }
        }

        return new Pair<Carrier, CarrierSla>(null, null);
    }

    static final String CART_SHIPPING_TOTAL_REF = "yc-cart-shipping-total";

    /**
     * {@inheritDoc}
     */
    public ProductPriceModel getCartShippingTotal(final ShoppingCart cart) {


        final long shopId = cart.getShoppingContext().getShopId();
        final String currency = cart.getCurrencyCode();

        final BigDecimal deliveriesCount = new BigDecimal(cart.getShippingList().size());
        final BigDecimal list = cart.getTotal().getDeliveryListCost();
        final BigDecimal sale = cart.getTotal().getDeliveryCost();

        final Shop shop = shopService.getById(shopId);

        final boolean showTax = Boolean.valueOf(shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO));
        final boolean showTaxNet = showTax && Boolean.valueOf(shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET));
        final boolean showTaxAmount = showTax && Boolean.valueOf(shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT));

        if (showTax) {

            final BigDecimal costInclTax = cart.getTotal().getDeliveryCostAmount();

            if (MoneyUtils.isFirstBiggerThanSecond(costInclTax, Total.ZERO)) {
                final BigDecimal totalTax = cart.getTotal().getDeliveryTax();
                final BigDecimal net = costInclTax.subtract(totalTax);
                final BigDecimal gross = costInclTax;

                final BigDecimal totalAdjusted = showTaxNet ? net : gross;

                final Set<String> taxes = new TreeSet<String>(); // sorts and de-dup's
                final Set<BigDecimal> rates = new TreeSet<BigDecimal>();
                for (final CartItem item : cart.getShippingList()) {
                    if (StringUtils.isNotBlank(item.getTaxCode())) {
                        taxes.add(item.getTaxCode());
                    }
                    rates.add(item.getTaxRate());
                }

                final BigDecimal taxRate;
                if (rates.size() > 1) {
                    // mixed rates in cart we use average with round up so that tax is not reduced by rounding
                    taxRate = totalTax.multiply(MoneyUtils.HUNDRED).divide(net, Constants.DEFAULT_SCALE, BigDecimal.ROUND_UP);
                } else {
                    // single rate for all items, use it to prevent rounding errors
                    taxRate = rates.iterator().next();
                }

                final String tax = StringUtils.join(taxes, ',');
                final boolean exclusiveTax = costInclTax.compareTo(sale) > 0;

                if (MoneyUtils.isFirstBiggerThanSecond(list, sale)) {
                    // if we have discounts

                    final MoneyUtils.Money listMoney = MoneyUtils.getMoney(list, taxRate, !exclusiveTax);
                    final BigDecimal listAdjusted = showTaxNet ? listMoney.getNet() : listMoney.getGross();

                    return new ProductPriceModelImpl(
                            CART_SHIPPING_TOTAL_REF,
                            currency,
                            deliveriesCount,
                            listAdjusted, totalAdjusted,
                            showTax, showTaxNet, showTaxAmount,
                            tax,
                            taxRate,
                            exclusiveTax,
                            totalTax
                    );

                }
                // no discounts
                return new ProductPriceModelImpl(
                        CART_SHIPPING_TOTAL_REF,
                        currency,
                        deliveriesCount,
                        totalAdjusted, null,
                        showTax, showTaxNet, showTaxAmount,
                        tax,
                        taxRate,
                        exclusiveTax,
                        totalTax
                );

            }

        }

        // standard "as is" prices

        if (MoneyUtils.isFirstBiggerThanSecond(sale, Total.ZERO)
                && MoneyUtils.isFirstBiggerThanSecond(list, sale)) {
            // if we have discounts
            return new ProductPriceModelImpl(
                    CART_SHIPPING_TOTAL_REF,
                    currency,
                    deliveriesCount,
                    list, sale
            );

        }
        // no discounts
        return new ProductPriceModelImpl(
                CART_SHIPPING_TOTAL_REF,
                currency,
                deliveriesCount,
                sale, null
        );


    }


}
