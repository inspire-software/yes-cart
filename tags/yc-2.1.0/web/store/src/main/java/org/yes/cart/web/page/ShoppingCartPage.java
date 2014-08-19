/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.page;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.cart.ShoppingCartView;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.page.component.js.ServerSideJs;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/8/11
 * Time: 9:27 PM
 */
public class ShoppingCartPage extends AbstractWebPage {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CART_VIEW = "shoppingCartView";
    private final static String CHECKOUT_LINK = "checkoutLink";
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

        executeHttpPostedCommands();

        final PageParameters params = getPageParameters();
        final StringValue checkoutError = params.get("e");
        if (!checkoutError.isEmpty()) {

           if ("ec".equals(checkoutError.toString())) {
                warn(getLocalizer().getString("orderErrorCouponInvalid", this, new Model<Object[]>(new Object[] { params.get("ec").toString() })));
           } else {
                error(getLocalizer().getString("orderErrorGeneral", this));
           }

        }

        addOrReplace(
                new FeedbackPanel(FEEDBACK)
        ).addOrReplace(
                new ShoppingCartView(CART_VIEW).setVisible(!ApplicationDirector.getShoppingCart().getCartItemList().isEmpty())
        ).addOrReplace(
                new BookmarkablePageLink<CheckoutPage>(CHECKOUT_LINK, CheckoutPage.class).setVisible(!ApplicationDirector.getShoppingCart().getCartItemList().isEmpty())
        ).addOrReplace(
                new StandardFooter(FOOTER)
        ).addOrReplace(
                new StandardHeader(HEADER)
        ).addOrReplace(
                new ServerSideJs("serverSideJs")
        );

        if (ApplicationDirector.getShoppingCart().getCartItemList().isEmpty()) {
            info(getLocalizer().getString("emptyCart", this));
        }

        super.onBeforeRender();

        persistCartIfNecessary();
    }


    /**
     * Get page title.
     *
     * @return page title
     */
    public IModel<String> getPageTitle() {
        return new Model<String>(getLocalizer().getString("shoppingCart", this));
    }


}
