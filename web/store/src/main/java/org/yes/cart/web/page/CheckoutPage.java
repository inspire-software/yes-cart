package org.yes.cart.web.page;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.component.customer.auth.LoginPanel;
import org.yes.cart.web.page.component.customer.auth.RegisterPanel;

/**
 * Checkout page has following main steps:
 * <p/>
 * 1. big shopping cart with coupons, taxes, items manipulations.
 * 2. quick registration, can be skipped if customer is registered.
 * 3. billing and shipping addresses
 * 4. payment page with payment method selection
 * 5. successful/unsuccessful callback page
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/8/11
 * Time: 8:06 PM
 */

public class CheckoutPage extends AbstractWebPage {

    private static final long serialVersionUID = 20101107L;

    // ------------------------------------- Const BEGIN ---------------------------------- //
    public static final String NAVIGATION_THREE_FRAGMENT = "threeStepNavigationFragment";
    public static final String NAVIGATION_FOUR_FRAGMENT = "fourStepNavigationFragment";
    public static final String LOGIN_FRAGMENT = "loginFragment";


    public static final String CONTENT_VIEW = "content";
    public static final String NAVIGATION_VIEW = "navigation";

    public static final String PART_REGISTER_VIEW = "registerView";
    public static final String PART_LOGIN_VIEW = "loginView";

    public static final String STEP = "step";

    public static final String STEP_LOGIN = "login";
    public static final String STEP_ADDR = "address";
    public static final String STEP_SHIPMENT = "ship";
    public static final String STEP_PAY = "payment";

    // ------------------------------------- Const END ---------------------------------- //

    public static final String THREE_STEPS_PROCESS = "thp";

    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public CheckoutPage(final PageParameters params) {
        super(params);
        final boolean threeStepsProcess = params.get(THREE_STEPS_PROCESS).toBoolean(
                ((AuthenticatedWebSession) getSession()).isSignedIn()
        );
        final String currentStep = params.get(THREE_STEPS_PROCESS).toString(STEP_LOGIN);


        add(
                new FeedbackPanel(FEEDBACK)
        ).add(
                new Fragment(NAVIGATION_VIEW, threeStepsProcess ? NAVIGATION_THREE_FRAGMENT : NAVIGATION_FOUR_FRAGMENT, this)
        ).add(
                getContent(currentStep)
        ) ;


    }


    /**
     * Resolve content by given current step.
     * @param currentStep current step label
     * @return  markup container
     */
    private MarkupContainer getContent(final String currentStep) {
        final MarkupContainer rez;

        if(STEP_ADDR.equals(currentStep)) {

        } else if(STEP_SHIPMENT.equals(currentStep)) {

        } else if(STEP_PAY.equals(currentStep)) {

        }

        // The default fragment is loogin/register page

        rez = new Fragment(CONTENT_VIEW, LOGIN_FRAGMENT, this);
        rez.add(
                new LoginPanel(PART_LOGIN_VIEW, true)
        ).add(
                new RegisterPanel(PART_REGISTER_VIEW, true)
        );

        return rez;
    }






}
