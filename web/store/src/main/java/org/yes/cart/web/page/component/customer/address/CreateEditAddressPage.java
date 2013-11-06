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

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.CheckoutPage;
import org.yes.cart.web.page.CustomerSelfCarePage;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.AddressBookFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 1:00 PM
 */
public class CreateEditAddressPage extends AbstractWebPage {

    public final static String RETURN_TO_SELFCARE = "selfcare";
    public final static String RETURN_TO_CHECKOUT = "checkout";

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String ADDRESS_FORM = "addressForm";
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.ADDRESS_BOOK_FACADE)
    private AddressBookFacade addressBookFacade;

    /**
     * Construct page to create / edit customer address.
     * Created address will be treated as default  shipping or billing address.
     *
     * @param params page parameters
     */
    public CreateEditAddressPage(final PageParameters params) {

        super(params);

        final Customer customer = customerServiceFacade.getCustomerByEmail(ApplicationDirector.getShoppingCart().getCustomerEmail());

        final String addrId = params.get(WebParametersKeys.ADDRESS_ID).toString();

        final String addrType = params.get(WebParametersKeys.ADDRESS_TYPE).toString();

        final Address address = addressBookFacade.getAddress(customer, addrId, addrType);


        final Class<? extends Page>  returnToPageClass = RETURN_TO_SELFCARE.equals(
                params.get(WebParametersKeys.ADDRESS_FORM_RETURN_LABEL).toString()) ?
                CustomerSelfCarePage.class : CheckoutPage.class;

        final PageParameters successPageParameters = new PageParameters();

        add(
                new FeedbackPanel(FEEDBACK)
        );

        add(
                new AddressForm(
                        ADDRESS_FORM,
                        new Model<Address>(address),
                        addrType,
                        returnToPageClass,
                        successPageParameters,
                        returnToPageClass,
                        successPageParameters
                )
        );


    }

}
