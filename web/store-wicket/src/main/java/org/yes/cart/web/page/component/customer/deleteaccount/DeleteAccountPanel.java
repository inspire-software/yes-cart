/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.web.page.component.customer.deleteaccount;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CustomerServiceFacade;
import org.yes.cart.web.utils.WicketUtil;

import java.util.Collections;

/**
 * User: denis
 */
public class DeleteAccountPanel extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String ACCOUNT_DELETE_FORM = "accountDeleteForm";
    private final static String SAVE_LINK = "saveLink";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;


    /**
     * Construct form to delete account.
     *
     * @param s             panel id
     * @param customerModel customer model
     */
    public DeleteAccountPanel(final String s, final IModel<Customer> customerModel) {

        super(s, customerModel);

        add(
                new Form<Customer>(ACCOUNT_DELETE_FORM, customerModel) {

                    @Override
                    protected void onSubmit() {
                        customerServiceFacade.deleteAccount(getCurrentShop(), getModelObject());

                        info(WicketUtil.createStringResourceModel(this, "deleteAccountRequestEmailSent",
                                Collections.singletonMap("email", getModelObject().getEmail())).getString());
                        super.onSubmit();
                    }

                }
                        .add(
                                new SubmitLink(SAVE_LINK)
                        )
        );

        setVisible(customerModel.getObject() != null && !getCurrentShop().isSfDeleteAccountDisabled(customerModel.getObject().getCustomerType()));

    }

}
