/*
 * Copyright 2009 Inspire-Software.com
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.order.impl.CustomerTypeAdapter;
import org.yes.cart.utils.ShopCodeContext;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.CheckoutPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.customer.dynaform.EditorFactory;
import org.yes.cart.web.page.component.util.CountryModel;
import org.yes.cart.web.page.component.util.CountryRenderer;
import org.yes.cart.web.page.component.util.StateModel;
import org.yes.cart.web.page.component.util.StateRenderer;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.AddressBookFacade;
import org.yes.cart.web.support.service.ContentServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;
import org.yes.cart.web.theme.WicketPagesMounter;

import java.util.*;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 22/10/11
 * Time: 15:58
 */
public class RegisterPanel extends BaseComponent {

    private final long serialVersionUid = 20111016L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String REGISTER_BUTTON = "registerBtn";
    private static final String REGISTER_FORM = "registerForm";
    private static final String CUSTOMER_TYPE_FORM = "customerTypesForm";
    private static final String CUSTOMER_TYPE_RADIO_GROUP = "customerTypesRadioGroup";
    private static final String FIELDS = "fields";
    private static final String NAME = "name";
    private static final String EDITOR = "editor";
    private static final String COUNTRY = "country";
    private static final String STATE = "state";
    private static final String CONTENT = "regformContent";

    private static final String REG_FORM_CODE = "regAddressForm";

    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.ADDRESS_BOOK_FACADE)
    private AddressBookFacade addressBookFacade;

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
                new PropertyModel<>(RegisterPanel.this, "customerType")) {{
                    add(new FormComponentUpdatingBehavior() {
                        @Override
                        protected void onUpdate() {
                            // Change the form
                            RegisterPanel.this.get(REGISTER_FORM).replaceWith(new RegisterForm(REGISTER_FORM, customerType, target.getFirst(), target.getSecond()));
                        }
                    });

        }}.add(
                new ListView<Pair<String, I18NModel>>("customerTypeList", availableTypes) {
                    @Override
                    protected void populateItem(final ListItem<Pair<String, I18NModel>> customerTypeItem) {
                        customerTypeItem.add(new Radio<>("customerTypeLabel", new Model<>(customerTypeItem.getModelObject().getFirst())));
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
        return new Pair<>(successfulPage, parameters);
    }


    public class RegisterForm extends BaseAuthForm {

        private final long serialVersionUid = 20111016L;


        private String customerType;

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

            setModel(new CompoundPropertyModel<>(RegisterForm.this));


            setCustomerType(customerType);

            RepeatingView fields = new RepeatingView(FIELDS);

            add(fields);

            final String addressType = Address.ADDR_TYPE_SHIPPING;
            final String lang = getLocale().getLanguage();
            final List<AttrValueWithAttribute> allReg = new ArrayList<>();
            final List<AttrValueWithAttribute> regProf = getCustomerServiceFacade()
                    .getShopRegistrationAttributes(getCurrentShop(), customerType);

            final Map<String, AttrValueWithAttribute> countryAndState = new HashMap<>();

            for (final AttrValueWithAttribute attrValue : regProf) {
                if (REG_FORM_CODE.equals(attrValue.getAttributeCode())) {
                    final List<AttrValueWithAttribute> regAddressForm = addressBookFacade
                            .getShopCustomerAddressAttributes(new CustomerTypeAdapter(customerType),
                                    getCurrentShop(), addressType);
                    if (CollectionUtils.isNotEmpty(regAddressForm)) {
                        for (final AttrValueWithAttribute attrValueAddr : regAddressForm) {
                            // include address form attributes but prefix them with address form code
                            attrValueAddr.setAttributeCode(REG_FORM_CODE.concat(".").concat(attrValueAddr.getAttributeCode()));
                            if ("stateCode".equals(attrValueAddr.getAttribute().getVal())) {
                                countryAndState.put("stateCode", attrValueAddr);
                            } else if ("countryCode".equals(attrValueAddr.getAttribute().getVal())) {
                                countryAndState.put("countryCode", attrValueAddr);
                            }
                            allReg.add(attrValueAddr);
                        }
                    }
                } else {
                    allReg.add(attrValue);
                }
            }

            final List<Country> countryList = addressBookFacade.getAllCountries(ShopCodeContext.getShopCode(), addressType);
            final List<State> stateList = CollectionUtils.isNotEmpty(countryList) ?
                    addressBookFacade.getStatesByCountry(countryList.get(0).getCountryCode()) : Collections.emptyList();


            final AbstractChoice<State, State> stateDropDownChoice;
            if (countryAndState.containsKey("stateCode")) {
                 stateDropDownChoice = new DropDownChoice<>(
                        STATE,
                        new StateModel(new PropertyModel(countryAndState.get("stateCode"), "val"), stateList),
                        stateList).setChoiceRenderer(new StateRenderer(getLocale().getLanguage()));
                final boolean needState = !stateList.isEmpty();
                stateDropDownChoice.setRequired(needState);
                stateDropDownChoice.setVisible(needState);
            } else {
                stateDropDownChoice = null;
            }

            final AbstractChoice<Country, Country> countryDropDownChoice;
            if (countryAndState.containsKey("countryCode")) {
                countryDropDownChoice = new DropDownChoice<Country>(
                        COUNTRY,
                        new CountryModel(new PropertyModel(countryAndState.get("countryCode"), "val"), countryList),
                        countryList);
                countryDropDownChoice.add(new FormComponentUpdatingBehavior() {
                    @Override
                    protected void onUpdate() {
                        final String countryCode = countryAndState.get("countryCode").getVal();
                        stateDropDownChoice.setChoices(addressBookFacade.getStatesByCountry(countryCode));
                        final boolean needState = !stateDropDownChoice.getChoices().isEmpty();
                        stateDropDownChoice.setRequired(needState);
                        stateDropDownChoice.setVisible(needState);
                        if (countryAndState.containsKey("stateCode")) {
                            countryAndState.get("stateCode").setVal(StringUtils.EMPTY);
                        }
                    }
                });
                countryDropDownChoice.setChoiceRenderer(new CountryRenderer(getLocale().getLanguage()));
                countryDropDownChoice.setRequired(true);
            } else {
                countryDropDownChoice = null;
            }


            for (final AttrValueWithAttribute attrValue : allReg) {

                WebMarkupContainer row;

                if ("countryCode".equals(attrValue.getAttribute().getVal())) {
                    row = new WebMarkupContainer(fields.newChildId());
                    row.add(getLabel(attrValue, lang));
                    row.add(new Fragment("editor", "countryCodeEditor", RegisterPanel.this).add(countryDropDownChoice));
                } else if ("stateCode".equals(attrValue.getAttribute().getVal())) {
                    row = new WebMarkupContainer(fields.newChildId()) {
                        @Override
                        public boolean isVisible() {
                            return stateDropDownChoice.isVisible();
                        }
                    };
                    row.add(getLabel(attrValue, lang));
                    row.add(new Fragment("editor", "stateCodeEditor", RegisterPanel.this).add(stateDropDownChoice));
                } else {
                    row = new WebMarkupContainer(fields.newChildId());
                    row.add(getLabel(attrValue, lang));
                    row.add(getEditor(attrValue, false));
                }

                fields.add(row);

            }

            add(new Label(CONTENT, ""));


            add(
                    new Button(REGISTER_BUTTON) {


                        @Override
                        public void onSubmit() {

                            final Map<String, Object> data = new HashMap<>();
                            // Data is now relayed via custom attributes, so we can sort and arrange all attributes on
                            // registration form.
                            // data.put("firstname", getFirstname());
                            // data.put("lastname", getLastname());
                            // data.put("phone", getPhone());
                            data.put("customerType", customerType); // Type is required for registration

                            String userPass = null;
                            String confirmPass = null;
                            for (final AttrValueWithAttribute av : allReg) {
                                if (StringUtils.isNotBlank(av.getVal())) {
                                    data.put(av.getAttributeCode(), av.getVal());
                                }
                                if ("password".equals(av.getAttribute().getVal())) {
                                    userPass = av.getVal();
                                    data.remove(av.getAttributeCode());
                                    data.put("password", userPass);
                                } else if ("confirmPassword".equals(av.getAttribute().getVal())) {
                                    confirmPass = av.getVal();
                                    data.remove(av.getAttributeCode());
                                    data.put("confirmPassword", confirmPass);
                                }
                            }

                            if (StringUtils.isNotBlank(userPass) && !userPass.equals(confirmPass)) {

                                error(
                                        getLocalizer().getString("passwordDoesNotMatch", this)
                                );

                            } else {

                                final CustomerServiceFacade.RegistrationResult reg = getCustomerServiceFacade().registerCustomer(
                                        getCurrentShop(), data);


                                if (reg.isDuplicate()) {

                                    error(
                                            getLocalizer().getString("customerExists", this)
                                    );

                                } else if (!reg.isSuccess()) {

                                    error(
                                            getLocalizer().getString("canNotRegister", this)
                                    );

                                } else {

                                    final String login = reg.getCustomer().getLogin();
                                    final String passw = reg.getRawPassword();


                                    if (signIn(login, passw)) {

                                        ((AbstractWebPage) getPage()).executeHttpPostedCommands();
                                        ((AbstractWebPage) getPage()).persistCartIfNecessary();
                                        setResponsePage(successfulPage, parameters);

                                    } else if (isCustomerExists(login)) {

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

        return new Label(NAME, new IModel<String>() {

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
     * @param readOnly  if true this component is read only
     *
     * @return editor
     */
    protected Component getEditor(final AttrValueWithAttribute attrValue, final Boolean readOnly) {

        return editorFactory.getEditor(EDITOR, this, getLocale().getLanguage(), attrValue, readOnly);
    }


}
