package org.yes.cart.web.page.component.customer.address;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.web.page.component.BaseComponent;

import java.util.List;

/**
 *
 * Address List view .
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 11:39 AM
 */
public class AddressListView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String ADDRESS_SELECTOR_ENTRY = "addressSelectorEntry";
    private final static String ADDRESS_FORM = "addressForm";
    private final static String CREATE_NEW_ADDRESS = "createNewAddress";
    private final static String NEW_ADDRESS_MESSAGE = "newAddressMessage";

    private final static String SHIP_NEW_ADDR = "shipToNewAddress";
	private final static String BILL_NEW_ADDR = "billToNewAddress";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.ADDRESS_SERVICE)
    private AddressService addressService;

    /**
     * Construct address list view.
     *
     * @param s           list view id
     * @param addresses   list of addreses to populate, include last null address
     * @param addressType address type.
     */
    public AddressListView(final String s, final List<Address> addresses, final String addressType) {

        super(s);
        final Address address = addressService.getGenericDao().getEntityFactory().getByIface(Address.class);

        /*add(
                new AddressEntryView(ADDRESS_SELECTOR_ENTRY, addresses)
        );   */



        final PageParameters succsesspageParameters = new PageParameters();
       /* succsesspageParameters.add(StepsPanel.STEP, StepsPanel.STEP_ADDR);
        if (Address.ADDR_TYPE_BILLING.equals(addressType)) {
            //keep billing panel expanded(visible)
            succsesspageParameters.add(AddressPanel.BILLING_ADDR_VISIBLE, Boolean.toString(true));
        }    */


        final AddressForm addressForm = new AddressForm (
                ADDRESS_FORM,
                new CompoundPropertyModel(address),
                addressType,
                null, //todo CheckoutPage.class,
                null,             //succsesspageParameters
                null,
                null );


        final CheckBox createNewAddressCheckBox = new CheckBox(CREATE_NEW_ADDRESS, new Model<Boolean>(addresses.isEmpty())) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            public void onSelectionChanged() {
                addressForm.setVisible(!addressForm.isVisible());
                setModelObject(addressForm.isVisible());
            }
        };

        add(createNewAddressCheckBox);

        add(addressForm);

        addressForm.setVisible(createNewAddressCheckBox.getModelObject());

        if (Address.ADDR_TYPE_BILLING.endsWith(addressType)) {
            add(new Label(NEW_ADDRESS_MESSAGE, getLocalizer().getString(BILL_NEW_ADDR, this)));
        } else {
            add(new Label(NEW_ADDRESS_MESSAGE, getLocalizer().getString(SHIP_NEW_ADDR, this)));
        }

    }

}
