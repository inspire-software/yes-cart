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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDet;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.utils.impl.CustomerOrderComparator;
import org.yes.cart.web.page.component.BaseComponent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
    private final static String ORDER_NUM = "orderNum";
    private final static String ORDER_DATE = "orderDate";
    private final static String ORDER_STATE = "orderStatus";
    private final static String ORDER_ITEMS = "orderItems";
    private final static String ORDER_AMOUNT = "orderAmount";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_ORDER_SERVICE)
    private CustomerOrderService customerOrderService;

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_ORDER_SERVICE)
    private CustomerService customerService;

    @SpringBean(name = ServiceSpringKeys.ORDER_PAYMENT_SERICE)
    private CustomerOrderPaymentService customerOrderPaymentService;


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
        final String selectedLocale = getLocale().getLanguage();
        final StringBuilder builder = new StringBuilder();
        for (CustomerOrderDet det : order.getOrderDetail()) {
            final I18NModel nameModel = getI18NSupport().getFailoverModel(det.getSku().getDisplayName(), det.getSku().getName());
            builder.append(nameModel.getValue(selectedLocale));
            builder.append('(');
            builder.append(det.getSku().getCode());
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

        final List<CustomerOrder> orders = customerOrderService.findCustomerOrders(customer, null);
        Collections.sort(orders, new CustomerOrderComparator());

        addOrReplace(

                new ListView<CustomerOrder>(ORDER_LIST, orders) {
                    protected void populateItem(final ListItem<CustomerOrder> customerOrderListItem) {

                        final CustomerOrder order = customerOrderListItem.getModelObject();
                        customerOrderListItem
                                .add(new Label(ORDER_NUM, order.getOrdernum()))
                                .add(new Label(ORDER_DATE, dateFormat.format(order.getOrderTimestamp())))
                                .add(new Label(ORDER_STATE, getLocalizer().getString(order.getOrderStatus(), this)))
                                .add(new Label(ORDER_ITEMS, getItemsList(order)).setEscapeModelStrings(false))
                                .add(new Label(ORDER_AMOUNT,
                                        decimalFormat.format(
                                                customerOrderPaymentService.getOrderAmount(order.getOrdernum())
                                        ) + " " + order.getCurrency()
                                ));

                    }
                }

        );


        super.onBeforeRender();
    }

}
