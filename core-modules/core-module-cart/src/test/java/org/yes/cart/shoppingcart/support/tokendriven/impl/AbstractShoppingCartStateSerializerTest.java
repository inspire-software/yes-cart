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

import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.order.impl.DeliveryBucketImpl;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.shoppingcart.impl.TotalImpl;
import org.yes.cart.shoppingcart.support.tokendriven.ShoppingCartStateSerializer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 21/04/2015
 * Time: 10:56
 */
public abstract class AbstractShoppingCartStateSerializerTest {


    /**
     * Create specifc cart so that we can assert all values before and after serialisation.
     *
     * @return deterministic cart
     */
    protected ShoppingCartImpl createFilledCart() {

        final ShoppingCartImpl cart = new ShoppingCartImpl();
        cart.initialise(new AmountCalculationStrategy() {
            @Override
            public Total calculate(final MutableShoppingCart cart) {
                return new TotalImpl(
                        new BigDecimal("99.99"),
                        new BigDecimal("98.99"),
                        new BigDecimal("97.99"),
                        new BigDecimal("96.99"),
                        true,
                        "ORD-001",
                        new BigDecimal("95.99"),
                        new BigDecimal("94.99"),
                        new BigDecimal("93.99"),
                        new BigDecimal("92.99"),
                        new BigDecimal("91.99"),
                        true,
                        "SHIP-001",
                        new BigDecimal("90.99"),
                        new BigDecimal("89.99"),
                        new BigDecimal("88.99"),
                        new BigDecimal("87.99"),
                        new BigDecimal("86.99"),
                        new BigDecimal("85.99")
                );
            }

            @Override
            public Total calculate(final CustomerOrder order, final CustomerOrderDelivery orderDelivery) {
                fail();
                return null;
            }

            @Override
            public Total calculate(final CustomerOrder order) {
                fail();
                return null;
            }
        });

        cart.addProductSkuToCart("ABC", "ABC", BigDecimal.TEN);
        cart.setProductSkuPrice("ABC", new BigDecimal("49.99"), new BigDecimal("99.99"));
        cart.setProductSkuPromotion("ABC", new BigDecimal("39.99"), "PROMO1");
        cart.addGiftToCart("GIFT", "GIFT", BigDecimal.ONE, "GIFT");
        cart.setGiftPrice("GIFT", new BigDecimal("9.99"), new BigDecimal("9.99"));
        cart.addShippingToCart(new DeliveryBucketImpl("D1", "ABC"), "10", "Ship10", BigDecimal.ONE);
        cart.setShippingPrice("10", new DeliveryBucketImpl("D1", "ABC"), new BigDecimal("4.99"), new BigDecimal("4.99"));
        cart.addCoupon("COUPON-001");

        cart.setCurrencyCode("EUR");
        cart.setCurrentLocale("en");

        cart.getShoppingContext().setCustomerEmail("bob@doe.com");
        cart.getShoppingContext().setCustomerName("Bob Doe");
        cart.getShoppingContext().setShopId(10L);
        cart.getShoppingContext().setShopCode("SHOP10");
        cart.getShoppingContext().setCustomerShops(Arrays.asList("SHOP10", "SHOP20"));
        cart.getShoppingContext().setCountryCode("GB");
        cart.getShoppingContext().setStateCode("GB-GB");

        cart.getOrderInfo().setBillingAddressId(10L);
        cart.getOrderInfo().setBillingAddressNotRequired(true);
        cart.getOrderInfo().setDeliveryAddressId(11L);
        cart.getOrderInfo().setDeliveryAddressNotRequired(true);
        cart.getOrderInfo().putCarrierSlaId("ABC", 12L);
        cart.getOrderInfo().setMultipleDelivery(true);
        cart.getOrderInfo().setOrderMessage("my message");
        cart.getOrderInfo().setPaymentGatewayLabel("pg1Label");
        cart.getOrderInfo().setSeparateBillingAddress(true);

        cart.recalculate();
        cart.markDirty();

        return cart;
    }

    /**
     * Assert method for deterministic cart
     *
     * @param cart cart to verify
     * @param guid original cart guid
     */
    protected void assertFilledCart(ShoppingCart cart, String guid) {

        final List<CartItem> items = cart.getCartItemList();
        assertEquals(2, items.size());

        final CartItem item = items.get(0);
        assertEquals("ABC", item.getProductSkuCode());
        assertEquals(BigDecimal.TEN, item.getQty());
        assertEquals(new BigDecimal("49.99"), item.getSalePrice());
        assertEquals(new BigDecimal("99.99"), item.getListPrice());
        assertEquals(new BigDecimal("39.99"), item.getPrice());
        assertFalse(item.isGift());
        assertTrue(item.isPromoApplied());
        assertEquals("PROMO1", item.getAppliedPromo());

        final CartItem gift = items.get(1);
        assertEquals("GIFT", gift.getProductSkuCode());
        assertEquals(BigDecimal.ONE, gift.getQty());
        assertEquals(new BigDecimal("9.99"), gift.getSalePrice());
        assertEquals(new BigDecimal("9.99"), gift.getListPrice());
        assertEquals(new BigDecimal("0.00"), gift.getPrice());
        assertTrue(gift.isGift());
        assertTrue(gift.isPromoApplied());
        assertEquals("GIFT", gift.getAppliedPromo());

        final List<CartItem> shipping = cart.getShippingList();
        assertEquals(1, shipping.size());


        final CartItem ship = shipping.get(0);
        assertEquals("10", ship.getProductSkuCode());
        assertEquals(BigDecimal.ONE, ship.getQty());
        assertEquals(new BigDecimal("4.99"), ship.getSalePrice());
        assertEquals(new BigDecimal("4.99"), ship.getListPrice());
        assertEquals(new BigDecimal("4.99"), ship.getPrice());
        assertFalse(ship.isGift());
        assertFalse(ship.isPromoApplied());
        assertNull(ship.getAppliedPromo());

        final List<String> coupons = cart.getCoupons();
        assertEquals(1, coupons.size());
        assertEquals("COUPON-001", coupons.get(0));

        assertEquals(guid, cart.getGuid());

        assertEquals("EUR", cart.getCurrencyCode());
        assertEquals("en", cart.getCurrentLocale());


        assertEquals("bob@doe.com", cart.getShoppingContext().getCustomerEmail());
        assertEquals("Bob Doe", cart.getShoppingContext().getCustomerName());
        assertEquals(10L, cart.getShoppingContext().getShopId());
        assertEquals("SHOP10", cart.getShoppingContext().getShopCode());
        assertEquals(Arrays.asList("SHOP10", "SHOP20"), cart.getShoppingContext().getCustomerShops());
        assertEquals("GB", cart.getShoppingContext().getCountryCode());
        assertEquals("GB-GB", cart.getShoppingContext().getStateCode());


        assertEquals(Long.valueOf(10L), cart.getOrderInfo().getBillingAddressId());
        assertTrue(cart.getOrderInfo().isBillingAddressNotRequired());
        assertEquals(Long.valueOf(11L), cart.getOrderInfo().getDeliveryAddressId());
        assertTrue(cart.getOrderInfo().isDeliveryAddressNotRequired());
        assertNotNull(cart.getOrderInfo().getCarrierSlaId());
        assertEquals(Long.valueOf(12L), cart.getOrderInfo().getCarrierSlaId().get("ABC"));
        assertTrue(cart.getOrderInfo().isMultipleDelivery());
        assertEquals("my message", cart.getOrderInfo().getOrderMessage());
        assertEquals("pg1Label", cart.getOrderInfo().getPaymentGatewayLabel());
        assertTrue(cart.getOrderInfo().isSeparateBillingAddress());


    }



    /**
     * This routine create sampleSize number of shopping carts with 25 SKU, 3 gifts serialises all of them
     * then deserialises all of them to measure time it takes for both.
     *
     * @throws Exception
     */
    public void serializationPerformanceRoutine(ShoppingCartStateSerializer serializer,
                                                int sampleSize,
                                                int skuCount,
                                                int giftCount,
                                                int couponCount) throws Exception {

        final List<ShoppingCart> carts = new ArrayList<ShoppingCart>(sampleSize);
        final List<byte[]> cartsB = new ArrayList<byte[]>(sampleSize);
        for (int i = 0; i < sampleSize; i++) {
            carts.add(createRandomCart(skuCount, giftCount, couponCount, i));
        }

        byte[] bytes = null;
        final long startSerializing = System.currentTimeMillis();
        for (final ShoppingCart cart : carts) {
            bytes = serializer.saveState(cart);
            cartsB.add(bytes);
        }
        final long finishSerializing = System.currentTimeMillis();

        System.out.println("Serializing " + sampleSize + " carts took ~" + (finishSerializing - startSerializing) + "ms " +
                "(size of cart is ~" + new BigDecimal(((double) bytes.length) / 1024).setScale(1, RoundingMode.HALF_UP).toPlainString() + "Kb)");

        ShoppingCart cart = null;

        final long startDeserializing = System.currentTimeMillis();
        for (byte[] b : cartsB) {
            cart = serializer.restoreState(b);
        }
        final long finishDeserializing = System.currentTimeMillis();

        System.out.println("Deserializing " + sampleSize + " carts took ~" + (finishDeserializing - startDeserializing) + "ms");
        assertNotNull(cart.getGuid());

        long totalMs = (finishSerializing - startSerializing + finishDeserializing - startDeserializing);
        System.out.println("Total to and back for " + sampleSize + " carts took ~" + totalMs + "ms (~" + new BigDecimal(totalMs).divide(new BigDecimal(sampleSize), 2, RoundingMode.HALF_UP).toPlainString() + "ms per cart)");

    }


    /**
     * Create random filled in cart.
     *
     * @param skuCount number of items to add
     * @param giftCount number of gifts to add
     * @param couponCount number of coupons to add
     * @param counter current counter (must be updated in test that uses this method)
     *
     * @return cart filled with random data
     */
    protected ShoppingCartImpl createRandomCart(int skuCount, int giftCount, int couponCount, int counter) {

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

            cart.addProductSkuToCart("ABC-" + i, "ABC", itemQty);
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

            cart.addGiftToCart("ABC-" + i, "GIFT", itemQty, "CPROMO-" + i);
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
            public Total calculate(final MutableShoppingCart cart) {
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
        cart.getOrderInfo().putCarrierSlaId(null, 20L);
        cart.getOrderInfo().setMultipleDelivery(true);
        cart.getOrderInfo().setOrderMessage("Some message" + counter + " on my order");
        cart.getOrderInfo().setPaymentGatewayLabel("courierPaymentGatewayLabel");

        return cart;
    }


}
