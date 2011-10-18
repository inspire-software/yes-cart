package org.yes.cart.web.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.component.customer.auth.LoginPanel;
import org.yes.cart.web.page.component.customer.auth.RegisterForm;
import org.apache.commons.lang.StringUtils;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 5:21 PM
 */
public class LoginPage extends AbstractWebPage {

    public static final String VIEW_NAME = "viewName";
    public static final String LOGIN_VIEW = "login";
    public static final String REGISTER_VIEW = "register";


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

        final String formLabel = params.get(VIEW_NAME).toString();

        add(new LoginPanel(CART_VIEW));

        /*add(
                StringUtils.isNotBlank(formLabel) && REGISTER_VIEW.endsWith(formLabel) ?
                        new RegisterForm(CART_VIEW, null, null) :
                        new LoginPanel(CART_VIEW, null, null)

        ); */




    }


}
