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

package org.yes.cart.shoppingcart.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * TODO: YC-411 Need to revise the most efficient way for saving and restoring cart state
 *
 * User: denispavlov
 * Date: 23/08/2014
 * Time: 10:41
 */
@Ignore("This is not a test but just a draft snippet to see how serialization performs")
public class ShoppingCartImplSerializationTest {

    private int counter = 0;

    /**
     * This test produces the following results on Mac OSX 2.4GHz Core 2 Duo:
     *
     * == Pure Java OOTB Serializable implementation: ======================================
     *
     * Serializing 10000 carts took ~4124ms (size of cart is ~7.5Kb)
     * Deserializing 10000 carts took ~4669ms
     * Total to and back for 10000 carts took ~8793ms (~0.88ms per cart)
     *
     * Conclusion: to and back is around ~1ms
     *
     * == Exterializable custom implementation: ============================================
     *
     *
     *
     * @throws Exception
     */
    @Test
    public void testSerializationPerformance() throws Exception {


        final int sampleSize = 10000;

        final List<ShoppingCart> carts = new ArrayList<ShoppingCart>(sampleSize);
        final List<byte[]> cartsB = new ArrayList<byte[]>(sampleSize);
        for (int i = 0; i < sampleSize; i++) {
            carts.add(create(25, 3, 2));
        }

        byte[] bytes = null;
        final long startSerializing = System.currentTimeMillis();
        for (final ShoppingCart cart : carts) {
            bytes = saveState(cart);
            cartsB.add(bytes);
        }
        final long finishSerializing = System.currentTimeMillis();

        System.out.println("Serializing " + sampleSize + " carts took ~" + (finishSerializing - startSerializing) + "ms " +
                "(size of cart is ~" + new BigDecimal(((double) bytes.length) / 1024).setScale(1, RoundingMode.HALF_UP).toPlainString() + "Kb)");

        ShoppingCart cart = null;

        final long startDeserializing = System.currentTimeMillis();
        for (byte[] b : cartsB) {
            cart = restoreState(b);
        }
        final long finishDeserializing = System.currentTimeMillis();

        System.out.println("Deserializing " + sampleSize + " carts took ~" + (finishDeserializing - startDeserializing) + "ms");
        assertNotNull(cart.getGuid());

        long totalMs = (finishSerializing - startSerializing + finishDeserializing - startDeserializing);
        System.out.println("Total to and back for " + sampleSize + " carts took ~" + totalMs + "ms (~" + new BigDecimal(totalMs).divide(new BigDecimal(sampleSize), 2, RoundingMode.HALF_UP).toPlainString() + "ms per cart)");

    }


    private ShoppingCart restoreState(final byte[] bytes) {

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = null;
        try {

            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            final ShoppingCart shoppingCart = (ShoppingCart) objectInputStream.readObject();
            return shoppingCart;

        } catch (Exception exception) {
            exception.printStackTrace();
            fail(exception.getMessage());
            return null;
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                byteArrayInputStream.close();
            } catch (IOException ioe) { // leave this one silent as we have the object.
            }

        }
    }

    private byte[] saveState(final ShoppingCart shoppingCart) {

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ObjectOutputStream objectOutputStream = null;
        try {

            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(shoppingCart);
            objectOutputStream.flush();
            objectOutputStream.close();

            return byteArrayOutputStream.toByteArray();

        } catch (Throwable ioe) {
            ioe.printStackTrace();
            fail(ioe.getMessage());
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                byteArrayOutputStream.close();
            } catch (IOException e) {

            }
        }
        return null;

    }


    private ShoppingCartImpl create(int skuCount, int giftCount, int couponCount) {

        final ShoppingCartImpl cart = new ShoppingCartImpl();

        cart.setCurrencyCode("EUR");
        cart.setCurrentLocale("EN");

        BigDecimal list = BigDecimal.ZERO;
        BigDecimal sale = BigDecimal.ZERO;
        BigDecimal promo = BigDecimal.ZERO;

        for (int i = 0; i < skuCount; i++) {

            final BigDecimal itemQty = new BigDecimal(i);
            final BigDecimal itemList = new BigDecimal(10 * i).setScale(2);
            final BigDecimal itemSale = itemList.multiply(new BigDecimal("0.95")).setScale(2, RoundingMode.HALF_EVEN);
            final boolean isPromo  = i % 2 == 0;
            final BigDecimal itemPromo = itemList.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_EVEN);

            list = list.add(itemQty.multiply(itemList));
            sale = sale.add(itemQty.multiply(itemSale));
            promo = promo.add(itemQty.multiply(itemPromo));

            cart.addProductSkuToCart("ABC-" + i, itemQty);
            cart.setProductSkuPrice("ABC-" + i, itemSale, itemList);
            if (isPromo) {
                cart.setProductSkuPromotion("ABC-" + i, itemPromo, "IPROMO-" + i);
            }
        }
        for (int i = 0; i < giftCount; i++) {

            final BigDecimal itemQty = new BigDecimal(i);
            final BigDecimal itemList = new BigDecimal(10 * i).setScale(2);
            final BigDecimal itemSale = itemList.multiply(new BigDecimal("0.95")).setScale(2, RoundingMode.HALF_EVEN);

            list = list.add(itemQty.multiply(itemList));
            sale = sale.add(itemQty.multiply(itemSale));

            cart.addGiftToCart("ABC-" + i, itemQty, "CPROMO-" + i);
            cart.setGiftPrice("ABC-" + i, itemSale, itemList);
        }
        for (int i = 0; i < couponCount; i++) {
            cart.addCoupon("COUPON-" + i);
        }


        final Total total = new TotalImpl(
                list,
                sale,
                sale,
                promo,
                true,
                "OPROMO-1",
                promo.multiply(new BigDecimal("0.95")).setScale(2, RoundingMode.HALF_EVEN),
                promo.multiply(new BigDecimal("0.228")).setScale(2, RoundingMode.HALF_EVEN),
                promo.multiply(new BigDecimal("0.95")).setScale(2, RoundingMode.HALF_EVEN),
                new BigDecimal("5.00"),
                new BigDecimal("4.00"),
                true,
                "SPROMO-1",
                new BigDecimal("0.67"),
                new BigDecimal("5.00"),
                promo.multiply(new BigDecimal("0.95")).setScale(2, RoundingMode.HALF_EVEN).add(new BigDecimal("4.00")),
                promo.multiply(new BigDecimal("0.228")).setScale(2, RoundingMode.HALF_EVEN).add(new BigDecimal("0.67")),
                promo.multiply(new BigDecimal("0.95")).setScale(2, RoundingMode.HALF_EVEN).add(new BigDecimal("5.00")),
                promo.multiply(new BigDecimal("0.95")).setScale(2, RoundingMode.HALF_EVEN).add(new BigDecimal("4.00"))
        );

        cart.initialise(new AmountCalculationStrategy() {
            @Override
            public Total calculate(final ShoppingCart cart) {
                return total;
            }

            @Override
            public Total calculate(final CustomerOrder order, final CustomerOrderDelivery orderDelivery) {
                fail("Not used in this test");
                return null;
            }

            @Override
            public Total calculate(final CustomerOrder order) {
                fail("Not used in this test");
                return null;
            }
        });

        cart.getShoppingContext().setCustomerEmail("bob" + counter + "@doe.com");
        cart.getShoppingContext().setCustomerName("Bob" + counter + " Doe");
        cart.getShoppingContext().setLatestViewedSkus(Arrays.asList("1" + counter, "2" + counter, "3" + counter));
        cart.getShoppingContext().setLatestViewedCategories(Arrays.asList("1" + counter, "2" + counter, "3" + counter));
        cart.getShoppingContext().setResolvedIp("127.0.0." + counter);
        cart.getShoppingContext().setShopCode("SHOP10");
        cart.getShoppingContext().setShopId(10L);

        cart.getOrderInfo().setSeparateBillingAddress(true);
        cart.getOrderInfo().setBillingAddressId(10L + counter);
        cart.getOrderInfo().setDeliveryAddressId(12L + counter);
        cart.getOrderInfo().setCarrierSlaId(20L);
        cart.getOrderInfo().setMultipleDelivery(true);
        cart.getOrderInfo().setOrderMessage("Some message" + counter + " on my order");
        cart.getOrderInfo().setPaymentGatewayLabel("courierPaymentGatewayLabel");

        counter++;

        return cart;
    }

}
