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

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.CreateEditAddressPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.AddressBookFacade;
import org.yes.cart.web.theme.WicketPagesMounter;

import java.util.HashMap;
import java.util.List;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 12/10/11
 * Time: 17:50
 */
public class ManageAddressesView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //

    private final static String SELECT_ADDRESSES_FORM = "selectAddressForm";
    protected final static String EDIT_ADDRESSES_FORM = "editAddressForm";
    private final static String ADDRESS_RADIO_GROUP = "addressRadioGroup";
    private final static String ADDRESS_RADIO = "addressRadio";

    private final static String ADDRESSES_LIST = "addressList";

    private final static String CREATE_LINK = "createLink";
    private final static String EDIT_LINK = "editLink";
    private final static String DELETE_LINK = "deleteLink";
    private final static String ADDRESS_LABEL = "addressTitle";

    private final static String FORMATTED_ADDRESS = "formattedAddress";

    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.ADDRESS_BOOK_FACADE)
    private AddressBookFacade addressBookFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @SpringBean(name = StorefrontServiceSpringKeys.WICKET_PAGES_MOUNTER)
    private WicketPagesMounter wicketPagesMounter;


    /**
     * Create panel to manage addresses
     *
     * @param panelId             panel id
     * @param customerModel customer model
     * @param addressType   address type to show
     * @param returnToCheckout true if need to return to checkout page after address creation.
     */
    public ManageAddressesView(final String panelId, final IModel<Customer> customerModel,
                               final String addressType, final boolean returnToCheckout) {

        super(panelId, customerModel);

        final List<Address> allowed = determineAllowedAddresses(customerModel, addressType);



        add(
                new Form(SELECT_ADDRESSES_FORM).add(
                        new RadioGroup<Address>(
                                ADDRESS_RADIO_GROUP,
                                new Model<>(addressBookFacade.getDefaultAddress(customerModel.getObject(), getCurrentCustomerShop(), addressType))) {

                            @Override
                            protected void onSelectionChanged(final Address address) {
                                super.onSelectionChanged(address);
                                addressBookFacade.useAsDefault(address, getCurrentCustomerShop());
                                final String key = Address.ADDR_TYPE_BILLING.equals(addressType) ?
                                        ShoppingCartCommand.CMD_SETADDRESES_P_BILLING_ADDRESS : ShoppingCartCommand.CMD_SETADDRESES_P_DELIVERY_ADDRESS;
                                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SETADDRESES, getCurrentCart(),
                                        new HashMap() {{
                                            put(ShoppingCartCommand.CMD_SETADDRESES, ShoppingCartCommand.CMD_SETADDRESES);
                                            put(key, address);
                                        }}
                                );
                                ((AbstractWebPage) getPage()).persistCartIfNecessary();
                            }

                            @Override
                            protected boolean wantOnSelectionChangedNotifications() {
                                return true;
                            }

                        }
                                .add(
                                        new Label(ADDRESS_LABEL, new StringResourceModel("addressType" + addressType, this, null))
                                )
                                .add(
                                        new SubmitLink(CREATE_LINK) {
                                            @Override
                                            public void onSubmit() {

                                                final Pair<Class<? extends Page>, PageParameters> target =
                                                        determineAddressPage(returnToCheckout, 0L, addressType);
                                                setResponsePage(target.getFirst(), target.getSecond());

                                            }
                                        }.setDefaultFormProcessing(false)
                                )
                                .add(
                                        new ListView<Address>(ADDRESSES_LIST, allowed) {
                                            @Override
                                            protected void populateItem(final ListItem<Address> addressListItem) {
                                                populateAddress(addressListItem, addressListItem.getModelObject(), returnToCheckout);
                                            }
                                        }
                                )
                ).setVisible(customerModel.getObject() != null)
        );

    }

    private List<Address> determineAllowedAddresses(final IModel<Customer> customerModel, final String addressType) {

        final Shop customerShop = getCurrentCustomerShop();

        return addressBookFacade.getAddresses(customerModel.getObject(), customerShop, addressType);

    }

    /**
     * * Populate address entry
     *
     * @param addressListItem list item
     * @param address         address entry
     */
    protected void populateAddress(final ListItem<Address> addressListItem, final Address address, final boolean returnToCheckout) {

        final String addressString = addressBookFacade.formatAddressFor(
                address,
                getCurrentShop(),
                (Customer) getDefaultModelObject(),
                getLocale().getLanguage()
        );

        addressListItem
                .add(new Radio<>(ADDRESS_RADIO, new Model<>(address)))
                .add(new Label(FORMATTED_ADDRESS, makeHtml(addressString)).setEscapeModelStrings(false))
                .add(
                        new SubmitLink(EDIT_LINK) {
                            @Override
                            public void onSubmit() {

                                final Pair<Class<? extends Page>, PageParameters> target =
                                        determineAddressPage(returnToCheckout, address.getAddressId(), address.getAddressType());
                                setResponsePage(target.getFirst(), target.getSecond());

                            }
                        }.setDefaultFormProcessing(false)
                )
                .add(
                        new SubmitLink(DELETE_LINK) {
                            /** {@inheritDoc} */
                            @Override
                            public void onSubmit() {
                                final Shop shop = ((AbstractWebPage) getPage()).getCurrentCustomerShop();
                                addressBookFacade.remove(address, shop);
                                if (returnToCheckout) {
                                    setResponsePage(wicketPagesMounter.getPageProviderByUri("/checkout").get());
                                } else {
                                    setResponsePage(wicketPagesMounter.getPageProviderByUri("/profile").get());
                                }
                            }
                        }.setDefaultFormProcessing(false)
                );
    }


    /**
     * Extension hook to override classes for themes.
     *
     * @param isCheckout is this checkout address panel
     * @param addressId address PK or 0
     * @param addressType address type
     *
     * @return redirect target
     */
    protected Pair<Class<? extends Page>, PageParameters> determineAddressPage(final boolean isCheckout,
                                                                               final long addressId,
                                                                               final String addressType) {

        final Class<? extends Page> successfulPage = (Class) wicketPagesMounter.getPageProviderByUri("/address").get();
        final PageParameters parameters = new PageParameters();

        parameters.add(WebParametersKeys.ADDRESS_FORM_RETURN_LABEL,
                isCheckout ? CreateEditAddressPage.RETURN_TO_CHECKOUT : CreateEditAddressPage.RETURN_TO_PROFILE);
        parameters.add(WebParametersKeys.ADDRESS_ID, String.valueOf(addressId));
        parameters.add(WebParametersKeys.ADDRESS_TYPE, addressType);

        return new Pair<>(successfulPage, parameters);
    }


    private String makeHtml(final String address) {
        return address.trim().replace("\r\n", "<br/>").replace("\r", "<br/>").replace("\n", "<br/>");
    }
}
