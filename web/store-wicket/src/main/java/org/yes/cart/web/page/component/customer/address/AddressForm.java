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

package org.yes.cart.web.page.component.customer.address;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.domain.entity.State;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.customer.dynaform.EditorFactory;
import org.yes.cart.web.page.component.util.CountryModel;
import org.yes.cart.web.page.component.util.CountryRenderer;
import org.yes.cart.web.page.component.util.StateModel;
import org.yes.cart.web.page.component.util.StateRenderer;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.i18n.I18NWebSupport;
import org.yes.cart.web.support.service.AddressBookFacade;
import org.yes.cart.web.util.WicketUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Form to create / edit customer address.
 * Igor Azarny iazarny@yahoo.com
 * Date: 12/10/11
 * Time: 18:01
 */
public class AddressForm extends Form<Address> {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String FIELDS = "fields";
    private final static String NAME = "name";
    private final static String EDITOR = "editor";
    private final static String COUNTRY = "country";
    private final static String STATE = "state";
    private final static String ADD_ADDRESS = "addAddress";
    private final static String CANCEL_LINK = "cancel";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    private final static int SMALL_LENGTH = 16;
    private final static int MEDIUM_LENGTH = 128;
    private final static int LARGE_LENGTH = 256;

    @SpringBean(name = StorefrontServiceSpringKeys.ADDRESS_BOOK_FACADE)
    private AddressBookFacade addressBookFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @SpringBean(name = StorefrontServiceSpringKeys.I18N_SUPPORT)
    private I18NWebSupport i18NWebSupport;

    private final EditorFactory editorFactory = new EditorFactory();

    private final Class<? extends Page> successPage;
    private final PageParameters successPageParameters;

    private final List<AttrValue> values;

    /**
     * Create address form.
     *
     * @param s                      form id
     * @param addressIModel          address model.
     * @param addressType            address type
     * @param successPage            success page class
     * @param successPageParameters  success page parameters
     * @param cancelPage             optional cancel page class
     * @param cancelPageParameters   optional  cancel page parameters
     */
    public AddressForm(final String s,
                       final IModel<Address> addressIModel,
                       final String addressType,
                       final Class<? extends Page> successPage,
                       final PageParameters successPageParameters,
                       final Class<? extends Page> cancelPage,
                       final PageParameters cancelPageParameters) {

        super(s, addressIModel);

        this.successPage = successPage;
        this.successPageParameters = successPageParameters;


        final Address address = addressIModel.getObject();

        preprocessAddress(address);
        final boolean creating = address.getAddressId() == 0L;

        final List<State> stateList = getStateList(address.getCountryCode());
        final List<Country> countryList = addressBookFacade.getAllCountries(ShopCodeContext.getShopCode(), addressType);


        RepeatingView fields = new RepeatingView(FIELDS);

        add(fields);

        final String lang = getLocale().getLanguage();
        values = addressBookFacade
                .getShopCustomerAddressAttributes(address.getCustomer(), ApplicationDirector.getCurrentShop());


        final Map<String, AttrValue> valuesMap = new HashMap<String, AttrValue>();
        for (final AttrValue av : values) {
            valuesMap.put(av.getAttribute().getVal(), av);
            try {
                final Object val = PropertyUtils.getProperty(address, av.getAttribute().getVal());
                av.setVal(val != null ? String.valueOf(val) : null);
            } catch (Exception e) {
                ShopCodeContext.getLog(this).error("Unable to get address property for {}, prop {}", av.getAttribute(), av.getAttribute().getVal());
            }
        }

        final AttrValue stateCode = valuesMap.get("stateCode");
        final AbstractChoice<State, State> stateDropDownChoice = new DropDownChoice<State>(
                STATE,
                new StateModel(new PropertyModel(stateCode, "val"), stateList),
                stateList).setChoiceRenderer(new StateRenderer());
        final boolean needState = !stateList.isEmpty();
        stateDropDownChoice.setRequired(needState);
        stateDropDownChoice.setVisible(needState);

        final AttrValue countryCode = valuesMap.get("countryCode");
        final AbstractChoice<Country, Country> countryDropDownChoice = new DropDownChoice<Country>(
                COUNTRY,
                new CountryModel(new PropertyModel(countryCode, "val"), countryList),
                countryList) {

            @Override
            protected void onSelectionChanged(final Country country) {
                super.onSelectionChanged(country);
                stateDropDownChoice.setChoices(getStateList(country.getCountryCode()));
                final boolean needState = !stateDropDownChoice.getChoices().isEmpty();
                stateDropDownChoice.setRequired(needState);
                stateDropDownChoice.setVisible(needState);
                if (valuesMap.containsKey("stateCode")) {
                    valuesMap.get("stateCode").setVal(StringUtils.EMPTY);
                }
            }

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

        }.setChoiceRenderer(new CountryRenderer());
        countryDropDownChoice.setRequired(true);



        for (final AttrValue attrValue : values) {

            WebMarkupContainer row;

            if ("countryCode".equals(attrValue.getAttribute().getVal())) {
                row = new WebMarkupContainer(fields.newChildId());
                row.add(getLabel(attrValue, lang));
                row.add(new Fragment("editor", "countryCodeEditor", this).add(countryDropDownChoice));
            } else if ("stateCode".equals(attrValue.getAttribute().getVal())) {
                row = new WebMarkupContainer(fields.newChildId()) {
                    @Override
                    public boolean isVisible() {
                        return stateDropDownChoice.isVisible();
                    }
                };
                row.add(getLabel(attrValue, lang));
                row.add(new Fragment("editor", "stateCodeEditor", this).add(stateDropDownChoice));
            } else {
                row = new WebMarkupContainer(fields.newChildId());
                row.add(getLabel(attrValue, lang));
                row.add(getEditor(attrValue, false));
            }

            fields.add(row);

        }

        final AbstractLink submit = new SubmitLink(ADD_ADDRESS) {

            @Override
            public void onSubmit() {

                final Address addr = getModelObject();

                final boolean isNew = addr.getAddressId() == 0;

                final ShoppingCart cart = ApplicationDirector.getShoppingCart();
                if (isNew || cart.getLogonState() == ShoppingCart.LOGGED_IN) {

                    for (final AttrValue value : values) {
                        try {
                            PropertyUtils.setProperty(addr, value.getAttribute().getVal(), value.getVal());
                        }  catch (Exception e) {
                            ShopCodeContext.getLog(this).error("Unable to set address property for {}, prop {}", value.getAttribute(), value.getAttribute().getVal());
                        }
                    }

                    addressBookFacade.createOrUpdate(addr);

                    // if we just added new address that became new default or we modified an address that is in the cart
                    // reset address
                    if (isNew && addr.isDefaultAddress() ||
                            Long.valueOf(addr.getAddressId()).equals(cart.getOrderInfo().getBillingAddressId()) ||
                            Long.valueOf(addr.getAddressId()).equals(cart.getOrderInfo().getDeliveryAddressId())) {
                        final String key = Address.ADDR_TYPE_BILLING.equals(addressType) ?
                                ShoppingCartCommand.CMD_SETADDRESES_P_BILLING_ADDRESS : ShoppingCartCommand.CMD_SETADDRESES_P_DELIVERY_ADDRESS;
                        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SETADDRESES, cart,
                                (Map) new HashMap() {{
                                    put(ShoppingCartCommand.CMD_SETADDRESES, ShoppingCartCommand.CMD_SETADDRESES);
                                    put(key, addr);
                                }}
                        );
                    }
                }
                setResponsePage(successPage, successPageParameters);
            }

        };

        add(submit);
        submit.add(new Label("addAddressLabel", WicketUtil.createStringResourceModel(this, creating ? "create" : "save")));

        final Component cancel = new SubmitLink(CANCEL_LINK) {

            @Override
            public void onSubmit() {
                setResponsePage(cancelPage, cancelPageParameters);
            }

        }.setDefaultFormProcessing(false).setVisible(cancelPage != null);

        add(cancel);

    }

    /**
     * Fill some data in case of new {@link Address}
     *
     * @param address     address to preprocess
     */
    private void preprocessAddress(final Address address) {
        if (address.getAddressId() == 0) {
            fillAddressWithGeoIpData(address);
        }
    }

    /**
     * Fill new Address with Geo Ip data.
     * At this moment only profile data are supplied. No geo ip.
     * INTEGRATION POINT with tag cloud, that will have geo ip data
     *
     * @param address  address to fill
     */
    private void fillAddressWithGeoIpData(final Address address) {
        // CPOINT
    }


    /**
     * Get states inside selected country.
     *
     * @param countryCode country ot retrieve the states.
     * @return state list inside selected country.
     */
    private List<State> getStateList(final String countryCode) {
        return addressBookFacade.getStatesByCountry(countryCode);
    }


    private Label getLabel(final AttrValue attrValue, final String lang) {

        final I18NModel model = i18NWebSupport.getFailoverModel(
                attrValue.getAttribute().getDisplayName(),
                attrValue.getAttribute().getName());
        final String prop = attrValue.getAttribute().getVal();

        final Label rez = new Label(NAME, new AbstractReadOnlyModel<String>() {

            private final I18NModel m = model;

            @Override
            public String getObject() {
                final String lang = getLocale().getLanguage();
                final String name = m.getValue(lang);
                if (StringUtils.isNotBlank(name)) {
                    return name;
                }
                return getLocalizer().getString(prop, AddressForm.this);
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
