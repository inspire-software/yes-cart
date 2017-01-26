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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * User: denispavlov
 * Date: 31/03/2014
 * Time: 22:25
 */
public class CookiePolicyInclude extends BaseComponent {

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    public CookiePolicyInclude(final String id) {
        super(id);
    }

    @Override
    protected void onBeforeRender() {

        final boolean alreadyAccepted = determineIfCookiePolicyIsAlreadyAccepted();

        String content = "";
        if (!alreadyAccepted) {

            final Shop shop = getCurrentShop();

            final boolean cookiePolicyEnabled = shop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_COOKIE_POLICY_ENABLE);

            if (cookiePolicyEnabled) {

                final long contentShopId = getCurrentShopId();
                content = contentServiceFacade.getDynamicContentBody(
                        "cookie_policy_include", contentShopId, getLocale().getLanguage(), Collections.EMPTY_MAP);
                if (content == null) {
                    content = contentShopId + "_cookie_policy_include";
                }
                setVisible(true);

            } else {

                addOrReplace(new Label("contentInclude", ""));
                setVisible(false);

            }

        } else {

            setVisible(false);

        }

        addOrReplace(new Label("contentInclude", content).setEscapeModelStrings(false));

        super.onBeforeRender();
    }

    private boolean determineIfCookiePolicyIsAlreadyAccepted() {

        final HttpServletRequest request = getWicketUtil().getHttpServletRequest();

        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : request.getCookies()) {
                if ("yccookiepolicy".equals(cookie.getName())) {
                    return true;
                }
            }
        }
        return false;

    }
}
