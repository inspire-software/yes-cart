package org.yes.cart.web.page.component.customer.address;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.CustomerSelfCarePage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.WebParametersKeys;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 1:00 PM
 */
public class CreateEditAddressPage extends AbstractWebPage {

    public final static String RETURN_TO_SELFCARE = "selfcare";
    public final static String RETURN_TO_CHECKOUT = "checkout";

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String ADDRESS_FORM = "addressForm";
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //


    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;

    /**
     * Construct page to create / edit customer address.
     * Created address will be treated as default  shipping or billing address.
     *
     * @param params
     */
    public CreateEditAddressPage(final PageParameters params) {

        super(params);

        final Customer customer = customerService.findCustomer(ApplicationDirector.getShoppingCart().getCustomerEmail());

        final String addrId = params.get(WebParametersKeys.ADDRESS_ID).toString();

        final String addrType = params.get(WebParametersKeys.ADDRESS_TYPE).toString();

        final Address address = getAddress(customer, addrId, addrType);


        final Class<? extends Page>  returnToPageClass = RETURN_TO_SELFCARE.equals(
                params.get(WebParametersKeys.ADDRESS_FORM_RETURN_LABEL).toString()) ?
                CustomerSelfCarePage.class : CustomerSelfCarePage.class;

        final PageParameters successPageParameters = new PageParameters();

        add(
                new FeedbackPanel(FEEDBACK)
        );

        add(
                new AddressForm(
                        ADDRESS_FORM,
                        new Model<Address>(address),
                        addrType,
                        returnToPageClass,
                        successPageParameters,
                        returnToPageClass,
                        successPageParameters
                )
        );


    }


    /**
     * Get existing or create new address to edit.
     *
     * @param customer    customer
     * @param addrId      address id
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
