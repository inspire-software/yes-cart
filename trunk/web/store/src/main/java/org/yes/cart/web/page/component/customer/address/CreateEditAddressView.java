package org.yes.cart.web.page.component.customer.address;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.CustomerSelfCarePage;
import org.yes.cart.web.page.component.BaseComponent;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 1:00 PM
 */
public class CreateEditAddressView extends BaseComponent {



    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String ADDRESS_FORM = "addressForm";
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;


    /**
     * Construct create edit address.
     * @param id component id
     * @param addrId
     * @param addressType
     */
    public CreateEditAddressView(final String id, final String addrId, final String addressType) {

        super(id);

        final Customer customer = customerService.findCustomer(ApplicationDirector.getShoppingCart().getCustomerEmail());

        if (customer != null) {

            final Address address = getAddress(customer, addrId, addressType);

            final PageParameters successPageParameters = new PageParameters();
            //todo successPageParameters.add(CustomerSelfCarePage.PANEL, CustomerSelfCarePage.SELFCARE_PANEL);

            add(
                    new AddressForm(
                            ADDRESS_FORM,
                            new Model<Address>(address),
                            addressType,
                            CustomerSelfCarePage.class,
                            successPageParameters,
                            CustomerSelfCarePage.class,
                            successPageParameters
                            )
            );

        }

    }

    /**
     * Get existing or create new address to edit.
     * @param customer customer
     * @param addrId address id
     * @param addressType address type
     * @return instance of {@link Address}
     */
    private Address getAddress(final Customer customer, final String addrId, final String addressType) {
        long pk;
        try {
            pk = NumberUtils.createLong(addrId);
        } catch (NumberFormatException nfe) {
            pk = 0;
        }
        Address rez = null;
        for (Address addr : customer.getAddress()) {
            if (addr.getAddressId() == pk) {
                rez = addr;
                break;
            }
        }
        if (rez == null) {
            rez = customerService.getGenericDao().getEntityFactory().getByIface(Address.class);
            rez.setCustomer(customer);
            rez.setAddressType(addressType);
            customer.getAddress().add(rez);
        }
        return rez;
    }


}
