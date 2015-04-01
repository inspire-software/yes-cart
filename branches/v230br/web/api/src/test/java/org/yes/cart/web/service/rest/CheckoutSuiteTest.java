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

package org.yes.cart.web.service.rest;

import org.junit.Test;
import org.junit.internal.matchers.StringContains;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.yes.cart.domain.ro.AddressOptionRO;
import org.yes.cart.domain.ro.ShippingOptionRO;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: denispavlov
 * Date: 01/04/2015
 * Time: 12:25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext.xml")
@WebAppConfiguration
public class CheckoutSuiteTest extends AbstractSuiteTest {

    private final Locale locale = Locale.ENGLISH;
    private final Pattern UUID_JSON = Pattern.compile("\"uuid\":\"([0-9a-zA-Z\\-]*)\"");
    private final Pattern ADDRESS_ID_JSON = Pattern.compile("\"addressId\":([0-9]*),");
    private final Pattern UUID_XML = Pattern.compile("uuid>([0-9a-zA-Z\\-]*)</uuid");
    private final Pattern ADDRESS_ID_XML = Pattern.compile("address-id=\"([0-9]*)\"");


    @Test
    public void testCheckoutJson() throws Exception {

        reindex();

        final byte[] regBody = toJsonBytesRegistrationDetails("bob.doe@yc-checkout-json.com");


        final MvcResult regResult =
                mockMvc.perform(put("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .locale(locale)
                        .content(regBody))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().string(StringContains.containsString("uuid")))
                        .andReturn();

        final Matcher matcher = UUID_JSON.matcher(regResult.getResponse().getContentAsString());
        matcher.find();
        final String uuid = matcher.group(1);

        mockMvc.perform(get("/auth/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"authenticated\":true")));

        final byte[] shippingAddress = toJsonBytesAddressDetails("UA-UA", "UA");

        final MvcResult shipAddress = mockMvc.perform(put("/customer/addressbook/S")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(shippingAddress))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("UA-UA")))
                .andReturn();

        final Matcher matcherS = ADDRESS_ID_JSON.matcher(shipAddress.getResponse().getContentAsString());
        matcherS.find();
        final String shippingAddressId = matcherS.group(1);

        mockMvc.perform(get("/cart/options/address/S")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("UA")));

        final byte[] billingAddress = toJsonBytesAddressDetails("GB-GB", "GB");

        final MvcResult billAddress = mockMvc.perform(put("/customer/addressbook/B")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(billingAddress))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("GB")))
                .andReturn();

        final Matcher matcherB = ADDRESS_ID_JSON.matcher(billAddress.getResponse().getContentAsString());
        matcherB.find();
        final String billingAddressId = matcherB.group(1);

        mockMvc.perform(get("/cart/options/address/B")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("GB")));

        final byte[] addToCart = toJsonBytes(new HashMap<String, String>() {{
            put(ShoppingCartCommand.CMD_ADDTOCART, "BENDER-ua");
        }});

        mockMvc.perform(put("/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(addToCart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(content().string(StringContains.containsString("currencyCode\":\"EUR\"")))
                .andExpect(content().string(StringContains.containsString("deliveryAddressId\":null")))
                .andExpect(content().string(StringContains.containsString("billingAddressId\":null")))
                .andExpect(content().string(StringContains.containsString("separateBillingAddress\":false")));


        final AddressOptionRO shipAddressOptionRO = new AddressOptionRO();
        shipAddressOptionRO.setAddressId(shippingAddressId);
        final byte[] setShippingCart = toJsonBytes(shipAddressOptionRO);

        mockMvc.perform(put("/cart/options/address/S")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(setShippingCart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("deliveryAddressId\":" + shippingAddressId)))
                .andExpect(content().string(StringContains.containsString("billingAddressId\":null")))
                .andExpect(content().string(StringContains.containsString("separateBillingAddress\":true")));

        final AddressOptionRO billAddressOptionRO = new AddressOptionRO();
        billAddressOptionRO.setAddressId(billingAddressId);
        final byte[] setBillingCart = toJsonBytes(billAddressOptionRO);

        mockMvc.perform(put("/cart/options/address/B")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(setBillingCart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("deliveryAddressId\":" + shippingAddressId)))
                .andExpect(content().string(StringContains.containsString("billingAddressId\":" + billingAddressId)))
                .andExpect(content().string(StringContains.containsString("separateBillingAddress\":true")));


        final AddressOptionRO billAddressOptionROSame = new AddressOptionRO();
        billAddressOptionROSame.setAddressId(billingAddressId);
        billAddressOptionROSame.setShippingSameAsBilling(true);
        final byte[] setBillingSameCart = toJsonBytes(billAddressOptionROSame);

        mockMvc.perform(put("/cart/options/address/B")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(setBillingSameCart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("deliveryAddressId\":" + billingAddressId)))
                .andExpect(content().string(StringContains.containsString("billingAddressId\":" + billingAddressId)))
                .andExpect(content().string(StringContains.containsString("separateBillingAddress\":false")))
                .andExpect(content().string(StringContains.containsString("carrierSlaId\":null")));


        mockMvc.perform(get("/cart/options/shipping")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"carrierId\":1")))
                .andExpect(content().string(StringContains.containsString("\"carrierslaId\":4")));

        final ShippingOptionRO shippingOptionRO = new ShippingOptionRO();
        shippingOptionRO.setCarrierslaId("4");

        final byte[] setCarrierCart = toJsonBytes(shippingOptionRO);

        mockMvc.perform(put("/cart/options/shipping")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(setCarrierCart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("deliveryAddressId\":" + billingAddressId)))
                .andExpect(content().string(StringContains.containsString("billingAddressId\":" + billingAddressId)))
                .andExpect(content().string(StringContains.containsString("separateBillingAddress\":false")))
                .andExpect(content().string(StringContains.containsString("carrierSlaId\":4")));


        final byte[] addMessageToCart = toJsonBytes(new HashMap<String, String>() {{
            put(ShoppingCartCommand.CMD_SETORDERMSG, "My Message");
        }});

        mockMvc.perform(put("/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale)
                .header("yc", uuid)
                .content(addMessageToCart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"orderMessage\":\"My Message\"")));


    }

    @Test
    public void testCheckoutXML() throws Exception {

        reindex();

        final byte[] regBody = toJsonBytesRegistrationDetails("bob.doe@-checkout-xml.com");


        final MvcResult regResult =
                mockMvc.perform(put("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_XML)
                        .locale(locale)
                        .content(regBody))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().string(StringContains.containsString("uuid")))
                        .andReturn();

        final Matcher matcher = UUID_XML.matcher(regResult.getResponse().getContentAsString());
        matcher.find();
        final String uuid = matcher.group(1);

        mockMvc.perform(get("/auth/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale)
                .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<authenticated>true</authenticated>")));

        final byte[] shippingAddress = toJsonBytesAddressDetails("UA-UA", "UA");

        final MvcResult shipAddress = mockMvc.perform(put("/customer/addressbook/S")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(shippingAddress))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("UA-UA")))
                .andReturn();

        final Matcher matcherS = ADDRESS_ID_XML.matcher(shipAddress.getResponse().getContentAsString());
        matcherS.find();
        final String shippingAddressId = matcherS.group(1);


        mockMvc.perform(get("/cart/options/address/S")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("UA")));


        final byte[] billingAddress = toJsonBytesAddressDetails("GB-GB", "GB");

        final MvcResult billAddress = mockMvc.perform(put("/customer/addressbook/B")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(billingAddress))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("GB")))
                .andReturn();

        final Matcher matcherB = ADDRESS_ID_XML.matcher(billAddress.getResponse().getContentAsString());
        matcherB.find();
        final String billingAddressId = matcherB.group(1);

        mockMvc.perform(get("/cart/options/address/B")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("GB")));



        final byte[] addToCart = toJsonBytes(new HashMap<String, String>() {{
            put(ShoppingCartCommand.CMD_ADDTOCART, "BENDER-ua");
        }});

        mockMvc.perform(put("/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(addToCart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(content().string(StringContains.containsString("currency=\"EUR\"")))
                .andExpect(content().string(StringContains.containsString("separate-billing-address=\"false\"")));


        final AddressOptionRO shipAddressOptionRO = new AddressOptionRO();
        shipAddressOptionRO.setAddressId(shippingAddressId);
        final byte[] setShippingCart = toJsonBytes(shipAddressOptionRO);

        mockMvc.perform(put("/cart/options/address/S")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(setShippingCart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("delivery-address-id=\"" + shippingAddressId)))
                .andExpect(content().string(StringContains.containsString("separate-billing-address=\"true")));

        final AddressOptionRO billAddressOptionRO = new AddressOptionRO();
        billAddressOptionRO.setAddressId(billingAddressId);
        final byte[] setBillingCart = toJsonBytes(billAddressOptionRO);

        mockMvc.perform(put("/cart/options/address/B")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(setBillingCart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("delivery-address-id=\"" + shippingAddressId)))
                .andExpect(content().string(StringContains.containsString("billing-address-id=\"" + billingAddressId)))
                .andExpect(content().string(StringContains.containsString("separate-billing-address=\"true")));


        final AddressOptionRO billAddressOptionROSame = new AddressOptionRO();
        billAddressOptionROSame.setAddressId(billingAddressId);
        billAddressOptionROSame.setShippingSameAsBilling(true);
        final byte[] setBillingSameCart = toJsonBytes(billAddressOptionROSame);

        mockMvc.perform(put("/cart/options/address/B")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(setBillingSameCart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("delivery-address-id=\"" + billingAddressId)))
                .andExpect(content().string(StringContains.containsString("billing-address-id=\"" + billingAddressId)))
                .andExpect(content().string(StringContains.containsString("separate-billing-address=\"false")));


        mockMvc.perform(get("/cart/options/shipping")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("carrier-id=\"1")))
                .andExpect(content().string(StringContains.containsString("carriersla-id=\"4")));

        final ShippingOptionRO shippingOptionRO = new ShippingOptionRO();
        shippingOptionRO.setCarrierslaId("4");

        final byte[] setCarrierCart = toJsonBytes(shippingOptionRO);

        mockMvc.perform(put("/cart/options/shipping")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(setCarrierCart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("delivery-address-id=\"" + billingAddressId)))
                .andExpect(content().string(StringContains.containsString("billing-address-id=\"" + billingAddressId)))
                .andExpect(content().string(StringContains.containsString("separate-billing-address=\"false")))
                .andExpect(content().string(StringContains.containsString("carrier-sla-id=\"4")));


        final byte[] addMessageToCart = toJsonBytes(new HashMap<String, String>() {{
            put(ShoppingCartCommand.CMD_SETORDERMSG, "My Message");
        }});

        mockMvc.perform(put("/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(addMessageToCart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("order-message>My Message</")));


    }

}
