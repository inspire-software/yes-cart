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
import java.util.Map;

/**
 * User: denis pavlov
 */
public class DeletePanel extends BaseComponent {

    private final long serialVersionUid = 20111016L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String AUTH_INPUT = "token";
    private static final String PASSWORD_LABEL = "passwordLabel";
    private static final String PASSWORD_INPUT = "password";
    private static final String DELETE_BUTTON = "deleteBtn";
    private static final String DELETE_FORM = "deleteForm";
    private static final String CONTENT = "deleteformContent";
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
    public DeletePanel(final String id, final String token) {
        super(id);

        final Customer customer = StringUtils.isNotBlank(token) ? customerServiceFacade.getCustomerByToken(token) : null;
        if (customer == null) {
            error(getLocalizer().getString("newPasswordInvalidToken", this));
        }

        add(new DeleteForm(DELETE_FORM, token).setVisible(customer != null));

    }

    @Override
    protected void onBeforeRender() {

        final String lang = getLocale().getLanguage();

        String loginformInfo = getContentInclude(getCurrentShopId(), "delete_deleteform_content_include", lang);
        get(DELETE_FORM).get(CONTENT).replaceWith(new Label(CONTENT, loginformInfo).setEscapeModelStrings(false));

        super.onBeforeRender();
    }


    public final class DeleteForm extends BaseAuthForm {

        private String token;
        private String password;

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

        /**
         * Construct login form.
         *
         * @param id              form id
         * @param token           auth token
         */
        public DeleteForm(final String id,
                          final String token) {

            super(id);

            setModel(new CompoundPropertyModel<>(DeleteForm.this));

            add(new PasswordTextField(PASSWORD_INPUT));


            add(
                    new Button(DELETE_BUTTON) {

                        @Override
                        public void onSubmit() {

                            try {
                                final Map<String, Object> deleteAccount = new HashMap<>();
                                deleteAccount.put(ShoppingCartCommand.CMD_DELETE_ACCOUNT, token);
                                deleteAccount.put(ShoppingCartCommand.CMD_DELETE_ACCOUNT_PW, password);
                                ((AbstractWebPage) getPage()).executeCommands(deleteAccount);

                                final Map<String, Object> logout = new HashMap<>();
                                logout.put(ShoppingCartCommand.CMD_LOGOUT, ShoppingCartCommand.CMD_LOGOUT);
                                ((AbstractWebPage) getPage()).executeCommands(logout);

                                getPage().setResponsePage(wicketPagesMounter.getLoginPageProvider().get());

                            } catch (BadCredentialsException bce) {

                                error(getLocalizer().getString("newPasswordInvalidToken", this));

                            }

                        }

                    }
            );

            add(new Label(CONTENT, ""));

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
