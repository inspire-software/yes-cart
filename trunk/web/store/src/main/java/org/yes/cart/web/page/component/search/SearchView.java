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

package org.yes.cart.web.page.component.search;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;

/**
 * Simple search .
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6 aug 2011
 * Time: 3:32 PM
 */
public class SearchView extends BaseComponent {

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    /**
     * Construct view.
     * @param id component id.
     */
    public SearchView(final String id) {
        super(id);
    }

    @Override
    protected void onBeforeRender() {

        String content = contentServiceFacade.getContentBody(
                "header_search_include", ShopCodeContext.getShopId(), getLocale().getLanguage());
        if (content == null) {
            content = "";
        }
        addOrReplace(new Label("contentInclude", content).setEscapeModelStrings(false));

        super.onBeforeRender();
    }
}
