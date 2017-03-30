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

package org.yes.cart.web.page.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;

import java.util.Date;
import java.util.HashMap;

public class DynoContentCentralView extends AbstractCentralView {

    private transient Category category;

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    protected ContentServiceFacade contentServiceFacade;

    /**
     * This is an internal constructor used by HomePage class. It disregards the
     * categoryId value.
     *
     * @param id panel id
     * @param categoryId ignored
     * @param navigationContext navigation context.
     */
    public DynoContentCentralView(final String id, final long categoryId, final NavigationContext navigationContext) {
        super(id, categoryId, navigationContext);
    }

    @Override
    protected void onBeforeRender() {

        add(new TopCategories("topCategories"));

        final String lang = getLocale().getLanguage();
        final String contentBody;
        if (getCategoryId() > 0l) {
            contentBody = contentServiceFacade.getDynamicContentBody(getCategoryId(), getCurrentShopId(), lang, new HashMap<String, Object>() {{
                put("shoppingCart", getCurrentCart());
                put("shop", getCurrentShop());
                put("datetime", new Date());
            }});
        } else {
            contentBody = "";
        }
        add(new Label("contentBody", contentBody).setEscapeModelStrings(false));

        super.onBeforeRender();
    }

    @Override
    public Category getCategory() {
        if (category == null) {
            category = contentServiceFacade.getContent(getCategoryId(), getCurrentShopId());
        }
        return category;
    }


    @Override
    protected String getRelCanonical(final Seo seo, final String language) {

        final String uri = getBookmarkService().saveBookmarkForContent(String.valueOf(getCategory().getCategoryId()));

        return getWicketUtil().getHttpServletRequest().getContextPath() + "/"
                + WebParametersKeys.CONTENT_ID + "/" + uri + "/" + ShoppingCartCommand.CMD_CHANGELOCALE + "/" + language;

    }


}
