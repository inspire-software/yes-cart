package org.yes.cart.web.page.component.customer.auth;

import org.apache.wicket.Page;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.*;
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
            successfulPage = HomePage.class;
        }

        add(
                new LoginForm(LOGIN_FORM, successfulPage, parameters)
        );
    }


    public final class LoginForm extends BaseAuthForm {

        private String email;
        private String password;

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

            final Button sendNewPasswordBtn = new Button(RESTORE_PASSWORD_BUTTON) {

                @Override
                public void onSubmit() {
                    final Customer customer = getCustomerService().findCustomer(getEmail());
                    if (customer != null) {
                        getCustomerService().resetPassword(customer, ApplicationDirector.getCurrentShop());
                    }
                    ((AbstractWebPage) getPage()).processCommands();

                    if(isCheckout) {
                        info("New password was sent. Check email please."); //todo localization
                    } else {
                        setResponsePage(HomePage.class);
                    }

                }


            };

            sendNewPasswordBtn.setDefaultFormProcessing(false)
                    .setVisible(false);

            add(
                    sendNewPasswordBtn
            );

            add(
                    new BookmarkablePageLink<RegistrationPage>(REGISTRATION_LINK, RegistrationPage.class)
                        .setVisible(!isCheckout)
            );

            add(
                    new TextField<String>(EMAIL_INPUT)
                            .setRequired(true)
                            .add(StringValidator.lengthBetween(MIN_LEN, MAX_LEN))
                            .add(EmailAddressValidator.getInstance())
            );
            add(
                    new PasswordTextField(PASSWORD_INPUT)
                            .setRequired(true)
            );

            add(
                    new Button(LOGIN_BUTTON) {

                        @Override
                        public void onSubmit() {

                            if (signIn(getEmail(), getPassword())) {
                                final IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
                                        .getAuthenticationStrategy();
                                strategy.save(getEmail(), getPassword());
                                if (!continueToOriginalDestination()) {
                                    setResponsePage(successfulPage, parameters);
                                }
                            } else {
                                if (isCustomerExists(getEmail())) {
                                    sendNewPasswordBtn.setVisible(true);
                                    error("Try to restore password ");
                                } else {
                                    sendNewPasswordBtn.setVisible(false);
                                    error("Register ");
                                }
                            }
                        }

                    }
            );

        }





    }


}
