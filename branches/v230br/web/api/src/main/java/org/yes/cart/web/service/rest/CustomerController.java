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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.ro.*;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.service.rest.impl.AddressSupportMixin;
import org.yes.cart.web.service.rest.impl.CartMixin;
import org.yes.cart.web.service.rest.impl.RoMappingMixin;
import org.yes.cart.web.support.service.AddressBookFacade;
import org.yes.cart.web.support.service.CurrencySymbolService;
import org.yes.cart.web.support.service.CustomerServiceFacade;
import org.yes.cart.web.support.service.ProductServiceFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 30/03/2015
 * Time: 07:52
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerServiceFacade customerServiceFacade;

    @Autowired
    private AddressBookFacade addressBookFacade;

    @Autowired
    private ProductServiceFacade productServiceFacade;

    @Autowired
    private CurrencySymbolService currencySymbolService;


    @Autowired
    private CartMixin cartMixin;
    @Autowired
    @Qualifier("restRoMappingMixin")
    private RoMappingMixin mappingMixin;
    @Autowired
    private AddressSupportMixin addressSupportMixin;


    /**
     * Interface: GET /yes-api/rest/customer/summary
     * <p>
     * <p>
     * Display customer profile information.
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
     *     <tr><td>JSON object CustomerRO</td><td>
     * <pre><code>
     * {
     *     "customerId": 1,
     *     "email": "bob.doe@yc-json.com",
     *     "firstname": "Bob",
     *     "lastname": "Doe",
     *     "middlename": null,
     *     "tag": null,
     *     "attributes": [{
     *         "attrvalueId": 1,
     *         "val": "123123123",
     *         "displayVals": null,
     *         "attributeId": 1030,
     *         "attributeName": "Customer phone",
     *         "attributeDisplayNames": null,
     *         "customerId": 1
     *     }]
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object CustomerRO</td><td>
     * <pre><code>
     * &lt;customer&gt;
     * 	&lt;attribute-values&gt;
     * 		&lt;attribute-value attribute-id="1030" attrvalue-id="2" customer-id="2"&gt;
     * 			&lt;attribute-name&gt;Customer phone&lt;/attribute-name&gt;
     * 			&lt;val&gt;123123123&lt;/val&gt;
     * 		&lt;/attribute-value&gt;
     * 	&lt;/attribute-values&gt;
     * 	&lt;customerId&gt;2&lt;/customerId&gt;
     * 	&lt;email&gt;bob.doe@yc-xml.com&lt;/email&gt;
     * 	&lt;firstname&gt;Bob&lt;/firstname&gt;
     * 	&lt;lastname&gt;Doe&lt;/lastname&gt;
     * &lt;/customer&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return customer object
     */
    @RequestMapping(
            value = "/summary",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CustomerRO viewSummary(final HttpServletRequest request,
                                                final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        final Shop shop = cartMixin.getCurrentShop();
        final ShoppingCart cart = cartMixin.getCurrentCart();

        final Customer customer = customerServiceFacade.getCustomerByEmail(cart.getCustomerEmail());

        final CustomerRO ro = mappingMixin.map(customer, CustomerRO.class, Customer.class);

        // Only map allowed attributes
        ro.setAttributes(mappingMixin.map((List) customerServiceFacade.getCustomerRegistrationAttributes(shop, customer), AttrValueCustomerRO.class, AttrValueCustomer.class));

        return ro;

    }


    private List<AddressRO> viewAddressbookInternal(final String type) {

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final Shop shop = cartMixin.getCurrentShop();

        return addressSupportMixin.viewAddressOptions(cart, shop, type);

    }


    /**
     * Interface: GET /yes-api/rest/customer/addressbook/{type}
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
            value = "/addressbook/{type}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<AddressRO> viewAddressbook(@PathVariable(value = "type") final String type,
                                                         final HttpServletRequest request,
                                                         final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return viewAddressbookInternal(type);

    }


    /**
     * Interface: GET /yes-api/rest/customer/addressbook/{type}
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
            value = "/addressbook/{type}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody AddressListRO viewAddressbookXML(@PathVariable(value = "type") final String type,
                                                          final HttpServletRequest request,
                                                          final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new AddressListRO(viewAddressbookInternal(type));

    }


    private List<CountryRO> viewAddressbookCountriesInternal(final String type) {

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final Shop shop = cartMixin.getCurrentShop();

        return addressSupportMixin.viewAddressCountryOptions(cart, shop, type);

    }



    /**
     * Interface: GET /yes-api/rest/customer/addressbook/{type}/options/countries
     * <p>
     * <p>
     * Display country options for address book.
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
     *     <tr><td>JSON array object CountryRO</td><td>
     * <pre><code>
     * [{
     *     "countryId": 218,
     *     "countryCode": "UA",
     *     "isoCode": "804",
     *     "name": "Ukraine",
     *     "displayName": "Україна"
     * }, {
     *     "countryId": 220,
     *     "countryCode": "GB",
     *     "isoCode": "826",
     *     "name": "United Kingdom",
     *     "displayName": null
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param request request
     * @param response response
     *
     * @return list of countries
     */
    @RequestMapping(
            value = "/addressbook/{type}/options/countries",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<CountryRO> viewAddressbookCountries(@PathVariable(value = "type") final String type,
                                                                  final HttpServletRequest request,
                                                                  final HttpServletResponse response) {
        cartMixin.persistShoppingCart(request, response);
        return viewAddressbookCountriesInternal(type);

    }



    /**
     * Interface: GET /yes-api/rest/customer/addressbook/{type}/options/countries
     * <p>
     * <p>
     * Display country options for address book.
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
     *     <tr><td>XML array object CountryRO</td><td>
     * <pre><code>
     * &lt;countries&gt;
     * 	&lt;country country-code="UA" country-id="218" country-iso="804"&gt;
     * 		&lt;name&gt;Ukraine&lt;/name&gt;
     * 		&lt;display-name&gt;Україна&lt;/display-name&gt;
     * 	&lt;/country&gt;
     * 	&lt;country country-code="GB" country-id="220" country-iso="826"&gt;
     * 		&lt;name&gt;United Kingdom&lt;/name&gt;
     * 	&lt;/country&gt;
     * &lt;/countries&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param request request
     * @param response response
     *
     * @return list of countries
     */
    @RequestMapping(
            value = "/addressbook/{type}/options/countries",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CountryListRO viewAddressbookCountriesXML(@PathVariable(value = "type") final String type,
                                                                   final HttpServletRequest request,
                                                                   final HttpServletResponse response) {
        cartMixin.persistShoppingCart(request, response);
        return new CountryListRO(viewAddressbookCountriesInternal(type));

    }



    private List<StateRO> viewAddressbookCountryStatesInternal(final String country) {

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final Shop shop = cartMixin.getCurrentShop();

        return addressSupportMixin.viewAddressCountryStateOptions(cart, shop, country);

    }




    /**
     * Interface: GET /yes-api/rest/customer/addressbook/{type}/options/country/{code}
     * <p>
     * <p>
     * Display country options for address book.
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
     *     <tr><td>code</td><td>country code {@link Country}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array object StateRO</td><td>
     * <pre><code>
     * [{
     *     "stateId": 218,
     *     "countryCode": "UA",
     *     "stateCode": "UA-UA",
     *     "name": "Kiev province",
     *     "displayName": "Київська область"
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param code two letter country code (see {@link Country} )
     * @param request request
     * @param response response
     *
     * @return list of country states
     */
    @RequestMapping(
            value = "/addressbook/{type}/options/country/{code}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<StateRO> viewAddressbookCountries(@PathVariable(value = "type") final String type,
                                                                @PathVariable(value = "code") final String code,
                                                                final HttpServletRequest request,
                                                                final HttpServletResponse response) {
        cartMixin.persistShoppingCart(request, response);
        return viewAddressbookCountryStatesInternal(code);

    }


    /**
     * Interface: GET /yes-api/rest/customer/addressbook/{type}/options/country/{code}
     * <p>
     * <p>
     * Display country options for address book.
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
     *     <tr><td>code</td><td>country code {@link Country}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array object StateRO</td><td>
     * <pre><code>
     * &lt;states&gt;
     * 	&lt;state country-code="UA" state-code="UA-UA" state-id="218"&gt;
     * 		&lt;name&gt;Kiev province&lt;/name&gt;
     * 		&lt;display-name&gt;Київська область&lt;/display-name&gt;
     * 	&lt;/state&gt;
     * &lt;/states&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param code two letter country code (see {@link Country} )
     * @param request request
     * @param response response
     *
     * @return list of country states
     */
    @RequestMapping(
            value = "/addressbook/{type}/options/country/{code}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody StateListRO viewAddressbookCountriesXML(@PathVariable(value = "type") final String type,
                                                                 @PathVariable(value = "code") final String code,
                                                                 final HttpServletRequest request,
                                                                 final HttpServletResponse response) {
        cartMixin.persistShoppingCart(request, response);
        return new StateListRO(viewAddressbookCountryStatesInternal(code));

    }



    private List<AddressRO> updateAddressbookInternal(final String type, final AddressRO address) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final Customer customer = customerServiceFacade.getCustomerByEmail(cart.getCustomerEmail());

        final Address addressEntity = addressBookFacade.getAddress(customer, String.valueOf(address.getAddressId()), type);

        if (StringUtils.isNotBlank(address.getFirstname())) {
            addressEntity.setFirstname(address.getFirstname());
        }
        if (StringUtils.isNotBlank(address.getMiddlename())) {
            addressEntity.setMiddlename(address.getMiddlename());
        }
        if (StringUtils.isNotBlank(address.getLastname())) {
            addressEntity.setLastname(address.getLastname());
        }

        addressEntity.setAddrline1(address.getAddrline1());
        addressEntity.setAddrline2(address.getAddrline2());

        addressEntity.setCity(address.getCity());
        addressEntity.setPostcode(address.getPostcode());
        addressEntity.setStateCode(address.getStateCode());
        addressEntity.setCountryCode(address.getCountryCode());

        if (StringUtils.isNotBlank(address.getPhoneList())) {
            addressEntity.setPhoneList(address.getPhoneList());
        }

        addressBookFacade.createOrUpdate(addressEntity);

        return viewAddressbookInternal(type);
    }


    /**
     * Interface: PUT /yes-api/rest/customer/addressbook/{type}
     * <p>
     * <p>
     * Display customer address book.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/json or application/xml</td></tr>
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     *     <tr><td>JSON body AddressRO</td><td>
     * <pre><code>
     * {
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
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML body AddressRO</td><td>
     * <pre><code>
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
     * </code></pre>
     *     </td></tr>
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
            value = "/addressbook/{type}",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody List<AddressRO> updateAddressbook(@PathVariable(value = "type") final String type,
                                                           @RequestBody final AddressRO address,
                                                           final HttpServletRequest request,
                                                           final HttpServletResponse response) {
        cartMixin.persistShoppingCart(request, response);
        return updateAddressbookInternal(type, address);

    }

    /**
     * Interface: PUT /yes-api/rest/customer/addressbook/{type}
     * <p>
     * <p>
     * Display customer address book.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/json or application/xml</td></tr>
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     *     <tr><td>JSON body AddressRO</td><td>
     * <pre><code>
     * {
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
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML body AddressRO</td><td>
     * <pre><code>
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
     * </code></pre>
     *     </td></tr>
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
            value = "/addressbook/{type}",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_XML_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody AddressListRO updateAddressbookXML(@PathVariable(value = "type") final String type,
                                                            @RequestBody final AddressRO address,
                                                            final HttpServletRequest request,
                                                            final HttpServletResponse response) {
        cartMixin.persistShoppingCart(request, response);
        return new AddressListRO(updateAddressbookInternal(type, address));

    }


    private List<ProductWishlistRO> viewWishlistInternal(final String type, final String tag) {


        final ShoppingCart cart = cartMixin.getCurrentCart();
        final long shopId = cartMixin.getCurrentShopId();

        final List<CustomerWishList> wishList = customerServiceFacade.getCustomerWishListByEmail(
                cart.getCustomerEmail(),
                type,
                tag != null ? new String[] { tag } : null);

        if (CollectionUtils.isNotEmpty(wishList)) {

            final List<String> productIds = new ArrayList<String>();

            for (final CustomerWishList item : wishList) {

                productIds.add(String.valueOf(item.getSkus().getProduct().getProductId()));

            }

            final List<ProductSearchResultDTO> uniqueProducts = productServiceFacade.getListProducts(
                    productIds, -1L, ShopCodeContext.getShopId());

            final List<ProductWishlistRO> wlRo = new ArrayList<ProductWishlistRO>();

            for (final CustomerWishList item : wishList) {

                final Pair<String, Boolean> symbol = currencySymbolService.getCurrencySymbol(cart.getCurrencyCode());

                for (final ProductSearchResultDTO uniqueProduct : uniqueProducts) {

                    if (uniqueProduct.getId() == item.getSkus().getProduct().getProductId()) {
                        final ProductWishlistRO wl = mappingMixin.map(uniqueProduct, ProductWishlistRO.class, ProductSearchResultDTO.class);
                        wl.setDefaultSkuCode(item.getSkus().getCode());
                        wl.setQuantity(item.getQuantity());

                        final ProductAvailabilityModel skuPam = productServiceFacade.getProductAvailability(uniqueProduct, shopId);
                        final ProductAvailabilityModelRO amRo = mappingMixin.map(skuPam, ProductAvailabilityModelRO.class, ProductAvailabilityModel.class);
                        wl.setProductAvailabilityModel(amRo);

                        final SkuPrice price = productServiceFacade.getSkuPrice(
                                null,
                                skuPam.getFirstAvailableSkuCode(),
                                BigDecimal.ONE,
                                cart.getCurrencyCode(),
                                shopId
                        );

                        final SkuPriceRO priceRo = mappingMixin.map(price, SkuPriceRO.class, SkuPrice.class);
                        priceRo.setSymbol(symbol.getFirst());
                        priceRo.setSymbolPosition(symbol.getSecond() != null && symbol.getSecond() ? "after" : "before");

                        wl.setPrice(priceRo);

                        final SkuPriceRO wlPrice = new SkuPriceRO();
                        wlPrice.setQuantity(BigDecimal.ONE);
                        wlPrice.setCurrency(item.getRegularPriceCurrencyWhenAdded());
                        wlPrice.setRegularPrice(item.getRegularPriceWhenAdded());
                        wl.setPriceWhenAdded(wlPrice);
                        final Pair<String, Boolean> wlSymbol = currencySymbolService.getCurrencySymbol(wlPrice.getCurrency());
                        wlPrice.setSymbol(wlSymbol.getFirst());
                        wlPrice.setSymbolPosition(wlSymbol.getSecond() != null && wlSymbol.getSecond() ? "after" : "before");

                        wlRo.add(wl);
                    }

                }

            }

            return wlRo;

        }

        return Collections.emptyList();

    }



    /**
     * Interface: GET /yes-api/rest/customer/wishlist/{type}
     * <p>
     * <p>
     * Display customer default wishlist.
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
     *     <tr><td>type</td><td>type of address book {@link CustomerWishList}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array object ProductWishlistRO</td><td>
     * <pre><code>
     * [{
     *     "id": 9998,
     *     "code": "BENDER-ua",
     *     "manufacturerCode": null,
     *     "multisku": false,
     *     "defaultSkuCode": "BENDER-ua",
     *     "name": "...",
     *     "displayName": {
     *         "ua": "...",
     *         "en": "Bender Bending Rodriguez",
     *         "ru": "..."
     *     },
     *     "description": "...",
     *     "displayDescription": null,
     *     "availablefrom": 1270721717451,
     *     "availableto": 2217492917451,
     *     "availability": 1,
     *     "productAvailabilityModel": {
     *         "available": true,
     *         "inStock": true,
     *         "perpetual": false,
     *         "defaultSkuCode": "BENDER-ua",
     *         "firstAvailableSkuCode": "BENDER-ua",
     *         "skuCodes": ["BENDER-ua"],
     *         "availableToSellQuantity": {
     *             "BENDER-ua": 1.000
     *         }
     *     },
     *     "price": {
     *         "currency": "EUR",
     *         "symbol": "€",
     *         "symbolPosition": "before",
     *         "quantity": 1,
     *         "regularPrice": 0.00,
     *         "salePrice": null,
     *         "discount": null
     *     },
     *     "defaultImage": "noimage.jpeg",
     *     "featured": false,
     *     "minOrderQuantity": 1.00,
     *     "maxOrderQuantity": 100000.00,
     *     "stepOrderQuantity": 1.00,
     *     "skus": null,
     *     "priceWhenAdded": {
     *         "currency": "EUR",
     *         "symbol": "€",
     *         "symbolPosition": "before",
     *         "quantity": 1,
     *         "regularPrice": 0.00,
     *         "salePrice": null,
     *         "discount": null
     *     },
     *     "quantity": 10.00
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link CustomerWishList}) )
     * @param request request
     * @param response response
     *
     * @return list of wish list items
     */
    @RequestMapping(
            value = "/wishlist/{type}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<ProductWishlistRO> viewWishlist(@PathVariable(value = "type") final String type,
                                                              final HttpServletRequest request,
                                                              final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return viewWishlistInternal(type, null);

    }


    /**
     * Interface: GET /yes-api/rest/customer/wishlist/{type}/{tag}
     * <p>
     * <p>
     * Display customer wishlist by tag.
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
     *     <tr><td>type</td><td>type of address book {@link CustomerWishList}</td></tr>
     *     <tr><td>tag</td><td>wishlist tag {@link CustomerWishList}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array object ProductWishlistRO</td><td>
     * <pre><code>
     * [{
     *     "id": 9998,
     *     "code": "BENDER-ua",
     *     "manufacturerCode": null,
     *     "multisku": false,
     *     "defaultSkuCode": "BENDER-ua",
     *     "name": "...",
     *     "displayName": {
     *         "ua": "...",
     *         "en": "Bender Bending Rodriguez",
     *         "ru": "..."
     *     },
     *     "description": "...",
     *     "displayDescription": null,
     *     "availablefrom": 1270721717451,
     *     "availableto": 2217492917451,
     *     "availability": 1,
     *     "productAvailabilityModel": {
     *         "available": true,
     *         "inStock": true,
     *         "perpetual": false,
     *         "defaultSkuCode": "BENDER-ua",
     *         "firstAvailableSkuCode": "BENDER-ua",
     *         "skuCodes": ["BENDER-ua"],
     *         "availableToSellQuantity": {
     *             "BENDER-ua": 1.000
     *         }
     *     },
     *     "price": {
     *         "currency": "EUR",
     *         "symbol": "€",
     *         "symbolPosition": "before",
     *         "quantity": 1,
     *         "regularPrice": 0.00,
     *         "salePrice": null,
     *         "discount": null
     *     },
     *     "defaultImage": "noimage.jpeg",
     *     "featured": false,
     *     "minOrderQuantity": 1.00,
     *     "maxOrderQuantity": 100000.00,
     *     "stepOrderQuantity": 1.00,
     *     "skus": null,
     *     "priceWhenAdded": {
     *         "currency": "EUR",
     *         "symbol": "€",
     *         "symbolPosition": "before",
     *         "quantity": 1,
     *         "regularPrice": 0.00,
     *         "salePrice": null,
     *         "discount": null
     *     },
     *     "quantity": 10.00
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link CustomerWishList}) )
     * @param request request
     * @param response response
     *
     * @return list of wish list items
     */
    @RequestMapping(
            value = "/wishlist/{type}/{tag}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<ProductWishlistRO> viewWishlist(@PathVariable(value = "type") final String type,
                                                              @PathVariable(value = "tag") final String tag,
                                                              final HttpServletRequest request,
                                                              final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return viewWishlistInternal(type, tag);

    }




    /**
     * Interface: GET /yes-api/rest/customer/wishlist/{type}
     * <p>
     * <p>
     * Display customer default wishlist.
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
     *     <tr><td>type</td><td>type of address book {@link CustomerWishList}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array object ProductWishlistRO</td><td>
     * <pre><code>
     * &lt;wishlist&gt;
     * 	&lt;product&gt;
     * 		&lt;availability&gt;1&lt;/availability&gt;
     * 		&lt;availablefrom&gt;2010-04-08T11:15:17.451+01:00&lt;/availablefrom&gt;
     * 		&lt;availableto&gt;2040-04-08T11:15:17.451+01:00&lt;/availableto&gt;
     * 		&lt;code&gt;BENDER-ua&lt;/code&gt;
     * 		&lt;default-image&gt;noimage.jpeg&lt;/default-image&gt;
     * 		&lt;default-sku-code&gt;BENDER-ua&lt;/default-sku-code&gt;
     * 		&lt;description&gt;...&lt;/description&gt;
     * 		&lt;display-names&gt;
     * 			&lt;entry lang="ua"&gt;...&lt;/entry&gt;
     * 			&lt;entry lang="en"&gt;Bender Bending Rodriguez&lt;/entry&gt;
     * 			&lt;entry lang="ru"&gt;...&lt;/entry&gt;
     * 		&lt;/display-names&gt;
     * 		&lt;featured&gt;false&lt;/featured&gt;
     * 		&lt;id&gt;9998&lt;/id&gt;
     * 		&lt;max-order-quantity&gt;100000.00&lt;/max-order-quantity&gt;
     * 		&lt;min-order-quantity&gt;1.00&lt;/min-order-quantity&gt;
     * 		&lt;multisku&gt;false&lt;/multisku&gt;
     * 		&lt;name&gt;...&lt;/name&gt;
     * 		&lt;price&gt;
     *   			&lt;currency&gt;EUR&lt;/currency&gt;
     * 			&lt;quantity&gt;1&lt;/quantity&gt;
     * 			&lt;regular-price&gt;0.00&lt;/regular-price&gt;
     * 			&lt;symbol&gt;€&lt;/symbol&gt;
     * 			&lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 		&lt;/price&gt;
     * 		&lt;product-availability&gt;
     * 			&lt;available&gt;true&lt;/available&gt;
     * 			&lt;ats-quantity&gt;
     * 				&lt;entry sku="BENDER-ua"&gt;1.000&lt;/entry&gt;
     * 			&lt;/ats-quantity&gt;
     * 			&lt;default-sku&gt;BENDER-ua&lt;/default-sku&gt;
     * 			&lt;first-available-sku&gt;BENDER-ua&lt;/first-available-sku&gt;
     * 			&lt;in-stock&gt;true&lt;/in-stock&gt;
     * 			&lt;perpetual&gt;false&lt;/perpetual&gt;
     * 			&lt;sku-codes&gt;BENDER-ua&lt;/sku-codes&gt;
     * 		&lt;/product-availability&gt;
     * 		&lt;step-order-quantity&gt;1.00&lt;/step-order-quantity&gt;
     * 		&lt;priceWhenAdded&gt;
     * 			&lt;currency&gt;EUR&lt;/currency&gt;
     * 			&lt;quantity&gt;1&lt;/quantity&gt;
     * 			&lt;regular-price&gt;0.00&lt;/regular-price&gt;
     * 			&lt;symbol&gt;€&lt;/symbol&gt;
     * 			&lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 		&lt;/priceWhenAdded&gt;
     * 		&lt;quantity&gt;10.00&lt;/quantity&gt;
     * 	&lt;/product&gt;
     * &lt;/wishlist&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link CustomerWishList}) )
     * @param request request
     * @param response response
     *
     * @return list of wish list items
     */
    @RequestMapping(
            value = "/wishlist/{type}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ProductWishlistListRO viewWishlistXML(@PathVariable(value = "type") final String type,
                                                               final HttpServletRequest request,
                                                               final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new ProductWishlistListRO(viewWishlistInternal(type, null));

    }


    /**
     * Interface: GET /yes-api/rest/customer/wishlist/{type}/{tag}
     * <p>
     * <p>
     * Display customer wishlist by tag.
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
     *     <tr><td>type</td><td>type of address book {@link CustomerWishList}</td></tr>
     *     <tr><td>tag</td><td>wishlist tag {@link CustomerWishList}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array object ProductWishlistRO</td><td>
     * <pre><code>
     * &lt;wishlist&gt;
     * 	&lt;product&gt;
     * 		&lt;availability&gt;1&lt;/availability&gt;
     * 		&lt;availablefrom&gt;2010-04-08T11:15:17.451+01:00&lt;/availablefrom&gt;
     * 		&lt;availableto&gt;2040-04-08T11:15:17.451+01:00&lt;/availableto&gt;
     * 		&lt;code&gt;BENDER-ua&lt;/code&gt;
     * 		&lt;default-image&gt;noimage.jpeg&lt;/default-image&gt;
     * 		&lt;default-sku-code&gt;BENDER-ua&lt;/default-sku-code&gt;
     * 		&lt;description&gt;...&lt;/description&gt;
     * 		&lt;display-names&gt;
     * 			&lt;entry lang="ua"&gt;...&lt;/entry&gt;
     * 			&lt;entry lang="en"&gt;Bender Bending Rodriguez&lt;/entry&gt;
     * 			&lt;entry lang="ru"&gt;...&lt;/entry&gt;
     * 		&lt;/display-names&gt;
     * 		&lt;featured&gt;false&lt;/featured&gt;
     * 		&lt;id&gt;9998&lt;/id&gt;
     * 		&lt;max-order-quantity&gt;100000.00&lt;/max-order-quantity&gt;
     * 		&lt;min-order-quantity&gt;1.00&lt;/min-order-quantity&gt;
     * 		&lt;multisku&gt;false&lt;/multisku&gt;
     * 		&lt;name&gt;...&lt;/name&gt;
     * 		&lt;price&gt;
     *   			&lt;currency&gt;EUR&lt;/currency&gt;
     * 			&lt;quantity&gt;1&lt;/quantity&gt;
     * 			&lt;regular-price&gt;0.00&lt;/regular-price&gt;
     * 			&lt;symbol&gt;€&lt;/symbol&gt;
     * 			&lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 		&lt;/price&gt;
     * 		&lt;product-availability&gt;
     * 			&lt;available&gt;true&lt;/available&gt;
     * 			&lt;ats-quantity&gt;
     * 				&lt;entry sku="BENDER-ua"&gt;1.000&lt;/entry&gt;
     * 			&lt;/ats-quantity&gt;
     * 			&lt;default-sku&gt;BENDER-ua&lt;/default-sku&gt;
     * 			&lt;first-available-sku&gt;BENDER-ua&lt;/first-available-sku&gt;
     * 			&lt;in-stock&gt;true&lt;/in-stock&gt;
     * 			&lt;perpetual&gt;false&lt;/perpetual&gt;
     * 			&lt;sku-codes&gt;BENDER-ua&lt;/sku-codes&gt;
     * 		&lt;/product-availability&gt;
     * 		&lt;step-order-quantity&gt;1.00&lt;/step-order-quantity&gt;
     * 		&lt;priceWhenAdded&gt;
     * 			&lt;currency&gt;EUR&lt;/currency&gt;
     * 			&lt;quantity&gt;1&lt;/quantity&gt;
     * 			&lt;regular-price&gt;0.00&lt;/regular-price&gt;
     * 			&lt;symbol&gt;€&lt;/symbol&gt;
     * 			&lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 		&lt;/priceWhenAdded&gt;
     * 		&lt;quantity&gt;10.00&lt;/quantity&gt;
     * 	&lt;/product&gt;
     * &lt;/wishlist&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link CustomerWishList}) )
     * @param request request
     * @param response response
     *
     * @return list of wish list items
     */
    @RequestMapping(
            value = "/wishlist/{type}/{tag}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ProductWishlistListRO viewWishlistXML(@PathVariable(value = "type") final String type,
                                                               @PathVariable(value = "tag") final String tag,
                                                               final HttpServletRequest request,
                                                               final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new ProductWishlistListRO(viewWishlistInternal(type, tag));

    }




    private List<ProductSearchResultRO> viewRecentlyViewedInternal() {


        final ShoppingCart cart = cartMixin.getCurrentCart();
        final long shopId = cartMixin.getCurrentShopId();

        final List<String> productIds = cart.getShoppingContext().getLatestViewedSkus();

        final List<ProductSearchResultDTO> viewedProducts = productServiceFacade.getListProducts(
                productIds, -1L, ShopCodeContext.getShopId());

        final List<ProductSearchResultRO> rvRo = new ArrayList<ProductSearchResultRO>();

        final Pair<String, Boolean> symbol = currencySymbolService.getCurrencySymbol(cart.getCurrencyCode());

        for (final ProductSearchResultDTO viewedProduct : viewedProducts) {

            final ProductSearchResultRO rv = mappingMixin.map(viewedProduct, ProductSearchResultRO.class, ProductSearchResultDTO.class);

            final ProductAvailabilityModel skuPam = productServiceFacade.getProductAvailability(viewedProduct, shopId);
            final ProductAvailabilityModelRO amRo = mappingMixin.map(skuPam, ProductAvailabilityModelRO.class, ProductAvailabilityModel.class);
            rv.setProductAvailabilityModel(amRo);

            final SkuPrice price = productServiceFacade.getSkuPrice(
                    null,
                    skuPam.getFirstAvailableSkuCode(),
                    BigDecimal.ONE,
                    cart.getCurrencyCode(),
                    shopId
            );

            final SkuPriceRO priceRo = mappingMixin.map(price, SkuPriceRO.class, SkuPrice.class);
            priceRo.setSymbol(symbol.getFirst());
            priceRo.setSymbolPosition(symbol.getSecond() != null && symbol.getSecond() ? "after" : "before");

            rv.setPrice(priceRo);

            rvRo.add(rv);

        }

        return rvRo;

    }


    /**
     * Interface: GET /customer/recentlyviewed
     * <p>
     * <p>
     * Display list of recently viewed products.
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
     * <p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array of object ProductSearchResultRO</td><td>
     * <pre><code>
     * 	[
     * 	  {
     * 	    "maxOrderQuantity" : null,
     * 	    "minOrderQuantity" : null,
     * 	    "displayDescription" : {
     * 	      "uk" : "Trust Isotto Wired Mini Mouse...",
     * 	      "ru" : "Trust Isotto Wired Mini Mouse...",
     * 	      "en" : "Trust Isotto Wired Mini Mouse..."
     * 	    },
     * 	    "code" : "19733",
     * 	    "availability" : 1,
     * 	    "displayName" : {
     * 	      "uk" : "Isotto Wired Mini Mouse",
     * 	      "ru" : "Isotto Wired Mini Mouse",
     * 	      "en" : "Isotto Wired Mini Mouse"
     * 	    },
     * 	    "defaultSkuCode" : "19733",
     * 	    "name" : "Isotto Wired Mini Mouse",
     * 	    "stepOrderQuantity" : null,
     * 	    "availablefrom" : null,
     * 	    "id" : 184,
     * 	    "availableto" : null,
     * 	    "manufacturerCode" : null,
     * 	    "skus" : null,
     * 	    "productAvailabilityModel" : {
     * 	      "firstAvailableSkuCode" : "19733",
     * 	      "available" : true,
     * 	      "defaultSkuCode" : "19733",
     * 	      "inStock" : true,
     * 	      "skuCodes" : [
     * 	        "19733"
     * 	      ],
     * 	      "perpetual" : false,
     * 	      "availableToSellQuantity" : {
     * 	        "19733" : 99
     * 	      }
     * 	    },
     * 	    "price" : {
     * 	      "symbol" : "€",
     * 	      "quantity" : 1,
     * 	      "regularPrice" : 643.2,
     * 	      "salePrice" : null,
     * 	      "discount" : null,
     * 	      "currency" : "EUR",
     * 	      "symbolPosition" : "before"
     * 	    },
     * 	    "featured" : false,
     * 	    "defaultImage" : "Trust-Isotto-Wired-Mini-Mouse_19733_a.jpg",
     * 	    "multisku" : false,
     * 	    "description" : "Trust Isotto Wired Mini Mouse..."
     * 	  },
     * 	...
     * 	]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return list of products
     */
    @RequestMapping(
            value = "/recentlyviewed",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<ProductSearchResultRO> viewRecent(final HttpServletRequest request,
                                                                final HttpServletResponse response) {
        cartMixin.persistShoppingCart(request, response);
        return viewRecentlyViewedInternal();

    }


    /**
     * Interface: GET /customer/recentlyviewed
     * <p>
     * <p>
     * Display list of recently viewed products.
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
     * <p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array of object ProductSearchResultRO</td><td>
     * <pre><code>
     * 	&lt;products&gt;
     * 	    &lt;product&gt;
     * 	        &lt;availability&gt;1&lt;/availability&gt;
     * 	        &lt;code&gt;19733&lt;/code&gt;
     * 	        &lt;default-image&gt;Trust-Isotto-Wired-Mini-Mouse_19733_a.jpg&lt;/default-image&gt;
     * 	        &lt;default-sku-code&gt;19733&lt;/default-sku-code&gt;
     * 	        &lt;description&gt;Trust Isotto Wired Mini Mouse...&lt;/description&gt;
     * 	        &lt;display-descriptions&gt;
     * 	            &lt;entry lang="uk"&gt;Trust Isotto Wired Mini Mouse...&lt;/entry&gt;
     * 	            &lt;entry lang="en"&gt;Trust Isotto Wired Mini Mouse...&lt;/entry&gt;
     * 	            &lt;entry lang="ru"&gt;Trust Isotto Wired Mini Mouse...&lt;/entry&gt;
     * 	        &lt;/display-descriptions&gt;
     * 	        &lt;display-names&gt;
     * 	            &lt;entry lang="uk"&gt;Isotto Wired Mini Mouse&lt;/entry&gt;
     * 	            &lt;entry lang="en"&gt;Isotto Wired Mini Mouse&lt;/entry&gt;
     * 	            &lt;entry lang="ru"&gt;Isotto Wired Mini Mouse&lt;/entry&gt;
     * 	        &lt;/display-names&gt;
     * 	        &lt;featured&gt;false&lt;/featured&gt;
     * 	        &lt;id&gt;184&lt;/id&gt;
     * 	        &lt;multisku&gt;false&lt;/multisku&gt;
     * 	        &lt;name&gt;Isotto Wired Mini Mouse&lt;/name&gt;
     * 	        &lt;price&gt;
     * 	            &lt;currency&gt;EUR&lt;/currency&gt;
     * 	            &lt;quantity&gt;1.00&lt;/quantity&gt;
     * 	            &lt;regular-price&gt;643.20&lt;/regular-price&gt;
     * 	            &lt;symbol&gt;€&lt;/symbol&gt;
     * 	            &lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 	        &lt;/price&gt;
     * 	        &lt;product-availability&gt;
     * 	            &lt;available&gt;true&lt;/available&gt;
     * 	            &lt;ats-quantity&gt;
     * 	                &lt;entry sku="19733"&gt;99.000&lt;/entry&gt;
     * 	            &lt;/ats-quantity&gt;
     * 	            &lt;default-sku&gt;19733&lt;/default-sku&gt;
     * 	            &lt;first-available-sku&gt;19733&lt;/first-available-sku&gt;
     * 	            &lt;in-stock&gt;true&lt;/in-stock&gt;
     * 	            &lt;perpetual&gt;false&lt;/perpetual&gt;
     * 	            &lt;sku-codes&gt;19733&lt;/sku-codes&gt;
     * 	        &lt;/product-availability&gt;
     * 	    &lt;/product&gt;
     * 	...
     * 	&lt;/products&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return list of products
     */
    @RequestMapping(
            value = "/recentlyviewed",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ProductSearchResultListRO viewRecentXML(final HttpServletRequest request,
                                                                 final HttpServletResponse response) {
        cartMixin.persistShoppingCart(request, response);
        return new ProductSearchResultListRO(viewRecentlyViewedInternal());

    }


}
