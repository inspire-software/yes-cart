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

package org.yes.cart.web.support.shoppingcart.tokendriven.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.shoppingcart.tokendriven.CartUpdateProcessor;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 22/08/2014
 * Time: 22:50
 */
public class CartUpdateProcessorImpl implements CartUpdateProcessor {

    private final ShoppingCartStateService shoppingCartStateService;
    private final ShoppingCartCommandFactory shoppingCartCommandFactory;

    public CartUpdateProcessorImpl(final ShoppingCartStateService shoppingCartStateService,
                                   final ShoppingCartCommandFactory shoppingCartCommandFactory) {
        this.shoppingCartStateService = shoppingCartStateService;
        this.shoppingCartCommandFactory = shoppingCartCommandFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void updateShoppingCart(final ShoppingCart shoppingCart) {

        // 1. Need to find this cart by guid in db
        ShoppingCartState dbState = shoppingCartStateService.findByGuid(shoppingCart.getGuid());
        if (dbState == null) {
            dbState = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
            dbState.setGuid(shoppingCart.getGuid());
        }

        // 2. If this is for logged in cart now but was anonymous we have just logged in
        if (shoppingCart.getLogonState() == ShoppingCart.LOGGED_IN && StringUtils.isBlank(dbState.getCustomerEmail())) {

            // 3. Make sure we save the customer email
            dbState.setCustomerEmail(shoppingCart.getCustomerEmail());

            // 4. Copy all items from old carts to current and delete them
            final List<ShoppingCartState> oldCartStates = shoppingCartStateService.findByCustomerEmail(shoppingCart.getCustomerEmail());
            final Map<String, Object> cmdParams = new HashMap<String, Object>();
            for (final ShoppingCartState oldCartState : oldCartStates) {
                // 4.1. Skip same cart
                if (dbState.getGuid().equals(oldCartState.getGuid())) {
                    continue;
                }
                final ShoppingCart oldCart = restoreState(oldCartState.getState());
                // 4.2. Merge only valid restored carts that are not
                if (oldCart != null) {
                    // 4.3. Only merge carts from the same shop as we may have mismatch on SKU's
                    if (shoppingCart.getShoppingContext().getShopCode().equals(oldCart.getShoppingContext().getShopCode())) {

                        mergeNonGiftSKU(shoppingCart, oldCart, cmdParams);
                        mergeCouponCodes(shoppingCart, oldCart, cmdParams);
                        mergeShoppingContext(shoppingCart, oldCart);
                        mergeOrderInfo(shoppingCart, oldCart, cmdParams);

                        // 4.4. Remove merged cart state
                        shoppingCartStateService.delete(oldCartState);
                    }
                }
            }
        }

        // 5. Store new state
        dbState.setState(saveState(shoppingCart));

        // 6. Persist
        if (dbState.getShoppingCartStateId() > 0L) {
            shoppingCartStateService.update(dbState);
        } else {
            shoppingCartStateService.create(dbState);
        }

    }

    private void mergeOrderInfo(final ShoppingCart shoppingCart, final ShoppingCart oldCart, final Map<String, Object> cmdParams) {

        final OrderInfo shoppingCartInfo = shoppingCart.getOrderInfo();
        final OrderInfo oldCartInfo = oldCart.getOrderInfo();
        if ((shoppingCartInfo.getCarrierSlaId() == null || shoppingCartInfo.getCarrierSlaId() == 0L) &&
                oldCartInfo.getCarrierSlaId() != null && oldCartInfo.getCarrierSlaId() > 0L) {

            cmdParams.clear();

            cmdParams.put(ShoppingCartCommand.CMD_SEPARATEBILLING, String.valueOf(oldCartInfo.isSeparateBillingAddress()));

            shoppingCartCommandFactory.execute(shoppingCart, cmdParams);

            cmdParams.clear();

            // Need to reinstate old addresses too because method may not be applicable for different address
            cmdParams.put(ShoppingCartCommand.CMD_SETCARRIERSLA, oldCartInfo.getCarrierSlaId().toString());
            if (oldCartInfo.getBillingAddressId() != null) {
                cmdParams.put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_BILLING_ADDRESS, oldCartInfo.getBillingAddressId().toString());
            }
            if (oldCartInfo.getDeliveryAddressId() != null) {
                cmdParams.put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_DELIVERY_ADDRESS, oldCartInfo.getDeliveryAddressId().toString());
            }

            shoppingCartCommandFactory.execute(shoppingCart, cmdParams);

        }
    }

    private void mergeShoppingContext(final ShoppingCart shoppingCart, final ShoppingCart oldCart) {

        final ShoppingContext oldCartCtx = oldCart.getShoppingContext();
        final ShoppingContext shoppingCartCtx = shoppingCart.getShoppingContext();
        if (oldCartCtx.getLatestViewedSkus() != null && shoppingCartCtx instanceof MutableShoppingContext) {
            final List<String> viewed = new ArrayList<String>();

            // Current cart first
            if (shoppingCartCtx.getLatestViewedSkus() != null) {
                viewed.addAll(shoppingCartCtx.getLatestViewedSkus());
            }
            // Then old
            viewed.addAll(oldCartCtx.getLatestViewedSkus());
            ((MutableShoppingContext) shoppingCartCtx).setLatestViewedSkus(viewed);
        }
        if (oldCartCtx.getLatestViewedCategories() != null && shoppingCartCtx instanceof MutableShoppingContext) {
            final List<String> viewed = new ArrayList<String>();

            // Current cart first
            if (shoppingCartCtx.getLatestViewedCategories() != null) {
                viewed.addAll(shoppingCartCtx.getLatestViewedCategories());
            }
            // Then old
            viewed.addAll(oldCartCtx.getLatestViewedCategories());
            ((MutableShoppingContext) shoppingCartCtx).setLatestViewedCategories(viewed);

        }

    }

    private void mergeCouponCodes(final ShoppingCart shoppingCart, final ShoppingCart oldCart, final Map<String, Object> cmdParams) {

        for (final String coupon : oldCart.getCoupons()) {

            cmdParams.clear();

            cmdParams.put(ShoppingCartCommand.CMD_ADDCOUPON, coupon);

            shoppingCartCommandFactory.execute(shoppingCart, cmdParams);


        }
    }

    private void mergeNonGiftSKU(final ShoppingCart shoppingCart, final ShoppingCart oldCart, final Map<String, Object> cmdParams) {

        for (final CartItem cartItem : oldCart.getCartItemList()) {
            if (!cartItem.isGift()) {

                cmdParams.clear();

                cmdParams.put(ShoppingCartCommand.CMD_ADDTOCART, cartItem.getProductSkuCode());
                cmdParams.put(ShoppingCartCommand.CMD_ADDTOCART_P_QTY, cartItem.getQty().toPlainString());

                shoppingCartCommandFactory.execute(shoppingCart, cmdParams);

            }
        }

    }


    /** {@inheritDoc} */
    @Override
    public ShoppingCart restoreState(final byte[] bytes) {

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = null;
        try {

            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            final ShoppingCart shoppingCart = (ShoppingCart) objectInputStream.readObject();
            return shoppingCart;

        } catch (Exception exception) {
            final String errMsg = "Unable to convert bytes assembled from tuple into object";
            ShopCodeContext.getLog(this).error(errMsg, exception);
            return null;
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                byteArrayInputStream.close();
            } catch (IOException ioe) { // leave this one silent as we have the object.
                ShopCodeContext.getLog(this).error("Unable to close object stream", ioe);
            }

        }
    }

    /** {@inheritDoc} */
    @Override
    public byte[] saveState(final ShoppingCart shoppingCart) {

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ObjectOutputStream objectOutputStream = null;
        try {

            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(shoppingCart);
            objectOutputStream.flush();
            objectOutputStream.close();

            return byteArrayOutputStream.toByteArray();

        } catch (Throwable ioe) {
            ShopCodeContext.getLog(this).error(
                    MessageFormat.format("Unable to serialize object {0}", shoppingCart),
                    ioe
            );
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                byteArrayOutputStream.close();
            } catch (IOException e) {
                ShopCodeContext.getLog(this).error("Can not close stream", e);
            }
        }
        return null;

    }

}
