/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.web.page.component.header;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.Currency;
import org.yes.cart.web.page.component.Language;
import org.yes.cart.web.page.component.cart.SmallShoppingCartView;
import org.yes.cart.web.page.component.customer.logout.LogoutPanel;
import org.yes.cart.web.page.component.search.SearchView;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.AttributableImageService;
import org.yes.cart.web.support.service.ContentServiceFacade;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * User: iazarny@yahoo.com
 * Date: 9/27/12
 * Time: 2:01 PM
 */
public class StandardHeader  extends BaseComponent {

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.SHOP_IMAGE_SERVICE)
    private AttributableImageService shopImageService;

    /**
     * {@inheritDoc}
     */
    protected void onBeforeRender() {

        addOrReplace(new CookiePolicyInclude("cookiePolicy"));
        addOrReplace(getHeaderNav());
        addOrReplace(getLogoFragment());
        addOrReplace(getProfileFragment());
        addOrReplace(new SearchView("search"));
        addOrReplace(new SmallShoppingCartView("smallCart"));

        addOrReplace(new Currency("currency"));
        addOrReplace(new Language("language"));

        super.onBeforeRender();
    }

    private Component getProfileFragment() {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();

        if (cart.getLogonState() == ShoppingCart.LOGGED_IN) {

            return new Fragment("profile", "profileLoggedIn", this)
                    .add(new Label("profileName", cart.getCustomerName()))
                    .add(new LogoutPanel("logout"));

        }

        return new Fragment("profile", "profileLoggedOff", this);
    }

    private Component getHeaderNav() {

        String content = contentServiceFacade.getContentBody(
                "header_nav_include", ShopCodeContext.getShopId(), getLocale().getLanguage());

        return new Label("headerNav", content).setEscapeModelStrings(false);

    }

    private Component getLogoFragment() {

        final Shop shop = ApplicationDirector.getCurrentShop();
        final String lang = getLocale().getLanguage();

        final List<Pair<String, String>> filenames = shopImageService.getImageAttributeFileNames(shop, lang);
        final String defaultAttribute = filenames.get(0).getFirst();

        final String shopLogo = shopImageService.getImage(
                shop,
                getWicketUtil().getHttpServletRequest().getContextPath(),
                lang,
                "as", "is",
                defaultAttribute, null);

        return new ContextImage("headerLogo", shopLogo)
                .setVisible(shopLogo != null && !shopLogo.contains(Constants.NO_IMAGE));
    }

    /**
     * Construct view.
     * @param id component id.
     */
    public StandardHeader(final String id) {
        super(id);
    }
}
