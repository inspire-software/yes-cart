/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.yes.cart.domain.entity.AttrValueWithAttribute;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.CheckoutPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.customer.dynaform.editor.CustomPatternValidator;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;
import org.yes.cart.web.theme.WicketPagesMounter;
import org.yes.cart.web.util.WicketUtil;

import java.util.Collections;

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
    private static final String TRY_RESTORE_PASSWORD_BUTTON = "tryRestorePasswordBtn";
    private static final String CANCEL_RESTORE_PASSWORD_BUTTON = "cancelRestorePasswordBtn";
    private static final String LOGIN_BUTTON = "loginBtn";
    private static final String REGISTRATION_LINK = "registrationLink";
    private static final String LOGIN_FORM = "loginForm";
    private static final String CONTENT = "regformContent";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    private final boolean isCheckout;

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.WICKET_PAGES_MOUNTER)
    private WicketPagesMounter wicketPagesMounter;

    /**
     * Construct login panel.
     *
     * @param id panel id
     * @param isCheckout is we are on checkout
     */
    public LoginPanel(final String id, final boolean isCheckout) {
        super(id);
        this.isCheckout = isCheckout;

        final Pair<Class<? extends Page>, PageParameters> target = determineRedirectTarget(this.isCheckout);
        final AttrValueWithAttribute emailConfig = customerServiceFacade.getShopEmailAttribute(getCurrentShop());
        final IValidator<String> emailValidator;
        if (emailConfig != null && StringUtils.isNotBlank(emailConfig.getAttribute().getRegexp())) {
            final String regexError = new FailoverStringI18NModel(
                    emailConfig.getAttribute().getValidationFailedMessage(),
                    emailConfig.getAttribute().getCode()).getValue(getLocale().getLanguage());

            emailValidator = new CustomPatternValidator(emailConfig.getAttribute().getRegexp(), new Model<>(regexError));
        } else {
            emailValidator = EmailAddressValidator.getInstance();
        }
        add(new LoginForm(LOGIN_FORM, target.getFirst(), target.getSecond(), emailValidator));

    }

    @Override
    protected void onBeforeRender() {

        final String lang = getLocale().getLanguage();

        String loginformInfo = getContentInclude(getCurrentShopId(), "login_loginform_content_include", lang);
        get(LOGIN_FORM).get(CONTENT).replaceWith(new Label(CONTENT, loginformInfo).setEscapeModelStrings(false));

        super.onBeforeRender();
    }

    /**
     * Extension hook to override classes for themes.
     *
     * @param isCheckout where this is checkout registration
     *
     * @return redirect target
     */
    protected Pair<Class<? extends Page>, PageParameters> determineRedirectTarget(boolean isCheckout) {

        final Class<? extends Page> successfulPage;
        final PageParameters parameters = new PageParameters();

        if (isCheckout) {
            successfulPage = (Class) wicketPagesMounter.getPageProviderByUri("/checkout").get();
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
        return new Pair<>(successfulPage, parameters);
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
                         final PageParameters parameters,
                         final IValidator<String> emailValidator) {

            super(id);

            setModel(new CompoundPropertyModel<>(LoginForm.this));


            add(
                    new BookmarkablePageLink(REGISTRATION_LINK, (Class) wicketPagesMounter.getPageProviderByUri("/registration").get())
            );

            final TextField<String> emailInput = (TextField<String>) new TextField<String>(EMAIL_INPUT)
                    .setRequired(true)
                    .add(emailValidator);

            add(
                    emailInput
            );

            final PasswordTextField passwordTextField = new PasswordTextField(PASSWORD_INPUT);

            add(
                    passwordTextField
            );

            add(new HiddenField<Boolean>(RESTORE_PASSWORD_HIDDEN));

            final Button sendNewPasswordBtn = new Button(RESTORE_PASSWORD_BUTTON) {
                @Override
                public void onSubmit() {
                    final String email = getEmail();
                    final Customer customer = getCustomerServiceFacade().getCustomerByEmail(getCurrentShop(), email);
                    if (customer != null) {
                        getCustomerServiceFacade().resetPassword(getCurrentShop(), customer);
                        ((AbstractWebPage) getPage()).executeHttpPostedCommands();
                        ((AbstractWebPage) getPage()).persistCartIfNecessary();

                        info(WicketUtil.createStringResourceModel(this, "newPasswordRequestEmailSent",
                                Collections.singletonMap("email", email)).getString());
                        setRestorePassword(null);
                    } else {
                        error(getLocalizer().getString("customerNotExists", this));
                    }
                }
            };

            add(sendNewPasswordBtn);

            final Button trySendNewPasswordBtn = new Button(TRY_RESTORE_PASSWORD_BUTTON) {
                @Override
                public void onSubmit() {
                    setRestorePassword("1");
                }
            };

            trySendNewPasswordBtn.setDefaultFormProcessing(false);
            add(trySendNewPasswordBtn);

            final Button cancelSendNewPasswordBtn = new Button(CANCEL_RESTORE_PASSWORD_BUTTON) {
                @Override
                public void onSubmit() {
                    setRestorePassword(null);
                }
            };

            cancelSendNewPasswordBtn.setDefaultFormProcessing(false);
            add(cancelSendNewPasswordBtn);


            add(
                    new Button(LOGIN_BUTTON) {

                        @Override
                        public void onSubmit() {

                            if (signIn(getEmail(), getPassword())) {
                                ((AbstractWebPage) getPage()).executeHttpPostedCommands();
                                ((AbstractWebPage) getPage()).persistCartIfNecessary();
                                continueToOriginalDestination();
                                setResponsePage(successfulPage, parameters);
                            } else {
                                if (getCurrentCart().getLogonState() == ShoppingCart.INACTIVE_FOR_SHOP) {
                                    setRestorePassword(null);
                                    error(getLocalizer().getString("customerNotActiveInShop", this));
                                } else {
                                    final String email = getEmail();
                                    if (isCustomerExists(email)) {
                                        //setRestorePassword(email);
                                        setRestorePassword(null);
                                        error(getLocalizer().getString("wrongPassword", this));
                                        info(getLocalizer().getString("tryToRestore", this));
                                    } else {
                                        setRestorePassword(null);
                                        error(getLocalizer().getString("customerNotExists", this));
                                    }
                                }
                            }
                        }

                    }
            );

            add(new Label(CONTENT, ""));

        }


        @Override
        protected void onBeforeRender() {

            final boolean showResend = StringUtils.isNotBlank(getRestorePassword());

            // We are in login mode
            get(TRY_RESTORE_PASSWORD_BUTTON).setVisible(!showResend);
            get(PASSWORD_INPUT).setVisible(!showResend);
            get(LOGIN_BUTTON).setVisible(!showResend);

            final boolean showRegistration = !isCheckout &&
                    !getCustomerServiceFacade().getShopSupportedCustomerTypes(getCurrentShop()).isEmpty();

            get(REGISTRATION_LINK).setVisible(showRegistration && !showResend);

            // We are in reset password mode
            get(RESTORE_PASSWORD_BUTTON).setVisible(showResend);
            get(CANCEL_RESTORE_PASSWORD_BUTTON).setVisible(showResend);

            super.onBeforeRender();
        }
    }


    private String getContentInclude(long shopId, String contentUri, String lang) {
        String content = contentServiceFacade.getContentBody(contentUri, shopId, lang);
        if (content == null) {
            content = "";
        }
        return content;
    }

}
