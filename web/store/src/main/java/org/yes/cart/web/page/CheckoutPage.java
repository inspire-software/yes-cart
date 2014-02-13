/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.page;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.cart.ShoppingCartPaymentVerificationView;
import org.yes.cart.web.page.component.customer.address.ManageAddressesView;
import org.yes.cart.web.page.component.customer.auth.LoginPanel;
import org.yes.cart.web.page.component.customer.auth.RegisterPanel;
import org.yes.cart.web.page.component.footer.CheckoutFooter;
import org.yes.cart.web.page.component.header.CheckoutHeader;
import org.yes.cart.web.page.component.shipping.ShippingView;
import org.yes.cart.web.page.component.util.PaymentGatewayDescriptorModel;
import org.yes.cart.web.page.component.util.PaymentGatewayDescriptorRenderer;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.AddressBookFacade;
import org.yes.cart.web.support.service.CheckoutServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Checkout page has following main steps:
 * <p/>
 * 1. big shopping cart with coupons, taxes, items manipulations.
 * 2. quick registration, can be skipped if customer is registered.
 * 3. billing and shipping addresses
 * 4. payment page with payment method selection
 * 5. successful/unsuccessful callback page
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/8/11
 * Time: 8:06 PM
 */
@RequireHttps
public class CheckoutPage extends AbstractWebPage {

    private static final long serialVersionUID = 20101107L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    public static final String NAVIGATION_THREE_FRAGMENT = "threeStepNavigationFragment";
    public static final String NAVIGATION_FOUR_FRAGMENT = "fourStepNavigationFragment";
    public static final String LOGIN_FRAGMENT = "loginFragment";

    public static final String ADDRESS_FRAGMENT = "addressFragment";
    public static final String SHIPPING_ADDRESS_VIEW = "shippingAddress";
    public static final String BILLING_ADDRESS_VIEW = "billingAddress";
    public static final String BILLING_THE_SAME_FORM = "billingTheSameForm";
    public static final String BILLING_THE_SAME = "billingTheSame";


    public static final String SHIPMENT_FRAGMENT = "shipmentFragment";
    public static final String SHIPMENT_VIEW = "shipmentView";

    private static final String PAYMENT_FRAGMENT = "paymentFragment";
    private static final String PAYMENT_FRAGMENT_OPTIONS_FORM = "paymentOptionsForm";
    private static final String PAYMENT_FRAGMENT_MD_CHECKBOX = "multipleDelivery";
    private static final String PAYMENT_FRAGMENT_MD_LABEL = "multipleDeliveryLabel";
    private static final String PAYMENT_FRAGMENT_GATEWAY_CHECKBOX = "paymentGateway";
    private static final String PAYMENT_FRAGMENT_PAYMENT_FORM = "dynamicPaymentForm";


    public static final String CONTENT_VIEW = "content";
    public static final String NAVIGATION_VIEW = "navigation";

    public static final String PART_REGISTER_VIEW = "registerView";
    public static final String PART_LOGIN_VIEW = "loginView";

    public static final String STEP = "step";

    public static final String STEP_LOGIN = "login";
    public static final String STEP_ADDR = "address";
    public static final String STEP_SHIPMENT = "ship";
    public static final String STEP_PAY = "payment";

    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    // ---------------------------------- PARAMETER NAMES BEGIN ------------------------------ //
    /**
     * Is Billing panel visible.
     */
    public static final String BILLING_ADDR_VISIBLE = "billingPanelVisible";
    //three steps checkout process, because customer already logged in
    // or registered
    public static final String THREE_STEPS_PROCESS = "thp";
    // ---------------------------------- PARAMETER NAMES  END ------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CHECKOUT_SERVICE_FACADE)
    private CheckoutServiceFacade checkoutServiceFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @SpringBean(name = StorefrontServiceSpringKeys.ADDRESS_BOOK_FACADE)
    private AddressBookFacade addressBookFacade;


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public CheckoutPage(final PageParameters params) {

        super(params);

        final boolean threeStepsProcess = params.get(THREE_STEPS_PROCESS).toBoolean(
                ((AuthenticatedWebSession) getSession()).isSignedIn()
        ) && ((AuthenticatedWebSession) getSession()).isSignedIn();

        final String currentStep =
                params.get(STEP).toString(threeStepsProcess ? STEP_ADDR : STEP_LOGIN);


        if (STEP_PAY.equals(currentStep) ) {

            checkoutServiceFacade.createFromCart(ApplicationDirector.getShoppingCart());

        }


        add(
                new FeedbackPanel(FEEDBACK)
        ).add(
                new Fragment(NAVIGATION_VIEW, threeStepsProcess ?
                        NAVIGATION_THREE_FRAGMENT : NAVIGATION_FOUR_FRAGMENT, this)
        ).add(
                getContent(currentStep)
        ).addOrReplace(
                new CheckoutFooter(FOOTER)
        ).addOrReplace(
                new CheckoutHeader(HEADER)
        );


    }






    /**
     * Resolve content by given current step.
     *
     * @param currentStep current step label
     * @return markup container
     */
    private MarkupContainer getContent(final String currentStep) {
        if (!((AuthenticatedWebSession) getSession()).isSignedIn()
                || StringUtils.isBlank(ApplicationDirector.getShoppingCart().getCustomerEmail())) {
            return createLoginFragment();
        }

        if (!addressBookFacade.customerHasAtLeastOneAddress(ApplicationDirector.getShoppingCart().getCustomerEmail())) {
            return createAddressFragment();
        }

        if (ApplicationDirector.getShoppingCart().getOrderInfo().getCarrierSlaId() == null) {
            //need to select carrier
            return createShippmentFragment();
        }


        if (STEP_ADDR.equals(currentStep)) {
            return createAddressFragment();
        } else if (STEP_SHIPMENT.equals(currentStep)) {
            return createShippmentFragment();
        } else if (STEP_PAY.equals(currentStep)) {
            return createPaymentFragment();
        } else {
            return createLoginFragment();
        }
    }

    /**
     * The default fragment is login/register page.
     *
     * @return login fragment
     */
    private MarkupContainer createLoginFragment() {
        return new Fragment(CONTENT_VIEW, LOGIN_FRAGMENT, this)
                .add(
                        new LoginPanel(PART_LOGIN_VIEW, true))
                .add(
                        new RegisterPanel(PART_REGISTER_VIEW, true)
                );
    }

    /**
     * Create payment fragment with order verification and payment methods forms.
     * <p/>
     * Shopping cart form. Used to show products in cart , adjust product quantity.
     * <p/>
     * <p/>
     * Complex form with several deliveries the shopping cart form will show following items:
     * <pre>
     *  -----------------------------------
     * name             price   qty    amount
     * sku item 1        2       2      4
     * sku item 2        3       3      6
     * subtotal                         10
     * delivery                         2
     * tax                              3
     * total                            15
     *
     * sku item 3        1       3      3
     * sku item 4        1       5      5
     * subtotal                         8
     * delivery                         2
     * tax                              3
     * total                            13
     *
     * grand total                      28
     *
     * ----------------------------------------
     * payment form
     * ----------------------------------------
     * </pre>
     *
     * @return payment fragment of checkout process.
     */
    private MarkupContainer createPaymentFragment() {

        final MarkupContainer rez = new Fragment(CONTENT_VIEW, PAYMENT_FRAGMENT, this);
        final ShoppingCart shoppingCart = ApplicationDirector.getShoppingCart();
        final OrderInfo orderInfo = shoppingCart.getOrderInfo();
        final boolean showMultipleDelivery = checkoutServiceFacade.isMultipleDeliveryAllowedForCart(shoppingCart);
        orderInfo.setPaymentGatewayLabel(null);

        rez.addOrReplace(new Label(PAYMENT_FRAGMENT_PAYMENT_FORM));
        rez.addOrReplace(new ShoppingCartPaymentVerificationView("orderVerificationView", shoppingCart.getGuid()));

        final Component multiDelivery = new CheckBox(PAYMENT_FRAGMENT_MD_CHECKBOX, new PropertyModel(orderInfo, "multipleDelivery")) {

            /** {@inheritDoc} */
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            public void onSelectionChanged() {
                setModelObject(!getModelObject());
                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_MULTIPLEDELIVERY,
                        ApplicationDirector.getShoppingCart(),
                        (Map) Collections.singletonMap(ShoppingCartCommand.CMD_MULTIPLEDELIVERY, getModelObject().toString()));
                super.onSelectionChanged();
                persistCartIfNecessary();
                setResponsePage(
                        CheckoutPage.class,
                        new PageParameters().set(
                                CheckoutPage.THREE_STEPS_PROCESS,
                                "true"
                        ).set(
                                CheckoutPage.STEP,
                                CheckoutPage.STEP_PAY
                        )
                );
            }

        }.setVisible(showMultipleDelivery);

        final List<Pair<PaymentGatewayDescriptor, String>> available =
                checkoutServiceFacade.getPaymentGatewaysDescriptors(ApplicationDirector.getCurrentShop(), ApplicationDirector.getShoppingCart());

        final Component pgSelector = new DropDownChoice<Pair<PaymentGatewayDescriptor, String>>(
                PAYMENT_FRAGMENT_GATEWAY_CHECKBOX,
                new PaymentGatewayDescriptorModel(
                        new PropertyModel<String>(orderInfo, "paymentGatewayLabel"),
                        available
                ),
                available) {

            /** {@inheritDoc} */
            protected void onSelectionChanged(final Pair<PaymentGatewayDescriptor, String> descriptor) {

                final CustomerOrder order = checkoutServiceFacade.findByGuid(ApplicationDirector.getShoppingCart().getGuid());
                final Total total = checkoutServiceFacade.getOrderTotal(order);
                final BigDecimal grandTotal = total.getTotalAmount();

                //pay pal express checkout gateway support
                order.setPgLabel(descriptor.getFirst().getLabel());
                checkoutServiceFacade.update(order);


                final String htmlForm = getPaymentForm(order, grandTotal);

                rez.addOrReplace(
                        new Label(PAYMENT_FRAGMENT_PAYMENT_FORM, htmlForm)
                                .setEscapeModelStrings(false)
                );

                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SETPGLABEL,
                        ApplicationDirector.getShoppingCart(),
                        (Map) Collections.singletonMap(ShoppingCartCommand.CMD_SETPGLABEL, descriptor.getFirst().getLabel()));

            }


            /** {@inheritDoc} */
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

        }.setChoiceRenderer(new PaymentGatewayDescriptorRenderer());

        rez.addOrReplace(
                        new Form(PAYMENT_FRAGMENT_OPTIONS_FORM)
                                .add(multiDelivery)
                                .add(
                                        new Label(PAYMENT_FRAGMENT_MD_LABEL,
                                                getLocalizer().getString(PAYMENT_FRAGMENT_MD_LABEL, this)

                                        ).setVisible(showMultipleDelivery)

                                )
                                .add(pgSelector)
                );


        return rez;
    }

    /**
     * Get html form for payment.
     *
     * @param order      order
     * @param grandTotal amount
     * @return payment form
     */
    protected String getPaymentForm(final CustomerOrder order,
                                    final BigDecimal grandTotal) {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();

        String fullName = StringUtils.EMPTY;

        if (order.getCustomer() != null) {

            fullName = (order.getCustomer().getFirstname()
                    + " "
                    + order.getCustomer().getLastname()).toUpperCase();

        }

        final PaymentGateway gateway = checkoutServiceFacade.getOrderPaymentGateway(order);
        final Payment payment = checkoutServiceFacade.createPaymentToAuthorize(order);

        final String submitBtnValue = getSubmitButton(gateway);
        final String postActionUrl = getPostActionUrl(gateway);

        final String htmlFragment = gateway.getHtmlForm(
                fullName,
                cart.getCurrentLocale(),
                grandTotal,
                cart.getCurrencyCode(),
                cart.getGuid(),
                payment);


        return MessageFormat.format(
                "<form method=\"POST\" action=\"{0}\">\n" +
                        "{1}\n" +
                        "<div id=\"paymentDiv\">\n" +
                        "{2}" +
                        "</div></form>",
                postActionUrl,
                htmlFragment,
                submitBtnValue
        );

    }

    /**
     * Get submit button html code.
     *
     * @param gateway selected gateway
     * @return html code for submit button.
     */
    private String getSubmitButton(PaymentGateway gateway) {
        String rez = null;
        if (gateway instanceof PaymentGatewayExternalForm) {
            rez = ((PaymentGatewayExternalForm) gateway).getSubmitButton();
        }
        if (StringUtils.isBlank(rez)) {
            rez = "<input type=\"submit\" value=\"" + getLocalizer().getString("paymentSubmit", this) + "\">";
        }
        return rez;
    }

    /**
     * Get the post action url for payment.
     *
     * @param gateway gateway
     * @return url for post
     */
    private String getPostActionUrl(final PaymentGateway gateway) {
        if (gateway instanceof PaymentGatewayExternalForm) {
            //paypal express checkout will return internal url , than mounted with "paymentpaypalexpress" url
            return ((PaymentGatewayExternalForm) gateway).getPostActionUrl();
        }
        /**
         * By default all payment processors and gateways  parked to page, that mounted with this url
         */
        return "payment";
    }

    /**
     * Create shipment method selection fragment.
     *
     * @return shipment method fragment
     */

    private MarkupContainer createShippmentFragment() {
        return new Fragment(CONTENT_VIEW, SHIPMENT_FRAGMENT, this)
                .add(
                        new ShippingView(SHIPMENT_VIEW)
                );
    }

    /**
     * Create address fragment to manage shipping and billing addresses.
     *
     * @return address fragment.
     */
    private MarkupContainer createAddressFragment() {

        MarkupContainer rez;

        boolean billingAddressHidden = getRequest().getRequestParameters().getParameterValue(
                BILLING_ADDR_VISIBLE).toBoolean(true);

        final Customer customer = customerServiceFacade.getCustomerByEmail(
                ApplicationDirector.getShoppingCart().getCustomerEmail());

        final Model<Customer> customerModel = new Model<Customer>(customer);

        final ManageAddressesView shipppingAddress =
                new ManageAddressesView(SHIPPING_ADDRESS_VIEW, customerModel, Address.ADDR_TYPE_SHIPING, true);

        final ManageAddressesView billingAddress =
                new ManageAddressesView(BILLING_ADDRESS_VIEW, customerModel, Address.ADDR_TYPE_BILLING, true);

        rez = new Fragment(CONTENT_VIEW, ADDRESS_FRAGMENT, this);

        rez.add(
                shipppingAddress
        ).add(
                billingAddress.setVisible(!billingAddressHidden)
        );

        rez.add(
                new Form(BILLING_THE_SAME_FORM).add(
                        new CheckBox(BILLING_THE_SAME, new Model<Boolean>(billingAddressHidden)) {

                            @Override
                            protected boolean wantOnSelectionChangedNotifications() {
                                return true;
                            }

                            @Override
                            public void onSelectionChanged() {
                                final boolean newVal = !getModelObject();
                                setModelObject(newVal);
                                billingAddress.setVisible(!newVal);
                            }
                        }
                )
        );
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        executeHttpPostedCommands();
        super.onBeforeRender();
        persistCartIfNecessary();

    }


}
