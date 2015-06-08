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
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.CheckoutPage;
import org.yes.cart.web.page.ProfilePage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.customer.dynaform.EditorFactory;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 22/10/11
 * Time: 15:58
 */
public class RegisterPanel extends BaseComponent {

    private final long serialVersionUid = 20111016L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String EMAIL_INPUT = "email";
    private static final String FIRSTNAME_INPUT = "firstname";
    private static final String LASTNAME_INPUT = "lastname";
    private static final String PHONE_INPUT = "phone";
    private static final String REGISTER_BUTTON = "registerBtn";
    private static final String REGISTER_FORM = "registerForm";
    private final static String FIELDS = "fields";
    private final static String NAME = "name";
    private final static String EDITOR = "editor";
    private final static String CONTENT = "regformContent";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    private final EditorFactory editorFactory = new EditorFactory();

    private final boolean isCheckout;

    /**
     * Create register panel.
     *
     * @param id         component id.
     * @param isCheckout true if we are on checkout
     */
    public RegisterPanel(final String id, final boolean isCheckout) {

        super(id);

        this.isCheckout = isCheckout;

        final Pair<Class<? extends Page>, PageParameters> target = determineRedirectTarget(this.isCheckout);
        add(new RegisterForm(REGISTER_FORM, target.getFirst(), target.getSecond()));

    }

    @Override
    protected void onBeforeRender() {


        final long shopId = ShopCodeContext.getShopId();
        final String lang = getLocale().getLanguage();

        // Refresh content
        String regformInfo = getContentInclude(shopId, "registration_regform_content_include", lang);
        get(REGISTER_FORM).get(CONTENT).replaceWith(new Label(CONTENT, regformInfo).setEscapeModelStrings(false));

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
            successfulPage = CheckoutPage.class;
            parameters.set(
                    CheckoutPage.THREE_STEPS_PROCESS,
                    "true"
            ).set(
                    CheckoutPage.STEP,
                    CheckoutPage.STEP_ADDR
            );
        } else {
            successfulPage = ProfilePage.class;
        }
        return new Pair<Class<? extends Page>, PageParameters>(successfulPage, parameters);
    }


    public class RegisterForm extends BaseAuthForm {

        private final long serialVersionUid = 20111016L;


        private String email;
        private String firstname;
        private String lastname;
        private String phone;


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
         * Get first name.
         *
         * @return first name
         */
        public String getFirstname() {
            return firstname;
        }

        /**
         * Set first name.
         *
         * @param firstname first name.
         */
        public void setFirstname(final String firstname) {
            this.firstname = firstname;
        }


        /**
         * Get last name.
         *
         * @return last name.
         */
        public String getLastname() {
            return lastname;
        }

        /**
         * set Last name.
         *
         * @param lastname last name.
         */
        public void setLastname(final String lastname) {
            this.lastname = lastname;
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
         * Construct form.
         *
         * @param id             form id.
         * @param successfulPage page to go in case of successful
         * @param parameters parameters
         */
        public RegisterForm(final String id,
                            final Class<? extends Page> successfulPage,
                            final PageParameters parameters) {

            super(id);

            setModel(new CompoundPropertyModel<RegisterForm>(RegisterForm.this));


            add(
                    new TextField<String>(EMAIL_INPUT)
                            .setRequired(true)
                            .add(StringValidator.lengthBetween(MIN_LEN, MAX_LEN))
                            .add(EmailAddressValidator.getInstance())
            );

            add(
                    new TextField<String>(FIRSTNAME_INPUT)
                            .setRequired(true)
            );

            add(
                    new TextField<String>(LASTNAME_INPUT)
                            .setRequired(true)
            );

            add(
                    new TextField<String>(PHONE_INPUT)
                            .setRequired(true)
                            .add(StringValidator.lengthBetween(4, 13))
            );


            RepeatingView fields = new RepeatingView(FIELDS);

            add(fields);

            final String lang = getLocale().getLanguage();
            final List<AttrValueCustomer> reg = getCustomerServiceFacade()
                    .getShopRegistrationAttributes(ApplicationDirector.getCurrentShop());

            for (final AttrValue attrValue : reg) {

                WebMarkupContainer row = new WebMarkupContainer(fields.newChildId());

                row.add(getLabel(attrValue, lang));

                row.add(getEditor(attrValue, false));

                fields.add(row);

            }

            add(new Label(CONTENT, ""));


            add(
                    new Button(REGISTER_BUTTON) {


                        @Override
                        public void onSubmit() {

                            if (isCustomerExists(getEmail())) {

                                error(
                                        getLocalizer().getString("customerExists", this)
                                );

                                //and sent the new password to already existing user



                                //CPOINT
                                //this commented out, because of YC-168
                                //but it may be valid behavior for some clients.
                                //Customer customer = getCustomerService().getCustomerByEmail(getEmail());
                                //getCustomerService().resetPassword(customer, ApplicationDirector.getCurrentShop());

                            } else {

                                final Map<String, Object> data = new HashMap<String, Object>();
                                data.put("firstname", getFirstname());
                                data.put("lastname", getLastname());
                                data.put("phone", getPhone());

                                for (final AttrValue av : reg) {
                                    if (StringUtils.isNotBlank(av.getVal())) {
                                        data.put(av.getAttribute().getCode(), av.getVal());
                                    }
                                }


                                final String password = getCustomerServiceFacade().registerCustomer(
                                        ApplicationDirector.getCurrentShop(), email, data);

                                if (signIn(getEmail(), password)) {

                                    ((AbstractWebPage) getPage()).executeHttpPostedCommands();
                                    ((AbstractWebPage) getPage()).persistCartIfNecessary();
                                    setResponsePage(successfulPage, parameters);

                                } else {

                                    error(
                                            getLocalizer().getString("canNotRegister", this)
                                    );

                                }
                            }
                        }
                    }
            );

        }

    }


    private String getContentInclude(long shopId, String contentUri, String lang) {
        String content = contentServiceFacade.getContentBody(contentUri, shopId, lang);
        if (content == null) {
            content = "";
        }
        return content;
    }


    private Label getLabel(final AttrValue attrValue, final String lang) {

        final I18NModel model = getI18NSupport().getFailoverModel(
                attrValue.getAttribute().getDisplayName(),
                attrValue.getAttribute().getName());

        final Label rez = new Label(NAME, new AbstractReadOnlyModel<String>() {

            private final I18NModel m = model;

            @Override
            public String getObject() {
                final String lang = getLocale().getLanguage();
                return m.getValue(lang);
            }
        });

        return rez;
    }


    /**
     * Get the particular editor for given attribute value. Type of editor depends from type of attribute value.
     *
     * @param attrValue give {@link org.yes.cart.domain.entity.AttrValue}
     * @param readOnly  if true this component is read only
     *
     * @return editor
     */
    protected Component getEditor(final AttrValue attrValue, final Boolean readOnly) {

        return editorFactory.getEditor(EDITOR, this, getLocale().getLanguage(), attrValue, readOnly);
    }


}
