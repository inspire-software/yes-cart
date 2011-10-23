package org.yes.cart.web.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.component.customer.auth.RegisterPanel;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 22/10/11
 * Time: 18:45
 */
public class RegistrationPage  extends AbstractWebPage {



    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CART_VIEW = "registrationView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public RegistrationPage(final PageParameters params) {

        super(params);

        add(
                new RegisterPanel(CART_VIEW)
        );
    }

    /** {@inheritDoc} */
    @Override
    protected void onBeforeRender() {

         processCommands();

        super.onBeforeRender();
    }
}
