package org.yes.cart.filter;

import org.yes.cart.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.payment.PaymentCallBackHandlerFacade;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.yes.cart.shoppingcart.RequestRuntimeContainer;

import java.io.IOException;
import java.util.Map;

/**
 *
 * This filter pefrom initial handling of silent / hidden call back from payment gateway,
 * that perform external form processing. See "External payment form processing. Back/silent filter. Handler"
 * sequence diagram.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 11:44:41
 */
public class BasePaymentGatewayCallBackFilter extends AbstractFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(BasePaymentGatewayCallBackFilter.class);

    private final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade;



    /**
     * Construct filter.
     * @param requestRuntimeContainer current runtime container
     * @param paymentCallBackHandlerFacade handler.
     */
    public BasePaymentGatewayCallBackFilter(
            final PaymentCallBackHandlerFacade paymentCallBackHandlerFacade,
            final RequestRuntimeContainer requestRuntimeContainer) {
        super(requestRuntimeContainer);
        this.paymentCallBackHandlerFacade = paymentCallBackHandlerFacade;
    }

    /** {@inheritDoc} */
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {

        if (isCalledIpAllowed()) {

            HttpUtil.requestDump((HttpServletRequest) servletRequest);

            final Map parameters = servletRequest.getParameterMap();

            final String paymentGatewayLabel = getFilterConfig().getInitParameter("paymentGatewayLabel");

            paymentCallBackHandlerFacade.handlePaymentCallback(parameters, paymentGatewayLabel);

            ((HttpServletResponse)servletResponse).sendError(HttpServletResponse.SC_OK);

        } else {

            ((HttpServletResponse)servletResponse).sendError(HttpServletResponse.SC_NOT_FOUND);

        }

        return null;  //no forwarding, just return
    }


    /** {@inheritDoc} */
    public void doAfter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        //nothing
    }

    private boolean isCalledIpAllowed() {
        return true; //TODO imple
    }
}
