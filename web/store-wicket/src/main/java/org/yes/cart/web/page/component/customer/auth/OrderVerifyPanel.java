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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;

/**
 * User: denis pavlov
 */
public class OrderVerifyPanel extends BaseComponent {

    private final long serialVersionUid = 20111016L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String EMAIL_INPUT = "email";
    private static final String VERIFY_BUTTON = "verifyBtn";
    private static final String VERIFY_FORM = "verifyForm";
    private static final String CONTENT = "verifyformContent";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    /**
     * Construct order verify panel.
     *
     * @param id panel id
     * @param check email check
     */
    public OrderVerifyPanel(final String id, final IModel<String> check) {
        super(id);
        add(new OrderVerifyForm(VERIFY_FORM, check));

    }

    @Override
    protected void onBeforeRender() {

        final String lang = getLocale().getLanguage();

        String loginformInfo = getContentInclude(getCurrentShopId(), "verify_verifyform_content_include", lang);
        get(VERIFY_FORM).get(CONTENT).replaceWith(new Label(CONTENT, loginformInfo).setEscapeModelStrings(false));

        super.onBeforeRender();
    }


    public final class OrderVerifyForm extends BaseAuthForm {

        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(final String email) {
            this.email = email;
        }

        /**
         * Construct login form.
         *
         * @param id              form id
         * @param check           auth model
         */
        public OrderVerifyForm(final String id,
                               final IModel<String> check) {

            super(id);

            setModel(new CompoundPropertyModel<>(OrderVerifyForm.this));

            add(new TextField(EMAIL_INPUT));


            add(
                    new Button(VERIFY_BUTTON) {

                        @Override
                        public void onSubmit() {

                            check.setObject(getEmail());

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
