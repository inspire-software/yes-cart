package org.yes.cart.web.page.payment.callback;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.AbstractWebPage;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/16/11
 * Time: 8:32 AM
 */
public class PayPalCancelUrlPage  extends AbstractWebPage {

    private static final long serialVersionUID = 20111612L;

    /**
     * Construct paypal cancel page.
     * @param params  page parameters
     */
    public PayPalCancelUrlPage(final PageParameters params) {
        super(params);
    }
}
