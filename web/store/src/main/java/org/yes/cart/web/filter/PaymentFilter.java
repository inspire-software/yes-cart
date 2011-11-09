package org.yes.cart.web.filter;

import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.web.application.ApplicationDirector;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Payment filter responsible to handle payments for gateways with internal/advanced
 * integration methods, in this case we are responsible to get the order from db perform
 * gateway call and redirect to sucsessful of failed page.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 11/11/11
 * Time: 11:40
 */
public class PaymentFilter  extends AbstractFilter implements Filter {



    /**
     * Construct filter.
     *
     * @param applicationDirector app director.
     * @param customerOrderService order service
     * @param orderStateManager order state manager
     */
    public PaymentFilter(
            final ApplicationDirector applicationDirector) {
        super(applicationDirector);
    }

    /** {@inheritDoc} */
    @Override
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void doAfter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        //nothing to do
    }
}
