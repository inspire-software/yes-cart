/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.vo.matcher.NoopMatcher;
import org.yes.cart.shoppingcart.CartItem;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 10/04/2018
 * Time: 19:53
 */
@Dto
public class VoCart {


    @DtoCollection(
            value = "cartItemList",
            dtoBeanKey = "VoCartItem",
            entityGenericType = CartItem.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<VoCartItem> cartItems = new ArrayList<>();
    @DtoField(readOnly = true)
    private List<String> coupons = new ArrayList<>();
    @DtoField(readOnly = true)
    private List<String> appliedCoupons = new ArrayList<>();
    @DtoCollection(
            value = "shippingList",
            dtoBeanKey = "VoCartItem",
            entityGenericType = CartItem.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<VoCartItem> shipping = new ArrayList<>();

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

    @DtoField(readOnly = true, dtoBeanKey = "VoCartShoppingContext")
    private VoCartShoppingContext shoppingContext = new VoCartShoppingContext();

    @DtoField(readOnly = true, dtoBeanKey = "VoCartOrderInfo")
    private VoCartOrderInfo orderInfo = new VoCartOrderInfo();

    @DtoField(readOnly = true, dtoBeanKey = "VoCartTotal")
    private VoCartTotal total = new VoCartTotal();

    public List<VoCartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(final List<VoCartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public List<String> getCoupons() {
        return coupons;
    }

    public void setCoupons(final List<String> coupons) {
        this.coupons = coupons;
    }

    public List<String> getAppliedCoupons() {
        return appliedCoupons;
    }

    public void setAppliedCoupons(final List<String> appliedCoupons) {
        this.appliedCoupons = appliedCoupons;
    }

    public List<VoCartItem> getShipping() {
        return shipping;
    }

    public void setShipping(final List<VoCartItem> shipping) {
        this.shipping = shipping;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(final String guid) {
        this.guid = guid;
    }

    public String getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(final String currentLocale) {
        this.currentLocale = currentLocale;
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

    public void setModifiedTimestamp(final long modifiedTimestamp) {
        this.modifiedTimestamp = modifiedTimestamp;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(final boolean modified) {
        this.modified = modified;
    }

    public int getLogonState() {
        return logonState;
    }

    public void setLogonState(final int logonState) {
        this.logonState = logonState;
    }

    public long getProcessingStartTimestamp() {
        return processingStartTimestamp;
    }

    public void setProcessingStartTimestamp(final long processingStartTimestamp) {
        this.processingStartTimestamp = processingStartTimestamp;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    public VoCartShoppingContext getShoppingContext() {
        return shoppingContext;
    }

    public void setShoppingContext(final VoCartShoppingContext shoppingContext) {
        this.shoppingContext = shoppingContext;
    }

    public VoCartOrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(final VoCartOrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public VoCartTotal getTotal() {
        return total;
    }

    public void setTotal(final VoCartTotal total) {
        this.total = total;
    }
}
