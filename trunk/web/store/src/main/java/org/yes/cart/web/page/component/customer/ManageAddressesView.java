package org.yes.cart.web.page.component.customer;

import org.apache.wicket.model.IModel;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.web.page.component.BaseComponent;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 12/10/11
 * Time: 17:50
 */
public class ManageAddressesView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CREATE_LINK = "createLink";
    private final static String EDIT_LINK = "editLink";
    private final static String DELETE_LINK = "deleteLink";
    private final static String ADDRESS_LABEL = "addressTitle";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //




    /**
     * Create panel to manage addresses
     * @param s panel id
     * @param customerModel customer model
     * @param addressType address type to show
     */

    public ManageAddressesView(final String s, final IModel<Customer> customerModel, final String addressType) {
        super(s);

    }

}
