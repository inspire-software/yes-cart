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

package org.yes.cart.web.page.component.footer;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;

/**
 * User: iazarny@yahoo.com
 * Date: 9/26/12
 * Time: 8:15 PM
 */
public class CheckoutFooter extends BaseComponent {

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    /**
     * Construct view.
     * @param id component id.
     */
    public CheckoutFooter(final String id) {
        super(id);
    }

    @Override
    protected void onBeforeRender() {

        final long shopId = ShopCodeContext.getShopId();
        final String lang = getLocale().getLanguage();

        String footerInclude = getContentInclude(shopId, "footer_include", lang);
        addOrReplace(new Label("footerInclude", footerInclude).setEscapeModelStrings(false));
        String footerNav = getContentInclude(shopId, "footer_co_nav_include", lang);
        addOrReplace(new Label("footerNav", footerNav).setEscapeModelStrings(false));

        super.onBeforeRender();
    }

    private String getContentInclude(long shopId, String contentUri, String lang) {
        String content = contentServiceFacade.getContentBody(
                contentUri, shopId, lang);
        if (content == null) {
            content = "";
        }
        return content;
    }
}
