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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.shoppingcart.support.tokendriven.ShoppingCartStateSerDes;

import java.io.*;

/**
 * User: denispavlov
 * Date: 21/04/2015
 * Time: 10:03
 */
public class ShoppingCartStateSerDesSdkImpl implements ShoppingCartStateSerDes {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartStateSerDesSdkImpl.class);

    private final AmountCalculationStrategy amountCalculationStrategy;

    public ShoppingCartStateSerDesSdkImpl(final AmountCalculationStrategy amountCalculationStrategy) {
        this.amountCalculationStrategy = amountCalculationStrategy;
    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart createState() {
        final MutableShoppingCart cart = new ShoppingCartImpl();
        cart.initialise(amountCalculationStrategy);
        return cart;
    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart restoreState(final byte[] bytes) {

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = null;
        try {

            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            final ShoppingCart cart = (ShoppingCart) objectInputStream.readObject();
            if (cart instanceof MutableShoppingCart) {
                ((MutableShoppingCart) cart).initialise(amountCalculationStrategy);
            }
            return cart;

        } catch (Exception exception) {
            LOG.error("Unable to convert bytes assembled from tuple into object: " + exception.getMessage(), exception);
            return null;
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                byteArrayInputStream.close();
            } catch (IOException ioe) { // leave this one silent as we have the object.
                LOG.error("Unable to close object stream: " + ioe.getMessage(), ioe);
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
            LOG.error(
                    "Unable to serialize object: " + shoppingCart,
                    ioe
            );
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                byteArrayOutputStream.close();
            } catch (IOException e) {
                LOG.error("Can not close stream: " + e.getMessage(), e);
            }
        }
        return null;

    }

}
