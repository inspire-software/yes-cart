package org.yes.cart.web.page.component.customer.auth;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 4:50 PM
 */
public class LoginForm extends BaseAuthForm {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String EMAIL_INPUT = "email";
    private static final String PASSWORD_INPUT = "password";
    private static final String RESTORE_PASSWORD_BUTTON = "restorePasswordBtn";
    private static final String LOGIN_LINK = "loginLink";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


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
                final Customer customer = getCustomerService().findCustomer(emailField.getInput());
                if (customer != null) {
                    getCustomerService().resetPassword(customer, ApplicationDirector.getCurrentShop());
                }
                setVisible(false);
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
                sendNewPasswordBtn.setVisible(false);
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
                }
            }

        };


        add(loginLink);
    }


}
