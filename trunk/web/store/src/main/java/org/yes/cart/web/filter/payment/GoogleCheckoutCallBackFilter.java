package org.yes.cart.web.filter.payment;

import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.util.HttpUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * This filter responsible for delegate google call back handling  notifications
 * to google payment gateway implementation and start order transition as usual,
 * acouring to external form processing.
 * See "External payment form processing. Back/silent filter. Handler" sequence diagram.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2012-Jan-07
 * Time: 11:44:41
 */
public class GoogleCheckoutCallBackFilter extends BasePaymentGatewayCallBackFilter {

    private final PaymentModulesManager paymentModulesManager;

    /**
     * Construct filter.
     *
     * @param paymentCallBackHandlerFacade handler.
     * @param applicationDirector          app director
     * @param paymentModulesManager to get gateway from application context.
     */
    public GoogleCheckoutCallBackFilter(final ApplicationDirector applicationDirector,
                                        final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade,
                                        final PaymentModulesManager paymentModulesManager) {
        super(applicationDirector, paymentCallBackHandlerFacade);
        this.paymentModulesManager = paymentModulesManager;
    }

    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {

        if (isCallerIpAllowed()) {

            HttpUtil.dumpRequest("GoogleCheckoutCallBackFilter" , servletRequest);

            final String paymentGatewayLabel = getFilterConfig().getInitParameter("paymentGatewayLabel");

            System.out.println("paymentGatewayLabel = " + paymentGatewayLabel);

            final PaymentGatewayExternalForm paymentGatewayExternalForm =
                    (PaymentGatewayExternalForm) paymentModulesManager.getPaymentGateway(paymentGatewayLabel);

            System.out.println("paymentGatewayExternalForm = " + paymentGatewayExternalForm);

            paymentGatewayExternalForm.handleNotification((HttpServletRequest) servletRequest,
                    (HttpServletResponse)servletResponse);

            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_OK);

        } else {

            ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_NOT_FOUND);

        }

        return null;  //no forwarding, just return
    }

}
