package org.yes.cart.web.page.component.customer.address;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.shipping.ShippingView;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 12:31 PM
 */
public class CheckoutAddressView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String SHIPPING_ADDRESSES = "shippingAddresses";
    private final static String BILLING_ADDRESSES = "billingAddresses";

    private final static String BILLING_THE_SAME = "billingTheSame";
    private final static String BILLING_THE_SAME_FORM = "billingTheSameForm";
    private static final String FEEDBACK_PANEL = "feedback";
    private static final String SHIPPING_PANEL = "shippingPanel";
    private static final String NEXT_LINK = "nextLink";

    // ------------------------------------- MARKUP IDs END ---------------------------------- //
    // ---------------------------------- PARAMETER NAMES BEGIN ------------------------------ //
    /**
     * Is Billing panel visible.
     */
    public static final String BILLING_ADDR_VISIBLE = "billingPanelVisible";
    // ---------------------------------- PARAMETER NAMES  END ------------------------------- //

    private Form form;
    private CheckBox checkBox;
    private AddressListView shippingAddressList;
    private AddressListView billingAddressList;


    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;

    @SpringBean(name = ServiceSpringKeys.ADDRESS_SERVICE)
    private AddressService addressService;


    /**
     * Construct address panel.
     *
     * @param id panel id
     */
    public CheckoutAddressView(final String id) {
        super(id);

        final FeedbackPanel feedbackPanel = new FeedbackPanel(FEEDBACK_PANEL);
        add(feedbackPanel);

        final Customer customer = customerService.findCustomer(
                ApplicationDirector.getShoppingCart().getCustomerEmail());

        form = new Form(BILLING_THE_SAME_FORM);

        shippingAddressList = new AddressListView(
                SHIPPING_ADDRESSES,
                customer.getAddresses(Address.ADDR_TYPE_SHIPING),
                Address.ADDR_TYPE_SHIPING);

        billingAddressList = new AddressListView(
                BILLING_ADDRESSES,
                customer.getAddresses(Address.ADDR_TYPE_BILLING),
                Address.ADDR_TYPE_BILLING);

        boolean billingPanelVisible = isBillingPanelVisible();

        billingAddressList.setVisible(billingPanelVisible);


        checkBox = new CheckBox(BILLING_THE_SAME, new Model<Boolean>(!billingPanelVisible)) {

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            public void onSelectionChanged() {
                billingAddressList.setVisible(!billingAddressList.isVisible());
                setModelObject(!billingAddressList.isVisible());
               // ApplicationDirector.getShoppingCart().setSeparateBillingAddress(billingAddressList.isVisible()); //TODO via cmd
            }
        };

        form.add(checkBox);
        add(shippingAddressList)
                .add(billingAddressList)
                .add(form)
                .add(new ShippingView(SHIPPING_PANEL));
        final boolean customerLoggedIn = true; //ShoppingCart.LOGGED_IN == getShoppingCart().getLogonState(); //TODO move into carr

        //add(
                /*new BookmarkablePageLink(NEXT_LINK, CheckoutPage.class, new PageParameters("step=payment")).setVisible(
                        customerLoggedIn
                                && isCustomerHasDefaultAddress(getShoppingCart())
                                && (getShoppingCart().getCarrierSlaId() != null)
                ) */
        //);

    }

    //TODO refactor  the same code in steps panel
    private boolean isCustomerHasDefaultAddress(final ShoppingCart shopppingCart) {

        final Customer customer = customerService.findCustomer(shopppingCart.getCustomerEmail());
        if (customer != null) {

            final List<Address> addrs = addressService.getAddressesByCustomerId(customer.getCustomerId());
            return (addrs != null && !addrs.isEmpty());


        }
        return false;


    }


    /**
     * Get initial state of billing address panel.
     *
     * @return
     */
    private boolean isBillingPanelVisible() {
        /*final RequestParameters parameters = getRequest().getRequestParameters();

        WebUtil.dumpRequestParameters(parameters);

        final String visible = HttpUtil.getSingleValue(parameters.getParameters().get(BILLING_ADDR_VISIBLE));
        if (visible != null) {
            return Boolean.valueOf(visible);
        }     */

        return true;
    }

}
