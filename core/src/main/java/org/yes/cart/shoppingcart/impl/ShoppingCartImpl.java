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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.TimeContext;

import java.math.BigDecimal;
import java.util.*;

/**
 * Shopping cart default implementation.
 *
 * Shopping cart is a high performance class since it represent current application state
 * with respect to the user.
 *
 * YC uses CartTuplizers that allow to Serialize it into byte stream and then package to
 * allow preserving state (e.g. in response cookies, headers or persistent storage).
 * In order to make this process maximum efficient we use Externalization for all object
 * hierarchy of shopping cart implementation.
 *
 * User: dogma
 * Date: Jan 15, 2011
 * Time: 10:39:12 PM
 */
public class ShoppingCartImpl implements MutableShoppingCart {

    private static final long serialVersionUID =  20110509L;

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartImpl.class);

    @JsonProperty
    private List<CartItemImpl> items = new ArrayList<>();
    @JsonProperty
    private List<CartItemImpl> gifts = new ArrayList<>();
    @JsonProperty
    private Set<String> coupons = new TreeSet<>();
    @JsonProperty
    private List<CartItemImpl> shipping = new ArrayList<>();

    private String guid = java.util.UUID.randomUUID().toString();

    private String currentLocale;

    private String currencyCode;

    private long modifiedTimestamp;

    private long processingStartTimestamp;

    private MutableShoppingContext shoppingContext;

    private MutableOrderInfo orderInfo;

    private Total total = new TotalImpl();

    private boolean promotionsDisabled = false;

    private String ordernum;
    @JsonIgnore
    private transient AmountCalculationStrategy calculationStrategy;

    @JsonIgnore
    private AmountCalculationStrategy getCalculationStrategy() {
        if (calculationStrategy == null) {
            LOG.error("Cart amount calculation strategy is not configured. Please configure \"calculationStrategy\" and set it to cart");
            throw new RuntimeException("Cart amount calculation strategy is not configured. Please configure \"calculationStrategy\" and set it to cart");
        }
        return calculationStrategy;
    }

    /** {@inheritDoc} */
    @Override
    public void initialise(final AmountCalculationStrategy calculationStrategy) {
        this.calculationStrategy = calculationStrategy;
        this.processingStartTimestamp = now();
    }

    /** {@inheritDoc} */
    @Override
    public void markDirty() {
        this.modifiedTimestamp = now();
        if (!isModified()) {
            // New carts need artificial time lag.
            this.modifiedTimestamp += 1;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clean() {
        guid = java.util.UUID.randomUUID().toString();
        items.clear();
        gifts.clear();
        coupons.clear();
        orderInfo.clearInfo();
        total = new TotalImpl();
        modifiedTimestamp = now();
    }

    long now() {
        return TimeContext.getMillis();
    }

    /** {@inheritDoc} */
    @Override
    public void recalculate() {
        total = getCalculationStrategy().calculate(this);
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isSeparateBillingAddress() {
        return getOrderInfo().isSeparateBillingAddress();
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isBillingAddressNotRequired() {
        return getOrderInfo().isBillingAddressNotRequired();
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isDeliveryAddressNotRequired() {
        return getOrderInfo().isDeliveryAddressNotRequired();
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public String getOrderMessage() {
        return getOrderInfo().getOrderMessage();
    }

    /** {@inheritDoc} */
    @Override
    public String getGuid() {
        return guid;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public Map<String, Long> getCarrierSlaId() {
        return getOrderInfo().getCarrierSlaId();
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isAllCarrierSlaSelected() {

        return ShoppingCartUtils.isAllCarrierSlaSelected(getItems(), getGifts(), getCarrierSlaId());

    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isAllCartItemsBucketed() {

        return ShoppingCartUtils.isAllCartItemsBucketed(getItems(), getGifts());

    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public List<CartItem> getCartItemList() {

        return ShoppingCartUtils.getCartItemImmutableList(getItems(), getGifts());

    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public Map<DeliveryBucket, List<CartItem>> getCartItemMap() {

        return ShoppingCartUtils.getCartItemImmutableMap(getItems(), getGifts());

    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public List<CartItem> getShippingList() {

        return ShoppingCartUtils.getShippingImmutableList(getShipping());

    }


    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public Map<DeliveryBucket, List<CartItem>> getShippingListMap() {

        return ShoppingCartUtils.getShippingImmutableMap(getShipping());

    }


    /** {@inheritDoc} */
    @Override
    public BigDecimal getProductSkuQuantity(final String supplier,
                                            final String sku) {

        BigDecimal quantity = BigDecimal.ZERO;

        for (final CartItem item : getItems()) {
            if (ShoppingCartUtils.isCartItem(item, supplier, sku)) {
                quantity = quantity.add(item.getQty());
            }
        }

        return quantity;
    }

    /** {@inheritDoc} */
    @Override
    public boolean addProductSkuToCart(final String supplier,
                                       final String sku,
                                       final String skuName,
                                       final BigDecimal quantity,
                                       final String itemGroup,
                                       final boolean configurable,
                                       final boolean notSoldSeparately) {

        for (final CartItemImpl item : getItems()) {
            if (ShoppingCartUtils.isCartItem(item, supplier, sku, itemGroup)) {
                item.addQuantity(quantity);
                return false;
            }
        }

        final CartItemImpl newItem = new CartItemImpl();
        newItem.setSupplierCode(supplier);
        newItem.setProductSkuCode(sku);
        newItem.setProductName(skuName);
        newItem.setQuantity(quantity);
        newItem.setItemGroup(itemGroup);
        newItem.setConfigurable(configurable);
        newItem.setNotSoldSeparately(notSoldSeparately);
        getItems().add(newItem);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean addShippingToCart(final DeliveryBucket deliveryBucket,
                                     final String carrierSlaGUID,
                                     final String carrierSlaName,
                                     final BigDecimal quantity) {

        for (final CartItemImpl shipping : getShipping()) {
            if (ShoppingCartUtils.isCartItem(shipping, deliveryBucket, carrierSlaGUID)) {
                shipping.addQuantity(quantity);
                return false;
            }
        }

        final CartItemImpl newItem = new CartItemImpl();
        newItem.setSupplierCode(deliveryBucket.getSupplier());
        newItem.setProductSkuCode(carrierSlaGUID);
        newItem.setProductName(carrierSlaName);
        newItem.setQuantity(quantity);
        newItem.setDeliveryGroup(deliveryBucket.getGroup());
        getShipping().add(newItem);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean addGiftToCart(final String supplier,
                                 final String sku,
                                 final String skuName,
                                 final BigDecimal quantity,
                                 final String promotionCode) {

        for (final CartItemImpl gift : getGifts()) {
            if (ShoppingCartUtils.isCartItem(gift, supplier, sku)) {
                gift.addQuantity(quantity);
                addPromoCode(gift, promotionCode);
                return false;
            }
        }

        final CartItemImpl newItem = new CartItemImpl();
        newItem.setSupplierCode(supplier);
        newItem.setProductSkuCode(sku);
        newItem.setProductName(skuName);
        newItem.setQuantity(quantity);
        newItem.setGift(true);
        newItem.setPromoApplied(true);
        newItem.setAppliedPromo(promotionCode);
        getGifts().add(newItem);
        return true;
    }

    private void addPromoCode(final CartItemImpl item,
                              final String promotionCode) {

        final String promo = item.getAppliedPromo();
        if (promo == null) {
            item.setAppliedPromo(promotionCode);
            item.setPromoApplied(true);
        } else {
            final String[] promoCodes = StringUtils.split(promo, ',');
            // could do a binary search but in most cases this would be a single promo
            // and we have overhead of sorting before binary search
            boolean duplicate = false;
            for (final String promoCode : promoCodes) {
                if (promoCode.equals(promotionCode)) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                item.setAppliedPromo(item.getAppliedPromo() + "," + promotionCode);
                item.setPromoApplied(true);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean setProductSkuToCart(final String supplier,
                                       final String sku,
                                       final String skuName,
                                       final BigDecimal quantity,
                                       final String itemGroup) {

        final CartItemImpl newItem = new CartItemImpl();
        newItem.setSupplierCode(supplier);
        newItem.setProductSkuCode(sku);
        newItem.setProductName(skuName);
        newItem.setQuantity(quantity);
        newItem.setItemGroup(itemGroup);

        boolean updated = false;

        for (int i = 0; i < getItems().size(); i++) {
            final CartItemImpl item = getItems().get(i);
            if (ShoppingCartUtils.isCartItem(item, supplier, sku, itemGroup)) {
                getItems().set(i, newItem);
                updated = true;
            }
        }

        if (!updated) {
            getItems().add(newItem);
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean setProductSkuDeliveryBucket(final String sku,
                                               final DeliveryBucket deliveryBucket) {

        boolean updated = false;

        for (final CartItemImpl item : getItems()) {
            if (ShoppingCartUtils.isCartItem(item, deliveryBucket.getSupplier(), sku)) {

                final DeliveryBucket bucket = item.getDeliveryBucket();
                if (bucket == null || !bucket.equals(deliveryBucket)) {
                    item.setSupplierCode(deliveryBucket.getSupplier());
                    item.setDeliveryGroup(deliveryBucket.getGroup());
                    updated = true;
                }
            }
        }

        return updated;

    }

    /** {@inheritDoc} */
    @Override
    public boolean setGiftDeliveryBucket(final String sku,
                                         final DeliveryBucket deliveryBucket) {

        boolean updated = false;

        for (final CartItemImpl gift : getGifts()) {
            if (ShoppingCartUtils.isCartItem(gift, deliveryBucket.getSupplier(), sku)) {
                final DeliveryBucket bucket = gift.getDeliveryBucket();

                if (bucket == null || !bucket.equals(deliveryBucket)) {
                    gift.setSupplierCode(deliveryBucket.getSupplier());
                    gift.setDeliveryGroup(deliveryBucket.getGroup());
                    updated = true;
                }
            }
        }

        return updated;

    }

    /** {@inheritDoc} */
    @Override
    public boolean removeCartItem(final String supplier,
                                  final String productSku,
                                  final String itemGroup) {

        return getItems().removeIf(item -> ShoppingCartUtils.isCartItem(item, supplier, productSku, itemGroup));

    }

    /** {@inheritDoc} */
    @Override
    public boolean removeCartItemQuantity(final String supplier,
                                          final String productSku,
                                          final BigDecimal quantity,
                                          final String itemGroup) {

        boolean updated = false;

        final Iterator<CartItemImpl> itemIt = getItems().iterator();
        while (itemIt.hasNext()) {
            final CartItemImpl item = itemIt.next();
            if (ShoppingCartUtils.isCartItem(item, supplier, productSku, itemGroup)) {
                try {
                    item.removeQuantity(quantity);
                } catch (final CartItemRequiresDeletion cartItemRequiresDeletion) {
                    itemIt.remove();
                }
                updated = true;
            }
        }

        return updated;
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeItemPromotions() {
        boolean removed = !getGifts().isEmpty();
        getGifts().clear();

        for (final CartItemImpl item : getItems()) {
            if (item.isPromoApplied()) {
                // reinstate sale price
                removed = true;
                item.setPrice(item.getSalePrice() != null ? item.getSalePrice() : item.getListPrice());
                item.setAppliedPromo(null);
                item.setPromoApplied(false);
            }
        }
        return removed;
    }


    /** {@inheritDoc} */
    @Override
    public boolean removeItemOffers() {
        boolean removed = false;

        for (final CartItemImpl item : getItems()) {
            if (item.isFixedPrice()) {
                // reinstate sale price
                removed = true;
                item.setPrice(item.getSalePrice() != null ? item.getSalePrice() : item.getListPrice());
                item.setAppliedPromo(null);
                item.setFixedPrice(false);
            }
        }
        return removed;
    }


    /** {@inheritDoc} */
    @Override
    public boolean removeShipping() {
        if (getShipping().isEmpty()) {
            return false;
        }
        getShipping().clear();
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeShipping(final String carrierSlaGUID,
                                  final DeliveryBucket deliveryBucket) {

        return getShipping().removeIf(shipping -> ShoppingCartUtils.isCartItem(shipping, deliveryBucket, carrierSlaGUID));

    }

    /** {@inheritDoc} */
    @Override
    public boolean setProductSkuPrice(final String supplier,
                                      final String skuCode,
                                      final BigDecimal salePrice,
                                      final BigDecimal listPrice) {

        boolean updated = false;

        for (final CartItemImpl item : getItems()) {

            if (ShoppingCartUtils.isCartItem(item, supplier, skuCode)) {

                if (item.isFixedPrice()) {
                    if (MoneyUtils.isFirstBiggerThanSecond(salePrice, item.getPrice())) {
                        // Do not overwrite the offers, only base prices
                        item.setSalePrice(salePrice);
                        item.setListPrice(listPrice);
                    } else {
                        // else fixed price is more than sale do not show sale price
                        item.setSalePrice(item.getPrice());
                        item.setListPrice(item.getPrice());
                    }
                } else {
                    item.setPrice(salePrice != null ? salePrice : listPrice);
                    item.setSalePrice(salePrice);
                    item.setListPrice(listPrice);
                    // clear promotion as we effectively changed the base price for promo calculations
                    item.setAppliedPromo(null);
                    item.setPromoApplied(false);
                }
                updated = true;

            }

        }

        return updated;
    }

    /** {@inheritDoc} */
    @Override
    public boolean setShippingPrice(final String carrierSlaGUID,
                                    final DeliveryBucket deliveryBucket,
                                    final BigDecimal salePrice,
                                    final BigDecimal listPrice) {

        boolean updated = false;

        for (final CartItemImpl shipping : getShipping()) {
            if (ShoppingCartUtils.isCartItem(shipping, deliveryBucket, carrierSlaGUID)) {
                shipping.setPrice(salePrice != null ? salePrice : listPrice);
                shipping.setSalePrice(salePrice);
                shipping.setListPrice(listPrice);
                // clear promotion as we effectively changed the base price for promo calculations
                shipping.setAppliedPromo(null);
                shipping.setPromoApplied(false);

                updated = true;
            }
        }

        return updated;
    }

    /** {@inheritDoc} */
    @Override
    public boolean setGiftPrice(final String supplier,
                                final String skuCode,
                                final BigDecimal salePrice,
                                final BigDecimal listPrice) {

        boolean updated = false;

        for (final CartItemImpl gift : getGifts()) {
            if (ShoppingCartUtils.isCartItem(gift, supplier, skuCode)) {
                gift.setPrice(MoneyUtils.ZERO);
                gift.setNetPrice(MoneyUtils.ZERO);
                gift.setGrossPrice(MoneyUtils.ZERO);
                gift.setTaxRate(MoneyUtils.ZERO);
                gift.setTaxCode("");
                gift.setTaxExclusiveOfPrice(false);
                gift.setSalePrice(salePrice);
                gift.setListPrice(listPrice);
                // must not clear any promotion data
                updated = true;
            }
        }

        return updated;
    }

    /** {@inheritDoc} */
    @Override
    public boolean setProductSkuPromotion(final String supplier,
                                          final String skuCode,
                                          final BigDecimal promoPrice,
                                          final String promoCode) {

        boolean updated = false;

        for (final CartItemImpl item : getItems()) {

            if (ShoppingCartUtils.isCartItem(item, supplier, skuCode)) {
                item.setPrice(promoPrice);
                addPromoCode(item, promoCode);
                updated = true;
            }

        }

        return updated;
    }

    /** {@inheritDoc} */
    @Override
    public boolean setProductSkuOffer(final String supplier,
                                      final String skuCode,
                                      final BigDecimal fixedPrice,
                                      final String authCode) {

        boolean updated = false;

        for (final CartItemImpl item : getItems()) {

            if (ShoppingCartUtils.isCartItem(item, supplier, skuCode)) {
                item.setPrice(fixedPrice);
                item.setAppliedPromo(authCode); // Only one Auth code for offer
                item.setPromoApplied(false); // This is not promotion, promotions are removed every time we recalculate
                item.setFixedPrice(true); // Offers do not participate in promotions

                getOrderInfo().putDetail(
                        AttributeNamesKeys.Cart.ORDER_INFO_ORDER_LINE_PRICE_REF_ID + supplier + "_" + skuCode,
                        StringUtils.isNotBlank(authCode) ? authCode : null
                );

                updated = true;
            }

        }

        return updated;
    }

    /** {@inheritDoc} */
    @Override
    public boolean setShippingPromotion(final String carrierSlaGUID,
                                        final DeliveryBucket deliveryBucket,
                                        final BigDecimal promoPrice,
                                        final String promoCode) {

        boolean updated = false;

        for (final CartItemImpl shipping : getShipping()) {
            if (ShoppingCartUtils.isCartItem(shipping, deliveryBucket, carrierSlaGUID)) {
                shipping.setPrice(promoPrice);
                addPromoCode(shipping, promoCode);
                updated = true;
            }
        }

        return updated;

    }

    /** {@inheritDoc} */
    @Override
    public boolean setProductSkuTax(final String supplier,
                                    final String skuCode,
                                    final BigDecimal netPrice,
                                    final BigDecimal grossPrice,
                                    final BigDecimal rate,
                                    final String taxCode,
                                    final boolean exclPrice) {

        boolean updated = false;

        for (final CartItemImpl item : getItems()) {

            if (ShoppingCartUtils.isCartItem(item, supplier, skuCode)) {
                item.setNetPrice(netPrice);
                item.setGrossPrice(grossPrice);
                item.setTaxRate(rate);
                item.setTaxCode(taxCode);
                item.setTaxExclusiveOfPrice(exclPrice);
                updated = true;
            }

        }

        return updated;
    }

    /** {@inheritDoc} */
    @Override
    public boolean setShippingTax(final String carrierSlaGUID, final DeliveryBucket deliveryBucket, final BigDecimal netPrice, final BigDecimal grossPrice, final BigDecimal rate, final String taxCode, final boolean exclPrice) {

        boolean updated = false;

        for (final CartItemImpl shipping : getShipping()) {
            if (ShoppingCartUtils.isCartItem(shipping, deliveryBucket, carrierSlaGUID)) {
                shipping.setNetPrice(netPrice);
                shipping.setGrossPrice(grossPrice);
                shipping.setTaxRate(rate);
                shipping.setTaxCode(taxCode);
                shipping.setTaxExclusiveOfPrice(exclPrice);
                updated = true;
            }
        }

        return updated;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getCoupons() {
        return Collections.unmodifiableList(new ArrayList<>(coupons));
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public List<String> getAppliedCoupons() {

        final List<String> all = new ArrayList<>(coupons);
        final List<String> applied = new ArrayList<>();

        if (!all.isEmpty()) {
            checkCouponPromoApplied(total.getAppliedOrderPromo(), all, applied);
            checkCouponPromoApplied(total.getAppliedDeliveryPromo(), all, applied);
        }

        if (!all.isEmpty()) {
            for (final CartItem item : getItems()) {
                if (all.isEmpty()) {
                    break;
                }
                final String appliedPromo = item.getAppliedPromo();
                checkCouponPromoApplied(appliedPromo, all, applied);
            }
        }

        if (!all.isEmpty()) {
            for (final CartItem gift : getGifts()) {
                if (all.isEmpty()) {
                    break;
                }
                final String appliedPromo = gift.getAppliedPromo();
                checkCouponPromoApplied(appliedPromo, all, applied);
            }
        }

        return applied;
    }

    private void checkCouponPromoApplied(final String appliedPromo, final List<String> all, final List<String> applied) {
        if (appliedPromo != null) {
            for (final String promo : StringUtils.split(appliedPromo, ',')) {
                final Iterator<String> enteredIt = all.iterator();
                while (enteredIt.hasNext()) {
                    final String entered = enteredIt.next();
                    if (promo.endsWith(":" + entered)) { // format of coupon promo is PROMOCODE:COUPON
                        applied.add(entered);
                        enteredIt.remove();
                        break;
                    }
                }
                if (all.isEmpty()) {
                    break;
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean addCoupon(final String coupon) {
        return coupons.add(coupon);
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeCoupon(final String coupon) {
        return coupons.remove(coupon);
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public int getCartItemsCount() {

        return ShoppingCartUtils.getCartItemsCount(getItems(), getGifts());

    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public List<String> getCartItemsSuppliers() {

        return ShoppingCartUtils.getCartItemsSuppliers(getItems(), getGifts());

    }

    /**
     * Internal access to mutable items.
     */
    List<CartItemImpl> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    /**
     * Internal access to mutable items.
     */
    List<CartItemImpl> getGifts() {
        if (gifts == null) {
            gifts = new ArrayList<>();
        }
        return gifts;
    }

    /**
     * Internal access to mutable items.
     */
    List<CartItemImpl> getShipping() {
        if (shipping == null) {
            shipping = new ArrayList<>();
        }
        return shipping;
    }

    /** {@inheritDoc} */
    @Override
    public String getCurrencyCode() {
        return currencyCode;
    }

    /** {@inheritDoc} */
    @Override
    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public String getCustomerName() {
        return getShoppingContext().getCustomerName();
    }

    /** {@inheritDoc} */
    @Override
    public long getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isModified() {
        return processingStartTimestamp < modifiedTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean hasGifts() {
        return !getGifts().isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public boolean isPromotionsDisabled() {
        return promotionsDisabled;
    }

    /** {@inheritDoc} */
    @Override
    public void setPromotionsDisabled(final boolean promotionsDisabled) {
        this.promotionsDisabled = promotionsDisabled;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public String getOrdernum() {
        return ordernum;
    }

    /** {@inheritDoc} */
    @Override
    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public String getCustomerEmail() {
        return  getShoppingContext().getCustomerEmail();
    }

    /** {@inheritDoc} */
    @Override
    @JsonIgnore
    public int getLogonState() {
        if (StringUtils.isBlank(getCustomerEmail())
                   && StringUtils.isNotBlank(getCustomerName())) {
            return ShoppingCart.SESSION_EXPIRED;
        } else if (StringUtils.isNotBlank(getCustomerEmail())
                   && StringUtils.isNotBlank(getCustomerName())) {
            final String currentShop = getShoppingContext().getCustomerShopCode();
            if (getShoppingContext().getCustomerShops().contains(currentShop)) {
                return ShoppingCart.LOGGED_IN;
            }
            return ShoppingCart.INACTIVE_FOR_SHOP;
        }
        return ShoppingCart.NOT_LOGGED;
    }

    /** {@inheritDoc} */
    @Override
    public MutableShoppingContext getShoppingContext() {
        if (shoppingContext == null) {
            shoppingContext = new ShoppingContextImpl();
        }
        return shoppingContext;
    }

    /** {@inheritDoc} */
    @Override
    public String getCurrentLocale() {
        return currentLocale;
    }

    /** {@inheritDoc} */
    @Override
    public void setCurrentLocale(final String currentLocale) {
        this.currentLocale = currentLocale;
    }

    /** {@inheritDoc} */
    @Override
    public MutableOrderInfo getOrderInfo() {
        if (orderInfo == null) {
            orderInfo = new OrderInfoImpl();
        }
        return orderInfo;
    }

    /** {@inheritDoc} */
    @Override
    public long getProcessingStartTimestamp() {
        return processingStartTimestamp;
    }

    /** {@inheritDoc} */
    @Override
    public Total getTotal() {
        if (total == null) {
            LOG.error("Total requested before cart was recalculated - revise page flow to ensure that recalculation happens");
            total = new TotalImpl();
        }
        return total;
    }
}
