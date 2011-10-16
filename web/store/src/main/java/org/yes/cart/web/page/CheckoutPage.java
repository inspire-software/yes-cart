package org.yes.cart.web.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Map;

/**
 * Checkout page has following main steps:
 *
 * 1. big shopping cart with coupons, taxes, items manipulations.
 * 2. quick registration, can be skipped if customer is registered.
 * 3. billing and shipping addresses
 * 4. payment page with payment method selection
 * 5. successful/unsuccessful callback page
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/8/11
 * Time: 8:06 PM
 */
@AuthorizeInstantiation("USER")
public class CheckoutPage extends AbstractWebPage {

    private static final long serialVersionUID = 20101107L;

     // ------------------------------------- Const BEGIN ---------------------------------- //
    public static final String STEP = "step";

    public static final String STEP_CART = "cart";
    public static final String STEP_LOGIN = "login";
    public static final String STEP_ADDR = "address";
    public static final String STEP_PAY = "payment";

    // ------------------------------------- Const END ---------------------------------- //


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public CheckoutPage(final PageParameters params) {
        super(params);
    }

    /** {@inheritDoc} */
    @Override
    protected void onBeforeRender() {

        processCommands();

       // final String centralViewLabel =  resolveMainPanelRendererLabel

    }

    /**
     * Resolve central renderer label.
     *
     * @return resolved main panel renderer label if resolved, otherwise null
     */
    public String resolveMainPanelRendererLabel(final PageParameters parameters) {

        return null;

    }


}
