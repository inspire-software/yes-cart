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

package org.yes.cart.web.page.component.customer.order;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDet;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.utils.impl.CustomerOrderComparator;
import org.yes.cart.web.page.OrderPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Show customer orders and order states.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/23/11
 * Time: 9:32 PM
 */
public class CustomerOrderPanel extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String ORDER_LIST = "orders";
    private final static String ORDER_VIEW_LINK = "orderViewLink";
    private final static String ORDER_NUM = "orderNum";
    private final static String ORDER_DATE = "orderDate";
    private final static String ORDER_STATE = "orderStatus";
    private final static String ORDER_ITEMS = "orderItems";
    private final static String ORDER_AMOUNT = "orderAmount";
    private final static String ORDER_INFORMATION_FRAGMENT = "orderInformationFragment";
    private final static String ORDER_INFORMATION = "orderInformation";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_ORDER_SERVICE)
    private CustomerOrderService customerOrderService;

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_ORDER_SERVICE)
    private CustomerService customerService;

    @SpringBean(name = StorefrontServiceSpringKeys.CURRENCY_SYMBOL_SERVICE)
    private CurrencySymbolService currencySymbolService;

    /**
     * Construct panel.
     *
     * @param id            panel id
     * @param customerModel model of customer
     */

    public CustomerOrderPanel(final String id, final IModel<Customer> customerModel) {
        super(id, customerModel);
    }

    /**
     * Get sku names in order.
     *
     * @param order given order
     * @return comma separated sku names.
     */
    private String getItemsList(final CustomerOrder order) {
        final StringBuilder builder = new StringBuilder();
        for (CustomerOrderDet det : order.getOrderDetail()) {
            builder.append(det.getProductName());
            builder.append('(');
            builder.append(det.getProductSkuCode());
            builder.append("), ");
        }
        return StringUtils.chop(StringUtils.chop(builder.toString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {
        final Customer customer = (Customer) getDefaultModel().getObject();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT, getLocale());

        final DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.US);
        final DecimalFormat decimalFormat = new DecimalFormat(Constants.MONEY_FORMAT, formatSymbols);

        final Date date = determineDate(getPage().getPageParameters());
        final List<CustomerOrder> orders = getValidCustomerOrderInChronologicalOrder(customer, date);

        if (orders.isEmpty()) {

            addOrReplace(
                    new Label(ORDER_INFORMATION, getLocalizer().getString("noOrders", this))
            );

        } else {

            addOrReplace(
                    new Fragment(ORDER_INFORMATION, ORDER_INFORMATION_FRAGMENT, this)
                            .add(
                                    new ListView<CustomerOrder>(ORDER_LIST, orders) {
                                        protected void populateItem(final ListItem<CustomerOrder> customerOrderListItem) {

                                            final CustomerOrder order = customerOrderListItem.getModelObject();
                                            final Pair<String, Boolean> symbol = currencySymbolService.getCurrencySymbol(order.getCurrency());
                                            final BigDecimal amount = order.getOrderTotal();

                                            customerOrderListItem
                                                    .add(determineOrderPageLink(order, CustomerOrderPanel.ORDER_VIEW_LINK))
                                                    .add(new Label(ORDER_NUM, order.getOrdernum()))
                                                    .add(new Label(ORDER_DATE, dateFormat.format(order.getOrderTimestamp())))
                                                    .add(new Label(ORDER_STATE, getLocalizer().getString(order.getOrderStatus(), this)))
                                                    .add(new Label(ORDER_ITEMS, getItemsList(order)).setEscapeModelStrings(false))
                                                    .add(new Label(ORDER_AMOUNT,
                                                            symbol.getSecond() ?
                                                                    decimalFormat.format(amount) + " " + symbol.getFirst() :
                                                                    symbol.getFirst() + " " + decimalFormat.format(amount)
                                                    ));

                                        }
                                    }
                            )
            );

        }


        super.onBeforeRender();
    }

    /**
     * Extension hook.
     *
     * @param order order
     *
     * @return view order page link
     */
    protected Link determineOrderPageLink(final CustomerOrder order, final String linkId) {

        final PageParameters viewOrder = new PageParameters();
        viewOrder.set("order", order.getCartGuid());
        return new BookmarkablePageLink(linkId, OrderPage.class, viewOrder);

    }

    private final static Set<String> SUPPORTED_VIEWS = new HashSet<String>(Arrays.asList("week", "month", "year", "all"));
    private final static String DEFAULT_VIEW = "week";

    private Date determineDate(final PageParameters pageParameters) {
        String viewTime = pageParameters.get("view").toString();
        if (viewTime == null || !SUPPORTED_VIEWS.contains(viewTime)) {
            viewTime = DEFAULT_VIEW;
        }

        if ("all".equals(viewTime)) {
            return null;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if ("month".equals(viewTime)) {

            calendar.set(Calendar.DAY_OF_MONTH, 1);

        } else if ("year".equals(viewTime)) {

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, 0);

        } else {

            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

            final Calendar now = Calendar.getInstance();
            if (now.before(calendar)) {
                calendar.add(Calendar.DATE, -7);
            }

        }

        return calendar.getTime();
    }

    private List<CustomerOrder> getValidCustomerOrderInChronologicalOrder(final Customer customer, final Date date) {

        // all in DB
        final List<CustomerOrder> orders = customerOrderService.findCustomerOrders(customer, date);

        // remove temporary orders
        final Iterator<CustomerOrder> ordersIt = orders.iterator();
        while (ordersIt.hasNext()) {
            final CustomerOrder order = ordersIt.next();
            if (CustomerOrder.ORDER_STATUS_NONE.equals(order.getOrderStatus())) {
                ordersIt.remove();
            }
        }

        // sort
        Collections.sort(orders, Collections.reverseOrder(new CustomerOrderComparator()));

        return orders;
    }

}
