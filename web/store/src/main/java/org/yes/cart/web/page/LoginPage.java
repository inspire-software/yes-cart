package org.yes.cart.web.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.component.customer.auth.LoginPanel;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 5:21 PM
 */
public class LoginPage extends AbstractWebPage {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CART_VIEW = "authView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public LoginPage(final PageParameters params) {

        super(params);

        add(
                new LoginPanel(CART_VIEW)
        );


    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        processCommands();

        super.onBeforeRender();
    }


}
