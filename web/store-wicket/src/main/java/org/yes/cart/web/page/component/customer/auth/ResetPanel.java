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
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.yes.cart.domain.entity.AttrValueWithAttribute;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.customer.dynaform.EditorFactory;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;
import org.yes.cart.web.theme.WicketPagesMounter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denis pavlov
 */
public class ResetPanel extends BaseComponent {

    private final long serialVersionUid = 20111016L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String AUTH_INPUT = "token";
    private static final String PASSWORD_LABEL = "passwordLabel";
    private static final String PASSWORD_INPUT = "password";
    private static final String PASSWORD_LABEL_2 = "passwordLabel2";
    private static final String PASSWORD_INPUT_2 = "password2";
    private static final String RESET_BUTTON = "resetBtn";
    private static final String RESET_FORM = "resetForm";
    private static final String CONTENT = "resetformContent";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.WICKET_PAGES_MOUNTER)
    private WicketPagesMounter wicketPagesMounter;

    private final EditorFactory editorFactory = new EditorFactory();

    /**
     * Construct login panel.
     *
     * @param id panel id
     */
    public ResetPanel(final String id, final String token) {
        super(id);

        final Customer customer = StringUtils.isNotBlank(token) ? customerServiceFacade.getCustomerByToken(token) : null;
        if (customer == null) {
            error(getLocalizer().getString("newPasswordInvalidToken", this));
        }
        final List<AttrValueWithAttribute> passwordConfig = customerServiceFacade.getShopRegistrationAttributes(getCurrentShop(), customer != null ? customer.getCustomerType() : null, true);
        AttrValueWithAttribute passAttr = null;
        AttrValueWithAttribute confirmPassAttr = null;
        for (final AttrValueWithAttribute av : passwordConfig) {
            if ("password".equals(av.getAttribute().getVal())) {
                passAttr = av;
            } else if ("confirmPassword".equals(av.getAttribute().getVal())) {
                confirmPassAttr = av;
            }
        }

        add(new ResetForm(RESET_FORM, passAttr, confirmPassAttr, token).setVisible(customer != null));

    }

    @Override
    protected void onBeforeRender() {

        final String lang = getLocale().getLanguage();

        String loginformInfo = getContentInclude(getCurrentShopId(), "reset_resetform_content_include", lang);
        get(RESET_FORM).get(CONTENT).replaceWith(new Label(CONTENT, loginformInfo).setEscapeModelStrings(false));

        super.onBeforeRender();
    }


    public final class ResetForm extends BaseAuthForm {

        private String token;
        private String password;
        private String password2;

        public String getToken() {
            return token;
        }

        public void setToken(final String token) {
            this.token = token;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPassword2() {
            return password2;
        }

        public void setPassword2(final String password2) {
            this.password2 = password2;
        }

        /**
         * Construct login form.
         *
         * @param id              form id
         * @param passAttr        password config
         * @param confirmPassAttr password config
         * @param token           auth token
         */
        public ResetForm(final String id,
                         final AttrValueWithAttribute passAttr,
                         final AttrValueWithAttribute confirmPassAttr,
                         final String token) {

            super(id);

            setModel(new CompoundPropertyModel<>(ResetForm.this));

            final String lang = getLocale().getLanguage();

            add(getLabel(passAttr, PASSWORD_LABEL, lang));
            add(getEditor(passAttr, PASSWORD_INPUT));
            add(getLabel(confirmPassAttr, PASSWORD_LABEL_2, lang));
            add(getEditor(confirmPassAttr, PASSWORD_INPUT_2));


            add(
                    new Button(RESET_BUTTON) {

                        @Override
                        public void onSubmit() {

                            if (StringUtils.isNotBlank(passAttr.getVal()) && passAttr.getVal().equals(confirmPassAttr.getVal())) {

                                try {
                                    final Map<String, Object> resetPassword = new HashMap<>();
                                    resetPassword.put(ShoppingCartCommand.CMD_RESET_PASSWORD, token);
                                    resetPassword.put(ShoppingCartCommand.CMD_RESET_PASSWORD_PW, passAttr.getVal());
                                    ((AbstractWebPage) getPage()).executeCommands(resetPassword);

                                    getPage().setResponsePage(wicketPagesMounter.getLoginPageProvider().get());

                                } catch (BadCredentialsException bce) {

                                    error(getLocalizer().getString("newPasswordInvalidToken", this));

                                }

                            } else {

                                error(getLocalizer().getString("passwordDoesNotMatch", this));

                            }

                        }

                    }
            );

            add(new Label(CONTENT, ""));

        }
    }

    private Label getLabel(final AttrValueWithAttribute attrValue, final String id, final String lang) {

        if (attrValue == null) {
            return new Label(id);
        }

        final I18NModel model = getI18NSupport().getFailoverModel(
                attrValue.getAttribute().getDisplayName(),
                attrValue.getAttribute().getName());

        return new Label(id, new AbstractReadOnlyModel<String>() {

            private final I18NModel m = model;

            @Override
            public String getObject() {
                final String lang1 = getLocale().getLanguage();
                return m.getValue(lang1);
            }
        });
    }


    /**
     * Get the particular editor for given attribute value. Type of editor depends from type of attribute value.
     *
     * @param attrValue give {@link org.yes.cart.domain.entity.AttrValue}
     * @param id  field id
     *
     * @return editor
     */
    protected Component getEditor(final AttrValueWithAttribute attrValue, final String id) {

        if (attrValue == null) {
            return new PasswordTextField(id);
        }

        return editorFactory.getEditor(id, this, getLocale().getLanguage(), attrValue, false);
    }



    private String getContentInclude(long shopId, String contentUri, String lang) {
        String content = contentServiceFacade.getContentBody(contentUri, shopId, lang);
        if (content == null) {
            content = "";
        }
        return content;
    }

}
