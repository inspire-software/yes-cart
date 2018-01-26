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
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueWithAttribute;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.CheckoutPage;
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
 * Igor Azarny iazarny@yahoo.com
 * Date: 22/10/11
 * Time: 15:58
 */
public class RegisterPanel extends BaseComponent {

    private final long serialVersionUid = 20111016L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String EMAIL_INPUT = "email";
    private static final String REGISTER_BUTTON = "registerBtn";
    private static final String REGISTER_FORM = "registerForm";
    private static final String CUSTOMER_TYPE_FORM = "customerTypesForm";
    private static final String CUSTOMER_TYPE_RADIO_GROUP = "customerTypesRadioGroup";
    private final static String FIELDS = "fields";
    private final static String NAME = "name";
    private final static String EDITOR = "editor";
    private final static String CONTENT = "regformContent";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.WICKET_PAGES_MOUNTER)
    private WicketPagesMounter wicketPagesMounter;

    private final EditorFactory editorFactory = new EditorFactory();

    private final boolean isCheckout;

    private String customerType;

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

        final Shop shop = getCurrentShop();

        final List<Pair<String, I18NModel>> availableTypes = customerServiceFacade.getShopSupportedCustomerTypes(shop);

        customerType = !availableTypes.isEmpty() ? availableTypes.get(0).getFirst() : null;

        add(new RegisterForm(REGISTER_FORM, customerType, target.getFirst(), target.getSecond()));

        final Form typeForm = new Form(CUSTOMER_TYPE_FORM);

        final Component typeSelector = new RadioGroup<String>(
                CUSTOMER_TYPE_RADIO_GROUP,
                new PropertyModel<String>(RegisterPanel.this, "customerType")) {

            /**
             * {@inheritDoc}
             */
            protected void onSelectionChanged(final String descriptor) {
                // Change the form
                RegisterPanel.this.get(REGISTER_FORM).replaceWith(new RegisterForm(REGISTER_FORM, customerType, target.getFirst(), target.getSecond()));
            }


            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

        }.add(
                new ListView<Pair<String, I18NModel>>("customerTypeList", availableTypes) {
                    protected void populateItem(final ListItem<Pair<String, I18NModel>> customerTypeItem) {
                        customerTypeItem.add(new Radio<String>("customerTypeLabel", new Model<String>(customerTypeItem.getModelObject().getFirst())));
                        customerTypeItem.add(new Label("customerType", customerTypeItem.getModelObject().getSecond().getValue(getLocale().getLanguage())));
                    }
                }
        );

        typeForm.add(typeSelector).setVisible(availableTypes.size() > 1);

        add(typeForm);

        setVisible(!availableTypes.isEmpty());

    }

    @Override
    protected void onBeforeRender() {

        final String lang = getLocale().getLanguage();

        // Refresh content
        String regformInfo = getContentInclude(getCurrentShopId(), "registration_regform_content_include_" + customerType, lang);
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
            successfulPage = (Class) wicketPagesMounter.getPageProviderByUri("/checkout").get();
            parameters.set(
                    CheckoutPage.THREE_STEPS_PROCESS,
                    "true"
            ).set(
                    CheckoutPage.STEP,
                    CheckoutPage.STEP_ADDR
            );
        } else {
            successfulPage = (Class) wicketPagesMounter.getPageProviderByUri("/profile").get();
        }
        return new Pair<Class<? extends Page>, PageParameters>(successfulPage, parameters);
    }


    public class RegisterForm extends BaseAuthForm {

        private final long serialVersionUid = 20111016L;


        private String customerType;

        private String email;


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
         * Customer type.
         *
         * @return type
         */
        public String getCustomerType() {
            return customerType;
        }

        /**
         * Set customer type.
         *
         * @param customerType type
         */
        public void setCustomerType(final String customerType) {
            this.customerType = customerType;
        }

        /**
         * Construct form.
         *
         * @param id             form id.
         * @param successfulPage page to go in case of successful
         * @param parameters parameters
         */
        public RegisterForm(final String id,
                            final String customerType,
                            final Class<? extends Page> successfulPage,
                            final PageParameters parameters) {

            super(id);

            setModel(new CompoundPropertyModel<RegisterForm>(RegisterForm.this));


            setCustomerType(customerType);

            add(
                    new TextField<String>(EMAIL_INPUT)
                            .setRequired(true)
                            .add(StringValidator.lengthBetween(MIN_LEN, MAX_LEN))
                            .add(EmailAddressValidator.getInstance())
            );

            RepeatingView fields = new RepeatingView(FIELDS);

            add(fields);

            final String lang = getLocale().getLanguage();
            final List<AttrValueWithAttribute> reg = getCustomerServiceFacade()
                    .getShopRegistrationAttributes(getCurrentShop(), customerType);

            for (final AttrValueWithAttribute attrValue : reg) {

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
                                //getCustomerService().resetPassword(customer, getCurrentShop());

                            } else {

                                final Map<String, Object> data = new HashMap<String, Object>();
                                // Data is now relayed via custom attributes, so we can sort and arrange all attributes on
                                // registration form.
                                // data.put("firstname", getFirstname());
                                // data.put("lastname", getLastname());
                                // data.put("phone", getPhone());
                                data.put("customerType", customerType); // Type is required for registration

                                for (final AttrValueWithAttribute av : reg) {
                                    if (StringUtils.isNotBlank(av.getVal())) {
                                        data.put(av.getAttributeCode(), av.getVal());
                                    }
                                }


                                final String password = getCustomerServiceFacade().registerCustomer(
                                        getCurrentShop(), email, data);

                                if (signIn(getEmail(), password)) {

                                    ((AbstractWebPage) getPage()).executeHttpPostedCommands();
                                    ((AbstractWebPage) getPage()).persistCartIfNecessary();
                                    setResponsePage(successfulPage, parameters);

                                } else if (isCustomerExists(getEmail())) {

                                    final Class<IRequestablePage> pendingPage = wicketPagesMounter.getPageProviderByUri("/login").get();
                                    final PageParameters parameters = new PageParameters();
                                    parameters.set("pending", "1");
                                    setResponsePage(pendingPage, parameters);

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


    private Label getLabel(final AttrValueWithAttribute attrValue, final String lang) {

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
    protected Component getEditor(final AttrValueWithAttribute attrValue, final Boolean readOnly) {

        return editorFactory.getEditor(EDITOR, this, getLocale().getLanguage(), attrValue, readOnly);
    }


}
