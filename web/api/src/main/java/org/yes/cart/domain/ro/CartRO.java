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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.shoppingcart.CartItem;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
@JsonIgnoreProperties({
    "cartItems"
})
@XmlRootElement(name = "cart")
public class CartRO implements Serializable {

    private static final long serialVersionUID =  20110509L;

    @DtoCollection(
            value = "cartItemList",
            dtoBeanKey = "org.yes.cart.domain.ro.CartItemRO",
            entityGenericType = CartItem.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<CartItemRO> cartItems = new ArrayList<CartItemRO>();
    private List<CartItemRO> items = new ArrayList<CartItemRO>();
    private List<CartItemRO> gifts = new ArrayList<CartItemRO>();
    @DtoField(readOnly = true)
    private List<String> coupons = new ArrayList<String>();
    @DtoField(readOnly = true)
    private List<String> appliedCoupons = new ArrayList<String>();
    @DtoCollection(
            value = "shippingList",
            dtoBeanKey = "org.yes.cart.domain.ro.CartItemRO",
            entityGenericType = CartItem.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<CartItemRO> shipping = new ArrayList<CartItemRO>();

    @DtoField(readOnly = true)
    private String guid;

    @DtoField(readOnly = true)
    private String currentLocale;

    @DtoField(readOnly = true)
    private String currencyCode;
    private String symbol;
    private String symbolPosition;

    @DtoField(readOnly = true)
    private long modifiedTimestamp;

    @DtoField(readOnly = true)
    private boolean modified;

    @DtoField(readOnly = true)
    private int logonState;

    @DtoField(readOnly = true)
    private long processingStartTimestamp;

    @DtoField(
            readOnly = true,
            dtoBeanKey = "org.yes.cart.domain.ro.CartShoppingContextRO"
    )
    private CartShoppingContextRO shoppingContext = new CartShoppingContextRO();

    @DtoField(
            readOnly = true,
            dtoBeanKey = "org.yes.cart.domain.ro.CartOrderInfoRO"
    )
    private CartOrderInfoRO orderInfo = new CartOrderInfoRO();

    @DtoField(
            readOnly = true,
            dtoBeanKey = "org.yes.cart.domain.ro.CartTotalRO"
    )
    private CartTotalRO total = new CartTotalRO();


    @XmlAttribute(name = "guid")
    public String getGuid() {
        return guid;
    }

    @XmlElementWrapper(name = "coupons")
    @XmlElement(name = "coupon")
    public List<String> getCoupons() {
        return coupons;
    }

    @XmlElementWrapper(name = "applied-coupons")
    @XmlElement(name = "applied-coupon")
    public List<String> getAppliedCoupons() {
        return appliedCoupons;
    }


    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    public List<CartItemRO> getItems() {
        if (items == null) {
            items = new ArrayList<CartItemRO>();
        }
        return items;
    }

    @XmlElementWrapper(name = "gifts")
    @XmlElement(name = "gift")
    public List<CartItemRO> getGifts() {
        if (gifts == null) {
            gifts = new ArrayList<CartItemRO>();
        }
        return gifts;
    }

    @XmlElementWrapper(name = "shipping-costs")
    @XmlElement(name = "shipping-cost")
    public List<CartItemRO> getShipping() {
        if (shipping == null) {
            shipping = new ArrayList<CartItemRO>();
        }
        return shipping;
    }

    @XmlAttribute(name = "currency")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @XmlAttribute(name = "symbol")
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    @XmlAttribute(name = "symbol-position")
    public String getSymbolPosition() {
        return symbolPosition;
    }

    public void setSymbolPosition(final String symbolPosition) {
        this.symbolPosition = symbolPosition;
    }


    @XmlAttribute(name = "modified-timestamp")
    public long getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    @XmlAttribute(name = "modified")
    public boolean isModified() {
        return modified;
    }

    @XmlAttribute(name = "logon-state")
    public int getLogonState() {
        return logonState;
    }

    @XmlElement(name = "shopping-context")
    public CartShoppingContextRO getShoppingContext() {
        if (shoppingContext == null) {
            shoppingContext = new CartShoppingContextRO();
        }
        return shoppingContext;
    }

    @XmlAttribute(name = "locale")
    public String getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(final String currentLocale) {
        this.currentLocale = currentLocale;
    }

    @XmlElement(name = "order-info")
    public CartOrderInfoRO getOrderInfo() {
        if (orderInfo == null) {
            orderInfo = new CartOrderInfoRO();
        }
        return orderInfo;
    }

    @XmlAttribute(name = "processing-timestamp")
    public long getProcessingStartTimestamp() {
        return processingStartTimestamp;
    }

    @XmlElement(name = "total")
    public CartTotalRO getTotal() {
        if (total == null) {
            total = new CartTotalRO();
        }
        return total;
    }

    @XmlTransient
    public List<CartItemRO> getCartItems() {
        return cartItems;
    }

    public void setCartItems(final List<CartItemRO> cartItems) {
        final List<CartItemRO> itemsOnly = new ArrayList<CartItemRO>();
        final List<CartItemRO> giftsOnly = new ArrayList<CartItemRO>();
        for (final CartItemRO item : cartItems) {
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

    public void setShipping(final List<CartItemRO> shipping) {
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

    public void setShoppingContext(final CartShoppingContextRO shoppingContext) {
        this.shoppingContext = shoppingContext;
    }

    public void setOrderInfo(final CartOrderInfoRO orderInfo) {
        this.orderInfo = orderInfo;
    }

    public void setTotal(final CartTotalRO total) {
        this.total = total;
    }

    public void setModified(final boolean modified) {
        this.modified = modified;
    }

}
