package org.yes.cart.web.page.component.customer;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.web.page.CustomerSelfCarePage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.customer.address.AddressEntryView;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.List;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 12/10/11
 * Time: 17:50
 */
public class ManageAddressesView extends AddressEntryView {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CREATE_LINK = "createLink";
    private final static String EDIT_LINK = "editLink";
    private final static String DELETE_LINK = "deleteLink";
    private final static String ADDRESS_LABEL = "addressTitle";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    /**
     * Create panel to manage addresses
     *
     * @param s             panel id
     * @param customerModel customer model
     * @param addressType   address type to show
     */
    public ManageAddressesView(final String s, final IModel<Customer> customerModel, final String addressType) {

        super(s, customerModel.getObject().getAddresses(addressType));

        getAddressForm().add(
                new Label(ADDRESS_LABEL, getLocalizer().getString("addressType" + addressType, this))
        );

        getAddressForm().add(

                new SubmitLink(CREATE_LINK) {

                    @Override
                    public void onSubmit() {
                        final PageParameters pageParameters = new PageParameters();
                        //todo pageParameters.add(CustomerSelfCarePage.PANEL, CustomerSelfCarePage.EDIT_ADDRESS_PANEL);
                        pageParameters.add(WebParametersKeys.ADDRESS_ID, "0");
                        pageParameters.add(WebParametersKeys.ADDRESS_TYPE, addressType);
                        setResponsePage(CustomerSelfCarePage.class, pageParameters);

                    }

                }.setDefaultFormProcessing(false)

        );


    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void populateAddress(final ListItem<Address> addressListItem, final Address address) {

        super.populateAddress(addressListItem, address);

        addressListItem.add(
                new SubmitLink(EDIT_LINK) {
                    @Override
                    public void onSubmit() {

                        final PageParameters pageParameters = new PageParameters();
                        //todo pageParameters.add(CustomerSelfCarePage.PANEL, CustomerSelfCarePage.EDIT_ADDRESS_PANEL);
                        pageParameters.add(WebParametersKeys.ADDRESS_ID, String.valueOf(address.getAddressId()));
                        pageParameters.add(WebParametersKeys.ADDRESS_TYPE, address.getAddressType());
                        setResponsePage(CustomerSelfCarePage.class, pageParameters);

                    }
                }.setDefaultFormProcessing(false)
        );
        addressListItem.add(
                new SubmitLink(DELETE_LINK) {
                    @Override
                    public void onSubmit() {

                        final boolean isDefault = address.isDefaultAddress();
                        final String addrType = address.getAddressType();
                        final long customerId = address.getCustomer().getCustomerId();

                        getAddressService().delete(address);

                        if (isDefault) {
                            // set new default address in case if default addres was deleted
                            final List<Address> restOfAddresses = getAddressService().getAddressesByCustomerId(customerId, addrType);
                            if (!restOfAddresses.isEmpty()) {
                                getAddressService().updateSetDefault(restOfAddresses.get(0));
                            }
                        }

                        final PageParameters pageParameters = new PageParameters();
                        //todo pageParameters.add(CustomerSelfCarePage.PANEL, CustomerSelfCarePage.SELFCARE_PANEL);
                        setResponsePage(CustomerSelfCarePage.class, pageParameters);

                    }
                }.setDefaultFormProcessing(false)
        );
    }
}
