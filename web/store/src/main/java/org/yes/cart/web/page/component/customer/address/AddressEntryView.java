package org.yes.cart.web.page.component.customer.address;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.service.domain.AddressService;

import java.util.List;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 15/10/11
 * Time: 14:00
 */
public class AddressEntryView {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    protected final static String ADDRESSES_FORM = "selectAddressForm";
    protected final static String ADDRESSES_LIST = "addressList";

    protected final static String ADDRESS_RADIO_GROUP = "addressRadioGroup";
    protected final static String ADDRESS_RADIO = "addressRadio";
    protected final static String ADDRESS_PANEL = "addressShopPanel";

    // ------------------------------------- MARKUP IDs AND ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.ADDRESS_SERVICE)
    private AddressService addressService;

    private final Form addressForm;


    /**
     * Construct Address entry.
     *
     * @param s           component id.
     * @param addresses   address model
     */
    public AddressEntryPanel(final String s, final List<Address> addresses) {
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




}
