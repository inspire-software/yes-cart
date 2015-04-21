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

import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.shoppingcart.tokendriven.ShoppingCartStateSerializer;

import java.io.*;
import java.text.MessageFormat;

/**
 * User: denispavlov
 * Date: 21/04/2015
 * Time: 10:03
 */
public class ShoppingCartStateSerializerSdkImpl implements ShoppingCartStateSerializer {


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
