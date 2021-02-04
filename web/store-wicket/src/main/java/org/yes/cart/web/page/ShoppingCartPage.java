/*
 * Copyright 2009 Inspire-Software.com
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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.value.ValueMap;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.page.component.cart.ShoppingCartView;
import org.yes.cart.web.page.component.customer.auth.GuestPanel;
import org.yes.cart.web.page.component.customer.auth.LoginPanel;
import org.yes.cart.web.page.component.customer.auth.RegisterPanel;
import org.yes.cart.web.page.component.customer.wishlist.WishListNotification;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.HeaderMetaInclude;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.page.component.js.ServerSideJs;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.util.Collections;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/8/11
 * Time: 9:27 PM
 */
public class ShoppingCartPage extends AbstractWebPage {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String LOGIN_FRAGMENT = "loginFragment";
    private static final String CART_FRAGMENT = "shoppingCartFragment";
    private static final String CART_VIEW = "shoppingCartView";
    private static final String CONTENT_VIEW = "content";

    private static final String PART_REGISTER_VIEW = "registerView";
    private static final String PART_LOGIN_VIEW = "loginView";
    private static final String PART_GUEST_VIEW = "guestView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

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

        final Shop shop = getCurrentShop();
        boolean loginView = false;
        if (shop.isSfRequireCustomerLoginForCart()) {
            final String login = getCurrentCart().getCustomerLogin();
            final Customer customer;
            if (StringUtils.isNotBlank(login)) {
                customer = customerServiceFacade.getCustomerByLogin(getCurrentShop(), login);
            } else {
                customer = null;
            }
            loginView = customer == null;
        }

        addOrReplace(
                new FeedbackPanel(FEEDBACK)
        ).addOrReplace(
                loginView ? createLoginFragment() : createCartFragment()
        ).addOrReplace(
                new StandardFooter(FOOTER)
        ).addOrReplace(
                new StandardHeader(HEADER)
        ).addOrReplace(
                new ServerSideJs("serverSideJs")
        ).addOrReplace(
                new HeaderMetaInclude("headerInclude")
        );

        final PageParameters params = getPageParameters();
        final StringValue checkoutError = params.get("e");
        if (!checkoutError.isEmpty()) {

            if ("ec".equals(checkoutError.toString())) {
                warn(getLocalizer().getString("orderErrorCouponInvalid", this,
                        new Model<>(new ValueMap(Collections.singletonMap("coupon", params.get("ec").toString())))));
            } else if ("es".equals(checkoutError.toString())) {
                warn(getLocalizer().getString("orderErrorSkuInvalid", this,
                        new Model<>(new ValueMap(Collections.singletonMap("sku", params.get("es").toString())))));
            } else {
                error(getLocalizer().getString("orderErrorGeneral", this));
            }

        }

        addOrReplace(new WishListNotification("wishListNotification"));

        super.onBeforeRender();

        final ShoppingCart cart = getCurrentCart();

        final boolean cartModified = cart.isModified();

        if (!cartModified && cart.getCartItemsCount() > 0) {
            // Refresh prices on cart view
            getShoppingCartCommandFactory().execute(ShoppingCartCommand.CMD_RECALCULATEPRICE,
                    cart,
                    (Map) Collections.singletonMap(ShoppingCartCommand.CMD_RECALCULATEPRICE, ShoppingCartCommand.CMD_RECALCULATEPRICE));
        }

        persistCartIfNecessary();

        if (cartModified) {
            // redirect to clear all command parameters
            throw new RestartResponseException(ShoppingCartPage.class);
        }
    }

    /**
     * The default fragment is login/register page.
     *
     * @return login fragment
     */
    private MarkupContainer createLoginFragment() {
        return new Fragment(CONTENT_VIEW, LOGIN_FRAGMENT, this)
                .add(new LoginPanel(PART_LOGIN_VIEW, LoginPanel.NextPage.CART))
                .add(new RegisterPanel(PART_REGISTER_VIEW, RegisterPanel.NextPage.CART))
                .add(new GuestPanel(PART_GUEST_VIEW));
    }

    /**
     * The default fragment is login/register page.
     *
     * @return login fragment
     */
    private MarkupContainer createCartFragment() {
        return new Fragment(CONTENT_VIEW, CART_FRAGMENT, this)
                .add(new ShoppingCartView(CART_VIEW));
    }



    /**
     * Get page title.
     *
     * @return page title
     */
    @Override
    public IModel<String> getPageTitle() {
        return new StringResourceModel("shoppingCart", this);
    }


}
