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
    private List<CartItem> cartItems = new ArrayList<CartItem>();
    private List<CartItem> items = new ArrayList<CartItem>();
    private List<CartItem> gifts = new ArrayList<CartItem>();
    @DtoField(readOnly = true)
    private List<String> coupons = new ArrayList<String>();
    @DtoField(readOnly = true)
    private List<String> appliedCoupons = new ArrayList<String>();
    @DtoCollection(
            value = "shippingList",
            dtoBeanKey = "org.yes.cart.shoppingcart.CartItem",
            entityGenericType = CartItem.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<CartItem> shipping = new ArrayList<CartItem>();

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


    public String getGuid() {
        return guid;
    }

    public List<String> getCoupons() {
        return coupons;
    }

    public List<String> getAppliedCoupons() {
        return appliedCoupons;
    }


    public List<CartItem> getItems() {
        if (items == null) {
            items = new ArrayList<CartItem>();
        }
        return items;
    }

    public List<CartItem> getGifts() {
        if (gifts == null) {
            gifts = new ArrayList<CartItem>();
        }
        return gifts;
    }

    public List<CartItem> getShipping() {
        if (shipping == null) {
            shipping = new ArrayList<CartItem>();
        }
        return shipping;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public long getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    public boolean isModified() {
        return modified;
    }

    public int getLogonState() {
        return logonState;
    }

    public ShoppingContext getShoppingContext() {
        if (shoppingContext == null) {
            shoppingContext = new CartShoppingContextDTOImpl();
        }
        return shoppingContext;
    }

    public String getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(final String currentLocale) {
        this.currentLocale = currentLocale;
    }

    public OrderInfo getOrderInfo() {
        if (orderInfo == null) {
            orderInfo = new CartOrderInfoDTOImpl();
        }
        return orderInfo;
    }

    public long getProcessingStartTimestamp() {
        return processingStartTimestamp;
    }

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
        final List<CartItem> itemsOnly = new ArrayList<CartItem>();
        final List<CartItem> giftsOnly = new ArrayList<CartItem>();
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

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    public List<CartItem> getCartItemList() {

        return ShoppingCartUtils.getCartItemImmutableList(getItems(), getGifts());

    }

    public Map<DeliveryBucket, List<CartItem>> getCartItemMap() {

        return ShoppingCartUtils.getCartItemImmutableMap(getItems(), getGifts());

    }

    public List<CartItem> getShippingList() {

        return ShoppingCartUtils.getShippingImmutableList(getShipping());

    }


    public Map<DeliveryBucket, List<CartItem>> getShippingListMap() {

        return ShoppingCartUtils.getShippingImmutableMap(getShipping());

    }


    /** {@inheritDoc} */
    public BigDecimal getProductSkuQuantity(final String sku) {
        final int skuIndex = indexOfProductSku(sku);
        if (skuIndex == -1) { //not found
            return BigDecimal.ZERO;
        }
        return getItems().get(skuIndex).getQty();
    }

    public int getCartItemsCount() {

        return ShoppingCartUtils.getCartItemsCount(getItems(), getGifts());

    }

    public List<String> getCartItemsSuppliers() {

        return ShoppingCartUtils.getCartItemsSuppliers(getItems(), getGifts());

    }

    public String getCustomerName() {
        return getShoppingContext().getCustomerName();
    }

    public String getCustomerEmail() {
        return getShoppingContext().getCustomerEmail();
    }

    public boolean isSeparateBillingAddress() {
        return getOrderInfo().isSeparateBillingAddress();
    }

    public boolean isBillingAddressNotRequired() {
        return getOrderInfo().isBillingAddressNotRequired();
    }

    public boolean isDeliveryAddressNotRequired() {
        return getOrderInfo().isDeliveryAddressNotRequired();
    }

    public Map<String, Long> getCarrierSlaId() {
        return getOrderInfo().getCarrierSlaId();
    }

    public boolean isAllCarrierSlaSelected() {

        return ShoppingCartUtils.isAllCarrierSlaSelected(getItems(), getGifts(), getCarrierSlaId());

    }

    public boolean isAllCartItemsBucketed() {

        return ShoppingCartUtils.isAllCartItemsBucketed(getItems(), getGifts());

    }

    public String getOrderMessage() {
        return getOrderInfo().getOrderMessage();
    }

    public boolean contains(final String skuCode) {
        return (indexOfProductSku(skuCode) != -1);
    }

    public int indexOfShipping(final String carrierSlaGUID, final DeliveryBucket deliveryBucket) {
        return ShoppingCartUtils.indexOf(carrierSlaGUID, deliveryBucket, getShipping());
    }

    public int indexOfProductSku(final String skuCode) {
        return ShoppingCartUtils.indexOf(skuCode, getItems());
    }

    public int indexOfGift(final String skuCode) {
        return ShoppingCartUtils.indexOf(skuCode, getGifts());
    }

}
