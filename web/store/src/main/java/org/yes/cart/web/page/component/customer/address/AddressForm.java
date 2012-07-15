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

package org.yes.cart.web.page.component.customer.address;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.CountryService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.StateService;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.util.CountryModel;
import org.yes.cart.web.page.component.util.CountryRenderer;
import org.yes.cart.web.page.component.util.StateModel;
import org.yes.cart.web.page.component.util.StateRenderer;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.List;

/**
 * Form to create / edit customer address.
 * Igor Azarny iazarny@yahoo.com
 * Date: 12/10/11
 * Time: 18:01
 */
public class AddressForm extends Form<Address> {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String FIRSTNAME = "firstName";
    private final static String LASTNAME = "lastName";
    private final static String LINE1 = "line1";
    private final static String LINE2 = "line2";
    private final static String CITY = "city";
    private final static String COUNTRY = "country";
    private final static String STATE = "state";
    private final static String POSTCODE = "postcode";
    private final static String PHONELIST = "phoneList";
    private final static String ADD_ADDRESS = "addAddress";
    private final static String CANCELL_LINK = "cancel";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    private final static int SMALL_LENGTH = 16;
    private final static int MEDIUM_LENGTH = 128;
    private final static int LARGE_LENGTH = 256;

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;

    @SpringBean(name = ServiceSpringKeys.ADDRESS_SERVICE)
    private AddressService addressService;

    @SpringBean(name = ServiceSpringKeys.COUNTRY_SERVICE)
    private CountryService countryService;

    @SpringBean(name = ServiceSpringKeys.STATE_SERVICE)
    private StateService stateService;


    private final Class<? extends Page> succsessPage;
    private final PageParameters succsessPageParameters;

    /**
     * Create address form.
     *
     * @param s                      form id
     * @param addressIModel          address model.
     * @param addressType            address type
     * @param succsessPage           succsess page class
     * @param succsessPageParameters succsess page parameters
     * @param cancelPage             optional cancel page class
     * @param cancelPageParameters   optional  cancel page parameters
     */
    public AddressForm(final String s,
                       final IModel<Address> addressIModel,
                       final String addressType,
                       final Class<? extends Page> succsessPage,
                       final PageParameters succsessPageParameters,
                       final Class<? extends Page> cancelPage,
                       final PageParameters cancelPageParameters) {

        super(s, addressIModel);

        this.succsessPage = succsessPage;
        this.succsessPageParameters = succsessPageParameters;


        final Address address = addressIModel.getObject();

        final Customer customer = customerService.findCustomer(
                ApplicationDirector.getShoppingCart().getCustomerEmail()
        );


        preprocessAddress(address, addressType, customer);


        final List<State> stateList = getStateList(address.getCountryCode());
        final List<Country> countryList = countryService.findAll();

        final AbstractChoice<State, State> stateDropDownChoice = new DropDownChoice<State>(
                STATE,
                new StateModel(new PropertyModel(address, "stateCode"), stateList),
                stateList).setChoiceRenderer(new StateRenderer());
        stateDropDownChoice.setRequired(!stateList.isEmpty());


        add(
                new TextField<String>(FIRSTNAME, new PropertyModel<String>(address, "firstname"))
                        .setRequired(true).add(new StringValidator.MaximumLengthValidator(MEDIUM_LENGTH))
        ).add(
                new TextField<String>(LASTNAME, new PropertyModel<String>(address, "lastname"))
                        .setRequired(true).add(new StringValidator.MaximumLengthValidator(MEDIUM_LENGTH))
        ).add(
                new TextField<String>(LINE1, new PropertyModel<String>(address, "addrline1"))
                        .setRequired(true).add(new StringValidator.MaximumLengthValidator(LARGE_LENGTH))
        ).add(
                new TextField<String>(LINE2, new PropertyModel<String>(address, "addrline2"))
                        .add(new StringValidator.MaximumLengthValidator(LARGE_LENGTH))
        ).add(
                new TextField<String>(CITY, new PropertyModel<String>(address, "city"))
                        .setRequired(true).add(new StringValidator.MaximumLengthValidator(MEDIUM_LENGTH))
        ).add(
                new TextField<String>(POSTCODE, new PropertyModel<String>(address, "postcode"))
                        .setRequired(true).add(new StringValidator.MaximumLengthValidator(SMALL_LENGTH))
        ).add(
                new TextField<String>(PHONELIST, new PropertyModel<String>(address, "phoneList"))
                        .setRequired(true).add(new StringValidator.MaximumLengthValidator(LARGE_LENGTH))
        ).add(
                stateDropDownChoice
        ).add(
                new DropDownChoice<Country>(
                        COUNTRY,
                        new CountryModel(new PropertyModel(address, "countryCode"), countryList),
                        countryList) {

                    @Override
                    protected void onSelectionChanged(final Country country) {
                        super.onSelectionChanged(country);
                        stateDropDownChoice.setChoices(getStateList(country.getCountryCode()));
                        stateDropDownChoice.setRequired(!stateDropDownChoice.getChoices().isEmpty());
                        address.setStateCode(StringUtils.EMPTY);
                    }

                    @Override
                    protected boolean wantOnSelectionChangedNotifications() {
                        return true;
                    }

                }.setChoiceRenderer(new CountryRenderer()).setRequired(true)
        ).add(
                new SubmitLink(ADD_ADDRESS) {

                    @Override
                    public void onSubmit() {
                        final Address addr = getModelObject();
                        if (addr.getAddressId() == 0) {
                            addressService.create(addr);
                        } else {
                            addressService.update(addr);
                        }
                        setResponsePage(succsessPage, succsessPageParameters);
                    }

                }
        ).add(
                new SubmitLink(CANCELL_LINK) {

                    @Override
                    public void onSubmit() {
                        setResponsePage(cancelPage, cancelPageParameters);
                    }

                }.setDefaultFormProcessing(false).setVisible(cancelPage != null)
        );

    }

    /**
     * Fill some data in case of new {@link Address}
     *
     * @param addressType addres type
     * @param address     address to preprocess
     * @param customer    customer.
     */
    private void preprocessAddress(final Address address, final String addressType, final Customer customer) {
        if (address.getAddressId() == 0) {

            address.setAddressType(addressType);
            address.setCustomer(customer);
            fillAddressWithGeoIpData(address, customer);

        }
    }

    /**
     * Fill new Address with Geo Ip data.
     * At this moment only profile data are supplied. No geo ip.
     * INTEGRATION POINT with tag cloud, that will have geo ip data
     *
     * @param address  address to fill
     * @param customer customer.
     */
    private void fillAddressWithGeoIpData(final Address address, final Customer customer) {
        final AttrValueCustomer attrValue = customer.getAttributeByCode(AttributeNamesKeys.CUSTOMER_PHONE);
        address.setFirstname(customer.getFirstname());
        address.setLastname(customer.getLastname());
        address.setPhoneList(attrValue == null ? StringUtils.EMPTY : attrValue.getVal());
    }


    /**
     * Get states inside selected country.
     *
     * @param countryCode country ot retrive the states.
     * @return state list inside selected country.
     */
    private List<State> getStateList(final String countryCode) {
        return stateService.findByCountry(countryCode);
    }


}
