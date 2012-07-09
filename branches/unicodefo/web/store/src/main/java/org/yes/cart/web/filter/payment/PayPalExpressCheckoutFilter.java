package org.yes.cart.web.filter.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.filter.AbstractFilter;
import org.yes.cart.web.filter.ShopResolverFilter;
import org.yes.cart.web.support.util.HttpUtil;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * This filter will server interaction with pay pall express checkout gateway.
 * And must me mapped to PayPalExpressCheckoutPaymentGatewayImpl#getPostActionUrl value,
 * by default it has value "paymentpaypalexpress".
 * <p/>
 * Action flow is following:
 * get the TOKEN from paypal express checkout gateway
 * redirect to paypal EC login page
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/18/11
 * Time: 5:50 PM
 */
public class PayPalExpressCheckoutFilter extends AbstractFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private final PaymentModulesManager paymentModulesManager;

    private final PaymentProcessor paymentProcessor;

    private final CustomerOrderService customerOrderService;

    /**
     * Construct filter.
     *
     * @param applicationDirector   app director.
     * @param paymentModulesManager to find gateway by label.
     * @param paymentProcessor      payment processor.
     * @param customerOrderService  {@link CustomerOrderService}     to use
     */
    public PayPalExpressCheckoutFilter(final ApplicationDirector applicationDirector,
                                       final PaymentModulesManager paymentModulesManager,
                                       final PaymentProcessor paymentProcessor,
                                       final CustomerOrderService customerOrderService) {
        super(applicationDirector);
        this.paymentModulesManager = paymentModulesManager;
        this.paymentProcessor = paymentProcessor;
        this.customerOrderService = customerOrderService;
    }

    @Override
    public ServletRequest doBefore(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();

        final String orderGuid = cart.getGuid();

        final CustomerOrder customerOrder =  customerOrderService.findByGuid(orderGuid);

        final PaymentGatewayExternalForm paymentGatewayExternalForm =
                (PaymentGatewayExternalForm) paymentModulesManager.getPaymentGateway(
                        cart.getOrderInfo().getPaymentGatewayLabel());

        paymentProcessor.setPaymentGateway(paymentGatewayExternalForm);

        final Payment payment = paymentProcessor.createPaymentsToAuthorize(
                customerOrder,
                true,
                servletRequest.getParameterMap(),
                "tmp")
                .get(0);

        final Map<String, String> nvpCallResult = paymentGatewayExternalForm.setExpressCheckoutMethod(
                payment.getPaymentAmount(), payment.getOrderCurrency());

        final String redirectUrl;
        
        if (paymentGatewayExternalForm.isSuccess(nvpCallResult)) {
            /*not encoded answer will be like this
            TOKEN=EC%2d8DX631540T256421Y&TIMESTAMP=2011%2d12%2d21T20%3a12%3a37Z&CORRELATIONID=2d2aa98bcd550&ACK=Success&VERSION=2%2e3&BUILD=2271164
             Redirect url  to paypal for perform login and payment */
            redirectUrl = paymentGatewayExternalForm.getParameterValue("PP_EC_PAYPAL_URL")
                    + "?orderGuid="  + orderGuid
                    + "&token="      + nvpCallResult.get("TOKEN")
                    + "&cmd=_express-checkout";

        } else {
            redirectUrl = "paymentresult" //mounted page
                    + "?orderNum="   + orderGuid
                    + "&errMsg="     + nvpCallResult.get("L_ERRORCODE0")
                    + '|'   + nvpCallResult.get("L_SHORTMESSAGE0")
                    + '|'   + nvpCallResult.get("L_LONGMESSAGE0")
                    + '|'   + nvpCallResult.get("L_SEVERITYCODE0") ;


            //todo move order to failed state
        }

        LOG.info("Pay pal filter user will be redirected to " + redirectUrl);

        ((HttpServletResponse) servletResponse).sendRedirect(
                ((HttpServletResponse) servletResponse).encodeRedirectURL(redirectUrl)
        );

        return null;
    }

    @Override
    public void doAfter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        // NOTHING
    }
}
