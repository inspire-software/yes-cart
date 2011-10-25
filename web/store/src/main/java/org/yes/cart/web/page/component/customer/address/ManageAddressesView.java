package org.yes.cart.web.page.component.customer.address;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.AddressService;
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

    protected final static String ADDRESSES_FORM = "selectAddressForm";
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


    /**
     * Create panel to manage addresses
     *
     * @param s             panel id
     * @param customerModel customer model
     * @param addressType   address type to show
     */
    public ManageAddressesView(final String s, final IModel<Customer> customerModel, final String addressType) {

        super(s);

        add(
                new Form(ADDRESSES_FORM)
                        .add(
                                new Label(ADDRESS_LABEL, getLocalizer().getString("addressType" + addressType, this))
                        )
                        .add(
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
                        )
                        .add(
                                new ListView<Address>(ADDRESSES_LIST, customerModel.getObject().getAddresses(addressType)) {
                                    protected void populateItem(final ListItem<Address> addressListItem) {
                                        populateAddress(addressListItem, addressListItem.getModelObject());
                                    }
                                }
                        )
        );


    }

    /**
     * {@inheritDoc}
     */
    protected void populateAddress(final ListItem<Address> addressListItem, final Address address) {

        addressListItem
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
                                //todo pageParameters.add(CustomerSelfCarePage.PANEL, CustomerSelfCarePage.EDIT_ADDRESS_PANEL);
                                pageParameters.add(WebParametersKeys.ADDRESS_ID, String.valueOf(address.getAddressId()));
                                pageParameters.add(WebParametersKeys.ADDRESS_TYPE, address.getAddressType());
                                setResponsePage(CustomerSelfCarePage.class, pageParameters);

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

                                final PageParameters pageParameters = new PageParameters();
                                //todo pageParameters.add(CustomerSelfCarePage.PANEL, CustomerSelfCarePage.SELFCARE_PANEL);
                                setResponsePage(CustomerSelfCarePage.class, pageParameters);

                            }
                        }.setDefaultFormProcessing(false)
                );
    }
}
