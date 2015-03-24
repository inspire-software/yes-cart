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

package org.yes.cart.web.support.shoppingcart.impl;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.shoppingcart.CartDetuplizationException;
import org.yes.cart.web.support.shoppingcart.CartTuplizationException;
import org.yes.cart.web.support.shoppingcart.CartTuplizer;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.text.MessageFormat;

/**
 * Default implementation of cookie tuplizer.
 * <p/>
 * The cookies are crypted in base64 form. According to RFC 2109
 * specification cookie storage limit at least 300 cookies at least 4096
 * bytes per cookie, so it allow to store 1228800 bytes aprox 1 Mb.
 * Base64 representation will be splited to chunks.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 2:17:57 PM
 */
public abstract class AbstractCryptedTuplizerImpl implements CartTuplizer {

    private static final long serialVersionUID = 20100116L;

    private final Cipher desCipher;
    private final Cipher desUnCipher;

    private SecretKey secretKey;

    /**
     * Default Constructor.
     *
     * @param keyRingPassword      key ring password to use.
     * @param secretKeyFactoryName Secret Key Factory Name.
     * @param cipherName           Cipher name.
     */
    public AbstractCryptedTuplizerImpl(final String keyRingPassword,
                                       final String secretKeyFactoryName,
                                       final String cipherName) {

        try {
            final DESKeySpec desKeySpec = new DESKeySpec(keyRingPassword.getBytes());

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(secretKeyFactoryName);
            secretKey = keyFactory.generateSecret(desKeySpec);

            // Create Cipher
            desCipher = Cipher.getInstance(cipherName);
            desCipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // create uncipher
            desUnCipher = Cipher.getInstance(cipherName);
            desUnCipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (Exception ike) {
            ShopCodeContext.getLog(this).error(ike.getMessage(), ike);
            throw new RuntimeException("Unable to load Cipher for CookieTuplizer", ike);
        }

    }

    /**
     * Converts cart object into a String tuple.
     *
     * @param serializable cart
     *
     * @return string
     *
     * @throws org.yes.cart.web.support.shoppingcart.CartTuplizationException when cannot convert to string tuple
     */
    protected String toToken(final Serializable serializable) throws CartTuplizationException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        synchronized (desCipher) {
            BASE64EncoderStream base64EncoderStream = new BASE64EncoderStream(byteArrayOutputStream, Integer.MAX_VALUE); //will be split manually
            CipherOutputStream cipherOutputStream = new CipherOutputStream(base64EncoderStream, desCipher);
            ObjectOutputStream objectOutputStream = null;
            try {
                objectOutputStream = new ObjectOutputStream(cipherOutputStream);
                objectOutputStream.writeObject(serializable);
                objectOutputStream.flush();
                objectOutputStream.close();
            } catch (Throwable ioe) {
                ShopCodeContext.getLog(this).error(
                        MessageFormat.format("Unable to serialize object {0}", serializable),
                        ioe
                );
                throw new CartTuplizationException(ioe);
            } finally {
                try {
                    if (objectOutputStream != null) {
                        objectOutputStream.close();
                    }
                    cipherOutputStream.close();
                    base64EncoderStream.close();
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    ShopCodeContext.getLog(this).error("Can not close stream", e);
                }
            }
        }

        return byteArrayOutputStream.toString();

    }

    /**
     * Convert string tuple back to the original object.
     *
     * @param tuple string tuple
     * @param <T> cart type
     *
     * @return cart object of null
     *
     * @throws org.yes.cart.web.support.shoppingcart.CartDetuplizationException when cannot deserilize the object
     */
    protected <T extends Serializable> T toObject(String tuple) throws CartDetuplizationException {

        if (tuple == null || tuple.length() == 0) {
            return null;
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(tuple.getBytes());
        final BASE64DecoderStream base64DecoderStream = new BASE64DecoderStream(byteArrayInputStream);
        final CipherInputStream cipherInputStream = new CipherInputStream(base64DecoderStream, desUnCipher);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(cipherInputStream);
            return (T) objectInputStream.readObject();

        } catch (Exception exception) {
            try {
                desUnCipher.init(Cipher.DECRYPT_MODE, secretKey); //reinit
            } catch (InvalidKeyException e) {
                ShopCodeContext.getLog(this).error("Cant reinit desUnCipher", exception);
            }
            final String errMsg = "Unable to convert bytes assembled from tuple into object";
            ShopCodeContext.getLog(this).error(errMsg, exception);
            throw new CartDetuplizationException(errMsg, exception);
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                cipherInputStream.close();
                base64DecoderStream.close();
                byteArrayInputStream.close();
            } catch (IOException ioe) { // leave this one silent as we have the object.
                ShopCodeContext.getLog(this).error("Unable to close object stream", ioe);
            }

        }
    }

}
