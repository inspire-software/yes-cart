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

package org.yes.cart.web.page.payment.callback;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.HeaderMetaInclude;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.page.component.js.ServerSideJs;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CheckoutServiceFacade;

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
    private final static String INFO_LABEL = "infoLabel";
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.PAYMENT_MODULES_MANAGER)
    private PaymentModulesManager paymentModulesManager;

    @SpringBean(name = ServiceSpringKeys.PAYMENT_CALLBACK_HANDLER)
    private PaymentCallBackHandlerFacade paymentCallBackHandlerFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CHECKOUT_SERVICE_FACADE)
    private CheckoutServiceFacade checkoutServiceFacade;

    @SpringBean(name = ServiceSpringKeys.PAYMENT_PROCESSOR_FACTORY)
    private PaymentProcessorFactory paymentProcessorFactory;

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

        final CustomerOrder customerOrder = checkoutServiceFacade.findByGuid(orderGuid);

        final PaymentProcessor paymentProcessor = paymentProcessorFactory.create(customerOrder.getPgLabel(), customerOrder.getShop().getCode());

        final PaymentGatewayExternalForm paymentGatewayExternalForm =
                (PaymentGatewayExternalForm) paymentProcessor.getPaymentGateway();

        paymentProcessor.setPaymentGateway(paymentGatewayExternalForm);

        final Payment payment = paymentProcessor.createPaymentsToAuthorize(
                customerOrder,
                true,
                Collections.EMPTY_MAP,
                "tmp")
                .get(0);
        // TODO: YC-156 exception handling

        try {

            // we are on step 4 according
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
                    new StatelessForm(FORM) {

                        @Override
                        protected void onSubmit() {

                            try {
                                paymentCallBackHandlerFacade.handlePaymentCallback(
                                        nvpCallResult,
                                        ApplicationDirector.getShoppingCart().getOrderInfo().getPaymentGatewayLabel()
                                );

                                final CustomerOrder customerOrder = checkoutServiceFacade.findByGuid(ApplicationDirector.getShoppingCart().getGuid());

                                if (checkoutServiceFacade.isOrderPaymentSuccessful(customerOrder)) {

                                    addOrReplace(
                                            new Label(
                                                    INFO_LABEL,
                                                    new StringResourceModel("paymentOk", this, null, (Object) customerOrder.getOrdernum()).getString()

                                            ).setEscapeModelStrings(false)
                                    );

                                    payLink.setVisible(false);

                                    shoppingCartCommandFactory.execute(
                                            ShoppingCartCommand.CMD_CLEAN, ApplicationDirector.getShoppingCart(),
                                            Collections.singletonMap(
                                                    ShoppingCartCommand.CMD_CLEAN,
                                                    null)
                                    );

                                } else {
                                    // TODO: YC-156 lacks information to show what the real problem is
                                    error(getLocalizer().getString("paymentFailed", this));

                                }

                            } catch (OrderException e) {

                                ShopCodeContext.getLog(this).error("Cant handle payment callback ", e);

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

            add(new StandardHeader(HEADER));
            add(new StandardFooter(FOOTER));

            add(new ServerSideJs("serverSideJs"));
            add(new HeaderMetaInclude("headerInclude"));

        } catch (IOException e) {
            ShopCodeContext.getLog(this).error("Cant call paypal gateway ", e);
        }
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
