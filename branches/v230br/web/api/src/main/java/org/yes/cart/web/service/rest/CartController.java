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
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.ro.*;
import org.yes.cart.domain.ro.xml.XMLParamsRO;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.service.AddressBookFacade;
import org.yes.cart.web.support.service.CheckoutServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;
import org.yes.cart.web.support.service.ShippingServiceFacade;
import org.yes.cart.web.support.util.CommandUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/03/2015
 * Time: 17:30
 */
@Controller
public class CartController extends AbstractApiController {

    @Autowired
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @Autowired
    private ShippingServiceFacade shippingServiceFacade;

    @Autowired
    private CustomerServiceFacade customerServiceFacade;

    @Autowired
    private AddressBookFacade addressBookFacade;

    @Autowired
    private CheckoutServiceFacade checkoutServiceFacade;

    @RequestMapping(
            value = "/cart",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CartRO viewCart(final HttpServletRequest request,
                                         final HttpServletResponse response) {

        persistShoppingCart(request, response);

        final CartRO cartRO = map(getCurrentCart(), CartRO.class, ShoppingCart.class);

        return cartRO;

    }

    public void commandInternalRun(final Map<String, Object> params) {

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

                shoppingCartCommandFactory.execute(getCurrentCart(), safe);

            }

        }

    }


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

        final ShoppingCart cart = getCurrentCart();

        final List<Carrier> carriers = shippingServiceFacade.findCarriers(cart);

        final List<CarrierRO> ros = map(carriers, CarrierRO.class, Carrier.class);

        return ros;

    }

    @RequestMapping(
            value = "/cart/options/shipping",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<CarrierRO> cartCarrierOptions(final HttpServletRequest request,
                                                            final HttpServletResponse response) {

        return cartCarrierOptionsInternal();

    }

    @RequestMapping(
            value = "/cart/options/shipping",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CarrierListRO cartCarrierOptionsXML(final HttpServletRequest request,
                                                             final HttpServletResponse response) {

        return new CarrierListRO(cartCarrierOptionsInternal());

    }

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

            final ShoppingCart cart = getCurrentCart();

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



    private List<AddressRO> cartAddressOptionsInternal() {

        final ShoppingCart cart = getCurrentCart();



        final Shop shop = getCurrentShop();

        final List<String> allowedBillingCountries = shop.getSupportedBillingCountriesAsList();
        final List<String> allowedDeliveryCountries = shop.getSupportedShippingCountriesAsList();

        final List<AddressRO> ros = new ArrayList<AddressRO>();
        if (cart.getLogonState() == ShoppingCart.LOGGED_IN) {

            final Customer customer = customerServiceFacade.getCustomerByEmail(cart.getCustomerEmail());

            if (customer != null) {

                final List<Country> billingCountries = addressBookFacade.getAllCountries(shop.getCode(), Address.ADDR_TYPE_BILLING);
                final List<Country> deliveryCountries = addressBookFacade.getAllCountries(shop.getCode(), Address.ADDR_TYPE_SHIPING);


                for (final Address address : customer.getAddress()) {

                    final String type = address.getAddressType();
                    final String country = address.getCountryCode();
                    final List<String> countryValidator = Address.ADDR_TYPE_BILLING.equals(type) ? allowedBillingCountries : allowedDeliveryCountries;
                    final List<Country> countries = Address.ADDR_TYPE_BILLING.equals(type) ? billingCountries : deliveryCountries;

                    if (countryValidator.isEmpty() || countryValidator.contains(country)) {

                        final AddressRO ro = map(address, AddressRO.class, Address.class);

                        for (final Country cnt : countries) {
                            if (cnt.getCountryCode().equals(ro.getCountryCode())) {
                                ro.setCountryName(cnt.getName());
                                ro.setCountryLocalName(cnt.getDisplayName());
                                final List<State> states = addressBookFacade.getStatesByCountry(ro.getCountryCode());
                                for (final State state : states) {
                                    if (state.getStateCode().equals(ro.getStateCode())) {
                                        ro.setStateName(state.getName());
                                        ro.setStateLocalName(state.getDisplayName());
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                    }

                }

            }

        }
        return ros;

    }

    @RequestMapping(
            value = "/cart/options/address",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<AddressRO> cartAddressOptions(final HttpServletRequest request,
                                                            final HttpServletResponse response) {

        return cartAddressOptionsInternal();

    }

    @RequestMapping(
            value = "/cart/options/address",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody AddressListRO cartAddressOptionsXML(final HttpServletRequest request,
                                                             final HttpServletResponse response) {

        return new AddressListRO(cartAddressOptionsInternal());

    }



}
