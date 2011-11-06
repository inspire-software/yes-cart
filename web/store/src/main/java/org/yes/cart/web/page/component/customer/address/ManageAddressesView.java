package org.yes.cart.web.page.component.customer.address;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.web.page.CustomerSelfCarePage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.List;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 12/10/11
 * Time: 17:50
 */
public class ManageAddressesView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //

    protected final static String SELECT_ADDRESSES_FORM = "selectAddressForm";
    protected final static String EDIT_ADDRESSES_FORM = "editAddressForm";
    protected final static String ADDRESS_RADIO_GROUP = "addressRadioGroup";
    protected final static String ADDRESS_RADIO = "addressRadio";

    protected final static String ADDRESSES_LIST = "addressList";

    private final static String CREATE_LINK = "createLink";
    private final static String EDIT_LINK = "editLink";
    private final static String DELETE_LINK = "deleteLink";
    private final static String ADDRESS_LABEL = "addressTitle";

    private final static String LINE1 = "addrLine1";
    private final static String LINE2 = "addrLine2";
    private final static String LINE3 = "addrLine3";
    private final static String LINE4 = "addrLine4";
    private final static String LINE5 = "addrLine5";

    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.ADDRESS_SERVICE)
    private AddressService addressService;


    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;


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

        super(panelId);

        add(
                new Form(SELECT_ADDRESSES_FORM).add(
                        new RadioGroup<Address>(
                                ADDRESS_RADIO_GROUP,
                                new Model<Address>(getDefaultAddress(customerModel.getObject().getAddresses(addressType)))) {

                            @Override
                            protected void onSelectionChanged(final Object o) {
                                super.onSelectionChanged(o);
                                addressService.updateSetDefault((Address) o);
                            }

                            @Override
                            protected boolean wantOnSelectionChangedNotifications() {
                                return true;
                            }

                        }
                                .add(
                                        new Label(ADDRESS_LABEL, getLocalizer().getString("addressType" + addressType, this))
                                )
                                .add(
                                        new SubmitLink(CREATE_LINK) {
                                            @Override
                                            public void onSubmit() {
                                                final PageParameters pageParameters = new PageParameters();
                                                pageParameters.add(WebParametersKeys.ADDRESS_FORM_RETURN_LABEL,
                                                        returnToCheckout ? CreateEditAddressPage.RETURN_TO_CHECKOUT :
                                                                CreateEditAddressPage.RETURN_TO_SELFCARE);
                                                pageParameters.add(WebParametersKeys.ADDRESS_ID, "0");
                                                pageParameters.add(WebParametersKeys.ADDRESS_TYPE, addressType);
                                                setResponsePage(CreateEditAddressPage.class, pageParameters);

                                            }
                                        }.setDefaultFormProcessing(false)
                                )
                                .add(
                                        new ListView<Address>(ADDRESSES_LIST, customerModel.getObject().getAddresses(addressType)) {
                                            protected void populateItem(final ListItem<Address> addressListItem) {
                                                populateAddress(addressListItem, addressListItem.getModelObject(), returnToCheckout);
                                            }
                                        }
                                )
                )
        );

    }

    /**
     * * Populate address entry
     *
     * @param addressListItem list item
     * @param address         address entry
     */
    protected void populateAddress(final ListItem<Address> addressListItem, final Address address, final boolean returnToCheckout) {

        addressListItem
                .add(new Radio<Address>(ADDRESS_RADIO, new Model<Address>(address)))
                .add(new Label(LINE1, address.getFirstname() + ", " + address.getLastname()))
                .add(new Label(LINE2, address.getAddrline1()))
                .add(new Label(LINE3, StringUtils.isBlank(address.getAddrline2()) ? StringUtils.EMPTY : address.getAddrline1()))
                .add(new Label(LINE4, address.getCity() + ", " + address.getStateCode()))
                .add(new Label(LINE5, address.getPostcode() + ", " + address.getCountryCode()))
                .add(
                        new SubmitLink(EDIT_LINK) {
                            @Override
                            public void onSubmit() {

                                final PageParameters pageParameters = new PageParameters();
                                pageParameters.add(WebParametersKeys.ADDRESS_FORM_RETURN_LABEL,
                                        returnToCheckout ? CreateEditAddressPage.RETURN_TO_CHECKOUT : CreateEditAddressPage.RETURN_TO_SELFCARE);
                                pageParameters.add(WebParametersKeys.ADDRESS_ID, String.valueOf(address.getAddressId()));
                                pageParameters.add(WebParametersKeys.ADDRESS_TYPE, address.getAddressType());
                                setResponsePage(CreateEditAddressPage.class, pageParameters);

                            }
                        }.setDefaultFormProcessing(false)
                )
                .add(
                        new SubmitLink(DELETE_LINK) {
                            /** {@inheritDoc} */
                            @Override
                            public void onSubmit() {
                                addressService.delete(address);

                                if (address.isDefaultAddress()) {
                                    // set new default address in case if default addres was deleted
                                    final List<Address> restOfAddresses = addressService.getAddressesByCustomerId(
                                            address.getCustomer().getCustomerId(),
                                            address.getAddressType());
                                    if (!restOfAddresses.isEmpty()) {
                                        addressService.updateSetDefault(restOfAddresses.get(0));
                                    }
                                }

                                setResponsePage(CustomerSelfCarePage.class);

                            }
                        }.setDefaultFormProcessing(false)
                );
    }


    /**
     * Getr first default address.
     *
     * @param addresses address list
     * @return first address, that marked as default or null if not found
     */
    protected Address getDefaultAddress(final List<Address> addresses) {
        for (Address addr : addresses) {
            if (addr.isDefaultAddress()) {
                return addr;
            }
        }
        return null;
    }

}
