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

package org.yes.cart.service.order.impl;

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.customer.CustomerNameFormatter;
import org.yes.cart.service.order.OrderAssemblyException;
import org.yes.cart.service.order.OrderDisassembler;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.MutableOrderInfo;
import org.yes.cart.shoppingcart.MutableShoppingContext;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Transform {@link org.yes.cart.domain.entity.CustomerOrder} to {@link org.yes.cart.shoppingcart.ShoppingCart}.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 01-Dec-2014
 * Time: 21:51:01
 */
public class OrderDisassemblerImpl implements OrderDisassembler {

    private final AmountCalculationStrategy amountCalculationStrategy;
    private final CustomerNameFormatter customerNameFormatter;

    public OrderDisassemblerImpl(final AmountCalculationStrategy amountCalculationStrategy,
                                 final CustomerNameFormatter customerNameFormatter) {
        this.amountCalculationStrategy = amountCalculationStrategy;
        this.customerNameFormatter = customerNameFormatter;
    }

    /**
     * Create {@link org.yes.cart.shoppingcart.ShoppingCart} from {@link org.yes.cart.domain.entity.CustomerOrder} for order adjustment.
     *
     * @param customerOrder given order
     * @return cart
     */
    public ShoppingCart assembleCustomerOrder(final CustomerOrder customerOrder) throws OrderAssemblyException {

        final ShoppingCartImpl shoppingCart = new ShoppingCartImpl();

        //fill cart item list
        for (CustomerOrderDet orderDet :customerOrder.getOrderDetail()) {
            if(orderDet.isGift()) {
                shoppingCart.addGiftToCart(orderDet.getProductSkuCode(), orderDet.getQty(), orderDet.getAppliedPromo());
            } else {
                shoppingCart.addProductSkuToCart(orderDet.getProductSkuCode(), orderDet.getQty());
            }
        }

        //fill deliveries
        for (CustomerOrderDelivery orderDelivery : customerOrder.getDelivery())  {
            shoppingCart.addShippingToCart(String.valueOf( orderDelivery.getCarrierSla().getCarrierslaId()), BigDecimal.ONE);
        }

        //coupons
        for(PromotionCouponUsage coupons : customerOrder.getCoupons()) {
            shoppingCart.addCoupon(coupons.getCoupon().getCode());
        }

        shoppingCart.setCurrentLocale(customerOrder.getLocale());
        shoppingCart.setCurrencyCode(customerOrder.getCurrency());

        MutableOrderInfo mutableOrderInfo = shoppingCart.getOrderInfo();
        MutableShoppingContext mutableShoppingContext = shoppingCart.getShoppingContext();

        mutableOrderInfo.setOrderMessage(customerOrder.getOrderMessage());

        mutableOrderInfo.setCarrierSlaId(getFirstDelivery(customerOrder).getCarrierSla().getCarrierslaId());

        mutableOrderInfo.setSeparateBillingAddress(!customerOrder.getBillingAddress().equals(customerOrder.getShippingAddress()));

        if (customerOrder.getBillingAddressDetails() != null) {
            mutableOrderInfo.setBillingAddressId(customerOrder.getBillingAddressDetails().getAddressId());
            mutableShoppingContext.setCountryCode(customerOrder.getBillingAddressDetails().getCountryCode());
            mutableShoppingContext.setStateCode(customerOrder.getBillingAddressDetails().getStateCode());
            mutableOrderInfo.setBillingAddressNotRequired(false);
        } else {
            mutableOrderInfo.setBillingAddressNotRequired(true);
        }
        if (customerOrder.getShippingAddressDetails() != null) {
            mutableOrderInfo.setDeliveryAddressId(customerOrder.getShippingAddressDetails().getAddressId());
            if (mutableShoppingContext.getCountryCode() == null) {
                mutableShoppingContext.setCountryCode(customerOrder.getShippingAddressDetails().getCountryCode());
                mutableShoppingContext.setStateCode(customerOrder.getShippingAddressDetails().getStateCode());
            }
            mutableOrderInfo.setDeliveryAddressNotRequired(false);
        } else {
            mutableOrderInfo.setDeliveryAddressNotRequired(true);
        }

        mutableOrderInfo.setPaymentGatewayLabel(customerOrder.getPgLabel());
        mutableOrderInfo.setMultipleDelivery(customerOrder.getDelivery().size() > 1);

        mutableShoppingContext.setCustomerEmail(customerOrder.getCustomer().getEmail());
        mutableShoppingContext.setCustomerName(formatNameFor(customerOrder.getCustomer(), customerOrder.getShop()));

        final List<String> customerShops = new ArrayList<String>();
        for (final CustomerShop shop : customerOrder.getCustomer().getShops()) {
            customerShops.add(shop.getShop().getCode());
        }
        mutableShoppingContext.setCustomerShops(customerShops);
        mutableShoppingContext.setShopId(customerOrder.getShop().getShopId());
        mutableShoppingContext.setShopCode(customerOrder.getShop().getCode());

        return shoppingCart;
    }

    private CustomerOrderDelivery getFirstDelivery(final CustomerOrder customerOrder) {

        return customerOrder.getDelivery().iterator().next();

    }


    private String formatNameFor(final Customer customer, final Shop shop) {

        final AttrValue format = shop.getAttributeByCode(AttributeNamesKeys.Shop.CUSTOMER_NAME_FORMATTER);
        if (format != null) {
            return customerNameFormatter.formatName(customer, format.getVal());
        }
        return customerNameFormatter.formatName(customer);

    }

}
