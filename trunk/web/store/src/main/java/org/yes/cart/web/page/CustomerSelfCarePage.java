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

package org.yes.cart.web.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.util.StringUtils;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.customer.address.ManageAddressesView;
import org.yes.cart.web.page.component.customer.dynaform.DynaFormPanel;
import org.yes.cart.web.page.component.customer.order.CustomerOrderPanel;
import org.yes.cart.web.page.component.customer.password.PasswordPanel;
import org.yes.cart.web.page.component.customer.summary.SummaryPanel;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CustomerServiceFacade;

/**
 *
 * Customer self care page to view orders, wish list, etc.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/11/11
 * Time: 9:51 PM
 */
@RequireHttps
@AuthorizeInstantiation("USER")
public class CustomerSelfCarePage extends AbstractWebPage {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String SUMMARY_PANEL = "summaryView";
    private final static String ATTR_PANEL = "attributesView";
    private final static String PASSWORD_PANEL = "passwordView";
    private final static String SHIPPING_ADDR_PANEL = "shippingAddressesView";
    private final static String BILLING_ADDR_PANEL = "billingAddressesView";
    private final static String ORDERS_PANEL = "ordersView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public CustomerSelfCarePage(final PageParameters params) {
        super(params);

        final String email = ApplicationDirector.getShoppingCart().getCustomerEmail();
        final Customer customer;
        if (StringUtils.hasLength(email)) {
            customer = customerServiceFacade.getCustomerByEmail(ApplicationDirector.getShoppingCart().getCustomerEmail());
        } else {
            customer = null;
        }

        final Model<Customer> customerModel = new Model<Customer>(customer);

        add(
                new FeedbackPanel(FEEDBACK)
        ).add(
                new PasswordPanel(PASSWORD_PANEL, customerModel)
        ).add(
                new ManageAddressesView(SHIPPING_ADDR_PANEL, customerModel, Address.ADDR_TYPE_SHIPING, false)
        ).add(
                new ManageAddressesView(BILLING_ADDR_PANEL, customerModel, Address.ADDR_TYPE_BILLING, false)
        ).add(
                new DynaFormPanel(ATTR_PANEL, customerModel)
        ).add(
                new SummaryPanel(SUMMARY_PANEL, customerModel)
        ).add(
                new CustomerOrderPanel(ORDERS_PANEL, customerModel)
        ).add(
                new StandardFooter(FOOTER)
        ).add(
                new StandardHeader(HEADER)
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {
        processCommands();
        super.onBeforeRender();
    }

    /**
     * Get page title.
     *
     * @return page title
     */
    public IModel<String> getPageTitle() {
        return new Model<String>(getLocalizer().getString("selfCare",this));
    }


}
