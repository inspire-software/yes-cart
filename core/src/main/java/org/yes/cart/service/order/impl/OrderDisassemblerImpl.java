/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import org.yes.cart.domain.entity.*;
import org.yes.cart.service.order.OrderAssemblyException;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.MutableOrderInfo;
import org.yes.cart.shoppingcart.MutableShoppingContext;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.NameFormatImpl;
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
public class OrderDisassemblerImpl {

    private AmountCalculationStrategy amountCalculationStrategy; //todo get from spring

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
        mutableOrderInfo.setOrderMessage(customerOrder.getOrderMessage());

        mutableOrderInfo.setCarrierSlaId(getFirstDelivery(customerOrder).getCarrierSla().getCarrierslaId());

        mutableOrderInfo.setSeparateBillingAddress(!customerOrder.getBillingAddress().equals(customerOrder.getShippingAddress()));

        mutableOrderInfo.setDeliveryAddressId(null); //todo - how ???
        mutableOrderInfo.setCarrierSlaId(null); //todo - how ???

        mutableOrderInfo.setBillingAddressNotRequired(false);
        mutableOrderInfo.setDeliveryAddressNotRequired(false);
        mutableOrderInfo.setPaymentGatewayLabel(customerOrder.getPgLabel());
        mutableOrderInfo.setMultipleDelivery(customerOrder.getDelivery().size() > 1);

        MutableShoppingContext mutableShoppingContext = shoppingCart.getShoppingContext();
        mutableShoppingContext.setCustomerEmail(customerOrder.getCustomer().getEmail());
        mutableShoppingContext.setCustomerName(new NameFormatImpl().formatFullName(customerOrder.getCustomer()));

        final List<String> customerShops = new ArrayList<String>();
        for (final CustomerShop shop : customerOrder.getCustomer().getShops()) {
            customerShops.add(shop.getShop().getCode());
        }
        mutableShoppingContext.setCustomerShops(customerShops);
        mutableShoppingContext.setShopId(customerOrder.getShop().getShopId());
        mutableShoppingContext.setShopCode(customerOrder.getShop().getCode());

        final Address billing = customerOrder.getCustomer().getDefaultAddress(Address.ADDR_TYPE_BILLING);

        mutableShoppingContext.setCountryCode(billing.getCountryCode());
        mutableShoppingContext.setStateCode(billing.getStateCode());





        return shoppingCart;
    }

    private CustomerOrderDelivery getFirstDelivery(final CustomerOrder customerOrder) {

        return customerOrder.getDelivery().iterator().next();

    }
}
