package org.yes.cart.web.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.component.cart.ShoppingCartView;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/8/11
 * Time: 9:27 PM
 */
public class ShoppingCartPage  extends AbstractWebPage {

     // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CART_VIEW = "shoppingCartView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public ShoppingCartPage(final PageParameters params) {
        super(params);
    }

    @Override
    protected void onBeforeRender() {

        add(
                new ShoppingCartView(CART_VIEW)
        );

        super.onBeforeRender();
    }
}
