package org.yes.cart.web.page.payment.callback;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.web.page.AbstractWebPage;

/**
 * Just show the result of payment operation for
 * payment gateways with call back
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/12/11
 * Time: 11:50 AM
 */
public class ResultPage extends AbstractWebPage {

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_ORDER_SERVICE)
    private CustomerOrderService customerOrderService;

    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public ResultPage(final PageParameters params) {

        super(params);

        final String orderNum = params.get("orderNum").toString();

        final CustomerOrder customerOrder = customerOrderService.findByGuid(orderNum);

        add(
                new Label(
                        "paymentResult",
                        getLocalizer().getString(isOk(customerOrder) ? "paymentWasOk" : "paymentWasFailed", this)
                )
        );
    }

    /**
     * Get payment status from order.
     * @param customerOrder  given customer order.
     * @return  true in case of successful page
     */
    private boolean isOk(final CustomerOrder customerOrder) {
        return true; //todo get from payment services
    }


}
