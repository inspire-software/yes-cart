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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.service.order.impl.DeliveryBucketImpl;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
@Dto
public class CartDTOImpl implements ShoppingCart, Serializable {

    private static final long serialVersionUID =  20110509L;

    private static final DeliveryBucket DEFAULT = new DeliveryBucketImpl("", "");

    @DtoCollection(
            value = "cartItemList",
            dtoBeanKey = "org.yes.cart.shoppingcart.CartItem",
            entityGenericType = CartItem.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<CartItem> cartItems = new ArrayList<>();
    private List<CartItem> items = new ArrayList<>();
    private List<CartItem> gifts = new ArrayList<>();
    @DtoField(readOnly = true)
    private List<String> coupons = new ArrayList<>();
    @DtoField(readOnly = true)
    private List<String> appliedCoupons = new ArrayList<>();
    @DtoCollection(
            value = "shippingList",
            dtoBeanKey = "org.yes.cart.shoppingcart.CartItem",
            entityGenericType = CartItem.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<CartItem> shipping = new ArrayList<>();

    @DtoField(readOnly = true)
    private String guid;

    @DtoField(readOnly = true)
    private String currentLocale;

    @DtoField(readOnly = true)
    private String currencyCode;

    @DtoField(readOnly = true)
    private long modifiedTimestamp;

    @DtoField(readOnly = true)
    private boolean modified;

    @DtoField(readOnly = true)
    private int logonState;

    @DtoField(readOnly = true)
    private long processingStartTimestamp;

    @DtoField(readOnly = true)
    private String ordernum;

    @DtoField(
            readOnly = true,
            dtoBeanKey = "org.yes.cart.shoppingcart.ShoppingContext"
    )
    private ShoppingContext shoppingContext = new CartShoppingContextDTOImpl();

    @DtoField(
            readOnly = true,
            dtoBeanKey = "org.yes.cart.shoppingcart.OrderInfo"
    )
    private OrderInfo orderInfo = new CartOrderInfoDTOImpl();

    @DtoField(
            readOnly = true,
            dtoBeanKey = "org.yes.cart.shoppingcart.Total"
    )
    private Total total = new CartTotalDTOImpl();


    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public List<String> getCoupons() {
        return coupons;
    }

    @Override
    public List<String> getAppliedCoupons() {
        return appliedCoupons;
    }


    public List<CartItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public List<CartItem> getGifts() {
        if (gifts == null) {
            gifts = new ArrayList<>();
        }
        return gifts;
    }

    public List<CartItem> getShipping() {
        if (shipping == null) {
            shipping = new ArrayList<>();
        }
        return shipping;
    }

    @Override
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public long getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public int getLogonState() {
        return logonState;
    }

    @Override
    public ShoppingContext getShoppingContext() {
        if (shoppingContext == null) {
            shoppingContext = new CartShoppingContextDTOImpl();
        }
        return shoppingContext;
    }

    @Override
    public String getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(final String currentLocale) {
        this.currentLocale = currentLocale;
    }

    @Override
    public OrderInfo getOrderInfo() {
        if (orderInfo == null) {
            orderInfo = new CartOrderInfoDTOImpl();
        }
        return orderInfo;
    }

    @Override
    public long getProcessingStartTimestamp() {
        return processingStartTimestamp;
    }

    @Override
    public Total getTotal() {
        if (total == null) {
            total = new CartTotalDTOImpl();
        }
        return total;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(final List<CartItem> cartItems) {
        final List<CartItem> itemsOnly = new ArrayList<>();
        final List<CartItem> giftsOnly = new ArrayList<>();
        for (final CartItem item : cartItems) {
            if (item.isGift()) {
                giftsOnly.add(item);
            } else {
                itemsOnly.add(item);
            }
        }
        this.cartItems = cartItems;
        this.items = itemsOnly;
        this.gifts = giftsOnly;
    }

    public void setCoupons(final List<String> coupons) {
        this.coupons = coupons;
    }

    public void setAppliedCoupons(final List<String> appliedCoupons) {
        this.appliedCoupons = appliedCoupons;
    }

    public void setLogonState(final int logonState) {
        this.logonState = logonState;
    }

    public void setShipping(final List<CartItem> shipping) {
        this.shipping = shipping;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public void setModifiedTimestamp(final long modifiedTimestamp) {
        this.modifiedTimestamp = modifiedTimestamp;
    }

    public void setProcessingStartTimestamp(final long processingStartTimestamp) {
        this.processingStartTimestamp = processingStartTimestamp;
    }

    public void setShoppingContext(final ShoppingContext shoppingContext) {
        this.shoppingContext = shoppingContext;
    }

    public void setOrderInfo(final OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public void setTotal(final Total total) {
        this.total = total;
    }

    public void setModified(final boolean modified) {
        this.modified = modified;
    }

    @Override
    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    @Override
    public List<CartItem> getCartItemList() {

        return ShoppingCartUtils.getCartItemImmutableList(getItems(), getGifts());

    }

    @Override
    public Map<DeliveryBucket, List<CartItem>> getCartItemMap() {

        return ShoppingCartUtils.getCartItemImmutableMap(getItems(), getGifts());

    }

    @Override
    public List<CartItem> getShippingList() {

        return ShoppingCartUtils.getShippingImmutableList(getShipping());

    }


    @Override
    public Map<DeliveryBucket, List<CartItem>> getShippingListMap() {

        return ShoppingCartUtils.getShippingImmutableMap(getShipping());

    }


    /** {@inheritDoc} */
    @Override
    public BigDecimal getProductSkuQuantity(final String supplier,
                                            final String sku) {

        final int skuIndex = indexOfProductSku(supplier, sku);
        if (skuIndex == -1) { //not found
            return BigDecimal.ZERO;
        }
        return getItems().get(skuIndex).getQty();
    }

    @Override
    public int getCartItemsCount() {

        return ShoppingCartUtils.getCartItemsCount(getItems(), getGifts());

    }

    @Override
    public List<String> getCartItemsSuppliers() {

        return ShoppingCartUtils.getCartItemsSuppliers(getItems(), getGifts());

    }

    @Override
    public String getCustomerName() {
        return getShoppingContext().getCustomerName();
    }

    @Override
    public String getCustomerEmail() {
        return getShoppingContext().getCustomerEmail();
    }

    @Override
    public boolean isSeparateBillingAddress() {
        return getOrderInfo().isSeparateBillingAddress();
    }

    @Override
    public boolean isBillingAddressNotRequired() {
        return getOrderInfo().isBillingAddressNotRequired();
    }

    @Override
    public boolean isDeliveryAddressNotRequired() {
        return getOrderInfo().isDeliveryAddressNotRequired();
    }

    @Override
    public Map<String, Long> getCarrierSlaId() {
        return getOrderInfo().getCarrierSlaId();
    }

    @Override
    public boolean isAllCarrierSlaSelected() {

        return ShoppingCartUtils.isAllCarrierSlaSelected(getItems(), getGifts(), getCarrierSlaId());

    }

    @Override
    public boolean isAllCartItemsBucketed() {

        return ShoppingCartUtils.isAllCartItemsBucketed(getItems(), getGifts());

    }

    @Override
    public String getOrderMessage() {
        return getOrderInfo().getOrderMessage();
    }

    @Override
    public boolean contains(final String supplier, final String skuCode) {
        return (indexOfProductSku(supplier, skuCode) != -1);
    }

    @Override
    public int indexOfShipping(final String carrierSlaGUID, final DeliveryBucket deliveryBucket) {
        return ShoppingCartUtils.indexOf(carrierSlaGUID, deliveryBucket, getShipping());
    }

    @Override
    public int indexOfProductSku(final String supplier, final String skuCode) {
        return ShoppingCartUtils.indexOf(supplier, skuCode, getItems());
    }

    @Override
    public int indexOfGift(final String supplier, final String skuCode) {
        return ShoppingCartUtils.indexOf(supplier, skuCode, getGifts());
    }

}
