package org.yes.cart.web.page.component.customer.auth;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/17/11
 * Time: 9:37 PM
 */
public class LoginPanel extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String EMAIL_INPUT = "email";
    private static final String PASSWORD_INPUT = "password";
    private static final String RESTORE_PASSWORD_BUTTON = "restorePasswordBtn";
    private static final String LOGIN_LINK = "loginLink";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    /**
     * Construct login panel.
     *
     * @param id panel id
     */
    public LoginPanel(final String id) {
        super(id);
        add(
                new LoginForm("loginForm", null, null)
        );
    }


    public final class LoginForm extends BaseAuthForm {


        /**
         * Construct login form.
         *
         * @param id                       form id
         * @param successfulPage           go to given page in case of successful login
         * @param successfulPageParameters go to given page with given parameters in case of successful login
         */
        public LoginForm(final String id,
                         final Class<? extends Page> successfulPage,
                         final PageParameters successfulPageParameters) {

            super(id);

            final TextField emailField;
            final PasswordTextField passwordField;
            final Button sendNewPasswordBtn;

            final String email = ApplicationDirector.getShoppingCart().getCustomerEmail();

            final Model<String> emailModel;
            if (StringUtils.isBlank(email)) {
                emailModel = new Model<String>(StringUtils.EMPTY);
            } else {
                emailModel = new Model<String>(email);

            }

            emailField = new TextField(EMAIL_INPUT, emailModel);
            emailField.setRequired(true)
                    .add(StringValidator.lengthBetween(MIN_LEN, MAX_LEN))
                    .add(EmailAddressValidator.getInstance());

            passwordField = new PasswordTextField(PASSWORD_INPUT, new Model<String>(StringUtils.EMPTY));
            passwordField.setRequired(true);

            sendNewPasswordBtn = new Button(RESTORE_PASSWORD_BUTTON) {
                @Override
                public void onSubmit() {
                    /*final Customer customer = getCustomerService().findCustomer(emailField.getInput());
                  if (customer != null) {
                      getCustomerService().resetPassword(customer, ApplicationDirector.getCurrentShop());
                  }
                  setVisible(false);  */
                }
            };

            sendNewPasswordBtn.setVisible(false);
            sendNewPasswordBtn.setDefaultFormProcessing(false);

            add(emailField);
            add(passwordField);
            add(sendNewPasswordBtn);


            final SubmitLink loginLink = new SubmitLink(LOGIN_LINK) {
                @Override
                public void onSubmit() {

                    IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
                            .getAuthenticationStrategy();

                    if (signIn(emailField.getInput(), passwordField.getInput())) {
                        strategy.save(emailField.getInput(), passwordField.getInput());
                        getPage().setResponsePage(successfulPage, successfulPageParameters);
                    } else {
                        sendNewPasswordBtn.setVisible(true);
                        error(
                                getLocalizer().getString("wrongPassword", this)
                        ); //todo register

                    }


                    /*sendNewPasswordBtn.setVisible(false);
                    if (isCustomerExists(emailField.getInput())) {
                        if (isPasswordValid(emailField.getInput(), passwordField.getInput())) {


                            final ShoppingCart visitableShoppingCart = ApplicationDirector.getShoppingCart();

                            executeLoginCommand(visitableShoppingCart, emailField.getInput());

                            getPage().setResponsePage(successfulPage, successfulPageParameters);

                        } else {
                            sendNewPasswordBtn.setVisible(true);
                            error(
                                    getLocalizer().getString("wrongPassword", this)
                            );
                        }
                    } else {
                        error(
                                getLocalizer().getString("customerNotExists", this)
                        );
                    }*/
                }

            };


            add(loginLink);
        }

        /**
         * Sign in user if possible.
         *
         * @param username The username
         * @param password The password
         * @return True if signin was successful
         */
        private boolean signIn(final String username, final String password) {
            return AuthenticatedWebSession.get().signIn(username, password);
        }


    }


}
