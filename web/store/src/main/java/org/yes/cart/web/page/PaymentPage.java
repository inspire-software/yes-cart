package org.yes.cart.web.page;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.payment.PaymentProcessFacade;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 *
 * Page  responsible to handle payments for gateways with internal/advanced
 * integration methods, in this case we are responsible to get the order from db perform
 * gateway call and show sucsessful of failed result.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 12/11/11
 * Time: 16:56
 */
public class PaymentPage extends AbstractWebPage {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String RESULT_CONTAINER = "paymentResult";

    private static final String POSITIVE_RESULT_FRAGMENT = "positiveResultFragment";

    private static final String NEGATIVE_RESULT_FRAGMENT = "negativeResultFragment";
    private static final String PAYMENT_DETAIL_LIST = "paymentDetail";
    private static final String PAYMENT_ID_LABEL = "id";
    private static final String PAYMENT_TRANS_ID = "transactionId";
    private static final String PAYMENT_AUTH_CODE = "authCode";
    private static final String PAYMENT_ERROR_CODE = "errorCode";
    private static final String PAYMENT_ERROR_DESCR = "errorDecription";

    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = ServiceSpringKeys.PAYMENT_PROCESS_FACADE)
    private PaymentProcessFacade paymentProcessFacade;

    @SpringBean(name = ServiceSpringKeys.ORDER_PAYMENT_SERICE)
    private CustomerOrderPaymentService customerOrderPaymentService;

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_ORDER_SERVICE)
    private CustomerOrderService customerOrderService;

    private final boolean result;



    /**
     * Construct payment page.
     * @param params page parameters
     */
    public PaymentPage(final PageParameters params) {

        super(params);

        result = paymentProcessFacade.pay(
                    ApplicationDirector.getShoppingCart(),
                    WicketUtil.pageParametesAsMap(getPage().getPageParameters())
                );
        //todo new cart in case of ok payment

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        processCommands();

        add(
               result ? createPositiveResultFragment() :  createNegativeResultFragment()
        );

        super.onBeforeRender();
    }


    /**
     * Get the negative result fragment.
     * @return negative result fragment
     */
    private MarkupContainer createNegativeResultFragment() {

        final List<CustomerOrderPayment> payments = customerOrderPaymentService.findBy(
                getOrderNumber(), null, null, null);


        return new Fragment(RESULT_CONTAINER, NEGATIVE_RESULT_FRAGMENT, this)
                .add(
                        new ListView<CustomerOrderPayment>(PAYMENT_DETAIL_LIST, payments) {
                            protected void populateItem(final ListItem<CustomerOrderPayment> item) {
                                final CustomerOrderPayment payment = item.getModelObject();
                                item.add(new Label(PAYMENT_ID_LABEL, String.valueOf(payment.getCustomerOrderPaymentId())));
                                item.add(new Label(PAYMENT_TRANS_ID, StringUtils.defaultIfEmpty(payment.getTransactionReferenceId(), StringUtils.EMPTY)));
                                item.add(new Label(PAYMENT_AUTH_CODE, StringUtils.defaultIfEmpty(payment.getTransactionAuthorizationCode(), StringUtils.EMPTY)));
                                item.add(new Label(PAYMENT_ERROR_CODE, StringUtils.defaultIfEmpty(payment.getTransactionOperationResultCode(), StringUtils.EMPTY)));
                                item.add(new Label(PAYMENT_ERROR_DESCR, StringUtils.defaultIfEmpty(payment.getTransactionOperationResultMessage(), StringUtils.EMPTY)));
                            }
                        }

                );
    }


    /**
     * Get positive result fragment.
     * @return positive result fragment.
     */
    private MarkupContainer createPositiveResultFragment() {
        return new Fragment(RESULT_CONTAINER, POSITIVE_RESULT_FRAGMENT, this);
    }


    /**
     * Get order number. Can be obtained from request in case if
     *  direct post method gateway was used or from shopping cart.
     *
     * @return order number.
     */
    private String getOrderNumber() {
        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        final CustomerOrder customerOrder = customerOrderService.findByGuid(cart.getGuid());
        return customerOrder.getOrdernum();
    }



}
