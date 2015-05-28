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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.ro.*;
import org.yes.cart.domain.ro.xml.XMLParamsRO;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentMiscParam;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.service.order.*;
import org.yes.cart.service.payment.PaymentProcessFacade;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.service.rest.impl.AddressSupportMixin;
import org.yes.cart.web.service.rest.impl.CartMixin;
import org.yes.cart.web.service.rest.impl.RoMappingMixin;
import org.yes.cart.web.support.service.AddressBookFacade;
import org.yes.cart.web.support.service.CheckoutServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;
import org.yes.cart.web.support.service.ShippingServiceFacade;
import org.yes.cart.web.support.util.CommandUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

/**
 * User: denispavlov
 * Date: 24/03/2015
 * Time: 17:30
 */
@Controller
public class CartController {

    @Autowired
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @Autowired
    private ShippingServiceFacade shippingServiceFacade;

    @Autowired
    private CustomerServiceFacade customerServiceFacade;

    @Autowired
    private CheckoutServiceFacade checkoutServiceFacade;

    @Autowired
    private AddressBookFacade addressBookFacade;

    @Autowired
    private PaymentProcessFacade paymentProcessFacade;


    @Autowired
    private CartMixin cartMixin;
    @Autowired
    @Qualifier("restRoMappingMixin")
    private RoMappingMixin mappingMixin;
    @Autowired
    private AddressSupportMixin addressSupportMixin;



    /**
     * Interface: GET /yes-api/rest/cart
     * <p>
     * <p>
     * Display current cart.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object CartRO</td><td>
     * <pre><code>
     * {
     *     "items": [{
     *         "productSkuCode": "BENDER-ua",
     *         "quantity": 1,
     *         "price": 99.99,
     *         "salePrice": 99.99,
     *         "listPrice": 99.99,
     *         "netPrice": 83.32,
     *         "grossPrice": 99.99,
     *         "taxRate": 20.00,
     *         "taxCode": "VAT",
     *         "taxExclusiveOfPrice": false,
     *         "gift": false,
     *         "promoApplied": false,
     *         "appliedPromo": null
     *     }],
     *     "gifts": [],
     *     "coupons": [],
     *     "appliedCoupons": [],
     *     "shipping": [{
     *         "productSkuCode": "4",
     *         "quantity": 1.00,
     *         "price": 10.00,
     *         "salePrice": 10.00,
     *         "listPrice": 10.00,
     *         "netPrice": 8.33,
     *         "grossPrice": 10.00,
     *         "taxRate": 20.00,
     *         "taxCode": "VAT",
     *         "taxExclusiveOfPrice": false,
     *         "gift": false,
     *         "promoApplied": false,
     *         "appliedPromo": null
     *     }],
     *     "guid": "9633d731-15e8-40ee-afdb-1cdcca4be78e",
     *     "currentLocale": "en",
     *     "currencyCode": "EUR",
     *     "modifiedTimestamp": 1427896792015,
     *     "modified": true,
     *     "logonState": 2,
     *     "processingStartTimestamp": 1427896791992,
     *     "shoppingContext": {
     *         "customerName": "Bob Doe",
     *         "shopId": 10,
     *         "shopCode": "SHOIP1",
     *         "countryCode": "GB",
     *         "stateCode": "GB-GB",
     *         "customerEmail": "bob.doe@yc-json.com",
     *         "customerShops": ["SHOIP1"],
     *         "latestViewedSkus": [],
     *         "latestViewedCategories": [],
     *         "resolvedIp": null
     *     },
     *     "orderInfo": {
     *         "paymentGatewayLabel": null,
     *         "multipleDelivery": false,
     *         "separateBillingAddress": false,
     *         "billingAddressNotRequired": false,
     *         "deliveryAddressNotRequired": false,
     *         "carrierSlaId": 4,
     *         "billingAddressId": 2,
     *         "deliveryAddressId": 2,
     *         "orderMessage": "My Message"
     *     },
     *     "total": {
     *         "listSubTotal": 99.99,
     *         "saleSubTotal": 99.99,
     *         "nonSaleSubTotal": 99.99,
     *         "priceSubTotal": 99.99,
     *         "orderPromoApplied": false,
     *         "appliedOrderPromo": null,
     *         "subTotal": 99.99,
     *         "subTotalTax": 16.67,
     *         "subTotalAmount": 99.99,
     *         "deliveryListCost": 10.00,
     *         "deliveryCost": 10.00,
     *         "deliveryPromoApplied": false,
     *         "appliedDeliveryPromo": null,
     *         "deliveryTax": 1.67,
     *         "deliveryCostAmount": 10.00,
     *         "total": 109.99,
     *         "totalTax": 18.34,
     *         "listTotalAmount": 109.99,
     *         "totalAmount": 109.99
     *     }
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object CartRO</td><td>
     * <pre><code>
     * &lt;cart currency="EUR" locale="en"
     * 	  guid="0f842ef5-620a-415f-a8c0-a0dda6bc6735" logon-state="2"
     * 	  modified="true" modified-timestamp="1427899955177" processing-timestamp="1427899955172"&gt;
     * 	&lt;applied-coupons/&gt;
     * 	&lt;coupons/&gt;
     * 	&lt;gifts/&gt;
     * 	&lt;items&gt;
     * 		&lt;item   gift="false"
     * 				gross-price="99.99"
     * 				list-price="99.99"
     * 				net-price="83.32"
     * 				price="99.99"
     * 				product-sku-code="BENDER-ua"
     * 				promo-applied="false"
     * 				quantity="1"
     * 				sale-price="99.99"
     * 				tax-code="VAT"
     * 				tax-exclusive-of-price="false"
     * 				tax-rate="20.00"/&gt;
     * 	&lt;/items&gt;
     * 	&lt;order-info billing-address-id="4"
     * 				billing-address-not-required="false"
     * 				carrier-sla-id="4"
     * 				delivery-address-id="4"
     * 				delivery-address-not-required="false"
     * 				multiple-delivery="false"
     * 				separate-billing-address="false"/&gt;
     * 	&lt;shipping-costs&gt;
     * 		&lt;shipping-cost  gift="false"
     * 					    gross-price="10.00"
     * 					    list-price="10.00"
     * 						net-price="8.33"
     * 						price="10.00"
     * 						product-sku-code="4"
     * 						promo-applied="false"
     * 						quantity="1.00"
     * 						sale-price="10.00"
     * 						tax-code="VAT"
     * 						tax-exclusive-of-price="false"
     * 						tax-rate="20.00"/&gt;
     * 	&lt;/shipping-costs&gt;
     * 	&lt;shopping-context country-code="GB" shop-code="SHOIP1" shop-id="10" state-code="GB-GB"&gt;
     * 		&lt;customer-email&gt;bob.doe@yc-xml.com&lt;/customer-email&gt;
     * 		&lt;customer-name&gt;Bob Doe&lt;/customer-name&gt;
     * 		&lt;customer-shops&gt;
     * 			&lt;shop&gt;SHOIP1&lt;/shop&gt;
     * 		&lt;/customer-shops&gt;
     * 		&lt;latest-viewed-categories/&gt;
     * 		&lt;latest-viewed-skus/&gt;
     * 	&lt;/shopping-context&gt;
     * 	&lt;total  delivery-cost="10.00"
     * 			delivery-cost-amount="10.00"
     * 			delivery-list-cost="10.00"
     * 			delivery-promo-applied="false"
     * 			delivery-tax="1.67"
     * 			list-sub-total="99.99"
     * 			list-total-amount="109.99"
     * 			non-sale-sub-total="99.99"
     * 			promo-applied="false"
     * 			price-sub-total="99.99"
     * 			sale-sub-total="99.99"
     * 			sub-total="99.99"
     * 			sub-total-amount="99.99"
     * 			sub-total-tax="16.67"
     * 			total="109.99"
     * 			total-amount="109.99"
     * 			total-tax="18.34"/&gt;
     * &lt;/cart&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return cart object
     */
    @RequestMapping(
            value = "/cart",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CartRO viewCart(final HttpServletRequest request,
                                         final HttpServletResponse response) {

        cartMixin.persistShoppingCart(request, response);

        final CartRO cartRO = mappingMixin.map(cartMixin.getCurrentCart(), CartRO.class, ShoppingCart.class);

        return cartRO;

    }

    /**
     * Run commands from parameters. Internal commands are skipped.
     *
     * @param params request params
     */
    protected void commandInternalRun(final Map<String, Object> params) {

        if (MapUtils.isNotEmpty(params)) {

            final Map<String, Object> safe = new HashMap<String, Object>();
            for (final Map.Entry<String, Object> entry : params.entrySet()) {
                if (!CommandUtils.isInternalCommandKey(entry.getKey())) {
                    safe.put(entry.getKey(), entry.getValue());
                } else {
                    ShopCodeContext.getLog(this).warn("Received internal command request {} ... skipping", entry.getKey());
                }
            }

            if (!safe.isEmpty()) {

                shoppingCartCommandFactory.execute(cartMixin.getCurrentCart(), safe);

            }

        }

    }



    /**
     * Interface: PUT /yes-api/rest/cart
     * <p>
     * <p>
     * Display current cart.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/json</td></tr>
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>JSON example (see {@link ShoppingCartCommand})</td><td>
     * <pre><code>
     * {
     *     "addToCartCmd": "BENDER-ua",
     *     "qty": "1"
     * }
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object CartRO</td><td>
     * <pre><code>
     * {
     *     "items": [{
     *         "productSkuCode": "BENDER-ua",
     *         "quantity": 1,
     *         "price": 99.99,
     *         "salePrice": 99.99,
     *         "listPrice": 99.99,
     *         "netPrice": 83.32,
     *         "grossPrice": 99.99,
     *         "taxRate": 20.00,
     *         "taxCode": "VAT",
     *         "taxExclusiveOfPrice": false,
     *         "gift": false,
     *         "promoApplied": false,
     *         "appliedPromo": null
     *     }],
     *     "gifts": [],
     *     "coupons": [],
     *     "appliedCoupons": [],
     *     "shipping": [{
     *         "productSkuCode": "4",
     *         "quantity": 1.00,
     *         "price": 10.00,
     *         "salePrice": 10.00,
     *         "listPrice": 10.00,
     *         "netPrice": 8.33,
     *         "grossPrice": 10.00,
     *         "taxRate": 20.00,
     *         "taxCode": "VAT",
     *         "taxExclusiveOfPrice": false,
     *         "gift": false,
     *         "promoApplied": false,
     *         "appliedPromo": null
     *     }],
     *     "guid": "9633d731-15e8-40ee-afdb-1cdcca4be78e",
     *     "currentLocale": "en",
     *     "currencyCode": "EUR",
     *     "modifiedTimestamp": 1427896792015,
     *     "modified": true,
     *     "logonState": 2,
     *     "processingStartTimestamp": 1427896791992,
     *     "shoppingContext": {
     *         "customerName": "Bob Doe",
     *         "shopId": 10,
     *         "shopCode": "SHOIP1",
     *         "countryCode": "GB",
     *         "stateCode": "GB-GB",
     *         "customerEmail": "bob.doe@yc-json.com",
     *         "customerShops": ["SHOIP1"],
     *         "latestViewedSkus": [],
     *         "latestViewedCategories": [],
     *         "resolvedIp": null
     *     },
     *     "orderInfo": {
     *         "paymentGatewayLabel": null,
     *         "multipleDelivery": false,
     *         "separateBillingAddress": false,
     *         "billingAddressNotRequired": false,
     *         "deliveryAddressNotRequired": false,
     *         "carrierSlaId": 4,
     *         "billingAddressId": 2,
     *         "deliveryAddressId": 2,
     *         "orderMessage": "My Message"
     *     },
     *     "total": {
     *         "listSubTotal": 99.99,
     *         "saleSubTotal": 99.99,
     *         "nonSaleSubTotal": 99.99,
     *         "priceSubTotal": 99.99,
     *         "orderPromoApplied": false,
     *         "appliedOrderPromo": null,
     *         "subTotal": 99.99,
     *         "subTotalTax": 16.67,
     *         "subTotalAmount": 99.99,
     *         "deliveryListCost": 10.00,
     *         "deliveryCost": 10.00,
     *         "deliveryPromoApplied": false,
     *         "appliedDeliveryPromo": null,
     *         "deliveryTax": 1.67,
     *         "deliveryCostAmount": 10.00,
     *         "total": 109.99,
     *         "totalTax": 18.34,
     *         "listTotalAmount": 109.99,
     *         "totalAmount": 109.99
     *     }
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object CartRO</td><td>
     * <pre><code>
     * &lt;cart currency="EUR" locale="en"
     * 	  guid="0f842ef5-620a-415f-a8c0-a0dda6bc6735" logon-state="2"
     * 	  modified="true" modified-timestamp="1427899955177" processing-timestamp="1427899955172"&gt;
     * 	&lt;applied-coupons/&gt;
     * 	&lt;coupons/&gt;
     * 	&lt;gifts/&gt;
     * 	&lt;items&gt;
     * 		&lt;item   gift="false"
     * 				gross-price="99.99"
     * 				list-price="99.99"
     * 				net-price="83.32"
     * 				price="99.99"
     * 				product-sku-code="BENDER-ua"
     * 				promo-applied="false"
     * 				quantity="1"
     * 				sale-price="99.99"
     * 				tax-code="VAT"
     * 				tax-exclusive-of-price="false"
     * 				tax-rate="20.00"/&gt;
     * 	&lt;/items&gt;
     * 	&lt;order-info billing-address-id="4"
     * 				billing-address-not-required="false"
     * 				carrier-sla-id="4"
     * 				delivery-address-id="4"
     * 				delivery-address-not-required="false"
     * 				multiple-delivery="false"
     * 				separate-billing-address="false"/&gt;
     * 	&lt;shipping-costs&gt;
     * 		&lt;shipping-cost  gift="false"
     * 					    gross-price="10.00"
     * 					    list-price="10.00"
     * 						net-price="8.33"
     * 						price="10.00"
     * 						product-sku-code="4"
     * 						promo-applied="false"
     * 						quantity="1.00"
     * 						sale-price="10.00"
     * 						tax-code="VAT"
     * 						tax-exclusive-of-price="false"
     * 						tax-rate="20.00"/&gt;
     * 	&lt;/shipping-costs&gt;
     * 	&lt;shopping-context country-code="GB" shop-code="SHOIP1" shop-id="10" state-code="GB-GB"&gt;
     * 		&lt;customer-email&gt;bob.doe@yc-xml.com&lt;/customer-email&gt;
     * 		&lt;customer-name&gt;Bob Doe&lt;/customer-name&gt;
     * 		&lt;customer-shops&gt;
     * 			&lt;shop&gt;SHOIP1&lt;/shop&gt;
     * 		&lt;/customer-shops&gt;
     * 		&lt;latest-viewed-categories/&gt;
     * 		&lt;latest-viewed-skus/&gt;
     * 	&lt;/shopping-context&gt;
     * 	&lt;total  delivery-cost="10.00"
     * 			delivery-cost-amount="10.00"
     * 			delivery-list-cost="10.00"
     * 			delivery-promo-applied="false"
     * 			delivery-tax="1.67"
     * 			list-sub-total="99.99"
     * 			list-total-amount="109.99"
     * 			non-sale-sub-total="99.99"
     * 			promo-applied="false"
     * 			price-sub-total="99.99"
     * 			sale-sub-total="99.99"
     * 			sub-total="99.99"
     * 			sub-total-amount="99.99"
     * 			sub-total-tax="16.67"
     * 			total="109.99"
     * 			total-amount="109.99"
     * 			total-tax="18.34"/&gt;
     * &lt;/cart&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return cart object
     */
    @RequestMapping(
            value = "/cart",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody CartRO command(@RequestBody final Map<String, Object> params,
                                        final HttpServletRequest request,
                                        final HttpServletResponse response) {

        commandInternalRun(params);
        return viewCart(request, response);

    }


    /**
     * Interface: PUT /yes-api/rest/cart
     * <p>
     * <p>
     * Display current cart.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/xml</td></tr>
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>XML example (see {@link ShoppingCartCommand})</td><td>
     * <pre><code>
     *    &lt;parameters&gt;
     *    &lt;entries&gt;
     *        &lt;entry key="addToCartCmd"&gt;BENDER-ua&lt;/entry&gt;
     *        &lt;entry key="qty"&gt;1&lt;/entry&gt;
     *    &lt;/entries&gt;
     *    &lt;/parameters&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object CartRO</td><td>
     * <pre><code>
     * {
     *     "items": [{
     *         "productSkuCode": "BENDER-ua",
     *         "quantity": 1,
     *         "price": 99.99,
     *         "salePrice": 99.99,
     *         "listPrice": 99.99,
     *         "netPrice": 83.32,
     *         "grossPrice": 99.99,
     *         "taxRate": 20.00,
     *         "taxCode": "VAT",
     *         "taxExclusiveOfPrice": false,
     *         "gift": false,
     *         "promoApplied": false,
     *         "appliedPromo": null
     *     }],
     *     "gifts": [],
     *     "coupons": [],
     *     "appliedCoupons": [],
     *     "shipping": [{
     *         "productSkuCode": "4",
     *         "quantity": 1.00,
     *         "price": 10.00,
     *         "salePrice": 10.00,
     *         "listPrice": 10.00,
     *         "netPrice": 8.33,
     *         "grossPrice": 10.00,
     *         "taxRate": 20.00,
     *         "taxCode": "VAT",
     *         "taxExclusiveOfPrice": false,
     *         "gift": false,
     *         "promoApplied": false,
     *         "appliedPromo": null
     *     }],
     *     "guid": "9633d731-15e8-40ee-afdb-1cdcca4be78e",
     *     "currentLocale": "en",
     *     "currencyCode": "EUR",
     *     "modifiedTimestamp": 1427896792015,
     *     "modified": true,
     *     "logonState": 2,
     *     "processingStartTimestamp": 1427896791992,
     *     "shoppingContext": {
     *         "customerName": "Bob Doe",
     *         "shopId": 10,
     *         "shopCode": "SHOIP1",
     *         "countryCode": "GB",
     *         "stateCode": "GB-GB",
     *         "customerEmail": "bob.doe@yc-json.com",
     *         "customerShops": ["SHOIP1"],
     *         "latestViewedSkus": [],
     *         "latestViewedCategories": [],
     *         "resolvedIp": null
     *     },
     *     "orderInfo": {
     *         "paymentGatewayLabel": null,
     *         "multipleDelivery": false,
     *         "separateBillingAddress": false,
     *         "billingAddressNotRequired": false,
     *         "deliveryAddressNotRequired": false,
     *         "carrierSlaId": 4,
     *         "billingAddressId": 2,
     *         "deliveryAddressId": 2,
     *         "orderMessage": "My Message"
     *     },
     *     "total": {
     *         "listSubTotal": 99.99,
     *         "saleSubTotal": 99.99,
     *         "nonSaleSubTotal": 99.99,
     *         "priceSubTotal": 99.99,
     *         "orderPromoApplied": false,
     *         "appliedOrderPromo": null,
     *         "subTotal": 99.99,
     *         "subTotalTax": 16.67,
     *         "subTotalAmount": 99.99,
     *         "deliveryListCost": 10.00,
     *         "deliveryCost": 10.00,
     *         "deliveryPromoApplied": false,
     *         "appliedDeliveryPromo": null,
     *         "deliveryTax": 1.67,
     *         "deliveryCostAmount": 10.00,
     *         "total": 109.99,
     *         "totalTax": 18.34,
     *         "listTotalAmount": 109.99,
     *         "totalAmount": 109.99
     *     }
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object CartRO</td><td>
     * <pre><code>
     * &lt;cart currency="EUR" locale="en"
     * 	  guid="0f842ef5-620a-415f-a8c0-a0dda6bc6735" logon-state="2"
     * 	  modified="true" modified-timestamp="1427899955177" processing-timestamp="1427899955172"&gt;
     * 	&lt;applied-coupons/&gt;
     * 	&lt;coupons/&gt;
     * 	&lt;gifts/&gt;
     * 	&lt;items&gt;
     * 		&lt;item   gift="false"
     * 				gross-price="99.99"
     * 				list-price="99.99"
     * 				net-price="83.32"
     * 				price="99.99"
     * 				product-sku-code="BENDER-ua"
     * 				promo-applied="false"
     * 				quantity="1"
     * 				sale-price="99.99"
     * 				tax-code="VAT"
     * 				tax-exclusive-of-price="false"
     * 				tax-rate="20.00"/&gt;
     * 	&lt;/items&gt;
     * 	&lt;order-info billing-address-id="4"
     * 				billing-address-not-required="false"
     * 				carrier-sla-id="4"
     * 				delivery-address-id="4"
     * 				delivery-address-not-required="false"
     * 				multiple-delivery="false"
     * 				separate-billing-address="false"/&gt;
     * 	&lt;shipping-costs&gt;
     * 		&lt;shipping-cost  gift="false"
     * 					    gross-price="10.00"
     * 					    list-price="10.00"
     * 						net-price="8.33"
     * 						price="10.00"
     * 						product-sku-code="4"
     * 						promo-applied="false"
     * 						quantity="1.00"
     * 						sale-price="10.00"
     * 						tax-code="VAT"
     * 						tax-exclusive-of-price="false"
     * 						tax-rate="20.00"/&gt;
     * 	&lt;/shipping-costs&gt;
     * 	&lt;shopping-context country-code="GB" shop-code="SHOIP1" shop-id="10" state-code="GB-GB"&gt;
     * 		&lt;customer-email&gt;bob.doe@yc-xml.com&lt;/customer-email&gt;
     * 		&lt;customer-name&gt;Bob Doe&lt;/customer-name&gt;
     * 		&lt;customer-shops&gt;
     * 			&lt;shop&gt;SHOIP1&lt;/shop&gt;
     * 		&lt;/customer-shops&gt;
     * 		&lt;latest-viewed-categories/&gt;
     * 		&lt;latest-viewed-skus/&gt;
     * 	&lt;/shopping-context&gt;
     * 	&lt;total  delivery-cost="10.00"
     * 			delivery-cost-amount="10.00"
     * 			delivery-list-cost="10.00"
     * 			delivery-promo-applied="false"
     * 			delivery-tax="1.67"
     * 			list-sub-total="99.99"
     * 			list-total-amount="109.99"
     * 			non-sale-sub-total="99.99"
     * 			promo-applied="false"
     * 			price-sub-total="99.99"
     * 			sale-sub-total="99.99"
     * 			sub-total="99.99"
     * 			sub-total-amount="99.99"
     * 			sub-total-tax="16.67"
     * 			total="109.99"
     * 			total-amount="109.99"
     * 			total-tax="18.34"/&gt;
     * &lt;/cart&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return cart object
     */
    @RequestMapping(
            value = "/cart",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CartRO commandXML(@RequestBody final XMLParamsRO params,
                                           final HttpServletRequest request,
                                           final HttpServletResponse response) {

        commandInternalRun((Map) params.getParameters());
        return viewCart(request, response);

    }


    private List<CarrierRO> cartCarrierOptionsInternal() {

        final ShoppingCart cart = cartMixin.getCurrentCart();

        final List<Carrier> carriers = shippingServiceFacade.findCarriers(cart);

        final List<CarrierRO> ros = mappingMixin.map(carriers, CarrierRO.class, Carrier.class);

        return ros;

    }

    /**
     * Interface: GET /yes-api/rest/cart/options/shipping
     * <p>
     * <p>
     * Display shipping methods available.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array object CarrierRO</td><td>
     * <pre><code>
     * [{
     *     "carrierId": 1,
     *     "name": "Test carrier 1",
     *     "description": null,
     *     "worldwide": true,
     *     "country": true,
     *     "state": true,
     *     "local": true,
     *     "displayNames": null,
     *     "displayDescriptions": null,
     *     "carrierSla": [{
     *         "carrierslaId": 4,
     *         "name": "14",
     *         "description": null,
     *         "currency": "EUR",
     *         "maxDays": null,
     *         "slaType": "F",
     *         "price": 10.00,
     *         "percent": 0.00,
     *         "script": null,
     *         "priceNotLess": null,
     *         "percentNotLess": null,
     *         "costNotLess": null,
     *         "carrierId": 1,
     *         "displayNames": null,
     *         "displayDescriptions": null,
     *         "supportedPaymentGateways": "testPaymentGatewayLabel,courierPaymentGatewayLabel",
     *         "billingAddressNotRequired": false,
     *         "deliveryAddressNotRequired": false
     *     }]
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return list of carriers and SLA
     */
    @RequestMapping(
            value = "/cart/options/shipping",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<CarrierRO> cartCarrierOptions(final HttpServletRequest request,
                                                            final HttpServletResponse response) {

        cartMixin.persistShoppingCart(request, response);
        return cartCarrierOptionsInternal();

    }

    /**
     * Interface: GET /yes-api/rest/cart/options/shipping
     * <p>
     * <p>
     * Display shipping methods available.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array object CarrierRO</td><td>
     * <pre><code>
     * &lt;carriers&gt;
     * 	&lt;carrier carrier-id="1" country="true" local="true" state="true" worldwide="true"&gt;
     * 		&lt;available-sla&gt;
     * 			&lt;sla billing-address-not-required="false" carrier-id="1" carriersla-id="4" currency="EUR" delivery-address-not-required="false" sla-type="F"&gt;
     * 				&lt;name&gt;14&lt;/name&gt;
     * 				&lt;percent&gt;0.00&lt;/percent&gt;
     * 				&lt;price&gt;10.00&lt;/price&gt;
     * 				&lt;supported-payment-gateways&gt;testPaymentGatewayLabel,courierPaymentGatewayLabel&lt;/supported-payment-gateways&gt;
     * 			&lt;/sla&gt;
     * 		&lt;/available-sla&gt;
     * 		&lt;name&gt;Test carrier 1&lt;/name&gt;
     * 	&lt;/carrier&gt;
     * &lt;/carriers&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return list of carriers and SLA
     */
    @RequestMapping(
            value = "/cart/options/shipping",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CarrierListRO cartCarrierOptionsXML(final HttpServletRequest request,
                                                             final HttpServletResponse response) {

        cartMixin.persistShoppingCart(request, response);
        return new CarrierListRO(cartCarrierOptionsInternal());

    }

    /**
     * Interface: PUT /yes-api/rest/cart/options/shipping
     * <p>
     * <p>
     * Set shipping method.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/json or application/xml</td></tr>
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>JSON example (see {@link ShippingOptionRO})</td><td>
     * <pre><code>
     * {
     *     "carrierslaId": "4",
     *     "billingAddressId": null,
     *     "deliveryAddressId": null
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example (see {@link ShippingOptionRO})</td><td>
     * <pre><code>
     * 	&lt;shipping-option/&gt;
     * 	&lt;carriersla-id/&gt;4&lt;/carriersla-id/&gt;
     * 	&lt;billing-address-id/&gt;4&lt;/billing-address-id/&gt;
     * 	&lt;delivery-address-id/&gt;4&lt;/delivery-address-id/&gt;
     * 	&lt;/shipping-option/&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object CartRO</td><td>
     * <pre><code>
     * {
     *     "items": [{
     *         "productSkuCode": "BENDER-ua",
     *         "quantity": 1,
     *         "price": 99.99,
     *         "salePrice": 99.99,
     *         "listPrice": 99.99,
     *         "netPrice": 83.32,
     *         "grossPrice": 99.99,
     *         "taxRate": 20.00,
     *         "taxCode": "VAT",
     *         "taxExclusiveOfPrice": false,
     *         "gift": false,
     *         "promoApplied": false,
     *         "appliedPromo": null
     *     }],
     *     "gifts": [],
     *     "coupons": [],
     *     "appliedCoupons": [],
     *     "shipping": [{
     *         "productSkuCode": "4",
     *         "quantity": 1.00,
     *         "price": 10.00,
     *         "salePrice": 10.00,
     *         "listPrice": 10.00,
     *         "netPrice": 8.33,
     *         "grossPrice": 10.00,
     *         "taxRate": 20.00,
     *         "taxCode": "VAT",
     *         "taxExclusiveOfPrice": false,
     *         "gift": false,
     *         "promoApplied": false,
     *         "appliedPromo": null
     *     }],
     *     "guid": "9633d731-15e8-40ee-afdb-1cdcca4be78e",
     *     "currentLocale": "en",
     *     "currencyCode": "EUR",
     *     "modifiedTimestamp": 1427896792015,
     *     "modified": true,
     *     "logonState": 2,
     *     "processingStartTimestamp": 1427896791992,
     *     "shoppingContext": {
     *         "customerName": "Bob Doe",
     *         "shopId": 10,
     *         "shopCode": "SHOIP1",
     *         "countryCode": "GB",
     *         "stateCode": "GB-GB",
     *         "customerEmail": "bob.doe@yc-json.com",
     *         "customerShops": ["SHOIP1"],
     *         "latestViewedSkus": [],
     *         "latestViewedCategories": [],
     *         "resolvedIp": null
     *     },
     *     "orderInfo": {
     *         "paymentGatewayLabel": null,
     *         "multipleDelivery": false,
     *         "separateBillingAddress": false,
     *         "billingAddressNotRequired": false,
     *         "deliveryAddressNotRequired": false,
     *         "carrierSlaId": 4,
     *         "billingAddressId": 2,
     *         "deliveryAddressId": 2,
     *         "orderMessage": "My Message"
     *     },
     *     "total": {
     *         "listSubTotal": 99.99,
     *         "saleSubTotal": 99.99,
     *         "nonSaleSubTotal": 99.99,
     *         "priceSubTotal": 99.99,
     *         "orderPromoApplied": false,
     *         "appliedOrderPromo": null,
     *         "subTotal": 99.99,
     *         "subTotalTax": 16.67,
     *         "subTotalAmount": 99.99,
     *         "deliveryListCost": 10.00,
     *         "deliveryCost": 10.00,
     *         "deliveryPromoApplied": false,
     *         "appliedDeliveryPromo": null,
     *         "deliveryTax": 1.67,
     *         "deliveryCostAmount": 10.00,
     *         "total": 109.99,
     *         "totalTax": 18.34,
     *         "listTotalAmount": 109.99,
     *         "totalAmount": 109.99
     *     }
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object CartRO</td><td>
     * <pre><code>
     * &lt;cart currency="EUR" locale="en"
     * 	  guid="0f842ef5-620a-415f-a8c0-a0dda6bc6735" logon-state="2"
     * 	  modified="true" modified-timestamp="1427899955177" processing-timestamp="1427899955172"&gt;
     * 	&lt;applied-coupons/&gt;
     * 	&lt;coupons/&gt;
     * 	&lt;gifts/&gt;
     * 	&lt;items&gt;
     * 		&lt;item   gift="false"
     * 				gross-price="99.99"
     * 				list-price="99.99"
     * 				net-price="83.32"
     * 				price="99.99"
     * 				product-sku-code="BENDER-ua"
     * 				promo-applied="false"
     * 				quantity="1"
     * 				sale-price="99.99"
     * 				tax-code="VAT"
     * 				tax-exclusive-of-price="false"
     * 				tax-rate="20.00"/&gt;
     * 	&lt;/items&gt;
     * 	&lt;order-info billing-address-id="4"
     * 				billing-address-not-required="false"
     * 				carrier-sla-id="4"
     * 				delivery-address-id="4"
     * 				delivery-address-not-required="false"
     * 				multiple-delivery="false"
     * 				separate-billing-address="false"/&gt;
     * 	&lt;shipping-costs&gt;
     * 		&lt;shipping-cost  gift="false"
     * 					    gross-price="10.00"
     * 					    list-price="10.00"
     * 						net-price="8.33"
     * 						price="10.00"
     * 						product-sku-code="4"
     * 						promo-applied="false"
     * 						quantity="1.00"
     * 						sale-price="10.00"
     * 						tax-code="VAT"
     * 						tax-exclusive-of-price="false"
     * 						tax-rate="20.00"/&gt;
     * 	&lt;/shipping-costs&gt;
     * 	&lt;shopping-context country-code="GB" shop-code="SHOIP1" shop-id="10" state-code="GB-GB"&gt;
     * 		&lt;customer-email&gt;bob.doe@yc-xml.com&lt;/customer-email&gt;
     * 		&lt;customer-name&gt;Bob Doe&lt;/customer-name&gt;
     * 		&lt;customer-shops&gt;
     * 			&lt;shop&gt;SHOIP1&lt;/shop&gt;
     * 		&lt;/customer-shops&gt;
     * 		&lt;latest-viewed-categories/&gt;
     * 		&lt;latest-viewed-skus/&gt;
     * 	&lt;/shopping-context&gt;
     * 	&lt;total  delivery-cost="10.00"
     * 			delivery-cost-amount="10.00"
     * 			delivery-list-cost="10.00"
     * 			delivery-promo-applied="false"
     * 			delivery-tax="1.67"
     * 			list-sub-total="99.99"
     * 			list-total-amount="109.99"
     * 			non-sale-sub-total="99.99"
     * 			promo-applied="false"
     * 			price-sub-total="99.99"
     * 			sale-sub-total="99.99"
     * 			sub-total="99.99"
     * 			sub-total-amount="99.99"
     * 			sub-total-tax="16.67"
     * 			total="109.99"
     * 			total-amount="109.99"
     * 			total-tax="18.34"/&gt;
     * &lt;/cart&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return cart object
     */
    @RequestMapping(
            value = "/cart/options/shipping",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CartRO cartCarrierOptionsSet(@RequestBody final ShippingOptionRO shippingOption,
                                                      final HttpServletRequest request,
                                                      final HttpServletResponse response) {


        final long carrierId = NumberUtils.toLong(shippingOption.getCarrierslaId());

        if (carrierId > 0L) {

            final ShoppingCart cart = cartMixin.getCurrentCart();

            final List<Carrier> carriers = shippingServiceFacade.findCarriers(cart);
            CarrierSla carrierSla = null;
            if (CollectionUtils.isNotEmpty(carriers)) {
                for (final Carrier carrier : carriers) {
                    for (final CarrierSla sla : carrier.getCarrierSla()) {
                        if (sla.getCarrierslaId() == carrierId) {
                            carrierSla = sla;
                            break;
                        }
                    }
                }
            }
            if (carrierSla != null) {

                final boolean billingNotRequired = carrierSla.isBillingAddressNotRequired();
                final boolean deliveryNotRequired = carrierSla.isDeliveryAddressNotRequired();

                final long billingAddressId = NumberUtils.toLong(shippingOption.getBillingAddressId(),
                        cart.getOrderInfo().getBillingAddressId() != null ? cart.getOrderInfo().getBillingAddressId() : 0L);
                final long deliveryAddressId = NumberUtils.toLong(shippingOption.getDeliveryAddressId(),
                        cart.getOrderInfo().getDeliveryAddressId() != null ? cart.getOrderInfo().getDeliveryAddressId() : 0L);

                Address billing = null;
                Address delivery = null;

                if ((billingAddressId > 0L || deliveryAddressId > 0L) && cart.getLogonState() == ShoppingCart.LOGGED_IN) {

                    final Customer customer = customerServiceFacade.getCustomerByEmail(cart.getCustomerEmail());

                    for (final Address address : customer.getAddress()) {
                        if (address.getAddressId() == billingAddressId) {
                            billing = address;
                        }
                        if (address.getAddressId() == deliveryAddressId) {
                            delivery = address;
                        }
                    }
                }

                if ((billing != null || billingNotRequired) && (delivery != null || deliveryNotRequired)) {

                    final Map<String, Object> params = new HashMap<String, Object>();
                    params.put(ShoppingCartCommand.CMD_SETCARRIERSLA, String.valueOf(carrierSla.getCarrierslaId()));
                    params.put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_BILLING_NOT_REQUIRED, carrierSla.isBillingAddressNotRequired());
                    params.put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_BILLING_ADDRESS, billing);
                    params.put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_DELIVERY_NOT_REQUIRED, carrierSla.isDeliveryAddressNotRequired());
                    params.put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_DELIVERY_ADDRESS, delivery);

                    shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SETCARRIERSLA, cart, params);

                }

            }

        }

        return viewCart(request, response);

    }



    private List<AddressRO> cartAddressOptionsInternal(final String type) {

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final Shop shop = cartMixin.getCurrentShop();

        return addressSupportMixin.viewAddressOptions(cart, shop, type);

    }

    /**
     * Interface: GET /yes-api/rest/cart/options/address/{type}
     * <p>
     * <p>
     * Display customer address book.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array object AddressRO</td><td>
     * <pre><code>
     * [{
     *     "addressId": 1,
     *     "city": "Nowhere",
     *     "postcode": "0001",
     *     "addrline1": "In the middle of",
     *     "addrline2": null,
     *     "addressType": "S",
     *     "countryCode": "UA",
     *     "countryName": "Ukraine",
     *     "countryLocalName": null,
     *     "stateCode": "UA-UA",
     *     "stateName": "Ukraine",
     *     "stateLocalName": null,
     *     "firstname": "Bob",
     *     "lastname": "Doe",
     *     "middlename": null,
     *     "defaultAddress": true,
     *     "phoneList": "123123123",
     *     "customerId": 1
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param request request
     * @param response response
     *
     * @return list of addresses
     */
    @RequestMapping(
            value = "/cart/options/address/{type}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<AddressRO> cartAddressOptions(@PathVariable(value = "type") final String type,
                                                            final HttpServletRequest request,
                                                            final HttpServletResponse response) {

        cartMixin.persistShoppingCart(request, response);
        return cartAddressOptionsInternal(type);

    }

    /**
     * Interface: GET /yes-api/rest/cart/options/address/{type}
     * <p>
     * <p>
     * Display customer address book.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array object AddressRO</td><td>
     * <pre><code>
     * &lt;addresses&gt;
     * &lt;address address-id="3" address-type="S" customer-id="2" default-address="true"&gt;
     * 	&lt;addrline1&gt;In the middle of&lt;/addrline1&gt;
     * 	&lt;city&gt;Nowhere&lt;/city&gt;
     * 	&lt;country-code&gt;UA&lt;/country-code&gt;
     * 	&lt;country-name&gt;Ukraine&lt;/country-name&gt;
     * 	&lt;country-local-name&gt;Україна&lt;/country-local-name&gt;
     * 	&lt;firstname&gt;Bob&lt;/firstname&gt;
     * 	&lt;lastname&gt;Doe&lt;/lastname&gt;
     * 	&lt;phone-list&gt;123123123&lt;/phone-list&gt;
     * 	&lt;postcode&gt;0001&lt;/postcode&gt;
     * 	&lt;state-code&gt;UA-KI&lt;/state-code&gt;
     * 	&lt;state-name&gt;Kiev region&lt;/state-name&gt;
     * 	&lt;state-local-name&gt;Київська обл&lt;/state-local-name&gt;
     * &lt;/address&gt;
     * &lt;/addresses&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param request request
     * @param response response
     *
     * @return list of addresses
     */
    @RequestMapping(
            value = "/cart/options/address/{type}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody AddressListRO cartAddressOptionsXML(@PathVariable(value = "type") final String type,
                                                             final HttpServletRequest request,
                                                             final HttpServletResponse response) {

        cartMixin.persistShoppingCart(request, response);
        return new AddressListRO(cartAddressOptionsInternal(type));

    }


    /**
     * Interface: PUT /yes-api/rest/cart/options/address/{type}
     * <p>
     * <p>
     * Set shipping method.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/json or application/xml</td></tr>
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     *     <tr><td>JSON example (see {@link AddressOptionRO})</td><td>
     * <pre><code>
     * {
     *     "addressId": "4",
     *     "shippingSameAsBilling": true
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example (see {@link AddressOptionRO})</td><td>
     * <pre><code>
     * 	&lt;address-option/&gt;
     * 	&lt;address-id/&gt;4&lt;/address-id/&gt;
     * 	&lt;shipping-same-as-billing/&gt;true&lt;/shipping-same-as-billing/&gt;
     * 	&lt;/address-option/&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object CartRO</td><td>
     * <pre><code>
     * {
     *     "items": [{
     *         "productSkuCode": "BENDER-ua",
     *         "quantity": 1,
     *         "price": 99.99,
     *         "salePrice": 99.99,
     *         "listPrice": 99.99,
     *         "netPrice": 83.32,
     *         "grossPrice": 99.99,
     *         "taxRate": 20.00,
     *         "taxCode": "VAT",
     *         "taxExclusiveOfPrice": false,
     *         "gift": false,
     *         "promoApplied": false,
     *         "appliedPromo": null
     *     }],
     *     "gifts": [],
     *     "coupons": [],
     *     "appliedCoupons": [],
     *     "shipping": [{
     *         "productSkuCode": "4",
     *         "quantity": 1.00,
     *         "price": 10.00,
     *         "salePrice": 10.00,
     *         "listPrice": 10.00,
     *         "netPrice": 8.33,
     *         "grossPrice": 10.00,
     *         "taxRate": 20.00,
     *         "taxCode": "VAT",
     *         "taxExclusiveOfPrice": false,
     *         "gift": false,
     *         "promoApplied": false,
     *         "appliedPromo": null
     *     }],
     *     "guid": "9633d731-15e8-40ee-afdb-1cdcca4be78e",
     *     "currentLocale": "en",
     *     "currencyCode": "EUR",
     *     "modifiedTimestamp": 1427896792015,
     *     "modified": true,
     *     "logonState": 2,
     *     "processingStartTimestamp": 1427896791992,
     *     "shoppingContext": {
     *         "customerName": "Bob Doe",
     *         "shopId": 10,
     *         "shopCode": "SHOIP1",
     *         "countryCode": "GB",
     *         "stateCode": "GB-GB",
     *         "customerEmail": "bob.doe@yc-json.com",
     *         "customerShops": ["SHOIP1"],
     *         "latestViewedSkus": [],
     *         "latestViewedCategories": [],
     *         "resolvedIp": null
     *     },
     *     "orderInfo": {
     *         "paymentGatewayLabel": null,
     *         "multipleDelivery": false,
     *         "separateBillingAddress": false,
     *         "billingAddressNotRequired": false,
     *         "deliveryAddressNotRequired": false,
     *         "carrierSlaId": 4,
     *         "billingAddressId": 2,
     *         "deliveryAddressId": 2,
     *         "orderMessage": "My Message"
     *     },
     *     "total": {
     *         "listSubTotal": 99.99,
     *         "saleSubTotal": 99.99,
     *         "nonSaleSubTotal": 99.99,
     *         "priceSubTotal": 99.99,
     *         "orderPromoApplied": false,
     *         "appliedOrderPromo": null,
     *         "subTotal": 99.99,
     *         "subTotalTax": 16.67,
     *         "subTotalAmount": 99.99,
     *         "deliveryListCost": 10.00,
     *         "deliveryCost": 10.00,
     *         "deliveryPromoApplied": false,
     *         "appliedDeliveryPromo": null,
     *         "deliveryTax": 1.67,
     *         "deliveryCostAmount": 10.00,
     *         "total": 109.99,
     *         "totalTax": 18.34,
     *         "listTotalAmount": 109.99,
     *         "totalAmount": 109.99
     *     }
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object CartRO</td><td>
     * <pre><code>
     * &lt;cart currency="EUR" locale="en"
     * 	  guid="0f842ef5-620a-415f-a8c0-a0dda6bc6735" logon-state="2"
     * 	  modified="true" modified-timestamp="1427899955177" processing-timestamp="1427899955172"&gt;
     * 	&lt;applied-coupons/&gt;
     * 	&lt;coupons/&gt;
     * 	&lt;gifts/&gt;
     * 	&lt;items&gt;
     * 		&lt;item   gift="false"
     * 				gross-price="99.99"
     * 				list-price="99.99"
     * 				net-price="83.32"
     * 				price="99.99"
     * 				product-sku-code="BENDER-ua"
     * 				promo-applied="false"
     * 				quantity="1"
     * 				sale-price="99.99"
     * 				tax-code="VAT"
     * 				tax-exclusive-of-price="false"
     * 				tax-rate="20.00"/&gt;
     * 	&lt;/items&gt;
     * 	&lt;order-info billing-address-id="4"
     * 				billing-address-not-required="false"
     * 				carrier-sla-id="4"
     * 				delivery-address-id="4"
     * 				delivery-address-not-required="false"
     * 				multiple-delivery="false"
     * 				separate-billing-address="false"/&gt;
     * 	&lt;shipping-costs&gt;
     * 		&lt;shipping-cost  gift="false"
     * 					    gross-price="10.00"
     * 					    list-price="10.00"
     * 						net-price="8.33"
     * 						price="10.00"
     * 						product-sku-code="4"
     * 						promo-applied="false"
     * 						quantity="1.00"
     * 						sale-price="10.00"
     * 						tax-code="VAT"
     * 						tax-exclusive-of-price="false"
     * 						tax-rate="20.00"/&gt;
     * 	&lt;/shipping-costs&gt;
     * 	&lt;shopping-context country-code="GB" shop-code="SHOIP1" shop-id="10" state-code="GB-GB"&gt;
     * 		&lt;customer-email&gt;bob.doe@yc-xml.com&lt;/customer-email&gt;
     * 		&lt;customer-name&gt;Bob Doe&lt;/customer-name&gt;
     * 		&lt;customer-shops&gt;
     * 			&lt;shop&gt;SHOIP1&lt;/shop&gt;
     * 		&lt;/customer-shops&gt;
     * 		&lt;latest-viewed-categories/&gt;
     * 		&lt;latest-viewed-skus/&gt;
     * 	&lt;/shopping-context&gt;
     * 	&lt;total  delivery-cost="10.00"
     * 			delivery-cost-amount="10.00"
     * 			delivery-list-cost="10.00"
     * 			delivery-promo-applied="false"
     * 			delivery-tax="1.67"
     * 			list-sub-total="99.99"
     * 			list-total-amount="109.99"
     * 			non-sale-sub-total="99.99"
     * 			promo-applied="false"
     * 			price-sub-total="99.99"
     * 			sale-sub-total="99.99"
     * 			sub-total="99.99"
     * 			sub-total-amount="99.99"
     * 			sub-total-tax="16.67"
     * 			total="109.99"
     * 			total-amount="109.99"
     * 			total-tax="18.34"/&gt;
     * &lt;/cart&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return cart object
     */
    @RequestMapping(
            value = "/cart/options/address/{type}",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CartRO cartAddressOptions(@PathVariable(value = "type") final String type,
                                                   @RequestBody final AddressOptionRO option,
                                                   final HttpServletRequest request,
                                                   final HttpServletResponse response) {


        cartMixin.throwSecurityExceptionIfNotLoggedIn();

        final long addressId = NumberUtils.toLong(option.getAddressId());

        if (addressId > 0L) {
            final ShoppingCart cart = cartMixin.getCurrentCart();
            final Shop shop = cartMixin.getCurrentShop();

            final List<AddressRO> addresses = addressSupportMixin.viewAddressOptions(cart, shop, type);

            for (final AddressRO address : addresses) {
                if (address.getAddressId() == addressId) {

                    final Customer customer = customerServiceFacade.getCustomerByEmail(cart.getCustomerEmail());

                    final Address optionAddress = addressBookFacade.getAddress(customer, option.getAddressId(), type);

                    if (optionAddress != null) {

                        final Map<String, Object> params = new HashMap<String, Object>();
                        params.put(ShoppingCartCommand.CMD_SETADDRESES, ShoppingCartCommand.CMD_SETADDRESES);
                        if (Address.ADDR_TYPE_BILLING.equals(type) || option.isShippingSameAsBilling()) {
                            params.put(ShoppingCartCommand.CMD_SETADDRESES_P_BILLING_ADDRESS, optionAddress);
                        }
                        if (Address.ADDR_TYPE_SHIPPING.equals(type) || option.isShippingSameAsBilling()) {
                            params.put(ShoppingCartCommand.CMD_SETADDRESES_P_DELIVERY_ADDRESS, optionAddress);
                        }

                        params.put(ShoppingCartCommand.CMD_SEPARATEBILLING, String.valueOf(!option.isShippingSameAsBilling()));

                        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SEPARATEBILLING, cart, params);
                        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SETADDRESES, cart, params);

                    }
                    break;
                }
            }
        }
        return viewCart(request, response);

    }




    private List<PaymentGatewayRO> cartPaymentOptionsInternal() {

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final Shop shop = cartMixin.getCurrentShop();

        final List<Pair<PaymentGatewayDescriptor, String>> descriptors = checkoutServiceFacade.getPaymentGatewaysDescriptors(shop, cart);

        final List<PaymentGatewayRO> ros = new ArrayList<PaymentGatewayRO>();
        for (final Pair<PaymentGatewayDescriptor, String> descriptor : descriptors) {
            final PaymentGatewayRO ro = new PaymentGatewayRO();
            ro.setPgLabel(descriptor.getFirst().getLabel());
            ro.setName(descriptor.getSecond());
            ros.add(ro);
        }

        return ros;

    }



    /**
     * Interface: GET /yes-api/rest/cart/options/payment
     * <p>
     * <p>
     * Display payment gateways available.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array object PaymentGatewayRO. "name" depends on the locale of the cart.</td><td>
     * <pre><code>
     * [{
     *     "pgLabel": "courierPaymentGatewayLabel",
     *     "name": "courierPaymentGateway"
     * }, {
     *     "pgLabel": "testPaymentGatewayLabel",
     *     "name": "testPaymentGateway"
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return list of payment gateways options
     */
    @RequestMapping(
            value = "/cart/options/payment",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<PaymentGatewayRO> cartPaymentOptions(final HttpServletRequest request,
                                                                   final HttpServletResponse response) {

        cartMixin.persistShoppingCart(request, response);
        return cartPaymentOptionsInternal();

    }

    /**
     * Interface: GET /yes-api/rest/cart/options/payment
     * <p>
     * <p>
     * Display payment gateways available.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array object PaymentGatewayRO. "name" depends on the locale of the cart.</td><td>
     * <pre><code>
     * &lt;payment-gateways&gt;
     * 	&lt;payment-gateway pg-label="courierPaymentGatewayLabel"&gt;
     * 		&lt;name&gt;courierPaymentGateway&lt;/name&gt;
     * 	&lt;/payment-gateway&gt;
     * 	&lt;payment-gateway pg-label="testPaymentGatewayLabel"&gt;
     * 		&lt;name&gt;testPaymentGateway&lt;/name&gt;
     * 	&lt;/payment-gateway&gt;
     * &lt;/payment-gateways&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return list of payment gateways options
     */
    @RequestMapping(
            value = "/cart/options/payment",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody PaymentGatewayListRO cartPaymentOptionsXML(final HttpServletRequest request,
                                                                    final HttpServletResponse response) {

        cartMixin.persistShoppingCart(request, response);
        return new PaymentGatewayListRO(cartPaymentOptionsInternal());

    }


    /**
     * Interface: PUT /yes-api/rest/cart/options/payment
     * <p>
     * <p>
     * Set payment option for current cart.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/json or application/json</td></tr>
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *     "pgLabel": "courierPaymentGatewayLabel"
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     *    &lt;payment-gateway-option&gt;
     *        &lt;pg-label&gt;courierPaymentGatewayLabel&lt;/pg-label&gt;
     *    &lt;/payment-gateway-option&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object CartRO</td><td>
     * <pre><code>
     * {
     *     "items": [{
     *         "productSkuCode": "BENDER-ua",
     *         "quantity": 1,
     *         "price": 99.99,
     *         "salePrice": 99.99,
     *         "listPrice": 99.99,
     *         "netPrice": 83.32,
     *         "grossPrice": 99.99,
     *         "taxRate": 20.00,
     *         "taxCode": "VAT",
     *         "taxExclusiveOfPrice": false,
     *         "gift": false,
     *         "promoApplied": false,
     *         "appliedPromo": null
     *     }],
     *     "gifts": [],
     *     "coupons": [],
     *     "appliedCoupons": [],
     *     "shipping": [{
     *         "productSkuCode": "4",
     *         "quantity": 1.00,
     *         "price": 10.00,
     *         "salePrice": 10.00,
     *         "listPrice": 10.00,
     *         "netPrice": 8.33,
     *         "grossPrice": 10.00,
     *         "taxRate": 20.00,
     *         "taxCode": "VAT",
     *         "taxExclusiveOfPrice": false,
     *         "gift": false,
     *         "promoApplied": false,
     *         "appliedPromo": null
     *     }],
     *     "guid": "9633d731-15e8-40ee-afdb-1cdcca4be78e",
     *     "currentLocale": "en",
     *     "currencyCode": "EUR",
     *     "modifiedTimestamp": 1427896792015,
     *     "modified": true,
     *     "logonState": 2,
     *     "processingStartTimestamp": 1427896791992,
     *     "shoppingContext": {
     *         "customerName": "Bob Doe",
     *         "shopId": 10,
     *         "shopCode": "SHOIP1",
     *         "countryCode": "GB",
     *         "stateCode": "GB-GB",
     *         "customerEmail": "bob.doe@yc-json.com",
     *         "customerShops": ["SHOIP1"],
     *         "latestViewedSkus": [],
     *         "latestViewedCategories": [],
     *         "resolvedIp": null
     *     },
     *     "orderInfo": {
     *         "paymentGatewayLabel": "courierPaymentGatewayLabel",
     *         "multipleDelivery": false,
     *         "separateBillingAddress": false,
     *         "billingAddressNotRequired": false,
     *         "deliveryAddressNotRequired": false,
     *         "carrierSlaId": 4,
     *         "billingAddressId": 2,
     *         "deliveryAddressId": 2,
     *         "orderMessage": "My Message"
     *     },
     *     "total": {
     *         "listSubTotal": 99.99,
     *         "saleSubTotal": 99.99,
     *         "nonSaleSubTotal": 99.99,
     *         "priceSubTotal": 99.99,
     *         "orderPromoApplied": false,
     *         "appliedOrderPromo": null,
     *         "subTotal": 99.99,
     *         "subTotalTax": 16.67,
     *         "subTotalAmount": 99.99,
     *         "deliveryListCost": 10.00,
     *         "deliveryCost": 10.00,
     *         "deliveryPromoApplied": false,
     *         "appliedDeliveryPromo": null,
     *         "deliveryTax": 1.67,
     *         "deliveryCostAmount": 10.00,
     *         "total": 109.99,
     *         "totalTax": 18.34,
     *         "listTotalAmount": 109.99,
     *         "totalAmount": 109.99
     *     }
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object CartRO</td><td>
     * <pre><code>
     * &lt;cart currency="EUR" locale="en"
     * 	  guid="0f842ef5-620a-415f-a8c0-a0dda6bc6735" logon-state="2"
     * 	  modified="true" modified-timestamp="1427899955177" processing-timestamp="1427899955172"&gt;
     * 	&lt;applied-coupons/&gt;
     * 	&lt;coupons/&gt;
     * 	&lt;gifts/&gt;
     * 	&lt;items&gt;
     * 		&lt;item   gift="false"
     * 				gross-price="99.99"
     * 				list-price="99.99"
     * 				net-price="83.32"
     * 				price="99.99"
     * 				product-sku-code="BENDER-ua"
     * 				promo-applied="false"
     * 				quantity="1"
     * 				sale-price="99.99"
     * 				tax-code="VAT"
     * 				tax-exclusive-of-price="false"
     * 				tax-rate="20.00"/&gt;
     * 	&lt;/items&gt;
     * 	&lt;order-info billing-address-id="4"
     * 				billing-address-not-required="false"
     * 				carrier-sla-id="4"
     * 				delivery-address-id="4"
     * 				delivery-address-not-required="false"
     * 				multiple-delivery="false"
     * 			    payment-gateway-label="courierPaymentGatewayLabel"
     * 				separate-billing-address="false"/&gt;
     * 	&lt;shipping-costs&gt;
     * 		&lt;shipping-cost  gift="false"
     * 					    gross-price="10.00"
     * 					    list-price="10.00"
     * 						net-price="8.33"
     * 						price="10.00"
     * 						product-sku-code="4"
     * 						promo-applied="false"
     * 						quantity="1.00"
     * 						sale-price="10.00"
     * 						tax-code="VAT"
     * 						tax-exclusive-of-price="false"
     * 						tax-rate="20.00"/&gt;
     * 	&lt;/shipping-costs&gt;
     * 	&lt;shopping-context country-code="GB" shop-code="SHOIP1" shop-id="10" state-code="GB-GB"&gt;
     * 		&lt;customer-email&gt;bob.doe@yc-xml.com&lt;/customer-email&gt;
     * 		&lt;customer-name&gt;Bob Doe&lt;/customer-name&gt;
     * 		&lt;customer-shops&gt;
     * 			&lt;shop&gt;SHOIP1&lt;/shop&gt;
     * 		&lt;/customer-shops&gt;
     * 		&lt;latest-viewed-categories/&gt;
     * 		&lt;latest-viewed-skus/&gt;
     * 	&lt;/shopping-context&gt;
     * 	&lt;total  delivery-cost="10.00"
     * 			delivery-cost-amount="10.00"
     * 			delivery-list-cost="10.00"
     * 			delivery-promo-applied="false"
     * 			delivery-tax="1.67"
     * 			list-sub-total="99.99"
     * 			list-total-amount="109.99"
     * 			non-sale-sub-total="99.99"
     * 			promo-applied="false"
     * 			price-sub-total="99.99"
     * 			sale-sub-total="99.99"
     * 			sub-total="99.99"
     * 			sub-total-amount="99.99"
     * 			sub-total-tax="16.67"
     * 			total="109.99"
     * 			total-amount="109.99"
     * 			total-tax="18.34"/&gt;
     * &lt;/cart&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return cart object
     */
    @RequestMapping(
            value = "/cart/options/payment",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CartRO cartPaymentOptionsSet(@RequestBody final PaymentGatewayOptionRO option,
                                                      final HttpServletRequest request,
                                                      final HttpServletResponse response) {

        final List<PaymentGatewayRO> available = cartPaymentOptionsInternal();
        for (final PaymentGatewayRO pg : available) {
            if (pg.getPgLabel().equals(option.getPgLabel())) {
                final ShoppingCart cart = cartMixin.getCurrentCart();
                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SETPGLABEL, cart,
                        (Map) Collections.singletonMap(ShoppingCartCommand.CMD_SETPGLABEL, option.getPgLabel()));
            }
        }

        return viewCart(request, response);

    }



    /**
     * Interface: PUT /order/preview
     * <p>
     * <p>
     * Create order preview (temporary order that is ready to be paid).
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/json or application/json</td></tr>
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *     "forceSingleDelivery": true
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     *    &lt;order-delivery-option&gt;
     *        &lt;force-single-delivery&gt;true&lt;/force-single-delivery&gt;
     *    &lt;/order-delivery-option&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object OrderPreviewRO</td><td>
     * <pre><code>
     * {
     *     "success": true,
     *     "problems": null,
     *     "pgName": "courierPaymentGateway",
     *     "pgOnline": false,
     *     "pgExternalForm": false,
     *     "pgFormUrl": "/order/payment",
     *     "pgFormHtml": "&lt;form method=\"POST\" action=\"/order/payment\"&gt;\n\n&lt;input type=\"submit\" value=\"submit\"&gt;&lt;/form&gt;",
     *     "order": {
     *         "customerorderId": 1,
     *         "ordernum": "150407103424-1",
     *         "pgLabel": "courierPaymentGatewayLabel",
     *         "billingAddress": "In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123",
     *         "shippingAddress": "In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123",
     *         "cartGuid": "a06cd0b1-b153-47ec-9cc1-12e30320a9c1",
     *         "currency": "EUR",
     *         "orderMessage": "My Message",
     *         "orderStatus": "os.none",
     *         "multipleShipmentOption": false,
     *         "orderTimestamp": 1428399264811,
     *         "email": "bob.doe@yc-checkout-json.com",
     *         "firstname": "Bob",
     *         "lastname": "Doe",
     *         "middlename": null,
     *         "customerId": 1,
     *         "shopId": 10,
     *         "code": "SHOIP1",
     *         "total": {
     *             "listSubTotal": 99.99,
     *             "saleSubTotal": 99.99,
     *             "nonSaleSubTotal": 99.99,
     *             "priceSubTotal": 99.99,
     *             "orderPromoApplied": false,
     *             "appliedOrderPromo": null,
     *             "subTotal": 99.99,
     *             "subTotalTax": 16.67,
     *             "subTotalAmount": 99.99,
     *             "deliveryListCost": 10.00,
     *             "deliveryCost": 10.00,
     *             "deliveryPromoApplied": false,
     *             "appliedDeliveryPromo": null,
     *             "deliveryTax": 1.67,
     *             "deliveryCostAmount": 10.00,
     *             "total": 109.99,
     *             "totalTax": 18.34,
     *             "listTotalAmount": 109.99,
     *             "totalAmount": 109.99
     *         },
     *         "deliveries": [{
     *             "customerOrderDeliveryId": 1,
     *             "deliveryNum": "150407103424-1-0",
     *             "refNo": null,
     *             "carrierSlaId": 4,
     *             "carrierSlaName": "14",
     *             "carrierSlaDisplayNames": null,
     *             "carrierId": 1,
     *             "carrierName": "Test carrier 1",
     *             "carrierDisplayNames": null,
     *             "deliveryStatus": "ds.fullfillment",
     *             "deliveryGroup": "D1",
     *             "total": {
     *                 "listSubTotal": 99.99,
     *                 "saleSubTotal": 99.99,
     *                 "nonSaleSubTotal": 99.99,
     *                 "priceSubTotal": 99.99,
     *                 "orderPromoApplied": false,
     *                 "appliedOrderPromo": null,
     *                 "subTotal": 99.99,
     *                 "subTotalTax": 16.67,
     *                 "subTotalAmount": 99.99,
     *                 "deliveryListCost": 10.00,
     *                 "deliveryCost": 10.00,
     *                 "deliveryPromoApplied": false,
     *                 "appliedDeliveryPromo": null,
     *                 "deliveryTax": 1.67,
     *                 "deliveryCostAmount": 10.00,
     *                 "total": 109.99,
     *                 "totalTax": 18.34,
     *                 "listTotalAmount": 109.99,
     *                 "totalAmount": 109.99
     *             },
     *             "deliveryItems": [{
     *                 "productSkuCode": "BENDER-ua",
     *                 "quantity": 1,
     *                 "price": 99.99,
     *                 "salePrice": 99.99,
     *                 "listPrice": 99.99,
     *                 "netPrice": 83.32,
     *                 "grossPrice": 99.99,
     *                 "taxRate": 20.00,
     *                 "taxCode": "VAT",
     *                 "taxExclusiveOfPrice": false,
     *                 "gift": false,
     *                 "promoApplied": false,
     *                 "appliedPromo": null,
     *                 "customerOrderDeliveryDetId": 0
     *             }]
     *         }],
     *         "orderItems": [{
     *             "productSkuCode": "BENDER-ua",
     *             "quantity": 1,
     *             "price": 99.99,
     *             "salePrice": 99.99,
     *             "listPrice": 99.99,
     *             "netPrice": 83.32,
     *             "grossPrice": 99.99,
     *             "taxRate": 20.00,
     *             "taxCode": "VAT",
     *             "taxExclusiveOfPrice": false,
     *             "gift": false,
     *             "promoApplied": false,
     *             "appliedPromo": null,
     *             "customerOrderDetId": 0
     *         }]
     *     }
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object OrderPreviewRO</td><td>
     * <pre><code>
     * &lt;order-review success="true"&gt;
     * 	&lt;order cart-guid="e4d72c92-2f5f-4a4b-a5ca-97383c54152b" shop-code="SHOIP1" currency="EUR"
     * 	          customer-id="2" customer-order-id="2" multiple-shipment-option="false" order-status="os.none"
     * 	          order-timestamp="2015-04-07T10:34:27.809+01:00" ordernum="150407103427-2"
     * 	          pg-label="courierPaymentGatewayLabel" shop-id="10"&gt;
     * 		&lt;billing-address&gt;In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123&lt;/billing-address&gt;
     * 		&lt;deliveries&gt;
     * 			&lt;delivery carrier-id="1" carriersla-id="4" customer-order-delivery-id="2" delivery-group="D1"
     * 		            	 delivery-num="150407103427-2-0" delivery-status="ds.fullfillment"&gt;
     * 				&lt;carrier-name&gt;Test carrier 1&lt;/carrier-name&gt;
     * 				&lt;carriersla-name&gt;14&lt;/carriersla-name&gt;
     * 				&lt;delivery-items&gt;
     * 					&lt;delivery-item customer-order-delivery-det-id="0" gift="false" gross-price="99.99"
     * 				          	          list-price="99.99" net-price="83.32" price="99.99" product-sku-code="BENDER-ua"
     * 				          	          promo-applied="false" quantity="1" sale-price="99.99" tax-code="VAT"
     * 				          	          tax-exclusive-of-price="false" tax-rate="20.00"/&gt;
     * 				&lt;/delivery-items&gt;
     * 				&lt;total delivery-cost="10.00" delivery-cost-amount="10.00" delivery-list-cost="10.00"
     * 			    	      delivery-promo-applied="false" delivery-tax="1.67" list-sub-total="99.99" list-total-amount="109.99"
     * 			    	      non-sale-sub-total="99.99" promo-applied="false" price-sub-total="99.99" sale-sub-total="99.99"
     * 			    	      sub-total="99.99" sub-total-amount="99.99" sub-total-tax="16.67" total="109.99" total-amount="109.99"
     * 			    	      total-tax="18.34"/&gt;
     * 			&lt;/delivery&gt;
     * 		&lt;/deliveries&gt;
     * 		&lt;email&gt;bob.doe@-checkout-xml.com&lt;/email&gt;
     * 		&lt;firstname&gt;Bob&lt;/firstname&gt;
     * 		&lt;lastname&gt;Doe&lt;/lastname&gt;
     * 		&lt;order-items&gt;
     * 			&lt;order-item customer-order-det-id="0" gift="false" gross-price="99.99" list-price="99.99" net-price="83.32"
     * 		 	               price="99.99" product-sku-code="BENDER-ua" promo-applied="false" quantity="1" sale-price="99.99"
     * 		 	               tax-code="VAT" tax-exclusive-of-price="false" tax-rate="20.00"/&gt;
     * 		&lt;/order-items&gt;
     * 		&lt;order-message&gt;My Message&lt;/order-message&gt;
     * 		&lt;shipping-address&gt;In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123&lt;/shipping-address&gt;
     * 		&lt;total delivery-cost="10.00" delivery-cost-amount="10.00" delivery-list-cost="10.00" delivery-promo-applied="false"
     * 		          delivery-tax="1.67" list-sub-total="99.99" list-total-amount="109.99" non-sale-sub-total="99.99"
     * 		          promo-applied="false" price-sub-total="99.99" sale-sub-total="99.99" sub-total="99.99" sub-total-amount="99.99"
     * 		          sub-total-tax="16.67" total="109.99" total-amount="109.99" total-tax="18.34"/&gt;
     * 	&lt;/order&gt;
     * 	&lt;pg-external-form&gt;false&lt;/pg-external-form&gt;
     * 	&lt;pg-form-html&gt;&lt;!-- escaped form --&gt;&lt;form method="POST" action="/order/payment"&gt;
     *
     * &lt;input type="submit" value="submit"&gt;&lt;/form&gt;&lt;/pg-form-html&gt;
     * 	&lt;pg-form-url&gt;/order/payment&lt;/pg-form-url&gt;
     * 	&lt;pg-name&gt;courierPaymentGateway&lt;/pg-name&gt;
     * 	&lt;pg-online&gt;false&lt;/pg-online&gt;
     * &lt;/order-review&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * <p>
     * <h3>Error codes</h3><p>
     * <table border="1">
     *     <tr><td>CART_ITEMS</td><td>MISSING if no items present in the cart</td></tr>
     *     <tr><td>BILLING_ADDRESS</td><td>MISSING if billing address is required and it is missing</td></tr>
     *     <tr><td>DELIVERY_ADDRESS</td><td>MISSING if delivery address is required and it is missing</td></tr>
     *     <tr><td>SHIPPING_METHOD</td><td>MISSING if carrier SLA is not set</td></tr>
     *     <tr><td>PAYMENT_METHOD</td><td>MISSING if payment gateway is not set</td></tr>
     *     <tr><td>ERROR_COUPON</td><td>coupon code, if coupon is not valid</td></tr>
     *     <tr><td>ERROR_SKU</td><td>sku code, if sku is unavailable</td></tr>
     *     <tr><td>CART_ITEMS</td><td>error message, if order cannot be created from cart items</td></tr>
     * </table>
     * <p>
     *
     * @param request request
     * @param response response
     *
     * @return order preview object
     */
    @RequestMapping(
            value = "/order/preview",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody OrderPreviewRO orderPreview(@RequestBody final OrderDeliveryOptionRO option,
                                                     final HttpServletRequest request,
                                                     final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();

        final ShoppingCart cart = cartMixin.getCurrentCart();
        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_MULTIPLEDELIVERY, cart,
                (Map) Collections.singletonMap(ShoppingCartCommand.CMD_MULTIPLEDELIVERY, String.valueOf(option.isForceSingleDelivery())));
        cartMixin.persistShoppingCart(request, response);

        if (cart.getCartItemsCount() == 0) {

            final OrderPreviewRO review = new OrderPreviewRO();
            review.setSuccess(false);
            final Map<String, String> problems = new HashMap<String, String>();
            problems.put("CART_ITEMS", "MISSING");
            review.setProblems(problems);
            return review;

        }

        if ((!cart.isBillingAddressNotRequired() || !cart.isDeliveryAddressNotRequired())
                && !addressBookFacade.customerHasAtLeastOneAddress(cart.getCustomerEmail())) {
            // Must have an address if it is required
            final OrderPreviewRO review = new OrderPreviewRO();
            review.setSuccess(false);
            final Map<String, String> problems = new HashMap<String, String>();
            if (!cart.isBillingAddressNotRequired()) {
                problems.put("BILLING_ADDRESS", "MISSING");
            }
            if (!cart.isDeliveryAddressNotRequired()) {
                problems.put("DELIVERY_ADDRESS", "MISSING");
            }
            review.setProblems(problems);
            return review;
        }

        if (cart.getCarrierSlaId() == null) {
            // Must select a carrier
            final OrderPreviewRO review = new OrderPreviewRO();
            review.setSuccess(false);
            final Map<String, String> problems = new HashMap<String, String>();
            problems.put("SHIPPING_METHOD", "MISSING");
            review.setProblems(problems);
            return review;
        }

        if (StringUtils.isBlank(cart.getOrderInfo().getPaymentGatewayLabel())) {
            // Must select a PG
            final OrderPreviewRO review = new OrderPreviewRO();
            review.setSuccess(false);
            final Map<String, String> problems = new HashMap<String, String>();
            problems.put("PAYMENT_METHOD", "MISSING");
            review.setProblems(problems);
            return review;
        }

        try {
            final CustomerOrder order = checkoutServiceFacade.createFromCart(cart);
            order.setPgLabel(cart.getOrderInfo().getPaymentGatewayLabel());
            checkoutServiceFacade.update(order);

            final Total total = checkoutServiceFacade.getOrderTotal(order);
            final OrderRO ro = mappingMixin.map(order, OrderRO.class, CustomerOrder.class);
            ro.setTotal(mappingMixin.map(total, CartTotalRO.class, Total.class));

            for (final CustomerOrderDelivery delivery : order.getDelivery()) {
                final DeliveryRO dro = mappingMixin.map(delivery, DeliveryRO.class, CustomerOrderDelivery.class);
                final Total dtotal = checkoutServiceFacade.getOrderDeliveryTotal(order, delivery);
                dro.setTotal(mappingMixin.map(dtotal, CartTotalRO.class, Total.class));
                ro.getDeliveries().add(dro);
            }

            final PaymentGateway gateway = checkoutServiceFacade.getOrderPaymentGateway(order);
            final Payment payment = checkoutServiceFacade.createPaymentToAuthorize(order);

            boolean isExternalFormPg = false;

            String submitBtnValue = null;
            if (gateway instanceof PaymentGatewayExternalForm) {
                submitBtnValue = ((PaymentGatewayExternalForm) gateway).getSubmitButton();
            }
            if (StringUtils.isBlank(submitBtnValue)) {
                submitBtnValue = "<input type=\"submit\" value=\"submit\">";
            }

            String postActionUrl = null;
            if (gateway instanceof PaymentGatewayExternalForm) {
                // some pgs will point to local mounted page (e.g. paypal express points to paymentpaypalexpress)
                // that triggers internal payment information processing via filter
                postActionUrl = ((PaymentGatewayExternalForm) gateway).getPostActionUrl();
            }
            if (StringUtils.isBlank(postActionUrl)) {
                // Default behaviour for internal payment processing
                postActionUrl = request.getRequestURI().replace("/order/preview", "/order/place");
            } else if (!postActionUrl.startsWith("http")) {
                // special behaviour handled by specific PG filters
                postActionUrl = request.getRequestURI().replace("/order/preview", "/order/place/").concat(postActionUrl);
            } else {
                // else this is an external url
                isExternalFormPg = true;
            }

            String fullName = StringUtils.EMPTY;

            if (order.getCustomer() != null) {

                fullName = (order.getCustomer().getFirstname()
                        + " "
                        + order.getCustomer().getLastname()).toUpperCase();

            }

            final BigDecimal grandTotal = total.getTotalAmount();

            final String htmlFragment = gateway.getHtmlForm(
                    fullName,
                    cart.getCurrentLocale(),
                    grandTotal,
                    cart.getCurrencyCode(),
                    cart.getGuid(),
                    payment);

            final String fullFormHtml = MessageFormat.format(
                    "<form method=\"POST\" action=\"{0}\">\n{1}\n{2}</form>",
                    postActionUrl,
                    htmlFragment,
                    submitBtnValue
            );

            final OrderPreviewRO review = new OrderPreviewRO();
            review.setSuccess(true);

            review.setPgName(gateway.getName(cart.getCurrentLocale()));
            review.setPgOnline(gateway.getPaymentGatewayFeatures().isOnlineGateway());
            review.setPgExternalForm(isExternalFormPg);
            review.setPgFormUrl(postActionUrl);
            review.setPgFormHtml(fullFormHtml);

            review.setOrder(ro);

            return review;

        } catch (CouponCodeInvalidException invalidCoupon) {

            ShopCodeContext.getLog(this).error(invalidCoupon.getMessage(), invalidCoupon);

            final OrderPreviewRO review = new OrderPreviewRO();
            review.setSuccess(false);
            final Map<String, String> problems = new HashMap<String, String>();
            problems.put("ERROR_COUPON", invalidCoupon.getCoupon());
            review.setProblems(problems);
            return review;

        } catch (SkuUnavailableException skuUnavailable) {

            ShopCodeContext.getLog(this).error(skuUnavailable.getMessage(), skuUnavailable);

            final OrderPreviewRO review = new OrderPreviewRO();
            review.setSuccess(false);
            final Map<String, String> problems = new HashMap<String, String>();
            problems.put("ERROR_SKU", skuUnavailable.getSkuCode());
            review.setProblems(problems);
            return review;

        } catch (OrderAssemblyException assembly) {

            ShopCodeContext.getLog(this).error(assembly.getMessage(), assembly);

            final OrderPreviewRO review = new OrderPreviewRO();
            review.setSuccess(false);
            final Map<String, String> problems = new HashMap<String, String>();
            problems.put("CART_ITEMS", assembly.getMessage());
            review.setProblems(problems);
            return review;

        }

    }


    /**
     * Interface: POST /order/place
     * <p>
     * <p>
     * Submit payment for internal payment processor and place order.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * Parameters posted from card form from PUT /order/preview
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object OrderPlacedRO</td><td>
     * <pre><code>
     * {
     *     "success": true,
     *     "problems": null,
     *     "order": {
     *         "customerorderId": 1,
     *         "ordernum": "150407155547-1",
     *         "pgLabel": "courierPaymentGatewayLabel",
     *         "billingAddress": "In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123",
     *         "shippingAddress": "In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123",
     *         "cartGuid": "94bde080-612f-41b9-b566-d2bd1269559e",
     *         "currency": "EUR",
     *         "orderMessage": "My Message",
     *         "orderStatus": "os.waiting",
     *         "multipleShipmentOption": false,
     *         "orderTimestamp": 1428418547773,
     *         "email": "bob.doe@yc-checkout-json.com",
     *         "firstname": "Bob",
     *         "lastname": "Doe",
     *         "middlename": null,
     *         "customerId": 1,
     *         "shopId": 10,
     *         "code": "SHOIP1",
     *         "total": {
     *             "listSubTotal": 99.99,
     *             "saleSubTotal": 99.99,
     *             "nonSaleSubTotal": 99.99,
     *             "priceSubTotal": 99.99,
     *             "orderPromoApplied": false,
     *             "appliedOrderPromo": null,
     *             "subTotal": 99.99,
     *             "subTotalTax": 16.67,
     *             "subTotalAmount": 99.99,
     *             "deliveryListCost": 10.00,
     *             "deliveryCost": 10.00,
     *             "deliveryPromoApplied": false,
     *             "appliedDeliveryPromo": null,
     *             "deliveryTax": 1.67,
     *             "deliveryCostAmount": 10.00,
     *             "total": 109.99,
     *             "totalTax": 18.34,
     *             "listTotalAmount": 109.99,
     *             "totalAmount": 109.99
     *         },
     *         "deliveries": [{
     *             "customerOrderDeliveryId": 1,
     *             "deliveryNum": "150407155547-1-0",
     *             "refNo": null,
     *             "carrierSlaId": 4,
     *             "carrierSlaName": "14",
     *             "carrierSlaDisplayNames": null,
     *             "carrierId": 1,
     *             "carrierName": "Test carrier 1",
     *             "carrierDisplayNames": null,
     *             "deliveryStatus": "ds.inventory.reserved",
     *             "deliveryGroup": "D1",
     *             "total": {
     *                 "listSubTotal": 99.99,
     *                 "saleSubTotal": 99.99,
     *                 "nonSaleSubTotal": 99.99,
     *                 "priceSubTotal": 99.99,
     *                 "orderPromoApplied": false,
     *                 "appliedOrderPromo": null,
     *                 "subTotal": 99.99,
     *                 "subTotalTax": 16.67,
     *                 "subTotalAmount": 99.99,
     *                 "deliveryListCost": 10.00,
     *                 "deliveryCost": 10.00,
     *                 "deliveryPromoApplied": false,
     *                 "appliedDeliveryPromo": null,
     *                 "deliveryTax": 1.67,
     *                 "deliveryCostAmount": 10.00,
     *                 "total": 109.99,
     *                 "totalTax": 18.34,
     *                 "listTotalAmount": 109.99,
     *                 "totalAmount": 109.99
     *             },
     *             "deliveryItems": [{
     *                 "productSkuCode": "BENDER-ua",
     *                 "quantity": 1.00,
     *                 "price": 99.99,
     *                 "salePrice": 99.99,
     *                 "listPrice": 99.99,
     *                 "netPrice": 83.32,
     *                 "grossPrice": 99.99,
     *                 "taxRate": 20.00,
     *                 "taxCode": "VAT",
     *                 "taxExclusiveOfPrice": false,
     *                 "gift": false,
     *                 "promoApplied": false,
     *                 "appliedPromo": null,
     *                 "customerOrderDeliveryDetId": 0
     *             }]
     *         }],
     *         "orderItems": [{
     *             "productSkuCode": "BENDER-ua",
     *             "quantity": 1.00,
     *             "price": 99.99,
     *             "salePrice": 99.99,
     *             "listPrice": 99.99,
     *             "netPrice": 83.32,
     *             "grossPrice": 99.99,
     *             "taxRate": 20.00,
     *             "taxCode": "VAT",
     *             "taxExclusiveOfPrice": false,
     *             "gift": false,
     *             "promoApplied": false,
     *             "appliedPromo": null,
     *             "customerOrderDetId": 0
     *         }]
     *     }
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object OrderPlacedRO</td><td>
     * <pre><code>
     * &lt;order-placed success="true"&gt;
     * 	&lt;order cart-guid="54880429-628f-45fb-bc9b-6fb04667cded" shop-code="SHOIP1" currency="EUR" customer-id="2"
     * 	          customer-order-id="2" multiple-shipment-option="false" order-status="os.waiting" order-timestamp="2015-04-07T15:55:50.540+01:00"
     * 	          ordernum="150407155550-2" pg-label="courierPaymentGatewayLabel" shop-id="10"&gt;
     * 		&lt;billing-address&gt;In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123&lt;/billing-address&gt;
     * 		&lt;deliveries&gt;
     * 			&lt;delivery carrier-id="1" carriersla-id="4" customer-order-delivery-id="2" delivery-group="D1"
     * 		  	             delivery-num="150407155550-2-0" delivery-status="ds.inventory.reserved"&gt;
     * 				&lt;carrier-name&gt;Test carrier 1&lt;/carrier-name&gt;
     * 				&lt;carriersla-name&gt;14&lt;/carriersla-name&gt;
     * 				&lt;delivery-items&gt;
     * 					&lt;delivery-item customer-order-delivery-det-id="0" gift="false" gross-price="99.99" list-price="99.99"
     * 				         	          net-price="83.32" price="99.99" product-sku-code="BENDER-ua" promo-applied="false"
     * 				         	          quantity="1.00" sale-price="99.99" tax-code="VAT" tax-exclusive-of-price="false"
     * 				         	          tax-rate="20.00"/&gt;
     * 				&lt;/delivery-items&gt;
     * 				&lt;total delivery-cost="10.00" delivery-cost-amount="10.00" delivery-list-cost="10.00"
     * 				          delivery-promo-applied="false" delivery-tax="1.67" list-sub-total="99.99" list-total-amount="109.99"
     * 				          non-sale-sub-total="99.99" promo-applied="false" price-sub-total="99.99" sale-sub-total="99.99"
     * 				          sub-total="99.99" sub-total-amount="99.99" sub-total-tax="16.67" total="109.99" total-amount="109.99"
     * 				          total-tax="18.34"/&gt;
     * 			&lt;/delivery&gt;
     * 		&lt;/deliveries&gt;
     * 		&lt;email&gt;bob.doe@-checkout-xml.com&lt;/email&gt;
     * 		&lt;firstname&gt;Bob&lt;/firstname&gt;
     * 		&lt;lastname&gt;Doe&lt;/lastname&gt;
     * 		&lt;order-items&gt;
     * 			&lt;order-item customer-order-det-id="0" gift="false" gross-price="99.99" list-price="99.99" net-price="83.32"
     * 			               price="99.99" product-sku-code="BENDER-ua" promo-applied="false" quantity="1.00" sale-price="99.99"
     * 			               tax-code="VAT" tax-exclusive-of-price="false" tax-rate="20.00"/&gt;
     * 		&lt;/order-items&gt;
     * 		&lt;order-message&gt;My Message&lt;/order-message&gt;
     * 		&lt;shipping-address&gt;In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123&lt;/shipping-address&gt;
     * 		&lt;total delivery-cost="10.00" delivery-cost-amount="10.00" delivery-list-cost="10.00" delivery-promo-applied="false"
     * 	 	          delivery-tax="1.67" list-sub-total="99.99" list-total-amount="109.99" non-sale-sub-total="99.99"
     * 	 	          promo-applied="false" price-sub-total="99.99" sale-sub-total="99.99" sub-total="99.99"
     * 	 	          sub-total-amount="99.99" sub-total-tax="16.67" total="109.99" total-amount="109.99" total-tax="18.34"/&gt;
     * 	&lt;/order&gt;
     * &lt;/order-placed&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <h3>Error codes</h3><p>
     * <table border="1">
     *     <tr><td>CART_ITEMS</td><td>MISSING if no items present in the cart</td></tr>
     *     <tr><td>ORDER</td><td>MISSING if order has not been previewed</td></tr>
     *     <tr><td>PAYMENT_METHOD</td><td>PAYMENT_FAILED if payment failed</td></tr>
     *     <tr><td>CART_ITEM_OUT_OF_STOCK</td><td>sku code, if SKU is out of stock</td></tr>
     *     <tr><td>ORDER</td><td>error message, for any other reason</td></tr>
     * </table>
     * <p>
     *
     * @param request request
     * @param response response
     *
     * @return order placed object
     */
    @RequestMapping(
            value = "/order/place",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody OrderPlacedRO orderPlace(final HttpServletRequest request,
                                                  final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();

        final Map param = request.getParameterMap();
        final Map mparam = new HashMap();
        mparam.putAll(param);
        mparam.put(PaymentMiscParam.CLIENT_IP, ApplicationDirector.getShopperIPAddress());

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final String guid = cart.getGuid();
        final OrderPlacedRO placed = new OrderPlacedRO();

        try {

            if (cart.getCartItemsCount() > 0) {

                final CustomerOrder order = checkoutServiceFacade.findByGuid(guid);

                if (order == null) {

                    placed.setSuccess(false);
                    final Map<String, String> problems = new HashMap<String, String>();
                    problems.put("ORDER", "MISSING");
                    placed.setProblems(problems);

                } else {

                    boolean result = paymentProcessFacade.pay(cart, mparam);
                    if (result) {

                        final Total total = checkoutServiceFacade.getOrderTotal(order);
                        final OrderRO ro = mappingMixin.map(order, OrderRO.class, CustomerOrder.class);
                        ro.setTotal(mappingMixin.map(total, CartTotalRO.class, Total.class));

                        for (final CustomerOrderDelivery delivery : order.getDelivery()) {
                            final DeliveryRO dro = mappingMixin.map(delivery, DeliveryRO.class, CustomerOrderDelivery.class);
                            final Total dtotal = checkoutServiceFacade.getOrderDeliveryTotal(order, delivery);
                            dro.setTotal(mappingMixin.map(dtotal, CartTotalRO.class, Total.class));
                            ro.getDeliveries().add(dro);
                        }


                        shoppingCartCommandFactory.execute(
                                ShoppingCartCommand.CMD_CLEAN, ApplicationDirector.getShoppingCart(),
                                Collections.singletonMap(
                                        ShoppingCartCommand.CMD_CLEAN,
                                        null)
                        );

                        placed.setOrder(ro);
                        placed.setSuccess(true);

                    } else {

                        placed.setSuccess(false);
                        final Map<String, String> problems = new HashMap<String, String>();
                        problems.put("PAYMENT_METHOD", "PAYMENT_FAILED");
                        placed.setProblems(problems);

                    }
                }
            } else {

                placed.setSuccess(false);
                final Map<String, String> problems = new HashMap<String, String>();
                problems.put("CART_ITEMS", "MISSING");
                placed.setProblems(problems);

            }


        } catch (OrderItemAllocationException e) {

            placed.setSuccess(false);
            final Map<String, String> problems = new HashMap<String, String>();
            problems.put("CART_ITEM_OUT_OF_STOCK", e.getProductSkuCode());
            placed.setProblems(problems);

        } catch (OrderException e) {

            placed.setSuccess(false);
            final Map<String, String> problems = new HashMap<String, String>();
            problems.put("ORDER", e.getMessage());
            placed.setProblems(problems);

        }

        cartMixin.persistShoppingCart(request, response);
        return placed;
    }






}
