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

package org.yes.cart.web.page.component.customer.summary;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.customer.auth.BaseAuthForm;
import org.yes.cart.web.page.component.customer.logout.LogoutPanel;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/23/11
 * Time: 9:20 PM
 */
public class SummaryPanel extends BaseComponent {
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String SUMMARY_FORM = "summaryForm";

    private static final String LOGIN_LABEL = "loginLabel";
    private static final String LOGIN_INPUT = "login";
    private static final String PASSWORD_INPUT = "password";
    private static final String CHANGE_USERNAME_HIDDEN = "changeUsername";

// ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;


    /**
     * Construct customer summary panel.
     *
     * @param id            panel id
     * @param customerModel model of customer
     */
    public SummaryPanel(final String id, final IModel<Customer> customerModel) {

        super(id, customerModel);

        add(new SummaryForm("summaryForm", customerModel));

        setVisible(customerModel.getObject() != null);

    }

    public final class SummaryForm extends BaseAuthForm {

        private String login;
        private String password;
        private String changeUsername;

        public String getLogin() {
            return login;
        }

        public void setLogin(final String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(final String password) {
            this.password = password;
        }

        public String getChangeUsername() {
            return changeUsername;
        }

        public void setChangeUsername(final String changeUsername) {
            this.changeUsername = changeUsername;
        }

        public SummaryForm(final String id, final IModel<Customer> customerModel) {
            super(id);

            setModel(new CompoundPropertyModel<>(SummaryForm.this));

            final Customer customer = customerModel.getObject();

            setLogin(customer != null ? customer.getLogin() : "");

            final TextField<String> loginInput = (TextField<String>) new TextField<String>(LOGIN_INPUT)
                    .setRequired(true);

            add(
                    loginInput
            );

            final PasswordTextField passwordTextField = new PasswordTextField(PASSWORD_INPUT);

            add(
                    passwordTextField
            );

            add(new HiddenField<Boolean>(CHANGE_USERNAME_HIDDEN));

            add(new Label(LOGIN_LABEL, getLogin() != null ? getLogin() : ""));

            add(new Button("changeBtn") {
                @Override
                public void onSubmit() {
                    setChangeUsername("1");
                }
            });

            add(new Button("cancelBtn") {
                @Override
                public void onSubmit() {
                    setChangeUsername(null);
                }
            }.setDefaultFormProcessing(false));

            add(new Button("saveBtn") {
                @Override
                public void onSubmit() {

                    if (customer != null && !customer.getLogin().equals(getLogin())) {
                        if (isCustomerExists(getLogin())) {
                            error(
                                    getLocalizer().getString("customerExists", this, new Model<Serializable>(new ValueMap(
                                            Collections.singletonMap("login", getLogin())
                                    )))
                            );
                        } else {
                            final Map<String, String> updateUsername = new HashMap<>();
                            updateUsername.put("newLogin", getLogin());
                            updateUsername.put("password", getPassword());
                            customerServiceFacade.updateCustomerAttributes(getCurrentShop(), customer, updateUsername);
                            setChangeUsername(null);
                            final Customer changed = customerServiceFacade.getCustomerByLogin(getCurrentShop(), getLogin());
                            if (changed != null && customer.getCustomerId() == changed.getCustomerId()) {
                                final Map<String, Object> cmd = new HashMap<>();
                                cmd.put(ShoppingCartCommand.CMD_LOGOUT, ShoppingCartCommand.CMD_LOGOUT);
                                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_LOGOUT, getCurrentCart(), cmd);
                                ((AbstractWebPage) getPage()).persistCartIfNecessary();
                                SummaryPanel.this.setResponsePage(Application.get().getHomePage());
                            } else {
                                error(
                                        getLocalizer().getString("passwordInvalid", this)
                                );
                            }
                        }
                    } else {
                        setChangeUsername(null);
                    }
                }
            });

        }

        @Override
        protected void onBeforeRender() {

            final boolean showEdit = StringUtils.isNotBlank(getChangeUsername());

            get(LOGIN_LABEL).setVisible(!showEdit);
            get(LOGIN_INPUT).setVisible(showEdit);
            get(PASSWORD_INPUT).setVisible(showEdit);
            get("changeBtn").setVisible(!showEdit);
            get("cancelBtn").setVisible(showEdit);
            get("saveBtn").setVisible(showEdit);

            super.onBeforeRender();

        }
    }

}
