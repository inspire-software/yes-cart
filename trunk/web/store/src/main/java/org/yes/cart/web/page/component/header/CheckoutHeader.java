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

package org.yes.cart.web.page.component.header;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.Currency;
import org.yes.cart.web.page.component.Language;
import org.yes.cart.web.page.component.cart.SmallShoppingCartView;
import org.yes.cart.web.page.component.customer.logout.LogoutPanel;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.AttributableImageService;
import org.yes.cart.web.util.WicketUtil;

/**
 * User: iazarny@yahoo.com
 * Date: 9/27/12
 * Time: 2:01 PM
 */
public class CheckoutHeader extends BaseComponent {

    @SpringBean(name = StorefrontServiceSpringKeys.SHOP_IMAGE_SERVICE)
    private AttributableImageService shopImageService;

    /**
     * {@inheritDoc}
     */
    protected void onBeforeRender() {

        addOrReplace(getLogoFragment());
        addOrReplace(new SmallShoppingCartView("smallCart"));

        addOrReplace(new Currency("currency"));
        addOrReplace(new Language("language"));
        addOrReplace(new LogoutPanel("logout"));
        super.onBeforeRender();
    }

    private Component getLogoFragment() {

        final String shopLogo = shopImageService.getImage(
                ApplicationDirector.getCurrentShop(),
                WicketUtil.getHttpServletRequest().getContextPath(),
                getLocale().getLanguage(),
                "as", "is",
                AttributeNamesKeys.Shop.SHOP_IMAGE, null);

        return new ContextImage("headerLogo", shopLogo)
                .setVisible(shopLogo != null && !shopLogo.contains(Constants.NO_IMAGE));
    }

    /**
     * Construct view.
     * @param id component id.
     */
    public CheckoutHeader(final String id) {
        super(id);
    }
}
