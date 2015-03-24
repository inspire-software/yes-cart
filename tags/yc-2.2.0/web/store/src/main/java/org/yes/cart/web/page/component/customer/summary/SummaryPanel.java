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

package org.yes.cart.web.page.component.customer.summary;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CustomerServiceFacade;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/23/11
 * Time: 9:20 PM
 */
public class SummaryPanel extends BaseComponent {
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String SUMMARY_FORM = "summaryForm";

    private static final String EMAIL_LABEL = "email";
    private static final String FIRSTNAME_INPUT = "firstname";
    private static final String LASTNAME_INPUT = "lastname";
    private final static String SAVE_LINK = "saveLink";

// ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;


    /**
     * Construct customer summary panel.
     *
     * @param id            panel id
     * @param customerModel model of customer
     */
    public SummaryPanel(final String id, final IModel<Customer> customerModel) {

        super(id, customerModel);

        final Customer customer = customerModel.getObject();

        add(
                new Form<Customer>(SUMMARY_FORM, customerModel) {

                    @Override
                    protected void onSubmit() {
                        customerServiceFacade.updateCustomer(getModelObject());
                        info(getLocalizer().getString("profileUpdated", this));
                        super.onSubmit();
                    }
                }.add(
                        new Label(EMAIL_LABEL, customer.getEmail())
                ).add(
                        new TextField(FIRSTNAME_INPUT, new PropertyModel(customer, "firstname")).setRequired(true)
                ).add(
                        new TextField(LASTNAME_INPUT, new PropertyModel(customer, "lastname")).setRequired(true)
                ).add(
                        new SubmitLink(SAVE_LINK)
                )
        );
    }

}
