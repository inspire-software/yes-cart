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

package org.yes.cart.web.service.rest.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.ro.AddressRO;
import org.yes.cart.domain.ro.CountryRO;
import org.yes.cart.domain.ro.StateRO;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.support.service.AddressBookFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 30/03/2015
 * Time: 08:31
 */
@Service("restAddressSupportMixin")
public class AddressSupportMixin extends RoMappingMixin {

    @Autowired
    private CustomerServiceFacade customerServiceFacade;

    @Autowired
    private AddressBookFacade addressBookFacade;

    /**
     * Common address book function.
     *
     * @param cart cart
     * @param shop shop
     * @param customerShop customerShop
     * @param addressType type
     *
     * @return list of customer addresses
     */
    public List<AddressRO> viewAddressOptions(final ShoppingCart cart,
                                              final Shop shop,
                                              final Shop customerShop,
                                              final String addressType) {

        final List<AddressRO> ros = new ArrayList<AddressRO>();

        final Customer customer = customerServiceFacade.getCheckoutCustomer(shop, cart);

        if (customer != null) {

            final List<Country> countries = addressBookFacade.getAllCountries(shop.getCode(), addressType);


            final List<Address> addresses = new ArrayList<Address>(addressBookFacade.getAddresses(customer, customerShop, addressType));

            for (final Address address : addresses) {

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
                                ros.add(ro);
                                break;
                            }
                        }
                        break;
                    }
                }

            }

        }
        return ros;

    }

    /**
     * Common address book function.
     *
     * @param cart cart
     * @param shop shop
     * @param addressType type
     *
     * @return list of available countries
     */
    public List<CountryRO> viewAddressCountryOptions(final ShoppingCart cart,
                                                     final Shop shop,
                                                     final String addressType) {

        final List<Country> countries = addressBookFacade.getAllCountries(shop.getCode(), addressType);

        return map(countries, CountryRO.class, Country.class);

    }

    /**
     * Common address book function.
     *
     * @param cart cart
     * @param shop shop
     * @param countryCode country code
     *
     * @return list of state for given country
     */
    public List<StateRO> viewAddressCountryStateOptions(final ShoppingCart cart,
                                                        final Shop shop,
                                                        final String countryCode) {

        final List<State> states = addressBookFacade.getStatesByCountry(countryCode);

        return map(states, StateRO.class, State.class);

    }




}
