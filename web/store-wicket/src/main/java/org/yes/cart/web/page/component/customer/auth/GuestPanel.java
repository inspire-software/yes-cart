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
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueCustomer;
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
 * Denis Pavlov
 * Date: 9/02/16
 */
public class GuestPanel extends BaseComponent {

    private final long serialVersionUid = 20111016L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String EMAIL_INPUT = "email";
    private static final String GUEST_BUTTON = "guestBtn";
    private static final String GUEST_FORM = "guestForm";
    private final static String FIELDS = "fields";
    private final static String NAME = "name";
    private final static String EDITOR = "editor";
    private final static String CONTENT = "guestformContent";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.WICKET_PAGES_MOUNTER)
    private WicketPagesMounter wicketPagesMounter;

    private final EditorFactory editorFactory = new EditorFactory();

    /**
     * Create guest panel.
     *
     * @param id         component id.
     */
    public GuestPanel(final String id) {

        super(id);

        final Pair<Class<? extends Page>, PageParameters> target = determineRedirectTarget();

        final Shop shop = getCurrentShop();

        final boolean guestSupported = customerServiceFacade.isShopGuestCheckoutSupported(shop);

        add(new GuestForm(GUEST_FORM, AttributeNamesKeys.Cart.CUSTOMER_TYPE_GUEST, target.getFirst(), target.getSecond()));

        setVisible(guestSupported);

    }

    @Override
    protected void onBeforeRender() {

        final String lang = getLocale().getLanguage();

        // Refresh content
        String regformInfo = getContentInclude(getCurrentShopId(), "registration_guestform_content_include", lang);
        get(GUEST_FORM).get(CONTENT).replaceWith(new Label(CONTENT, regformInfo).setEscapeModelStrings(false));

        super.onBeforeRender();
    }

    /**
     * Extension hook to override classes for themes.
     *
     * @return redirect target
     */
    protected Pair<Class<? extends Page>, PageParameters> determineRedirectTarget() {

        final Class<? extends Page> successfulPage;
        final PageParameters parameters = new PageParameters();

        successfulPage = (Class) wicketPagesMounter.getPageProviderByUri("/checkout").get();
        parameters.set(
                CheckoutPage.THREE_STEPS_PROCESS,
                "true"
        ).set(
                CheckoutPage.STEP,
                CheckoutPage.STEP_ADDR
        );
        return new Pair<Class<? extends Page>, PageParameters>(successfulPage, parameters);
    }


    public class GuestForm extends BaseAuthForm {

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
        public GuestForm(final String id,
                         final String customerType,
                         final Class<? extends Page> successfulPage,
                         final PageParameters parameters) {

            super(id);

            setModel(new CompoundPropertyModel<GuestForm>(GuestForm.this));


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
            final List<AttrValueCustomer> reg = getCustomerServiceFacade()
                    .getShopRegistrationAttributes(getCurrentShop(), customerType);

            for (final AttrValue attrValue : reg) {

                WebMarkupContainer row = new WebMarkupContainer(fields.newChildId());

                row.add(getLabel(attrValue, lang));

                row.add(getEditor(attrValue, false));

                fields.add(row);

            }

            add(new Label(CONTENT, ""));


            add(
                    new Button(GUEST_BUTTON) {


                        @Override
                        public void onSubmit() {

                            final Map<String, Object> data = new HashMap<String, Object>();
                            // Data is now relayed via custom attributes, so we can sort and arrange all attributes on
                            // registration form.
                            // data.put("firstname", getFirstname());
                            // data.put("lastname", getLastname());
                            // data.put("phone", getPhone());
                            data.put("cartGuid", getCurrentCart().getGuid()); // Cart is required for registration
                            data.put("customerType", customerType); // Type is required for registration

                            for (final AttrValue av : reg) {
                                if (StringUtils.isNotBlank(av.getVal())) {
                                    data.put(av.getAttribute().getCode(), av.getVal());
                                }
                            }


                            final String guest = getCustomerServiceFacade().registerGuest(
                                    getCurrentShop(), email, data);

                            if (StringUtils.isNotBlank(guest)) {

                                ((AbstractWebPage) getPage()).executeHttpPostedCommands();
                                ((AbstractWebPage) getPage()).persistCartIfNecessary();
                                setResponsePage(successfulPage, parameters);

                            } else {

                                error(
                                        getLocalizer().getString("canNotGuestCheckout", this)
                                );

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
     * @param attrValue give {@link AttrValue}
     * @param readOnly  if true this component is read only
     *
     * @return editor
     */
    protected Component getEditor(final AttrValue attrValue, final Boolean readOnly) {

        return editorFactory.getEditor(EDITOR, this, getLocale().getLanguage(), attrValue, readOnly);
    }


}
