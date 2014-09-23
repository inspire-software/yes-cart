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

package org.yes.cart.web.page.component.customer.auth;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.CheckoutPage;
import org.yes.cart.web.page.RegistrationPage;
import org.yes.cart.web.page.component.BaseComponent;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/17/11
 * Time: 9:37 PM
 */
public class LoginPanel extends BaseComponent {

    private final long serialVersionUid = 20111016L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String EMAIL_INPUT = "email";
    private static final String PASSWORD_INPUT = "password";
    private static final String RESTORE_PASSWORD_HIDDEN = "restorePassword";
    private static final String RESTORE_PASSWORD_BUTTON = "restorePasswordBtn";
    private static final String LOGIN_BUTTON = "loginBtn";
    private static final String REGISTRATION_LINK = "registrationLink";
    private static final String LOGIN_FORM = "loginForm";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    private final boolean isCheckout;


    /**
     * Construct login panel.
     *
     * @param id panel id
     * @param isCheckout is we are on checkout
     */
    public LoginPanel(final String id, final boolean isCheckout) {
        super(id);
        this.isCheckout = isCheckout;

        final Class<? extends Page> successfulPage;
        final PageParameters parameters = new PageParameters();

        if (isCheckout) {
            successfulPage = CheckoutPage.class;
            parameters.set(
                    CheckoutPage.THREE_STEPS_PROCESS,
                    "true"
            ).set(
                    CheckoutPage.STEP,
                    CheckoutPage.STEP_ADDR
            );
        } else {
            successfulPage = Application.get().getHomePage();

        }

        add(
                new LoginForm(LOGIN_FORM, successfulPage, parameters)
        );
    }


    public final class LoginForm extends BaseAuthForm {

        private String email;
        private String password;
        private String restorePassword;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRestorePassword() {
            return restorePassword;
        }

        public void setRestorePassword(final String restorePassword) {
            this.restorePassword = restorePassword;
        }

        /**
         * Construct login form.
         *
         * @param id                       form id
         * @param successfulPage page to go in case of successful
         * @param parameters parameters
         */
        public LoginForm(final String id,
                         final Class<? extends Page> successfulPage,
                         final PageParameters parameters) {

            super(id);

            setModel(new CompoundPropertyModel<LoginForm>(LoginForm.this));


            add(
                    new BookmarkablePageLink<RegistrationPage>(REGISTRATION_LINK, RegistrationPage.class)
                        .setVisible(!isCheckout)
            );

            final TextField<String> emailInput = (TextField<String>) new TextField<String>(EMAIL_INPUT)
                    .setRequired(true)
                    .add(StringValidator.lengthBetween(MIN_LEN, MAX_LEN))
                    .add(EmailAddressValidator.getInstance());

            add(
                    emailInput
            );

            final PasswordTextField passwordTextField = (PasswordTextField) new PasswordTextField(PASSWORD_INPUT)
                    .setRequired(true);

            add(
                    passwordTextField
            );

            add(new HiddenField<Boolean>(RESTORE_PASSWORD_HIDDEN));

            final Button sendNewPasswordBtn = new Button(RESTORE_PASSWORD_BUTTON) {

                @Override
                public void onSubmit() {
                    final String email = getRestorePassword();
                    final Customer customer = getCustomerServiceFacade().getCustomerByEmail(email);
                    if (customer != null) {
                        getCustomerServiceFacade().resetPassword(ApplicationDirector.getCurrentShop(), customer);
                    }
                    ((AbstractWebPage) getPage()).executeHttpPostedCommands();
                    ((AbstractWebPage) getPage()).persistCartIfNecessary();

                    info(new StringResourceModel("emailSent", this, null, new Object[] {email}).getString());
                    emailInput.getModel().setObject(email);
                    setRestorePassword(null);

                }


            };


            sendNewPasswordBtn.setDefaultFormProcessing(false);

            add(
                    sendNewPasswordBtn
            );


            add(
                    new Button(LOGIN_BUTTON) {

                        @Override
                        public void onSubmit() {

                            if (signIn(getEmail(), getPassword())) {
                                final IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
                                        .getAuthenticationStrategy();
                                strategy.save(getEmail(), getPassword());
                                ((AbstractWebPage) getPage()).executeHttpPostedCommands();
                                ((AbstractWebPage) getPage()).persistCartIfNecessary();
                                if (!continueToOriginalDestination()) {
                                    setResponsePage(successfulPage, parameters);
                                }
                            } else {
                                final String email = getEmail();
                                if (isCustomerExists(email)) {
                                    setRestorePassword(email);

                                    error(getLocalizer().getString("wrongPassword", this));
                                    error(new StringResourceModel("tryToRestore", this, null, new Object[] {email}).getString());

                                } else {
                                    setRestorePassword(null);

                                    error(getLocalizer().getString("customerNotExists", this));
                                }
                            }
                        }

                    }
            );

        }


        @Override
        protected void onBeforeRender() {

            final boolean showResend = StringUtils.isNotBlank(getRestorePassword());

            get(RESTORE_PASSWORD_BUTTON).setVisible(showResend);

            super.onBeforeRender();
        }
    }


}
