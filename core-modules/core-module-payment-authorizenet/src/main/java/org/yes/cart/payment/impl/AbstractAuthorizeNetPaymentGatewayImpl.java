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

package org.yes.cart.payment.impl;

import net.authorize.data.ShippingCharges;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentLine;

import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractAuthorizeNetPaymentGatewayImpl extends AbstractAuthorizeNetBasePaymentGatewayImpl implements PaymentGateway {


    /**
     * Environment name. Allowed values SANDBOX, SANDBOX_TESTMODE, PRODUCTION, PRODUCTION_TESTMODE.
     * CUSTOM  not supported at this moment. CP
     */
    protected static final String AN_MERCHANT_ENVIRONMENT = "MERCHANT_ENVIRONMENT";

    /**
     * API login ID
     */
    protected static final String AN_API_LOGIN_ID = "API_LOGIN_ID";
    /**
     * API transaction key
     */
    protected static final String AN_TRANSACTION_KEY = "TRANSACTION_KEY";


    //Length limits
    private static final int ITEMSKU = 30;
    private static final int ITEMNAME = 30;




    /**
     * Get the Environment for merchant.
     *
     * @return {@link net.authorize.Environment}
     */
    protected net.authorize.Environment getEnvironment() {
        final String envName = getParameterValue(AN_MERCHANT_ENVIRONMENT);
        if ("SANDBOX".equalsIgnoreCase(envName)) {
            return net.authorize.Environment.SANDBOX;
        } else if ("PRODUCTION".equalsIgnoreCase(envName)) {
            return net.authorize.Environment.PRODUCTION;
        }
        return net.authorize.Environment.CUSTOM;
    }

    /**
     * Get the {@link net.authorize.Merchant}.
     *
     * @return {@link net.authorize.Merchant}
     */
    protected net.authorize.Merchant createMerchant() {
        return net.authorize.Merchant.createMerchant(
                getEnvironment(),
                getParameterValue(AN_API_LOGIN_ID),
                getParameterValue(AN_TRANSACTION_KEY)
        );
    }


    /**
     * Create authorize net customer from given {@link Payment}. {@link Payment} has all details
     * to recreate order delivery , customet, etc.
     *
     * @param payment given payment
     * @return anet customer.
     */
    protected net.authorize.data.Customer createAnetCustomer(final Payment payment) {

        final net.authorize.data.Customer anetCustomer = net.authorize.data.Customer.createCustomer();
        anetCustomer.setFirstName(payment.getBillingAddress().getFirstname());
        anetCustomer.setLastName(payment.getBillingAddress().getLastname());

        anetCustomer.setAddress(
            getStreetAddress(payment.getBillingAddress().getAddrline1(), payment.getBillingAddress().getAddrline2())
        );

        anetCustomer.setCity(payment.getBillingAddress().getCity());
        anetCustomer.setCountry(payment.getBillingAddress().getCountryCode());
        anetCustomer.setCustomerId(payment.getBillingEmail());
        anetCustomer.setEmail(payment.getBillingEmail());
        anetCustomer.setPhone(payment.getBillingAddress().getPhone1());
        anetCustomer.setZipPostalCode(payment.getBillingAddress().getPostcode());
        anetCustomer.setState(payment.getBillingAddress().getStateCode());
        anetCustomer.setCustomerIP(payment.getShopperIpAddress());
        return anetCustomer;
    }

    /**
     * Create shipping address from given {@link Payment}.
     *
     * @param payment given payment
     * @return shipping address
     */
    protected net.authorize.data.ShippingAddress createShippingAddress(final Payment payment) {
        net.authorize.data.ShippingAddress shippingAddress = net.authorize.data.ShippingAddress.createShippingAddress();
        shippingAddress.setFirstName(payment.getShippingAddress().getFirstname());
        shippingAddress.setLastName(payment.getShippingAddress().getLastname());
        shippingAddress.setAddress(
                getStreetAddress(payment.getShippingAddress().getAddrline1(), payment.getShippingAddress().getAddrline2())
        );
        shippingAddress.setCity(payment.getShippingAddress().getCity());
        shippingAddress.setCountry(payment.getBillingAddress().getCountryCode());
        shippingAddress.setZipPostalCode(payment.getBillingAddress().getPostcode());
        shippingAddress.setState(payment.getBillingAddress().getStateCode());
        return shippingAddress;
    }


    /**
     * Create authorize net order from given {@link Payment}.
     *
     * @param payment given payment
     * @return anet order.
     */
    protected net.authorize.data.Order createAnetOrder(final Payment payment) {
        final net.authorize.data.Order order = net.authorize.data.Order.createOrder();
        order.setInvoiceNumber(payment.getOrderShipment());
        order.setPurchaseOrderNumber(payment.getOrderShipment());
        order.setTotalAmount(payment.getPaymentAmount());

        final List<net.authorize.data.OrderItem> itemsInDelivery = new ArrayList<net.authorize.data.OrderItem>(payment.getOrderItems().size());
        for (PaymentLine paymentLine : payment.getOrderItems()) {
            if (paymentLine.isShipment()) {
                final ShippingCharges shipping = ShippingCharges.createShippingCharges();
                shipping.setFreightAmount(paymentLine.getUnitPrice());
                shipping.setFreightItemName(
                        paymentLine.getSkuName().length() > ITEMNAME ? paymentLine.getSkuName().substring(0, ITEMNAME - 1) + "~" : paymentLine.getSkuName()
                );
                shipping.setFreightDescription(
                        paymentLine.getSkuName().length() > ITEMNAME ? paymentLine.getSkuName().substring(0, ITEMNAME - 1) + "~" : paymentLine.getSkuName()
                );
                order.setShippingCharges(shipping);
            } else {
                net.authorize.data.OrderItem item = net.authorize.data.OrderItem.createOrderItem();
                item.setItemId(
                        paymentLine.getSkuCode().length() > ITEMSKU ? paymentLine.getSkuCode().substring(0, ITEMSKU - 1) + "~" : paymentLine.getSkuCode()
                );
                item.setItemName(
                        paymentLine.getSkuName().length() > ITEMNAME ? paymentLine.getSkuName().substring(0, ITEMNAME - 1) + "~" : paymentLine.getSkuName()
                );
                item.setItemPrice(paymentLine.getUnitPrice());
                item.setItemQuantity(paymentLine.getQuantity());
                //item.setItemTaxable(); // CPOINT
                itemsInDelivery.add(
                        item
                );
            }
        }
        order.setOrderItems(itemsInDelivery);
        // TODO: tax exempt and duty
        return order;
    }


    /**
     * Create authorize net credit card object from given {@link Payment}.
     *
     * @param payment given payment
     * @return anet credit card.
     */
    protected net.authorize.data.creditcard.CreditCard createAnetCreditCard(final Payment payment) {
        net.authorize.data.creditcard.CreditCard card =
                net.authorize.data.creditcard.CreditCard.createCreditCard();
        if (payment.getCardNumber() != null) {
            card.setCreditCardNumber(payment.getCardNumber());
        }
        if (payment.getCardExpireYear() != null) {
            card.setExpirationYear(payment.getCardExpireYear());
        }
        if (payment.getCardExpireMonth() != null) {
            card.setExpirationMonth(payment.getCardExpireMonth());
        }
        if (payment.getCardCvv2Code() != null) {
            card.setCardCode(payment.getCardCvv2Code());
        }
        card.setCardType(net.authorize.data.creditcard.CardType.findByValue(payment.getCardType()));
        return card;
    }


    /**
     * Sanitize strings for output. Copy from anet_java_sdk_sample_app\common\helper.jsp
     *
     * @param string string to sanitize
     * @return sanitize striung
     */
    public String sanitizeString(String string) {

        StringBuilder retval = new StringBuilder();
        StringCharacterIterator iterator = new StringCharacterIterator(string);
        char character = iterator.current();

        while (character != java.text.CharacterIterator.DONE) {
            if (character == '<') {
                retval.append("&lt;");
            } else if (character == '>') {
                retval.append("&gt;");
            } else if (character == '&') {
                retval.append("&amp;");
            } else if (character == '\"') {
                retval.append("&quot;");
            } else if (character == '\t') {
                addCharEntity(9, retval);
            } else if (character == '!') {
                addCharEntity(33, retval);
            } else if (character == '#') {
                addCharEntity(35, retval);
            } else if (character == '$') {
                addCharEntity(36, retval);
            } else if (character == '%') {
                addCharEntity(37, retval);
            } else if (character == '\'') {
                addCharEntity(39, retval);
            } else if (character == '(') {
                addCharEntity(40, retval);
            } else if (character == ')') {
                addCharEntity(41, retval);
            } else if (character == '*') {
                addCharEntity(42, retval);
            } else if (character == '+') {
                addCharEntity(43, retval);
            } else if (character == ',') {
                addCharEntity(44, retval);
            } else if (character == '-') {
                addCharEntity(45, retval);
            } else if (character == '.') {
                addCharEntity(46, retval);
            } else if (character == '/') {
                addCharEntity(47, retval);
            } else if (character == ':') {
                addCharEntity(58, retval);
            } else if (character == ';') {
                addCharEntity(59, retval);
            } else if (character == '=') {
                addCharEntity(61, retval);
            } else if (character == '?') {
                addCharEntity(63, retval);
            } else if (character == '@') {
                addCharEntity(64, retval);
            } else if (character == '[') {
                addCharEntity(91, retval);
            } else if (character == '\\') {
                addCharEntity(92, retval);
            } else if (character == ']') {
                addCharEntity(93, retval);
            } else if (character == '^') {
                addCharEntity(94, retval);
            } else if (character == '_') {
                addCharEntity(95, retval);
            } else if (character == '`') {
                addCharEntity(96, retval);
            } else if (character == '{') {
                addCharEntity(123, retval);
            } else if (character == '|') {
                addCharEntity(124, retval);
            } else if (character == '}') {
                addCharEntity(125, retval);
            } else if (character == '~') {
                addCharEntity(126, retval);
            } else {
                retval.append(character);
            }
            character = iterator.next();
        }
        return retval.toString();
    }


    /**
     * Convert integer to char entity
     *
     * @param i  integer
     * @param sb string builder
     */
    public void addCharEntity(int i, StringBuilder sb) {

        String padding = "";
        if (i <= 9) {
            padding = "00";
        } else if (i <= 99) {
            padding = "0";
        }
        String number = padding + i;
        sb.append("&#");
        sb.append(number);
        sb.append(';');
    }


}
