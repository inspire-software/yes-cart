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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.order.OrderAssemblyException;
import org.yes.cart.service.order.OrderDisassembler;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.utils.DateUtils;

import java.util.*;

/**
 * Transform {@link org.yes.cart.domain.entity.CustomerOrder} to {@link org.yes.cart.shoppingcart.ShoppingCart}.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 01-Dec-2014
 * Time: 21:51:01
 */
public class OrderDisassemblerImpl implements OrderDisassembler {

    private final AmountCalculationStrategy amountCalculationStrategy;
    private final CustomerService customerService;

    public OrderDisassemblerImpl(final AmountCalculationStrategy amountCalculationStrategy,
                                 final CustomerService customerService) {
        this.amountCalculationStrategy = amountCalculationStrategy;
        this.customerService = customerService;
    }

    /**
     * Create {@link org.yes.cart.shoppingcart.ShoppingCart} from {@link org.yes.cart.domain.entity.CustomerOrder} for order adjustment.
     *
     * @param customerOrder given order
     * @return cart
     */
    @Override
    public ShoppingCart assembleShoppingCart(final CustomerOrder customerOrder, final boolean promotionsDisabled) throws OrderAssemblyException {

        final ShoppingCartImpl shoppingCart = new ShoppingCartImpl();
        shoppingCart.setPromotionsDisabled(promotionsDisabled); // Allow to not to calculate promotions to preserve "deals" on the order
        shoppingCart.setOrdernum(customerOrder.getOrdernum()); // Mark this as amendment cart
        shoppingCart.initialise(amountCalculationStrategy);

        //fill cart item list
        if (CollectionUtils.isEmpty(customerOrder.getDelivery())) {
            // fill from order if no deliveries are available
            for (CustomerOrderDet orderDet : customerOrder.getOrderDetail()) {
                if (orderDet.isGift()) {
                    shoppingCart.addGiftToCart(orderDet.getProductSkuCode(), orderDet.getProductName(), orderDet.getQty(), orderDet.getAppliedPromo());
                    shoppingCart.setGiftPrice(orderDet.getProductSkuCode(), orderDet.getSalePrice(), orderDet.getListPrice());
                    shoppingCart.setGiftDeliveryBucket(orderDet.getProductSkuCode(), orderDet.getDeliveryBucket());
                } else {
                    shoppingCart.addProductSkuToCart(orderDet.getProductSkuCode(), orderDet.getProductName(), orderDet.getQty());
                    shoppingCart.setProductSkuPrice(orderDet.getProductSkuCode(), orderDet.getSalePrice(), orderDet.getListPrice());
                    if (orderDet.isFixedPrice()) {
                        // Offers in existing order
                        shoppingCart.setProductSkuOffer(orderDet.getProductSkuCode(), orderDet.getPrice(), orderDet.getAppliedPromo());
                    }
                    shoppingCart.setProductSkuDeliveryBucket(orderDet.getProductSkuCode(), orderDet.getDeliveryBucket());
                }

                copyLineRemarks(shoppingCart, orderDet.getProductSkuCode(), orderDet.getB2bRemarks());
                copyLineCustomAttributes(shoppingCart, orderDet.getProductSkuCode(), orderDet.getAllValues());
            }
        } else {
            // fill from delivery details
            for (CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
                for (CustomerOrderDeliveryDet orderDet : delivery.getDetail()) {
                    if (orderDet.isGift()) {
                        shoppingCart.addGiftToCart(orderDet.getProductSkuCode(), orderDet.getProductName(), orderDet.getQty(), orderDet.getAppliedPromo());
                        shoppingCart.setGiftPrice(orderDet.getProductSkuCode(), orderDet.getSalePrice(), orderDet.getListPrice());
                        shoppingCart.setGiftDeliveryBucket(orderDet.getProductSkuCode(), orderDet.getDeliveryBucket());
                    } else {
                        shoppingCart.addProductSkuToCart(orderDet.getProductSkuCode(), orderDet.getProductName(), orderDet.getQty());
                        shoppingCart.setProductSkuPrice(orderDet.getProductSkuCode(), orderDet.getSalePrice(), orderDet.getListPrice());
                        if (orderDet.isFixedPrice()) {
                            // Offers in existing order
                            shoppingCart.setProductSkuOffer(orderDet.getProductSkuCode(), orderDet.getPrice(), orderDet.getAppliedPromo());
                        }
                        shoppingCart.setProductSkuDeliveryBucket(orderDet.getProductSkuCode(), orderDet.getDeliveryBucket());
                    }

                    copyLineRemarks(shoppingCart, orderDet.getProductSkuCode(), orderDet.getB2bRemarks());
                    copyLineCustomAttributes(shoppingCart, orderDet.getProductSkuCode(), orderDet.getAllValues());
                }
            }
        }

        //coupons
        for(PromotionCouponUsage coupons : customerOrder.getCoupons()) {
            shoppingCart.addCoupon(coupons.getCoupon().getCode());
        }

        shoppingCart.setCurrentLocale(customerOrder.getLocale());
        shoppingCart.setCurrencyCode(customerOrder.getCurrency());

        MutableOrderInfo mutableOrderInfo = shoppingCart.getOrderInfo();
        MutableShoppingContext mutableShoppingContext = shoppingCart.getShoppingContext();

        mutableOrderInfo.putDetail(AttributeNamesKeys.Cart.ORDER_INFO_B2B_REF, customerOrder.getB2bRef());
        mutableOrderInfo.putDetail(AttributeNamesKeys.Cart.ORDER_INFO_B2B_EMPLOYEE_ID, customerOrder.getB2bEmployeeId());
        mutableOrderInfo.putDetail(AttributeNamesKeys.Cart.ORDER_INFO_B2B_CHARGE_ID, customerOrder.getB2bChargeId());
        mutableOrderInfo.putDetail(AttributeNamesKeys.Cart.ORDER_INFO_APPROVE_ORDER, String.valueOf(customerOrder.isB2bRequireApprove()));
        mutableOrderInfo.putDetail(AttributeNamesKeys.Cart.ORDER_INFO_B2B_APPROVED_BY, customerOrder.getB2bApprovedBy());
        if (customerOrder.getB2bApprovedBy() != null) {
            mutableOrderInfo.putDetail(AttributeNamesKeys.Cart.ORDER_INFO_B2B_APPROVED_DATE,
                    DateUtils.formatSDT(customerOrder.getB2bApprovedDate()));
        }

        mutableOrderInfo.setOrderMessage(customerOrder.getOrderMessage());

        final Collection<CustomerOrderDelivery> deliveries = customerOrder.getDelivery();
        final Map<String, Integer> noOfStdDeliveriesBySupplier = new HashMap<>();
        boolean multi = false;
        final Map<String, Boolean> multiAvailable = new HashMap<>();
        if (CollectionUtils.isNotEmpty(deliveries)) {
            for (final CustomerOrderDelivery delivery : deliveries) {
                // Preset shipping methods by supplier from first item in the delivery
                final CartItem first = CollectionUtils.isNotEmpty(delivery.getDetail()) ? delivery.getDetail().iterator().next() : null;
                final String supplier = first != null && first.getSupplierCode() != null ? first.getSupplierCode() : "";
                mutableOrderInfo.putCarrierSlaId(supplier, delivery.getCarrierSla().getCarrierslaId());
                // Count standard deliveries (electronics is always separate)
                if (!CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(delivery.getDeliveryGroup())) {
                    Integer count = noOfStdDeliveriesBySupplier.get(supplier);
                    if (count == null) {
                        noOfStdDeliveriesBySupplier.put(supplier, 1);
                        multiAvailable.put(supplier, Boolean.FALSE);
                    } else {
                        noOfStdDeliveriesBySupplier.put(supplier, count + 1);
                        multi = true;
                        multiAvailable.put(supplier, Boolean.TRUE);
                    }
                }
            }
        }

        mutableOrderInfo.setMultipleDelivery(multi);
        mutableOrderInfo.setMultipleDeliveryAvailable(multiAvailable);

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

        if (customerOrder.getBillingAddressDetails() != null && customerOrder.getShippingAddressDetails() != null) {
            mutableOrderInfo.setSeparateBillingAddress(!customerOrder.getBillingAddress().equals(customerOrder.getShippingAddress()));
        } else {
            mutableOrderInfo.setSeparateBillingAddress(false);
        }

        mutableOrderInfo.setPaymentGatewayLabel(customerOrder.getPgLabel());

        mutableShoppingContext.setCustomerEmail(customerOrder.getEmail());
        final Shop configShop = customerOrder.getShop().getMaster() != null ? customerOrder.getShop().getMaster() : customerOrder.getShop();
        mutableShoppingContext.setCustomerName(formatNameFor(customerOrder, configShop));

        final List<String> customerShops = new ArrayList<>();
        customerShops.add(customerOrder.getShop().getCode());
        mutableShoppingContext.setCustomerShops(customerShops); // Only use order's shop, since it may be guest checkout
        if (customerOrder.getShop().getMaster() != null) {
            mutableShoppingContext.setShopId(customerOrder.getShop().getMaster().getShopId());
            mutableShoppingContext.setShopCode(customerOrder.getShop().getMaster().getCode());
            mutableShoppingContext.setCustomerShopId(customerOrder.getShop().getShopId());
            mutableShoppingContext.setCustomerShopCode(customerOrder.getShop().getCode());
        } else {
            mutableShoppingContext.setShopId(customerOrder.getShop().getShopId());
            mutableShoppingContext.setShopCode(customerOrder.getShop().getCode());
            mutableShoppingContext.setCustomerShopId(customerOrder.getShop().getShopId());
            mutableShoppingContext.setCustomerShopCode(customerOrder.getShop().getCode());
        }
        mutableShoppingContext.setResolvedIp(customerOrder.getOrderIp());

        copyOrderCustomAttributes(shoppingCart, customerOrder.getAllValues());

        shoppingCart.recalculate();
        shoppingCart.markDirty();

        return shoppingCart;
    }

    private void copyLineRemarks(final ShoppingCartImpl shoppingCart, final String skuCode, final String remarks) {
        if (StringUtils.isNotBlank(remarks)) {
            shoppingCart.getOrderInfo().putDetail(
                    AttributeNamesKeys.Cart.ORDER_INFO_B2B_ORDER_LINE_REMARKS_ID + skuCode,
                    remarks
            );
        }
    }

    private void copyLineCustomAttributes(final ShoppingCartImpl shoppingCart, final String skuCode, final Map<String, Pair<String, String>> attributes) {

        final String attributeIdPrefix = AttributeNamesKeys.Cart.ORDER_INFO_ORDER_LINE_ATTRIBUTE_ID + ":";
        for (final Map.Entry<String, Pair<String, String>> custom : attributes.entrySet()) {
            if (custom.getKey().startsWith(attributeIdPrefix)) {
                final String avCode = custom.getKey().substring(attributeIdPrefix.length());
                shoppingCart.getOrderInfo().putDetail(
                        AttributeNamesKeys.Cart.ORDER_INFO_ORDER_LINE_ATTRIBUTE_ID + skuCode + "_" + avCode,
                        custom.getValue().getFirst()
                );
            }
        }

    }

    private void copyOrderCustomAttributes(final ShoppingCartImpl shoppingCart, final Map<String, Pair<String, String>> attributes) {

        final String attributeIdPrefix = AttributeNamesKeys.Cart.ORDER_INFO_ORDER_ATTRIBUTE_ID + ":";
        for (final Map.Entry<String, Pair<String, String>> custom : attributes.entrySet()) {
            if (custom.getKey().startsWith(attributeIdPrefix)) {
                final String avCode = custom.getKey().substring(attributeIdPrefix.length());
                shoppingCart.getOrderInfo().putDetail(
                        AttributeNamesKeys.Cart.ORDER_INFO_ORDER_ATTRIBUTE_ID + "_" + avCode,
                        custom.getValue().getFirst()
                );
            }
        }

    }


    private String formatNameFor(final CustomerOrder order, final Shop shop) {

        return customerService.formatNameFor(new CustomerNameAdapter(order), shop);

    }

}
