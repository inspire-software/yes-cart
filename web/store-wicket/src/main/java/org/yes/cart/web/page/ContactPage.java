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

package org.yes.cart.web.page;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.customer.auth.BaseAuthForm;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.HeaderMetaInclude;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.page.component.js.ServerSideJs;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: igora Igor Azarny
 * Date: 4/28/12
 * Time: 1:58 PM
 */
public class ContactPage  extends AbstractWebPage {


    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    /**
     * Construct contact information page.
     * @param params page parameters.
     */
    public ContactPage(final PageParameters params) {
        super(params);
    }

    /**
     * {@inheritDoc}
     */
    protected void onBeforeRender() {

        executeHttpPostedCommands();
        addOrReplace(new StandardFooter(FOOTER));
        addOrReplace(new StandardHeader(HEADER));
        addOrReplace(new ServerSideJs("serverSideJs"));
        addOrReplace(new HeaderMetaInclude("headerInclude"));
        addOrReplace(new ContactForm("contactForm"));
        addOrReplace(new FeedbackPanel("feedback"));

        final long shopId = ShopCodeContext.getShopId();
        final String lang = getLocale().getLanguage();
        String contactInfo = getContentInclude(shopId, "contacts_content_include", lang);
        addOrReplace(new Label("contactInfo", contactInfo));

        super.onBeforeRender();
        persistCartIfNecessary();
    }


    private String getContentInclude(long shopId, String contentUri, String lang) {
        String content = contentServiceFacade.getContentBody(contentUri, shopId, lang);
        if (content == null) {
            content = "";
        }
        return content;
    }


    /**
     * Get page title.
     *
     * @return page title
     */
    public IModel<String> getPageTitle() {
        return new Model<String>(getLocalizer().getString("contact",this));
    }


    public class ContactForm extends BaseAuthForm {

        private final long serialVersionUid = 20151204L;


        private String email;
        private String name;
        private String phone;
        private String subject;
        private String body;


        /**
         * Get email.
         *
         * @return email.
         */
        public String getEmail() {
            return email;
        }

        /**
         * Set email.
         *
         * @param email
         */
        public void setEmail(final String email) {
            this.email = email;
        }

        /**
         * Get name.
         *
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * Set name.
         *
         * @param name name.
         */
        public void setName(final String name) {
            this.name = name;
        }

        /**
         * Get phone.
         *
         * @return [hone
         */
        public String getPhone() {
            return phone;
        }

        /**
         * Set phone.
         *
         * @param phone phone .
         */
        public void setPhone(final String phone) {
            this.phone = phone;
        }

        /**
         * Get subject
         *
         * @return subject
         */
        public String getSubject() {
            return subject;
        }

        /**
         * Set subject.
         *
         * @param subject subject
         */
        public void setSubject(final String subject) {
            this.subject = subject;
        }

        /**
         * Get body.
         *
         * @return body
         */
        public String getBody() {
            return body;
        }

        /**
         * Set body.
         *
         * @param body body
         */
        public void setBody(final String body) {
            this.body = body;
        }

        /**
         * Construct form.
         *
         * @param id             form id.
         */
        public ContactForm(final String id) {

            super(id);

            setModel(new CompoundPropertyModel<ContactForm>(ContactForm.this));


            add(
                    new TextField<String>("email")
                            .setRequired(true)
                            .add(StringValidator.lengthBetween(MIN_LEN, MAX_LEN))
                            .add(EmailAddressValidator.getInstance())
            );

            add(
                    new TextField<String>("name")
                            .setRequired(true)
            );

            add(
                    new TextField<String>("subject")
                            .setRequired(true)
            );

            add(
                    new TextField<String>("phone")
                            .setRequired(true)
                            .add(StringValidator.lengthBetween(4, 13))
            );

            add(
                    new TextArea<String>("body")
                            .setRequired(true)
            );


            add(
                    new Button("sendBtn") {


                        @Override
                        public void onSubmit() {

                            final Map<String, Object> data = new HashMap<String, Object>();
                            data.put("name", getName());
                            data.put("phone", getPhone());
                            data.put("email", getEmail());
                            data.put("subject", getSubject());
                            data.put("body", getBody());

                            getCustomerServiceFacade().registerEmailRequest(
                                    ApplicationDirector.getCurrentShop(), email, data);

                            info(
                                    getLocalizer().getString("emailSend", this)
                            );

                            setEmail(null);
                            setPhone(null);
                            setName(null);
                            setSubject(null);
                            setBody(null);

                        }
                    }
            );

        }

    }



}
