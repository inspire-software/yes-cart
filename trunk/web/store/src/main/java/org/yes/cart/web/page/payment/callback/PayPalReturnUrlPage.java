package org.yes.cart.web.page.payment.callback;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.CleanCartCommandImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This is separate and out of standart checkout
 * process page to get the token from pay pall.
 * <p/>
 * This page will serve call back from pal pal to
 * verify payment.
 * <p/>
 * Followwin parametrs are expected
 * <p/>
 * token=EC-23A39526HT141645R&PayerID=RMWAJBTAM4DAY
 * <p/>
 * token already geted by PayPalExpressCheckoutPaymentGatewayImpl#setExpressCheckoutMethod method.
 * <p/>
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/16/11
 * Time: 8:32 AM
 */
public class PayPalReturnUrlPage extends AbstractWebPage {

    private static final Logger LOG = LoggerFactory.getLogger(PayPalReturnUrlPage.class);

    private static final long serialVersionUID = 20111612L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String FORM = "ppform";
    private final static String PAY_LINK = "payLink";
    private final static String INFO_LABEL = "infoLabel";
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.PAYMENT_MODULES_MANAGER)
    private PaymentModulesManager paymentModulesManager;

    @SpringBean(name = ServiceSpringKeys.PAYMENT_CALLBACK_HANDLER)
    private PaymentCallBackHandlerFacade paymentCallBackHandlerFacade;

    @SpringBean(name = ServiceSpringKeys.ORDER_PAYMENT_SERICE)
    private CustomerOrderPaymentService customerOrderPaymentService;

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_ORDER_SERVICE)
    private CustomerOrderService customerOrderService;

    @SpringBean(name = ServiceSpringKeys.PAYMENT_PROCESSOR)
    private PaymentProcessor paymentProcessor;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;


    /**
     * Construct receipt and  confirmation page.
     *
     * @param params page parameters
     */
    public PayPalReturnUrlPage(final PageParameters params) {

        super(params);

        final String orderGuid = ApplicationDirector.getShoppingCart().getGuid();

        final CustomerOrder customerOrder = customerOrderService.findByGuid(orderGuid);

        final PaymentGatewayExternalForm paymentGatewayExternalForm =
                (PaymentGatewayExternalForm) paymentModulesManager.getPaymentGateway(
                        ApplicationDirector.getShoppingCart().getOrderInfo().getPaymentGatewayLabel());

        final Payment payment = paymentProcessor.createPaymentsToAuthorize(
                customerOrder,
                true,
                Collections.EMPTY_MAP,
                "tmp")
                .get(0);

        try {

            // we are on step 4 accourding
            // to https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/e_howto_api_ECGettingStarted
            final Map<String, String> nvpCallResult = paymentGatewayExternalForm
                    .getExpressCheckoutDetails(getPageParameters().get("token").toString());

            //Add out information to perform order transition when user click pay
            nvpCallResult.put("orderGuid", ApplicationDirector.getShoppingCart().getGuid());

            final String payerStatus = nvpCallResult.get("PAYERSTATUS");

            final boolean checkoutDetailOk = paymentGatewayExternalForm.isSuccess(nvpCallResult)
                    && "verified".equalsIgnoreCase(payerStatus);

            final SubmitLink payLink = new SubmitLink(PAY_LINK);
            payLink.setVisible(checkoutDetailOk);

            final Label infoLabel = new Label(INFO_LABEL);

            add(
                    new Form(FORM) {

                        @Override
                        protected void onSubmit() {

                            paymentCallBackHandlerFacade.handlePaymentCallback(
                                    nvpCallResult,
                                    ApplicationDirector.getShoppingCart().getOrderInfo().getPaymentGatewayLabel()
                            );

                            final CustomerOrder customerOrder = customerOrderService.findByGuid(ApplicationDirector.getShoppingCart().getGuid());

                            if (isOk(customerOrder)) {

                                addOrReplace(
                                        new Label(
                                                INFO_LABEL,
                                                new StringResourceModel("paymentOk", this, null, (Object) customerOrder.getOrdernum()).getString()

                                        ).setEscapeModelStrings(false)
                                );

                                payLink.setVisible(false);

                                shoppingCartCommandFactory.create(
                                        Collections.singletonMap(
                                                CleanCartCommandImpl.CMD_KEY,
                                                null)
                                ).execute(ApplicationDirector.getShoppingCart());

                            } else {
                                //TODO lacks of information to show what the real problem
                                error(getLocalizer().getString("paymentFailed", this));

                            }

                            super.onSubmit();
                        }

                    }.add(
                            payLink
                    ).add(
                            infoLabel
                    ).add(
                            new FeedbackPanel(FEEDBACK)
                    )
            );

            if (checkoutDetailOk) {
                info(
                        new StringResourceModel("confirmPayment", this, null, payment.getPaymentAmount(), payment.getOrderCurrency()).getString()
                );
            } else {
                error(getLocalizer().getString("badStatus", this));
            }

        } catch (IOException e) {
            LOG.error("Cant call paypal gateway ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        processCommands();

        super.onBeforeRender();

    }

    /**
     * Get payment status from order.
     * We expected one payment only.
     *
     * @param customerOrder given customer order.
     * @return true in case of successful page
     */
    private boolean isOk(final CustomerOrder customerOrder) {

        final List<CustomerOrderPayment> payments =
                customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null);

        return payments != null
                && !payments.isEmpty()
                && Payment.PAYMENT_STATUS_OK.equals(payments.get(0).getPaymentProcessorResult());

    }

}
