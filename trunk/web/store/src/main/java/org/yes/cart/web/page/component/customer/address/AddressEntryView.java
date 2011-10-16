package org.yes.cart.web.page.component.customer.address;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.web.page.component.BaseComponent;

import java.util.List;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 15/10/11
 * Time: 14:00
 */
public class AddressEntryView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    protected final static String ADDRESSES_FORM = "selectAddressForm";
    protected final static String ADDRESSES_LIST = "addressList";

    protected final static String ADDRESS_RADIO_GROUP = "addressRadioGroup";
    protected final static String ADDRESS_RADIO = "addressRadio";
    protected final static String ADDRESS_PANEL = "addressShopPanel";


    private final static String LINE1 = "addrLine1";
    private final static String LINE2 = "addrLine2";
    private final static String LINE3 = "addrLine3";
    private final static String LINE4 = "addrLine4";
    private final static String LINE5 = "addrLine5";

    // ------------------------------------- MARKUP IDs AND ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.ADDRESS_SERVICE)
    private AddressService addressService;

    private final Form addressForm;


    /**
     * Construct Address entry.
     *
     * @param s         component id.
     * @param addresses address model
     */
    public AddressEntryView(final String s, final List<Address> addresses) {
        super(s);

        addressForm = new Form(ADDRESSES_FORM);

        add(addressForm);

        final RadioGroup group = new RadioGroup<Address>(
                ADDRESS_RADIO_GROUP,
                new Model<Address>(getDefaultAddress(addresses))) {

            @Override
            protected void onSelectionChanged(final Object o) {
                super.onSelectionChanged(o);
                addressService.updateSetDefault((Address) o);
            }

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }
        };

        //group.setRequired(true);

        addressForm.add(group);

        final ListView<Address> addressList = new ListView<Address>(ADDRESSES_LIST, addresses) {

            protected void populateItem(final ListItem<Address> addressListItem) {

                final Address address = addressListItem.getModelObject();

                populateAddress(addressListItem, address);


            }

        };

        group.add(addressList);

    }

     /**
     * Populate address into list item
     * @param addressListItem list item
     * @param address address to populate
     */
    protected void populateAddress(ListItem<Address> addressListItem, Address address) {
        addressListItem
                .add(new Radio<Address>(ADDRESS_RADIO, new Model<Address>(address)))
                .add(new Label(LINE1, address.getFirstname() + ", " + address.getLastname()))
                .add(new Label(LINE2, address.getAddrline1()))
                .add(new Label(LINE3, StringUtils.isBlank(address.getAddrline2()) ? StringUtils.EMPTY : address.getAddrline1()))
                .add(new Label(LINE4, address.getCity() + ", " + address.getStateCode()))
                .add(new Label(LINE5, address.getPostcode() + ", " + address.getCountryCode()));
    }

    protected Form getAddressForm() {
        return addressForm;
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

    /**
     * Get address service.
     *
     * @return address service.
     */
    protected AddressService getAddressService() {
        return addressService;
    }


}
