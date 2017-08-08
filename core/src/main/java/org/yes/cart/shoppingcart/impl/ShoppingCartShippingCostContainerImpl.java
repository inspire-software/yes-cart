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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This is a read only container that allows to provide a view of cart with a different shipping
 * cost.
 *
 * User: denispavlov
 * Date: 11/07/2016
 * Time: 09:52
 */
public class ShoppingCartShippingCostContainerImpl implements MutableShoppingCart {

    private final ShoppingCart original;
    private final Map<String, Long> carrierSlaId;

    private final MutableShoppingContext shoppingContext = new ShoppingContextImpl();
    private final MutableOrderInfo orderInfo = new OrderInfoImpl();

    public ShoppingCartShippingCostContainerImpl(final ShoppingCart original, final String supplier, final Long carrierSlaId) {

        this.original = original;

        this.carrierSlaId = Collections.singletonMap(supplier, carrierSlaId);

        this.shoppingContext.setCountryCode(original.getShoppingContext().getCountryCode());
        this.shoppingContext.setStateCode(original.getShoppingContext().getStateCode());

        this.shoppingContext.setCustomerEmail(original.getShoppingContext().getCustomerEmail());
        this.shoppingContext.setCustomerName(original.getShoppingContext().getCustomerName());

        if (original.getShoppingContext().getCustomerShops() != null) {
            this.shoppingContext.setCustomerShops(Collections.unmodifiableList(original.getShoppingContext().getCustomerShops()));
        }
        if (original.getShoppingContext().getLatestViewedCategories() != null) {
            this.shoppingContext.setLatestViewedCategories(Collections.unmodifiableList(original.getShoppingContext().getLatestViewedCategories()));
        }
        if (original.getShoppingContext().getLatestViewedSkus() != null) {
            this.shoppingContext.setLatestViewedSkus(Collections.unmodifiableList(original.getShoppingContext().getLatestViewedSkus()));
        }

        this.shoppingContext.setResolvedIp(original.getShoppingContext().getResolvedIp());

        this.shoppingContext.setShopCode(original.getShoppingContext().getShopCode());
        this.shoppingContext.setShopId(original.getShoppingContext().getShopId());
        this.shoppingContext.setCustomerShopCode(original.getShoppingContext().getCustomerShopCode());
        this.shoppingContext.setCustomerShopId(original.getShoppingContext().getCustomerShopId());

        this.orderInfo.setBillingAddressId(original.getOrderInfo().getBillingAddressId());
        this.orderInfo.setBillingAddressNotRequired(original.getOrderInfo().isBillingAddressNotRequired());
        this.orderInfo.setCarrierSlaId(this.carrierSlaId);
        this.orderInfo.setDeliveryAddressId(original.getOrderInfo().getDeliveryAddressId());
        this.orderInfo.setDeliveryAddressNotRequired(original.getOrderInfo().isDeliveryAddressNotRequired());
        this.orderInfo.setMultipleDelivery(original.getOrderInfo().isMultipleDelivery());
        this.orderInfo.setOrderMessage(original.getOrderInfo().getOrderMessage());
        this.orderInfo.setPaymentGatewayLabel(original.getOrderInfo().getPaymentGatewayLabel());
        this.orderInfo.setSeparateBillingAddress(original.getOrderInfo().isSeparateBillingAddress());
        this.orderInfo.setSeparateBillingAddressEnabled(original.getOrderInfo().isSeparateBillingAddressEnabled());

    }

    @Override
    public String getGuid() {
        return original.getGuid();
    }

    @Override
    public List<CartItem> getCartItemList() {
        return original.getCartItemList();
    }

    @Override
    public Map<DeliveryBucket, List<CartItem>> getCartItemMap() {
        return original.getCartItemMap();
    }

    @Override
    public List<CartItem> getShippingList() {
        return Collections.emptyList();
    }

    @Override
    public Map<DeliveryBucket, List<CartItem>> getShippingListMap() {
        return original.getShippingListMap();
    }

    @Override
    public BigDecimal getProductSkuQuantity(final String sku) {
        return original.getProductSkuQuantity(sku);
    }

    @Override
    public int getCartItemsCount() {
        return original.getCartItemsCount();
    }

    @Override
    public List<String> getCartItemsSuppliers() {
        return Collections.singletonList(this.carrierSlaId.keySet().iterator().next());
    }

    @Override
    public List<String> getCoupons() {
        return original.getCoupons();
    }

    @Override
    public List<String> getAppliedCoupons() {
        return original.getAppliedCoupons();
    }

    @Override
    public String getCurrencyCode() {
        return original.getCurrencyCode();
    }

    @Override
    public long getModifiedTimestamp() {
        return original.getModifiedTimestamp();
    }

    @Override
    public boolean isModified() {
        return original.isModified();
    }

    @Override
    public String getCustomerName() {
        return original.getCustomerName();
    }

    @Override
    public String getCustomerEmail() {
        return original.getCustomerEmail();
    }

    @Override
    public boolean isSeparateBillingAddress() {
        return original.isSeparateBillingAddress();
    }

    @Override
    public boolean isBillingAddressNotRequired() {
        return original.isBillingAddressNotRequired();
    }

    @Override
    public boolean isDeliveryAddressNotRequired() {
        return original.isDeliveryAddressNotRequired();
    }

    @Override
    public Map<String, Long> getCarrierSlaId() {
        return carrierSlaId;
    }

    @Override
    public boolean isAllCarrierSlaSelected() {
        return true;
    }

    @Override
    public boolean isAllCartItemsBucketed() {
        return false;
    }

    @Override
    public String getOrderMessage() {
        return original.getOrderMessage();
    }

    @Override
    public int getLogonState() {
        return original.getLogonState();
    }

    @Override
    public boolean contains(final String skuCode) {
        return original.contains(skuCode);
    }

    @Override
    public int indexOfShipping(final String carrierSlaId, final DeliveryBucket deliveryBucket) {
        return original.indexOfShipping(carrierSlaId, deliveryBucket);
    }

    @Override
    public int indexOfProductSku(final String skuCode) {
        return original.indexOfProductSku(skuCode);
    }

    @Override
    public int indexOfGift(final String skuCode) {
        return original.indexOfGift(skuCode);
    }

    @Override
    public MutableShoppingContext getShoppingContext() {
        return shoppingContext;
    }

    @Override
    public MutableOrderInfo getOrderInfo() {
        return orderInfo;
    }

    @Override
    public String getCurrentLocale() {
        return original.getCurrentLocale();
    }

    @Override
    public long getProcessingStartTimestamp() {
        return original.getProcessingStartTimestamp();
    }

    @Override
    public Total getTotal() {
        return original.getTotal();
    }

    @Override
    public String getOrdernum() {
        return original.getOrdernum();
    }


    @Override
    public void initialise(final AmountCalculationStrategy calculationStrategy) {

    }

    @Override
    public void markDirty() {

    }

    @Override
    public void clean() {

    }

    @Override
    public void recalculate() {

    }

    @Override
    public boolean addProductSkuToCart(final String sku, final String skuName, final BigDecimal quantity) {
        return false;
    }

    @Override
    public boolean addShippingToCart(final DeliveryBucket deliveryBucket, final String carrierSlaGUID, final String carrierSlaName, final BigDecimal quantity) {
        return false;
    }

    @Override
    public boolean addGiftToCart(final String sku, final String skuName, final BigDecimal quantity, final String promotionCode) {
        return false;
    }

    @Override
    public boolean setProductSkuToCart(final String sku, final String skuName, final BigDecimal quantity) {
        return false;
    }

    @Override
    public boolean setProductSkuDeliveryBucket(final String sku, final DeliveryBucket deliveryBucket) {
        return false;
    }

    @Override
    public boolean setGiftDeliveryBucket(final String sku, final DeliveryBucket deliveryBucket) {
        return false;
    }

    @Override
    public boolean removeCartItem(final String productSku) {
        return false;
    }

    @Override
    public boolean removeShipping(final String carrierSlaGUID, final DeliveryBucket deliveryBucket) {
        return false;
    }

    @Override
    public boolean removeShipping() {
        return false;
    }

    @Override
    public boolean removeCartItemQuantity(final String productSku, final BigDecimal quantity) {
        return false;
    }

    @Override
    public boolean removeItemPromotions() {
        return false;
    }

    @Override
    public boolean removeItemOffers() {
        return false;
    }

    @Override
    public boolean setProductSkuPrice(final String productSkuCode, final BigDecimal salePrice, final BigDecimal listPrice) {
        return false;
    }

    @Override
    public boolean setShippingPrice(final String carrierSlaGUID, final DeliveryBucket deliveryBucket, final BigDecimal salePrice, final BigDecimal listPrice) {
        return false;
    }

    @Override
    public boolean setGiftPrice(final String productSkuCode, final BigDecimal salePrice, final BigDecimal listPrice) {
        return false;
    }

    @Override
    public boolean setProductSkuPromotion(final String productSkuCode, final BigDecimal promoPrice, final String promoCode) {
        return false;
    }

    @Override
    public boolean setProductSkuOffer(final String productSkuCode, final BigDecimal fixedPrice, final String authCode) {
        return false;
    }

    @Override
    public boolean setShippingPromotion(final String carrierSlaGUID, final DeliveryBucket deliveryBucket, final BigDecimal promoPrice, final String promoCode) {
        return false;
    }

    @Override
    public boolean setProductSkuTax(final String productSkuCode, final BigDecimal netPrice, final BigDecimal grossPrice, final BigDecimal rate, final String taxCode, final boolean exclPrice) {
        return false;
    }

    @Override
    public boolean setShippingTax(final String carrierSlaGUID, final DeliveryBucket deliveryBucket, final BigDecimal netPrice, final BigDecimal grossPrice, final BigDecimal rate, final String taxCode, final boolean exclPrice) {
        return false;
    }

    @Override
    public boolean addCoupon(final String coupon) {
        return false;
    }

    @Override
    public boolean removeCoupon(final String coupon) {
        return false;
    }

    @Override
    public void setCurrencyCode(final String currencyCode) {

    }

    @Override
    public void setCurrentLocale(final String currentLocale) {

    }

    @Override
    public boolean hasGifts() {
        return false;
    }

    @Override
    public boolean isPromotionsDisabled() {
        return false;
    }

    @Override
    public void setPromotionsDisabled(final boolean promotionsDisabled) {

    }

    @Override
    public void setOrdernum(final String ordernum) {

    }
}
