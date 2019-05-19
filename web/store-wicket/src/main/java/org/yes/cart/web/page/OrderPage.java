/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.web.page.component.cart.ShoppingCartPaymentVerificationView;
import org.yes.cart.web.page.component.customer.auth.OrderVerifyPanel;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.HeaderMetaInclude;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.page.component.js.ServerSideJs;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CheckoutServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;
import org.yes.cart.web.theme.WicketPagesMounter;
import org.yes.cart.web.utils.WicketUtil;

import java.util.Collections;

/**
 *
 * Customer self care page to view orders, wish list, etc.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/11/11
 * Time: 9:51 PM
 */
@RequireHttps
public class OrderPage extends AbstractWebPage {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String ORDER_NUM = "orderNum";
    private final static String ORDER_STATE = "orderStatus";
    private final static String ORDER_PANEL = "orderView";
    private final static String ORDER_VERIFY = "orderVerifyForm";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CHECKOUT_SERVICE_FACADE)
    private CheckoutServiceFacade checkoutServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.WICKET_PAGES_MOUNTER)
    private WicketPagesMounter wicketPagesMounter;

    private IModel<String> customerOrder = Model.of();
    private IModel<Boolean> showOrder = Model.of(Boolean.FALSE);
    private IModel<String> orderCheck = Model.of();
    private IModel<Boolean> showGuestCheck = Model.of(Boolean.FALSE);

    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public OrderPage(final PageParameters params) {
        super(params);

        final String orderGuid = params.get("order").toString();

        final String email = getCurrentCart().getCustomerEmail();
        Customer customer = null;
        if (StringUtils.isNotBlank(email)) {
            customer = customerServiceFacade.getCustomerByEmail(getCurrentShop(), email);
        } // else attempt to view guest order

        CustomerOrder order = checkoutServiceFacade.findByReference(orderGuid);

        if (order != null) {

            final boolean guestOrder = order.getCustomer() == null || order.getCustomer().isGuest();

            if (guestOrder) {

                if (customer != null) {
                    customerOrder.setObject(null);
                    showOrder.setObject(Boolean.FALSE); // DO NOT ALLOW LOGGED IN CUSTOMERS VIEW GUEST ORDERS
                } else {
                    customerOrder.setObject(order.getOrdernum());
                    showOrder.setObject(Boolean.FALSE);
                    showGuestCheck.setObject(Boolean.TRUE);  // NEED TO ASK TO ADDITIONAL VERIFICATION
                }

            } else if (customer == null) {

                customerOrder.setObject(null);
                showOrder.setObject(Boolean.FALSE);

                // CUSTOMER NEEDS TO LOGIN TO VIEW THE ORDER
                setResponsePage(wicketPagesMounter.getPageProviderByUri("/login").get());

            } else if (order.getCustomer().getCustomerId() != customer.getCustomerId()) {

                customerOrder.setObject(null);
                showOrder.setObject(Boolean.FALSE); // DO NOT ALLOW VIEWING ORDERS THAT DO NOT BELONG TO CUSTOMER

            } else {

                customerOrder.setObject(order.getOrdernum());
                showOrder.setObject(Boolean.TRUE);

            }

        }

        add(new FeedbackPanel(FEEDBACK));

        add(new BookmarkablePageLink("historyBtn", wicketPagesMounter.getPageProviderByUri("/orders").get()));
        add(new OrderVerifyPanel(ORDER_VERIFY, orderCheck).setVisible(showGuestCheck.getObject()));

        add(new StandardFooter(FOOTER));
        add(new StandardHeader(HEADER));
        add(new ServerSideJs("serverSideJs"));
        add(new HeaderMetaInclude("headerInclude"));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final String orderNum = customerOrder.getObject();
        final Boolean show = showOrder.getObject();
        final Boolean showCheck = showGuestCheck.getObject();
        final String guestEmail = orderCheck.getObject();

        final boolean auth = isAuthenticated();
        if (!auth && !showCheck) {
            forceLogoutRedirect();
        } else {
            executeHttpPostedCommands();
        }

        super.onBeforeRender();

        boolean doShow = show;
        boolean doShowCheck = !doShow && showCheck;

        if (!doShow && StringUtils.isNotBlank(orderNum) && StringUtils.isNotBlank(guestEmail)) {

            CustomerOrder order = checkoutServiceFacade.findByReference(orderNum);
            if (guestEmail.equalsIgnoreCase(order.getEmail())) {
                doShowCheck = false;
                doShow = true;
            } else {
                error(getLocalizer().getString("orderNotFoundGuest", this));
            }

        }

        if (doShow && StringUtils.isNotBlank(orderNum)) {

            CustomerOrder order = checkoutServiceFacade.findByReference(orderNum);

            addOrReplace(new Label(ORDER_NUM,
                    WicketUtil.createStringResourceModel(this, "orderNoTitle",
                            Collections.singletonMap("ordernum", order.getOrdernum()))));
            addOrReplace(new Label(ORDER_STATE,
                    WicketUtil.createStringResourceModel(this, order.getOrderStatus())));
            addOrReplace(new ShoppingCartPaymentVerificationView(ORDER_PANEL, orderNum, true));
        } else {
            addOrReplace(new Label(ORDER_NUM, ""));
            addOrReplace(new Label(ORDER_STATE, ""));
            addOrReplace(new Label(ORDER_PANEL, ""));
            addOrReplace(new Label("receipt", "#").setVisible(false));
            if (!doShowCheck) {
                error(getLocalizer().getString("orderNotFound", this));
            }
        }

        get("historyBtn").setVisible(auth);

        addOrReplace(new StatelessForm("receiptForm") {
            @Override
            protected CharSequence getActionUrl() {
                return getWicketUtil().getHttpServletRequest().getContextPath() + "/orderreceipt.pdf";
            }
        }
                .add(new HiddenField<>("ordernum", Model.of(orderNum)))
                .add(new HiddenField<>("guestEmail", Model.of(guestEmail)).setVisible(StringUtils.isNotBlank(guestEmail)))
                .setVisible(doShow));


        get(ORDER_VERIFY).setVisible(doShowCheck);

        if (auth) {
            persistCartIfNecessary();
        }
    }

    /**
     * Get page title.
     *
     * @return page title
     */
    @Override
    public IModel<String> getPageTitle() {
        return new StringResourceModel("ordersSummary",this);
    }


}
