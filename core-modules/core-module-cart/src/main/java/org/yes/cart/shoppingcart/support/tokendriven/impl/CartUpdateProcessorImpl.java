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

package org.yes.cart.shoppingcart.support.tokendriven.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.support.tokendriven.CartUpdateProcessor;
import org.yes.cart.shoppingcart.support.tokendriven.ShoppingCartStateSerializer;

import java.util.*;

/**
 * User: denispavlov
 * Date: 22/08/2014
 * Time: 22:50
 */
public class CartUpdateProcessorImpl implements CartUpdateProcessor {

    private final ShoppingCartStateService shoppingCartStateService;
    private final AddressService addressService;
    private final ShoppingCartCommandFactory shoppingCartCommandFactory;
    private final ShoppingCartStateSerializer shoppingCartStateSerializer;

    public CartUpdateProcessorImpl(final ShoppingCartStateService shoppingCartStateService,
                                   final AddressService addressService,
                                   final ShoppingCartCommandFactory shoppingCartCommandFactory,
                                   final ShoppingCartStateSerializer shoppingCartStateSerializer) {
        this.shoppingCartStateService = shoppingCartStateService;
        this.addressService = addressService;
        this.shoppingCartCommandFactory = shoppingCartCommandFactory;
        this.shoppingCartStateSerializer = shoppingCartStateSerializer;
    }

    /** {@inheritDoc} */
    @Override
    public void updateShoppingCart(final ShoppingCart shoppingCart) {

        // 1. Need to find this cart by guid in db
        ShoppingCartState dbState = shoppingCartStateService.findByGuid(shoppingCart.getGuid());
        if (dbState == null) {
            dbState = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
            dbState.setGuid(shoppingCart.getGuid());
            dbState.setOrdernum(shoppingCart.getOrdernum());
        }

        // 2. If this is for logged in cart now but was anonymous we have just logged in
        if (shoppingCart.getLogonState() == ShoppingCart.LOGGED_IN && StringUtils.isBlank(dbState.getCustomerEmail())) {

            // 3. Make sure we save the customer email
            dbState.setCustomerEmail(shoppingCart.getCustomerEmail());

            // 4. Copy all items from old carts to current and delete them
            final List<ShoppingCartState> oldCartStates = shoppingCartStateService.findByCustomerEmail(shoppingCart.getCustomerEmail());
            final Map<String, Object> cmdParams = new HashMap<>();
            for (final ShoppingCartState oldCartState : oldCartStates) {
                // 4.1. Skip same cart
                if (dbState.getGuid().equals(oldCartState.getGuid())) {
                    continue;
                }
                final ShoppingCart oldCart = restoreStateInternal(oldCartState.getState());
                // 4.2. Merge only valid restored carts that are not
                if (oldCart != null) {
                    // 4.3. Only merge carts from the same shop as we may have mismatch on SKU's
                    if (shoppingCart.getShoppingContext().getShopCode().equals(oldCart.getShoppingContext().getShopCode())) {

                        mergeNonGiftSKU(shoppingCart, oldCart, cmdParams);
                        mergeCouponCodes(shoppingCart, oldCart, cmdParams);
                        mergeShoppingContext(shoppingCart, oldCart, cmdParams);
                        mergeOrderInfo(shoppingCart, oldCart, cmdParams);

                        // 4.4. Remove merged cart state
                        shoppingCartStateService.delete(oldCartState);
                    }
                }
            }
        }

        // 5. Store new state
        dbState.setEmpty(shoppingCart.getCartItemsCount() == 0);
        dbState.setState(saveState(shoppingCart));

        // 6. Persist
        if (dbState.getState() != null && dbState.getState().length > 0) {
            if (dbState.getShoppingCartStateId() > 0L) {
                shoppingCartStateService.update(dbState);
            } else {
                shoppingCartStateService.create(dbState);
            }
        }

    }

    private void mergeOrderInfo(final ShoppingCart shoppingCart, final ShoppingCart oldCart, final Map<String, Object> cmdParams) {

        final OrderInfo oldCartInfo = oldCart.getOrderInfo();

        final Map<String, Long> slaSelection = shoppingCart.getCarrierSlaId();
        final StringBuilder slaSelectionParam = new StringBuilder();
        for (final Map.Entry<String, Long> sla : slaSelection.entrySet()) {
            if (slaSelectionParam.length() > 0) {
                slaSelectionParam.append('|');
            }
            slaSelectionParam.append(sla.getValue());
            if (StringUtils.isNotBlank(sla.getKey())) {
                slaSelectionParam.append('-').append(sla.getKey());
            }
        }

        if (!slaSelection.isEmpty()) {

            cmdParams.clear();

            cmdParams.put(ShoppingCartCommand.CMD_SEPARATEBILLING, String.valueOf(oldCartInfo.isSeparateBillingAddress()));

            shoppingCartCommandFactory.execute(shoppingCart, cmdParams);

            cmdParams.clear();

            // Need to reinstate old addresses too because method may not be applicable for different address
            cmdParams.put(ShoppingCartCommand.CMD_SETCARRIERSLA, slaSelectionParam.toString());
            if (oldCartInfo.getBillingAddressId() != null) {
                final Address billing = addressService.findById(oldCartInfo.getBillingAddressId());
                if (billing != null) {
                    cmdParams.put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_BILLING_ADDRESS, billing);
                }
            }
            if (oldCartInfo.getDeliveryAddressId() != null) {
                final Address delivery = addressService.findById(oldCartInfo.getDeliveryAddressId());
                if (delivery != null) {
                    cmdParams.put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_DELIVERY_ADDRESS, delivery);
                }
            }

            shoppingCartCommandFactory.execute(shoppingCart, cmdParams);

        }
    }

    private void mergeShoppingContext(final ShoppingCart shoppingCart, final ShoppingCart oldCart, final Map<String, Object> cmdParams) {

        final ShoppingContext oldCartCtx = oldCart.getShoppingContext();
        final ShoppingContext shoppingCartCtx = shoppingCart.getShoppingContext();
        if (CollectionUtils.isNotEmpty(oldCartCtx.getLatestViewedSkus()) && shoppingCartCtx instanceof MutableShoppingContext) {

            // Keep reference to current SKU
            final List<String> thisCartSku = shoppingCartCtx.getLatestViewedSkus();
            final List<String> oldCartSkuCopy = new ArrayList<>(oldCartCtx.getLatestViewedSkus());

            // Set old ones first
            ((MutableShoppingContext) shoppingCartCtx).setLatestViewedSkus(oldCartSkuCopy);

            // replay new ones
            if (CollectionUtils.isNotEmpty(thisCartSku)) {
                cmdParams.clear();
                cmdParams.put(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, thisCartSku);
                shoppingCartCommandFactory.execute(shoppingCart, cmdParams);
            }

        }
        /*
        if (CollectionUtils.isNotEmpty(oldCartCtx.getLatestViewedCategories()) && shoppingCartCtx instanceof MutableShoppingContext) {

            TODO: YC-360 Track latest viewed categories in shopping context

        }*/

    }

    private void mergeCouponCodes(final ShoppingCart shoppingCart, final ShoppingCart oldCart, final Map<String, Object> cmdParams) {

        for (final String coupon : oldCart.getCoupons()) {
            // Only add coupon codes that are not in the current cart
            if (!shoppingCart.getCoupons().contains(coupon)) {

                cmdParams.clear();

                cmdParams.put(ShoppingCartCommand.CMD_ADDCOUPON, coupon);

                shoppingCartCommandFactory.execute(shoppingCart, cmdParams);

            }

        }
    }

    private void mergeNonGiftSKU(final ShoppingCart shoppingCart, final ShoppingCart oldCart, final Map<String, Object> cmdParams) {

        for (final CartItem cartItem : oldCart.getCartItemList()) {
            // Only merge non gifts and items that are NOT already in the cart
            if (!cartItem.isGift() && shoppingCart.indexOfProductSku(cartItem.getProductSkuCode()) == -1) {

                cmdParams.clear();

                cmdParams.put(ShoppingCartCommand.CMD_ADDTOCART, cartItem.getProductSkuCode());
                cmdParams.put(ShoppingCartCommand.CMD_ADDTOCART_P_QTY, cartItem.getQty().toPlainString());

                shoppingCartCommandFactory.execute(shoppingCart, cmdParams);

            }
        }

    }

    /** {@inheritDoc} */
    @Override
    public void invalidateShoppingCart(final ShoppingCart shoppingCart) {

        shoppingCartCommandFactory.execute(shoppingCart,
                Collections.singletonMap(ShoppingCartCommand.CMD_EXPIRE, ShoppingCartCommand.CMD_EXPIRE));

    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart restoreState(final byte[] bytes) {

        final ShoppingCart cart = restoreStateInternal(bytes);

        if (cart instanceof MutableShoppingCart) {
            // deserializing old cart may contain invalid settings
            shoppingCartCommandFactory.execute(cart,
                    Collections.singletonMap(ShoppingCartCommand.CMD_RESTORE, ShoppingCartCommand.CMD_RESTORE));
        }

        return cart;

    }

    protected ShoppingCart restoreStateInternal(final byte[] bytes) {

        return shoppingCartStateSerializer.restoreState(bytes);

    }


    /** {@inheritDoc} */
    @Override
    public byte[] saveState(final ShoppingCart shoppingCart) {

        return shoppingCartStateSerializer.saveState(shoppingCart);

    }

}
