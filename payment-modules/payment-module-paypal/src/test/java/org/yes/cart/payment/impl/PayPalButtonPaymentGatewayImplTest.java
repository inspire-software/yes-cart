package org.yes.cart.payment.impl;

import com.paypal.ipn.IPNMessage;
import org.junit.Test;
import org.yes.cart.payment.CallbackAware;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 14/01/2018
 * Time: 14:16
 */
public class PayPalButtonPaymentGatewayImplTest {

    private PayPalButtonPaymentGatewayImpl gateway;

    @Test
    public void convertToCallbackConfirm() throws Exception {

        gateway = new PayPalButtonPaymentGatewayImpl() {
            @Override
            protected IPNMessage createIPNMessage(final Map<String, String[]> requestParams) {
                return new IPNMessage(requestParams, Collections.emptyMap()) {
                    @Override
                    public boolean validate() {
                        return true; // legitimate call
                    }
                };
            }
        };

        final Map<String, String[]> callBackresult = getPaymentParameters();

        final CallbackAware.Callback callback = gateway.convertToCallback(callBackresult, false);

        assertNotNull(callback);
        assertEquals(CallbackAware.CallbackOperation.PAYMENT, callback.getOperation());
        assertEquals(new BigDecimal("42.75"), callback.getAmount());
        assertEquals("160309183235-123", callback.getOrderGuid());
        assertTrue(callback.isValidated());

    }

    @Test
    public void convertToCallbackRefund() throws Exception {

        gateway = new PayPalButtonPaymentGatewayImpl() {
            @Override
            protected IPNMessage createIPNMessage(final Map<String, String[]> requestParams) {
                return new IPNMessage(requestParams, Collections.emptyMap()) {
                    @Override
                    public boolean validate() {
                        return true; // legitimate call
                    }
                };
            }
        };

        final Map<String, String[]> callBackresult = getRefundParameters();

        final CallbackAware.Callback callback = gateway.convertToCallback(callBackresult, false);

        assertNotNull(callback);
        assertEquals(CallbackAware.CallbackOperation.REFUND, callback.getOperation());
        assertEquals(new BigDecimal("43.70"), callback.getAmount());
        assertEquals("171130110742-63", callback.getOrderGuid());
        assertTrue(callback.isValidated());

    }

    private Map<String, String[]> getRefundParameters() {
        return new HashMap<String, String[]>() {{

                put("mc_gross", new String[] { "-43.70" });
                put("invoice", new String[] { "171130110742-63" });
                put("protection_eligibility", new String[] { "Eligible" });
                put("item_number1", new String[] { "POSTPACPRIORITY" });
                put("item_number2", new String[] { "596970" });
                put("payer_id", new String[] { "XXXXXXXXXXXXX" });
                put("address_street", new String[] { "Xxxxxxxxxxxx 44" });
                put("payment_date", new String[] { "00:15:36 Dec 05, 2017 PST" });
                put("payment_status", new String[] { "Refunded" });
                put("charset", new String[] { "UTF-8" });
                put("address_zip", new String[] { "3956" });
                put("mc_shipping", new String[] { "0.00" });
                put("mc_handling", new String[] { "0.00" });
                put("first_name", new String[] { "Erich" });
                put("mc_fee", new String[] { "-1.49" });
                put("address_country_code", new String[] { "CH" });
                put("address_name", new String[] { "Bob Doe" });
                put("notify_version", new String[] { "3.8" });
                put("reason_code", new String[] { "refund" });
                put("custom", new String[] { "171130110742-63" });
                put("business", new String[] { "csc@shop.ch" });
                put("address_country", new String[] { "Switzerland" });
                put("mc_handling1", new String[] { "0.00" });
                put("mc_handling2", new String[] { "0.00" });
                put("address_city", new String[] { "Xxxxxxxxxx" });
                put("verify_sign", new String[] { "ZZZZZZZZZZZZZZZZZZZZ" });
                put("payer_email", new String[] { "bob.doe@shop.ch" });
                put("mc_shipping1", new String[] { "0.00" });
                put("mc_shipping2", new String[] { "0.00" });
                put("tax1", new String[] { "0.00" });
                put("tax2", new String[] { "0.00" });
                put("parent_txn_id", new String[] { "XXXXXXXXXXXXX" });
                put("txn_id", new String[] { "7RW0835DDDDDDDDDDDD" });
                put("payment_type", new String[] { "instant" });
                put("last_name", new String[] { "Doe" });
                put("address_state", new String[] { "" });
                put("item_name1", new String[] { "PostPac Priority" });
                put("receiver_email", new String[] { "csc@shop.ch" });
                put("item_name2", new String[] { "Die Drei ??? Adventskalender 2017 Alter: 8+" });
                put("payment_fee", new String[] { "" });
                put("quantity1", new String[] { "1" });
                put("quantity2", new String[] { "1" });
                put("receiver_id", new String[] { "TMTSZVFFFFFFFFFF" });
                put("mc_gross_1", new String[] { "0.00" });
                put("mc_currency", new String[] { "CHF" });
                put("mc_gross_2", new String[] { "40.46" });
                put("residence_country", new String[] { "CH" });
                put("transaction_subject", new String[] { "171130110742-63" });
                put("payment_gross", new String[] { "" });
                put("ipn_track_id", new String[] { "b6819ttttttttt" });

            }};
    }


    @Test
    public void convertToCallbackInvalid() throws Exception {

        gateway = new PayPalButtonPaymentGatewayImpl() {
            @Override
            protected IPNMessage createIPNMessage(final Map<String, String[]> requestParams) {
                return new IPNMessage(requestParams, Collections.emptyMap()) {
                    @Override
                    public boolean validate() {
                        return false; // not a legitimate call
                    }
                };
            }
        };

        Map<String, String[]> callBackresult = Collections.emptyMap();

        final CallbackAware.Callback callback = gateway.convertToCallback(callBackresult, false);

        assertNotNull(callback);
        assertEquals(CallbackAware.CallbackOperation.INVALID, callback.getOperation());
        assertNull(callback.getAmount());
        assertNull(callback.getOrderGuid());
        assertFalse(callback.isValidated());

    }

    @Test
    public void convertToCallbackForced() throws Exception {

        gateway = new PayPalButtonPaymentGatewayImpl() {
            @Override
            protected IPNMessage createIPNMessage(final Map<String, String[]> requestParams) {
                return new IPNMessage(requestParams, Collections.emptyMap()) {
                    @Override
                    public boolean validate() {
                        return false; // not a legitimate call
                    }
                };
            }
        };

        final Map<String, String[]> callBackresult = getPaymentParameters();

        final CallbackAware.Callback callback = gateway.convertToCallback(callBackresult, true);

        assertNotNull(callback);
        assertEquals(CallbackAware.CallbackOperation.PAYMENT, callback.getOperation());
        assertEquals(new BigDecimal("42.75"), callback.getAmount());
        assertEquals("160309183235-123", callback.getOrderGuid());
        assertFalse(callback.isValidated());

    }

    private Map<String, String[]> getPaymentParameters() {
        return new HashMap<String, String[]>() {{

                put("address_city", new String[] { "Xxxxx" });
                put("address_country", new String[] { "Switzerland" });
                put("address_country_code", new String[] { "CH" });
                put("address_name", new String[] { "bob doe" });
                put("address_state", new String[] { "" });
                put("address_status", new String[] { "unconfirmed" });
                put("address_street", new String[] { "Xxxxxxxxx 10 G" });
                put("address_zip", new String[] { "3600" });
                put("business", new String[] { "csc@shop.ch" });
                put("charset", new String[] { "UTF-8" });
                put("custom", new String[] { "160309183235-123" });
                put("discount", new String[] { "0.00" });
                put("first_name", new String[] { "bob" });
                put("insurance_amount", new String[] { "0.00" });
                put("invoice", new String[] { "160309183235-123" });
                put("ipn_track_id", new String[] { "63e32f3951e7c" });
                put("item_name1", new String[] { "SPARKline: Glibber-Bubbles" });
                put("item_name2", new String[] { "Experimentierkasten: Selbstbau-Mikroskop" });
                put("item_name3", new String[] { "Mitbring-Experimente: Space Bubbles" });
                put("item_name4", new String[] { "PostPac Priority" });
                put("item_number1", new String[] { "609386" });
                put("item_number2", new String[] { "609373" });
                put("item_number3", new String[] { "601209" });
                put("item_number4", new String[] { "1" });
                put("last_name", new String[] { "doe" });
                put("mc_currency", new String[] { "CHF" });
                put("mc_fee", new String[] { "2.00" });
                put("mc_gross", new String[] { "42.75" });
                put("mc_gross_1", new String[] { "5.92" });
                put("mc_gross_2", new String[] { "15.87" });
                put("mc_gross_3", new String[] { "8.98" });
                put("mc_gross_4", new String[] { "8.79" });
                put("mc_handling", new String[] { "0.00" });
                put("mc_handling1", new String[] { "0.00" });
                put("mc_handling2", new String[] { "0.00" });
                put("mc_handling3", new String[] { "0.00" });
                put("mc_handling4", new String[] { "0.00" });
                put("mc_shipping", new String[] { "0.00" });
                put("mc_shipping1", new String[] { "0.00" });
                put("mc_shipping2", new String[] { "0.00" });
                put("mc_shipping3", new String[] { "0.00" });
                put("mc_shipping4", new String[] { "0.00" });
                put("notify_version", new String[] { "3.8" });
                put("num_cart_items", new String[] { "4" });
                put("payer_email", new String[] { "bob.doe@shop.ch" });
                put("payer_id", new String[] { "XXXXXXXXXXX" });
                put("payer_status", new String[] { "unverified" });
                put("payment_date", new String[] { "09:35:13 Mar 09, 2016 PST" });
                put("payment_fee", new String[] { "" });
                put("payment_gross", new String[] { "" });
                put("payment_status", new String[] { "Completed" });
                put("payment_type", new String[] { "instant" });
                put("protection_eligibility", new String[] { "Eligible" });
                put("quantity1", new String[] { "1" });
                put("quantity2", new String[] { "1" });
                put("quantity3", new String[] { "1" });
                put("quantity4", new String[] { "1" });
                put("receiver_email", new String[] { "csc@supportworld.ch" });
                put("receiver_id", new String[] { "XXXXXXXXXXX" });
                put("residence_country", new String[] { "CH" });
                put("shipping_discount", new String[] { "0.00" });
                put("shipping_method", new String[] { "Default" });
                put("tax", new String[] { "3.19" });
                put("transaction_subject", new String[] { "" });
                put("txn_id", new String[] { "4VJ042474XXXXXXXXX" });
                put("txn_type", new String[] { "cart" });
                put("verify_sign", new String[] { "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX" });

            }};
    }


}