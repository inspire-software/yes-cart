package org.yes.cart.web.page.payment.callback;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;

import java.io.IOException;
import java.util.Collections;
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

    private static final long serialVersionUID = 20111612L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String FORM = "ppform";
    private final static String PAY_LINK = "payLink";
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.PAYMENT_MODULES_MANAGER)
    private PaymentModulesManager paymentModulesManager;


    @SpringBean(name = ServiceSpringKeys.CUSTOMER_ORDER_SERVICE)
    private CustomerOrderService customerOrderService;

    @SpringBean(name = ServiceSpringKeys.PAYMENT_PROCESSOR)
    private PaymentProcessor paymentProcessor;


    /**
     * Construct recaipt  and  confirmation page.
     *
     * @param params page parameters
     */
    public PayPalReturnUrlPage(final PageParameters params) {

        super(params);

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();

        final PaymentGatewayExternalForm paymentGatewayExternalForm =
                (PaymentGatewayExternalForm) paymentModulesManager.getPaymentGateway(
                        cart.getOrderInfo().getPaymentGatewayLabel());

        try {

            // we are on step 4 accourding
            // to https://cms.paypal.com/us/cgi-bin/?cmd=_render-content&content_ID=developer/e_howto_api_ECGettingStarted
            Map<String, String> nvpCallResult = paymentGatewayExternalForm
                    .getExpressCheckoutDetails(getPageParameters().get("token").toString());


            final String token = nvpCallResult.get("TOKEN");
            final String payerId = nvpCallResult.get("PAYERID");
            final String payerStatus = nvpCallResult.get("PAYERSTATUS");   //todo check result PAYERSTATUS

            add(
                    new Form(FORM) {

                        @Override
                        protected void onSubmit() {

                            final String orderGuid = cart.getGuid();

                            final CustomerOrder customerOrder = customerOrderService.findByGuid(orderGuid);

                            final Payment payment = paymentProcessor.createPaymentsToAuthorize(
                                    customerOrder,
                                    paymentGatewayExternalForm.createPaymentPrototype(Collections.EMPTY_MAP),
                                    true)
                                    .get(0);

                            try {
                                Map<String, String> paymentResult = paymentGatewayExternalForm.doDoExpressCheckoutPayment(token, payerId,
                                        payment.getPaymentAmount(), payment.getOrderCurrency());

                                if(paymentGatewayExternalForm.isSuccess(paymentResult)) {
                                    info("payment was ok "); //todo localization

                                } else {
                                    error("payment failed "); //todo localization

                                }


                            } catch (IOException e) {
                                error("eeeeeeeeeeee " + e.getMessage());
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }

                            super.onSubmit();
                        }

                    }.add(
                            new SubmitLink(PAY_LINK)
                    ).add(
                            new FeedbackPanel(FEEDBACK)
                    )
            );

            info("hi There we are going to pay press pay btn"); //todo localization
        } catch (IOException e) {
            e.printStackTrace();  //Todo     logging
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {


        super.onBeforeRender();
    }
}
